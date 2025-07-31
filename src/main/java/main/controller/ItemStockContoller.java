package main.controller;

import main.model.db.dao.ItemStockDAO;
import main.model.db.dto.itemStockList.ItemStockRequest;
import main.model.db.dto.itemStockList.JoinedItemStockDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/itemStock")
public class ItemStockContoller {

    @Autowired
    private ItemStockDAO itemStockDAO;

    @PostMapping("/list")
    public List<JoinedItemStockDTO> getItemStockList(
            @RequestBody ItemStockRequest request,
            @RequestParam(required = false) String state) {

        try {
            if (state == null || state.isEmpty()) {
                return itemStockDAO.getItemStockList((request.getAffiliationCode()));
            } else {
                return itemStockDAO.getItemStockList((request.getAffiliationCode()), state);
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}