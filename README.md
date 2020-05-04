# Catssandra (Async Cassandra Client for Cats)

In this project an asynchronous client for cassandra friendly with scala cats is created.

Requirements:
   * JDK >= 1.8
   * Scala 2.13.x
   * Docker
   * Docker Compose
   
1. Run Cassandra.
    `docker-compose up -d`
    
2. Init cql file `init.cql`:
    `docker exec -it ${CASSANDRA_CONTAINER_ID} cqlsh`
    
3. Run App: `com.leysoft.catssandra.App`
    