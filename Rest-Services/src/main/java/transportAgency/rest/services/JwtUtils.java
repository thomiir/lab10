package transportAgency.rest.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class JwtUtils {
    private static final String SECRET_KEY;

    static {
        Properties properties = new Properties();
        try (InputStream input = JwtUtils.class.getClassLoader().getResourceAsStream("hibernate.properties")) {
            properties.load(input);
            SECRET_KEY = properties.getProperty("jwt.secret");
        } catch (IOException e) {
            throw new RuntimeException("Could not load hibernate.properties", e);
        }
    }

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public static SecretKey getKEY() {
        return KEY;
    }

    public static void validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);
        } catch (SecurityException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        }
    }
}
