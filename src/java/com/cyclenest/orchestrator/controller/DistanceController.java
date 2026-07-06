package com.cyclenest.orchestrator.controller;

import com.cyclenest.orchestrator.service.DistanceService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *This class defines a RESTful web service endpoint for calculating the distance and travel duration between two sets of coordinates (user and item) user and the item.
 * 
 * @author princ
 */
@Path("/distance")
public class DistanceController {
    
    private final DistanceService distanceService = new DistanceService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDistance(
            @QueryParam("startLat") Double startLat,
            @QueryParam("startLon") Double startLon,

            // Mode A
            @QueryParam("endLat") Double endLat,
            @QueryParam("endLon") Double endLon,

            // Mode B (Cosmos)
            @QueryParam("itemId") String itemId
    ) {
        try {
            // Validate start coords (required in BOTH modes)
            if (startLat == null || startLon == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"startLat and startLon are required\"}")
                        .header("Access-Control-Allow-Origin", "*")
                        .build();
            }

            String jsonResult;

            // --- Mode B: Cosmos itemId provided ---
            if (itemId != null && !itemId.isBlank()) {
                jsonResult = distanceService.getDistanceToCosmosItem(startLat, startLon, itemId.trim());
                return Response.ok(jsonResult)
                        .header("Access-Control-Allow-Origin", "*")
                        .build();
            }

            // --- Mode A: end coords provided ---
            if (endLat == null || endLon == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Provide either (endLat,endLon) OR itemId\"}")
                        .header("Access-Control-Allow-Origin", "*")
                        .build();
            }

            jsonResult = distanceService.getDistanceFromOSRM(startLat, startLon, endLat, endLon);

            return Response.ok(jsonResult)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();

        } catch (IllegalArgumentException e) {
            // Bad input (e.g., item not found in Cosmos)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();

        } catch (RuntimeException e) {
            System.err.println("Error fetching distance: " + e.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
    }

    // Minimal JSON string escaping for error messages
    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }  
}
