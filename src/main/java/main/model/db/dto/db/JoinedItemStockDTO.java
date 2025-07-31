package main.model.db.dto.db;

import java.sql.Timestamp;

public class JoinedItemStockDTO {
    private int stockId;
    private int itemId;
    private String itemName;       // from item table
    private String itemCategory;   // from item table
    private int quantity;
    private Timestamp expireDate;
    private Timestamp receivedDate;
    private String status;
    private int affiliationCode;

    public int getStockId() { return stockId; }
    public void setStockId(int stockId) { this.stockId = stockId; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemCategory() { return itemCategory; }
    public void setItemCategory(String itemCategory) { this.itemCategory = itemCategory; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Timestamp getExpireDate() { return expireDate; }
    public void setExpireDate(Timestamp expireDate) { this.expireDate = expireDate; }

    public Timestamp getReceivedDate() { return receivedDate; }
    public void setReceivedDate(Timestamp receivedDate) { this.receivedDate = receivedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getAffiliationCode() { return affiliationCode; }
    public void setAffiliationCode(int affiliationCode) { this.affiliationCode = affiliationCode; }
}