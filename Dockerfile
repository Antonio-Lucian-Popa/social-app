FROM openjdk:17-jdk-slim
MAINTAINER antonioLucian
COPY target/user-api-0.0.1-SNAPSHOT.jar user-api-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/user-api-0.0.1-SNAPSHOT.jar"]
