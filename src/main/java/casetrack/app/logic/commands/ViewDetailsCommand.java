package casetrack.app.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import casetrack.app.commons.core.index.Index;
import casetrack.app.commons.util.ToStringBuilder;
import casetrack.app.logic.Messages;
import casetrack.app.logic.commands.exceptions.CommandException;
import casetrack.app.model.Model;
import casetrack.app.model.person.Person;

/**
 * Views details for a person identified using it's displayed index from the address book.
 */
public class ViewDetailsCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows details for the patient identified by the index number used in the displayed patient list.\n"
            + "Parameters: <PATIENT_INDEX> (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_VIEW_DETAILS_SUCCESS = "Viewing details for patient: %1$s";

    private final Index targetIndex;

    public ViewDetailsCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person detailTarget = lastShownList.get(targetIndex.getZeroBased());
        return new CommandResult(
            String.format(MESSAGE_VIEW_DETAILS_SUCCESS, Messages.format(detailTarget)), detailTarget, false, false);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ViewDetailsCommand)) {
            return false;
        }

        ViewDetailsCommand otherViewCommand = (ViewDetailsCommand) other;
        return targetIndex.equals(otherViewCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
