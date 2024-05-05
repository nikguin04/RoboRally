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

	final public static Image blankSquare = new Image(SpaceView.class.getClassLoader().getResourceAsStream("assets/empty.png"));
	final public static Image wallTexture = new Image(SpaceView.class.getClassLoader().getResourceAsStream("assets/wall.png"));

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

		ImageView tile = new ImageView();
		tile.setFitWidth(SPACE_WIDTH);
		tile.setFitHeight(SPACE_HEIGHT);
		this.getChildren().add(tile);

		if (space.getElement() == null) {
			tile.setImage(blankSquare);
		}

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

}
