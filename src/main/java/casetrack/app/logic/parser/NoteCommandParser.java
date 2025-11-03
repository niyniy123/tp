package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_NOTE_TEXT;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.commands.NoteCommand;
import casetrack.app.logic.parser.exceptions.ParseException;
import casetrack.app.model.person.Note;

/**
 * Parses input arguments and creates a new NoteCommand object
 */
public class NoteCommandParser implements Parser<NoteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the NoteCommand
     * and returns a NoteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public NoteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NOTE_TEXT);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NOTE_TEXT);

        // Check if note text is present
        if (!argMultimap.getValue(PREFIX_NOTE_TEXT).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE));
        }

        // Check if index is present in preamble
        if (argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE));
        }

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(pe.getMessage() + "\n%1$s", NoteCommand.MESSAGE_USAGE));
        }

        Note note = ParserUtil.parseNote(argMultimap.getValue(PREFIX_NOTE_TEXT).get());
        return new NoteCommand(index, note);
    }
}

