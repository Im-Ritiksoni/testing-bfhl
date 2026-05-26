package com.acropolis.bfhl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// ============================================================
// Deploy to Render.com (free tier):
// 1. Push this project to a GitHub repository.
// 2. Go to https://render.com → New Web Service → connect repo.
// 3. Build command : mvn clean package -DskipTests
// 4. Start command : java -jar target/*.jar
// 5. Set PORT env variable in Render dashboard;
//    application.properties already reads: server.port=${PORT:8080}
// 6. Live URL will be: https://your-app.onrender.com/bfhl
// ============================================================

@SpringBootApplication
public class BfhlApplication {

    public static void main(String[] args) {
        SpringApplication.run(BfhlApplication.class, args);
    }
}
