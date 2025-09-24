# Build stage: use JDK 21 and the project wrapper for consistent Gradle version
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy only files needed for dependency download first (better layer caching)
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew --version

# Then project sources/resources (includes src/main/resources/application.yml)
COPY src ./src

# Build only the bootJar (tests can run in CI separately) - adjust if you need tests here
RUN ./gradlew clean bootJar -x test

# Runtime stage (JRE 21)
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
