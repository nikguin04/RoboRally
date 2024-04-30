package dk.dtu.compute.se.pisd.roborally.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.sun.javafx.scene.control.skin.resources.ControlResources;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Deserializer;
import dk.dtu.compute.se.pisd.roborally.model.Player;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import dk.dtu.compute.se.pisd.roborally.utils.FileResourceUtils;
import java.io.InputStreamReader;
import java.io.Reader;

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

	Board current_board;
	private Label board_info_label = new Label("placeholder");


	/**
	 * Opens a dialog in which the user can pick a save file from the gamedata folder
	 * On selection of game save file, display data about the saved game on label in windows
	 *
	 * @return LoadDialog<Board>
	 * @see Dialog
	 */
    public LoadDialog() {
		Set<String> files_json;
        Set<String> files;
		// UserDirectory = ""C:\Users\nikla\Desktop\Programmering\RoboRally""
		// TODO: This should be changed so it also works at release, this currently only works when debugging (point to proper resource path)
		try (Stream<Path> stream = Files.list(Paths.get("target/classes/gamedata"))) {
			files_json = stream
			  .filter(file -> !Files.isDirectory(file))
			  .map(Path::getFileName)
			  .map(Path::toString)
			  .collect(Collectors.toSet());

            files = new HashSet<String>();
            for (String s: files_json) {
                if (s.endsWith(".json"))
                    { files.add(s.substring(0, s.length() - ".json".length())); }
            }
		} catch (Exception e) {
			System.out.println("Load error, cant find gamedata/save path");
			files = new HashSet<String>();
			//files.add("ERROR");
		}
		String defaultChoice = files.stream().findFirst().get(); // This might crash if above exception is caught

		final DialogPane dialogPane = getDialogPane();

        // -- grid
        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);
        //grid.prefHeightProperty().bind(board_info_label.heightProperty());
        //grid.setHgrow(board_info_label, Priority.ALWAYS);
        //dialogPane.prefHeightProperty().bind(board_info_label.heightProperty());
        //dialogPane.height

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
        comboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            updateGrid();
        });
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


        setResultConverter((dialogButton) -> {
            ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
			// TODO: RETURN PROPER BOARD HERE!
			return current_board;
            //return data == ButtonData.OK_DONE ? getSelectedItem() : null;
        });
    }




    /* ************************************************************************
     *
     * Public API
     *
     **************************************************************************/

     /**
	 * @return String, The relative filename of the currently selected file in the drop down menu
	 */
    public final String getSelectedItem() {
        return comboBox.getSelectionModel().getSelectedItem();
    }

    /**
	 * @return ReadOnlyObjectProperty<String>, The object property (read only)
     * @see ReadOnlyObjectProperty
	 */
    public final ReadOnlyObjectProperty<String> selectedItemProperty() {
        return comboBox.getSelectionModel().selectedItemProperty();
    }

    /**
	 * @param item Set the selected item in the drop down menu
	 */
    public final void setSelectedItem(String item) {
        comboBox.getSelectionModel().select(item);
    }

    /**
	 * @return ObservableList<String>, List of all items that are available to pick in the drop down meny
	 */
    public final ObservableList<String> getItems() {
        return comboBox.getItems();
    }

    /**
	 * @return String, the default choice the user is presented with (usually alphabetically sorted or sorted by file's timestamp)
	 */
    public final String getDefaultChoice() {
        return defaultChoice;
    }



    /* ************************************************************************
     *
     * Private Implementation
     *
     **************************************************************************/


     /**
	 * Updates the board variable of chosen save file
     * Displays data about the board to the user in a label
	 */
    private void updateGrid() {
        grid.getChildren().clear();

		FileResourceUtils fileutil = new FileResourceUtils();
		String fileName = "gamedata/" + getSelectedItem() + ".json";
		System.out.println("\ngetResource : " + fileName);
		InputStream file;
		try {
        	file = fileutil.getFileFromResourceAsStream(fileName);
			//FileResourceUtils.printFile(file);
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Board.class, new Deserializer.BoardDeserializer());
			gsonBuilder.registerTypeAdapter(Player.class, new Deserializer.PlayerDeserializer());
			gsonBuilder.registerTypeAdapter(Command.class, new Deserializer.CommandDeserializer());
			Gson gson = gsonBuilder.setPrettyPrinting().create();

			Reader targetReader = new InputStreamReader(file);
			JsonReader reader = new JsonReader(targetReader);
			current_board = gson.fromJson(reader, Board.class);

			updateLabel();
			targetReader.close();
			reader.close();
		} catch (IOException e) {
			current_board = null;
		}



        grid.add(label, 0, 0);
        grid.add(comboBox, 0, 1);
		grid.add(board_info_label, 0, 2);
        getDialogPane().setContent(grid);


        Platform.runLater(() -> comboBox.requestFocus());
    }

    /**
	 * Updates the board_info_label to reflect new board about to be loaded
     * Displays data such as players names and board size
	 */
	private void updateLabel() {

		String info = "";
		info += String.format("Board Size: %d,%d\n", current_board.height, current_board.width);
		info += "Players:\n";
		for (int i = 0; i < current_board.getPlayersNumber(); i++) {
			Player p = current_board.getPlayer(i);
			info += String.format("\t%s\n", p.getName());
		}
		board_info_label = new Label(info);
        board_info_label.setWrapText(true);
        board_info_label.setMinHeight(200); // Hardcoded size because we are unsure how big it will be
	}
}
