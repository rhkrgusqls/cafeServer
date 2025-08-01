package main.controller;


import main.exception.SignupException;
import main.model.auth.AuthServiceDefault;
import main.model.db.dto.signup.SignUpRequest;
import main.model.db.dto.signup.SignUpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//종결

@RestController
@RequestMapping("/register")
public class SignUpController {

    @Autowired
    private AuthServiceDefault authServiceDefault;

    @PostMapping("/signup")
    public SignUpResponse signup(@RequestBody SignUpRequest request) {
        try {
            return new SignUpResponse(
                            authServiceDefault.signup(
                                        request.getAffiliationCode(),
                                        request.getPassword(),
                                        request.getStoreName())
                            ,"회원가입 성공");
        } catch (SignupException e) {
            //회원가입시 발생할 수 있는 회원가입만의 에러를 반송 (보안상 다른 모든 익셉션을 전송하지는 않음 (로그인 로직에서 SignupException 에러를 발생시킴))
            return new SignUpResponse(false, e.getMessage());
        } catch (Exception e) {
            //예상할 수 없는 에러는 숨기고 반송
            return new SignUpResponse(false, "예기치 못한 오류가 발생했습니다.");
        }
    }
}
