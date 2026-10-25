package com.campusconnect.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class SkillCreditTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        INITIAL_GRANT,
        SESSION_BOOKING,
        SESSION_COMPLETION,
        SESSION_REFUND,
        RESOURCE_UPLOAD,
        ACCEPTED_ANSWER,
        ADMIN_ADJUSTMENT
    }

    private int id;
    private int userId;
    private String userName;
    private int amount;
    private Type transactionType;
    private String reason;
    private String relatedEntityType;
    private Integer relatedEntityId;
    private Timestamp createdAt;

    public SkillCreditTransaction() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public Type getTransactionType() { return transactionType; }
    public void setTransactionType(Type transactionType) { this.transactionType = transactionType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }

    public Integer getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(Integer relatedEntityId) { this.relatedEntityId = relatedEntityId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}