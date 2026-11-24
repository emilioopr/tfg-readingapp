FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
# Las dependencias van en su propia capa: si solo cambia el código, Docker no las vuelve a descargar
RUN ./mvnw -q dependency:go-offline
COPY src src
# Sin -DskipTests: si falla algún test, falla la construcción y Render no despliega
RUN ./mvnw clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Hora española: si no, entre medianoche y la 1-2 h el servidor (en UTC) cree que aún es ayer (rachas, retos, calendario)
ENTRYPOINT ["java", "-Duser.timezone=Europe/Madrid", "-jar", "app.jar"]