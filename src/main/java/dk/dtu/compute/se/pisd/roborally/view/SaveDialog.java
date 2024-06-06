package dk.dtu.compute.se.pisd.roborally.view;

import com.sun.javafx.scene.control.skin.resources.ControlResources;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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

	/**
	 * This method makes a grid, for the save,
	 * to make sure the user can save the file correctly,
	 * while also could name the file, the person wants to save.
	 * Example save name: Hello.
	 * Then there would be made a file, with the name Hello.
	 */
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
