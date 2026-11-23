FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY . .
RUN mvn -B -pl consumer-service -am -DskipTests package
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/consumer-service/target/consumer-service-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
