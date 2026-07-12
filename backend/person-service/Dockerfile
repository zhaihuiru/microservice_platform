FROM eclipse-temurin:17-jre
WORKDIR /app

RUN mkdir -p /app/uploads/persons

COPY person-service/target/person-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9004
ENTRYPOINT ["java", "-jar", "app.jar"]