package main.controller;

import main.model.db.dao.ItemStockDAO;
import main.model.db.dto.db.ItemStockDTO;
import main.model.db.dto.db.JoinedItemStockDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/itemStock")
public class ItemStockContoller {
    @Autowired
    private ItemStockDAO itemStockDAO;

    @GetMapping("/list")
    public List<JoinedItemStockDTO> getItemStockList(
            @RequestParam int affiliationCode,
            @RequestParam(required = false) String state) {

        try {
            if (state == null || state.isEmpty()) {
                //오버리딩 state가 없다면 자동으로 available 상태인 리스트만 출력
                return itemStockDAO.getItemStockList(affiliationCode);
            } else {
                //오버리딩 state가 있다면 해당 state의 리스트 출력
                return itemStockDAO.getItemStockList(affiliationCode, state);
            }
        } catch (Exception e) {
            // 예외 발생시 빈 리스트 반환 또는 별도 처리
            return Collections.emptyList();
        }
    }
}
