package com.sousgroupe2.e_daara.service.authentification;

import com.sousgroupe2.e_daara.entities.utilisateur.Utilisateur;
import com.sousgroupe2.e_daara.service.utilisateur.UtilisateurServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
@AllArgsConstructor
@Service
public class AuthentificationToken {

    private UtilisateurServiceImpl utilisateurServiceImpl;

    //private final String key="95a7e2aa65753e4a506f5a8c0fcaf54b399735297021b26c9ceb2bbc2f302d04";
    private final String key = "0eb98edfe37b4533a41cb7ef13d0daf2a422e83f2d5672b3e7282063b3f2fb19ee90ccafe2ea8fa1648db77b285eed1fa49a42c449c7a6f36b2fbef6e4b17459";

    public String genere(String email) {
        Utilisateur user = (Utilisateur) utilisateurServiceImpl.loadUserByUsername(email);
        return generateToken(user);
    }

    private String generateToken(Utilisateur user) {
            Long init = System.currentTimeMillis();
            Long dest = init+3000*60*1000;
            Map<String, String> userLogin = Map.of(
                    "email", user.getEmail(),
                    "password", user.getPassword(),
                    Claims.EXPIRATION, String.valueOf(dest),
                    Claims.SUBJECT, user.getUsername()
            );

           String token= Jwts.builder()
                    .setIssuedAt(new Date(init))
                    .setExpiration(new Date(dest))
                    .setSubject(user.getUsername())
                    .setClaims(userLogin)
                    .signWith(key(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
    }
    private Key key() {
      byte[] decoder=  Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(decoder);
    }

    public String extractusername(String token) {
        String user = parseToken(token).getSubject();
        return user;
    }

    public boolean isTokenExpired(String token) {
        Date today = parseToken(token).getExpiration();
        return today.before(new Date());
    }
    public Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(key()).parseClaimsJws(token).getBody();
    }
}
