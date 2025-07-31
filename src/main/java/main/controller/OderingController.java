package main.controller;

import main.model.db.dao.OrderDAO;
import main.model.db.dto.db.OrderDTO;
import main.model.db.dto.itemStockList.ItemStockRequest;
import main.model.db.dto.login.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//ToDO:유저인증 인터페이스 적용

import java.util.List;

@RestController
@RequestMapping("/ordering")
public class OderingController {

    @Autowired
    private OrderDAO orderDAO;

    //요청목록을 보기위한 메서드
    @PostMapping("/display")
    public List<OrderDTO> display(@RequestBody ItemStockRequest request) {
        String affiliationCode = request.getAffiliationCode();

        return orderDAO.displayByAffiliationCode(affiliationCode);
    }
    @PostMapping("/request")
    public String request(@RequestBody ItemStockRequest request,
                          @RequestParam("item_id") int itemId,
                          @RequestParam("quantity") int quantity) {

        OrderDTO order = new OrderDTO();
        order.setItemId(itemId);
        order.setQuantity(quantity);
        order.setAffiliationCode(request.getAffiliationCode());

        int result = orderDAO.insertOrder(order);

        return result > 0 ? "Order successfully created." : "Failed to create order.";
    }

    //요청을 수락하기 위한 메서드
    @PostMapping("/accept")
    public String accept(@RequestBody ItemStockRequest request, @RequestParam int order_id) {
        if ("101".equals(request.getAffiliationCode())) {
            int result = orderDAO.updateState(order_id, "processed"); // 예: 처리됨 상태

            return result > 0 ? "Order accepted." : "Failed to update order.";
        }
        return "Unauthorized to accept order.";
    }

    //요청을 거절하기위한 메서드
    @PostMapping("/dismissed")
    public String dismissed(@RequestBody ItemStockRequest request, @RequestParam int order_id) {
        if ("101".equals(request.getAffiliationCode())) {
            int result = orderDAO.updateState(order_id, "dismissed"); // 예: 거절 상태
            return result > 0 ? "Order dismissed." : "Failed to update order.";
        }
        return "Unauthorized to dismiss order.";
    }

    //수락된 요청에 대해 검토 후 물자확인이 되었다면 체크하는 메서드
    @PostMapping("/completed")
    public String completed(@RequestBody ItemStockRequest request, @RequestParam int order_id) {
        int result = orderDAO.updateState(order_id, "completed");

        //ToDo:내부처리 추가 서비스로 변환

        return result > 0 ? "Order set to reviewing." : "Failed to update order.";
    }

    //수락된 요청에 대해 검토 후 물자확인중 문제가 있다면 체크하는 메서드
    @PostMapping("/review")
    public String review(@RequestBody ItemStockRequest request, @RequestParam int order_id) {
        int result = orderDAO.updateState(order_id, "re-review-needed"); // 검토중 상태
        return result > 0 ? "Order set to reviewing." : "Failed to update order.";
    }

}