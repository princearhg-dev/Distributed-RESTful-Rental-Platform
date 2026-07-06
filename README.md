# Distributed-RESTful-Rental-Platform
A Java-based REST orchestrator for a rental marketplace, built with a microservices-style architecture, secured API endpoints, cloud data integration, and load-tested for performance under scale. Built as a third-year project.


## Features
- **REST orchestrator** — central service exposing GET/POST/DELETE endpoints for item search, rental requests, and management, communicating with underlying services via JSON
- **Secure API authentication** — endpoints protected by an API-key authentication filter, using constant-time string comparison to prevent timing attacks against the key
- **Cloud data integration** — connects to a cloud-hosted NoSQL database for persistent storage
- **External API integration** — integrates with the OSRM routing API for location-based proximity queries, with error handling for timeouts and failed requests
- **Performance tested** — load and scalability tested using JMeter to identify and resolve bottlenecks under high request volume

## Tech Stack
Java, REST, NoSQL (cloud-hosted), OSRM API, JMeter

## How it works
Client requests hit the central orchestrator, which authenticates each request via an API-key filter before routing it to the appropriate handler. Item and rental data is persisted in a cloud NoSQL database, while location-based queries (e.g. finding nearby available items) are resolved through calls to the OSRM routing API. The system was stress-tested with JMeter to simulate concurrent users, surfacing and fixing bottlenecks in the request-handling pipeline.
