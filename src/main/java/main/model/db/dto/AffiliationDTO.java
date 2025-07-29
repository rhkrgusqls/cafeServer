package main.model.db.dto;

public class AffiliationDTO {
    private int affiliationCode;
    private String storeName;

    public int getAffiliationCode() { return affiliationCode; }
    public void setAffiliationCode(int affiliationCode) { this.affiliationCode = affiliationCode; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
}
