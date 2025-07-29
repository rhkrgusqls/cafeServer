package main.model.auth;

import org.springframework.stereotype.Service;

@Service
public class TestDebugAuth implements AuthService {
    // 로그인용 메서드
    @Override
    public boolean login(String username, String password) {
        return true;
    }

    // 로그인 확인용 메서드
    @Override
    public boolean authenticate(){
        return true;
    }

    // 회원가입용 메서드
    @Override
    public boolean signup(String userId , String password, String affiliationCode){
        return true;
    }
}