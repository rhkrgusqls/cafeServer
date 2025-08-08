package main.log.db.dao;

import main.log.db.dto.ChangeLogDTO;
import main.log.db.dto.ConsumptionStatDTO;
import main.log.db.dto.ShipmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    public List<ShipmentDTO> getShipmentsByAffiliationAndItem(String affiliationCode, int itemId, String groupType) {
        String groupByClause;
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

        StringBuilder sql = new StringBuilder();

        sql.append("""
        SELECT item_id,
               SUM(quantity) AS total_quantity,
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

        sql.append(" GROUP BY item_id, " + groupByClause);
        sql.append(" ORDER BY shipment_time ASC");

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> new ShipmentDTO(
                rs.getInt("item_id"),
                rs.getInt("total_quantity"),
                affiliationCode,  // 파라미터로 들어온 값 고정으로 넣기
                rs.getTimestamp("shipment_time")
        ));
    }



    // 주간/월간/연간 소비량 합산 (점포별)
    public List<ConsumptionStatDTO> getConsumptionStatsByAffiliationAndItem(String period, Date startDate, String affiliationCode, int itemId) {
        String timeFormat;
        switch (period.toLowerCase()) {
            case "day":
                timeFormat = "%Y-%m-%d";
                break;
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
    public Map<String, Integer> getMonthlyInventoryBreakdown(String month,int  itemId, String affiliationCode) {
        // 월별 누적 LEFTOVER 계산용
        String sumSql = """
        SELECT change_type, SUM(quantity) AS sum_qty
        FROM inventory_change_logs
        WHERE item_id = ?
          AND affiliation_code = ?
          AND DATE_FORMAT(change_time, '%Y-%m') <= ?
        GROUP BY change_type
    """;

        // 이번 달 기준 음수 데이터만 추출용
        String filteredSql = """
        SELECT change_type, SUM(quantity) AS sum_qty
        FROM inventory_change_logs
        WHERE item_id = ?
          AND affiliation_code = ?
          AND DATE_FORMAT(change_time, '%Y-%m') = ?
        GROUP BY change_type
    """;

        List<Map<String, Object>> currentMonth = jdbcTemplate.queryForList(filteredSql, itemId, affiliationCode, month);
        List<Map<String, Object>> cumulative = jdbcTemplate.queryForList(sumSql, itemId, affiliationCode, month);

        int disposal = 0;
        int usage = 0;
        int shipment = 0;
        int leftover = 0;

        // 현재 월 음수 데이터 필터링
        for (Map<String, Object> row : currentMonth) {
            String type = (String) row.get("change_type");
            int qty = ((Number) row.get("sum_qty")).intValue();

            if (qty >= 0) continue;

            switch (type) {
                case "DISPOSAL" -> disposal += Math.abs(qty);
                case "USAGE" -> usage += Math.abs(qty);
                case "SHIPMENT" -> shipment += Math.abs(qty);
            }
        }

        // 전체 누적 LEFTOVER 계산
        for (Map<String, Object> row : cumulative) {
            int qty = ((Number) row.get("sum_qty")).intValue();
            leftover += qty;
        }

        Map<String, Integer> result = new LinkedHashMap<>();
        if (disposal > 0) result.put("DISPOSAL", disposal);
        if (usage > 0) result.put("USAGE", usage);
        if (shipment > 0) result.put("SHIPMENT", shipment);
        result.put("LEFTOVER", leftover);

        return result;
    }
}
