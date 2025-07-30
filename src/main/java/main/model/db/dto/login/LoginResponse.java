package main.model.db.dto.login;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    // Getter/Setter
    private boolean success;
    private String message;

    // 기본 생성자
    public LoginResponse() {}

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}
