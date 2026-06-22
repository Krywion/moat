# --- etap budowania ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
# openapi.yaml jest źródłem prawdy DTO — generator potrzebuje go w buildzie
COPY openapi.yaml .
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# --- etap uruchomieniowy ---
FROM eclipse-temurin:21-jre
WORKDIR /app
# curl potrzebny do health-checka kontenera w docker-compose
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
