package main.model.db.dto.affiliationList;

public class AffiliationListDTO {
    private String affiliationCode;
    private String storeName;

    public AffiliationListDTO(){}

    // Constructor
    public AffiliationListDTO(String affiliationCode, String storeName) {
        this.affiliationCode = affiliationCode;
        this.storeName = storeName;
    }

    // Getters and Setters
    public String getAffiliationCode() {
        return affiliationCode;
    }

    public void setAffiliationCode(String affiliationCode) {
        this.affiliationCode = affiliationCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
