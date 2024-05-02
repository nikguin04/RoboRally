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

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Serializer;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.view.LoadDialog;
import dk.dtu.compute.se.pisd.roborally.view.SaveDialog;
//import dk.dtu.compute.se.pisd.roborally.view.SaveDialog;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextField;

import org.jetbrains.annotations.NotNull;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * <p>Start a new game with players in default positions,
     * the user gets a popup dialog where they can select amount of players in new game.
     * This can only be called when there is not a game already being played.</p>
     *
     * <p>Modifies following variables in Roborally class: {@link Board}, {@link GameController}</p>
     * <p>The dialog box suspends all other use of the Roborally application.
     * It is a syncronous function which awaits a result from the dialog box on close</p>
     * <p>Uses variable {@link #PLAYER_NUMBER_OPTIONS} as {@link List} to choose player number from</p>
     *
     * @see ChoiceDialog
     * @see javafx.scene.control.Dialog
     */
    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            // XXX the board should eventually be created programmatically or loaded from a file
            //     here we just create an empty board with the required number of players.
            Board board = new Board(8,8);
            board.getSpace(2,2).setElement(new ConveyorBelt()); // WARN: TODO: This is for debugging json temporaryly and might be helpful to debug other parts of our program, delete this before production release
            gameController = new GameController(board);
            int no = result.get();
            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }

            // XXX: V2
            // board.setCurrentPlayer(board.getPlayer(0));
            gameController.StartProgrammingPhase(true);

            roboRally.createBoardView(gameController);
        }
    }

    /**
     * TODO: JAVADOC.
     * This javadoc will be implemented finally when "Simon Olsen" finishes save function
     */
    public void saveGame() {
        SaveDialog saveDialog = new SaveDialog();
        saveDialog.setTitle("Save Game");
        saveDialog.setHeaderText("Select File Name To Save");
        Optional<String> result = saveDialog.showAndWait();
        if(result.isPresent()){
            String filename = result.get();
            if (gameController != null) {
                Board board = gameController.board;

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Board.class, new Serializer.BoardSerializer());
                gsonBuilder.registerTypeAdapter(Player.class, new Serializer.PlayerSerializer());
                gsonBuilder.registerTypeAdapter(Command.class, new Serializer.CommandSerializer());
                gsonBuilder.registerTypeAdapter(Space.class, new Serializer.SpaceSerializer());
                Gson gson = gsonBuilder.setPrettyPrinting().create();

                String json = gson.toJson(board);

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/gamedata/" + filename + ".json"));
                    writer.write(json);
                    writer.close();
                } catch (IOException e) {
                    System.out.println("FAILED TO WRITE SAVE FILE: "  + e.getMessage());
                }
            }
        }

    }

    /**
     * <p>Load and start a previously saved game,
     * the user gets a popup dialog where they can select any save file to load.
     * This can only be called when there is not a game already being played.</p>
     *
     * <p>Modifies following variables in Roborally class: {@link Board}, {@link GameController}</p>
     * <p>The dialog box suspends all other use of the Roborally application.
     * It is a syncronous function which awaits a result from the dialog box on close</p>
     *
     * @see LoadDialog
     * @see javafx.scene.control.Dialog
     */
    public void loadGame() {
        LoadDialog<Board> dialog = new LoadDialog<>();
        dialog.setTitle("Load Hej");
        dialog.setHeaderText("Select path to load game from");
        Optional<Board> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            Board board = result.get();
            gameController = new GameController(board);

            gameController.StartProgrammingPhase(false); // TODO: Make sure to load the correct phase here
            roboRally.createBoardView(gameController);
        }

    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            //saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
