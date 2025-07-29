package main.model.db.dto;

import java.sql.Timestamp;

public class OrderDTO {
    private int orderId;
    private String id;
    private int itemId;
    private int quantity;
    private String state;
    private Timestamp orderDate;

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}

