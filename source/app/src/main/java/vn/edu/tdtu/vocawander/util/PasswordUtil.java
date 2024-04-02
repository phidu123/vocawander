package vn.edu.tdtu.vocawander.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
	// encrypt password
    public static String getHashing(String password) {
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(password, salt);
        return hashedPassword;
    }
    public static boolean checkPassword(String plaintextPassword, String hashedPassword) {
        return BCrypt.checkpw(plaintextPassword, hashedPassword);
    }
}
