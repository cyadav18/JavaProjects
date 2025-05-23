# Stage 1: Build the gateway JAR
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /gateway

# Copy only what's needed to build (optimize Docker layer caching)
# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

# Copy only the gateway-service code + pom from monorepo structure
COPY gateway-service/pom.xml ./gateway-service/
COPY gateway-service/src ./gateway-service/src

# Build only gateway-service module
RUN mvn -f gateway-service/pom.xml clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre
WORKDIR /gateway
COPY --from=build /build/gateway-service/target/gateway-service.jar gateway-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "gateway-service.jar", "--spring.profiles.active=dev"]


# Build the JAR without running tests
RUN mvn clean package -DskipTests

# Stage 2: Run the built JAR
FROM eclipse-temurin:17-jre
WORKDIR /gateway

# Copy the JAR from build stage
COPY --from=build /gateway/target/*.jar gateway-service.jar

# Expose the port the gateway listens on
EXPOSE 8080

# Run the Spring Boot gateway with active profile
ENTRYPOINT ["java", "-jar", "gateway-service.jar", "--spring.profiles.active=dev"]
