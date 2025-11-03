---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# CaseTrack Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S1-CS2103-F12-3/tp/blob/master/src/main/java/casetrack/app/Main.java) and [`MainApp`](https://github.com/AY2526S1-CS2103-F12-3/tp/blob/master/src/main/java/casetrack/app/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S1-CS2103-F12-3/tp/blob/master/src/main/java/casetrack/app/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S1-CS2103-F12-3/tp/blob/master/src/main/java/casetrack/app/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S1-CS2103-F12-3/tp/blob/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S1-CS2103-F12-3/tp/blob/master/src/main/java/casetrack/app/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete patient 1")` API call as an example.

<puml src="diagrams/DeletePatientSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete patient 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeletePatientCommand` or `DeleteNoteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a patient or a note).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2526S1-CS2103-F12-3/tp/blob/master/src/main/java/casetrack/app/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the CaseTrack data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S1-CS2103-F12-3/tp/blob/master/src/main/java/casetrack/app/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both CaseTrack data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `casetrack.app.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Add new patient feature

#### Implementation

The add patient feature adds a patient to the CaseTrack.

<puml src="diagrams/AddPatientSequenceDiagram.puml" alt="Interactions inside the Logic Component for the `add n/Alice ... i/5000 mi/diabetes` Command" />

How add patient works:

1. `AddressBookParser` creates a `AddCommandParser` to parse the add command.
2. `AddCommandParser` identifies the command as a patient addition based on the "add" keyword.
3. `AddCommand` is created with the patient details.
4. The command validates the patient details and creates a new patient object.
5. The patient is added to the CaseTrack and the UI is updated.

### Search feature

#### Implementation

The search feature finds patients by name, phone number, email, or tags using specialized predicate classes.

<puml src="diagrams/SearchSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `search number +6591234567` Command" />

How the search works:

1. `AddressBookParser` creates a `FindCommandParser` to parse the search command.
2. `FindCommandParser` extracts the search type and keywords.
3. An appropriate predicate is created (e.g., `PhoneContainsKeywordsPredicate` for phone searches).
4. `FindCommand` updates the filtered patient list using the predicate.
5. The predicate validates keywords and matches against patient's phone (including country codes).

### Add new note feature

#### Implementation

The add note feature adds a note to a patient.

<puml src="diagrams/NoteSequenceDiagram.puml" alt="Interactions inside the Logic Component for the `add note 1 2` Command" />

How add note works:

1. `AddressBookParser` creates a `NoteCommandParser` to parse the add command.
2. `NoteCommandParser` identifies the command as a note addition based on the "note" keyword.
3. `NoteCommand` is created with the patient index and note text.
4. The command retrieves the patient and validates that the note text is not empty.
5. The note is added to the patient's list of notes using `Person#addNote()`.
6. The patient is updated back to the model and the UI is updated.

### Delete patient / note feature

#### Implementation

The delete feature supports two types of delete operations: deleting patients and deleting notes from patients.

**Delete Patient Command**

The `DeletePatientCommand` removes a patient from the CaseTrack.

<puml src="diagrams/DeletePatientSequenceDiagram.puml" alt="Interactions inside the Logic Component for the `delete patient 1` Command" />

How delete patient works:

1. `AddressBookParser` creates a `DeleteCommandParser` to parse the delete command.
2. `DeleteCommandParser` identifies the command as a patient deletion based on the "patient" keyword.
3. `DeletePatientCommand` is created with the patient index.
4. The command retrieves the patient from the filtered list and calls `Model#deletePerson()`.
5. The patient is removed from the CaseTrack and the UI is updated.

**Delete Note Command**

The `DeleteNoteCommand` removes a specific note from a patient's record.

<puml src="diagrams/DeleteNoteSequenceDiagram.puml" alt="Interactions inside the Logic Component for the `delete note 1 2` Command" />

How delete note works:

1. `AddressBookParser` creates a `DeleteCommandParser` to parse the delete command.
2. `DeleteCommandParser` identifies the command as a note deletion based on the "note" keyword.
3. `DeleteNoteCommand` is created with both patient and note index.
4. The command retrieves the patient and validates that the note index is valid.
5. The note is removed from the patient's list of notes using `Person#removeNote()`.
6. The patient is updated back to the model and the UI is updated.


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Tech-savvy social workers in hospitals (with no access to patient information from the hospital database)
* has a need to manage a significant number of patients
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: quickly work with large amounts of patient information during sessions or home visits,
optimized for fast CLI interactions.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                 | So that I can…​                                                        |
|----------|--------------------------------------------|------------------------------|------------------------------------------------------------------------|
| `* * *`  | User                                        | add patients with essential details (name, phone, address, income, medical info) | record their data for future reference and case management |
| `* * *`  | User                                        | list all patients at once    | browse patient records easily and get an overview of my caseload      |
| `* * *`  | User                                        | delete patient records        | clean up patient records when they are no longer relevant or cases are closed |
| `* * *`  | New User                                    | see usage instructions and command help | refer to instructions when I forget how to use the app |
| `* *`     | Social Worker                               | filter patients by a single attribute (name, number, email, or tag) | get information about my patients quickly during sessions |
| `* *`     | Social Worker                               | take quick notes during or right after a session, even with incomplete data | capture important context immediately and avoid forgetting key details later |
| `* *`     | Social Worker                               | enter partial patient details and still retrieve useful results | still access key patient information even when the data I have is incomplete |
| `* *`     | Social Worker                               | tag and categorize patients based on needs | prioritize cases and follow up more systematically |
| `* *`     | Social Worker                               | link related patient records  | see family connections and related cases                               |
| `* *`     | Social Worker                               | quickly copy information out of the application | work effortlessly with other systems and reports |
| `* *`     | Social Worker                               | pin frequently accessed patient records | decrease the amount of time spent searching for cases I am working on |
| `* *`     | Social Worker                               | search across all patient notes and records by keyword | quickly retrieve specific information without manually scanning through each record |
| `* *`     | Social Worker                               | set reminders or follow-up dates for patients | ensure timely check-ins and avoid missing important appointments |
| `* *`     | Social Worker                               | export selected patient records into a shareable format | easily collaborate with colleagues or submit reports without retyping data |
| `* *`     | Expert User                                 | enter command arguments in any order | focus less on the act of keying in a command and work more naturally |
| `* *`     | Expert User                                 | create custom aliases for commands | work faster with familiar shortcuts |
| `* *`     | Expert User                                 | customize where the app saves data to / loads data from | easily make backups of data and revert to older versions if necessary |
| `* *`     | New User                                    | easily discover available commands | use the product immediately, without having to consult a guide |
| `*`      | New User                                    | import data from CSV/Excel files | migrate my existing records from other systems |
| `*`      | Social Worker                               | see patients close to my proximity | plan home visits efficiently |
| `*`      | Social Worker                               | group patients by neighbourhood | cover multiple visits in one area |

### Use cases

(For all use cases below, the **System** is the `CaseTrack` and the **Actor** is the `user`, unless specified otherwise)

**Use case (UC01): Add a patient**

**MSS**

1. User requests to add a patient.
2. System validates the input.
3. System creates the new patient record.
4. System confirms through success message.

   Use case ends.

**Extensions**

* 1a. Missing mandatory fields.

    * 1a1. System shows the required fields.

    * 1a2. No patient is added.

  Use case ends.

* 2a. Invalid input parameters.

    * 2a1. System shows an error message.

    * 2a2. No patient is added.

  Use case ends.

* 2b. Duplicate patient found.

    * 2b1. System shows an error message.

    * 2b2. No patient is added.

  Use case ends.

* *a. At anytime, User cancels the action.

  Use case ends.

#### Use case (UC02): View All Patients

**MSS**

1. User inputs a list command.
2. CaseTrack displays a list of all patients.

   Use case ends.


#### Use case (UC03): Delete patient

**MSS**

1. User requests to list patients.
2.  System <u>shows a list of patients</u> ([UC02](#use-case-uc02-view-all-patients)).
3.  User requests to delete a specific patients in the list.
4.  System deletes the patient.
5.  System shows success message.

    Use case ends.

**Extensions**

* 2a. The list is empty.

    * 2a1. System informs the User that there are no patients available

      Use case ends.

* 3a. The given index is invalid.

    * 3a1. System shows an error message.

      Use case resumes at step 2.

* 4a. Deletion fails due to system error.

    * 4a1. System shows an error message.

      Use case ends.

* *a. At anytime, User cancels the action.

  Use case ends.


#### Use case (UC04): Search patient

**MSS**

1. User requests search with a single filter attribute (name, number, email, or tag).
2. System validates input parameters.
3. System displays all matching patient records.

   Use case ends.

**Extensions**

* 1a. Invalid input parameters.

    * 1a1. System shows an error message.

      Use case ends.

* 1b. No patients match the search criteria.

    * 1b1. System informs the User that no patients match the search criteria.

      Use case ends.

* *a. At any time, User cancels the action.

    Use case ends.


#### Use case (UC05): Add quick note

**Preconditions**
* Patient record exists in the system (by index or by Name + Phone).

**Guarantees**
* A new note will be stored only if a valid patient is identified.
* Notes are linked correctly to the intended patient.
* Invalid inputs will not create notes.

**MSS**

1. User requests to add a quick note with patient reference and text.
2. System validates the patient reference and note text.
3. System stores the note to the patient's record.
4. System confirms success by displaying the created note.

    Use case ends.

**Extensions**
*	2a. Missing patient reference.

    * 2a1. System shows an error message.

      Use case ends.

*	2b. No matching patient found.

    * 2b1. System shows an error message.

      Use case ends.

*	2c. Note text is empty.

    * 2c1. System shows an error message.

      Use case ends.

*	*a. At any time, User cancels the action.

    Use case ends.

#### Use case: (UC06): Delete quick note

**Preconditions**
* Patient record exists in the system (by index or by Name + Phone).

**Guarantees**
* A note will only be deleted if a valid patient is identified and the note index exists.
* Only notes from the linked patient will be deleted.
* Invalid inputs will not delete notes.

**MSS**

1. User requests to remove a quick note with patient reference and note index.
2. System validates the patient reference and note index.
3. System deletes the specified note from the patient's record.
4. System confirms success by displaying the deleted note.

    Use case ends.

**Extensions**
*	2a. Missing patient reference.

    * 2a1. System shows an error message.

      Use case ends.

*	2b. No matching patient found.

    * 2b1. System shows an error message.

      Use case ends.

*	2c. Phone number is invalid.

    * 2c1. System shows an error message.

      Use case ends.

*	2d. Note index is empty.

    * 2d1. System shows an error message.

      Use case ends.

*	2e. Note index is invalid.

    * 2e1. System shows an error message.

      Use case ends.

* *a. At any time, User cancels the action.

    Use case ends.

#### Use case: (UC07): Edit quick note

**Preconditions**
* Patient record exists in the system (by index or by Name + Phone).

**Guarantees**

* A note will only be edited if a valid patient is identified and the note index exists.
* Only notes from the linked patient will be edited.
* Invalid inputs will not modify notes.

**MSS**

1. User types the command to edit a quick note with patient index, note index, and new note text.
2. System validates the patient index, note index, and new note text.
3. System replaces the specified note under the patient's record with the new note text.
4. System confirms success by displaying the updated note.

   Use case ends.

**Extensions**

* 2a. Patient index is invalid.

    * 2a1. System shows an error message.

    Use case ends.

* 2b. Note index is invalid.

    * 2b1. System shows an error message.

    Use case ends.

* 2c. Person has no notes.

    * 2d1. System shows an error message.

    Use case ends.

* 2d. New note text is empty.

    * 2e1. System shows an error message.

    Use case ends.

* *a. At any time, User cancels the action.

    Use case ends.

#### Use case (UC08): Edit patient

**Preconditions**
* At least one patient is listed in the current view.

**Guarantees**
* Patient details are updated only when a valid patient selection and valid details are provided.
* Invalid inputs do not modify any patient data.

**MSS**
1. User requests to list patients.
2. System <u>shows a list of patients</u> ([UC02](#use-case-uc02-view-all-patients))
3. User requests an update to a patient details.
4. System updates the patient's details.
5. System confirms that the update was successful.

   Use case ends.

**Extensions**
* 2a. The list is empty.

    * 2a1. System indicates that there are no patients available.

      Use case ends.

* 3a. The patient selection is invalid.

    * 3a1. System shows an error message.

      Use case resumes at step 2.

* 3b. The patient edit details are invalid.

    * 3b1. System shows an error message.

      Use case ends.

* 3c. Duplicate patient found.

    * 3c1. System shows an error message.

      Use case ends.

* *a. At any time, User cancels the action.

  Use case ends.

#### Use case (UC09): View patient details

**Preconditions**
* At least one patient is listed in the current view.

**Guarantees**
* Patient details will only be displayed if a valid patient index is provided.
* Invalid inputs will not modify any patient data.

**MSS**
1. User requests to list patients.
2. System <u>shows a list of patients</u> ([UC02](#use-case-uc02-view-all-patients))
3. User requests to view details of a specific patient.
4. System displays the patient details.
5. System confirms by displaying a success message.

   Use case ends.

**Extensions**
* 3a. The patient index is invalid.

    * 3a1. System shows an error message.

      Use case resumes at step 1.

* 3b. No patient index is provided.

    * 3b1. System shows an error message.

      Use case resumes at step 1.

* *a. At any time, User cancels the action.

  Use case ends.

### Non-Functional Requirements

1. Should work on any mainstream OS as long as it has Java `17` or above installed.
2. Should be able to store and retrieve up to 10,000 patient records with the following performance requirements:
    - Initial application startup and data load: < 1 minute on modern hardware (M2 Pro MacBook or equivalent)
    - Search and filter operations: < 3 seconds to display results
    - Individual record operations (add/edit/delete): < 1 second
3. All patient data must be stored locally with no transmission over networks to ensure patient privacy compliance.
4. Healthcare helpers with basic computer literacy should be able to perform common tasks (add, search, update patient records) within 5 minutes of initial training.
5. A user with above average typing speed for regular English text should be able to accomplish most of the tasks faster using commands than using the mouse.
6. The application should be deployed and used only on secure, access-controlled systems that comply with relevant data protection regulations (e.g., PDPA, HIPAA).
7. Only English is supported, as the application targets users in Singapore. Right-to-left (RTL) languages are less commonly used and should not be supported.

### Security and Privacy Considerations

**Data Storage**

CaseTrack stores all patient data in plaintext JSON at `data/casetrack.json`. This includes names, phone numbers, addresses, income, and medical information. No encryption is provided - this keeps the implementation simple and data recoverable.

**Regulatory Compliance**

Patient data falls under Singapore's PDPA and [PDPC Advisory Guidelines for the Healthcare Sector](https://www.pdpc.gov.sg/guidelines-and-consultation/2017/10/advisory-guidelines-for-the-healthcare-sector). To comply, deploy CaseTrack only in secure environments:

1. **Network Isolation**: Use on systems with no internet access or isolated internal networks.

2. **Physical Security**: Install on computers in locked, access-controlled areas.

3. **Access Controls**:
   - Require strong authentication (passwords, biometrics)
   - Set file permissions to restrict `casetrack.json` access
   - Enable automatic screen locking

4. **Data Handling**:
   - Establish data retention and deletion policies
   - Store backups securely
   - Train staff on data protection
   - Maintain access logs where possible

**What CaseTrack Does NOT Provide**

- Data encryption
- User authentication
- Audit logging
- Network security features

Organizations must provide these through their IT infrastructure and policies.


### Glossary

| Term | Definition |
|------|------------|
| **Mainstream OS** | Windows, Linux, Unix, MacOS |
| **Patient Record** | A data entry with a patient's name, phone, address, income, medical info, and notes|
| **Quick Note** | A brief text annotation attached to a patient record, typically captured during or immediately after a session |
| **Session** | A meeting or consultation between a social worker and a patient |
| **Duplicate Patient** | A patient record with identical name (case-insensitive, whitespace-normalized) and phone number as an existing record |
| **Medical Information** | Health-related details about a patient including conditions, medications, or treatment notes |
| **Partial Data** | Incomplete patient information, common during initial visits or when full details are not available |
| **Prefix** | Command parameter identifiers (e.g., n/ for name, p/ for phone) used in the CLI syntax |

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample patients. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

### Adding a patient

1. Adding a patient with all required fields

   1. Test case: `add n/John Doe p/91234567 e/johndoe@example.com a/123 Main St i/5000 m/Diabetes`<br>
      Expected: Patient is added to the list. Success message shows patient details. Patient appears in the patient list.

   1. Test case: `add n/Jane Smith p/98765432 e/janesmith@example.com a/456 Elm St i/3000`<br>
      Expected: Patient is added without medical information. Success message confirms addition.

1. Adding a patient with invalid inputs

   1. Test case: `add n/John Doe p/12 e/johndoe@example.com a/123 Main St i/5000`<br>
      Expected: No patient is added. Error message indicates phone number must be 3-17 digits.

   1. Test case: `add n/John Doe p/91234567 e/johndoe@example.com a/123 Main St i/-100`<br>
      Expected: No patient is added. Error message indicates income must be non-negative.

   1. Test case: `add n/John Doe p/91234567 e/johndoe@example.com a/123 Main St i/abc`<br>
      Expected: No patient is added. Error message indicates income must be numeric.

   1. Test case: `add n/John Doe p/91234567 e/invalidemail a/123 Main St i/5000`<br>
      Expected: No patient is added. Error message indicates invalid email format.

1. Adding a patient with missing required fields

   1. Test case: `add p/91234567 e/johndoe@example.com a/123 Main St i/5000`<br>
      Expected: No patient is added. Error message indicates name is required.

   1. Test case: `add n/John Doe e/johndoe@example.com a/123 Main St i/5000`<br>
      Expected: No patient is added. Error message indicates phone is required.

   1. Test case: `add n/John Doe p/91234567 a/123 Main St i/5000`<br>
      Expected: No patient is added. Error message indicates email is required.

1. Adding a duplicate patient

   1. Prerequisites: A patient "John Doe" with phone "91234567" already exists.

   1. Test case: `add n/John Doe p/91234567 e/johndoe@example.com a/456 Different St i/6000`<br>
      Expected: No patient is added. Error message indicates patient already exists.

   1. Test case: `add n/john doe p/91234567 e/johndoe@example.com a/789 Another St i/7000`<br>
      Expected: No patient is added. Error message indicates patient already exists (name comparison is case-insensitive).

### Searching/filtering patients

1. Searching by name

   1. Prerequisites: Multiple patients in the list with different names.

   1. Test case: `search name John`<br>
      Expected: All patients with "John" in their name are displayed. Count message shows number of patients found.

   1. Test case: `search name xyz`<br>
      Expected: No patients displayed. Message indicates no patients found.

1. Searching by phone number

   1. Prerequisites: Multiple patients in the list with different phone numbers.

   1. Test case: `search number +6591234567`<br>
      Expected: Patients with matching phone number (including country code) are displayed.

   1. Test case: `search number 91234567`<br>
      Expected: Patients with matching phone number are displayed.

1. Searching by email

   1. Prerequisites: Patients with email addresses in the list.

   1. Test case: `search email john@example.com`<br>
      Expected: Patients with matching email are displayed.

1. Searching by tags

   1. Prerequisites: Patients with various tags in the list.

   1. Test case: `search tag diabetes`<br>
      Expected: All patients tagged with "diabetes" are displayed.

1. Invalid search commands

   1. Test case: `search`<br>
      Expected: Error message indicating search attribute must be specified.

   1. Test case: `search invalidtype keyword`<br>
      Expected: Error message indicating invalid search type.

### Deleting a patient

1. Deleting a patient while all patients are being shown

   1. Prerequisites: List all patients using the `list` command. Multiple patients in the list.

   1. Test case: `delete patient 1`<br>
      Expected: First patient is deleted from the list. Details of the deleted patient shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete patient 0`<br>
      Expected: No patient is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete patient commands to try: `delete patient`, `delete patient x`, `delete patient -1` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. Deleting a note from a patient

   1. Prerequisites: List all patients using the `list` command. Choose a patient with multiple notes, doing so through the `view` command.

   1. Test case: `delete note 1 1`<br>
      Expected: First note from the first patient is deleted. Details of the deleted note shown in the status message. Patient's note list is updated.

   1. Test case: `delete note 1 0`<br>
      Expected: No note is deleted. Error message indicating invalid note index is shown.

   2. Test case: `delete note 1 5` (where patient 1 has only 2 notes)<br>
      Expected: No note is deleted. Error message indicating invalid note index is shown.

   3. Test case: `delete note 1` (missing note index)<br>
      Expected: Error message indicating invalid note command format is shown.

   4. Test case: `delete note` (missing both patient and note index)<br>
      Expected: Error message indicating invalid command format is shown.

### Adding a note to a patient

1. Adding a note by patient index

   1. Prerequisites: List all patients using the `list` command. At least one patient in the list.

   1. Test case: `note 1 t/Patient reported feeling better today`<br>
      Expected: Note is added to the first patient. Success message shows the note content. Patient's note list is updated.

   1. Test case: `note 1 t/Follow-up needed next week`<br>
      Expected: Another note is added to the first patient. Multiple notes can exist for one patient.

1. Adding a note with invalid inputs

   1. Test case: `note 0 t/Some note`<br>
      Expected: No note is added. Error message indicates invalid patient index.

   1. Test case: `note 1 t/`<br>
      Expected: No note is added. Error message indicates note cannot be empty.

   1. Test case: `note 999 t/Some note` (where 999 exceeds the list size)<br>
      Expected: No note is added. Error message indicates patient index is out of range.

   1. Test case: `note 1` (missing note text)<br>
      Expected: No note is added. Error message indicates invalid command format.

1. Adding a note by patient name and phone

   1. Prerequisites: A patient "John Doe" with phone "91234567" exists.

   1. Test case: `note n/John Doe p/91234567 t/Patient attended session today`<br>
      Expected: Note is added to the patient. Success message confirms addition.

   1. Test case: `note n/Jane Doe p/99999999 t/Some note`<br>
      Expected: No note is added. Error message indicates no patient found with given details.

### Editing a note

1. Editing an existing note

   1. Prerequisites: First patient has at least 2 notes.

   1. Test case: `edit note 1 1 t/Updated note content`<br>
      Expected: First note of the first patient is updated. Success message shows the updated note.

   1. Test case: `edit note 1 2 t/Another update`<br>
      Expected: Second note of the first patient is updated.

1. Editing a note with invalid inputs

   1. Test case: `edit note 1 0 t/Some content`<br>
      Expected: No note is edited. Error message indicates invalid note index.

   1. Test case: `edit note 1 999 t/Some content` (where patient 1 has fewer than 999 notes)<br>
      Expected: No note is edited. Error message indicates note index is out of range.

   1. Test case: `edit note 1 1 t/`<br>
      Expected: No note is edited. Error message indicates note cannot be empty.

   1. Test case: `edit note 0 1 t/Some content`<br>
      Expected: No note is edited. Error message indicates invalid patient index.

1. Editing a note when patient has no notes

   1. Prerequisites: A patient exists with no notes.

   1. Test case: `edit note 1 1 t/Some content`<br>
      Expected: No note is edited. Error message indicates patient has no notes to edit.

### Viewing patient details

1. Viewing details of a patient

   1. Prerequisites: Multiple patients in the list.

   1. Test case: `view 1`<br>
      Expected: Details of the first patient are displayed in the detail panel, including all notes, medical information, and other attributes.

   1. Test case: `view 2`<br>
      Expected: Details of the second patient are displayed.

1. Viewing with invalid index

   1. Test case: `view 0`<br>
      Expected: Error message indicates invalid patient index.

   1. Test case: `view 999` (where 999 exceeds the list size)<br>
      Expected: Error message indicates patient index is out of range.

   1. Test case: `view` (missing index)<br>
      Expected: Error message indicates invalid command format.

### Editing patient income

1. Editing income with valid values

   1. Prerequisites: List all patients using the `list` command. At least one patient in the list.

   1. Test case: `edit patient 1 i/6000`<br>
      Expected: First patient's income is updated to 6000. Success message confirms the update.

   1. Test case: `edit patient 1 i/0`<br>
      Expected: First patient's income is updated to 0. Zero is a valid income value.

1. Editing income with invalid values

   1. Test case: `edit patient 1 i/-500`<br>
      Expected: Income is not updated. Error message indicates income must be non-negative.

   1. Test case: `edit patient 1 i/abc`<br>
      Expected: Income is not updated. Error message indicates income must be numeric.

   1. Test case: `edit patient 1 i/$5000`<br>
      Expected: Income is not updated. Error message indicates invalid income format (no currency symbols).

   1. Test case: `edit patient 0 i/5000`<br>
      Expected: Income is not updated. Error message indicates invalid patient index.

### Editing patient medical information

1. Editing medical information with valid values

   1. Prerequisites: List all patients using the `list` command. At least one patient in the list.

   1. Test case: `edit patient 1 m/Diabetes, Hypertension`<br>
      Expected: First patient's medical information is updated. Success message confirms the update.

   1. Test case: `edit patient 1 m/None`<br>
      Expected: First patient's medical information is updated to "None".

1. Editing medical information with invalid values

   1. Test case: `edit patient 1 m/`<br>
      Expected: Medical information is not updated. Error message indicates medical information cannot be empty.

   1. Test case: `edit patient 1 m/   `<br>
      Expected: Medical information is not updated. Error message indicates medical information cannot be only whitespace.

   1. Test case: `edit patient 0 m/Diabetes`<br>
      Expected: Medical information is not updated. Error message indicates invalid patient index.

### Saving data

1. Dealing with missing data files

   1. Missing data file

      1. Go to `/data` folder and delete `casetrack.json`.
      2. Launch CaseTrack.
      3. CaseTrack launches normally with sample data loaded.

## **Appendix: Planned Enhancements**

Team size: 5

This appendix lists planned enhancements for upcoming releases.

1. Make product terminology consistent across UI, logs, and docs
   - Feature flaw: Mixed "AddressBook" terms remain in UI messages, class names, and diagrams, which can confuse users (e.g., error logs mention `AddressBookStorage`).
   - Change: Standardize user-facing terminology to "CaseTrack" and patient-centric terms. Update UI strings, log messages, and documentation references. No functional behavior changes.
   - Sample outputs:
     - Before: `Failed to save AddressBook to file.`
     - After: `Failed to save CaseTrack data to file.`

2. Make medical information input structured and readable
   - Feature flaw: The `m/` field is an unstructured free‑text blob, making information hard to read and impossible to filter precisely.
   - Change: Accept optional subfields when adding/editing a patient, and render them as labeled sections in the UI.
   - Sample inputs:
     ```
     add n/Noah Tan p/91234567 e/noah.tan@example.com a/123 Serangoon Ave i/4000 \
       v/BP 128/82; Pulse 76; Temp 37.1 \
       al/Penicillin(rash), Peanuts(swelling) \
       mh/T2D(2019); Hypertension(2021) \
       cm/Metformin 500mg daily; Lisinopril 10mg daily \
       cn/Reports occasional dizziness
     ```
   - Sample UI (textual):
     - Vitals: BP 128/82, Pulse 76, Temp 37.1°C
     - Allergies: Penicillin (rash), Peanuts (swelling)
     - History: T2D (2019), Hypertension (2021)
     - Medications: Metformin 500mg daily; Lisinopril 10mg daily
     - Notes: Reports occasional dizziness

3. Make Help command restore and focus the Help window
   - Feature flaw: Re-running `help` when the Help window is minimized does nothing visible; users think the command failed.
   - Change: If a Help window exists, bring it to front and un-minimize it; otherwise, open a new Help window. Provide a status message when restored.
   - Sample behavior:
     - Command: `help` (when Help is minimized) ➜ Help window is restored and focused; status bar shows: `Help window restored`.
     - Command: `help` (when Help is closed) ➜ Help window opens as usual.

4. Extend search to cover medical conditions in patient records
   - Feature flaw: Existing search supports name/phone/email/tag only; users cannot find patients by conditions or medications stored in medical info.
   - Change: Extend the existing `search` command with qualifiers that filter by structured medical info or substrings within the medical information field.
   - Sample inputs:
     ```
     search condition asthma
     search condition diabetes
     search meds metformin
     search allergy penicillin
     ```
   - Expected outcome: Lists patients whose medical information matches the qualifier (e.g., all patients with asthma or taking Metformin).

5. Add country code context for consistent phone number handling
    - Feature flaw: Phone numbers with and without country codes (e.g., "9111 1111" vs "+65 9111 1111") are treated as distinct, allowing duplicate patients with identical details but different phone number formats to be added.
    - Change: Allow users to configure a default country code in application settings. When checking for duplicates, normalize all phone numbers by applying the default country code to numbers lacking one. Display the active country code setting prominently in the UI and provide clear feedback about phone number normalization.
    - Sample inputs:
    ```
     setcountry +65
     add n/John Doe p/9111 1111 e/john@example.com a/123 Street
     add n/John Doe p/+65 9111 1111 e/john@example.com a/123 Street
    ```
    - Sample behavior:
        - Command: `setcountry +65` ➜ Status bar shows: `Default country code set to +65. Phone numbers without country codes will be treated as +65 numbers for duplicate detection.`
        - First `add` command succeeds; internally normalized and stored as "+65 9111 1111"
        - Second `add` command fails with: `This patient already exists in CaseTrack. A patient with the name "John Doe" and phone number "+65 9111 1111" is already registered.`
        - UI displays country code indicator in settings panel (e.g., "Default Country Code: +65") and shows normalized format in patient details

6. Make result display box dynamically resizable for improved readability
    - Feature flaw: The result display box remains fixed in size, requiring users to scroll vertically and/or horizontally to view long messages or error feedback, even when the application window is maximized. This breaks the natural command-feedback flow and hinders usability.
    - Change: Implement dynamic resizing for the result display box with automatic text wrapping and adaptive height. The box should expand to accommodate longer messages up to a reasonable maximum, and all UI components should resize proportionally when the application window is resized. Enable smooth vertical scrolling only when messages exceed the maximum displayable height.
    - Sample behavior:
        - User executes command with invalid format ➜ Result box expands vertically to display the complete error message without horizontal scrolling; text wraps naturally within the available width.
        - User maximizes application window ➜ Result box scales proportionally, displaying more content without requiring scrolling for moderately long messages.
        - User executes command with extremely long output ➜ Result box expands to predefined maximum height (e.g., 40% of window height), then enables smooth vertical scrolling for remaining content.
        - All feedback messages remain immediately visible without manual scrolling for typical use cases, preserving the CLI-style immediate feedback experience.

7. Make search name function use AND-based matching for precise results
    - Feature flaw: The `search name` command performs a loose OR-based keyword match, returning any patient whose name contains any of the provided keywords rather than all of them. This causes search results to include many irrelevant entries, making it difficult to locate specific patients efficiently.
    - Change: Modify the `search name` command to use AND-based matching by default, requiring all provided keywords to be present in the patient's name. Optionally, add a flag to enable OR-based matching when users need broader search results.
    - Sample inputs:
    ```
         search name John Tan
         search name John Tan --any
    ```
    - Sample behavior:
       - Command: `search name John Tan` (AND-based, default) ➜ Returns only patients whose names contain both "John" AND "Tan" (e.g., "John Tan", "Tan John Wei").
       - Command: `search name John Tan` (current behavior) ➜ Returns all patients with "John" OR "Tan" in their names (e.g., "John Doe", "Mary Tan", "John Tan"), resulting in many irrelevant matches.
       - Command: `search name John Tan --any` (OR-based, optional) ➜ Returns patients with "John" OR "Tan" for broader searches when needed.
       - Result message displays search mode: `5 patients found matching all keywords: John, Tan` or `12 patients found matching any keyword: John, Tan`

8. Include useful fields to make the application more complete
    - Feature flaw: CaseTrack requires income as a mandatory field while lacking more critical healthcare-related fields such as date of birth, sex/gender, emergency contact, and insurance information. This makes the application feel incomplete for its intended healthcare management purpose, especially since medical information itself is optional while income is mandatory.
    - Change: Introduce new fields for essential patient information. Add date of birth (mandatory), sex/gender (optional), emergency contact (optional with name and phone), and insurance provider (optional). Update validation logic and UI to accommodate these fields with appropriate input formats and display layouts.
    - Sample inputs:
    ```
         add n/Sarah Lee dob/15/03/1985 g/Female p/91234567 e/sarah@example.com a/123 Street i/3500 \
           ec/John Lee; 98765432 ins/MediShield Life
         add n/David Ng dob/22/11/1992 p/87654321 e/david@example.com a/456 Avenue
    ```
    - Sample UI (textual):
       - Name: Sarah Lee
       - Date of Birth: 15/03/1985 (Age: 40)
       - Gender: Female
       - Emergency Contact: John Lee (98765432)
       - Insurance: MediShield Life
       - Income: $3,500

9. Optimize tag handling and rendering for improved performance
    - Feature flaw: The application experiences significant lag and slowdown when a large number of tags are attached to a single patient, impacting responsiveness and usability for users who utilize extensive tagging systems.
    - Change: Implement performance optimizations for tag storage, retrieval, and rendering. Add lazy loading for tag display, optimize the UI rendering pipeline to handle large tag collections efficiently, and introduce a configurable limit on the maximum number of visible tags (with an expandable "show more" option). Refactor the underlying tag data structure to improve lookup and filtering performance.
    - Sample behavior:
        - Patient with 50+ tags ➜ UI displays first 10 tags with a badge showing "+40 more tags" and an expand button; clicking expands to show all tags in a scrollable view.
        - Tag filtering and search operations ➜ Remain responsive even with patients having 100+ tags due to optimized indexing.
        - Tag display rendering ➜ Uses virtualization to render only visible tags, significantly reducing DOM overhead.
        - Settings option ➜ Users can configure the default number of visible tags (e.g., 5, 10, 15, or "show all").
    - Sample UI (textual):
        - Tags: [Diabetes] [Hypertension] [High-Risk] [Regular-Checkup] [Senior] [Wheelchair] [Hearing-Aid] [Vision-Impaired] [Cognitive-Decline] [Fall-Risk] ... +42 more [Expand ▼]

10. Support adding multiple notes to a patient in a single command
    - Feature flaw: The `note` command currently only processes the first note when multiple `t/` prefixes are provided in a single command (e.g., `note 2 t/first note t/second note`), ignoring subsequent notes. This requires users to execute multiple commands to add several notes, reducing efficiency.
    - Change: Extend the `note` command to accept and process multiple `t/` prefixes in a single command, adding all specified notes to the patient's note list in the order provided.
    - Sample inputs:
    ```
     note 2 t/Patient reports chest pain during exercise t/Scheduled for ECG next week t/Follow-up required
     note 5 t/Medication dosage adjusted t/Patient tolerating new dosage well
    ```
    - Sample behavior:
      - Command: `note 2 t/first note t/second note t/third note` ➜ All three notes are added to patient #2's record
      - Success message: `3 notes added to patient: John Tan`
      - Single note command continues to work: `note 2 t/single note` ➜ Adds one note as before.
