/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.storage;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.SqlQuerySpec;
import com.cyclenest.orchestrator.model.CosmosRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *Cosmos DB store for requests container.
 * @author princ
 */
public class CosmosRequestStore {
    
    
    private final CosmosContainer container;

    public CosmosRequestStore() {
        CosmosClient client = CosmosClientProvider.getClient();
        String dbName = envOrProp("COSMOS_DB", "coursework");
        String containerName = envOrProp("COSMOS_REQUESTS_CONTAINER", "requests");

        CosmosDatabase db = client.getDatabase(dbName);
        this.container = db.getContainer(containerName);
    }

    public CosmosRequest create(CosmosRequest req) {
        // Must have id + request_id set (and match)
        if (req.getRequestId() == null || req.getRequestId().isBlank()) {
            throw new IllegalArgumentException("request_id is required");
        }
        req.setId(req.getRequestId());

        CosmosItemResponse<CosmosRequest> res =
                container.createItem(req, new PartitionKey(req.getRequestId()), null);

        return res.getItem();
    }

    public CosmosRequest update(CosmosRequest req) {
        if (req.getRequestId() == null || req.getRequestId().isBlank()) {
            throw new IllegalArgumentException("request_id is required");
        }
        req.setId(req.getRequestId());

        CosmosItemResponse<CosmosRequest> res =
                container.upsertItem(req, new PartitionKey(req.getRequestId()), null);

        return res.getItem();
    }

    public Optional<CosmosRequest> findById(String requestId) {
        if (requestId == null || requestId.isBlank()) return Optional.empty();

        try {
            CosmosItemResponse<CosmosRequest> res =
                    container.readItem(requestId, new PartitionKey(requestId), CosmosRequest.class);
            return Optional.ofNullable(res.getItem());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<CosmosRequest> listAll() {
        List<CosmosRequest> out = new ArrayList<>();
        SqlQuerySpec spec = new SqlQuerySpec("SELECT * FROM c ORDER BY c.created_at DESC");

        container.queryItems(spec, new CosmosQueryRequestOptions(), CosmosRequest.class)
                .iterableByPage()
                .forEach(page -> out.addAll(page.getResults()));

        return out;
    }

    private static String envOrProp(String name, String fallback) {
        String v = System.getenv(name);
        if (v == null || v.isBlank()) v = System.getProperty(name);
        return (v == null || v.isBlank()) ? fallback : v;
    }
    
    
}
