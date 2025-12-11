FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY ProyectoBIB/pom.xml ./
COPY ProyectoBIB/src ./src
COPY ProyectoBIB/.mvn ./.mvn
COPY ProyectoBIB/mvnw ./
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
ENV PORT=8080
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
