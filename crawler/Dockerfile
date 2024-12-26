FROM openjdk:17-jdk-slim AS build

RUN apt-get update && apt-get install -y maven

COPY pom.xml /app/pom.xml
WORKDIR /app

RUN mvn dependency:go-offline

COPY src /app/src

RUN mvn clean install

FROM openjdk:17-jdk-slim

COPY --from=build /app/target/crawlerEY-0.0.1-SNAPSHOT.jar /app/app.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
