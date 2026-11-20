# -------------------------------------------------------------
# Stage 1: Build Vue 3 Frontend
# -------------------------------------------------------------
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# -------------------------------------------------------------
# Stage 2: Build Spring Boot Backend
# -------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app/backend
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B
COPY backend/src ./src
# Đưa các file tĩnh Vue đã build vào Spring Boot
COPY --from=frontend-builder /app/backend/src/main/resources/static ./src/main/resources/static
RUN mvn clean package -DskipTests

# -------------------------------------------------------------
# Stage 3: Lightweight Runtime
# -------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend-builder /app/backend/target/*.jar app.jar
COPY data/ ./data/
EXPOSE 8888
ENV PORT=8888
ENTRYPOINT ["java", "-jar", "app.jar"]
