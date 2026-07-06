/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *Simple controller used to verify that the service is running.
 * @author princ
 */
@Path("/ping")
public class PingController {
    @GET
    public Response ping() {
        return Response.ok("pong").build();
    }
    
}
