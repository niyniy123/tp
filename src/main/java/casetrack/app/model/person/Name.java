package casetrack.app.model.person;

import static casetrack.app.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain alphanumeric characters, spaces, periods (.), apostrophes ('), hyphens (-),"
                    + "round brackets (()), at (@), 's/o' or 'd/o' (case-insensitive), and it should not be blank."
                    + " Names must contain at least one alphabetic character.";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}()]([\\p{Alnum} .'()@-]|(([sdSD]/[oO]) ))*";

    /*
     * Ensures that at least one character is an alphabetic character
     */
    public static final String AT_LEAST_ONE_ALPHABET = ".*[a-zA-Z].*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.matches(VALIDATION_REGEX) && test.matches(AT_LEAST_ONE_ALPHABET);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name)) {
            return false;
        }

        Name otherName = (Name) other;
        return normalizedName(fullName).equals(normalizedName(otherName.fullName));
    }

    @Override
    public int hashCode() {
        return normalizedName(fullName).hashCode();
    }

    /**
     * Normalizes a name by converting to lowercase and replacing multiple consecutive spaces with a single space.
     */
    private static String normalizedName(String name) {
        return name.toLowerCase().replaceAll("\\s+", " ").trim();
    }

}
