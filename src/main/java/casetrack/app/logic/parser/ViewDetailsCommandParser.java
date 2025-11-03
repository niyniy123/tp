package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.commands.Command;
import casetrack.app.logic.commands.ViewDetailsCommand;
import casetrack.app.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a {@code ViewDetailsCommand} object.
 */
public class ViewDetailsCommandParser implements Parser<Command> {

    public static final String MESSAGE_INVALID_VIEW_DETAILS_FORMAT =
        "Expected 'view <PATIENT_INDEX>'";

    /**
     * Parses the given {@code String} of arguments and returns a
     * {@code ViewDetailsCommand} for execution.
     *
     * @throws ParseException if the user input does not follow the expected format
     */
    public Command parse(String args) throws ParseException {
        if (args == "") {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewDetailsCommand.MESSAGE_USAGE));
        }

        try {
            Index index = ParserUtil.parseIndex(args);
            return new ViewDetailsCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(String.format(pe.getMessage() + "\n%1$s", ViewDetailsCommand.MESSAGE_USAGE));
        }
    }
}
