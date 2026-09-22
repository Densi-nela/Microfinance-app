# Stage 1: Build the application using Maven and JDK 11
FROM maven:3.8.5-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application using Eclipse Temurin JRE 11 (Maintained & lightweight)
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /app/target/shieldflow-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
