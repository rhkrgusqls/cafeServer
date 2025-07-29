package main.model.db.dao;

import main.model.db.dto.AffiliationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AffiliationDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public AffiliationDTO findByCode(int code) {
        String sql = "SELECT affiliation_code, store_name FROM affiliation WHERE affiliation_code = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{code}, (rs, rowNum) -> {
                AffiliationDTO aff = new AffiliationDTO();
                aff.setAffiliationCode(rs.getInt("affiliation_code"));
                aff.setStoreName(rs.getString("store_name"));
                return aff;
            });
        } catch (Exception e) {
            return null;
        }
    }

    public int insertAffiliation(AffiliationDTO affiliation) {
        String sql = "INSERT INTO affiliation (affiliation_code, store_name) VALUES (?, ?)";
        return jdbcTemplate.update(sql, affiliation.getAffiliationCode(), affiliation.getStoreName());
    }
}
