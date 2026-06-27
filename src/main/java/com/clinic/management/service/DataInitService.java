package com.clinic.management.service;

import com.clinic.management.entity.Role;
import com.clinic.management.entity.User;
import com.clinic.management.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataInitService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    /*
     * PasswordEncoder inject ettik — SecurityConfig'de @Bean olarak tanımladık
     * Spring otomatik olarak BCryptPasswordEncoder'ı buraya verir
     */

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            /*
             * Şifreyi düz metin olarak SAKLAMIYORUZ
             * passwordEncoder.encode("admin123") →
             * "$2a$10$abc123..." gibi bir hash üretir
             * Bu hash DB'ye kaydedilir
             */
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println(">>> DEFAULT ADMIN CREATED");
            System.out.println(">>> Username: admin");
            System.out.println(">>> Password: admin123");
        }
    }
}