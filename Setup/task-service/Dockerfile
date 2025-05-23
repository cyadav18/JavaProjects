FROM ubuntu:latest
LABEL authors="cyadav"
# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
LABEL authors="cyadav"
WORKDIR /task-service-build

COPY . .

# Build only the task-service module and its dependencies
RUN mvn clean package  -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre
WORKDIR /task

# Copy the JAR from the build stage
COPY --from=build /task-service-build/task-service/target/task-service.jar task-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "task-service.jar", "--spring.profiles.active=dev"]
