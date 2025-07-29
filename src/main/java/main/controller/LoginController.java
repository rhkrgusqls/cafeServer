package main.controller;

import main.model.auth.AuthService;
import main.model.auth.AuthServiceDefault;
import main.model.login.dto.LoginRequest;
import main.model.login.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AuthServiceDefault authServiceDefault;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        boolean result = authServiceDefault.login(request.getAffiliationCode(), request.getPassword());
        if (result) {
            return new LoginResponse(true, "로그인 성공");
        } else {
            return new LoginResponse(false, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

}
