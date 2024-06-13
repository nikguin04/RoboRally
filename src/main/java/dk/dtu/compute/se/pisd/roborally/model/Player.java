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

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dk.dtu.compute.se.pisd.roborallyserver.model.MovesPlayed;
import org.jetbrains.annotations.NotNull;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.utils.CompareException;
import dk.dtu.compute.se.pisd.roborally.utils.FieldsCompare;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Player extends Subject {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    final public Board board;

    private String name;
    private String color;

    private Space space;
    private Heading heading = SOUTH;

    private CommandCardField[] program;
    private CommandCardField[] cards;
	private int checkPointCounter;
	private CommandCard lastCardPlayed;

    private Long playerNetworkID;
    public ObjectProperty<PlayerStatus> playerStatus;

    /**
     * {@inheritDoc}
     * @param board         Board on which player is located and interacts with
     * @param color         Color {@link String}, needs to comply with css colors, <a href="https://www.w3schools.com/cssref/css_colors.php">css colors (w3schools)</a>
     * @param name          Given name for a player, which will be used for identification during game
     *
     * @see Player#Player(Board, String, String, Command[])  Player() - For creating a player with predefined commands
     */
    public Player(@NotNull Board board, String color, @NotNull String name, @NotNull Long playerNetworkID) {
        this(board, color, name, null, playerNetworkID);
        for (int i = 0; i < this.cards.length; i++) {
            this.cards[i] = new CommandCardField(this);
        }

    }


    /**
     * Creates player
     * @param board asdasd
     * @param color
     * @param name
     * @param Commands given predefined commands
     *
     * @see Player#Player(Board, String, String)  Player() - For creating a player with blank commands
     */
    public Player(@NotNull Board board, String color, @NotNull String name, Command[] Commands, @NotNull Long playerNetworkID) {
        this.board = board;
        this.name = name;
        this.color = color;
        this.playerNetworkID = playerNetworkID;

        this.space = null;
		checkPointCounter = 0;
        playerStatus = new SimpleObjectProperty<PlayerStatus>(PlayerStatus.WAITING);

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }
        this.cards = new CommandCardField[NO_CARDS];
        if (Commands != null) {
            for (int i = 0; i < Commands.length; i++) {
                this.cards[i] = new CommandCardField(this);
                this.cards[i].setCard(new CommandCard(Commands[i]));
            }
        }

        //this.cards = cards;
    }

	/**
	 * Retrieves players checkpoint counter.
	 * @author Anders Greve Sørensen, s235093@dtu.dk
	 * @return Checkpoint counter of the player.
	 */
	public int getCheckPointCounter() {
		return this.checkPointCounter;
	}

	/**
	 * Sets players checkpoint counter to given value.
	 * @author Anders Greve Sørensen, s235093@dtu.dk
	 * @param checkPointCounter The checkpoint number of the last checkpoint player has passed. 0 if none have
	 *                          been passed yet.
	 */
	public void setCheckPointCounter(int checkPointCounter) {
		this.checkPointCounter = checkPointCounter;
	}
	public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    public CommandCard getLastCardPlayed() {
        return lastCardPlayed;
    }

    public void setLastCardPlayed(CommandCard card) {
        this.lastCardPlayed = card;
    }

    public Long getNetworkId() {
        return this.playerNetworkID;
    }

    /*public PlayerStatus getStatus() {
        return playerStatus.get();
    }
    public void setStatus(PlayerStatus status) {
        playerStatus.set(status);;
    }*/
    public enum PlayerStatus {
        READY("Ready"),
        WAITING("Waiting"),
        IDLE("Idle");

        final public String displayName;

        PlayerStatus(String displayName) {
            this.displayName = displayName;
        }
    }

	public void parseServerMovesToProgram(MovesPlayed moves) {
		CommandCardField[] ccfArray = new CommandCardField[5];
		ccfArray[0] = new CommandCardField(this);
		ccfArray[0].setCard(new CommandCard(Command.valueOf(moves.getMove1())));

		ccfArray[1] = new CommandCardField(this);
		ccfArray[1].setCard(new CommandCard(Command.valueOf(moves.getMove2())));

		ccfArray[2] = new CommandCardField(this);
		ccfArray[2].setCard(new CommandCard(Command.valueOf(moves.getMove3())));

		ccfArray[3] = new CommandCardField(this);
		ccfArray[3].setCard(new CommandCard(Command.valueOf(moves.getMove4())));

		ccfArray[4] = new CommandCardField(this);
		ccfArray[4].setCard(new CommandCard(Command.valueOf(moves.getMove5())));
	}


    @Override
    public boolean equals(Object obj) { // TODO: This should be made as the board, where all variables are checked
        if (obj instanceof Player comp) {
            try {
                FieldsCompare<Player> fc = new FieldsCompare<Player>();
                // Don't test player for an equal board, since the player can be identical but on another board.
                fc.CompareFields(this, comp, List.of("board"));
                return true;
            } catch (CompareException e) {
                return false;
            }
        }
        return false;
    }

}
