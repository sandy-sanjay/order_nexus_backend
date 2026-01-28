package com.ordernexus.order_nexus_backend.config;

import com.ordernexus.order_nexus_backend.auth.AuthService;
import com.ordernexus.order_nexus_backend.auth.AuthUser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AuthService authService;

    public DataSeeder(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "sandysanjay589@gmail.com";
        if (authService.findByUsername(adminEmail).isEmpty()) {
            AuthUser admin = new AuthUser();
            admin.setUsername(adminEmail);
            admin.setPassword("admin123");
            admin.setRole("ADMIN");
            authService.save(admin);
            System.out.println("✅ Authorization: Admin user created (" + adminEmail + "/admin123)");
        }
    }

}
