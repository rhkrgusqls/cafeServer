package main.model.db.dto.itemStockList;

public class ItemStockRequest {
    private int affiliationCode;
    private String state; // nullable

    public int getAffiliationCode() {
        return affiliationCode;
    }

    public void setAffiliationCode(int affiliationCode) {
        this.affiliationCode = affiliationCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
