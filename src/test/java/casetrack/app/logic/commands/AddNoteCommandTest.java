package casetrack.app.logic.commands;

import static casetrack.app.logic.commands.CommandTestUtil.assertCommandFailure;
import static casetrack.app.logic.commands.CommandTestUtil.assertCommandSuccess;
import static casetrack.app.testutil.Assert.assertThrows;
import static casetrack.app.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static casetrack.app.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static casetrack.app.testutil.TypicalPersons.getTypicalAddressBook;
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

/**
 * Contains integration tests (interaction with the Model) and unit tests for NoteCommand.
 */
public class AddNoteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        Note validNote = new Note("Valid note");
        assertThrows(NullPointerException.class, () -> new NoteCommand((Index) null, validNote));
    }

    @Test
    public void constructor_nullNote_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new NoteCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToAddNoteTo = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Note noteToAdd = new Note("Follow-up in 2 weeks");
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, noteToAdd);

        Person expectedPerson = personToAddNoteTo.addNote(noteToAdd);
        String expectedMessage = String.format(NoteCommand.MESSAGE_SUCCESS,
                expectedPerson.getName().fullName,
                expectedPerson.getPhone().value,
                noteToAdd.value);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToAddNoteTo, expectedPerson);

        CommandResult expectedResult = new CommandResult(expectedMessage, expectedPerson, false, false);
        assertCommandSuccess(noteCommand, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Note noteToAdd = new Note("Follow-up in 2 weeks");
        NoteCommand noteCommand = new NoteCommand(outOfBoundIndex, noteToAdd);

        assertCommandFailure(noteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        Note note1 = new Note("First note");
        Note note2 = new Note("Second note");

        NoteCommand addNoteFirstCommand = new NoteCommand(INDEX_FIRST_PERSON, note1);
        NoteCommand addNoteSecondCommand = new NoteCommand(INDEX_SECOND_PERSON, note1);

        // same object -> returns true
        assertTrue(addNoteFirstCommand.equals(addNoteFirstCommand));

        // same values -> returns true
        NoteCommand addNoteFirstCommandCopy = new NoteCommand(INDEX_FIRST_PERSON, note1);
        assertTrue(addNoteFirstCommand.equals(addNoteFirstCommandCopy));

        // different types -> returns false
        assertFalse(addNoteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(addNoteFirstCommand.equals(null));

        // different person index -> returns false
        assertFalse(addNoteFirstCommand.equals(addNoteSecondCommand));

        // different note -> returns false
        NoteCommand addNoteDifferentNoteCommand = new NoteCommand(INDEX_FIRST_PERSON, note2);
        assertFalse(addNoteFirstCommand.equals(addNoteDifferentNoteCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        Note note = new Note("Test note");
        NoteCommand noteCommand = new NoteCommand(targetIndex, note);
        String expected = "casetrack.app.logic.commands.NoteCommand{targetIndex=" + targetIndex
                + ", note=" + note + "}";
        assertEquals(expected, noteCommand.toString());
    }
}
