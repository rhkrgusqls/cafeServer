package main.model.db.dto.db;

public class OrderRejectionHistoryDTO {
    private int rejectionId;
    private int orderId;
    private String rejectionReason;
    private String rejectionTime; // 또는 java.time.LocalDateTime
    private String notes;

    // Getters and Setters
    public int getRejectionId() { return rejectionId; }
    public void setRejectionId(int rejectionId) { this.rejectionId = rejectionId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getRejectionTime() { return rejectionTime; }
    public void setRejectionTime(String rejectionTime) { this.rejectionTime = rejectionTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
