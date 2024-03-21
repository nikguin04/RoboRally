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

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * The class responsible for handling the overall game logic such as executing cards and transitioning to different phases.
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {

	final public Board board;

	public GameController(@NotNull Board board) {
		this.board = board;
	}
	/**
	 * This is just some dummy controller operation to make a simple move to see something
	 * happening on the board. This method should eventually be deleted!
	 * @param space The space to which the current player should move
	 */
	public void moveCurrentPlayerToSpace(@NotNull Space space)  {
		// TODO Task1: method should be implemented by the students:
		//   - the current player should be moved to the given space
		//     (if it is free())
		//   - and the current player should be set to the player
		//     following the current player
		//   - the counter of moves in the game should be increased by one
		//     if the player is moved

		if (space.getPlayer() == null) { // no player on space
			board.getCurrentPlayer().setSpace(space);
			board.incMoveCount(); // Increase move by one (ONLY IF MOVED)
		} else { /*player on space*/ }
		board.setCurrentPlayer(board.getNextPlayer());



	}

	/**
	 * Starts the programming phase, setting the current player and step,
	 * as well as giving each player new random cards and clearing the registers.
	 */
	public void startProgrammingPhase() {
		board.setPhase(Phase.PROGRAMMING);
		board.setCurrentPlayer(board.getPlayer(0));
		board.setStep(0);

		for (int i = 0; i < board.getPlayersNumber(); i++) {
			Player player = board.getPlayer(i);
			if (player != null) {
				for (int j = 0; j < Player.NO_REGISTERS; j++) {
					CommandCardField field = player.getProgramField(j);
					field.setCard(null);
					field.setVisible(true);
				}
				for (int j = 0; j < Player.NO_CARDS; j++) {
					CommandCardField field = player.getCardField(j);
					field.setCard(generateRandomCommandCard());
					field.setVisible(true);
				}
			}
		}
	}

	/**
	 * Generates a random command card based on the values of the {@link Command} enum.
	 * @return A new command card instance with a random command
	 */
	private CommandCard generateRandomCommandCard() {
		Command[] commands = Command.values();
		int random = (int) (Math.random() * commands.length);
		return new CommandCard(commands[random]);
	}

	/**
	 * Stops the programming phase and starts the activation phase,
	 * including resetting the current player and step, as well as making all but
	 * the first register visible for all players.
	 */
	public void finishProgrammingPhase() {
		makeProgramFieldsInvisible();
		makeProgramFieldsVisible(0);
		board.setPhase(Phase.ACTIVATION);
		board.setCurrentPlayer(board.getPlayer(0));
		board.setStep(0);
	}

	/**
	 * Makes a given register visible for all players.
	 * @param register The register to make visible
	 */
	private void makeProgramFieldsVisible(int register) {
		if (register >= 0 && register < Player.NO_REGISTERS) {
			for (int i = 0; i < board.getPlayersNumber(); i++) {
				Player player = board.getPlayer(i);
				CommandCardField field = player.getProgramField(register);
				field.setVisible(true);
			}
		}
	}

	/**
	 * Makes all registers invisible for all players.
	 */
	private void makeProgramFieldsInvisible() {
		for (int i = 0; i < board.getPlayersNumber(); i++) {
			Player player = board.getPlayer(i);
			for (int j = 0; j < Player.NO_REGISTERS; j++) {
				CommandCardField field = player.getProgramField(j);
				field.setVisible(false);
			}
		}
	}

	/**
	 * Turns off step mode and executes the rest of the activation phase.
	 */
	public void executePrograms() {
		board.setStepMode(false);
		continuePrograms();
	}

	/**
	 * Enables step mode and executes one step.
	 */
	public void executeStep() {
		board.setStepMode(true);
		continuePrograms();
	}

	/**
	 * Executes the next step. If step mode is disabled, continues executing steps
	 * until the activation phase is complete.
	 */
	private void continuePrograms() {
		do {
			executeNextStep();
		} while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
	}

	/**
	 * Executes the next step for the current player based on the card in their current field.
	 * It then sets the current player to be the next player. In case there is no next player,
	 * if all players have completed activation for the current field, it sets the current field to the next one.
	 * If all fields have been executed, it starts the programming phase.
	 */
	private void executeNextStep() {
		Player currentPlayer = board.getCurrentPlayer();
		if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
			int step = board.getStep();
			if (step >= 0 && step < Player.NO_REGISTERS) {
				CommandCard card = currentPlayer.getProgramField(step).getCard();
				if (card != null) {
					Command command = card.command;
					executeCommand(currentPlayer, command);
				}
				int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
				if (nextPlayerNumber < board.getPlayersNumber()) {
					board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
				} else {
					step++;
					if (step < Player.NO_REGISTERS) {
						makeProgramFieldsVisible(step);
						board.setStep(step);
						board.setCurrentPlayer(board.getPlayer(0));
					} else {
						startProgrammingPhase();
					}
				}
			} else {
				// this should not happen
				assert false;
			}
		} else {
			// this should not happen
			assert false;
		}
	}

	/**
	 * Executes a given command card for a given player.
	 * @param player The player to execute the command card for
	 * @param command The command card to execute
	 */
	private void executeCommand(@NotNull Player player, Command command) {
		if (player != null && player.board == board && command != null) {
			// XXX This is a very simplistic way of dealing with some basic cards and
			//     their execution. This should eventually be done in a more elegant way
			//     (this concerns the way cards are modelled as well as the way they are executed).

			switch (command) {
				case FORWARD:
					this.moveForward(player);
					break;
				case RIGHT:
					this.turnRight(player);
					break;
				case LEFT:
					this.turnLeft(player);
					break;
				case FAST_FORWARD:
					this.fastForward(player);
					break;
				default:
					// DO NOTHING (for now)
			}
		}
	}

	// DONE Task2

	/**
	 * Moveset for moving a player 1 step in the heading direction
	 * @param player the player to move
	 */
	public void moveForward(@NotNull Player player) {
		Space space = board.getNeighbour(player.getSpace(), player.getHeading());
		moveCurrentPlayerToSpace(space);
	}

	// DONE Task2

	/**
	 * Moveset for moving a player 2 step in the heading direction
	 * @param player the player to move
	 */
	public void fastForward(@NotNull Player player) {
		Space space = board.getNeighbour(player.getSpace(), player.getHeading());
		space = board.getNeighbour(space, player.getHeading());
		moveCurrentPlayerToSpace(space);
	}

	/**
	 * Change the direction of the robot from the robot perspective to the right
	 * @param player the player that changes direction
	 */
	// DONE Task2
	public void turnRight(@NotNull Player player) {
		Heading playerTurn = player.getHeading();
		playerTurn = playerTurn.next();
		player.setHeading(playerTurn);
	}

	/**
	 * Change the direction of the robot from the robot perspective to the left
	 * @param player the player that changes direction
	 */
	//DONE Task2
	public void turnLeft(@NotNull Player player) {
		Heading playerTurn = player.getHeading();
		playerTurn = playerTurn.prev();
		player.setHeading(playerTurn);
	}

	/**
	 * Moves a command card from one field to another.
	 * @param source The {@link CommandCardField} to take the card from
	 * @param target The {@link CommandCardField} to put the card into
	 * @return True if the card could be moved, false otherwise
	 */
	public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
		CommandCard sourceCard = source.getCard();
		CommandCard targetCard = target.getCard();
		if (sourceCard != null && targetCard == null) {
			target.setCard(sourceCard);
			source.setCard(null);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * A method called when no corresponding controller operation is implemented yet. This
	 * should eventually be removed.
	 */
	public void notImplemented() {
		// XXX just for now to indicate that the actual method is not yet implemented
		assert false;
	}

}
