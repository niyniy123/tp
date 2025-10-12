package casetrack.app.testutil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import casetrack.app.model.person.Address;
import casetrack.app.model.person.Email;
import casetrack.app.model.person.Income;
import casetrack.app.model.person.MedicalInfo;
import casetrack.app.model.person.Name;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Person;
import casetrack.app.model.person.Phone;
import casetrack.app.model.tag.Tag;
import casetrack.app.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";
    public static final String DEFAULT_INCOME = "0";

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Income income;
    private MedicalInfo medicalInfo;
    private Set<Tag> tags;
    private List<Note> notes;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        income = new Income(DEFAULT_INCOME);
        medicalInfo = null;
        tags = new HashSet<>();
        notes = new ArrayList<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        income = personToCopy.getIncome();
        medicalInfo = personToCopy.getMedicalInfo();
        tags = new HashSet<>(personToCopy.getTags());
        notes = new ArrayList<>(personToCopy.getNotes());
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the {@code Notes} of the {@code Person} that we are building.
     */
    public PersonBuilder withNotes(Note... notes) {
        this.notes.clear();
        for (Note note : notes) {
            this.notes.add(note);
        }
        return this;
    }

    /**
     * Sets the {@code Income} of the {@code Person} that we are building.
     */
    public PersonBuilder withIncome(String income) {
        this.income = new Income(income);
        return this;
    }

    /**
     * Sets the {@code MedicalInfo} of the {@code Person} that we are building, to null.
     */
    public PersonBuilder withMedicalInfo() {
        this.medicalInfo = null;
        return this;
    }

    /**
     * Sets the {@code MedicalInfo} of the {@code Person} that we are building.
     */
    public PersonBuilder withMedicalInfo(String medicalInfo) {
        this.medicalInfo = new MedicalInfo(medicalInfo);
        return this;
    }

    /**
     * Builds a Person object with the specified details.
     * @return a Person object with the configuration
     */
    public Person build() {
        if (notes.isEmpty()) {
            return new Person(name, phone, email, address, income, medicalInfo, tags);
        } else {
            return new Person(name, phone, email, address, income, medicalInfo, tags, notes);
        }
    }

}
