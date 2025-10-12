package casetrack.app.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import casetrack.app.commons.exceptions.IllegalValueException;
import casetrack.app.model.person.Address;
import casetrack.app.model.person.Email;
import casetrack.app.model.person.Income;
import casetrack.app.model.person.MedicalInfo;
import casetrack.app.model.person.Name;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Person;
import casetrack.app.model.person.Phone;
import casetrack.app.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String income;
    private final String medicalInfo; // optional
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<String> notes = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name,
            @JsonProperty("phone") String phone, @JsonProperty("email") String email,
            @JsonProperty("address") String address, @JsonProperty("income") String income,
            @JsonProperty("medicalInfo") String medicalInfo,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("notes") List<String> notes) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.income = income;
        this.medicalInfo = medicalInfo;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (notes != null) {
            this.notes.addAll(notes);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        income = source.getIncome().toString();
        medicalInfo = source.getMedicalInfo() == null ? null : source.getMedicalInfo().toString();
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        notes.addAll(source.getNotes().stream()
                .map(note -> note.value)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        final List<Note> personNotes = new ArrayList<>();
        for (String noteValue : notes) {
            if (Note.isValidNote(noteValue)) {
                personNotes.add(new Note(noteValue));
            }
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        if (income == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Income.class.getSimpleName()));
        }
        if (!Income.isValidIncome(income)) {
            throw new IllegalValueException(Income.MESSAGE_CONSTRAINTS);
        }
        final Income modelIncome = new Income(income);

        final MedicalInfo modelMedicalInfo;
        if (medicalInfo == null) {
            modelMedicalInfo = null;
        } else {
            if (!MedicalInfo.isValidMedicalInfo(medicalInfo)) {
                throw new IllegalValueException(MedicalInfo.MESSAGE_CONSTRAINTS);
            }
            modelMedicalInfo = new MedicalInfo(medicalInfo);
        }

        final Set<Tag> modelTags = new HashSet<>(personTags);
        return new Person(modelName, modelPhone, modelEmail, modelAddress, modelIncome, modelMedicalInfo,
                modelTags, personNotes);
    }

}
