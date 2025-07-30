package main.model.db.dao;

import main.model.db.dto.affiliationList.AffiliationListDTO;
import main.model.db.dto.db.AffiliationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AffiliationDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public AffiliationDTO findByCode(String code) {
        String sql = "SELECT affiliation_code, password, store_name FROM affiliation WHERE affiliation_code = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{code}, (rs, rowNum) -> {
                AffiliationDTO aff = new AffiliationDTO();
                aff.setAffiliationCode(rs.getString("affiliation_code"));
                aff.setPassword(rs.getString("password"));
                aff.setStoreName(rs.getString("store_name"));
                return aff;
            });
        } catch (Exception e) {
            return null;
        }
    }

    public int insertAffiliation(AffiliationDTO affiliation) {
        String sql = "INSERT INTO affiliation (affiliation_code, store_name, password) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, affiliation.getAffiliationCode(), affiliation.getStoreName(), affiliation.getPassword());
    }

    public List<AffiliationListDTO> getAllAffiliationList() {
        String sql = "SELECT affiliation_code, store_name FROM affiliation";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            AffiliationListDTO aff = new AffiliationListDTO();
            aff.setAffiliationCode(rs.getString("affiliation_code"));
            aff.setStoreName(rs.getString("store_name"));
            return aff;
        });
    }
}
