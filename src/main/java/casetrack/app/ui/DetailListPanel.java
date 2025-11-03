package casetrack.app.ui;

import java.util.List;
import java.util.logging.Logger;

import casetrack.app.commons.core.LogsCenter;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Person;
import casetrack.app.model.person.PersonAttribute;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

/**
 * Panel containing a list of details.
 */
public class DetailListPanel extends UiPart<Region> {
    private static final String FXML = "DetailListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(DetailListPanel.class);

    @FXML
    private ListView<PersonAttribute> detailListView;

    /**
     * Creates a {@code DetailListPanel}.
     */
    public DetailListPanel() {
        super(FXML);
        detailListView.setCellFactory(listView -> new DetailListViewCell());
    }

    /**
     * Clears the detail panel.
     */
    public void clearDetails() {
        logger.info("Clearing detail panel");
        detailListView.setItems(FXCollections.emptyObservableList());
    }

    /**
     * Displays details for a specific person
     * @param person A {@code Person} whose details are to be displayed.
     */
    public void showDetails(Person person) {
        // Display notes as numbered list
        String noteValue = "None";
        List<Note> personNotes = person.getNotes();
        if (!personNotes.isEmpty()) {
            noteValue = "";
            for (int i = 0; i < personNotes.size(); i++) {
                noteValue += (i + 1) + ". " + personNotes.get(i).value + "\n";
            }
            noteValue = noteValue.strip();
        }

        ObservableList<PersonAttribute> detailList = FXCollections.observableArrayList(
            new PersonAttribute(person.getName().getClass().getSimpleName(), person.getName().fullName),
            new PersonAttribute(person.getPhone().getClass().getSimpleName(), person.getPhone().value),
            new PersonAttribute(person.getAddress().getClass().getSimpleName(), person.getAddress().value),
            new PersonAttribute(person.getEmail().getClass().getSimpleName(), person.getEmail().value),
            new PersonAttribute(person.getIncome().getClass().getSimpleName(), person.getIncome().toString()),
            new PersonAttribute("Medical Info", person.getMedicalInfo().toString()),
            new PersonAttribute("Notes", noteValue)
        );
        detailListView.setItems(detailList);
    }

    /**
     * Custom {@code ListCell} that displays the details of a {@code Person} using a
     * {@code DetailCard}.
     */
    class DetailListViewCell extends ListCell<PersonAttribute> {
        @Override
        protected void updateItem(PersonAttribute attribute, boolean empty) {
            super.updateItem(attribute, empty);
            setMouseTransparent(true);
            setFocusTraversable(false);

            if (empty || attribute == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new DetailCard(attribute, getIndex() + 1).getRoot());
            }
        }
    }

}
