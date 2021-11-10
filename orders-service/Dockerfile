FROM adoptopenjdk:11-jre-hotspot
#create folder
RUN mkdir -p /app/
WORKDIR /app
#run maven build
COPY . .
RUN if [ -f orders-service/target/orders-service-0.0.1-SNAPSHOT.jar ] ; then echo "Jar already exists. Skipping build" ; else  ./mvnw clean package -DskipTests ; fi
RUN cp orders-service/target/orders-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
