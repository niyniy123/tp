package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_NOTE_TEXT;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.commands.EditNoteCommand;
import casetrack.app.logic.parser.exceptions.ParseException;
import casetrack.app.model.person.Note;

/**
 * Parses input arguments and creates an EditNoteCommand object for editing a note.
 */
public class EditNoteCommandParser implements Parser<EditNoteCommand> {

    private static final int EXPECTED_INDICES_COUNT = 2;

    /**
     * Parses the given {@code String} of arguments and returns an EditNoteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditNoteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(" " + args, PREFIX_NOTE_TEXT);

        // Preamble should contain two indices: PATIENT_INDEX and NOTE_INDEX
        String preamble = argMultimap.getPreamble().trim();
        String[] parts = preamble.split("\\s+");

        if (parts.length != EXPECTED_INDICES_COUNT) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditNoteCommand.MESSAGE_USAGE));
        }

        if (!argMultimap.getValue(PREFIX_NOTE_TEXT).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditNoteCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NOTE_TEXT);
        Index patientIndex;
        Index noteIndex;
        Note newNote;

        try {
            patientIndex = ParserUtil.parseIndex(parts[0]);
            noteIndex = ParserUtil.parseIndex(parts[1]);
        } catch (ParseException pe) {
            throw new ParseException(String.format(pe.getMessage() + "\n%1$s", EditNoteCommand.MESSAGE_USAGE));
        }

        newNote = ParserUtil.parseNote(argMultimap.getValue(PREFIX_NOTE_TEXT).get());

        return new EditNoteCommand(patientIndex, noteIndex, newNote);
    }
}

