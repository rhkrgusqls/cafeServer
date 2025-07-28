package db.dto;

public class UserDTO {
    private String id;
    private String password;
    private String affiliationCode;  // camelCase 권장

    // getter
    public String getId() {
        return id;
    }

    // setter
    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getAffiliationCode() {
        return affiliationCode;
    }
    public void setAffiliationCode(String affiliationCode) {
        this.affiliationCode = affiliationCode;
    }
}
