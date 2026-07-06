/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

/**

 * @author princ
 */
public class CreateRequestPayload {
    
    @JsonProperty("itemId")
    private String itemId;

    @JsonProperty("requesterName")
    private String requesterName;

    public CreateRequestPayload() {}

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    
    
  
}
