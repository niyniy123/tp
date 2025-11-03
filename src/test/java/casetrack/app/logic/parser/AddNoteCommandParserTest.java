package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_NOTE_TEXT;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseFailure;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static casetrack.app.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import casetrack.app.logic.commands.NoteCommand;
import casetrack.app.model.person.Note;

public class AddNoteCommandParserTest {

    private NoteCommandParser parser = new NoteCommandParser();

    @Test
    public void parse_validArgs_returnsNoteCommand() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_NOTE_TEXT + "Follow-up in 2 weeks";
        Note expectedNote = new Note("Follow-up in 2 weeks");
        NoteCommand expectedCommand = new NoteCommand(INDEX_FIRST_PERSON, expectedNote);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingNoteText_throwsParseException() {
        String userInput = "1";
        assertParseFailure(parser, userInput, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyNoteText_throwsParseException() {
        String userInput = "1 " + PREFIX_NOTE_TEXT;
        assertParseFailure(parser, userInput, Note.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidNoteText_throwsParseException() {
        String userInput = "1 " + PREFIX_NOTE_TEXT + "   "; // whitespace only
        assertParseFailure(parser, userInput, Note.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String userInput = "a " + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput,
                ParserUtil.MESSAGE_INVALID_INDEX + "\n" + NoteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_negativeIndex_throwsParseException() {
        String userInput = "-1 " + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput,
                ParserUtil.MESSAGE_INVALID_INDEX + "\n" + NoteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_zeroIndex_throwsParseException() {
        String userInput = "0 " + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput,
                ParserUtil.MESSAGE_INVALID_INDEX + "\n" + NoteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        String userInput = " " + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateNotePrefixes_throwsParseException() {
        String userInput = "1 " + PREFIX_NOTE_TEXT + "First note " + PREFIX_NOTE_TEXT + "Second note";
        assertParseFailure(parser, userInput, "Multiple values specified for the following single-valued field(s): t/");
    }

    @Test
    public void parse_validArgsWithExtraSpaces_returnsNoteCommand() {
        String userInput = "  1   " + PREFIX_NOTE_TEXT + "  Follow-up in 2 weeks  ";
        Note expectedNote = new Note("Follow-up in 2 weeks");
        NoteCommand expectedCommand = new NoteCommand(INDEX_FIRST_PERSON, expectedNote);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_validArgsWithSpecialCharacters_returnsNoteCommand() {
        String userInput = "1 " + PREFIX_NOTE_TEXT + "Patient needs follow-up @clinic #urgent!";
        Note expectedNote = new Note("Patient needs follow-up @clinic #urgent!");
        NoteCommand expectedCommand = new NoteCommand(INDEX_FIRST_PERSON, expectedNote);
        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
