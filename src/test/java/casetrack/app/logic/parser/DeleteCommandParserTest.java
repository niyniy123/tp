package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseFailure;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static casetrack.app.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static casetrack.app.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import casetrack.app.logic.commands.DeleteNoteCommand;
import casetrack.app.logic.commands.DeletePatientCommand;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeletePatientCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeletePatientCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validPatientArgs_returnsDeletePatientCommand() {
        assertParseSuccess(parser, "patient 1", new DeletePatientCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_validNoteArgs_returnsDeleteNoteCommand() {
        assertParseSuccess(parser, "note 1 2", new DeleteNoteCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
    }

    @Test
    public void parse_noteWithInvalidFormat_throwsParseException() {
        assertParseFailure(parser, "note 1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteNoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noteWithWrongArgs_throwsParseException() {
        assertParseFailure(parser, "note 1 2 3",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteNoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommandParser.MESSAGE_INVALID_DELETE_FORMAT));
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommandParser.MESSAGE_INVALID_DELETE_FORMAT));
    }
}
