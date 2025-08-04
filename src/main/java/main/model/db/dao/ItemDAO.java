package main.model.db.dao;

import main.model.db.dto.db.ItemDTO;
import main.model.db.dto.itemQuantity.ItemQuantityRequest;
import main.model.db.dto.itemQuantity.ItemQuantityResponse;
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

    public List<ItemQuantityResponse> getItemQuantityByItemAndAffiliation(ItemQuantityRequest request) {
        String sql = """
        SELECT i.item_id, i.name, i.category, IFNULL(SUM(s.quantity), 0) AS quantity
        FROM item i
        LEFT JOIN item_stock s 
          ON i.item_id = s.item_id AND s.affiliation_code = ?
        WHERE i.item_id = ?
        GROUP BY i.item_id, i.name, i.category
        """;

        return jdbcTemplate.query(sql, new Object[]{request.getAffiliationCode(), request.getItemId()}, (rs, rowNum) -> {
            ItemQuantityResponse result = new ItemQuantityResponse();
            result.setItemId(rs.getInt("item_id"));
            result.setName(rs.getString("name"));
            result.setCategory(rs.getString("category"));
            result.setQuantity(rs.getInt("quantity"));
            return result;
        });
    }

}
