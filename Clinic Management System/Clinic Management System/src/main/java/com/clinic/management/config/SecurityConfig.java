package com.clinic.management.config;

import com.clinic.management.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/*
 * FINAL SecurityConfig — Login sistemi aktif
 *
 * Nasıl çalışır:
 * 1. Kullanıcı /patients açmaya çalışır
 * 2. Login olmamışsa → /login'e yönlendirilir
 * 3. Username + password girer
 * 4. CustomUserDetailsService DB'den kullanıcıyı bulur
 * 5. BCrypt şifreyi doğrular
 * 6. Giriş başarılı → /dashboard'a gider
 *
 * BCrypt nedir?
 * Şifreleri düz metin olarak saklamak tehlikeli.
 * BCrypt şifreyi karmaşık bir hash'e çevirir:
 * "12345" → "$2a$10$xK8J..." gibi
 * DB'ye bu hash kaydedilir, asla düz şifre saklanmaz.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Login sayfası herkese açık
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                        // Geri kalan her şey login gerektirir
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")           // bizim login sayfamız
                        .defaultSuccessUrl("/dashboard", true)  // başarılı → dashboard
                        .failureUrl("/login?error")    // hatalı şifre → hata mesajı
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")  // çıkış → login
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    /*
     * BCryptPasswordEncoder → şifreleri hash'ler
     * strength=10 → ne kadar güçlü hash (10 standarttır)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /*
     * AuthenticationProvider → kim kimliği doğruluyor?
     * DB'den kullanıcıyı bul + BCrypt ile şifreyi karşılaştır
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}