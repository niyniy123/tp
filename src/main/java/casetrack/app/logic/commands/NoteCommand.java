package casetrack.app.logic.commands;

import static casetrack.app.logic.parser.CliSyntax.PREFIX_NOTE_TEXT;
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
 * Adds a note to an existing person in the address book.
 */
public class NoteCommand extends Command {

    public static final String COMMAND_WORD = "note";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a note to a patient. "
            + "Parameters: INDEX " + PREFIX_NOTE_TEXT + "TEXT\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_NOTE_TEXT
            + "Follow-up in 2 weeks about housing support";

    public static final String MESSAGE_SUCCESS = "Note added for %1$s (%2$s): %3$s";

    private final Index targetIndex;
    private final Note note;

    /**
     * Creates a NoteCommand to add a note to a person identified by index.
     */
    public NoteCommand(Index targetIndex, Note note) {
        requireNonNull(targetIndex);
        requireNonNull(note);
        this.targetIndex = targetIndex;
        this.note = note;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person personToUpdate = findPerson(model);
        Person updatedPerson = personToUpdate.addNote(note);

        model.setPerson(personToUpdate, updatedPerson);
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                updatedPerson.getName().fullName,
                updatedPerson.getPhone().value,
                note.value), updatedPerson, false, false);
    }

    /**
     * Finds the person to update based on the provided index.
     */
    private Person findPerson(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return lastShownList.get(targetIndex.getZeroBased());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NoteCommand)) {
            return false;
        }

        NoteCommand otherNoteCommand = (NoteCommand) other;
        return targetIndex.equals(otherNoteCommand.targetIndex)
                && note.equals(otherNoteCommand.note);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("note", note)
                .toString();
    }
}
