# ============================
# STEP 1 — BUILD THE APP
# ============================
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests


# ============================
# STEP 2 — RUN THE APP
# ============================
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# ❌ DO NOT HARD-CODE PORT
# ENV PORT=8080  ← REMOVE THIS

# Render will set PORT automatically
EXPOSE 8080

# ✅ Bind Spring Boot to Render's dynamic PORT
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
