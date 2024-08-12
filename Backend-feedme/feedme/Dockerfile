# Use an official OpenJDK runtime with Alpine Linux
FROM openjdk:17-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle Wrapper and source code
COPY gradlew /app/gradlew
COPY gradle /app/gradle
COPY src /app/src
COPY build.gradle /app/build.gradle
COPY settings.gradle /app/settings.gradle 

# Fix potential carriage return issues
RUN sed -i 's/\r$//' ./gradlew

# Grant execution permission for the Gradle Wrapper
RUN chmod +x ./gradlew

# Build the application and skip tests
RUN ./gradlew clean build -x test

# Copy the built JAR file to the app directory
# COPY build/libs/feedme-0.0.1-SNAPSHOT.jar /app/MailService.jar

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/build/libs/feedme-0.0.1-SNAPSHOT.jar"]

# Expose port 8080
EXPOSE 8085
