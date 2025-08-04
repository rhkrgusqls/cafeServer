package main.controller;

import main.exception.DeleteAffiliationException;
import main.model.auth.AuthServiceSession;
import main.model.db.dao.ItemDAO;
import main.model.db.dto.db.ItemDTO;
import main.model.db.dto.db.ItemStockDTO;
import main.model.db.dto.itemQuantity.ItemQuantityRequest;
import main.model.db.dto.itemQuantity.ItemQuantityResponse;
import main.properties.CustomProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/quantity")
    public ResponseEntity<?> getItemQuantity(@RequestBody ItemQuantityRequest request) {
        // 세션의 사용자와 요청의 affiliationCode 일치 여부 확인
        if (!authServiceSession.getSessionUser().equals(request.getAffiliationCode())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }

        try {
            List<ItemQuantityResponse> results = itemDAO.getItemQuantityByItemAndAffiliation(request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예기치 못한 오류가 발생");
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
