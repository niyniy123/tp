package casetrack.app.model.person;

import static casetrack.app.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static casetrack.app.testutil.Assert.assertThrows;
import static casetrack.app.testutil.TypicalPersons.ALICE;
import static casetrack.app.testutil.TypicalPersons.BOB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import casetrack.app.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress() + ", tags=" + ALICE.getTags()
                + ", notes=" + ALICE.getNotes() + "}";
        assertEquals(expected, ALICE.toString());
    }

    @Test
    public void getNotes_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getNotes().remove(0));
    }

    @Test
    public void addNote() {
        Person person = new PersonBuilder().build();
        Note note = new Note("Test note");
        Person personWithNote = person.addNote(note);

        assertTrue(person.getNotes().isEmpty());

        // new person have the note
        assertFalse(personWithNote.getNotes().isEmpty());
        assertEquals(1, personWithNote.getNotes().size());
        assertEquals(note, personWithNote.getNotes().get(0));
    }

    @Test
    public void removeNote() {
        Note note1 = new Note("First note");
        Note note2 = new Note("Second note");
        Person person = new PersonBuilder()
                .withNotes(note1, note2)
                .build();

        Person personRemoved = person.removeNote(0);

        // original person should not be modified
        assertEquals(2, person.getNotes().size());
        assertEquals(note1, person.getNotes().get(0));
        assertEquals(note2, person.getNotes().get(1));

        // new person should have one note removed
        assertEquals(1, personRemoved.getNotes().size());
        assertEquals(note2, personRemoved.getNotes().get(0));
    }

    @Test
    public void removeNote_middleNote() {
        Note note1 = new Note("First note");
        Note note2 = new Note("Second note");
        Note note3 = new Note("Third note");
        Person person = new PersonBuilder()
                .withNotes(note1, note2, note3)
                .build();

        // remove middle note
        Person personRemoved = person.removeNote(1);

        // original person should not be modified
        assertEquals(3, person.getNotes().size());

        assertEquals(2, personRemoved.getNotes().size());
        assertEquals(note1, personRemoved.getNotes().get(0));
        assertEquals(note3, personRemoved.getNotes().get(1));
    }
}
