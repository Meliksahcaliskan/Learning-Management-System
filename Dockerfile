# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the build output (JAR file) from the host machine to the container
# This assumes you are using Gradle or Maven and the JAR is located in `build/libs/`
COPY build/libs/*.jar app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
