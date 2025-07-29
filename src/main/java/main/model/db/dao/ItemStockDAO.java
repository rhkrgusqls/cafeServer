package main.model.db.dao;

import main.model.db.dto.ItemStockDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Map;
import main.exception.InsufficientStockException;

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
                stock.setAffiliationCode(rs.getInt("affiliation_code"));
                return stock;
            });
        } catch (Exception e) {
            return null;
        }
    }

    public int insertItemStock(ItemStockDTO stock) {
        String sql = "INSERT INTO item_stock (stock_id, item_id, quantity, expire_date, received_date, status, affiliation_code) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, stock.getStockId(), stock.getItemId(), stock.getQuantity(),
                stock.getExpireDate(), stock.getReceivedDate(), stock.getStatus(), stock.getAffiliationCode());
    }

    public int updateItemStock(int stockId, int quantity, String status) {
        String sql = "UPDATE item_stock SET quantity = ?, status = ? WHERE stock_id = ?";
        return jdbcTemplate.update(sql, quantity, status, stockId);
    }

    public int updateItemStock(int stockId, int quantity) {
        String sql = "UPDATE item_stock SET quantity = ? WHERE stock_id = ?";
        return jdbcTemplate.update(sql, quantity, stockId);
    }

    public int updateItemStock(int stockId, String status) {
        String sql = "UPDATE item_stock SET status = ? WHERE stock_id = ?";
        return jdbcTemplate.update(sql, status, stockId);
    }

    /**
     * 특정 점포의 특정 아이템 전체 데이터 조회
     */
    public int getAvailableQuantity(int itemId, int affiliationCode) {
        String sql = "SELECT SUM(quantity) FROM item_stock " +
                "WHERE item_id = ? AND affiliation_code = ? AND status = 'available' AND quantity > 0";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, itemId, affiliationCode);
        return (total != null) ? total : 0;
    }


    /**
     * 특정 점포에 있는 특정 아이템의 전체 데이터를 조회
     * 해당 점포에 있는 지정한 아이템의 갯수를 quantityToDecrease만큼 차감(원본 - quantityToDecrease)
     * 만약 수량이 부족하다면 예외Exception 발생
     */
    public void decreaseStock(int itemId, int affiliationCode, int quantityToDecrease) {
        // 1. 가용 수량 확인
        int totalAvailable = getAvailableQuantity(itemId, affiliationCode);
        if (totalAvailable < quantityToDecrease) {
            throw new InsufficientStockException("Not enough stock to fulfill the request. Available: " +
                    totalAvailable + ", Requested: " + quantityToDecrease);
        }

        // 2. FIFO 방식 차감
        String sql = "SELECT stock_id, quantity FROM item_stock " +
                "WHERE item_id = ? AND affiliation_code = ? AND status = 'available' AND quantity > 0 " +
                "ORDER BY received_date ASC";

        var stocks = jdbcTemplate.queryForList(sql, itemId, affiliationCode);

        for (Map<String, Object> stock : stocks) {
            int stockId = (int) stock.get("stock_id");
            int currentQuantity = (int) stock.get("quantity");

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
}

