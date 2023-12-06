FROM openjdk:17-oracle
WORKDIR /app
COPY /build/libs/*.jar pc-configurator-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","pc-configurator-0.0.1-SNAPSHOT.jar"]