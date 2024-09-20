package com.thedevlair.user.security.jwt;

import com.thedevlair.user.security.util.RsaUtil;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.thedevlair.user.security.util.EncryptionUtility.PRIVATE_KEY;
import static com.thedevlair.user.security.util.EncryptionUtility.PUBLIC_KEY;

@Component
public class JwtService {

    private static final Set<String> invalidatedTokens = new HashSet<>();
    private final RsaUtil rsaUtil;
    @Value("${theDevLair.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public JwtService(RsaUtil rsaUtil) {
        this.rsaUtil = rsaUtil;
    }

    public static void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    public String generateToken(String userName) {
        try {
            PrivateKey privateKey = rsaUtil.getPrivateKey(PRIVATE_KEY);
            return Jwts.builder()
                    .setSubject(userName)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                    .signWith(privateKey, SignatureAlgorithm.RS512)
                    .compact();
        } catch (Exception ex) {
            throw new RuntimeException("Error al generar el token JWT", ex);
        }
    }

    public String getUserNameFromToken(String token) {
        try {
            PublicKey publicKey = rsaUtil.getPublicKey(PUBLIC_KEY);
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
            return claimsJws.getBody().getSubject();
        } catch (JwtException ex) {
            return ex.getMessage();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public boolean validateToken(String token) {
        try {
            PublicKey publicKey = rsaUtil.getPublicKey(PUBLIC_KEY);
            Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
            return !invalidatedTokens.contains(token);
        } catch (Exception ex) {
            return false;
        }
    }

}
