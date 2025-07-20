FROM openjdk:17-jdk-slim

WORKDIR /app

COPY src/Main.java ./src/Main.java
COPY quotes.txt index.html ./

RUN javac src/Main.java && mv src/Main.class .

EXPOSE 8000

CMD ["java", "Main"]

