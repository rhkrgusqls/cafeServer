package main.model.auth;

import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    // 로그인용 메서드
    public boolean login(String username, String password);

    // 로그인 확인용 메서드
    public boolean authenticate();

    // 회원가입용 메서드
    public boolean signup(String affiliationCode , String password);
}