package casetrack.app.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import casetrack.app.commons.core.index.Index;
import casetrack.app.commons.util.ToStringBuilder;
import casetrack.app.logic.Messages;
import casetrack.app.logic.commands.exceptions.CommandException;
import casetrack.app.model.Model;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Person;

/**
 * Deletes a note from a person identified using its displayed index from the address book.
 */
public class DeleteNoteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a note from a person identified by the index number used in the displayed person list.\n"
            + "Parameters: note <PERSON_INDEX> <NOTE_INDEX>\n"
            + "Example: " + COMMAND_WORD + " note 1 2";

    public static final String MESSAGE_DELETE_NOTE_SUCCESS = "Deleted Note from %1$s: %2$s";
    public static final String MESSAGE_INVALID_NOTE_INDEX = "The note index provided is invalid.";
    public static final String MESSAGE_NO_NOTES = "This person has no notes to delete.";

    private final Index personIndex;
    private final Index noteIndex;

    /**
     * Creates a DeleteNoteCommand to delete a note from a person.
     *
     * @param personIndex The index of the person in the filtered person list
     * @param noteIndex The index of the note to delete from the person's notes
     */
    public DeleteNoteCommand(Index personIndex, Index noteIndex) {
        this.personIndex = personIndex;
        this.noteIndex = noteIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (personIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(personIndex.getZeroBased());
        List<Note> notes = personToUpdate.getNotes();

        if (notes.isEmpty()) {
            throw new CommandException(MESSAGE_NO_NOTES);
        }

        if (noteIndex.getZeroBased() >= notes.size()) {
            throw new CommandException(MESSAGE_INVALID_NOTE_INDEX);
        }

        Note noteToDelete = notes.get(noteIndex.getZeroBased());
        Person updatedPerson = personToUpdate.removeNote(noteIndex.getZeroBased());
        model.setPerson(personToUpdate, updatedPerson);
        return new CommandResult(String.format(MESSAGE_DELETE_NOTE_SUCCESS,
                personToUpdate.getName().fullName, noteToDelete.value));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteNoteCommand)) {
            return false;
        }

        DeleteNoteCommand otherDeleteNoteCommand = (DeleteNoteCommand) other;
        return personIndex.equals(otherDeleteNoteCommand.personIndex)
                && noteIndex.equals(otherDeleteNoteCommand.noteIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("personIndex", personIndex)
                .add("noteIndex", noteIndex)
                .toString();
    }
}
