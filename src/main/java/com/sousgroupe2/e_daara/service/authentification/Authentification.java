package com.sousgroupe2.e_daara.service.authentification;


import com.sousgroupe2.e_daara.service.utilisateur.UtilisateurServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.file.AccessDeniedException;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
public class Authentification {

    @Autowired
    JwtFilter filter;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception, AccessDeniedException {

        return  http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource())) // Configuration CORS avec la methode corsConfigurationSource()
                .authorizeHttpRequests( authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Autoriser les requêtes OPTIONS pour CORS
                        .requestMatchers("/utilisateurs/**", "/categories", "/parcours", "/contenu/**").permitAll() // Ces endpoints sont accessibles à tous
                        //.requestMatchers("/auth/**", "/oauth2/**", "/categories", "/parcours", "/contenu/**").permitAll() // Ces endpoints sont accessibles à tous
                        .requestMatchers("/api/**").hasAnyRole("ADMIN", "APPRENANT") // Il faut avoir Il faut avoir ces roles pour accéder à l'application (ensuite il faut utiliser l'annotation @Secured("ROLE_ADMIN") pour restreindre certains endpoints à l'usage exclusif des ADMIN)
                        .anyRequest().authenticated() // Tout autre endpoint nécessite une authentification

                        /*
                        .requestMatchers(POST,"/utilisateurs").permitAll()
                        .requestMatchers(POST,"/utilisateurs/login").permitAll()
                        .requestMatchers(POST,"/utilisateurs/new-password").permitAll()
                        .requestMatchers("/utilisateurs/verif-key/**").permitAll()
                        .requestMatchers(POST,"/utilisateurs/reset-password").permitAll()
                        .requestMatchers(GET,"/utilisateurs").hasRole("ADMIN")
                        .requestMatchers(GET,"/categories/**").permitAll()
                        .requestMatchers(POST,"/categories").hasRole("ADMIN")
                        .requestMatchers(PUT,"/categories").hasRole("ADMIN")
                        .requestMatchers(DELETE,"/categories").hasRole("ADMIN")
                        .requestMatchers(DELETE, "/utilisateurs").hasRole("ADMIN")
                        .requestMatchers(GET, "/utilisateurs/role/**").hasRole("ADMIN")
                        .requestMatchers(GET, "/utilisateurs/count/**").hasRole("ADMIN")
                        .requestMatchers(GET,"/parcours/**").permitAll()
                        .requestMatchers(POST,"/parcours").hasRole("ADMIN")
                        .requestMatchers(PUT,"/parcours").hasRole("ADMIN")
                        .requestMatchers(DELETE,"/parcours").hasRole("ADMIN")
                        .requestMatchers(GET,"/cours/**").permitAll()
                        .requestMatchers(GET,"/cours").hasRole("ADMIN")
                        .requestMatchers(POST,"/cours").hasRole("ADMIN")
                        .requestMatchers(PUT,"/cours").hasRole("ADMIN")
                        .requestMatchers(DELETE,"/cours").hasRole("ADMIN")
                        .anyRequest().authenticated()
                         */
                )
                .sessionManagement(
                        httpSecuritySessionManagementConfigurer ->
                                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // Paramétrage CORS
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.applyPermitDefaultValues();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:8082")); // On pourrait aussi utiliser la valeur de "cors.allowed-origins" définie dans application.properties
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Types de requetes HTTP autorisées
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept")); // Autorise tous les headers
        corsConfiguration.setAllowCredentials(true); // Autorise l'envoi de credentials (JWT, Cookies)
        corsConfiguration.setMaxAge(3600L); // Durée pendant laquelle la réponse du preflight CORS est mise en cache

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Applique la configuration à toutes les routes ("/**")
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UtilisateurServiceImpl utilisateurService) {
        DaoAuthenticationProvider daoAuthenticationProvider= new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService((UserDetailsService) utilisateurService);
        daoAuthenticationProvider.setPasswordEncoder(this.encoder);
        return daoAuthenticationProvider;
    }
}
