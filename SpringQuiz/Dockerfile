# Start with a base image
FROM openjdk:21-jdk

# Set the working directory
WORKDIR /app

# Copy the built JAR into the container
COPY target/SpringQuiz-0.0.1-SNAPSHOT.jar app.jar

# Set the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
