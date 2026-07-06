/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.dto;

import java.time.Instant;
/**
 
 * @author princ
 */
public class RequestEvent {
    
    
    private String type;
    private String requestId;
    private String itemId;
    private String userId;
    private String status;
    private Instant timestamp;

    public RequestEvent() {}

    public RequestEvent(String type, String requestId, String itemId, String userId, String status, Instant timestamp) {
        this.type = type;
        this.requestId = requestId;
        this.itemId = itemId;
        this.userId = userId;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    
    
}
