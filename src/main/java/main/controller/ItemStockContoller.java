package main.controller;

import main.model.db.dao.ItemStockDAO;
import main.model.db.dto.db.ItemStockDTO;
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

    // 기존 조회 메서드 유지
    @PostMapping("/list")
    public List<JoinedItemStockDTO> getItemStockList(
            @RequestBody ItemStockRequest request,
            @RequestParam(required = false) String state) {

        if (state == null || state.isEmpty()) {
            return itemStockDAO.getItemStockList(request.getAffiliationCode());
        } else {
            return itemStockDAO.getItemStockList(request.getAffiliationCode(), state);
        }
    }

    // 추가 (Create)
    @PostMapping("/add")
    public String addItemStock(@RequestBody ItemStockDTO itemStock) {
        int result = itemStockDAO.insertItemStock(itemStock);
        return result > 0 ? "Insert success" : "Insert failed";
    }

    // 수정 (Update quantity + status 둘 다 가능하게)
    @PostMapping("/update")
    public String updateItemStock(@RequestBody ItemStockDTO itemStock) {
        int result = itemStockDAO.updateItemStock(itemStock.getStockId(), itemStock.getQuantity(), itemStock.getStatus());
        return result > 0 ? "Update success" : "Update failed";
    }

    // 삭제 (Delete)
    @PostMapping("/delete")
    public String deleteItemStock(@RequestParam int stockId) {
        int result = itemStockDAO.deleteItemStock(stockId);  // DAO에 deleteItemStock 메서드 추가 필요
        return result > 0 ? "Delete success" : "Delete failed";
    }

    // 재고 감소 (decreaseStock 호출)
    @PostMapping("/decrease")
    public String decreaseStock(@RequestParam int itemId, @RequestParam int affiliationCode, @RequestParam int quantity) {
        try {
            itemStockDAO.decreaseStock(itemId, affiliationCode, quantity);
            return "Stock decreased successfully";
        } catch (Exception e) {
            return "Failed: " + e.getMessage();
        }
    }
}
