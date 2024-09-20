# OpenJDK JRE
FROM openjdk:21-jdk-slim

WORKDIR /opt/app

ARG JAR_FILE=ms-core-user-v1-1.0.0-SNAPSHOT.jar

EXPOSE 9082

# Copy JAR file
COPY ${JAR_FILE} ms-core-user-v1-1.0.0.jar

# Run the app
ENTRYPOINT ["java", "-jar", "ms-core-user-v1-1.0.0.jar", "-web", "-webAllowOthers", "-tcp", "-tcpAllowOthers", "-browser"]
