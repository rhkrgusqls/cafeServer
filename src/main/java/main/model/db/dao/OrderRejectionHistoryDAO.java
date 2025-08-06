package main.model.db.dao;

import main.model.db.dto.db.OrderRejectionHistoryDTO;
import main.properties.CustomProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OrderRejectionHistoryDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final CustomProperties customProperties;

    @Autowired
    public OrderRejectionHistoryDAO(CustomProperties customProperties) {
        this.customProperties = customProperties;
    }

    /** 거절 이력 INSERT */
    public void insert(OrderRejectionHistoryDTO dto) {
        String sql = "INSERT INTO order_rejection_history (order_id, rejection_reason, rejection_time, notes) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                dto.getOrderId(),
                dto.getRejectionReason(),
                dto.getRejectionTime(),
                dto.getNotes()
        );
    }

    /** order_id로 거절 이력 조회 */
    public List<OrderRejectionHistoryDTO> findByAffiliationCode(String affiliationCode) {
        String sql;
        Object[] args;

        if (affiliationCode.equals("101")) {
            sql = """
        SELECT r.rejection_id, r.order_id, r.rejection_reason, r.rejection_time, r.notes
        FROM order_rejection_history r
        JOIN _order o ON r.order_id = o.order_id
        """;
            args = new Object[]{}; // 파라미터 없음
        } else {
            sql = """
        SELECT r.rejection_id, r.order_id, r.rejection_reason, r.rejection_time, r.notes
        FROM order_rejection_history r
        JOIN _order o ON r.order_id = o.order_id
        WHERE o.affiliation_code = ?
        """;
            args = new Object[]{affiliationCode};
        }

        return jdbcTemplate.query(sql, args, this::mapRowToDto);
    }

    /** affiliation_code별 거절 횟수 조회 */
    public int getRejectionCountByAffiliationCode(String affiliationCode) {
        String sql = """
            SELECT COUNT(r.rejection_id) AS rejection_count
            FROM order_rejection_history r
            JOIN _order o ON r.order_id = o.order_id
            WHERE o.affiliation_code = ?
        """;

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{affiliationCode}, Integer.class);
        } catch (Exception e) {
            return 0; // 조회 실패 시 0 리턴
        }
    }

    /** ResultSet → DTO 매핑 함수 */
    private OrderRejectionHistoryDTO mapRowToDto(ResultSet rs, int rowNum) throws SQLException {
        OrderRejectionHistoryDTO dto = new OrderRejectionHistoryDTO();
        dto.setRejectionId(rs.getInt("rejection_id"));
        dto.setOrderId(rs.getInt("order_id"));
        dto.setRejectionReason(rs.getString("rejection_reason"));
        dto.setRejectionTime(rs.getString("rejection_time"));
        dto.setNotes(rs.getString("notes"));
        return dto;
    }
}
