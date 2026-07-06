/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.controller;

import com.cyclenest.orchestrator.dto.ErrorResponse;
import com.cyclenest.orchestrator.model.CosmosItem;
import com.cyclenest.orchestrator.service.ItemService;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 ** REST controller responsible for handling item-related requests.
 * 
 * Exposes endpoints that allow clients to search for available items
 * based on optional filters such as maximum price and location.
 * @author princ
 * 
 * 
 */
@Path("/items")
public class ItemController {
    
    private final ItemService service = new ItemService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchItems(
            @QueryParam("maxPrice") Double maxPrice,
            @QueryParam("location") String location
    ) {
        try {
            if (maxPrice != null && maxPrice < 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("maxPrice must be >= 0"))
                        .header("Access-Control-Allow-Origin", "*")
                        .build();
            }

            List<CosmosItem> results = service.search(maxPrice, location);

            return Response.ok(results)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();

        } catch (IllegalArgumentException e) {
            // Client error (bad input)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .header("Access-Control-Allow-Origin", "*")
                    .build();

        } catch (Exception e) {
            // Server error
            System.err.println("Failed to search items: " + e.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to search items"))
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
    }
    
    
}
