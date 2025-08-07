package main.model.db.dao;

import main.model.db.dto.db.ItemLimitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// DAO class 예시
@Repository
public class ItemLimitsDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insertItemLimitsIfNotExists(String affiliationCode) {
        String sql = """
            INSERT INTO item_limits (item_id, affiliation_code, quantity)
            SELECT i.item_id, ?, 0
            FROM item i
            WHERE NOT EXISTS (
                SELECT 1
                FROM item_limits il
                WHERE il.item_id = i.item_id
                  AND il.affiliation_code = ?
            )
        """;

        return jdbcTemplate.update(sql, affiliationCode, affiliationCode);
    }

    public List<ItemLimitDTO> getItemLimitsByAffiliationCode(String affiliationCode) {
        String sql = """
                    SELECT item_id, affiliation_code, quantity
                    FROM item_limits
                    WHERE affiliation_code = ?
                """;

        return jdbcTemplate.query(sql, new Object[]{affiliationCode}, new RowMapper<ItemLimitDTO>() {
            @Override
            public ItemLimitDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                ItemLimitDTO dto = new ItemLimitDTO();
                dto.setItemId(rs.getInt("item_id"));
                dto.setAffiliationCode(rs.getString("affiliation_code"));
                dto.setQuantity(rs.getInt("quantity"));
                return dto;
            }
        });
    }

    public int updateItemLimitQuantity(int itemId, String affiliationCode, int quantity) {
        String sql = """
        UPDATE item_limits
        SET quantity = ?
        WHERE item_id = ?
          AND affiliation_code = ?
    """;

        return jdbcTemplate.update(sql, quantity, itemId, affiliationCode);
    }

    public boolean isStockQuantityWithinLimit(int itemId, String affiliationCode) {
        String sql = """
        SELECT
            COALESCE(SUM(s.quantity), 0) <= l.quantity AS is_valid
        FROM item_limits l
        LEFT JOIN item_stock s
            ON l.item_id = s.item_id
           AND l.affiliation_code = s.affiliation_code
           AND s.status = 'available'
        WHERE l.item_id = ?
          AND l.affiliation_code = ?
        GROUP BY l.quantity
    """;

        Boolean result = jdbcTemplate.queryForObject(
                sql,
                new Object[]{itemId, affiliationCode},
                Boolean.class
        );

        return result != null && result;
    }

}
