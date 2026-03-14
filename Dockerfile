# Start from a Java 17 base image
FROM eclipse-temurin:17-jdk-jammy

# Set work directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (to cache dependencies)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src src

# Build the Spring Boot app
RUN ./mvnw clean package -DskipTests

# Set the JAR name (replace with your actual jar name)
ARG JAR_FILE=target/desker-1.0-SNAPSHOT.jar

# Copy the jar into the container
COPY ${JAR_FILE} app.jar

# Expose the port your app runs on
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","/app/app.jar"]