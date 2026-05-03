FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y \
    git \
    openssh-client \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /root/.ssh

COPY src/main/resources/ssh/config-server-key /root/.ssh/config-server-key

RUN chmod 600 /root/.ssh/config-server-key

RUN ssh-keyscan github.com >> /root/.ssh/known_hosts

RUN echo "Host github.com" > /root/.ssh/config && \
    echo "  IdentityFile /root/.ssh/config-server-key" >> /root/.ssh/config

VOLUME /tmp

COPY target/config-server-*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]