package main.model.db.dao;

import main.model.db.dto.db.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ItemDTO findById(int itemId) {
        String sql = "SELECT item_id, name, category FROM item WHERE item_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{itemId}, (rs, rowNum) -> {
                ItemDTO item = new ItemDTO();
                item.setItemId(rs.getInt("item_id"));
                item.setName(rs.getString("name"));
                item.setCategory(rs.getString("category"));
                return item;
            });
        } catch (Exception e) {
            return null;
        }
    }

    public int insertItem(ItemDTO item) {
        String sql = "INSERT INTO item (item_id, name, category) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, item.getItemId(), item.getName(), item.getCategory());
    }

    public List<ItemDTO> getItemList() {
        String sql = "SELECT item_id, name, category FROM item";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ItemDTO item = new ItemDTO();
            item.setItemId(rs.getInt("item_id"));
            item.setName(rs.getString("name"));
            item.setCategory(rs.getString("category"));
            return item;
        });
    }
}
