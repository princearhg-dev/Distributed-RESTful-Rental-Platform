/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.dto;

/**
 *
 * @author princ
 */
public class ErrorResponse {
    private String error;

    public ErrorResponse() {}
    public ErrorResponse(String error) { this.error = error; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
}
