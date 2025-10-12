package casetrack.app.model.person;

import static casetrack.app.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import casetrack.app.commons.util.ToStringBuilder;
import casetrack.app.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final List<Note> notes = new ArrayList<>();

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
    }

    /**
     * Constructor with notes.
     */
    public Person(Name name, Phone phone, Email email, Address address,
            Set<Tag> tags, List<Note> notes) {
        requireAllNonNull(name, phone, email, address, tags, notes);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.notes.addAll(notes);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an immutable note list, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public List<Note> getNotes() {
        return Collections.unmodifiableList(notes);
    }

    /**
     * Returns a new Person with the given note added to the notes list.
     */
    public Person addNote(Note note) {
        List<Note> updatedNotes = new ArrayList<>(notes);
        updatedNotes.add(note);
        return new Person(name, phone, email, address, new HashSet<>(tags), updatedNotes);
    }

    /**
     * Returns a new Person with the note at the specified index removed from the notes list.
     */
    public Person removeNote(int noteIndex) {
        List<Note> updatedNotes = new ArrayList<>(notes);
        updatedNotes.remove(noteIndex);
        return new Person(name, phone, email, address, new HashSet<>(tags), updatedNotes);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags)
                && notes.equals(otherPerson.notes);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, notes);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("notes", notes)
                .toString();
    }

}
