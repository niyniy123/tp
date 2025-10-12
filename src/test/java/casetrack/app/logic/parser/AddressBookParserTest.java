package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static casetrack.app.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static casetrack.app.testutil.Assert.assertThrows;
import static casetrack.app.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import casetrack.app.logic.commands.AddCommand;
import casetrack.app.logic.commands.ClearCommand;
import casetrack.app.logic.commands.DeletePatientCommand;
import casetrack.app.logic.commands.EditCommand;
import casetrack.app.logic.commands.EditCommand.EditPersonDescriptor;
import casetrack.app.logic.commands.ExitCommand;
import casetrack.app.logic.commands.FindCommand;
import casetrack.app.logic.commands.HelpCommand;
import casetrack.app.logic.commands.ListCommand;
import casetrack.app.logic.commands.NoteCommand;
import casetrack.app.logic.parser.exceptions.ParseException;
import casetrack.app.model.person.NameContainsKeywordsPredicate;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Person;
import casetrack.app.testutil.EditPersonDescriptorBuilder;
import casetrack.app.testutil.PersonBuilder;
import casetrack.app.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeletePatientCommand command = (DeletePatientCommand) parser.parseCommand(
                DeletePatientCommand.COMMAND_WORD + " patient " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeletePatientCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        // Build a descriptor containing only fields that EditCommandParser supports via CLI
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(person.getName().fullName)
                .withPhone(person.getPhone().value)
                .withEmail(person.getEmail().value)
                .withAddress(person.getAddress().value)
                .withTags(person.getTags().stream().map(tag -> tag.tagName).toArray(String[]::new))
                .build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " name " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_note() throws Exception {
        Note note = new Note("Follow-up in 2 weeks");
        NoteCommand command = (NoteCommand) parser.parseCommand(NoteCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " t/Follow-up in 2 weeks");
        assertEquals(new NoteCommand(INDEX_FIRST_PERSON, note), command);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
