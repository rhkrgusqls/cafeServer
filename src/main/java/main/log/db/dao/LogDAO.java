package main.log.db.dao;

import main.log.db.dto.ChangeLogDTO;
import main.log.db.dto.ConsumptionStatDTO;
import main.log.db.dto.ShipmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

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

    // 시간순 정렬된 출고 리스트 (점포별)
    public List<ShipmentDTO> getAllShipmentsByAffiliation(String affiliationCode) {
        String sql = """
        SELECT item_id, quantity, target_affiliation_code, shipment_time
        FROM headquarter_shipments
        WHERE target_affiliation_code = ?
        ORDER BY shipment_time ASC
    """;
        return jdbcTemplate.query(sql, new Object[]{affiliationCode}, (rs, rowNum) -> new ShipmentDTO(
                rs.getInt("item_id"),
                rs.getInt("quantity"),
                rs.getString("target_affiliation_code"),
                rs.getTimestamp("shipment_time")
        ));
    }

    // 주간/월간/연간 소비량 합산 (점포별)
    public List<ConsumptionStatDTO> getConsumptionStatsByAffiliation(String period, Date startDate, String affiliationCode) {
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
        WHERE consumption_time >= ? AND affiliation_code = ?
        GROUP BY item_id, period
        ORDER BY period ASC
    """;

        return jdbcTemplate.query(sql,
                new Object[]{timeFormat, new java.sql.Timestamp(startDate.getTime()), affiliationCode},
                (rs, rowNum) -> new ConsumptionStatDTO(
                        rs.getInt("item_id"),
                        rs.getString("period"),
                        rs.getInt("total_quantity")
                ));
    }

    // 재고 변화 로그 전체 (점포별)
    public List<ChangeLogDTO> getAllInventoryChangeLogsByAffiliation(String affiliationCode) {
        String sql = """
        SELECT item_id, quantity, change_type, affiliation_code, change_time
        FROM inventory_change_logs
        WHERE affiliation_code = ?
        ORDER BY change_time ASC
    """;
        return jdbcTemplate.query(sql, new Object[]{affiliationCode}, (rs, rowNum) -> new ChangeLogDTO(
                rs.getInt("item_id"),
                rs.getInt("quantity"),
                rs.getString("change_type"),
                rs.getString("affiliation_code"),
                rs.getTimestamp("change_time")
        ));
    }

}
