package dk.dtu.compute.se.pisd.roborally.view;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.StartTile;
import javafx.scene.layout.StackPane;


public class StartTileView extends StackPane implements ViewObserver {
	final public static int SPACE_HEIGHT = 60; // 75;
	final public static int SPACE_WIDTH = 60; // 75;

	public final StartTile startTile;

	public StartTileView(StartTile startTile) {
		this.startTile = startTile;

		this.setPrefWidth(SPACE_WIDTH);
		this.setMinWidth(SPACE_WIDTH);
		this.setMaxWidth(SPACE_WIDTH);

		this.setPrefHeight(SPACE_HEIGHT);
		this.setMinHeight(SPACE_HEIGHT);
		this.setMaxHeight(SPACE_HEIGHT);

		this.setStyle("-fx-background-color: black;");
	}

	@Override
	public void updateView(Subject subject) {

	}
}
