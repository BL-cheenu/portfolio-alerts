package com.ch.utils;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class UserInputValidator {
    // ── Email ──────────────────────────────────────────────────────────────
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public final Predicate<String> isValidEmail =
            email -> email != null && EMAIL_PATTERN.matcher(email.trim()).matches();

    // ── Password predicates ────────────────────────────────────────────────
    public final Predicate<String> hasMinLength =
            pwd -> pwd != null && pwd.length() >= 8;

    public final Predicate<String> hasUpperCase =
            pwd -> pwd != null && pwd.chars().anyMatch(Character::isUpperCase);

    public final Predicate<String> hasLowerCase =
            pwd -> pwd != null && pwd.chars().anyMatch(Character::isLowerCase);

    public final Predicate<String> hasDigit =
            pwd -> pwd != null && pwd.chars().anyMatch(Character::isDigit);

    public final Predicate<String> hasSpecialChar =
            pwd -> pwd != null && pwd.chars()
                    .mapToObj(c -> (char) c)
                    .anyMatch(c -> "@#$%^*-_".indexOf(c) >= 0);

    // ── Combined password predicate ────────────────────────────────────────
    public final Predicate<String> isValidPassword =
            hasMinLength
                    .and(hasUpperCase)
                    .and(hasLowerCase)
                    .and(hasDigit)
                    .and(hasSpecialChar);

    // ── Non-blank name ─────────────────────────────────────────────────────
    public final Predicate<String> isValidName =
            name -> name != null && !name.trim().isEmpty();

    // ── Descriptor helpers for error messages ──────────────────────────────
    public String describePasswordFailure(String password) {
        if (!hasMinLength.test(password))  return "Password must be at least 8 characters long.";
        if (!hasUpperCase.test(password))  return "Password must contain at least one uppercase letter.";
        if (!hasLowerCase.test(password))  return "Password must contain at least one lowercase letter.";
        if (!hasDigit.test(password))      return "Password must contain at least one digit.";
        if (!hasSpecialChar.test(password))return "Password must contain at least one special character (@, #, $, %, ^, *, -, _).";
        return "Invalid password.";
    }
}
