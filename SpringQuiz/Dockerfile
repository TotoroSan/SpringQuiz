# Start with a base image
FROM openjdk:21-jdk

# Set the working directory
WORKDIR /app

# Copy the built JAR into the container
COPY target/SpringQuiz-0.0.1-SNAPSHOT.jar app.jar


# TODO For running on heroku: Port=8080 Profile=production  /  For local usage: Port=8443 Profile=docker
# Allow the port and profile to be dynamically set
ARG PORT=8443
ARG PROFILE=docker

# Expose the dynamic port
EXPOSE ${PORT}
# Set the active Spring profile dynamically at runtime
ENV SPRING_PROFILES_ACTIVE=${PROFILE}


# Set the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
