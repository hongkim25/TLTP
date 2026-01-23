package com.hong.thebaker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests

                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/", "/privacy.html", "/about.html", "/index.html", "/menu.html", "/reservation.html").permitAll()
                        .requestMatchers("/manifest.json", "/sw.js", "/icon-*.png").permitAll() // Static assets

                        // 2. PUBLIC APIs (So customers can check menu & order)
                        .requestMatchers("/api/products/**", "/api/orders/**").permitAll() // *Note: detailed locking comes later
                        .requestMatchers("/api/staff/status").permitAll()
                        // 3. SECURED PAGES (Staff) - Everything else requires login
                        .requestMatchers("/staff/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")        // my custom login html
                        .loginProcessingUrl("/login") // Where the form POSTs data to
                        .defaultSuccessUrl("/staff", true) // Where to go after success
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll())
                .csrf((csrf) -> csrf.disable()); // *Important: Disabling CSRF for simplicity in MVP (allows POST requests easily)

        return http.build();
    }

    @Value("${app.staff.username}")
    private String staffUsername;

    @Value("${app.staff.password}")
    private String staffPassword;

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        // Create the single Staff User
        UserDetails staff = User.withDefaultPasswordEncoder() // Not safe for production DB, but fine for MVP memory auth
                .username(staffUsername)
                .password(staffPassword)
                .roles("STAFF")
                .build();
        return new InMemoryUserDetailsManager(staff);
    }
}