package auth;

public class TestDebugAuth implements AuthService {
    // 로그인 확인용 메서드
    @Override
    public boolean authenticate(){
        return true;
    }

    // 회원가입용 메서드
    @Override
    public boolean signup(String userId , String password){
        return true;
    }
}