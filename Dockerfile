# Usar una imagen oficial de OpenJDK runtime como imagen base
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -B

# Copiar el resto del código fuente de la aplicación
COPY src ./src

RUN ./mvnw package -DskipTests

# Hacer que el puerto 8080 esté disponible para el mundo exterior a este contenedor
EXPOSE 8080

# Ejecutar el archivo JAR
ENTRYPOINT ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]