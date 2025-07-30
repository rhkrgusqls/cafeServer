package main.controller;


import main.model.auth.AuthServiceDefault;
import main.model.login.dto.LoginRequest;
import main.model.login.dto.LoginResponse;
import main.model.signup.dto.SignUpRequest;
import main.model.signup.dto.SignUpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class SignUpController {

    @Autowired
    private AuthServiceDefault authServiceDefault;

    @PostMapping("/signup")
    public SignUpResponse signup(@RequestBody SignUpRequest request) {
        boolean result = authServiceDefault.signup(request.getAffiliationCode(), request.getPassword(), request.getStoreName());
        if (result) {
            return new SignUpResponse(true, "회원가입 성공");
        } else {
            return new SignUpResponse(false, "점포 번호가 중복이거나 유효한 값이 아닙니다.");
        }
    }
}
