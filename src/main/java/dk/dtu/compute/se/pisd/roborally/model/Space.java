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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.SpaceElement;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Space extends Subject {

    private Player player;

    private List<Heading> walls = new ArrayList<>();
    private SpaceElement element = null;

    public final Board board;

    public final int x;
    public final int y;

    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    public List<Heading> getWalls() {
        return walls;
    }

    public SpaceElement getElement() {
        return element;
    }

	public void setElement(SpaceElement element) {
		this.element = element;
	}

    public void copyAttributesFrom(Space news) {
        walls = news.walls;
        element = news.element;
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Space) {
            Space comp = ((Space)obj);
            boolean same = true;
            if (this.element != null && comp.element != null)
                same = (!same) ? false : this.element.getClass() == comp.element.getClass();
            else if (this.element == null || comp.element == null)
                same = (!same) ? false : this.element == null && comp.element == null;
            same = (!same) ? false : this.walls.equals(comp.walls);
            same = (!same) ? false : this.x == comp.x;
            same = (!same) ? false : this.y == comp.y;
            return same;
        }
        return false;
    }

}
