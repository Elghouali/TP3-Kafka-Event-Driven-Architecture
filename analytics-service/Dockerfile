FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY . .
RUN mvn -B -pl analytics-service -am -DskipTests package
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/analytics-service/target/analytics-service-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
