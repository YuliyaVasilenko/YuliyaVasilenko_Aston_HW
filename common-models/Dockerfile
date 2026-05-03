FROM eclipse-temurin:21-jdk
VOLUME /tmp
COPY target/common-models-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]