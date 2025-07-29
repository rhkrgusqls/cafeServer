package main.model.db.dto;

public class AffiliationDTO {
    private String affiliationCode;
    private String password;
    private String storeName;

    public String getAffiliationCode() { return affiliationCode; }
    public void setAffiliationCode(String affiliationCode) { this.affiliationCode = affiliationCode; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
}
