/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 *Cosmos DB document model for rental requests
 * Fields stored in Cosmos:
 *  - id (Cosmos internal id)
 *  - request_id (partition key + logical id)
 *  - item_id
 *  - user_id
 *  - status: "pending" or "cancelled"
 * @author princ
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CosmosRequest {
    
    
    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("user_id")
    private String userId;

    private String status;

    @JsonProperty("created_at")
    private Instant createdAt;

    // Cosmos requires an "id" field too.
    private String id;

    public CosmosRequest() {}

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
        this.id = requestId; // keep id in sync
    }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    
}
