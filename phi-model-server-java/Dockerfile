# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build
COPY pom.xml .
# Download dependencies first (cached layer)
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

RUN apt-get update && apt-get install -y \
    libgomp1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar
COPY models/ /app/models/

ENV MODEL_PATH=/app/models/phi-3-mini-4k-instruct-q4.gguf
ENV N_CTX=2048
ENV N_THREADS=4
ENV JAVA_OPTS="-Xmx3g -Xms1g"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]