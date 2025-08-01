package main.controller;

import main.model.auth.AuthServiceDefault;
import main.model.db.dto.login.LoginRequest;
import main.model.db.dto.login.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import main.exception.LoginException;

//종결

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AuthServiceDefault authServiceDefault;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            //로그인시도
            return new LoginResponse(
                            authServiceDefault.login(
                                    request.getAffiliationCode(),
                                    request.getPassword())
                            ,"로그인 성공");
        } catch (LoginException e) {
            //로그인시 발생할 수 있는 로그인만의 에러를 반송 (보안상 다른 모든 익셉션을 전송하지는 않음 (로그인 로직에서 LoginException 에러를 발생시킴))
            return new LoginResponse(false, e.getMessage());
        } catch (Exception e) {
            //예상할 수 없는 에러는 숨기고 반송
            return new LoginResponse(false, "예기치 못한 오류가 발생했습니다.");
        }
    }
}
