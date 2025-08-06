// ChangeLogDTO.java
package main.log.db.dto;

import java.util.Date;

public class ChangeLogDTO {
    public int itemId;
    public int quantity;
    public String changeType;
    public String affiliationCode;
    public Date changeTime;

    public ChangeLogDTO(int itemId, int quantity, String changeType, String affiliationCode, Date changeTime) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.changeType = changeType;
        this.affiliationCode = affiliationCode;
        this.changeTime = changeTime;
    }
}
