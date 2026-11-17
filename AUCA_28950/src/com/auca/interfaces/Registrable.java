package com.auca.interfaces;

import com.auca.exceptions.ValidationException;
import java.time.LocalDate;

/**
 * Contract for every record that can be registered (validated, printed, summarised).
 * updateFrom(...) is the extra business operation used by "Update Record"; it is declared
 * here first, as the assignment requires. The static methods are shared validation helpers.
 */
public interface Registrable {
    int MIN_NAME_LENGTH = 2;
    int MAX_NAME_LENGTH = 60;
    int MAX_EMAIL_LENGTH = 100;
    int MIN_PHONE_LENGTH = 10;
    int MAX_PHONE_LENGTH = 15;
    String DATE_PATTERN = "yyyy-MM-dd";

    void validate() throws ValidationException;
    void printDetails();
    String getRegistrationSummary();
    void updateFrom(Registrable source) throws ValidationException;

    // ---------- shared validation helpers ----------
    static String text(String value, String label, int min, int max) throws ValidationException {
        if (value == null || value.trim().isEmpty()) throw new ValidationException(label + " must not be blank.");
        String t = value.trim();
        if (t.length() < min || t.length() > max)
            throw new ValidationException(label + " must be " + min + "-" + max + " characters long.");
        return t;
    }

    static void id(String value, String label) throws ValidationException {
        text(value, label, Identifiable.MIN_ID_LENGTH, Identifiable.MAX_ID_LENGTH);
    }

    static void email(String value, String label) throws ValidationException {
        String t = text(value, label, 5, MAX_EMAIL_LENGTH);
        if (!t.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$"))
            throw new ValidationException(label + " is not a valid email address (example: name@example.com).");
    }

    static void phone(String value, String label) throws ValidationException {
        if (value == null || value.trim().isEmpty()) throw new ValidationException(label + " must not be blank.");
        String t = value.trim();
        String digits = t.startsWith("+") ? t.substring(1) : t;
        if (!digits.matches("\\d{" + MIN_PHONE_LENGTH + "," + MAX_PHONE_LENGTH + "}"))
            throw new ValidationException(label + " must contain " + MIN_PHONE_LENGTH + "-" + MAX_PHONE_LENGTH
                    + " digits with an optional leading +.");
    }

    static void required(Object value, String label) throws ValidationException {
        if (value == null) throw new ValidationException(label + " is required.");
    }

    static void pastDate(LocalDate d, String label) throws ValidationException {
        required(d, label);
        if (!d.isBefore(LocalDate.now())) throw new ValidationException(label + " must be in the past.");
    }

    static void notFuture(LocalDate d, String label) throws ValidationException {
        required(d, label);
        if (d.isAfter(LocalDate.now())) throw new ValidationException(label + " cannot be in the future.");
    }

    static int range(int v, String label, int min, int max) throws ValidationException {
        if (v < min || v > max) throw new ValidationException(label + " must be between " + min + " and " + max + ".");
        return v;
    }
}
