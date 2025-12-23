# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

ENV SERVER_PORT=8081
ENV MONGODB_URI=mongodb://host.docker.internal:27017/reviewdb
ENV MONGODB_DATABASE=reviewdb
ENV SENTIMENT_SERVICE_URL=http://phi-sentiment-predictor:80
ENV SENTIMENT_MODEL_NAME=phi-sentiment
ENV SENTIMENT_TIMEOUT=30
ENV JAVA_OPTS="-Xmx512m -Xms256m"

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]