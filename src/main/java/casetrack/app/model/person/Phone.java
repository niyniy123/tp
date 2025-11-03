package casetrack.app.model.person;

import static casetrack.app.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers should only contain numbers, and it should be at least 3 digits long. "
                    + "The maximum length is 17 digits, excluding any country code. "
                    + "Phone numbers may be preceded by an optional country code (up to 3 digits) "
                    + "with an optional '+' prefix. "
                    + "A space may optionally separate the country code from the main number.";
    public static final String VALIDATION_REGEX = "(\\+\\d{1,3}\\s?|\\d{1,3}\\s)?\\d{3,17}";
    public final String value;
    private final String normalizedValue;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone;
        normalizedValue = normalize(phone);
    }

    /**
     * Normalizes a phone number by removing all '+' signs and spaces.
     *
     * @param phone The phone number to normalize.
     * @return The normalized phone number containing only digits.
     */
    private static String normalize(String phone) {
        return phone.replaceAll("[+\\s]", "");
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone)) {
            return false;
        }

        Phone otherPhone = (Phone) other;
        return normalizedValue.equals(otherPhone.normalizedValue);
    }

    @Override
    public int hashCode() {
        return normalizedValue.hashCode();
    }

}
