/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.controller;

import com.cyclenest.orchestrator.dto.CreateRequestPayload;
import com.cyclenest.orchestrator.dto.ErrorResponse;
import com.cyclenest.orchestrator.model.CosmosRequest;
import com.cyclenest.orchestrator.service.RequestService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


/**
 *REST controller responsible for rental request operations.
 *
 * Provides endpoints to:
 * - create a rental request (default status: PENDING)
 * - cancel a rental request (status becomes: CANCELLED)

 * @author princ
 */
@Path("/requests")
public class RequestController {
    
       private final RequestService service = new RequestService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRequest(CreateRequestPayload payload) {
        try {
            CosmosRequest created = service.create(payload);
            return Response.status(Response.Status.CREATED)
                    .entity(created)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .header("Access-Control-Allow-Origin", "*")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to create request"))
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
    }

    @PUT
    @Path("/{id}/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelRequest(@PathParam("id") String id) {
        try {
            CosmosRequest updated = service.cancel(id);
            return Response.ok(updated)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();

        } catch (IllegalArgumentException e) {
            String msg = (e.getMessage() == null) ? "" : e.getMessage().toLowerCase();
            Response.Status status = msg.contains("not found") ? Response.Status.NOT_FOUND : Response.Status.BAD_REQUEST;

            return Response.status(status)
                    .entity(new ErrorResponse(e.getMessage()))
                    .header("Access-Control-Allow-Origin", "*")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to cancel request"))
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listRequests() {
        try {
            List<CosmosRequest> list = service.listAll();
            return Response.ok(list)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to list requests"))
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRequest(@PathParam("id") String id) {
        try {
            return service.findById(id)
                    .map(req -> Response.ok(req)
                            .header("Access-Control-Allow-Origin", "*")
                            .build())
                    .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                            .entity(new ErrorResponse("Request not found: " + id))
                            .header("Access-Control-Allow-Origin", "*")
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Failed to fetch request"))
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
    }
    
}
