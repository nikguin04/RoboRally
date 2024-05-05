package dk.dtu.compute.se.pisd.roborally.view;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.PrioAntenna;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;
public class PrioAntennaView extends StackPane implements ViewObserver {
	final public static int SPACE_HEIGHT = 60; // 75;
	final public static int SPACE_WIDTH = 60; // 75;

	public final PrioAntenna prioAntenna;

	public PrioAntennaView(PrioAntenna prioAntenna) {
		this.prioAntenna = prioAntenna;

		this.setPrefWidth(SPACE_WIDTH);
		this.setMinWidth(SPACE_WIDTH);
		this.setMaxWidth(SPACE_WIDTH);

		this.setPrefHeight(SPACE_HEIGHT);
		this.setMinHeight(SPACE_HEIGHT);
		this.setMaxHeight(SPACE_HEIGHT);

		this.setStyle("-fx-background-color: blue;");
	}

	@Override
	public void updateView(Subject subject) {

	}
}
