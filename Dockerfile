# ============================
# STEP 1 — BUILD THE APP
# ============================
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copy all backend code
COPY . .

# Build the jar (skip tests for faster build)
RUN mvn clean package -DskipTests


# ============================
# STEP 2 — RUN THE APP
# ============================
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Render sets PORT automatically
ENV PORT=8080
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "app.jar"]
