/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.service;

import com.cyclenest.orchestrator.dto.CreateRequestPayload;
import com.cyclenest.orchestrator.dto.RequestEvent;
import com.cyclenest.orchestrator.model.CosmosItem;
import com.cyclenest.orchestrator.model.CosmosRequest;
import com.cyclenest.orchestrator.mq.RabbitMqPublisher;
import com.cyclenest.orchestrator.storage.CosmosItemStore;
import com.cyclenest.orchestrator.storage.CosmosRequestStore;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 *Business logic for creating and cancelling requests using Cosmos DB.
 * @author princ
 */
public class RequestService {
    
    
    private final CosmosItemStore itemStore = new CosmosItemStore();
    private final CosmosRequestStore requestStore = new CosmosRequestStore();

    private final RabbitMqPublisher publisher = new RabbitMqPublisher("localhost", 5672);

    public CosmosRequest create(CreateRequestPayload payload) {
        if (payload == null) throw new IllegalArgumentException("Request body is required");

        String itemId = payload.getItemId();
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId is required (example: i002)");
        }

        String requesterName = payload.getRequesterName();
        if (requesterName == null || requesterName.isBlank()) {
            throw new IllegalArgumentException("requesterName is required");
        }

        // Validate item exists in Cosmos
        CosmosItem item = itemStore.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        // Validate availability
        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item is not available: " + itemId);
        }

        // Create request doc
        CosmosRequest req = new CosmosRequest();
        String requestId = "r-" + UUID.randomUUID(); // unique string id
        req.setRequestId(requestId);
        req.setItemId(itemId);
        req.setUserId(requesterName);
        req.setStatus("PENDING");
        req.setCreatedAt(Instant.now());

        CosmosRequest created = requestStore.create(req);

        // RabbitMQ event
        publisher.publish(new RequestEvent(
                "REQUEST_CREATED",
                created.getRequestId(),
                created.getItemId(),
                created.getUserId(),
                created.getStatus(),
                Instant.now()
        ));

        return created;
    }

    public CosmosRequest cancel(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("requestId is required");
        }

        CosmosRequest req = requestStore.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));

        if ("CANCELLED".equalsIgnoreCase(req.getStatus())) {
            throw new IllegalArgumentException("Request is already cancelled: " + requestId);
        }

        req.setStatus("CANCELLED");
        CosmosRequest updated = requestStore.update(req);

        publisher.publish(new RequestEvent(
                "REQUEST_CANCELLED",
                updated.getRequestId(),
                updated.getItemId(),
                updated.getUserId(),
                updated.getStatus(),
                Instant.now()
        ));

        return updated;
    }

    public List<CosmosRequest> listAll() {
        return requestStore.listAll();
    }

    public Optional<CosmosRequest> findById(String requestId) {
        return requestStore.findById(requestId);
    }
    
    
}
