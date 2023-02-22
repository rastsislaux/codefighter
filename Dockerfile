FROM gradle:7-alpine as BUILDER
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test --stacktrace

FROM amazoncorretto:19
WORKDIR /app
ARG JAR_FILE=/app/build/libs/CodeFighter-0.0.1-SNAPSHOT.jar
RUN yum install -y python3
COPY --from=builder ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
