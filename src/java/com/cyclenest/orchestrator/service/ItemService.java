/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.service;

import com.cyclenest.orchestrator.model.CosmosItem;
import com.cyclenest.orchestrator.storage.CosmosItemStore;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
/**
 *Returns CosmosItem so the API matches the db schema
 * @author princ
 */
public class ItemService {
    
    private final CosmosItemStore store = new CosmosItemStore();

    /**
     * Search for available items and optionally filter by:
     * - maxPrice 
     * - location (contains, case-insensitive)
     *
     * @param maxPrice optional maximum daily rate
     * @param location optional location filter
     * @return list of matching items (available only)
     */
    public List<CosmosItem> search(Double maxPrice, String location) {
        // Pull from Cosmos with basic filters
        List<CosmosItem> results = store.search(maxPrice, location);

       
        return results.stream()
                .filter(i -> i != null && Boolean.TRUE.equals(i.getAvailable()))
                .filter(i -> location == null || location.isBlank()
                        || (i.getLocation() != null
                        && i.getLocation().toLowerCase(Locale.ROOT)
                                .contains(location.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
    }

    /**
     * Get a single item using the  item_id .
     *
     * @param itemId item_id
     * @return matching CosmosItem
     * @throws IllegalArgumentException if item not found
     */
    public CosmosItem getByItemIdOrThrow(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId is required");
        }

        return store.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
    }
    
    
}
