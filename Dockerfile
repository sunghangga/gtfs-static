FROM openjdk:8-jdk-alpine
COPY target/gtfs-0.0.1-SNAPSHOT.jar gtfs-static.jar
ENTRYPOINT ["java","-Xmx3G","-jar","gtfs-static.jar"]