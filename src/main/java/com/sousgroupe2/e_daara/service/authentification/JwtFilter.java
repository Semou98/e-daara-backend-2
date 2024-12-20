package com.sousgroupe2.e_daara.service.authentification;

import com.sousgroupe2.e_daara.entities.utilisateur.Utilisateur;
import com.sousgroupe2.e_daara.service.utilisateur.UtilisateurServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@Service
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    UtilisateurServiceImpl utilisateurService;
    @Autowired
    AuthentificationToken jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token= null;
        String username = null;
        boolean expiredToken = true;
        final String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer")) {
            token = auth.substring(7);
            log.info("auth token {}", token);
            expiredToken = this.jwtService.isTokenExpired(token);
            log.info("auth expiration {}", expiredToken);
            username = this.jwtService.extractusername(token);
            log.info("auth user {}", username);

        }
        if(!expiredToken && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("token valid=>{}", true);
            Utilisateur user = (Utilisateur) this.utilisateurService.loadUserByUsername(username);
            log.info("authority {}", user.getAuthorities());
            UsernamePasswordAuthenticationToken passwordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(passwordAuthenticationToken);

        }
        filterChain.doFilter(request, response);
    }
}
