package auth;

public interface AuthService {

    // 로그인 확인용 메서드
    public boolean authenticate();

    // 회원가입용 메서드
    public boolean signup(String userId , String password);
}