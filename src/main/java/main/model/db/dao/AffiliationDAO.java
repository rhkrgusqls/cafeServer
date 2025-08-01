package main.model.db.dao;

import main.exception.DeleteAffiliationException;
import main.model.db.dto.affiliationList.AffiliationListDTO;
import main.model.db.dto.db.AffiliationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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

    /**
     * 가맹점을 삭제시 백업으로 옮기며 삭제시도
     */
    public int deleteAffiliation(String affiliationCode) {
        // 1. 존재 여부 확인
        String checkSql = "SELECT affiliation_code FROM affiliation WHERE affiliation_code = ?";
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(checkSql, affiliationCode);
            if (result == null || result.isEmpty()) {
                throw new DeleteAffiliationException("존재하지 않는 가맹점 코드입니다: " + affiliationCode);
            }
        } catch (DataAccessException e) {
            throw new DeleteAffiliationException("가맹점 코드 조회 중 오류 발생: " + e.getMessage());
        }

        // 2. 트랜잭션 함수 호출
        String callSql = "CALL deleteAffiliationAndBackup(?)";
        try {
            jdbcTemplate.update(callSql, affiliationCode);
        } catch (DataAccessException e) {
            String message = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();

            String userMessage;
            if (message.contains("foreign key constraint fails")) {
                userMessage = "삭제할 수 없습니다. 관련된 다른 데이터가 존재합니다.";
            } else if (message.contains("doesn't exist")) {
                userMessage = "존재하지 않는 데이터입니다.";
            } else if (message.contains("Duplicate entry")) {
                userMessage = "중복된 항목이 이미 존재합니다.";
            } else {
                userMessage = "가맹점 삭제 중 알 수 없는 오류가 발생했습니다.";
            }

            throw new DeleteAffiliationException(userMessage + " [기술적 원인: " + message + "]");
        }
        return 1; // 성공
    }
}
