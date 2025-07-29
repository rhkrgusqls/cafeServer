package main.model.db.dao;

import main.model.db.dto.ItemStockDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}

