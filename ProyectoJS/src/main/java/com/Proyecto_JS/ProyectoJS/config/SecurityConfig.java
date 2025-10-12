package com.Proyecto_JS.ProyectoJS.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/css/**", 
                    "/js/**", 
                    "/images/**", 
                    "/", 
                    "/catalogo/**", 
                    "/sucursales", 
                    "/nosotros", 
                    "/contacto"
                ).permitAll()
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") 
                .permitAll() 
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .permitAll()
            );

        return http.build();
    }
}