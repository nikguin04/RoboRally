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
import dk.dtu.compute.se.pisd.roborally.controller.PriorityAntenna;
import dk.dtu.compute.se.pisd.roborally.controller.StartTile;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public final Lobby lobby;

    private Integer gameId;

    private final Space[][] spaces;

	private PriorityAntenna priorityAntenna;

    private StartTile startTile;

	@Getter @Setter
	private int numCheckpoints;
	private Map<Long, Player> playerNetworkIdIndex = new HashMap<>();

    private List<Player> players = new ArrayList<>();

	private List<Player> prioritisedPlayers = new ArrayList<>();

    private Player current;

	@Getter @Setter
	private Player winner;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    private int move_count = 0;

    public Board(int width, int height, Lobby lobby) {
		this.width = width;
        this.height = height;
        this.lobby = lobby;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
					Space space = new Space(this, x, y);
					spaces[x][y] = space;
            }
        }
        this.stepMode = false;
    }

    // Board for patching tests (DO NOT USE FOR NEW DEVELOPING!)
    public Board(int w, int h) {
        this(w,h,null);
    }

	/**
	 * Gives the priority antenna of the board.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @return the priority antenna of this board.
	 */
	public PriorityAntenna getPriorityAntenna() {
		return this.priorityAntenna;
	}

	/**
	 * Retrieves list of all players.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @return list of Player objects.
	 */
	public List<Player> getPlayers() {
		return this.players;
	}

	/**
	 * Attaches a given priority antenna to this board.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param priorityAntenna the priority antenna to be attached to this board.
	 */
	public void setPriorityAntenna(PriorityAntenna priorityAntenna, Space space) {
		priorityAntenna.attachBoard(this, space);
		this.priorityAntenna = priorityAntenna;
	}

    /**
	 * attaches a given StartTile to this board.
	 * @author Emil Thostrup Pedersen, s235105@dtu.dk.
	 * @param startTile the StartTile to be attached to this board.
	 */
	public void setStartTile(StartTile startTile) {
        startTile.attachBoard(this);
		this.startTile = startTile;
	}

    /**
	 * Gives the priority antenna of the board.
	 * @author Emil Thostrup, s235105@dtu.dk.
	 * @return the StartTile of this board.
	 */
	public StartTile getStartTile() {
		return this.startTile;
	}

	/**
	 * Retrieve this board's list of prioritised players.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @return the board's list of prioritised players.
	 */
	public List<Player> getPrioritisedPlayers() {
		return prioritisedPlayers;
	}

	/**
	 * Set this boards prioritised players list to the given list of players.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param prioritisedPlayerList The player list to set this boards prioritised players list to.
	 */
	public void setPrioritisedPlayers(List<Player> prioritisedPlayerList) {
		this.prioritisedPlayers = prioritisedPlayerList;
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
        if (x >= 0 && x < width && y >= 0 && y < height) {
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
            playerNetworkIdIndex.put(player.getNetworkId(), player);
            players.add(player);
            notifyChange();
        }
    }

	/**
	 * Add a player to this board's prioritised players list.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param player the player to be added to the prioritised players list.
	 */
	public void addPriorityPlayer(@NotNull Player player) {
		if (player.board == this && !prioritisedPlayers.contains(player)) {
			prioritisedPlayers.add(player);
			notifyChange();
		}
	}

	/**
	 * Get a player from the prioritised player list, given its index.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param i the index of the player in the prioritised player list.
	 * @return the player with index i in this board's prioritised player list, null if index is out of range.
	 */
	public Player getPriorityPlayer(int i) {
		if (i >= 0 && i < prioritisedPlayers.size()) {
			return prioritisedPlayers.get(i);
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

    public Player getPlayerByNetworkId(Long id) {
        return playerNetworkIdIndex.get(id);
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
	 * Gets the index of the given player in the prioritised player list of this board.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param player The player the find the index of.
	 * @return the index of the given player in the boards prioritised players list.
	 */
	public int getPriorityPlayerNumber(@NotNull Player player) {
		if (player.board == this) {
			return prioritisedPlayers.indexOf(player);
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

    public void setMoveCount(int move_count) {
		this.move_count = move_count;
	}

    public Player getNextPlayer() {
		return this.getPlayer((this.getPlayerNumber(this.getCurrentPlayer()) + 1) % this.getPlayersNumber());
	}

	/**
	 * Returns the neighbour of the given space of the board in the given heading.
	 * If there is no space in the given direction because the given space is at the
	 * edge of the board, null is returned.
	 *
	 * @param space   the space for which the neighbour should be computed
	 * @param heading the heading of the neighbour
	 * @return the space in the given direction; null if there is no neighbour
	 */
	public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
		int x = space.x;
		int y = space.y;
		switch (heading) {
			case SOUTH -> y++;
			case WEST  -> x--;
			case NORTH -> y--;
			case EAST  -> x++;
		}
		return getSpace(x, y);
	}

	public boolean isObstructed(@NotNull Space space, @NotNull Heading heading) {
		if (space.getWalls().contains(heading))
			return true;
		Space nextSpace = getNeighbour(space, heading);
		if (nextSpace == null)
			return false;
		return nextSpace.getWalls().contains(heading.opposite())
			|| nextSpace.getElement() instanceof PriorityAntenna;
	}

    public String getStatusMessage() {
        return "Phase: " + getPhase().name() +
        ", Player = " + getCurrentPlayer().getName() +
        ", Step: " + getStep() +
        ", Move: " + getMoveCount();
    }
}
