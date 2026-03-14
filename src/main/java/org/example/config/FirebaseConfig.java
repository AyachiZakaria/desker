package org.example.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {

                // Read Firebase credentials JSON from environment variable
                String firebaseJson = System.getenv("FIREBASE_CREDENTIALS_JSON");
                if (firebaseJson == null || firebaseJson.isEmpty()) {
                    throw new IllegalStateException("FIREBASE_CREDENTIALS_JSON env variable not set!");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(
                                new ByteArrayInputStream(firebaseJson.getBytes(StandardCharsets.UTF_8))
                        ))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize Firebase Admin SDK", e);
        }
    }
}