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
import dk.dtu.compute.se.pisd.roborally.model.Player.PlayerStatus;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;

import javafx.application.Platform;
import javafx.util.Duration;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public static double POLLING_RATE = 0.25;

    final public Board board;
    final public ServerPlayer splayer;
    final public Lobby lobby;
    final public ServerPlayer[] players;

    public GameController(Board board, ServerPlayer splayer, Lobby lobby, ServerPlayer[] players) {
        this.board = board;
        this.splayer = splayer;
        this.lobby = lobby;
        this.players = players;
		timer = new TimerController(board, new NetworkController(this));
    }

    public void moveForward(@NotNull Player player) {
        moveAmt(player, 1);
    }

	@Getter
	private TimerController timer;
    // TODO Assignment A3
    public void fastForward(@NotNull Player player) {
        moveAmt(player, 2);
    }

    public void fastfastForward(@NotNull Player player) {
        moveAmt(player, 3);
    }

	public void moveAmt(@NotNull Player player, int amount) {
		if (player.board != board) return;
		for (int i = 0; i < amount; i++) {
			Space target = board.getNeighbour(player.getSpace(), player.getHeading());
			if (target != null) {
				try {
					moveToSpace(player, player.getHeading());
				} catch (ImpossibleMoveException e) {
					// we don't do anything here  for now; we just catch the
					// exception so that we don't pass it on to the caller
					// (which would be very bad style).
				}
			}
		}
	}

    // TODO Assignment A3
    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().next());
    }

    // TODO Assignment A3
    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());
    }

    void moveToSpace(@NotNull Player player, @NotNull Heading heading) throws ImpossibleMoveException {
		Space space = board.getNeighbour(player.getSpace(), heading);
		if (board.isObstructed(player.getSpace(), heading))
			throw new ImpossibleMoveException(player, space, heading);
        Player other = space.getPlayer();
        if (other != null) {
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                // XXX Note that there might be additional problems with
                //     infinite recursion here (in some special cases)!
                //     We will come back to that!
                moveToSpace(other, heading);

                // Note that we do NOT embed the above statement in a try catch block, since
                // the thrown exception is supposed to be passed on to the caller

                assert target.getPlayer() == null : target; // make sure target is free now
            } else {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        player.setSpace(space);
    }

    public void moveCurrentPlayerToSpace(Space space) {
        // TODO: Import or Implement this method. This method is only for debugging purposes. Not useful for the game.
        if (space.getPlayer() == null) { // no player on space
			board.getCurrentPlayer().setSpace(space);
			board.incMoveCount(); // Increase move by one (ONLY IF MOVED)
		} else { /*player on space*/ }
		board.setCurrentPlayer(board.getNextPlayer());
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    public void finishProgrammingPhase() {
		makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
		// When entering activation phase, calculate priority of players.
		board.getPriorityAntenna().updatePlayerPriority();
        board.setCurrentPlayer(board.getPriorityPlayer(0));
        board.setStep(0);
    }

	public void autoSelectCards(Player player){
		for (int i = 0; i < 5; i++){
			if(player.getProgramField(i).getCard() == null) {
				while (true) {
					int randomCardIndex = (int)(Math.random() * 8);
					CommandCard randomCard = player.getCardField(randomCardIndex).getCard();
					if (randomCard != null) {
						player.getProgramField(i).setCard(randomCard);
						player.getCardField(randomCardIndex).setCard(null);
						break;
					}
				}
			}
		}
	}

	public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

	private void executeNextStep() {
		Player currentPlayer = board.getCurrentPlayer();
		int step = board.getStep();
		assert board.getPhase() == Phase.ACTIVATION && currentPlayer != null;
		assert step >= 0 && step < Player.NO_REGISTERS;
		CommandCard card = currentPlayer.getProgramField(step).getCard();
		if (card != null) {
			Command command = card.command;
			if (command == Command.OPTION_LEFT_RIGHT) {
				board.setPhase(Phase.PLAYER_INTERACTION);
                // Start awaiting result
                Platform.runLater(() -> {
                    for (int i = 0; i < board.getPlayersNumber(); i++) {
                        Player p = board.getPlayer(i);
                        p.playerStatus.set( currentPlayer.equals(p) ? PlayerStatus.WAITING : PlayerStatus.IDLE );
                    }
                });
                if (currentPlayer.getNetworkId() != splayer.getId()) {
                    InteractionDecisionScheduler ids = new InteractionDecisionScheduler(this, lobby, currentPlayer.getNetworkId());
                    ids.setPeriod(Duration.seconds(POLLING_RATE));
                    ids.start();
                }
				return;
			}
			executeCommand(currentPlayer, command);
			currentPlayer.setLastCardPlayed(card);
		}

		handleAfterStep();
	}

	public void executeCommandOptionAndContinue(Command command){
		board.setPhase(Phase.ACTIVATION);
		executeCommand(board.getCurrentPlayer(), command);

		handleAfterStep();
        // Start the execution of activation/programming cards again
        startAutoActivationExecution();
	}

    public void startAutoActivationExecution() {
		// WARNING: this needs to be canceled again by setting phase to something other than activation
		ProgramPhaseScheduler pps = new ProgramPhaseScheduler(this);
		pps.setPeriod(Duration.seconds(1));
		pps.start();
	}

	private void handleAfterStep() {
		Player currentPlayer = board.getCurrentPlayer();
		int step = board.getStep();

		int nextPlayerNumber = board.getPriorityPlayerNumber(currentPlayer) + 1;
		if (nextPlayerNumber < board.getPlayersNumber()) {
			board.setCurrentPlayer(board.getPriorityPlayer(nextPlayerNumber));
		} else { // else = we have reached the final step
			int numCheckpoints = board.getNumCheckpoints();
			for (Player player : board.getPlayers()) {
				Space space = player.getSpace();
				SpaceElement element = space.getElement();
				if (element == null) continue;
				// TODO We should probably handle activation order
				element.doAction(this, space);
				// Check if the player has won
				if (numCheckpoints > 0 && player.getCheckPointCounter() == numCheckpoints) {
					board.setWinner(player);
					board.setPhase(Phase.GAME_OVER);
				}
			}
			step++;
			// Each time all players have made a move, recalculate priority
			board.getPriorityAntenna().updatePlayerPriority();
			if (step < Player.NO_REGISTERS) {
				makeProgramFieldsVisible(step);
				board.setStep(step);
				board.setCurrentPlayer(board.getPriorityPlayer(0));
			} else {
				startProgrammingPhase(true);
			}
		}
	}

	private void executeCommand(@NotNull Player player, Command command) {
		if (player == null || player.board != board || command == null) return;

		// XXX This is a very simplistic way of dealing with some basic cards and
		//     their execution. This should eventually be done in a more elegant way
		//     (this concerns the way cards are modelled as well as the way they are executed).

		switch (command) {
			case LEFT  -> this.turnLeft(player);
			case RIGHT -> this.turnRight(player);
			case FWD1  -> this.moveForward(player);
			case FWD2  -> this.fastForward(player);
			case FWD3  -> this.fastfastForward(player);
			case Back -> {
				this.turnLeft(player);
				this.turnLeft(player);
				this.moveForward(player);
				this.turnLeft(player);
				this.turnLeft(player);
			}
			case UTRN -> {
				this.turnLeft(player);
				this.turnLeft(player);
			}
			case OPTION_LEFT_RIGHT -> {
				board.setPhase(Phase.PLAYER_INTERACTION);
			}
			case AGAN -> {
				CommandCard lastCard = player.getLastCardPlayed();
				if (lastCard != null && lastCard.command != Command.AGAN) {
					executeCommand(player, lastCard.command);
				}
			}
			default -> {}
		}
	}

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard == null || targetCard != null)
			return false;
        target.setCard(sourceCard);
        source.setCard(null);
        return true;
    }

    /**
     * <p>Starts the {@link Phase#PROGRAMMING} Phase</p>
     * <p>If cards are NOT randomized, they have to be provided to the player before calling this function, since no cards will be loaded into the {@link Player} otherwise</p>
     * <p>TODO: Make current player to start align with priority antenna (currently always picking player 0 to begin)</p>
     * @param randomizeCards    Determines whether or not to randomize cards
     * @see CommandCardField
     */
    public void startProgrammingPhase(Boolean randomizeCards) {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayerByNetworkId(splayer.getId()));
        board.setStep(0);
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            player.playerStatus.set(PlayerStatus.WAITING);
            if (player == null) continue;
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setCard(null);
                field.setVisible(true);
            }
            for (int j = 0; j < Player.NO_CARDS; j++) {
                CommandCardField field = player.getCardField(j);
                if (randomizeCards) field.setCard(generateRandomCommandCard());
                field.setVisible(true);
            }
        }
		MoveNetworkScheduler mns = new MoveNetworkScheduler(board.lobby, splayer, this);
		mns.setPeriod(Duration.seconds(POLLING_RATE));
		timer.startTimer();
		mns.start();
    }

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

    class ImpossibleMoveException extends Exception {

        private Player player;
        private Space space;
        private Heading heading;

        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
            this.player = player;
            this.space = space;
            this.heading = heading;
        }
    }

}
