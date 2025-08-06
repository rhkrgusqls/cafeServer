// ShipmentDTO.java
package main.log.db.dto;

import java.util.Date;

public class ShipmentDTO {
    public int itemId;
    public int quantity;
    public String targetAffiliationCode;
    public Date shipmentTime;

    public ShipmentDTO(int itemId, int quantity, String targetAffiliationCode, Date shipmentTime) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.targetAffiliationCode = targetAffiliationCode;
        this.shipmentTime = shipmentTime;
    }
}
