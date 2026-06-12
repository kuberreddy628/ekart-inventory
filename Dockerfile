FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/inventory-0.0.1-SNAPSHOT.jar inventory.jar

ENTRYPOINT ["java", "-jar", "inventory.jar"]



