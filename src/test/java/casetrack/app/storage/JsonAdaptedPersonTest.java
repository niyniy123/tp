package casetrack.app.storage;

import static casetrack.app.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static casetrack.app.testutil.Assert.assertThrows;
import static casetrack.app.testutil.TypicalPersons.BENSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import casetrack.app.commons.exceptions.IllegalValueException;
import casetrack.app.model.person.Address;
import casetrack.app.model.person.Email;
import casetrack.app.model.person.Name;
import casetrack.app.model.person.Person;
import casetrack.app.model.person.Phone;
import casetrack.app.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final String VALID_INCOME = BENSON.getIncome().toString();
    private static final String VALID_MEDICAL_INFO = "Asthma";
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());
    private static final List<String> VALID_NOTES = new ArrayList<>();

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(INVALID_NAME, VALID_PHONE, VALID_EMAIL,
                        VALID_ADDRESS, VALID_INCOME, VALID_MEDICAL_INFO, VALID_TAGS, VALID_NOTES);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_PHONE,
                VALID_EMAIL, VALID_ADDRESS, VALID_INCOME, VALID_MEDICAL_INFO, VALID_TAGS, VALID_NOTES);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_EMAIL,
                        VALID_ADDRESS, VALID_INCOME, VALID_MEDICAL_INFO, VALID_TAGS, VALID_NOTES);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null,
                VALID_EMAIL, VALID_ADDRESS, VALID_INCOME, VALID_MEDICAL_INFO, VALID_TAGS, VALID_NOTES);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, INVALID_EMAIL,
                        VALID_ADDRESS, VALID_INCOME, VALID_MEDICAL_INFO, VALID_TAGS, VALID_NOTES);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE,
                null, VALID_ADDRESS, VALID_INCOME, VALID_MEDICAL_INFO, VALID_TAGS, VALID_NOTES);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                        INVALID_ADDRESS, VALID_INCOME, VALID_MEDICAL_INFO, VALID_TAGS, VALID_NOTES);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE,
                VALID_EMAIL, null, VALID_INCOME, VALID_MEDICAL_INFO, VALID_TAGS, VALID_NOTES);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                        VALID_ADDRESS, VALID_INCOME, VALID_MEDICAL_INFO, invalidTags, VALID_NOTES);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_validNotes_returnsPerson() throws Exception {
        List<String> validNotes = new ArrayList<>();
        validNotes.add("Follow-up in 2 weeks");
        validNotes.add("Patient mentioned financial difficulties");

        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_INCOME, VALID_MEDICAL_INFO, VALID_TAGS, validNotes);

        Person modelPerson = person.toModelType();
        assertEquals(2, modelPerson.getNotes().size());
        assertTrue(modelPerson.getNotes().stream().anyMatch(note -> note.value.equals("Follow-up in 2 weeks")));
        assertTrue(modelPerson.getNotes().stream().anyMatch(note ->
                note.value.equals("Patient mentioned financial difficulties")));
    }

    @Test
    public void toModelType_emptyNotes_returnsPerson() throws Exception {
        List<String> emptyNotes = new ArrayList<>();

        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_INCOME, VALID_MEDICAL_INFO, VALID_TAGS, emptyNotes);

        Person modelPerson = person.toModelType();
        assertTrue(modelPerson.getNotes().isEmpty());
    }

    @Test
    public void toModelType_nullNotes_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_INCOME, VALID_MEDICAL_INFO, VALID_TAGS, null);

        Person modelPerson = person.toModelType();
        assertTrue(modelPerson.getNotes().isEmpty());
    }

    @Test
    public void toModelType_invalidNotes_skipsInvalidNotes() throws Exception {
        List<String> notesWithInvalid = new ArrayList<>();
        notesWithInvalid.add("Valid note");
        notesWithInvalid.add("   "); // Invalid note (whitespace only)
        notesWithInvalid.add("Another valid note");
        notesWithInvalid.add(""); // Invalid note (empty)

        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_INCOME, VALID_MEDICAL_INFO, VALID_TAGS, notesWithInvalid);

        Person modelPerson = person.toModelType();

        // Should only have the 2 valid notes
        assertEquals(2, modelPerson.getNotes().size());
        assertTrue(modelPerson.getNotes().stream().anyMatch(note -> note.value.equals("Valid note")));
        assertTrue(modelPerson.getNotes().stream().anyMatch(note -> note.value.equals("Another valid note")));
    }

    @Test
    public void constructor_personWithNotes_success() throws Exception {
        // Create a person with notes using PersonBuilder (need to extend it first)
        Person personWithNotes = new PersonBuilder(BENSON).build();
        personWithNotes = personWithNotes.addNote(new casetrack.app.model.person.Note("Test note 1"));
        personWithNotes = personWithNotes.addNote(new casetrack.app.model.person.Note("Test note 2"));

        JsonAdaptedPerson jsonPerson = new JsonAdaptedPerson(personWithNotes);

        // Verify notes are properly stored
        assertEquals(2, jsonPerson.toModelType().getNotes().size());
    }

    @Test
    public void toModelType_notesWithSpecialCharacters_success() throws Exception {
        List<String> specialNotes = new ArrayList<>();
        specialNotes.add("Patient needs follow-up @clinic #urgent!");
        specialNotes.add("Medication: 50mg/day (morning & evening)");
        specialNotes.add("Contact: john.doe@email.com or +65-1234-5678");

        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_INCOME, VALID_MEDICAL_INFO, VALID_TAGS, specialNotes);

        Person modelPerson = person.toModelType();
        assertEquals(3, modelPerson.getNotes().size());
        assertTrue(modelPerson.getNotes().stream().anyMatch(note ->
            note.value.equals("Patient needs follow-up @clinic #urgent!")));
        assertTrue(modelPerson.getNotes().stream().anyMatch(note ->
            note.value.equals("Medication: 50mg/day (morning & evening)")));
        assertTrue(modelPerson.getNotes().stream().anyMatch(note ->
            note.value.equals("Contact: john.doe@email.com or +65-1234-5678")));
    }

}
