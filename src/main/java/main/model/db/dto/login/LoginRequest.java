package main.model.db.dto.login;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    private String affiliationCode;
    private String password;
}
