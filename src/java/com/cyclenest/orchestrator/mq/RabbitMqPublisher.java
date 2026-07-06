/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyclenest.orchestrator.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

/**
 * RabbitMQ publisher for the Orchestrator.
 * Publishes JSON messages to a single queue.
 * @author princ
 */
public class RabbitMqPublisher {
    
   
    // Queue name used by the application
    public static final String QUEUE_NAME = "cyclenest.requests";

    // RabbitMQ connection settings 
    private final String host;
    private final int port;

    // Local ObjectMapper
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT);

    public RabbitMqPublisher(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Serialize any POJO to JSON and publish it to the queue.
     */
    public void publish(Object messagePojo) {
        try {
            String json = MAPPER.writeValueAsString(messagePojo);

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setConnectionTimeout(3000); // 3s connect timeout

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                // Declare the queue 
                channel.queueDeclare(
                        QUEUE_NAME,
                        true,   // durable
                        false,  // exclusive
                        false,  // autoDelete
                        null
                );

                channel.basicPublish(
                        "",              // default exchange
                        QUEUE_NAME,      // routing key = queue name
                        null,
                        json.getBytes(StandardCharsets.UTF_8)
                );
            }

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            System.err.println("RabbitMQ publish failed: JSON serialization error: " + e.getMessage());

        } catch (java.util.concurrent.TimeoutException e) {
            System.err.println("RabbitMQ publish failed: timeout connecting to broker: " + e.getMessage());

        } catch (java.io.IOException e) {
            System.err.println("RabbitMQ publish failed: IO error: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("RabbitMQ publish failed: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
