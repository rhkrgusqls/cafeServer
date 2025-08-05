package main.controller;

import main.exception.DeleteAffiliationException;
import main.model.auth.AuthServiceSession;
import main.model.db.dao.ItemDAO;
import main.model.db.dto.db.AffiliationDTO;
import main.model.db.dto.db.ItemDTO;
import main.model.db.dto.db.ItemStockDTO;
import main.model.db.dto.itemQuantity.ItemQuantityRequest;
import main.model.db.dto.itemQuantity.ItemQuantityResponse;
import main.properties.CustomProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

//ToDo:삭제요청에 대해 검토가 필요(잘못만든 데이터, 이제는 판매하지 않는 물품에 대해서는 어떻게 처리하는게 좋을것인가, DB가 꼬이기에 함부로 삭제하긴 힘들다.)

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemDAO itemDAO;

    @Autowired
    private AuthServiceSession authServiceSession;

    @Autowired
    private CustomProperties customProperties;

    @GetMapping("/list")
    public List<ItemDTO> getItemList() {
        return itemDAO.getItemList();
    }

    @PostMapping("/add")
    public String getItemList(@RequestBody ItemDTO itemDTO) {
        if(!authServiceSession.getSessionUser().equals(customProperties.getAffiliationCode())) {
            return "권한이 없습니다.";
        }
        try {
            itemDAO.insertItem(itemDTO);
            return "추가 성공";
        } catch (DeleteAffiliationException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "예기치 못한 오류가 발생했습니다.";
        }
    }
    @GetMapping("/quantity")
    public List<ItemQuantityResponse> getItemQuantity(
            @RequestParam List<Integer> itemIds,
            @RequestParam(required = false) String affiliationCode) {

        String sessionUser = authServiceSession.getSessionUser();
        List<ItemQuantityResponse> result = new ArrayList<>();
        try {
            if(affiliationCode == null){
                for (int itemId : itemIds) {
                    List<ItemQuantityResponse> partialResult = itemDAO.getItemQuantityByItemAndAffiliation(itemId, sessionUser);
                    result.addAll(partialResult);
                }
                return result;
            }
            if (sessionUser.equals(customProperties.getAffiliationCode())) {
                for (int itemId : itemIds) {
                    List<ItemQuantityResponse> partialResult = itemDAO.getItemQuantityByItemAndAffiliation(itemId, affiliationCode);
                    result.addAll(partialResult);
                }
                return result;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    @GetMapping("/setState")
    public ResponseEntity<String> setState(@RequestParam int itemId, @RequestParam String state) {
        if(!authServiceSession.getSessionUser().equals(customProperties.getAffiliationCode())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
        try {
            itemDAO.updateItemState(itemId, state);
            return ResponseEntity.ok("상태 변환에 성공했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예기치 못한 오류가 발생");
        }
    }
}
