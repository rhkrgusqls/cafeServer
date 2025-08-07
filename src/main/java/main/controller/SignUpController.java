package main.controller;


import main.exception.SignupException;
import main.model.auth.AuthServiceDefault;
import main.model.db.dao.ItemLimitsDAO;
import main.model.db.dto.signup.SignUpRequest;
import main.model.db.dto.signup.SignUpResponse;
import main.refresh.RefreshWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//종결

@RestController
@RequestMapping("/register")
public class SignUpController {

    @Autowired
    private RefreshWebSocketHandler refreshWebSocketHandler;

    @Autowired
    private AuthServiceDefault authServiceDefault;

    @Autowired
    private ItemLimitsDAO itemLimitsDAO;

    @PostMapping("/signup")
    public SignUpResponse signup(@RequestBody SignUpRequest request) {
        try {
            // 회원가입 수행
            authServiceDefault.signup(
                    request.getAffiliationCode(),
                    request.getPassword(),
                    request.getStoreName()
            );

            // 회원가입 성공 후 item_limits 초기화
            itemLimitsDAO.insertItemLimitsIfNotExists(request.getAffiliationCode());
            refreshWebSocketHandler.notifyAdmin(List.of("storeManagement"));
            return new SignUpResponse(true, "회원가입 성공");
        } catch (SignupException e) {
            // 보안상 회원가입 관련 예외만 클라이언트에 전달
            return new SignUpResponse(false, e.getMessage());
        } catch (Exception e) {
            // 기타 예외는 숨기고 일반 메시지 반환
            return new SignUpResponse(false, "예기치 못한 오류가 발생했습니다.");
        }
    }

}
