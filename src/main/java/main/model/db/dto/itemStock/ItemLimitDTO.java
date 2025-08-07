package main.model.db.dto.itemStock;

public class ItemLimitDTO {
    private int itemId;
    private String affiliationCode;
    private int realQuantity;  // 실제 수량 필드 추가
    private int quantity;
    private boolean withinLimit;  // 필드명 수정

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getAffiliationCode() { return affiliationCode; }
    public void setAffiliationCode(String affiliationCode) { this.affiliationCode = affiliationCode; }

    public int getRealQuantity() {  // realQuantity getter
        return realQuantity;
    }
    public void setRealQuantity(int realQuantity) {  // realQuantity setter
        this.realQuantity = realQuantity;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isWithinLimit() {  // getter는 is~
        return withinLimit;
    }
    public void setWithinLimit(boolean withinLimit) {
        this.withinLimit = withinLimit;
    }
}
