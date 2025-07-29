package main.log.db.dto;

import java.sql.Timestamp;

public class LogDTO {
    private int logId;
    private Timestamp logTimestamp;
    private String userId;
    private String action;
    private String details;
    private String ipAddress;
    private Integer affiliationCode;

    public int getLogId() {
        return logId;
    }
    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Timestamp getLogTimestamp() {
        return logTimestamp;
    }
    public void setLogTimestamp(Timestamp logTimestamp) {
        this.logTimestamp = logTimestamp;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getAffiliationCode() {
        return affiliationCode;
    }
    public void setAffiliationCode(Integer affiliationCode) {
        this.affiliationCode = affiliationCode;
    }
}
