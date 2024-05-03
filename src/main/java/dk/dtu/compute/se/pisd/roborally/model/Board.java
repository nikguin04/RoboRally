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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    private Integer gameId;

    private final Space[][] spaces;

	private PrioAntenna prioAntenna;

    private List<Player> players = new ArrayList<>();

	private List<Player> priotizedPlayers = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    private int move_count = 0;

    public Board(int width, int height) {
		this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
					Space space = new Space(this, x, y);
					spaces[x][y] = space;
            }
        }
        this.stepMode = false;
    }

	/**
	 * Gives the priority antenna of the board.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @return the priority antenna of this board.
	 */
	public PrioAntenna getPrioAntenna() {
		return this.prioAntenna;
	}

	/**
	 * Retrieves list of all players.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @return list of Player objects.
	 */
	public List<Player> getPlayers() {
		return this.players;
	}

//	/**
//	 * Sets the player list of the board, to the given list of players.
//	 * @author Anders Greve Sørensen, s235093@dtu.dk
//	 * @param playerList The list of players to set for the board.
//	 */
////	public void setPlayers(List<Player> playerList) {
////		this.players = playerList;
////	}
//
////
////	public Space[][] getSpaces() {
////		return this.spaces;
////	}

	/**
	 * attaches a given priority antenna to this board.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param prioAntenna the priority antenna to be attached to this board.
	 */
	public void setPrioAntenna(PrioAntenna prioAntenna) {
		this.prioAntenna = prioAntenna;
	}

	/**
	 * retrieve this boards list of prioritized players.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @return the boards list of prioritized players.
	 */
	public List<Player> getPrioritizedPlayers() {
		return priotizedPlayers;
	}

	/**
	 * Set this boards prioritized players list to the given list of players.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param prioPlayerList The player list to set this boards prioritized players list to.
	 */
	public void setPrioritizedPlayers(List<Player> prioPlayerList) {
		this.priotizedPlayers = prioPlayerList;
	}


	public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }


    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public int getPlayersNumber() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

	/**
	 * Add a player to this board's prioritized players list.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param player the player to be added to the prioritized players list.
	 */
	public void addPrioPlayer(@NotNull Player player) {
		if (player.board == this && !priotizedPlayers.contains(player)) {
			priotizedPlayers.add(player);
			notifyChange();
		}
	}

	/**
	 * Get a player from the prioritized player list, given its index.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param i the index of the player in the prioritized player list.
	 * @return the player with index i in this board's prioritized player list. null if index is out of range.
	 */
	public Player getPrioPlayer(int i) {
		if (i >= 0 && i < priotizedPlayers.size()) {
			return priotizedPlayers.get(i);
		}
		else {return null;}
	}

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

	/**
	 * Gets the index of the given player in the prioritized player list of this board.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param player The player the find the index of.
	 * @return the index of the given player in the boards prioritized players list.
	 */
	public int getPrioPlayerNumber(@NotNull Player player) {
		if (player.board == this) {
			return priotizedPlayers.indexOf(player);
		} else {
			return -1;
		}
	}

    public int getMoveCount() {
		return move_count;
	}

	public void incMoveCount() {
		move_count++;
	}

    public Player getNextPlayer() {
		return this.getPlayer((this.getPlayerNumber(this.getCurrentPlayer()) + 1) % this.getPlayersNumber());
	}

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        // TODO needs to be implemented based on the actual spaces
        //      and obstacles and walls placed there. For now it,
        //      just calculates the next space in the respective
        //      direction in a cyclic way.

        // XXX an other option (not for now) would be that null represents a hole
        //     or the edge of the board in which the players can fall

        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }
	    return getSpace(x, y);
    }

    public String getStatusMessage() {
        return "Phase: " + getPhase().name() +
        ", Player = " + getCurrentPlayer().getName() +
        ", Step: " + getStep() +
        ", Move: " + getMoveCount();
    }
}
