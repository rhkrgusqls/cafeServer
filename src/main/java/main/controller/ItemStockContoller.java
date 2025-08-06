package main.controller;

import main.exception.InsertItemStockException;
import main.model.auth.AuthServiceSession;
import main.model.db.dao.ItemStockDAO;
import main.model.db.dto.db.ItemStockDTO;
import main.model.db.dto.itemStockList.ItemStockRequest;
import main.model.db.dto.itemStockList.JoinedItemStockDTO;

import main.properties.CustomProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//ToDo:Try catch
//ToDo:익셉션 바리에이션 추가

@RestController
@RequestMapping("/itemStock")
public class ItemStockContoller {

    @Autowired
    private ItemStockDAO itemStockDAO;

    @Autowired
    private AuthServiceSession authServiceSession;

    @Autowired
    private CustomProperties customProperties;

    @PostMapping("/list")
    public List<JoinedItemStockDTO> getItemStockList(
            @RequestBody(required = false) ItemStockRequest request,
            @RequestParam(required = false) String state) {

        try {
            if (request == null || request.getAffiliationCode() == null) {
                return null;
            }

            String sessionUser = authServiceSession.getSessionUser();
            String currentAffiliation = request.getAffiliationCode();

            if (!sessionUser.equals(currentAffiliation) && !sessionUser.equals(customProperties.getAffiliationCode())) {
                return null;
            }

            if (state == null || state.isEmpty()) {
                return itemStockDAO.getItemStockList(currentAffiliation);
            } else {
                return itemStockDAO.getItemStockList(currentAffiliation, state);
            }

        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/listAll")
    public List<JoinedItemStockDTO> getAllItemStockList(
            @RequestParam(required = false) String state) {

        try {
            String sessionUser = authServiceSession.getSessionUser();

            if (!sessionUser.equals(customProperties.getAffiliationCode())) {
                return null;
            }

            if (state == null || state.isEmpty()) {
                return itemStockDAO.getAllItemStockList();
            } else {
                return itemStockDAO.getAllItemStockList(state);
            }

        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/add")
    public String addItemStock(@RequestBody ItemStockDTO itemStock) {
        if (!authServiceSession.getSessionUser().equals(customProperties.getAffiliationCode())) {
            return "권한이 없습니다.";
        }

        try {
            itemStock.setAffiliationCode(authServiceSession.getSessionUser());
            int result = itemStockDAO.insertItemStock(itemStock);
            return result > 0 ? "물자추가 성공" : "추가 실패";

        } catch (InsertItemStockException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "예기치 못한 오류가 발생했습니다.";
        }
    }

    @GetMapping("/update")
    public String updateItemStock(@RequestParam int stockId,
                                  @RequestParam(required = false) Integer quantity,
                                  @RequestParam(required = false) String status) {
        if(!authServiceSession.getSessionUser().equals(itemStockDAO.findById(stockId).getAffiliationCode()) && !authServiceSession.getSessionUser().equals(customProperties.getAffiliationCode())) {
            return "권한이 없습니다.";
        }
        try {
            int result = 0;

            if (quantity != null && status != null) {
                result = itemStockDAO.updateItemStock(stockId, quantity, status);
            } else if (quantity != null) {
                result = itemStockDAO.updateItemStock(stockId, quantity);
            } else if (status != null) {
                result = itemStockDAO.updateItemStock(stockId, status);
            } else {
                return "수정할 항목이 없습니다.";
            }
            return result > 0 ? "업데이트 성공" : "업데이트 실패";
        } catch (Exception e) {
            return "예기치 못한 오류가 발생했습니다.";
        }
    }

    @GetMapping("/delete")
    public String deleteItemStock(@RequestParam int stockId) {
        if(!authServiceSession.getSessionUser().equals(itemStockDAO.findById(stockId).getAffiliationCode()) && !authServiceSession.getSessionUser().equals(customProperties.getAffiliationCode())) {
            return "권한이 없습니다.";
        }
        try {
            int result = itemStockDAO.deleteItemStock(stockId);
            return result > 0 ? "삭제 성공" : "삭제 실패";
        } catch (Exception e) {
            return "예기치 못한 오류가 발생했습니다.";
        }
    }

    @GetMapping("/decrease")
    public String decreaseStock(@RequestParam int itemId, @RequestParam String affiliationCode, @RequestParam int quantity) {
        if(!authServiceSession.getSessionUser().equals(affiliationCode) && !authServiceSession.getSessionUser().equals(customProperties.getAffiliationCode())) {
            return "권한이 없습니다.";
        }
        try {
            itemStockDAO.decreaseStock(itemId, affiliationCode, quantity);
            return "재고 감소 성공";
        } catch (Exception e) {
            return "예기치 못한 오류가 발생했습니다.";
        }
    }
}
