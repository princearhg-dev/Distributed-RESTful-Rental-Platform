/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.controller;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

/**
 *This class initializes and configures the Jersey REST framework by:
 * - Scanning the specified package for REST controllers 
 * @author princ
 */
public class JerseyConfig extends ResourceConfig {
    
    public JerseyConfig() {
        // Scan controllers
        packages("com.cyclenest.orchestrator.controller");

        // Enable Jackson JSON processing
        register(JacksonFeature.class);
        
        
         // Security
        register(com.cyclenest.orchestrator.security.ApikeyAuthFilter.class);
    }
    
}
