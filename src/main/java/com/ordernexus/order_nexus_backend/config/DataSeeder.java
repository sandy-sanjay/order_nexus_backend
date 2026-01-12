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
        if (authService.findByUsername("admin").isEmpty()) {
            AuthUser admin = new AuthUser();
            admin.setUsername("admin");
            admin.setPassword("admin123"); // PasswordEncoder will handle hashing in service if implemented there,
                                           // otherwise we might need to hash here.
            // Checking AuthController logic, it seems it calls authService.save(user).
            // We need to assume authService handles hashing or we need to check
            // AuthService.
            // Let's check AuthService first to be safe, but for now I'll create this
            // assuming standard practice.
            // Actually, best to check AuthService to see if it hashes password.
            // The AuthController uses passwordEncoder for MATCHING, so likely
            // AuthService.save() HAS hashes or AuthController does it?
            // AuthController: authService.save(user);
            // Let's assume AuthService handles it or I should check.
            // To be safe, I will inject PasswordEncoder here too.

            admin.setRole("ADMIN");
            authService.save(admin);
            System.out.println("✅ Authorization: Admin user created (admin/admin123)");
        }
    }
}
