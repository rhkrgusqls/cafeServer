package main.model.login.dto;

public class LoginRequest {
    private String affiliationCode;
    private String password;

    public LoginRequest() {}

    public LoginRequest(String affiliationCode, String password) {
        this.affiliationCode = affiliationCode;
        this.password = password;
    }

    public String getAffiliationCode() {
        return affiliationCode;
    }

    public void setAffiliationCode(String affiliationCode) {
        this.affiliationCode = affiliationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
