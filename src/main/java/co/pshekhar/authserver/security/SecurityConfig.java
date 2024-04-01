package co.pshekhar.authserver.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final String password;

    public SecurityConfig(@Value("${auth.config.jwt.signing-key}") String password) {
        this.password = password;
    }

    @Bean(name = "jwt-secret-key")
    SecretKey getSecretKey() throws NoSuchAlgorithmException {
        byte[] keyBytes = password.getBytes();
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = sha.digest(keyBytes);
        byte[] truncatedHash = Arrays.copyOf(hashedBytes, 64);
        return new SecretKeySpec(truncatedHash, "HmacSHA256");
    }
}
