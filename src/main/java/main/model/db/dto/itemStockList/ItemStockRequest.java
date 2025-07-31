package main.model.db.dto.itemStockList;

public class ItemStockRequest {
    private String affiliationCode;
    private String state; // nullable

    public String getAffiliationCode() {
        return affiliationCode;
    }

    public void setAffiliationCode(String affiliationCode) {
        this.affiliationCode = affiliationCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
