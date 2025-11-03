package casetrack.app.logic.commands;

import static java.util.Objects.requireNonNull;

import casetrack.app.commons.core.index.Index;
import casetrack.app.commons.util.ToStringBuilder;
import casetrack.app.logic.Messages;
import casetrack.app.logic.commands.exceptions.CommandException;
import casetrack.app.model.Model;
import casetrack.app.model.person.Person;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeletePatientCommand extends DeleteCommand {

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the patient identified by the index number used in the displayed patient list.\n"
            + "Parameters: <PATIENT_INDEX> (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " patient 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Patient: %1$s";

    /**
     * Creates a DeletePatientCommand to delete the person at the target index.
     *
     * @param targetIndex The index of the person in the person list
     */
    public DeletePatientCommand(Index targetIndex) {
        super(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToDelete = getPersonByIndex(model, targetIndex);
        model.deletePerson(personToDelete);

        DetailPanelInstruction clearInstruction = new DetailPanelInstruction(null);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete)), clearInstruction);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeletePatientCommand)) {
            return false;
        }

        DeletePatientCommand otherDeleteCommand = (DeletePatientCommand) other;
        return targetIndex.equals(otherDeleteCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
