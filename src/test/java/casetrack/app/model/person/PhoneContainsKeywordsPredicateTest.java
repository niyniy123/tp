package casetrack.app.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import casetrack.app.testutil.PersonBuilder;

public class PhoneContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("123");
        List<String> secondPredicateKeywordList = Arrays.asList("123", "456");

        PhoneContainsKeywordsPredicate firstPredicate =
                new PhoneContainsKeywordsPredicate(firstPredicateKeywordList);
        PhoneContainsKeywordsPredicate secondPredicate =
                new PhoneContainsKeywordsPredicate(firstPredicateKeywordList);
        PhoneContainsKeywordsPredicate thirdPredicate =
                new PhoneContainsKeywordsPredicate(secondPredicateKeywordList);

        assertTrue(firstPredicate.equals(firstPredicate));

        assertTrue(firstPredicate.equals(secondPredicate));

        assertFalse(firstPredicate.equals(1));

        assertFalse(firstPredicate.equals(null));

        assertFalse(firstPredicate.equals(thirdPredicate));
    }

    @Test
    public void test_phoneContainsKeywords_returnsTrue() {
        PhoneContainsKeywordsPredicate predicate =
                new PhoneContainsKeywordsPredicate(Collections.singletonList("123"));
        assertTrue(predicate.test(new PersonBuilder().withPhone("12345678").build()));

        predicate = new PhoneContainsKeywordsPredicate(Arrays.asList("123", "456"));
        assertTrue(predicate.test(new PersonBuilder().withPhone("12345678").build()));

        predicate = new PhoneContainsKeywordsPredicate(Arrays.asList("456", "789"));
        assertTrue(predicate.test(new PersonBuilder().withPhone("12345678").build()));

        predicate = new PhoneContainsKeywordsPredicate(Arrays.asList("123", "456"));
        assertTrue(predicate.test(new PersonBuilder().withPhone("12345678").build()));

        predicate = new PhoneContainsKeywordsPredicate(Arrays.asList("234"));
        assertTrue(predicate.test(new PersonBuilder().withPhone("12345678").build()));
    }

    @Test
    public void test_phoneDoesNotContainKeywords_returnsFalse() {
        PhoneContainsKeywordsPredicate predicate =
                new PhoneContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withPhone("12345678").build()));

        predicate = new PhoneContainsKeywordsPredicate(Arrays.asList("999"));
        assertFalse(predicate.test(new PersonBuilder().withPhone("12345678").build()));

        predicate = new PhoneContainsKeywordsPredicate(Arrays.asList("Alice", "alice@email.com", "Main", "Street"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("12345678")
                .withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    public void test_partialPhoneKeywords() {
        // Partial keywords with special characters that don't match
        PhoneContainsKeywordsPredicate predicate =
                new PhoneContainsKeywordsPredicate(Arrays.asList("(123)", "12-34"));
        assertFalse(predicate.test(new PersonBuilder().withPhone("12345678").build()));

        // Partial match with country code prefix
        predicate = new PhoneContainsKeywordsPredicate(Arrays.asList("+65"));
        assertTrue(predicate.test(new PersonBuilder().withPhone("+65 12345678").build()));

        // Partial match without prefix in stored number
        predicate = new PhoneContainsKeywordsPredicate(Arrays.asList("+65"));
        assertFalse(predicate.test(new PersonBuilder().withPhone("65123456").build()));

        // Partial numeric match
        predicate = new PhoneContainsKeywordsPredicate(Arrays.asList("65"));
        assertTrue(predicate.test(new PersonBuilder().withPhone("65123456").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = Arrays.asList("123", "456");
        PhoneContainsKeywordsPredicate predicate = new PhoneContainsKeywordsPredicate(keywords);

        String expected = PhoneContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
