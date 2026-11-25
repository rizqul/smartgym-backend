FROM maven:3.10.0-eclipse-temurin-25-alpine AS builder

WORKDIR /app
COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests dependency:go-offline

COPY /src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests clean package

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar smartgym.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/smartgym.jar"]