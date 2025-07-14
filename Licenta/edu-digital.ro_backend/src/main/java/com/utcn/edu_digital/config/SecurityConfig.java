package com.utcn.edu_digital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Activează CORS şi dezactivează CSRF (REST/JWT)
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                // 2. Headerele de securitate deja configurate (CSP, XSS, HSTS etc.)
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; " +
                                                "script-src 'self'; " +
                                                "style-src 'self' 'unsafe-inline'; " +
                                                "img-src 'self' data:; " +
                                                "font-src 'self' data:; " +
                                                "frame-ancestors 'none';"
                                )
                        )
                        .xssProtection(Customizer.withDefaults())
                        .contentTypeOptions(Customizer.withDefaults())
                        .frameOptions(Customizer.withDefaults())
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31_536_000)
                        )
                        .referrerPolicy(ref -> ref
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)
                        )
                        .addHeaderWriter(new StaticHeadersWriter(
                                "Permissions-Policy",
                                "geolocation=(), microphone=()"
                        ))
                )

                // 3. Permisiuni de acces
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }


}
