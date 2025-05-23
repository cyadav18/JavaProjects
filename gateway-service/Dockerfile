# Stage 1: Build the gateway JAR
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /gateway-service-build

# Copy gateway-service module (pom + source code)
COPY . .

# Build only gateway-service module
RUN mvn -f gateway-service/pom.xml clean package -DskipTests && ls -l gateway-service/target

# Stage 2: Run the built JAR
FROM eclipse-temurin:17-jre
WORKDIR /gateway

# Corrected path to copy the built JAR
COPY --from=build /gateway-service-build/gateway-service/target/*.jar gateway-service.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "gateway-service.jar", "--spring.profiles.active=dev"]
