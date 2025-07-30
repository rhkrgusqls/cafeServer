package main.model.db.dto.signup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    private String affiliationCode;
    private String password;
    private String storeName;
}
