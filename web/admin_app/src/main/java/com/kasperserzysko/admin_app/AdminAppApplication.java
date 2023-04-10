package com.kasperserzysko.admin_app;

import com.kasperserzysko.data.models.User;
import com.kasperserzysko.data.models.enums.Role;
import com.kasperserzysko.data.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kasperserzysko.admin_app",
        "com.kasperserzysko.security",
        "com.kasperserzysko.data",
        "com.kasperserzysko.web"
        })
@RequiredArgsConstructor
public class AdminAppApplication implements CommandLineRunner {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(AdminAppApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        var adminUser = new User();
        adminUser.setEmail("admin");
        adminUser.setPassword(passwordEncoder.encode("admin"));
        adminUser.setEnabled(true);
        adminUser.setRoles(new HashSet<>(List.of(Role.ROLE_USER, Role.ROLE_ADMIN)));
        userRepository.save(adminUser);
    }
}
