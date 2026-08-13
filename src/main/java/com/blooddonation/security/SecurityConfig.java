package com.blooddonation.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAccessDeniedHandler jwtAccessDeniedHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/test").permitAll()

                        // Public browsing/search endpoints
                        .requestMatchers(HttpMethod.GET, "/api/donors", "/api/donors/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blood-requests", "/api/blood-requests/**", "/api/requests", "/api/requests/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/recipients", "/api/recipients/{id}").permitAll()

                        // Protected Donor endpoints
                        .requestMatchers(HttpMethod.POST, "/api/donors").hasAnyRole("DONOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/donors/**").hasAnyRole("DONOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/donors/**").hasAnyRole("DONOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/donors/**").hasRole("ADMIN")

                        // Protected Recipient endpoints
                        .requestMatchers(HttpMethod.POST, "/api/recipients").hasAnyRole("RECIPIENT", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/recipients/**").hasAnyRole("RECIPIENT", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/recipients/**").hasRole("ADMIN")

                        // Protected Blood Request mutations
                        .requestMatchers(HttpMethod.POST, "/api/blood-requests", "/api/requests").hasAnyRole("RECIPIENT", "HOSPITAL", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/blood-requests/**", "/api/requests/**").hasAnyRole("HOSPITAL", "BLOOD_BANK", "RECIPIENT", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/blood-requests/**", "/api/requests/**").hasAnyRole("RECIPIENT", "ADMIN")

                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
