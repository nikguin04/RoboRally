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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import dk.dtu.compute.se.pisd.roborally.utils.FileResourceUtils;
import java.io.InputStreamReader;
import java.io.Reader;
import javafx.scene.control.TextField;


/**
 * A dialog that shows a load menu to the user where they can pick a path
 *
 *
 * @see Dialog
 * @param <T> The type of the items to show to the user, and the type that is returned
 *            via {@link #getResult()} when the dialog is dismissed.
 */


 public class SaveDialog<T> extends Dialog<T> {

    private final GridPane grid;
    private final Label label;
    public final TextField saveNameText;


    public SaveDialog() {

		final DialogPane dialogPane = getDialogPane();

        // -- grid
        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);

        // -- label
		label = new Label(dialogPane.getContentText());
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        label.textProperty().bind(dialogPane.contentTextProperty());


        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("choice-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // TextField for filename input
        saveNameText = new TextField();
        saveNameText.setPromptText("Enter filename");
        saveNameText.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(saveNameText, Priority.ALWAYS);

        grid.add(label, 0, 0);
        grid.add(saveNameText, 0, 1);

        getDialogPane().setContent(grid);

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // Return the filename entered by the user
                System.out.println(saveNameText.getText());
                return (T) saveNameText.getText();

            }
            return null;
        });
    }

}
