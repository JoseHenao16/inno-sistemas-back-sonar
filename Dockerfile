# Etapa 1: Build con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final con Java
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copiar JAR generado
COPY --from=build /app/target/*.jar app.jar

# Copiar script de inicio
COPY entrypoint.sh .

# Dar permisos de ejecución
RUN chmod +x entrypoint.sh

# Exponer puerto
EXPOSE 8080

# Usar entrypoint
ENTRYPOINT ["./entrypoint.sh"]
