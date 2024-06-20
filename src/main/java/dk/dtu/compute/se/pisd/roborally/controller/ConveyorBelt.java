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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class ConveyorBelt extends SpaceElement {

    public final Heading heading;
    public final boolean fast;

    /**
     * Instantiate a slow ConveyorBelt with a default heading of north
     */
    public ConveyorBelt() {
        this.heading = Heading.NORTH;
        this.fast = false;
    }

    /**
     * Instantiate a slow ConveyorBelt with a specific heading
     * @param heading enum Heading
     */
    public ConveyorBelt(Heading heading) {
        this.heading = heading;
        this.fast = false;
    }

    /**
     * Instantiate a ConveyorBelt with a specific heading and whether it should be fast
     * @param heading enum Heading
     */
    public ConveyorBelt(Heading heading, boolean fast) {
        this.heading = heading;
        this.fast = fast;
    }

	private static boolean movePlayer(@NotNull GameController gameController, @NotNull Player player, @NotNull ConveyorBelt belt) {
		// TODO: Should pushing other players be allowed?
		try {
			gameController.moveToSpace(player, belt.heading);
			if (player.getSpace().getElement() instanceof ConveyorBelt second) {
				if (belt.heading.next() == second.heading) {
					gameController.turnRight(player);
				} else if (belt.heading.prev() == second.heading) {
					gameController.turnLeft(player);
				}
			}
			return true;
		} catch (GameController.ImpossibleMoveException e) {
			return false;
		}
	}

	@Override
	public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
		Player player = space.getPlayer();
		boolean success = movePlayer(gameController, player, this);
		if (fast && player.getSpace().getElement() instanceof ConveyorBelt second)
			success |= movePlayer(gameController, player, second);
		return success;
	}

}
