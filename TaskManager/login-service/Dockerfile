# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
LABEL authors="cyadav"
WORKDIR /login-service-build

COPY login-service/pom.xml ./login-service/
COPY login-service/src ./login-service/src

RUN mvn clean package -pl login-service -am -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre
WORKDIR /login

COPY --from=build /login-service-build/login-service/target/login-service.jar login-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "login-service.jar", "--spring.profiles.active=dev"]
