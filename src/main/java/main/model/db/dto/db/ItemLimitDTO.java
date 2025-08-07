package main.model.db.dto.db;

public class ItemLimitDTO {
    private int itemId;
    private String affiliationCode;
    private int quantity;

    // Getter / Setter
    public int getItemId() {
        return itemId;
    }
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getAffiliationCode() {
        return affiliationCode;
    }
    public void setAffiliationCode(String affiliationCode) {
        this.affiliationCode = affiliationCode;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
