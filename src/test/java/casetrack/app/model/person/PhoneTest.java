package casetrack.app.model.person;

import static casetrack.app.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("91")); // less than 3 numbers
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("9312 1534")); // spaces within digits
        assertFalse(Phone.isValidPhone("123456789012345678")); // more than 17 digits
        assertFalse(Phone.isValidPhone("+999 123456789012345678")); // more than 17 digits with country code

        // valid phone numbers
        assertTrue(Phone.isValidPhone("911")); // exactly 3 numbers
        assertTrue(Phone.isValidPhone("93121534"));
        assertTrue(Phone.isValidPhone("124293842033123")); // long phone numbers
        assertTrue(Phone.isValidPhone("12345678901234567")); // exactly 17 digits (maximum)
        assertTrue(Phone.isValidPhone("999 124293842033123")); // long phone numbers with country code
        assertTrue(Phone.isValidPhone("+999 124293842033123")); // long phone numbers with country code and +
        assertTrue(Phone.isValidPhone("+999124293842033123")); // phone numbers with no space before country code
        assertTrue(Phone.isValidPhone("+999 12345678901234567")); // country code with 17 digit number
    }

    @Test
    public void equals() {
        Phone phone = new Phone("999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("995")));
    }

    @Test
    public void equals_normalizedPhoneNumbers_returnsTrue() {
        // Test phone numbers with and without + sign are equal
        Phone phoneWithPlus = new Phone("+6591234567");
        Phone phoneWithoutPlus = new Phone("6591234567");
        assertTrue(phoneWithPlus.equals(phoneWithoutPlus));
        assertTrue(phoneWithoutPlus.equals(phoneWithPlus));

        // Test phone numbers with and without spaces are equal
        Phone phoneWithSpace = new Phone("+65 91234567");
        Phone phoneWithoutSpace = new Phone("+6591234567");
        assertTrue(phoneWithSpace.equals(phoneWithoutSpace));

        // Test all variations are equal
        Phone phone1 = new Phone("+65 91234567");
        Phone phone2 = new Phone("+6591234567");
        Phone phone3 = new Phone("65 91234567");
        Phone phone4 = new Phone("6591234567");

        assertTrue(phone1.equals(phone2));
        assertTrue(phone1.equals(phone3));
        assertTrue(phone1.equals(phone4));
        assertTrue(phone2.equals(phone3));
        assertTrue(phone2.equals(phone4));
        assertTrue(phone3.equals(phone4));

        // Test country code variations
        Phone withCountryCode = new Phone("65 12345678");
        Phone withCountryCodeNoSpace = new Phone("6512345678");
        assertTrue(withCountryCode.equals(withCountryCodeNoSpace));
    }

    @Test
    public void equals_differentPhoneNumbers_returnsFalse() {
        // Different digits should not be equal
        Phone phone1 = new Phone("+65 91234567");
        Phone phone2 = new Phone("+65 91234568");
        assertFalse(phone1.equals(phone2));

        // Different country codes should not be equal
        Phone phoneSG = new Phone("+65 91234567");
        Phone phoneUS = new Phone("+1 91234567");
        assertFalse(phoneSG.equals(phoneUS));

        // Completely different numbers
        Phone phoneA = new Phone("12345678");
        Phone phoneB = new Phone("87654321");
        assertFalse(phoneA.equals(phoneB));
    }

    @Test
    public void hashCode_normalizedPhoneNumbers_equal() {
        // Phone numbers that are equal should have the same hash code
        Phone phone1 = new Phone("+65 91234567");
        Phone phone2 = new Phone("+6591234567");
        Phone phone3 = new Phone("65 91234567");
        Phone phone4 = new Phone("6591234567");

        assertTrue(phone1.hashCode() == phone2.hashCode());
        assertTrue(phone1.hashCode() == phone3.hashCode());
        assertTrue(phone1.hashCode() == phone4.hashCode());
        assertTrue(phone2.hashCode() == phone3.hashCode());
        assertTrue(phone2.hashCode() == phone4.hashCode());
        assertTrue(phone3.hashCode() == phone4.hashCode());
    }

    @Test
    public void hashCode_differentPhoneNumbers_notEqual() {
        // Different phone numbers should (likely) have different hash codes
        Phone phone1 = new Phone("+65 91234567");
        Phone phone2 = new Phone("+65 91234568");

        assertFalse(phone1.hashCode() == phone2.hashCode());
    }
}
