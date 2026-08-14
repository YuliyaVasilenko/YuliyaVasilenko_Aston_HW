FROM eclipse-temurin:21-jdk
VOLUME /tmp
COPY target/api-gateway-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]