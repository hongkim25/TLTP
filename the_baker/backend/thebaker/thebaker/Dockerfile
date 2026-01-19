# STAGE 1: BUILD
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# STAGE 2: RUN
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Note: This finds any jar ending in SNAPSHOT.jar in the libs folder
COPY --from=build /app/build/libs/*SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]