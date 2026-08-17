package com.edgareldy.jsftutorial.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * jBCrypt wrapper: the only place in this project allowed to hash or compare
 * a password, so no code path can slip into a plain-text comparison.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
public final class PasswordHasher {

    private PasswordHasher() {
    }

    public static String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}
