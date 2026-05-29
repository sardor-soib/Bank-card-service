FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/bank-cards-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]