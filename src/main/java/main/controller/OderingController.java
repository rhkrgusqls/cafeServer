package main.controller;

import main.model.auth.AuthServiceSession;
import main.model.db.dao.ItemStockDAO;
import main.model.db.dao.OrderDAO;
import main.model.db.dto.db.ItemStockDTO;
import main.model.db.dto.db.OrderDTO;
import main.model.db.dto.itemStockList.ItemStockRequest;
import main.properties.CustomProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//ToDo:유저인증 인터페이스 적용 (권한분립과 본인인증 처리에 대한 시스템 대책마련 필요)
//ToDo:익셉션 바리에이션 추가

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/ordering")
public class OderingController {

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    CustomProperties customProperties;

    @Autowired
    private AuthServiceSession authServiceSession;

    @Autowired
    private ItemStockDAO itemStockDAO;
    //요청목록을 보기위한 메서드
    @PostMapping("/display")
    public List<OrderDTO> display(@RequestBody ItemStockRequest request) {
        if(!authServiceSession.getSessionUser().equals(request.getAffiliationCode()) || !authServiceSession.getSessionUser().equals(customProperties.getAffiliationCode())) {
            return null;
        }
        String affiliationCode = request.getAffiliationCode();

        return orderDAO.displayByAffiliationCode(affiliationCode);
    }

    @PostMapping("/request")
    public String request(@RequestBody ItemStockRequest request,
                          @RequestParam("item_id") int itemId,
                          @RequestParam("quantity") int quantity) {
        if(!authServiceSession.getSessionUser().equals(request.getAffiliationCode()) || !authServiceSession.getSessionUser().equals(customProperties.getAffiliationCode())) {
            return "권한이 없습니다.";
        }
        try {
            OrderDTO order = new OrderDTO();
            order.setItemId(itemId);
            order.setQuantity(quantity);
            order.setAffiliationCode(request.getAffiliationCode());

            int result = orderDAO.insertOrder(order);
            return result > 0 ? "요청을 발송했습니다." : "Failed to create order.";
        } catch (Exception e) {
            return "예기치 못한 오류가 발생했습니다.";
        }
    }

    @PostMapping("/accept")
    public String accept(@RequestBody ItemStockRequest request, @RequestParam int order_id) {
        if(!authServiceSession.getSessionUser().equals(request.getAffiliationCode()) || !authServiceSession.getSessionUser().equals(customProperties.getAffiliationCode())) {
            return "권한이 없습니다.";
        }
        try {

            if (customProperties.getAffiliationCode().equals(request.getAffiliationCode())) {
                int result = orderDAO.updateState(order_id, "processed");
                return result > 0 ? "요청이 수락되었습니다." : "Failed to update order.";
            }
            return "Unauthorized to accept order.";
        } catch (Exception e) {
            return "예기치 못한 오류가 발생했습니다.";
        }
    }

    @PostMapping("/dismissed")
    public String dismissed(@RequestBody ItemStockRequest request, @RequestParam int order_id) {
        if(!authServiceSession.getSessionUser().equals(request.getAffiliationCode()) || !authServiceSession.getSessionUser().equals(customProperties.getAffiliationCode())) {
            return "권한이 없습니다.";
        }
        try {
            if (customProperties.getAffiliationCode().equals(request.getAffiliationCode())) {
                int result = orderDAO.updateState(order_id, "dismissed");
                return result > 0 ? "요청이 파기되었습니다." : "Failed to update order.";
            }
            return "Unauthorized to dismiss order.";
        } catch (Exception e) {
            return "예기치 못한 오류가 발생했습니다.";
        }
    }

    @PostMapping("/completed")
    public String completed(@RequestBody ItemStockRequest request, @RequestParam int order_id) {
        if(!authServiceSession.getSessionUser().equals(request.getAffiliationCode()) || !authServiceSession.getSessionUser().equals(customProperties.getAffiliationCode())) {
            return "권한이 없습니다.";
        }
        try {
            OrderDTO orderDTO = orderDAO.findById(order_id);
            // ToDo: 내부 처리 서비스 로직으로 이전 예정
            itemStockDAO.transferStock(
                    orderDTO.getItemId(),
                    customProperties.getAffiliationCode(),
                    orderDTO.getAffiliationCode(),
                    orderDTO.getQuantity()
            );

            int result = orderDAO.updateState(order_id, "completed");
            return result > 0 ? "요청이 종결되었습니다." : "Failed to update order.";
        } catch (Exception e) {
            return "예기치 못한 오류가 발생했습니다.";
        }
    }

    @PostMapping("/review")
    public String review(@RequestBody ItemStockRequest request, @RequestParam int order_id) {
        if(!authServiceSession.getSessionUser().equals(request.getAffiliationCode()) || !authServiceSession.getSessionUser().equals(customProperties.getAffiliationCode())) {
            return "권한이 없습니다.";
        }
        try {
            int result = orderDAO.updateState(order_id, "re-review-needed");
            return result > 0 ? "재검토 요청이 발송되었습니다." : "Failed to update order.";
        } catch (Exception e) {
            return "예기치 못한 오류가 발생했습니다.";
        }
    }
}