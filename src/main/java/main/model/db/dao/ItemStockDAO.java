package main.model.db.dao;

import main.model.db.dto.db.ItemStockDTO;
import main.model.db.dto.itemStockList.JoinedItemStockDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import main.exception.InsufficientStockException;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ItemStockDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        String sql = "INSERT INTO item_stock (stock_id, item_id, quantity, expire_date, received_date, status, affiliation_code) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, stock.getStockId(), stock.getItemId(), stock.getQuantity(),
                stock.getExpireDate(), stock.getReceivedDate(), stock.getStatus(), stock.getAffiliationCode());
    }

    /**
     * 물량, 상태 수정
     */
    public int updateItemStock(int stockId, int quantity, String status) {
        String sql = "UPDATE item_stock SET quantity = ?, status = ? WHERE stock_id = ?";
        return jdbcTemplate.update(sql, quantity, status, stockId);
    }

    /**
     * 물량 수정
     */
    public int updateItemStock(int stockId, int quantity) {
        String sql = "UPDATE item_stock SET quantity = ? WHERE stock_id = ?";
        return jdbcTemplate.update(sql, quantity, stockId);
    }

    /**
     * 상태 수정
     * 유효상태 available, 불량 재고상태 defective, 재고 없음 상태 depleted
     */
    public int updateItemStock(int stockId, String status) {
        String sql = "UPDATE item_stock SET status = ? WHERE stock_id = ?";
        return jdbcTemplate.update(sql, status, stockId);
    }

    /**
     * 특정 점포에 있는 특정 아이템의 가용 가능수량 전체 데이터 조회
     */
    public int getAvailableQuantity(int itemId, int affiliationCode) {
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
    public void decreaseStock(int itemId, int affiliationCode, int quantityToDecrease) {
        int totalAvailable = getAvailableQuantity(itemId, affiliationCode);
        if (totalAvailable < quantityToDecrease) {
            throw new InsufficientStockException("Not enough stock to fulfill the request. Available: " +
                    totalAvailable + ", Requested: " + quantityToDecrease);
        }

        String sql = "SELECT stock_id, quantity FROM item_stock " +
                "WHERE item_id = ? AND affiliation_code = ? AND status = 'available' AND quantity > 0 " +
                "ORDER BY received_date ASC";

        var stocks = jdbcTemplate.queryForList(sql, itemId, affiliationCode);

        for (Map<String, Object> stock : stocks) {
            int stockId = ((Number) stock.get("stock_id")).intValue();
            int currentQuantity = ((Number) stock.get("quantity")).intValue();

            if (quantityToDecrease <= 0) break;

            if (currentQuantity <= quantityToDecrease) {
                jdbcTemplate.update("UPDATE item_stock SET quantity = 0, status = 'depleted' WHERE stock_id = ?", stockId);
                quantityToDecrease -= currentQuantity;
            } else {
                int newQuantity = currentQuantity - quantityToDecrease;
                jdbcTemplate.update("UPDATE item_stock SET quantity = ? WHERE stock_id = ?", newQuantity, stockId);
                quantityToDecrease = 0;
            }
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

    public int deleteItemStock(int stockId) {
        String sql = "DELETE FROM item_stock WHERE stock_id = ?";
        return jdbcTemplate.update(sql, stockId);
    }
}

