/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.Gears;
import dk.dtu.compute.se.pisd.roborally.controller.PriorityAntenna;
import dk.dtu.compute.se.pisd.roborally.controller.StartTile;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

	final public static Image blankSquare;
	final public static Image wallTexture;
	final public static Image antennaTexture;
	final public static Image startTileTexture;
	final public static Image gearAnticlockwiseTexture;
	final public static Image gearClockwiseTexture;
	final public static Image[] checkpointTextures;
	final public static Image[] conveyorTextures;

    final public static int SPACE_HEIGHT = 60; // 75;
    final public static int SPACE_WIDTH = 60; // 75;

    public final Space space;

	Node playerNode = null;

    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

		this.addBackground();

		for (Heading heading : space.getWalls()) {
			ImageView wall = new ImageView();
			wall.setImage(wallTexture);
			wall.setFitWidth(SPACE_WIDTH);
			wall.setFitHeight(SPACE_HEIGHT);
			wall.setRotate(90 * (heading.ordinal() + 1));
			this.getChildren().add(wall);
		}

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

	/**
	 * Inserts an {@link ImageView} to the list of children depending on the space element.
	 * If no space element is present, inserts an {@link ImageView} containing a blank tile.
	 * The inserted {@link ImageView} may be rotated or transformed depending on the space element.
	 */
	private void addBackground() {
		ImageView tile = new ImageView();
		tile.setFitWidth(SPACE_WIDTH);
		tile.setFitHeight(SPACE_HEIGHT);
		this.getChildren().add(tile);

		if (space.getElement() == null) {
			tile.setImage(blankSquare);
		} else if (space.getElement() instanceof PriorityAntenna) {
			tile.setImage(antennaTexture);
		} else if (space.getElement() instanceof StartTile) {
			tile.setImage(startTileTexture);
		} else if (space.getElement() instanceof CheckPoint cp) {
			assert cp.checkPointNr <= 8;
			tile.setImage(checkpointTextures[cp.checkPointNr - 1]);
		} else if (space.getElement() instanceof Gears gears) {
			tile.setImage(gears.anticlockwise ? gearAnticlockwiseTexture : gearClockwiseTexture);
		} else if (space.getElement() instanceof ConveyorBelt belt) {
			int index = 0;
			Space neighbour;
			// Check if there's another conveyor pointing to this one on each of the neighbour spaces
			neighbour = space.board.getNeighbour(space, belt.heading.prev());
			if (neighbour != null && neighbour.getElement() instanceof ConveyorBelt neighbourBelt
				&& neighbourBelt.heading == belt.heading.next())
				index |= 1;
			neighbour = space.board.getNeighbour(space, belt.heading.next());
			if (neighbour != null && neighbour.getElement() instanceof ConveyorBelt neighbourBelt
				&& neighbourBelt.heading == belt.heading.prev())
				index |= 2;
			neighbour = space.board.getNeighbour(space, belt.heading.opposite());
			if (neighbour != null && neighbour.getElement() instanceof ConveyorBelt neighbourBelt
				&& neighbourBelt.heading == belt.heading)
				index |= 4;
			if (belt.fast)
				index |= 8;
			tile.setImage(conveyorTextures[index]);
			tile.setRotate(90 * (belt.heading.ordinal() + 2));
		}
	}

	private void updatePlayer() {
		Player player = space.getPlayer();
		this.getChildren().remove(playerNode);
		if (player == null) return;
		Polygon arrow = new Polygon(0.0, 0.0,
			10.0, 20.0,
			20.0, 0.0);
		try {
			arrow.setFill(Color.valueOf(player.getColor()));
		} catch (Exception e) {
			arrow.setFill(Color.MEDIUMPURPLE);
		}

		arrow.setRotate((90 * player.getHeading().ordinal()) % 360);
		playerNode = arrow;
		this.getChildren().add(arrow);
	}

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
        }
    }

	private static Image loadImage(String path) {
		InputStream stream = SpaceView.class.getClassLoader().getResourceAsStream(path);
		assert stream != null;
		return new Image(stream);
	}

	static {
		blankSquare = loadImage("assets/empty.png");
		wallTexture = loadImage("assets/wall.png");
		antennaTexture = loadImage("assets/antenna.png");
		startTileTexture = loadImage("assets/startTile.png");
		gearAnticlockwiseTexture = loadImage("assets/gearLeft.png");
		gearClockwiseTexture = loadImage("assets/gearRight.png");
		checkpointTextures = new Image[8];
		for (int i = 1; i <= 8; i++) {
			checkpointTextures[i - 1] = loadImage("assets/" + i + ".png");
		}
		conveyorTextures = new Image[16];
		String prefix = "assets/green";
		for (int i = 0; i < 16; ) {
			int straight = i;
			conveyorTextures[i++] = loadImage(prefix + ".png");
			conveyorTextures[i++] = loadImage(prefix + "TurnLeft.png");
			conveyorTextures[i++] = loadImage(prefix + "TurnRight.png");
			conveyorTextures[i++] = loadImage(prefix + "MergeSide.png");
			conveyorTextures[i++] = conveyorTextures[straight];
			conveyorTextures[i++] = loadImage(prefix + "MergeLeft.png");
			conveyorTextures[i++] = loadImage(prefix + "MergeRight.png");
			conveyorTextures[i++] = conveyorTextures[straight];
			prefix = "assets/blue";
		}
	}

}
