package casetrack.app.logic.commands;

import static casetrack.app.logic.commands.CommandTestUtil.DESC_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.DESC_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import casetrack.app.logic.commands.EditCommand.EditPersonDescriptor;
import casetrack.app.testutil.EditPersonDescriptorBuilder;

public class EditPersonDescriptorTest {

    @Test
    public void isAnyFieldEdited_and_isMedicalInfoEdited() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        // initially false
        assertFalse(descriptor.isAnyFieldEdited());
        assertFalse(descriptor.isMedicalInfoEdited());
        // set name -> now edited
        descriptor.setName(new casetrack.app.model.person.Name("Alice"));
        assertTrue(descriptor.isAnyFieldEdited());
        // medical still not edited
        assertFalse(descriptor.isMedicalInfoEdited());
        // getMedicalInfo should be empty until explicitly edited
        assertTrue(descriptor.getMedicalInfo().isEmpty());
        // set medical info -> flag set and value present
        descriptor.setMedicalInfo(new casetrack.app.model.person.MedicalInfo("Asthma"));
        assertTrue(descriptor.isAnyFieldEdited());
        assertTrue(descriptor.isMedicalInfoEdited());
        assertTrue(descriptor.getMedicalInfo().isPresent());
    }

    @Test
    public void getTags_returnsUnmodifiableSet() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        java.util.Set<casetrack.app.model.tag.Tag> tags = new java.util.HashSet<>();
        tags.add(new casetrack.app.model.tag.Tag("friends"));
        descriptor.setTags(tags);
        java.util.Set<casetrack.app.model.tag.Tag> returned = descriptor.getTags().get();
        casetrack.app.testutil.Assert.assertThrows(
                UnsupportedOperationException.class, () -> returned.add(new casetrack.app.model.tag.Tag("new")));
    }

    @Test
    public void equals() {
        // same values -> returns true
        EditPersonDescriptor descriptorWithSameValues = new EditPersonDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditPersonDescriptor editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withPhone(VALID_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different email -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different address -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different tags -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void toStringMethod() {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        String expected = EditPersonDescriptor.class.getCanonicalName() + "{name="
                + editPersonDescriptor.getName().orElse(null) + ", phone="
                + editPersonDescriptor.getPhone().orElse(null) + ", email="
                + editPersonDescriptor.getEmail().orElse(null) + ", address="
                + editPersonDescriptor.getAddress().orElse(null) + ", tags="
                + editPersonDescriptor.getTags().orElse(null) + ", income="
                + editPersonDescriptor.getIncome().orElse(null) + ", medicalInfo="
                + editPersonDescriptor.getMedicalInfo().orElse(null) + "}";
        assertEquals(expected, editPersonDescriptor.toString());
    }
}
