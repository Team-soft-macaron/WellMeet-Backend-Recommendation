FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY build/libs/WellMeet-Recommendation-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=local"]
