package dk.dtu.compute.se.pisd.roborally.view;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.javafx.scene.control.skin.resources.ControlResources;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * A dialog that shows a load menu to the user where they can pick a path
 *
 *
 * @see Dialog
 * @param <T> The type of the items to show to the user, and the type that is returned
 *            via {@link #getResult()} when the dialog is dismissed.
 */
public class LoadDialog<T> extends Dialog<Board> {

    /* ************************************************************************
     *
     * Fields
     *
     **************************************************************************/

    private final GridPane grid;
    private final Label label;
    private final ComboBox<String> comboBox;
    private final String defaultChoice;


    public LoadDialog() {
		Set<String> files;
		// UserDirectory = ""C:\Users\nikla\Desktop\Programmering\RoboRally""
		// TODO: This should be changed so it also works at release, this currently only works when debugging (point to proper resource path)
		try (Stream<Path> stream = Files.list(Paths.get("target/classes/gamedata"))) {
			files = stream
			  .filter(file -> !Files.isDirectory(file))
			  .map(Path::getFileName)
			  .map(Path::toString)
			  .collect(Collectors.toSet());
		} catch (Exception e) {
			System.out.println("Load error, cant find gamedata/save path");
			files = new HashSet<String>();
			files.add("ERROR");
		}
		String defaultChoice = files.stream().findFirst().get();

		final DialogPane dialogPane = getDialogPane();

        // -- grid
        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);

        // -- label

        //label = DialogPane.createContentLabel(dialogPane.getContentText());
		label = new Label(dialogPane.getContentText());
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        label.textProperty().bind(dialogPane.contentTextProperty());

        dialogPane.contentTextProperty().addListener(o -> updateGrid());

        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("choice-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final double MIN_WIDTH = 150;

        comboBox = new ComboBox<String>();
        comboBox.setMinWidth(MIN_WIDTH);
        if (files != null) {
			String arr[] = new String[files.size()];
			files.toArray(arr);
            comboBox.getItems().addAll(arr);
        }
        comboBox.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(comboBox, Priority.ALWAYS);
        GridPane.setFillWidth(comboBox, true);

        this.defaultChoice = comboBox.getItems().contains(defaultChoice) ? defaultChoice : null;

        if (defaultChoice == null) {
            comboBox.getSelectionModel().selectFirst();
        } else {
            comboBox.getSelectionModel().select(defaultChoice);
        }

        updateGrid();

        setResultConverter((dialogButton) -> {
            ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
			// TODO: RETURN PROPER BOARD HERE!
			return new Board(8, 8);
            //return data == ButtonData.OK_DONE ? getSelectedItem() : null;
        });
    }



    /*public LoadDialog(T defaultChoice, Collection<T> choices) {
        final DialogPane dialogPane = getDialogPane();

        // -- grid
        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);

        // -- label

        //label = DialogPane.createContentLabel(dialogPane.getContentText());
		label = new Label(dialogPane.getContentText());
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        label.textProperty().bind(dialogPane.contentTextProperty());

        dialogPane.contentTextProperty().addListener(o -> updateGrid());

        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("choice-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final double MIN_WIDTH = 150;

        comboBox = new ComboBox<T>();
        comboBox.setMinWidth(MIN_WIDTH);
        if (choices != null) {
            comboBox.getItems().addAll(choices);
        }
        comboBox.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(comboBox, Priority.ALWAYS);
        GridPane.setFillWidth(comboBox, true);

        this.defaultChoice = comboBox.getItems().contains(defaultChoice) ? defaultChoice : null;

        if (defaultChoice == null) {
            comboBox.getSelectionModel().selectFirst();
        } else {
            comboBox.getSelectionModel().select(defaultChoice);
        }

        updateGrid();

        setResultConverter((dialogButton) -> {
            ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonData.OK_DONE ? getSelectedItem() : null;
        });
    }*/



    /* ************************************************************************
     *
     * Public API
     *
     **************************************************************************/

    public final String getSelectedItem() {
        return comboBox.getSelectionModel().getSelectedItem();
    }

    public final ReadOnlyObjectProperty<String> selectedItemProperty() {
        return comboBox.getSelectionModel().selectedItemProperty();
    }

    public final void setSelectedItem(String item) {
        comboBox.getSelectionModel().select(item);
    }

    public final ObservableList<String> getItems() {
        return comboBox.getItems();
    }

    public final String getDefaultChoice() {
        return defaultChoice;
    }



    /* ************************************************************************
     *
     * Private Implementation
     *
     **************************************************************************/

    private void updateGrid() {
        grid.getChildren().clear();

        grid.add(label, 0, 0);
        grid.add(comboBox, 1, 0);
        getDialogPane().setContent(grid);

        Platform.runLater(() -> comboBox.requestFocus());
    }
}
