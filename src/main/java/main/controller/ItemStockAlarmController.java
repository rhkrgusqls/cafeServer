package main.controller;

import main.model.auth.AuthServiceSession;
import main.model.db.dao.ItemLimitsDAO;
import main.model.db.dto.itemStock.ItemLimitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itemStock/alarm")
public class ItemStockAlarmController {

    @Autowired
    private ItemLimitsDAO itemLimitsDAO;

    @Autowired
    private AuthServiceSession authServiceSession;

    /**
     * GET: 특정 affiliationCode에 해당하는 item_limits 목록 반환
     */
    @GetMapping("/list")
    public List<ItemLimitDTO> getItemLimits() {
        try {
            return itemLimitsDAO.getItemLimitsWithStockCheckByAffiliationCode(authServiceSession.getSessionUser());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * POST: 특정 item_id, affiliation_code 조합의 quantity 수정
     */
    @GetMapping("/update")
    public boolean updateItemLimit(@RequestParam int itemId, @RequestParam int quantity) {
        try {
            int affected = itemLimitsDAO.updateItemLimitQuantity(
                    itemId,
                    authServiceSession.getSessionUser(),
                    quantity
            );
            return affected > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
