package utils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.util.Arrays;

public class XPassword {
    public static String hashPassword(String password) {
        Argon2 argon2 = Argon2Factory.create();

        char[] passwordCharArray = password.toCharArray();

        // Hash with default settings (4 parallelism, 65536 memory cost, 32 iterations)
        String hashedPassword = argon2.hash(4, 65536, 32, passwordCharArray);

        Arrays.fill(passwordCharArray, '\0');

        return hashedPassword;
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        Argon2 argon2 = Argon2Factory.create();
        return argon2.verify(hashedPassword, password.toCharArray());
    }


}
