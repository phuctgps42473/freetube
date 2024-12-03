package utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class XToken {

    public static String create(String jsonPayload, long expireInMinutes) {
        String secret = XSecret.getSecret("JWT_SECRET");

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withPayload(jsonPayload)
                    .withIssuer("auth0")
                    .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * expireInMinutes))
                    .withIssuedAt(new Date())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            System.out.println("Invalid Signing configuration / Couldn't convert Claims.");
            return null;
        }
    }

    public static DecodedJWT verify(String token) {
        DecodedJWT decodedJWT = null;

        String secret = XSecret.getSecret("JWT_SECRET");

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build();

            decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException exception) {
            System.out.println("Invalid signature/claims");
        }
        return decodedJWT;
    }
}
