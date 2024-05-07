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
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(Board board) {
        this.board = board;
    }

    public void moveForward(@NotNull Player player) {
        moveAmt(player, 1);
    }

    // TODO Assignment A3
    public void fastForward(@NotNull Player player) {
        moveAmt(player, 2);
    }

    public void fastfastForward(@NotNull Player player) {
        moveAmt(player, 3);
    }

    public void moveAmt(@NotNull Player player, int amount) {
        if (player.board == board) {
            for (int i = 0; i < amount; i++) {
                Space target = board.getNeighbour(player.getSpace(), player.getHeading());
                if (target != null) {
                    try {
                        moveToSpace(player, target, player.getHeading());
                    } catch (ImpossibleMoveException e) {
                        // we don't do anything here  for now; we just catch the
                        // exception so that we do no pass it on to the caller
                        // (which would be very bad style).
                    }
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

    void moveToSpace(@NotNull Player player, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        assert board.getNeighbour(player.getSpace(), heading) == space; // make sure the move to here is possible in principle
		if (player.getSpace().getWalls().contains(heading) || space.getWalls().contains(heading.opposite()))
			throw new ImpossibleMoveException(player, space, heading);
        Player other = space.getPlayer();
        if (other != null){
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                // XXX Note that there might be additional problems with
                //     infinite recursion here (in some special cases)!
                //     We will come back to that!
                moveToSpace(other, target, heading);

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
		board.getPrioAntenna().updatePlayerPrio();
        board.setCurrentPlayer(board.getPrioPlayer(0));
        board.setStep(0);
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
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                    currentPlayer.setLastCardPlayed(card);
                }

				// After any player move, check space of all players, if checkpoint, activate checkpoint.
				for (Player p : board.getPlayers()) {
					if (p.getSpace().getElement() instanceof CheckPoint) {
						p.getSpace().getElement().doAction(this, p.getSpace());
					}
				}

                int nextPlayerNumber = board.getPrioPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPrioPlayer(nextPlayerNumber));
                } else {
					// For some reason, we can't just get a list of players???
					for (int i = 0; i < board.getPlayersNumber(); i++) {
						Space space = board.getPlayer(i).getSpace();
						SpaceElement element = space.getElement();
						if (element == null) continue;
						// TODO We should probably handle activation order
						element.doAction(this, space);
					}
                    step++;
					// Each time all players have made a move, recalculate priority
					board.getPrioAntenna().updatePlayerPrio();
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
						board.setCurrentPlayer(board.getPrioPlayer(0));
                    } else {
                        StartProgrammingPhase(true);
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

    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).
            //     player.setLastCardPlayed(currentCard); function calls the setLastCardPlayed and 
            //     copies the latest card for the Again card to use. 
            //

            CommandCard currentCard = new CommandCard(command);
            switch (command) {
                case FWD1:
                    this.moveForward(player);
                    player.setLastCardPlayed(currentCard);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    player.setLastCardPlayed(currentCard);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    player.setLastCardPlayed(currentCard);
                    break;
                case FWD2:
                    this.fastForward(player);
                    break;
                case FWD3:
                    this.fastfastForward(player);
                    break;
                case Back:
                    this.turnLeft(player);
                    this.turnLeft(player);
                    this.moveForward(player);
                    this.turnLeft(player);
                    this.turnLeft(player);
                    player.setLastCardPlayed(currentCard);
                    break;
                case UTRN:
                    this.turnLeft(player);
                    this.turnLeft(player);
                    player.setLastCardPlayed(currentCard);
                    break;
                case AGAN:
                    CommandCard lastCard = player.getLastCardPlayed();
                    if (lastCard != null && lastCard.command != Command.AGAN) {
                        executeCommand(player, lastCard.command);
                        
                    }
                    player.setLastCardPlayed(currentCard);
                    break;
                    default:
                    // DO NOTHING (for now)
            }
        }
    }

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
     * <p>Starts the {@link Phase#PROGRAMMING} Phase</p>
     * <p>If cards are NOT randomized, they have to be provided to the player before calling this function, since no cards will be loaded into the {@link Player} otherwise</p>
     * <p>TODO: Make current player to start align with priority antenna (currently always picking player 0 to begin)</p>
     * @param RandomizeCards    Determintes whether or not to randomize cards
     * @see CommandCardField
     */
    public void StartProgrammingPhase(Boolean RandomizeCards) {
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
                    if (RandomizeCards) field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
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
