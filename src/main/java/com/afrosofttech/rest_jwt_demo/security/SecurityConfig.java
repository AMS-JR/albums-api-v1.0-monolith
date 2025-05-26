package com.afrosofttech.rest_jwt_demo.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private RSAKey rsaKey;
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        rsaKey = Jwks.generateRSAKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//    @Bean
//    public InMemoryUserDetailsManager users(){
//        return new InMemoryUserDetailsManager(
//                User.withUsername("amadou")
//                        .password("{noop}password")
//                        .authorities("read")
//                        .build()
//        );
//    }
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(authProvider);
    }
    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwks) {
        return new NimbusJwtEncoder(jwks);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/api/v1/auth/token").permitAll()
                                .requestMatchers("/api/v1/auth/users/add").permitAll()
                                .requestMatchers("/api/v1/auth/users").hasAuthority("SCOPE_ADMIN")
                                .requestMatchers("/api/v1/auth/profile").authenticated()
                                .requestMatchers("/api/v1/auth/user/{userId}/authorities/update").hasAuthority("SCOPE_ADMIN")
                                .requestMatchers("/api/v1/auth/profile/authorities/update").authenticated()
                                .requestMatchers("/api/v1/auth/user/{userId}/delete").authenticated()
                                .requestMatchers("/api/v1/albums/add").authenticated()
                                .requestMatchers("/api/v1/albums").authenticated()
                                .requestMatchers("/api/v1/albums/{id}").authenticated()
                                .requestMatchers("/api/v1/albums/{albumId}/photos").authenticated()
                                .requestMatchers("/api/v1/albums/{id}/photos/{photoId}").authenticated()
                                .requestMatchers("/api/v1/albums/{albumId}/photos/{photoId}/download").authenticated()
                                .requestMatchers("/api/v1/albums/{albumId}/thumbnails/{photoId}/download").authenticated()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/db-console/**").permitAll()
                                .requestMatchers("/uploads/**").permitAll()
                                // All other requests must be authenticated
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .formLogin(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/db-console/**"))
                .csrf(csrf -> csrf.disable())
                .headers(headers ->
                        headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
