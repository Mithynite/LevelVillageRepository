package levelvillage.com.levelvillage.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import levelvillage.com.levelvillage.config.ConfigManager;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * Utility class for handling JWT (JSON Web Tokens) related operations.
 *
 * @author Jakub Hofman
 */
@Component
public class JWTTokenUtil {

    private Key secretKey;
    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 10; // 10 hours

    /**
     * Initializes the secret key for JWT token generation.
     */
    @PostConstruct
    public void init() {
        String secret = ConfigManager.jwtSecret;

        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT secret not configured in ConfigManager.");
        }

        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    /**
     * Generates a JWT token for the given username.
     *
     * @param username the username for which the token needs to be generated
     * @return the generated JWT token
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token the JWT token from which the username needs to be extracted
     * @return the extracted username
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extracts the expiration time from the given JWT token.
     *
     * @param token the JWT token from which the expiration time needs to be extracted
     * @return the extracted expiration time in milliseconds
     */
    public long extractExpiration(String token) {
        return extractAllClaims(token).getExpiration().getTime();
    }

    /**
     * Checks if the given JWT token is expired.
     *
     * @param token the JWT token to be checked
     * @return true if the token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Validates the given JWT token for the given username.
     *
     * @param token the JWT token to be validated
     * @param username the username for which the token needs to be validated
     * @return true if the token is valid for the given username, false otherwise
     */
    public boolean validateToken(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    /**
     * Extracts all claims from the given JWT token.
     *
     * @param token the JWT token from which the claims need to be extracted
     * @return the extracted claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
