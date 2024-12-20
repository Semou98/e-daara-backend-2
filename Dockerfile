# Étape 1 : Construire l'application
FROM maven:3.9.5-eclipse-temurin-21 AS builder

# Définir le répertoire de travail
WORKDIR /app

# Fichiers nécessaires pour le build
COPY pom.xml ./
COPY src ./src

# Construire l'application (avec tests désactivés)
RUN mvn clean package -DskipTests

# Étape 2 : Image finale pour exécuter l'application
FROM openjdk:21-jdk-slim

# Utiliser une image de base Java 21
FROM openjdk:21-jdk-slim

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le fichier JAR de votre application
#COPY target/e-daara-backend-0.0.1-SNAPSHOT.jar app.jar
COPY --from=builder /app/target/e-daara-backend-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port sur lequel votre application s'exécute
EXPOSE 8080

# Diagnostic réseau
RUN apt-get update && apt-get install -y netcat-openbsd

# Commande pour exécuter l'application
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/app.jar"]

# Debbogage
#RUN ls -l /app/target