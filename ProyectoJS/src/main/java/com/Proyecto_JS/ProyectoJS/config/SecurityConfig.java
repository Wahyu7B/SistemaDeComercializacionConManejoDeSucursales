// Ubicación: src/main/java/com/Proyecto_JS/ProyectoJS/config/SecurityConfig.java
package com.Proyecto_JS.ProyectoJS.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Reglas para páginas públicas (se quedan igual)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/", "/catalogo/**", "/registro").permitAll()

                .requestMatchers("/carrito/**", "/perfil").authenticated()
                
                // ✅ NUEVA REGLA: Solo los ADMIN pueden acceder a /admin/**
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Cualquier otra petición requiere autenticación (se queda igual)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}