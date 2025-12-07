package com.realestate.realestate.config;

import com.realestate.realestate.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // ✅ ✅ ✅ PUBLIC FRONTEND ROUTES (React Must Load)
                .requestMatchers(
                        "/",
                        "/index.html",
                        "/favicon.ico",
                        "/static/**",
                        "/assets/**",
                        "/admin",          
                        "/admin/**"        
                ).permitAll()

                // ✅ ✅ ✅ PUBLIC ADMIN AUTH APIs ONLY
                .requestMatchers(
                        "/api/admin/login",
                        "/api/admin/create",
                        "/api/admin/forgot-password",
                        "/api/admin/reset-password"
                ).permitAll()

                // ✅ ✅ ✅ PUBLIC USER APIs
                .requestMatchers(
                        "/uploads/**",
                        "/property/**",
                        "/seller/**",
                        "/enquiry/**",
                        "/project/**"
                ).permitAll()

                // 🔒 🔥 EVERYTHING ELSE REQUIRES JWT
                .anyRequest().authenticated()
            )

            // 🔒 No session — only JWT
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 🔐 Add JWT filter before security chain
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
