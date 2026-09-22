# Stage 1: Build the application using Maven and JDK 11
FROM maven:3.8.5-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first to cache this layer and speed up subsequent builds
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application using JRE 11 Slim
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/shieldflow-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
