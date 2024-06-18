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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.net.MapRest;
import dk.dtu.compute.se.pisd.roborallyserver.model.Map;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Serializer;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.net.LobbyRest;
import dk.dtu.compute.se.pisd.roborally.net.PlayerRest;
import dk.dtu.compute.se.pisd.roborally.view.LoadDialog;
import dk.dtu.compute.se.pisd.roborally.view.SaveDialog;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

import org.springframework.web.client.HttpServerErrorException;

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
    private String playerName = null;

    private GameController gameController;
	private NetworkController network;
	private ServerPlayer sPlayer;

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
     * It is a synchronous function which awaits a result from the dialog box on close</p>
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
                // Give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            // XXX the board should eventually be created programmatically or loaded from a file

            Board board = new Board(8,8, null);

			// TODO: This is very temporary for debugging, delete this when proper boards are loaded
			board.getSpace(2,2).setElement(new ConveyorBelt());
			board.getSpace(2,4).setElement(new CheckPoint(1));
			board.getSpace(4,5).setElement(new CheckPoint(2));
			board.setNumCheckpoints(2);
			// Add the priority antenna to the board
			PrioAntenna prioAntenna = new PrioAntenna(5,5);
            board.setPrioAntenna(prioAntenna);

            // Set the startTile on the board
            for(int i = 0; i < PLAYER_NUMBER_OPTIONS.get(0); i++){ //TODO Make this so that the number of players is the number of players in the lobbey
                StartTile startTile = new StartTile(i,0);
                board.setStartTile(startTile);
            }


            // Set Player on startTile
            gameController = new GameController(board, new ServerPlayer(0l, "", null), null, null);
            int no = result.get();
            Player player;
            int i = 0;
            int x = 0;
            for(int g = 0; g < board.width; g++){
                for(int j = 0; j < board.height; j++){
                    if (i >= no) {
                        x = 1;
                        break;
                    }
                    if(board.getSpace(g, j).getElement() instanceof StartTile){
                        player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i+1), 0l); // hardcoded 0 for netowork id since this is offline game
                        board.addPlayer(player);
                        board.addPrioPlayer(player);
                        player.setSpace(board.getSpace(g, j));
                        i += 1;
                    }
                }
                if(x == 1){
                    break;
                }

            }
			network = new NetworkController(gameController);
            // XXX: V2
            // board.setCurrentPlayer(board.getPlayer(0));
            gameController.StartProgrammingPhase(true);

            roboRally.createBoardView(gameController, network);
        }
    }

	public void newLobby() {
		if (playerName == null) changeName();

		List<String> files = new ArrayList<>();
		try {
			Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("boards"); // replace with your directory path

			while (resources.hasMoreElements()) {
				URL directoryURL = resources.nextElement();
				List<String> filenames = IOUtils.readLines(directoryURL.openStream(), StandardCharsets.UTF_8);
				for (String filename : filenames) {
					if (filename.endsWith(".json")) {
						files.add(filename.substring(0, filename.length() - ".json".length()));
					}
				}
			}
		} catch (IOException ignored) {}

		ChoiceDialog<String> mapChoiceDialog = new ChoiceDialog<>();
		mapChoiceDialog.setTitle("Choose board");
		mapChoiceDialog.setHeaderText("Please choose the board to use");
		mapChoiceDialog.getItems().addAll(files);
		mapChoiceDialog.setSelectedItem(files.get(0));

		Optional<String> result = mapChoiceDialog.showAndWait();
		if (result.isEmpty()) return;
		String mapName = result.get();

		Map map;
		try {
			String mapJSON = IOUtils.resourceToString("boards/" + mapName + ".json", StandardCharsets.UTF_8, AppController.class.getClassLoader());
			map = MapRest.newMap(mapName, mapJSON, 0L);
		} catch (IOException e) { return; }

		Lobby lobby = LobbyRest.requestNewLobby(map);

		ServerPlayer splayer = PlayerRest.PushPlayerToLobby(lobby.getId(), playerName);

		roboRally.createLobbyView(this, lobby, splayer);
	}

	public void joinLobby() {
		if (playerName == null) changeName();

		Dialog dialog = new Dialog<>();
		dialog.setTitle("Join Lobby");
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().autosize();

		List<Integer> availableLobbies = new ArrayList<>();

		Lobby[] joinableLobbies = LobbyRest.requestJoinableLobbies();
		for (Lobby l : joinableLobbies) {
			availableLobbies.add(l.getId().intValue());
		}

		ObservableList<String> lobbyNames = FXCollections.observableArrayList();

		for (Integer lobby : availableLobbies) {
			lobbyNames.add("Lobby " + lobby);
		}

		ListView<String> listView = new ListView<String>(lobbyNames);

		VBox layout = new VBox(10);
		layout.setPadding(new Insets(10, 20, 5, 20));

		TextField textField = new TextField();
		textField.setPromptText("Lobby ID");

		layout.getChildren().addAll(listView, textField);
		dialog.getDialogPane().setContent(layout);

		listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				textField.setText(newValue.substring(6));
			}
		});

		Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		okButton.setDisable(true);
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			boolean isValid = false;
			try {
				Integer lobby = Integer.valueOf(newValue);
				isValid = availableLobbies.contains(lobby);
			} catch (NumberFormatException ignored) { }
			okButton.setDisable(!isValid);
		});

		if (dialog.showAndWait().get() == ButtonType.OK) {
			String result = textField.getText();
			if (result == null || result.isEmpty()) return;
			try {
				Lobby lobby = new Lobby(Long.valueOf(textField.getText()), 0L, null, false, null); // TODO: TEMP VARIABLE, add actual lobby fetching
				ServerPlayer splayer = PlayerRest.PushPlayerToLobby(lobby.getId(), playerName);
				roboRally.createLobbyView(this, lobby, splayer);
			} catch (HttpServerErrorException e) {
				Alert alert = new Alert(AlertType.ERROR, "There was an error when trying to join the lobby.", ButtonType.OK);
				alert.setHeaderText("Something went wrong on the server");
				alert.showAndWait();
			}
		}
	}

	public void initGameFromLobbyStart(Lobby lobby, ServerPlayer[] players, ServerPlayer splayer) {
		Map map = lobby.getMap();
		Board board = LoadBoard.loadBoard(new StringReader(map.getJson()), lobby);

		int playerCount = players.length;

		// Set Player on startTile
		gameController = new GameController(board, splayer, lobby, players);
		Player player;
		int i = 0;
		outerLoop:
		for (int x = 0; x < board.width; x++) {
			for (int y = 0; y < board.height; y++) {
				if (i >= playerCount) {
					break outerLoop;
				}
				if (board.getSpace(x, y).getElement() instanceof StartTile) {
					player = new Player(board, PLAYER_COLORS.get(i), players[i].getName(), players[i].getId());
					board.addPlayer(player);
					board.addPrioPlayer(player);
					player.setSpace(board.getSpace(x, y));
					i++;
				}
			}
		}
		assert i == playerCount : "There wasn't enough start tiles for all the players";
		this.network = new NetworkController(gameController);
		gameController.StartProgrammingPhase(true);

		roboRally.createBoardView(gameController, network);
	}

	public void changeName() {
		TextInputDialog dialog = new TextInputDialog(playerName != null ? playerName : "Player");
		dialog.setTitle("Choose new name");
		dialog.setHeaderText("Please input your wanted name");
		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()) {
			playerName = result.get();
		} else {
			playerName = dialog.getDefaultValue();
		}
	}

	/**
	 * Start up a dialog with user and
	 * calls on the saveFile function, to save the file,
	 * with the name the user has giving it
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

                saveFile(board, filename);
            }
        }

    }

	/**
	 *
	 * @param board
	 * @param filename
	 * Saves a game to Json
	 */
	public void saveFile(Board board, String filename){
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

    /**
     * <p>Load and start a previously saved game,
     * the user gets a popup dialog where they can select any save file to load.
     * This can only be called when there is not a game already being played.</p>
     *
     * <p>Modifies following variables in Roborally class: {@link Board}, {@link GameController}</p>
     * <p>The dialog box suspends all other use of the Roborally application.
     * It is a synchronous function which awaits a result from the dialog box on close</p>
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
                // Give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            Board board = result.get();
            loadBoardIntoGame(board);
        }
    }

    private void loadBoardIntoGame(Board board) {

        gameController = new GameController(board, new ServerPlayer(0l, "", null), null, null);

        gameController.StartProgrammingPhase(false); // TODO: Make sure to load the correct phase here
        roboRally.createBoardView(gameController, network);
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

            // Here we save the game (without asking the user).
            //saveGame();

            gameController = null;
            roboRally.createBoardView(null, null);
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

            if (result.isEmpty() || result.get() != ButtonType.OK) {
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
