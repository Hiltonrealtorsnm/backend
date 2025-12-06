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

ENV PORT=8080
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
