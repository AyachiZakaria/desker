# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /build

# Copy Maven wrapper and pom first to leverage caching
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies (offline)
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the app without tests
RUN ./mvnw clean package -DskipTests

# Stage 2: Create minimal image
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /build/target/desker-1.0-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java","-jar","app.jar"]