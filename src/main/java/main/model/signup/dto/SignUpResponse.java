package main.model.signup.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpResponse {
    // Getter/Setter
    private boolean success;
    private String message;

    // 기본 생성자
    public SignUpResponse() {}

    public SignUpResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}
