package main.model.db.dao;

import main.exception.InsertItemStockException;
import main.log.db.dao.LogDAO;
import main.model.db.dto.db.ItemStockDTO;
import main.model.db.dto.itemStockList.JoinedItemStockDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import main.exception.InsufficientStockException;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ItemStockDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LogDAO logDAO;

    public ItemStockDTO findById(int stockId) {
        String sql = "SELECT stock_id, item_id, quantity, expire_date, received_date, status, affiliation_code FROM item_stock WHERE stock_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{stockId}, (rs, rowNum) -> {
                ItemStockDTO stock = new ItemStockDTO();
                stock.setStockId(rs.getInt("stock_id"));
                stock.setItemId(rs.getInt("item_id"));
                stock.setQuantity(rs.getInt("quantity"));
                stock.setExpireDate(rs.getTimestamp("expire_date"));
                stock.setReceivedDate(rs.getTimestamp("received_date"));
                stock.setStatus(rs.getString("status"));
                stock.setAffiliationCode(rs.getString("affiliation_code"));
                return stock;
            });
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 새 아이템 스톡 데이터 생성
     */
    public int insertItemStock(ItemStockDTO stock) {
        try {
            String sql = "INSERT INTO item_stock (item_id, quantity, expire_date, affiliation_code) VALUES (?, ?, ?, ?)";

            int result = jdbcTemplate.update(sql,
                    stock.getItemId(),
                    stock.getQuantity(),
                    stock.getExpireDate(),
                    stock.getAffiliationCode());

            if(result > 0) {
                logDAO.insertChangeLog(stock.getItemId(), stock.getQuantity(), "INBOUND", stock.getAffiliationCode(), new java.util.Date());
            }

            return result;

        } catch (Exception e) {
            Throwable cause = e.getCause();

            while (cause != null) {
                if (cause instanceof java.sql.SQLIntegrityConstraintViolationException
                        || (cause.getMessage() != null && cause.getMessage().contains("Duplicate entry"))) {
                    throw new InsertItemStockException("중복된 항목으로 인해 추가 실패했습니다.",
                            InsertItemStockException.Reason.DUPLICATE_KEY, e);
                }

                if (cause instanceof java.sql.SQLNonTransientConnectionException
                        || (cause.getMessage() != null && cause.getMessage().contains("Communications link failure"))) {
                    throw new InsertItemStockException("DB 연결 실패",
                            InsertItemStockException.Reason.CONNECTION_FAILURE, e);
                }

                cause = cause.getCause();
            }

            // 알 수 없는 예외
            throw new InsertItemStockException("알 수 없는 이유로 DB insert 실패",
                    InsertItemStockException.Reason.UNKNOWN, e);
        }
    }

    /**
     * 물량, 상태 수정
     */
    public int updateItemStock(int stockId, int quantity, String status) {
        String selectSql = "SELECT item_id, quantity, status, affiliation_code FROM item_stock WHERE stock_id = ?";
        Map<String, Object> stockInfo;

        try {
            stockInfo = jdbcTemplate.queryForMap(selectSql, stockId);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }

        int currentQuantity = ((Number) stockInfo.get("quantity")).intValue();
        String currentStatus = (String) stockInfo.get("status");

        // 변경사항이 없으면 아무것도 하지 않음
        if (currentQuantity == quantity && Objects.equals(currentStatus, status)) {
            return 0;
        }

        String updateSql = "UPDATE item_stock SET quantity = ?, status = ? WHERE stock_id = ?";
        int result = jdbcTemplate.update(updateSql, quantity, status, stockId);

        if (result > 0) {
            int itemId = ((Number) stockInfo.get("item_id")).intValue();
            String affiliationCode = (String) stockInfo.get("affiliation_code");

            int changeAmount = quantity - currentQuantity;
            logDAO.insertChangeLog(itemId, changeAmount, "MODIFY", affiliationCode, new java.util.Date());
        }

        return result;
    }

    /**
     * 물량 수정
     */
    public int updateItemStock(int stockId, int quantity) {
        String selectSql = "SELECT item_id, quantity, affiliation_code FROM item_stock WHERE stock_id = ?";
        Map<String, Object> stockInfo;

        try {
            stockInfo = jdbcTemplate.queryForMap(selectSql, stockId);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }

        int currentQuantity = ((Number) stockInfo.get("quantity")).intValue();

        if (currentQuantity == quantity) {
            return 0;
        }

        String updateSql = "UPDATE item_stock SET quantity = ? WHERE stock_id = ?";
        int result = jdbcTemplate.update(updateSql, quantity, stockId);

        if (result > 0) {
            int itemId = ((Number) stockInfo.get("item_id")).intValue();
            String affiliationCode = (String) stockInfo.get("affiliation_code");

            int changeAmount = quantity - currentQuantity;
            logDAO.insertChangeLog(itemId, changeAmount, "MODIFY", affiliationCode, new java.util.Date());
        }

        return result;
    }

    /**
     * 상태 수정
     * 유효상태 available, 불량 재고상태 defective, 재고 없음 상태 depleted
     */
    public int updateItemStock(int stockId, String status) {
        String selectSql = "SELECT item_id, quantity, status, affiliation_code FROM item_stock WHERE stock_id = ?";
        Map<String, Object> stockInfo;

        try {
            stockInfo = jdbcTemplate.queryForMap(selectSql, stockId);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }

        String currentStatus = (String) stockInfo.get("status");

        if (Objects.equals(currentStatus, status)) {
            return 0;
        }

        String updateSql = "UPDATE item_stock SET status = ? WHERE stock_id = ?";
        int result = jdbcTemplate.update(updateSql, status, stockId);

        if (result > 0) {
            int itemId = ((Number) stockInfo.get("item_id")).intValue();
            int quantity = ((Number) stockInfo.get("quantity")).intValue();
            String affiliationCode = (String) stockInfo.get("affiliation_code");

            logDAO.insertChangeLog(itemId, 0, "MODIFY", affiliationCode, new java.util.Date()); // 변화량 없음 → 0 기록
        }

        return result;
    }


    /**
     * 특정 점포에 있는 특정 아이템의 가용 가능수량 전체 데이터 조회
     */
    public int getAvailableQuantity(int itemId, String affiliationCode) {
        String sql = "SELECT SUM(quantity) FROM item_stock " +
                "WHERE item_id = ? AND affiliation_code = ? AND status = 'available' AND quantity > 0";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, itemId, affiliationCode);
        return (total != null) ? total : 0;
    }


    /**
     * 특정 점포에 있는 특정 아이템의 가용 가능수량 전체 데이터를 조회
     * 해당 점포에 있는 지정한 아이템의 갯수를 quantityToDecrease만큼 차감(원본 - quantityToDecrease)
     * 만약 수량이 부족하다면 예외Exception 발생
     */
    @Transactional
    public void decreaseStock(int itemId, String affiliationCode, int quantityToDecrease) {
        int totalAvailable = getAvailableQuantity(itemId, affiliationCode);
        if (totalAvailable < quantityToDecrease) {
            throw new InsufficientStockException("Not enough stock to fulfill the request. Available: " +
                    totalAvailable + ", Requested: " + quantityToDecrease);
        }

        String sql = "SELECT stock_id, quantity FROM item_stock " +
                "WHERE item_id = ? AND affiliation_code = ? AND status = 'available' AND quantity > 0 " +
                "ORDER BY received_date ASC";

        var stocks = jdbcTemplate.queryForList(sql, itemId, affiliationCode);

        int remainingToDecrease = quantityToDecrease;

        for (Map<String, Object> stock : stocks) {
            int stockId = ((Number) stock.get("stock_id")).intValue();
            int currentQuantity = ((Number) stock.get("quantity")).intValue();

            if (remainingToDecrease <= 0) break;

            if (currentQuantity <= remainingToDecrease) {
                jdbcTemplate.update("UPDATE item_stock SET quantity = 0, status = 'depleted' WHERE stock_id = ?", stockId);
                remainingToDecrease -= currentQuantity;
            } else {
                int newQuantity = currentQuantity - remainingToDecrease;
                jdbcTemplate.update("UPDATE item_stock SET quantity = ? WHERE stock_id = ?", newQuantity, stockId);
                remainingToDecrease = 0;
            }
        }

        // 소비 로그 기록 (전체 소비 수량 단위로 1회 기록)
        int insertCount = logDAO.insertConsumption(itemId, quantityToDecrease, affiliationCode, new java.util.Date());
        if (insertCount <= 0) {
            throw new RuntimeException("Failed to insert consumption log");
        }

        int changeLogCount = logDAO.insertChangeLog(itemId, quantityToDecrease * -1, "USAGE", affiliationCode, new java.util.Date());
        if (changeLogCount <= 0) {
            throw new RuntimeException("Failed to insert inventory change log");
        }
    }


    /**
     * 오버리딩 state가 없다면 자동으로 available 상태인 리스트만 출력
     * @param affiliationCode
     * @return
     */
    public List<JoinedItemStockDTO> getItemStockList(String affiliationCode) {
        String sql = "SELECT s.stock_id, s.item_id, i.name AS item_name, i.category AS item_category, " +
                "s.quantity, s.expire_date, s.received_date, s.status, s.affiliation_code " +
                "FROM item_stock s " +
                "JOIN item i ON s.item_id = i.item_id " +
                "WHERE s.affiliation_code = ? AND (s.status = 'available' OR s.status = 'defective')";

        return jdbcTemplate.query(sql, new Object[]{affiliationCode}, (rs, rowNum) -> {
            JoinedItemStockDTO stock = new JoinedItemStockDTO();
            stock.setStockId(rs.getInt("stock_id"));
            stock.setItemId(rs.getInt("item_id"));
            stock.setItemName(rs.getString("item_name"));         // from item table
            stock.setItemCategory(rs.getString("item_category")); // from item table
            stock.setQuantity(rs.getInt("quantity"));
            stock.setExpireDate(rs.getTimestamp("expire_date"));
            stock.setReceivedDate(rs.getTimestamp("received_date"));
            stock.setStatus(rs.getString("status"));
            stock.setAffiliationCode(rs.getString("affiliation_code"));
            return stock;
        });
    }

    /**
     * 오버리딩 state가 있다면 해당 state의 리스트 출력
     * @param affiliationCode
     * @param state
     * @return
     */
    public List<JoinedItemStockDTO> getItemStockList(String affiliationCode, String state) {
        String sql = "SELECT s.stock_id, s.item_id, i.name AS item_name, i.category AS item_category, " +
                "s.quantity, s.expire_date, s.received_date, s.status, s.affiliation_code " +
                "FROM item_stock s " +
                "JOIN item i ON s.item_id = i.item_id " +
                "WHERE s.affiliation_code = ? AND s.status = ?";

        return jdbcTemplate.query(sql, new Object[]{affiliationCode, state}, (rs, rowNum) -> {
            JoinedItemStockDTO stock = new JoinedItemStockDTO();
            stock.setStockId(rs.getInt("stock_id"));
            stock.setItemId(rs.getInt("item_id"));
            stock.setItemName(rs.getString("item_name"));         // 추가 필드
            stock.setItemCategory(rs.getString("item_category")); // 추가 필드
            stock.setQuantity(rs.getInt("quantity"));
            stock.setExpireDate(rs.getTimestamp("expire_date"));
            stock.setReceivedDate(rs.getTimestamp("received_date"));
            stock.setStatus(rs.getString("status"));
            stock.setAffiliationCode(rs.getString("affiliation_code"));
            return stock;
        });
    }

    /**
     * 오버리딩 state가 없다면 자동으로 available 상태인 리스트만 출력
     * @return
     */
    public List<JoinedItemStockDTO> getAllItemStockList() {
        String sql = "SELECT s.stock_id, s.item_id, i.name AS item_name, i.category AS item_category, " +
                "s.quantity, s.expire_date, s.received_date, s.status, s.affiliation_code " +
                "FROM item_stock s " +
                "JOIN item i ON s.item_id = i.item_id " +
                "WHERE (s.status = 'available' OR s.status = 'defective')";

        return jdbcTemplate.query(sql, new Object[]{}, (rs, rowNum) -> {
            JoinedItemStockDTO stock = new JoinedItemStockDTO();
            stock.setStockId(rs.getInt("stock_id"));
            stock.setItemId(rs.getInt("item_id"));
            stock.setItemName(rs.getString("item_name"));         // from item table
            stock.setItemCategory(rs.getString("item_category")); // from item table
            stock.setQuantity(rs.getInt("quantity"));
            stock.setExpireDate(rs.getTimestamp("expire_date"));
            stock.setReceivedDate(rs.getTimestamp("received_date"));
            stock.setStatus(rs.getString("status"));
            stock.setAffiliationCode(rs.getString("affiliation_code"));
            return stock;
        });
    }

    /**
     * 오버리딩 state가 있다면 해당 state의 리스트 출력
     * @param state
     * @return
     */
    public List<JoinedItemStockDTO> getAllItemStockList(String state) {
        String sql = "SELECT s.stock_id, s.item_id, i.name AS item_name, i.category AS item_category, " +
                "s.quantity, s.expire_date, s.received_date, s.status, s.affiliation_code " +
                "FROM item_stock s " +
                "JOIN item i ON s.item_id = i.item_id " +
                "WHERE s.status = ?";

        return jdbcTemplate.query(sql, new Object[]{state}, (rs, rowNum) -> {
            JoinedItemStockDTO stock = new JoinedItemStockDTO();
            stock.setStockId(rs.getInt("stock_id"));
            stock.setItemId(rs.getInt("item_id"));
            stock.setItemName(rs.getString("item_name"));         // 추가 필드
            stock.setItemCategory(rs.getString("item_category")); // 추가 필드
            stock.setQuantity(rs.getInt("quantity"));
            stock.setExpireDate(rs.getTimestamp("expire_date"));
            stock.setReceivedDate(rs.getTimestamp("received_date"));
            stock.setStatus(rs.getString("status"));
            stock.setAffiliationCode(rs.getString("affiliation_code"));
            return stock;
        });
    }

    public int deleteItemStock(int stockId) {
        // 1. 재고 정보 조회 (quantity, item_id, affiliation_code)
        String selectSql = "SELECT item_id, quantity, affiliation_code FROM item_stock WHERE stock_id = ?";
        Map<String, Object> stock = jdbcTemplate.queryForMap(selectSql, stockId);
        int quantity = ((Number) stock.get("quantity")).intValue();
        int itemId = ((Number) stock.get("item_id")).intValue();
        String affiliationCode = (String) stock.get("affiliation_code");

        // 2. 재고 폐기 처리
        String updateSql = "UPDATE item_stock SET quantity = 0, status = 'depleted' WHERE stock_id = ?";
        int result = jdbcTemplate.update(updateSql, stockId);

        // 3. 폐기 로그 기록 (재고 변화 로그)
        if (result > 0) {
            logDAO.insertChangeLog(itemId, quantity * -1, "DISPOSAL", affiliationCode, new java.util.Date());
        }

        return result;
    }


    @Transactional
    public void transferStock(int itemId, String fromAffiliationCode, String toAffiliationCode, int quantityToTransfer) {
        System.out.println("[transferStock] Input Params -> itemId: " + itemId +
                ", fromAffiliationCode: " + fromAffiliationCode +
                ", toAffiliationCode: " + toAffiliationCode +
                ", quantityToTransfer: " + quantityToTransfer);
        // 1. 출고 가능 재고 총량 검사
        int totalAvailable = getAvailableQuantity(itemId, fromAffiliationCode);
        if (totalAvailable < quantityToTransfer) {
            throw new InsufficientStockException("Not enough stock to transfer. Available: " +
                    totalAvailable + ", Requested: " + quantityToTransfer);
        }
        System.out.println("[transferStock] itemId=" + itemId + ", fromAffiliationCode=" + fromAffiliationCode + ", totalAvailable=" + totalAvailable + ", requested=" + quantityToTransfer);
        // 2. 출고 대상 재고 조회 (FIFO)
        String sql = "SELECT stock_id, quantity, expire_date, status FROM item_stock " +
                "WHERE item_id = ? AND affiliation_code = ? AND status = 'available' AND quantity > 0 " +
                "ORDER BY expire_date ASC";

        var stocks = jdbcTemplate.queryForList(sql, itemId, fromAffiliationCode);

        int remainingToTransfer = quantityToTransfer;
        List<ItemStockDTO> stocksToInsert = new ArrayList<>();

        // 3. 출고 재고 분할 및 차감 준비
        for (Map<String, Object> stock : stocks) {
            if (remainingToTransfer <= 0) break;

            int stockId = ((Number) stock.get("stock_id")).intValue();
            int currentQuantity = ((Number) stock.get("quantity")).intValue();
            Timestamp expireDate = (Timestamp) stock.get("expire_date");
            String status = (String) stock.get("status");

            System.out.println("[transferStock] Processing stockId=" + stockId + ", currentQuantity=" + currentQuantity + ", remainingToTransfer=" + remainingToTransfer);


            int quantityUsed = Math.min(currentQuantity, remainingToTransfer);

            // 출고 재고 정보 생성 (toAffiliationCode로 입고할 때 사용할 것)
            ItemStockDTO newStock = new ItemStockDTO();
            newStock.setItemId(itemId);
            newStock.setQuantity(quantityUsed);
            newStock.setExpireDate(expireDate);
            newStock.setStatus(status);
            newStock.setAffiliationCode(toAffiliationCode);
            newStock.setReceivedDate(new Timestamp(System.currentTimeMillis())); // 입고 시간 현재 시각으로 설정

            stocksToInsert.add(newStock);

            // 4. 기존 재고 차감 처리
            if (currentQuantity <= quantityUsed) {
                System.out.println("[transferStock] Depleting stockId=" + stockId);
                jdbcTemplate.update("UPDATE item_stock SET quantity = 0, status = 'depleted' WHERE stock_id = ?", stockId);
            } else {
                int newQuantity = currentQuantity - quantityUsed;
                System.out.println("[transferStock] Updating stockId=" + stockId + " newQuantity=" + newQuantity);
                jdbcTemplate.update("UPDATE item_stock SET quantity = ? WHERE stock_id = ?", newQuantity, stockId);
            }

            remainingToTransfer -= quantityUsed;
        }
        System.out.println("[transferStock] Finished processing existing stocks, inserting new stock records");
        // 5. 입고 처리
        for (ItemStockDTO insertStock : stocksToInsert) {
            insertStock.setAffiliationCode(toAffiliationCode);
            System.out.println("[transferStock] Inserting new stock: itemId=" + insertStock.getItemId()
                    + ", quantity=" + insertStock.getQuantity()
                    + ", expireDate=" + insertStock.getExpireDate()
                    + ", status=" + insertStock.getStatus()
                    + ", affiliationCode=" + insertStock.getAffiliationCode());


            int result = insertItemStock(insertStock);
            if (result <= 0) {
                throw new RuntimeException("Failed to insert stock for transfer");
            }
        }
        logDAO.insertHeadquarterShipment(itemId, quantityToTransfer, toAffiliationCode, new java.util.Date());
        logDAO.insertChangeLog(itemId, quantityToTransfer * -1, "SHIPMENT", fromAffiliationCode, new java.util.Date());
        logDAO.insertChangeLog(itemId, quantityToTransfer, "INBOUND", toAffiliationCode, new java.util.Date());


        System.out.println("[transferStock] Completed successfully");
    }
}

