/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author princ
 */
public class CosmosItem {
    
     // fields 
    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("owner_id")
    private String ownerId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private String category;

    // Human-readable location (city / postcode)
    @JsonProperty("location")
    private String location;

    //  precise coordinates (for distance calculations)
    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("daily_rate")
    private Double dailyRate;

    @JsonProperty("available")
    private Boolean available;

    @JsonProperty("condition")
    private String condition;

    @JsonProperty("description")
    private String description;

    // === Cosmos system fields ===
    @JsonProperty("id")
    private String id;

    @JsonProperty("_rid")
    private String rid;

    @JsonProperty("_self")
    private String self;

    @JsonProperty("_etag")
    private String etag;

    @JsonProperty("_attachments")
    private String attachments;

    @JsonProperty("_ts")
    private Long ts;

    public CosmosItem() {}

    // === Getters / setters ===

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getDailyRate() { return dailyRate; }
    public void setDailyRate(Double dailyRate) { this.dailyRate = dailyRate; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRid() { return rid; }
    public void setRid(String rid) { this.rid = rid; }

    public String getSelf() { return self; }
    public void setSelf(String self) { this.self = self; }

    public String getEtag() { return etag; }
    public void setEtag(String etag) { this.etag = etag; }

    public String getAttachments() { return attachments; }
    public void setAttachments(String attachments) { this.attachments = attachments; }

    public Long getTs() { return ts; }
    public void setTs(Long ts) { this.ts = ts; }
    
   
    
}
