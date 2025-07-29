package main.model.db.dao;

import main.model.db.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserDTO findById(String id) {
        String sql = "SELECT id, password, affiliation_code FROM user WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
                UserDTO user = new UserDTO();
                user.setId(rs.getString("id"));
                user.setPassword(rs.getString("password"));
                user.setAffiliationCode(rs.getString("affiliation_code"));
                return user;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int insertUser(UserDTO user) {
        String sql = "INSERT INTO user (id, password, affiliation_code) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, user.getId(), user.getPassword(), user.getAffiliationCode());
    }
}

