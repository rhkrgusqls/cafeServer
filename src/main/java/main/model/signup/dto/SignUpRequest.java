package main.model.signup.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    private String affiliationCode;
    private String password;
    private String storeName;
}
