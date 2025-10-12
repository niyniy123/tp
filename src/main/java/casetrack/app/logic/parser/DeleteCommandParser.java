package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.commands.Command;
import casetrack.app.logic.commands.DeleteNoteCommand;
import casetrack.app.logic.commands.DeletePatientCommand;
import casetrack.app.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates either a DeleteNoteCommand or DeletePatientCommand object
 * based on the arguments provided.
 */
public class DeleteCommandParser implements Parser<Command> {

    public static final String MESSAGE_INVALID_DELETE_FORMAT =
        "Expected 'delete note <PERSON_INDEX> <NOTE_INDEX>' or 'delete patient <INDEX>'";

    /**
     * Parses the given {@code String} of arguments and returns either a DeleteNoteCommand
     * or DeletePatientCommand object for execution.
     * @throws ParseException if the user input does not follow the expected format
     */
    public Command parse(String args) throws ParseException {
        final String trimmedArgs = args.trim();

        // To determine the command type
        if (trimmedArgs.startsWith(ParserUtil.NOTE_STRING + " ")) {
            // remove "note " prefix
            String remainingArgs = trimmedArgs.substring(ParserUtil.NOTE_STRING.length() + 1).trim();
            String[] parts = remainingArgs.split("\\s+");

            if (parts.length != 2) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        DeleteNoteCommand.MESSAGE_USAGE));
            }

            Index personIndex = ParserUtil.parseIndex(parts[0]);
            Index noteIndex = ParserUtil.parseIndex(parts[1]);
            return new DeleteNoteCommand(personIndex, noteIndex);

        } else if (trimmedArgs.startsWith(ParserUtil.PATIENT_STRING + " ")) {
            String remainingArgs = trimmedArgs.substring(ParserUtil.PATIENT_STRING.length() + 1).trim();

            Index index = ParserUtil.parseIndex(remainingArgs);
            return new DeletePatientCommand(index);

        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_INVALID_DELETE_FORMAT));
        }
    }
}
