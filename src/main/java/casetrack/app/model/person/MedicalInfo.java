package casetrack.app.model.person;

import static casetrack.app.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's medical information in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidMedicalInfo(String)}
 */
public class MedicalInfo {

    public static final String MESSAGE_CONSTRAINTS =
            "Medical info must not be empty.";

    /*
     * The first character must not be a whitespace to prevent blank strings after trimming.
     * Allows any subsequent characters including spaces.
     */
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public final String medicalInfo;

    /**
     * Constructs a {@code MedicalInfo}.
     *
     * @param medicalInfo Non-empty medical info string.
     */
    public MedicalInfo(String medicalInfo) {
        requireNonNull(medicalInfo);
        String trimmed = medicalInfo.trim();
        checkArgument(isValidMedicalInfo(trimmed), MESSAGE_CONSTRAINTS);
        this.medicalInfo = trimmed;
    }

    /**
     * Returns true if a given string is a valid medical info.
     */
    public static boolean isValidMedicalInfo(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return medicalInfo;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MedicalInfo)) {
            return false;
        }
        MedicalInfo otherInfo = (MedicalInfo) other;
        return medicalInfo.equals(otherInfo.medicalInfo);
    }

    @Override
    public int hashCode() {
        return medicalInfo.hashCode();
    }
}
