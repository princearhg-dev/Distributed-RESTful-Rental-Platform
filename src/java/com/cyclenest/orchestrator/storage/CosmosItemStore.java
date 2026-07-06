/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.storage;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.cyclenest.orchestrator.model.CosmosItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Optional;

/**
 *
 * @author princ
 */
public class CosmosItemStore {
    
    private final CosmosContainer container;

    public CosmosItemStore() {
        CosmosClient client = CosmosClientProvider.getClient();

        String dbName = envOrProp("COSMOS_DB", "coursework");
        String containerName = envOrProp("COSMOS_ITEMS_CONTAINER", "items");

        CosmosDatabase db = client.getDatabase(dbName);
        this.container = db.getContainer(containerName);
    }

    /**
     * List all items.
     *
     * @return list of items from Cosmos
     */
    public List<CosmosItem> listAll() {
        String q = "SELECT * FROM c ORDER BY c.item_id";
        return query(new SqlQuerySpec(q));
    }

    /**
     * Search by o maxPrice and/or location.
     *
     * @param maxPrice max daily_rate filter 
     * @param location location exact match filter 
     * @return list of matching items
     */
    public List<CosmosItem> search(Double maxPrice, String location) {
        StringBuilder q = new StringBuilder("SELECT * FROM c WHERE 1=1");
        List<SqlParameter> params = new ArrayList<>();

        if (maxPrice != null) {
            q.append(" AND c.daily_rate <= @maxPrice");
            params.add(new SqlParameter("@maxPrice", maxPrice));
        }

        if (location != null && !location.isBlank()) {
            q.append(" AND c.location = @location");
            params.add(new SqlParameter("@location", location));
        }

        q.append(" ORDER BY c.item_id");

        SqlQuerySpec spec = params.isEmpty()
                ? new SqlQuerySpec(q.toString())
                : new SqlQuerySpec(q.toString(), params);

        return query(spec);
    }

    /**
     * Find a single item "item_id" (e.g. "i002").
     *
     * @param itemId item_id value
     * @return Optional item
     */
    public Optional<CosmosItem> findByItemId(String itemId) {
        if (itemId == null || itemId.isBlank()) return Optional.empty();

        SqlQuerySpec spec = new SqlQuerySpec(
                "SELECT * FROM c WHERE c.item_id = @itemId",
                List.of(new SqlParameter("@itemId", itemId))
        );

        Iterator<FeedResponse<CosmosItem>> it = container
                .queryItems(spec, new CosmosQueryRequestOptions(), CosmosItem.class)
                .iterableByPage()
                .iterator();

        if (!it.hasNext()) return Optional.empty();

        List<CosmosItem> page = it.next().getResults();
        return page.isEmpty() ? Optional.empty() : Optional.of(page.get(0));
    }

    // ---- internal query helper ----
    private List<CosmosItem> query(SqlQuerySpec spec) {
        try {
            List<CosmosItem> out = new ArrayList<>();

            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

            Iterable<FeedResponse<CosmosItem>> pages =
                    container.queryItems(spec, options, CosmosItem.class).iterableByPage();

            for (FeedResponse<CosmosItem> page : pages) {
                out.addAll(page.getResults());
            }
            return out;

        } catch (CosmosException e) {
            throw new RuntimeException(
                    "Cosmos query failed (status=" + e.getStatusCode() + "): " + e.getMessage(),
                    e
            );
        }
    }

    private static String envOrProp(String name, String fallback) {
        String v = System.getenv(name);
        if (v == null || v.isBlank()) v = System.getProperty(name);
        return (v == null || v.isBlank()) ? fallback : v;
    }
    
}
