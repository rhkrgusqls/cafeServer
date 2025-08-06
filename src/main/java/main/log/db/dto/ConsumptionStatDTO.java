// ConsumptionStatDTO.java
package main.log.db.dto;

public class ConsumptionStatDTO {
    public int itemId;
    public String period;
    public int totalQuantity;

    public ConsumptionStatDTO(int itemId, String period, int totalQuantity) {
        this.itemId = itemId;
        this.period = period;
        this.totalQuantity = totalQuantity;
    }
}
