package main.model.db.dto.db;

import java.sql.Timestamp;

public class OrderDTO {
    private int orderId;
    private int itemId;
    private int quantity;
    private String state;
    private Timestamp orderDate;

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    /** 대기상태 wait,
     * reviewing 검토중상태,
     * processed요청 처리됨상태(본점에서만 검토됨),
     * re-review-needed 재검토필요상태 ,
     * 종결상태 completed */
    public String getState() { return state; }

    /** 대기상태 wait,
     * reviewing 검토중상태,
     * processed요청 처리됨상태(본점에서만 검토됨),
     * re-review-needed 재검토필요상태 ,
     * 종결상태 completed */
    public void setState(String state) { this.state = state; }
}

