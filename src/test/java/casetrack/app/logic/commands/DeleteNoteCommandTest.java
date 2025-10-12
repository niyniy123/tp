package casetrack.app.logic.commands;

import static casetrack.app.logic.commands.CommandTestUtil.assertCommandFailure;
import static casetrack.app.logic.commands.CommandTestUtil.assertCommandSuccess;
import static casetrack.app.logic.commands.CommandTestUtil.showPersonAtIndex;
import static casetrack.app.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static casetrack.app.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.Messages;
import casetrack.app.model.Model;
import casetrack.app.model.ModelManager;
import casetrack.app.model.UserPrefs;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Person;
import casetrack.app.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for DeleteNoteCommand.
 */
public class DeleteNoteCommandTest {

    private Model model = new ModelManager();

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personWithNotes = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong West Ave 6, #01-111")
                .withTags("friends")
                .build();

        // add notes to the person
        Person personWithTwoNotes = personWithNotes
                .addNote(new Note("First note"))
                .addNote(new Note("Second note"));

        model.addPerson(personWithTwoNotes);

        Note noteToDelete = personWithTwoNotes.getNotes().get(0);
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteNoteCommand.MESSAGE_DELETE_NOTE_SUCCESS,
                personWithTwoNotes.getName().fullName, noteToDelete.value);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person expectedPerson = personWithTwoNotes.removeNote(0);
        expectedModel.setPerson(personWithTwoNotes, expectedPerson);

        assertCommandSuccess(deleteNoteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Person personWithNotes = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #01-111")
                .withTags("friends")
                .build();

        Person personWithOneNote = personWithNotes.addNote(new Note("Test note"));
        model.addPerson(personWithOneNote);

        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(outOfBoundIndex, INDEX_FIRST_PERSON);

        assertCommandFailure(deleteNoteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidNoteIndex_failure() {
        Person personWithNotes = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #08-111")
                .withTags("friends")
                .build();

        Person personWithOneNote = personWithNotes.addNote(new Note("Test note"));
        model.addPerson(personWithOneNote);

        Index outOfBoundNoteIndex = Index.fromOneBased(personWithOneNote.getNotes().size() + 1);
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(INDEX_FIRST_PERSON, outOfBoundNoteIndex);

        assertCommandFailure(deleteNoteCommand, model, DeleteNoteCommand.MESSAGE_INVALID_NOTE_INDEX);
    }

    @Test
    public void execute_personWithNoNotes_failure() {
        Person personWithoutNotes = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #01-111")
                .withTags("friends")
                .build();

        model.addPerson(personWithoutNotes);

        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON);

        assertCommandFailure(deleteNoteCommand, model, DeleteNoteCommand.MESSAGE_NO_NOTES);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        // Create a person with notes
        Person personWithNotes = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #01-111")
                .withTags("friends")
                .build();

        Person personWithTwoNotes = personWithNotes
                .addNote(new Note("First note"))
                .addNote(new Note("Second note"));

        model.addPerson(personWithTwoNotes);

        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Note noteToDelete = personWithTwoNotes.getNotes().get(0);
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteNoteCommand.MESSAGE_DELETE_NOTE_SUCCESS,
                personWithTwoNotes.getName().fullName, noteToDelete.value);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person expectedPerson = personWithTwoNotes.removeNote(0);
        expectedModel.setPerson(personWithTwoNotes, expectedPerson);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(deleteNoteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        Person personWithNotes = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #01-111")
                .withTags("friends")
                .build();

        Person personWithOneNote = personWithNotes.addNote(new Note("Test note"));
        model.addPerson(personWithOneNote);

        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(outOfBoundIndex, INDEX_FIRST_PERSON);

        assertCommandFailure(deleteNoteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteNoteCommand deleteFirstNoteCommand = new DeleteNoteCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON);
        DeleteNoteCommand deleteSecondNoteCommand = new DeleteNoteCommand(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON);
        DeleteNoteCommand deleteFirstNoteSecondIndexCommand = new DeleteNoteCommand(INDEX_FIRST_PERSON,
                INDEX_SECOND_PERSON);

        // same object - returns true
        assertTrue(deleteFirstNoteCommand.equals(deleteFirstNoteCommand));

        // same values - returns true
        DeleteNoteCommand deleteFirstNoteCommandCopy = new DeleteNoteCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON);
        assertTrue(deleteFirstNoteCommand.equals(deleteFirstNoteCommandCopy));

        // different types - returns false
        assertFalse(deleteFirstNoteCommand.equals(1));

        // null - returns false
        assertFalse(deleteFirstNoteCommand.equals(null));

        // different person index - returns false
        assertFalse(deleteFirstNoteCommand.equals(deleteSecondNoteCommand));

        // different note index - returns false
        assertFalse(deleteFirstNoteCommand.equals(deleteFirstNoteSecondIndexCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetPersonIndex = Index.fromOneBased(1);
        Index targetNoteIndex = Index.fromOneBased(1);
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(targetPersonIndex, targetNoteIndex);
        String expected = DeleteNoteCommand.class.getCanonicalName() + "{personIndex=" + targetPersonIndex
                + ", noteIndex=" + targetNoteIndex + "}";
        assertEquals(expected, deleteNoteCommand.toString());
    }
}
