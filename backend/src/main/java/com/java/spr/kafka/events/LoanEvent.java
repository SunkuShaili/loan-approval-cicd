package com.java.spr.kafka.events;

import java.time.LocalDateTime;

public class LoanEvent {

    private String loanId;
    private String oldStatus;
    private String newStatus;
    private String actionBy;
    private LocalDateTime timestamp;

    public LoanEvent(){}

    public LoanEvent(String loanId, String oldStatus, String newStatus, String actionBy) {
        this.loanId = loanId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.actionBy = actionBy;
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getActionBy() {
        return actionBy;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    @Override
    public String toString() {
        return "LoanEvent{" +
                "loanId='" + loanId + '\'' +
                ", oldStatus='" + oldStatus + '\'' +
                ", newStatus='" + newStatus + '\'' +
                ", actionBy='" + actionBy + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
