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
import dk.dtu.compute.se.pisd.roborally.controller.PrioAntenna;
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
		} else if (space.getElement() instanceof PrioAntenna) {
			tile.setImage(antennaTexture);
		} else if (space.getElement() instanceof StartTile) {
			tile.setImage(startTileTexture);
		} else if (space.getElement() instanceof CheckPoint cp) {
			assert cp.checkPointNr <= 8;
			tile.setImage(checkpointTextures[cp.checkPointNr - 1]);
		} else if (space.getElement() instanceof ConveyorBelt belt) {
			int index = 0;
			Space neighbour;
			// Check if there's another conveyor pointing to this one on each of the neighbour spaces
			neighbour = space.board.getNeighbour(space, belt.getHeading().prev());
			if (neighbour != null && neighbour.getElement() instanceof ConveyorBelt neighbourBelt
				&& neighbourBelt.getHeading() == belt.getHeading().next())
				index |= 1;
			neighbour = space.board.getNeighbour(space, belt.getHeading().next());
			if (neighbour != null && neighbour.getElement() instanceof ConveyorBelt neighbourBelt
				&& neighbourBelt.getHeading() == belt.getHeading().prev())
				index |= 2;
			neighbour = space.board.getNeighbour(space, belt.getHeading().opposite());
			if (neighbour != null && neighbour.getElement() instanceof ConveyorBelt neighbourBelt
				&& neighbourBelt.getHeading() == belt.getHeading())
				index |= 4;
			tile.setImage(conveyorTextures[index]);
			tile.setRotate(90 * (belt.getHeading().ordinal() + 2));
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

	static {
		ClassLoader loader = SpaceView.class.getClassLoader();
		blankSquare = new Image(loader.getResourceAsStream("assets/empty.png"));
		wallTexture = new Image(loader.getResourceAsStream("assets/wall.png"));
		antennaTexture = new Image(loader.getResourceAsStream("assets/antenna.png"));
		startTileTexture = new Image(loader.getResourceAsStream("assets/startTile.png"));
		checkpointTextures = new Image[8];
		for (int i = 1; i <= 8; i++) {
			checkpointTextures[i - 1] = new Image(loader.getResourceAsStream("assets/" + i + ".png"));
		}
		conveyorTextures = new Image[8];
		conveyorTextures[0] = new Image(loader.getResourceAsStream("assets/green.png"));
		conveyorTextures[1] = new Image(loader.getResourceAsStream("assets/greenTurnLeft.png"));
		conveyorTextures[2] = new Image(loader.getResourceAsStream("assets/greenTurnRight.png"));
		conveyorTextures[3] = new Image(loader.getResourceAsStream("assets/greenMergeSide.png"));
		conveyorTextures[4] = conveyorTextures[0];
		conveyorTextures[5] = new Image(loader.getResourceAsStream("assets/greenMergeLeft.png"));
		conveyorTextures[6] = new Image(loader.getResourceAsStream("assets/greenMergeRight.png"));
		conveyorTextures[7] = conveyorTextures[0];
	}

}
