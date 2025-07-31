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

    @PostMapping("/accept")
    public String accept(@RequestBody ItemStockRequest request, @RequestParam int order_id) {
        if ("101".equals(request.getAffiliationCode())) {
            int result = orderDAO.updateState(order_id, "processed"); // 예: 처리됨 상태
            return result > 0 ? "Order accepted." : "Failed to update order.";
        }
        return "Unauthorized to accept order.";
    }

    @PostMapping("/dismissed")
    public String dismissed(@RequestBody ItemStockRequest request, @RequestParam int order_id) {
        if ("101".equals(request.getAffiliationCode())) {
            int result = orderDAO.updateState(order_id, "dismissed"); // 예: 거절 상태
            return result > 0 ? "Order dismissed." : "Failed to update order.";
        }
        return "Unauthorized to dismiss order.";
    }

    @PostMapping("/review")
    public String review(@RequestBody ItemStockRequest request, @RequestParam int order_id) {
        int result = orderDAO.updateState(order_id, "reviewing"); // 검토중 상태
        return result > 0 ? "Order set to reviewing." : "Failed to update order.";
    }

}