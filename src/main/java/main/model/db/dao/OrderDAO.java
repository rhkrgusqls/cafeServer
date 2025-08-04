package main.model.db.dao;

import main.model.db.dto.affiliationList.AffiliationListDTO;
import main.model.db.dto.db.OrderDTO;
import main.properties.CustomProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

//ToDO:유저인증 인터페이스 적용

@Repository
public class OrderDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final CustomProperties customProperties;

    @Autowired
    public OrderDAO(CustomProperties customProperties) {
        this.customProperties = customProperties;
    }

    /** 하나의 주문내역을 상세 조회 */
    public OrderDTO findById(int orderId) {
        String sql = "SELECT order_id, item_id, quantity, order_date, state FROM _order WHERE order_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{orderId}, (rs, rowNum) -> {
                OrderDTO order = new OrderDTO();
                order.setOrderId(rs.getInt("order_id"));
                order.setItemId(rs.getInt("item_id"));
                order.setQuantity(rs.getInt("quantity"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setState(rs.getString("state"));
                return order;
            });
        } catch (Exception e) {
            return null;
        }
    }

    /** DTO를 외부에서 정의 후 삽입하며 데이터 생성 이때 order_date는 CURRENT_TIMESTAMP로 자동 생성됨 */
    public int insertOrder(OrderDTO order) {
        String sql = "INSERT INTO _order (item_id, affiliation_code, quantity) VALUES ( ?, ?, ?)";
        return jdbcTemplate.update(sql,
                order.getItemId(),
                order.getAffiliationCode(),
                order.getQuantity());
    }

    /** 대기상태 wait,
     * reviewing 검토중상태,
     * processed요청 처리됨상태(본점에서만 검토됨),
     * re-review-needed 재검토필요상태 ,
     * 종결상태 completed */
    public int updateState(int orderId, String newState) {
        String sql = "UPDATE _order SET state = ? WHERE order_id = ?";
        return jdbcTemplate.update(sql, newState, orderId);
    }

    /** affiliation_code로 묶어 item_id별 quantity 합산 조회*/
    public List<Map<String, Object>> getQuantityByItem(String affiliationCode) {
        String sql = "SELECT item_id, SUM(quantity) AS total_quantity " +
                "FROM _order " +
                "WHERE affiliation_code = ? " +
                "GROUP BY item_id";
        return jdbcTemplate.queryForList(sql, affiliationCode);
    }

    /** 전체 item_id별 quantity 합산 조회 (오버로딩)*/
    public List<Map<String, Object>> getQuantityByItem() {
        String sql = "SELECT item_id, SUM(quantity) AS total_quantity FROM _order GROUP BY item_id";
        return jdbcTemplate.queryForList(sql);
    }

    /** 전체 주문 내역 조회 */
    public List<OrderDTO> displayByAffiliationCode(String affiliationCode) {
        String sql;
        Object[] params;
        if (affiliationCode.equals(customProperties.getAffiliationCode())) {
            // 전체 조회
            sql = "SELECT order_id, item_id, quantity, order_date, state, affiliation_code FROM _order ORDER BY order_date DESC";
            params = new Object[] { };
        } else {
            // 특정 affiliation_code만 조회
            sql = "SELECT order_id, item_id, quantity, order_date, state, affiliation_code FROM _order WHERE affiliation_code = ? ORDER BY order_date DESC";
            params = new Object[] { affiliationCode };
        }

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            OrderDTO order = new OrderDTO();
            order.setOrderId(rs.getInt("order_id"));
            order.setAffiliationCode(rs.getString("affiliation_code"));
            order.setItemId(rs.getInt("item_id"));
            order.setQuantity(rs.getInt("quantity"));
            order.setOrderDate(rs.getTimestamp("order_date"));
            order.setState(rs.getString("state"));
            return order;
        });
    }
}