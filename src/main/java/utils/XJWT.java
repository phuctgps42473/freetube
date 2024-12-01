package utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import jakarta.servlet.ServletContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;
import java.util.Map;

public class XJWT {
    public static String create(String jsonPayload, long expireInMinutes) {
        try {

            String projectRoot = System.getenv("PROJECT_ROOT");
            JsonReader jr = new JsonReader(new FileReader(projectRoot + "\\src\\main\\java\\secret.json"));
            Map<String, String> map = new Gson().fromJson(jr, Map.class);

            try {
                Algorithm algorithm = Algorithm.HMAC256(map.get("JWT_SECRET"));
                String token = JWT.create()
                        .withPayload(jsonPayload)
                        .withIssuer("auth0")
                        .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * expireInMinutes))
                        .withIssuedAt(new Date())
                        .sign(algorithm);
                return token;
            } catch (JWTCreationException exception) {
                System.out.println("Invalid Signing configuration / Couldn't convert Claims.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND BITCH");
        }
        return null;
    }
}
