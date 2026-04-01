package com.ch.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserInputValidator - Predicate Tests")
class UserInputValidatorTest {

    private UserInputValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserInputValidator();
    }

    // ───────────────────────── NAME ─────────────────────────

    @Test
    @DisplayName("Valid name should pass")
    void testValidName() {
        assertTrue(validator.isValidName.test("John Doe"));
    }

    @Test
    @DisplayName("Null name should fail")
    void testNullName() {
        assertFalse(validator.isValidName.test(null));
    }

    @Test
    @DisplayName("Blank name should fail")
    void testBlankName() {
        assertFalse(validator.isValidName.test("   "));
    }

    @Test
    @DisplayName("Empty name should fail")
    void testEmptyName() {
        assertFalse(validator.isValidName.test(""));
    }

    // ───────────────────────── EMAIL ────────────────────────

    @Test
    @DisplayName("Valid email should pass")
    void testValidEmail() {
        assertTrue(validator.isValidEmail.test("john@example.com"));
    }

    @Test
    @DisplayName("Email without @ should fail")
    void testEmailWithoutAt() {
        assertFalse(validator.isValidEmail.test("johnexample.com"));
    }

    @Test
    @DisplayName("Email without domain extension should fail")
    void testEmailWithoutDomain() {
        assertFalse(validator.isValidEmail.test("john@example"));
    }

    @Test
    @DisplayName("Null email should fail")
    void testNullEmail() {
        assertFalse(validator.isValidEmail.test(null));
    }

    @Test
    @DisplayName("Empty email should fail")
    void testEmptyEmail() {
        assertFalse(validator.isValidEmail.test(""));
    }

    @Test
    @DisplayName("Email starting with @ should fail")
    void testEmailStartingWithAt() {
        assertFalse(validator.isValidEmail.test("@nodomain.com"));
    }

    // ───────────────────────── PASSWORD ─────────────────────

    @Test
    @DisplayName("Valid password should pass all predicates")
    void testValidPassword() {
        assertTrue(validator.isValidPassword.test("Secret@123"));
    }

    @Test
    @DisplayName("Password less than 8 chars should fail")
    void testPasswordTooShort() {
        assertFalse(validator.hasMinLength.test("Ab@1"));
        assertFalse(validator.isValidPassword.test("Ab@1"));
    }

    @Test
    @DisplayName("Password without uppercase should fail")
    void testPasswordNoUppercase() {
        assertFalse(validator.hasUpperCase.test("secret@123"));
        assertFalse(validator.isValidPassword.test("secret@123"));
    }

    @Test
    @DisplayName("Password without lowercase should fail")
    void testPasswordNoLowercase() {
        assertFalse(validator.hasLowerCase.test("SECRET@123"));
        assertFalse(validator.isValidPassword.test("SECRET@123"));
    }

    @Test
    @DisplayName("Password without digit should fail")
    void testPasswordNoDigit() {
        assertFalse(validator.hasDigit.test("Secret@abc"));
        assertFalse(validator.isValidPassword.test("Secret@abc"));
    }

    @Test
    @DisplayName("Password without special char should fail")
    void testPasswordNoSpecialChar() {
        assertFalse(validator.hasSpecialChar.test("Secret123"));
        assertFalse(validator.isValidPassword.test("Secret123"));
    }

    @Test
    @DisplayName("Null password should fail all predicates")
    void testNullPassword() {
        assertFalse(validator.hasMinLength.test(null));
        assertFalse(validator.hasUpperCase.test(null));
        assertFalse(validator.hasLowerCase.test(null));
        assertFalse(validator.hasDigit.test(null));
        assertFalse(validator.hasSpecialChar.test(null));
        assertFalse(validator.isValidPassword.test(null));
    }

    // ─────────────────── DESCRIBE PASSWORD FAILURE ──────────────────────

    @Test
    @DisplayName("describePasswordFailure - too short")
    void testDescribePasswordTooShort() {
        assertEquals("Password must be at least 8 characters long.",
                validator.describePasswordFailure("Ab@1"));
    }

    @Test
    @DisplayName("describePasswordFailure - no uppercase")
    void testDescribePasswordNoUppercase() {
        assertEquals("Password must contain at least one uppercase letter.",
                validator.describePasswordFailure("secret@123"));
    }

    @Test
    @DisplayName("describePasswordFailure - no lowercase")
    void testDescribePasswordNoLowercase() {
        assertEquals("Password must contain at least one lowercase letter.",
                validator.describePasswordFailure("SECRET@123"));
    }

    @Test
    @DisplayName("describePasswordFailure - no digit")
    void testDescribePasswordNoDigit() {
        assertEquals("Password must contain at least one digit.",
                validator.describePasswordFailure("Secret@abc"));
    }

    @Test
    @DisplayName("describePasswordFailure - no special char")
    void testDescribePasswordNoSpecialChar() {
        assertEquals("Password must contain at least one special character (@, #, $, %, ^, *, -, _).",
                validator.describePasswordFailure("Secret123"));
    }
}