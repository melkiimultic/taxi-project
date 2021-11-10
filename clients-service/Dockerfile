FROM adoptopenjdk:11-jre-hotspot
#create folder
RUN mkdir -p /app/
WORKDIR /app
#run maven build
COPY . .
RUN if [ -f clients-service/target/clients-service-0.0.1-SNAPSHOT.jar ] ; then echo "Jar already exists. Skipping build" ; else ./mvnw clean package -DskipTests ; fi
RUN cp clients-service/target/clients-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
