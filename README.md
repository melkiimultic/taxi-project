## What is it?
Bunch of microservices creating taxi-service system. Feign clients are used for synchronous REST communications between microservices.
Kafka is used for asynchronous communications. Flyway is used for migrations. 
## Requirements
 - Java 11
## How to start
 - run command ./mvnw spring-boot:build-image in terminal to build jars and then images from them with Spring boot 
 - or you can run ./mvnw spring-boot:build-image -DskipTests to skip tests while building images
 - or you can run mvn spring-boot:build-image (-DskipTests) from the maven panel of IDEA
 - then run docker compose-up in terminal (docker should be installed and running)
## How to interact
Import taxi-driver.postman_collection.json file to postman from postman-collection package of the project
## One possible flow suggestion using Postman collection for REST API invocation
1. create client (client create)
2. create driver (driver create)
3. create order (client create order)
4. check unassigned orders (driver get unassigned orders) - should return created earlier order
5. check order's history (get orders history) - use id of created order as path variable
6. update order (driver update order) use id of test order and next status - ASSIGNED
7. update order (driver update order) use id of test order and wrong next status - CLOSED - you'll get exception
8. check unassigned orders (driver get unassigned orders) - should return empty list as order has been assigned to a driver
9. check order's history (get orders history) - use id of test order as path variable -should return order's history sorted by time of creation
10. Congrats! You're awesome!

