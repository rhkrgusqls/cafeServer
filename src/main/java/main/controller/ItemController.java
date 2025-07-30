package main.controller;

import main.model.db.dao.ItemDAO;
import main.model.db.dto.db.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemDAO itemDAO;

    @GetMapping("/list")
    public List<ItemDTO> getItemList() {
        return itemDAO.getItemList();
    }
}
