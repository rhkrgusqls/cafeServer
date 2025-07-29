package main.model.db.dto;

import java.sql.Timestamp;

public class OrderDTO {
    private int orderId;
    private int itemId;
    private String id;  // user id
    private Timestamp orderDate;

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }
}
