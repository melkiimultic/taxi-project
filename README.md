## How to start
 - run command ./mvnw spring-boot:build-image in terminal to build jars and then images from them with Spring boot 
 - or you can run ./mvnw spring-boot:build-image -DskipTests to skip tests while building images
 - or you can run mvn spring-boot:build-image (-DskipTests) from the maven panel of IDEA
 - then run docker compose-up in terminal (docker should be installed and running)
 
## How to interact
Import taxi-driver.postman_collection.json file to postman from postman-collection package of the project