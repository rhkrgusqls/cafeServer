package db.dao;

import db.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public OrderDTO findById(int orderId) {
        String sql = "SELECT order_id, item_id, id, order_date FROM _order WHERE order_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{orderId}, (rs, rowNum) -> {
                OrderDTO order = new OrderDTO();
                order.setOrderId(rs.getInt("order_id"));
                order.setItemId(rs.getInt("item_id"));
                order.setId(rs.getString("id"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                return order;
            });
        } catch (Exception e) {
            return null;
        }
    }

    public int insertOrder(OrderDTO order) {
        String sql = "INSERT INTO _order (order_id, item_id, id, order_date) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, order.getOrderId(), order.getItemId(), order.getId(), order.getOrderDate());
    }
}
