package main.model.db.dao;

import main.MainApplication;
import main.model.db.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class OrderDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public OrderDTO findById(int orderId) {
        String sql = "SELECT order_id, item_id, id, quantity, order_date, state FROM _order WHERE order_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{orderId}, (rs, rowNum) -> {
                OrderDTO order = new OrderDTO();
                order.setOrderId(rs.getInt("order_id"));
                order.setItemId(rs.getInt("item_id"));
                order.setId(rs.getString("id"));
                order.setQuantity(rs.getInt("quantity"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setState(rs.getString("state"));
                return order;
            });
        } catch (Exception e) {
            return null;
        }
    }

    public int insertOrder(OrderDTO order) {
        String sql = "INSERT INTO _order (order_id, item_id, id, quantity, state) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                order.getOrderId(),
                order.getItemId(),
                order.getId(),
                order.getQuantity(),
                order.getState());
    }

    public int updateState(int orderId, String newState) {
        String sql = "UPDATE _order SET state = ? WHERE order_id = ?";
        return jdbcTemplate.update(sql, newState, orderId);
    }

    // affiliation_code로 묶어 item_id별 quantity 합산 조회
    public List<Map<String, Object>> getQuantitySumGroupedByItemForAffiliation(int affiliationCode) {
        String sql = "SELECT item_id, SUM(quantity) AS total_quantity " +
                "FROM _order o JOIN user u ON o.id = u.id " +
                "WHERE u.affiliation_code = ? " +
                "GROUP BY item_id";
        return jdbcTemplate.queryForList(sql, affiliationCode);
    }

    // 전체 item_id별 quantity 합산 조회 (오버로딩)
    public List<Map<String, Object>> getQuantitySumGroupedByItemForUserAffiliation() {
        String sql = "SELECT item_id, SUM(quantity) AS total_quantity FROM _order GROUP BY item_id";
        return jdbcTemplate.queryForList(sql);
    }

    // 내부적 헬퍼 메서드 (userId로 affiliation_code 조회)
    private Integer findAffiliationCodeByUserId(String userId) {
        String sql = "SELECT affiliation_code FROM user WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{userId}, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }
}



//ApplicationContext context = SpringApplication.run(MainApplication.class, args);
//OrderDAO orderDAO = context.getBean(OrderDAO.class);
//
//// findById 호출
//OrderDTO order = orderDAO.findById(5001);
//        System.out.println(order);
//
//// insertOrder 호출
//OrderDTO newOrder = new OrderDTO();
//        newOrder.setOrderId(6001);
//        newOrder.setId("barista.lee");
//        newOrder.setItemId(2);
//        newOrder.setQuantity(5);
//        newOrder.setState("wait");
//        orderDAO.insertOrder(newOrder);
//
//// updateState 호출
//        orderDAO.updateState(6001, "completed");
//
//// affiliationCode로 그룹별 수량 합계 조회
//List<Map<String, Object>> sumsForAffiliation = orderDAO.getQuantitySumGroupedByItemForAffiliation(101);
//        System.out.println(sumsForAffiliation);
//
//// 전체 그룹별 수량 합계 조회 (오버로딩)
//List<Map<String, Object>> sumsAll = orderDAO.getQuantitySumGroupedByItemForUserAffiliation();
//        System.out.println(sumsAll);