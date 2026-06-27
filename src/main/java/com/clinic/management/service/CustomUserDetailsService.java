package com.clinic.management.service;

import com.clinic.management.entity.User;
import com.clinic.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * CustomUserDetailsService → Spring Security'ye
 * "kullanıcıyı nasıl bulacağını" öğretiyoruz.
 *
 * Spring Security login sırasında şunu sorar:
 * "Bu username'e sahip kullanıcı var mı DB'de?"
 * Biz de burda DB'ye bakıp cevap veriyoruz.
 *
 * UserDetailsService → Spring Security'nin interface'i
 * loadUserByUsername → Spring bu metodu çağırır
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // DB'den kullanıcıyı bul
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));

        /*
         * Spring Security'nin anlayacağı formata çevir
         * ROLE_ prefix zorunlu — Spring Security kuralı
         * user.getRole() → "ADMIN" → "ROLE_ADMIN"
         */
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}