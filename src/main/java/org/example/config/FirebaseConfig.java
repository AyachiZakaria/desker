package org.example.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials.path}")
    private String credentialsPath;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options;
                if (credentialsPath.startsWith("classpath:")) {
                    String path = credentialsPath.substring("classpath:".length());
                    options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(
                                    getClass().getClassLoader().getResourceAsStream(path)
                            ))
                            .build();
                } else {
                    options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsPath)))
                            .build();
                }
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            // Log error or throw unchecked exception based on requirements
            throw new RuntimeException("Could not initialize Firebase Admin SDK", e);
        }
    }
}
