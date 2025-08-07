package main.log.db.dao;

import main.log.db.dto.ChangeLogDTO;
import main.log.db.dto.ConsumptionStatDTO;
import main.log.db.dto.ShipmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class LogDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insertHeadquarterShipment(int itemId, int quantity, String targetAffiliationCode, java.util.Date shipmentTime) {
        String sql = "INSERT INTO headquarter_shipments (item_id, quantity, target_affiliation_code, shipment_time) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, itemId, quantity, targetAffiliationCode, new java.sql.Timestamp(shipmentTime.getTime()));
    }

    public int insertConsumption(int itemId, int quantity, String affiliationCode, java.util.Date consumptionTime) {
        String sql = "INSERT INTO item_consumptions (item_id, quantity, affiliation_code, consumption_time) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, itemId, quantity, affiliationCode, new java.sql.Timestamp(consumptionTime.getTime()));
    }

    public int insertChangeLog(int itemId, int quantity, String changeType, String affiliationCode, java.util.Date changeTime) {
        String sql = "INSERT INTO inventory_change_logs (item_id, quantity, change_type, affiliation_code, change_time) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, itemId, quantity, changeType, affiliationCode, new java.sql.Timestamp(changeTime.getTime()));
    }

    // 시간순 정렬된 출고 리스트 (점포 + 아이템별)
    public List<ShipmentDTO> getShipmentsByAffiliationAndItem(String affiliationCode, int itemId, String groupType) {
        String groupFormat;
        switch (groupType.toLowerCase()) {
            case "day":
                groupFormat = "%Y-%m-%d";
                break;
            case "month":
                groupFormat = "%Y-%m";
                break;
            case "year":
                groupFormat = "%Y";
                break;
            default:
                throw new IllegalArgumentException("Invalid group type. Use 'day', 'month', or 'year'");
        }

        StringBuilder sql = new StringBuilder();

        String groupByClause = "";
        switch (groupType.toLowerCase()) {
            case "day":
                groupByClause = "DATE(shipment_time)";
                break;
            case "month":
                groupByClause = "DATE_FORMAT(shipment_time, '%Y-%m-01')";
                break;
            case "year":
                groupByClause = "DATE_FORMAT(shipment_time, '%Y-01-01')";
                break;
            default:
                throw new IllegalArgumentException("Invalid group type. Use 'day', 'month', or 'year'");
        }

                sql.append("""
            SELECT item_id,
                   SUM(quantity) AS total_quantity,
                   target_affiliation_code,
                   MIN(shipment_time) AS shipment_time
            FROM headquarter_shipments
            WHERE item_id = ?
        """);

        List<Object> params = new ArrayList<>();
        params.add(itemId);

        if (!"*".equals(affiliationCode)) {
            sql.append(" AND target_affiliation_code = ?");
            params.add(affiliationCode);
        }

        sql.append(" GROUP BY item_id, target_affiliation_code, " + groupByClause);
        sql.append(" ORDER BY shipment_time ASC");

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> new ShipmentDTO(
                rs.getInt("item_id"),
                rs.getInt("total_quantity"),
                rs.getString("target_affiliation_code"),
                rs.getTimestamp("shipment_time")  // DTO Timestamp 타입 유지
        ));
    }

    // 주간/월간/연간 소비량 합산 (점포별)
    public List<ConsumptionStatDTO> getConsumptionStatsByAffiliationAndItem(String period, Date startDate, String affiliationCode, int itemId) {
        String timeFormat;
        switch (period.toLowerCase()) {
            case "week":
                timeFormat = "%Y-%u";
                break;
            case "month":
                timeFormat = "%Y-%m";
                break;
            case "year":
                timeFormat = "%Y";
                break;
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }

        String sql = """
        SELECT item_id,
               DATE_FORMAT(consumption_time, ?) as period,
               SUM(quantity) as total_quantity
        FROM item_consumptions
        WHERE consumption_time >= ? AND affiliation_code = ? AND item_id = ?
        GROUP BY item_id, period
        ORDER BY period ASC
    """;

        return jdbcTemplate.query(sql,
                new Object[]{timeFormat, new java.sql.Timestamp(startDate.getTime()), affiliationCode, itemId},
                (rs, rowNum) -> new ConsumptionStatDTO(
                        rs.getInt("item_id"),
                        rs.getString("period"),
                        rs.getInt("total_quantity")
                ));
    }

    // 재고 변화 로그 - 점포 및 아이템별 조회
    public List<ChangeLogDTO> getInventoryChangeLogsByAffiliationAndItem(String affiliationCode, int itemId) {
        String sql = """
        SELECT item_id, quantity, change_type, affiliation_code, change_time
        FROM inventory_change_logs
        WHERE affiliation_code = ? AND item_id = ?
        ORDER BY change_time ASC
    """;
        return jdbcTemplate.query(sql, new Object[]{affiliationCode, itemId}, (rs, rowNum) -> new ChangeLogDTO(
                rs.getInt("item_id"),
                rs.getInt("quantity"),
                rs.getString("change_type"),
                rs.getString("affiliation_code"),
                rs.getTimestamp("change_time")
        ));
    }
    public List<Map<String, Integer>> getMonthlyInventoryBreakdown(String month, int itemId, int affiliationCode) {
        String sql = """
        SELECT change_type, SUM(quantity) AS sum_qty
        FROM inventory_change_logs
        WHERE item_id = ?
          AND affiliation_code = ?
          AND DATE_FORMAT(change_time, '%Y-%m') = ?
        GROUP BY change_type
    """;

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, itemId, affiliationCode, month);

        int inbound = 0;
        int modify = 0;
        int usage = 0;
        int disposal = 0;
        int shipment = 0;

        for (Map<String, Object> row : results) {
            String type = (String) row.get("change_type");
            int qty = ((Number) row.get("sum_qty")).intValue();

            switch (type) {
                case "INBOUND" -> inbound += qty;
                case "MODIFY" -> modify += qty;
                case "USAGE" -> usage += Math.abs(qty);
                case "DISPOSAL" -> disposal += Math.abs(qty);
                case "SHIPMENT" -> shipment += Math.abs(qty);
            }
        }

        int leftover = inbound + modify - (usage + disposal + shipment);

        List<Map<String, Integer>> result = new ArrayList<>();
        result.add(Map.of("DISPOSAL", disposal));
        result.add(Map.of("USAGE", usage));
        result.add(Map.of("SHIPMENT", shipment));
        result.add(Map.of("LEFTOVER", leftover));

        return result;
    }
}
