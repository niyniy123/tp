package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseFailure;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static casetrack.app.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import casetrack.app.logic.commands.ViewDetailsCommand;

public class ViewDetailsCommandParserTest {

    private ViewDetailsCommandParser parser = new ViewDetailsCommandParser();

    @Test
    public void parse_validPatientArgs_returnsViewDetailsCommand() {
        assertParseSuccess(parser, "1", new ViewDetailsCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_patientWithInvalidFormat_throwsParseException() {
        assertParseFailure(parser, "-1",
                String.format(ParserUtil.MESSAGE_INVALID_INDEX + "\n%1$s", ViewDetailsCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, "",
                String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, ViewDetailsCommand.MESSAGE_USAGE));
    }
}
