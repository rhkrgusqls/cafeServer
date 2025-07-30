package main.controller;

import main.model.auth.AuthServiceDefault;
import main.model.login.dto.LoginRequest;
import main.model.login.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import main.exception.LoginException;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AuthServiceDefault authServiceDefault;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            authServiceDefault.login(request.getAffiliationCode(), request.getPassword());
            return new LoginResponse(true, "로그인 성공");
        } catch (LoginException e) {
            return new LoginResponse(false, e.getMessage());
        }
    }
}
