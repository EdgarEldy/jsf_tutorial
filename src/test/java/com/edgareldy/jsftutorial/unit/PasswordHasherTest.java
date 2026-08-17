package com.edgareldy.jsftutorial.unit;

import com.edgareldy.jsftutorial.security.PasswordHasher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link PasswordHasher}: hashing is never reversible to the
 * raw value, and only the exact original password matches its hash.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
class PasswordHasherTest {

    @Test
    void hashNeverEqualsTheRawPassword() {
        String hash = PasswordHasher.hash("correct-horse-battery-staple");
        assertNotEquals("correct-horse-battery-staple", hash);
    }

    @Test
    void matchesReturnsTrueForTheCorrectPassword() {
        String hash = PasswordHasher.hash("correct-horse-battery-staple");
        assertTrue(PasswordHasher.matches("correct-horse-battery-staple", hash));
    }

    @Test
    void matchesReturnsFalseForTheWrongPassword() {
        String hash = PasswordHasher.hash("correct-horse-battery-staple");
        assertFalse(PasswordHasher.matches("wrong-password", hash));
    }

    @Test
    void hashingTheSamePasswordTwiceProducesDifferentHashes() {
        String first = PasswordHasher.hash("correct-horse-battery-staple");
        String second = PasswordHasher.hash("correct-horse-battery-staple");
        assertNotEquals(first, second);
    }
}
