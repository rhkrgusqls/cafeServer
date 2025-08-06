package main.log.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
