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

                // ✅ ✅ ✅ ALLOW ALL REACT ROUTES (VERY IMPORTANT)
                .requestMatchers(
                        "/",
                        "/index.html",
                        "/static/**",
                        "/assets/**",

                        // ✅ Frontend pages
                        "/admin/**",
                        "/properties",
                        "/property/**",
                        "/projects",
                        "/project/**",
                        "/sell",
                        "/rent-properties",
                        "/wishlist",
                        "/about",
                        "/contact-agent/**"
                ).permitAll()

                // ✅ Public admin APIs
                .requestMatchers(
                        "/admin/login",
                        "/admin/create",
                        "/admin/forgot-password",
                        "/admin/reset-password"
                ).permitAll()

                // ✅ Public backend APIs (if needed)
                .requestMatchers(
                        "/uploads/**",
                        "/seller/**",
                        "/enquiry/**"
                ).permitAll()

                // 🔒 Everything else requires JWT
                .anyRequest().authenticated()
            )

            // 🔒 Stateless JWT security
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 🔐 JWT filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
