package main.log.db.dao;

import main.log.db.dto.LogDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LogDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     *
     */
    public int insertLog(LogDTO log) {
        String sql = "INSERT INTO action_log (user_id, action, details, ip_address, affiliation_code) " +
                "VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                log.getUserId(),
                log.getAction(),
                log.getDetails(),
                log.getIpAddress(),
                log.getAffiliationCode());
    }
}
