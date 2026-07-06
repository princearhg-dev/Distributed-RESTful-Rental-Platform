/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.security;


import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * * Simple API key auth for protecting request-related endpoints.
 * Requires header: X-API-Key
 * @author princ
 */

@Provider
@Priority(Priorities.AUTHENTICATION)
public class ApikeyAuthFilter implements ContainerRequestFilter {
    
    private static final String HEADER = "X-API-Key";
    private static final String ENV_NAME = "CYCLE_NEST_API_KEY";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Only protect /requests endpoints 
        String path = requestContext.getUriInfo().getPath(); // e.g. "requests", "requests/123/cancel"
        if (path == null || !path.startsWith("requests")) {
            return;
        }

        String expected = getExpectedKey();
        if (expected == null || expected.isBlank()) {
          
            abort(requestContext, "Server misconfigured: missing " + ENV_NAME);
            return;
        }

        String provided = requestContext.getHeaderString(HEADER);
        if (provided == null || provided.isBlank() || !constantTimeEquals(expected, provided)) {
            abort(requestContext, "Missing or invalid API key");
        }
    }

    private static String getExpectedKey() {
        String v = System.getenv(ENV_NAME);
        if (v == null || v.isBlank()) v = System.getProperty(ENV_NAME);
        return v;
    }

    private static void abort(ContainerRequestContext ctx, String msg) {
        ctx.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"" + escapeJson(msg) + "\"}")
                        .header("Access-Control-Allow-Origin", "*")
                        .build()
        );
    }

    
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
}
