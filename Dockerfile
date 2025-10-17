# ---------- Build Stage (JDK 25) ----------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Gradle Wrapper & Build-Skripte zuerst (Cache)
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle build.gradle ./
RUN chmod +x gradlew && ./gradlew --version

# Quellcode kopieren
COPY src ./src

# Tests optional überspringen (Standard = true)
ARG SKIP_TESTS=true
RUN if [ "$SKIP_TESTS" = "true" ]; then \
      ./gradlew bootJar --no-daemon -x test ; \
    else \
      ./gradlew bootJar --no-daemon ; \
    fi

# ---------- Runtime Stage (JRE 25, klein) ----------
FROM eclipse-temurin:25-jre
WORKDIR /app
ENV PORT=8080
EXPOSE 8080

# Gebautes Boot-JAR übernehmen (Wildcard)
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Render gibt $PORT vor → an Spring weiterreichen
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar /app/app.jar"]
