/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.storage;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.ConsistencyLevel;

/**
 * * Creates and reuses a single CosmosClient and provides access to the database.
 * @author princ
 */
public class CosmosClientProvider {
    
    
    private static final String COSMOS_ENDPOINT = "https://myfreedb9.documents.azure.com:443/";
    private static final String COSMOS_KEY = "AVwbE7ipdbw7TcvlWIru8D87eP57tSWPGsTnBpupgZrW13VmUGsfJ6LfuJ5ioxxQ2eypYWR6sHF9ACDb08jXsg==";

    
    private static final String DATABASE_NAME = "coursework";

    private static CosmosClient client;

    private CosmosClientProvider() {}

    public static synchronized CosmosClient getClient() {
        if (client == null) {
            client = new CosmosClientBuilder()
                    .endpoint(COSMOS_ENDPOINT)
                    .key(COSMOS_KEY)
                    .consistencyLevel(ConsistencyLevel.SESSION)
                    .contentResponseOnWriteEnabled(true)
                    .buildClient();
        }
        return client;
    }

    public static CosmosDatabase getDatabase() {
        return getClient().getDatabase(DATABASE_NAME);
    }
    
    
}
