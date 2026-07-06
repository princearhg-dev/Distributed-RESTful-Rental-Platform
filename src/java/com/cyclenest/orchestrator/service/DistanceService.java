/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.service;

import com.cyclenest.orchestrator.model.CosmosItem;
import com.cyclenest.orchestrator.storage.CosmosItemStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *This class contains the core logic for consuming the external OSRM API to calculate distance and duration between two geographic coordinates.
 * Uses Jackson POJOs for JSON.
 * @author princ
 */
public class DistanceService {
    
    private static final int TIMEOUT_MS = 5000;

    private final CosmosItemStore cosmosItemStore = new CosmosItemStore();

    /**
     * Calls the OSRM Route service to calculate distance & duration between two
     * coordinate points.
     *
     * @param startLat starting point latitude
     * @param startLon starting point longitude
     * @param endLat   destination latitude
     * @param endLon   destination longitude
     * @return JSON string containing distance (meters) and duration (seconds)
     */
    public String getDistanceFromOSRM(double startLat, double startLon,
                                      double endLat, double endLon) {

        String url = String.format(
                "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false",
                startLon, startLat, endLon, endLat // longitude first!
        );

        Client client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, TIMEOUT_MS);
        client.property(ClientProperties.READ_TIMEOUT, TIMEOUT_MS);

        try {
            Response response = client.target(url).request().get();

            String json = response.readEntity(String.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("OSRM service unavailable: HTTP " + response.getStatus());
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            OsrmRouteResponse osrmResponse = mapper.readValue(json, OsrmRouteResponse.class);

            if (osrmResponse.getRoutes() == null || osrmResponse.getRoutes().length == 0) {
                throw new RuntimeException("Invalid OSRM response: no routes found");
            }

            OsrmRouteResponse.Route firstRoute = osrmResponse.getRoutes()[0];
            double distance = firstRoute.getDistance();
            double duration = firstRoute.getDuration();

            DistanceResult result = new DistanceResult(distance, duration);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);

        } catch (ProcessingException e) {
            // Network-related problems, including timeouts.
            throw new RuntimeException("Error calling OSRM service (network/timeout)", e);

        } catch (JsonProcessingException e) {
            // Problems parsing JSON (malformed or unexpected format).
            throw new RuntimeException("Error parsing OSRM JSON response", e);
        }
    }

    /**
     * Part B (Cosmos linked):
     * User provides coordinates + itemId (Cosmos item_id e.g. "i002").
     * Fetch the item from Cosmos, resolve the item's coordinates, then call OSRM.
     *
     * @param startLat user's latitude
     * @param startLon user's longitude
     * @param itemId   Cosmos item_id (e.g., "i002")
     * @return JSON string containing distance (meters) and duration (seconds)
     */
    public String getDistanceToCosmosItem(double startLat, double startLon, String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId is required");
        }

        CosmosItem item = cosmosItemStore.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found in Cosmos: " + itemId));

        // If coordinates exist in Cosmos, use them (best + fastest)
        if (item.getLatitude() != null && item.getLongitude() != null) {
            return getDistanceFromOSRM(startLat, startLon, item.getLatitude(), item.getLongitude());
        }

        // Otherwise fall back to geocoding the "location" string (city/postcode)
        String loc = item.getLocation();
        if (loc == null || loc.isBlank()) {
            throw new IllegalArgumentException("Item has no location/coordinates: " + itemId);
        }

        GeoPoint itemPoint = geocodeLocation(loc);
        return getDistanceFromOSRM(startLat, startLon, itemPoint.lat, itemPoint.lon);
    }

    /**
     * Geocode a location string to lat/lon using OpenStreetMap Nominatim.
     * Works for city names (Leeds) and postcodes.
     */
    private GeoPoint geocodeLocation(String location) {
        String encoded = URLEncoder.encode(location, StandardCharsets.UTF_8);
        String url = "https://nominatim.openstreetmap.org/search?q=" + encoded + "&format=json&limit=1";

        Client client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, TIMEOUT_MS);
        client.property(ClientProperties.READ_TIMEOUT, TIMEOUT_MS);

        try {
            Response resp = client.target(url).request()
                    // Nominatim requires a User-Agent
                    .header("User-Agent", "CycleNest-Orchestrator/1.0 (student project)")
                    .get();

            String body = resp.readEntity(String.class);

            if (resp.getStatus() != 200) {
                throw new RuntimeException("Geocoding failed: HTTP " + resp.getStatus());
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            List<NominatimResult> results = mapper.readValue(
                    body,
                    mapper.getTypeFactory().constructCollectionType(List.class, NominatimResult.class)
            );

            if (results == null || results.isEmpty()) {
                throw new RuntimeException("Geocoding returned no results for: " + location);
            }

            double lat = Double.parseDouble(results.get(0).lat);
            double lon = Double.parseDouble(results.get(0).lon);
            return new GeoPoint(lat, lon);

        } catch (ProcessingException e) {
            throw new RuntimeException("Geocoding network/timeout error", e);
        } catch (Exception e) {
            throw new RuntimeException("Geocoding error: " + e.getMessage(), e);
        }
    }

    // ===================== Helper DTOs =====================

    /** Simple holder for coordinates */
    private static class GeoPoint {
        final double lat;
        final double lon;

        GeoPoint(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    /** Nominatim response entry  */
    private static class NominatimResult {
        public String lat;
        public String lon;
    }

    /**
     * POJO representing the small part of the OSRM Route API response.
     * Only the 'routes' array with 'distance' and 'duration' fields is mapped.
     */
    private static class OsrmRouteResponse {
        private Route[] routes;

        public Route[] getRoutes() {
            return routes;
        }

        public void setRoutes(Route[] routes) {
            this.routes = routes;
        }

        static class Route {
            private double distance;
            private double duration;

            public double getDistance() {
                return distance;
            }

            public void setDistance(double distance) {
                this.distance = distance;
            }

            public double getDuration() {
                return duration;
            }

            public void setDuration(double duration) {
                this.duration = duration;
            }
        }
    }

    /**
     * DTO used for the response that the orchestrator sends back to the client.
     * This is what gets serialized to JSON and shown in the browser.
     */
    private static class DistanceResult {
        private double distance_meters;
        private double duration_seconds;

        public DistanceResult(double distanceMeters, double durationSeconds) {
            this.distance_meters = distanceMeters;
            this.duration_seconds = durationSeconds;
        }

        public double getDistance_meters() {
            return distance_meters;
        }

        public void setDistance_meters(double distance_meters) {
            this.distance_meters = distance_meters;
        }

        public double getDuration_seconds() {
            return duration_seconds;
        }

        public void setDuration_seconds(double duration_seconds) {
            this.duration_seconds = duration_seconds;
        }
    }
    
     
    
     
}
