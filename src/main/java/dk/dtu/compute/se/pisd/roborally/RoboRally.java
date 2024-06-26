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
package dk.dtu.compute.se.pisd.roborally;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyNetworkScheduler;
import dk.dtu.compute.se.pisd.roborally.controller.NetworkController;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import dk.dtu.compute.se.pisd.roborally.view.LobbyView;
import dk.dtu.compute.se.pisd.roborally.view.RoboRallyMenuBar;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRally extends Application {

    private static final int MIN_APP_WIDTH = 600;

    public static final String SERVER_HTTPURL = "http://localhost:8080/";

    private Stage stage;
    private BorderPane boardRoot;

	private NetworkController network;

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        // Set icon for game
        ClassLoader loader = RoboRally.class.getClassLoader();
        primaryStage.getIcons().add(new Image(loader.getResourceAsStream("assets/logo.jpg")));

        AppController appController = new AppController(this);

        // Create the primary scene with a menu bar and a pane for
        // the board view (which initially is empty); it will be filled
        // when the user creates a new game or loads a game
        RoboRallyMenuBar menuBar = new RoboRallyMenuBar(appController);
        boardRoot = new BorderPane();
        VBox vbox = new VBox(menuBar, boardRoot);
        vbox.setMinWidth(MIN_APP_WIDTH);

        boardRoot.setCenter(startMenuView(appController));

        Scene primaryScene = new Scene(vbox);
        stage.setScene(primaryScene);
        stage.setTitle("RoboRally");
        stage.setOnCloseRequest(
                e -> {
                    e.consume();
                    appController.exit();} );
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    public void createBoardView(GameController gameController, NetworkController networkController) {
        // if present, remove old BoardView
        boardRoot.getChildren().clear();
        //this.network = networkController;
        if (gameController != null) {
            // create and add view for new board
            BoardView boardView = new BoardView(gameController, networkController);
            boardRoot.setCenter(boardView);
        }

        stage.sizeToScene();
    }

    public void createLobbyView(AppController appController, Lobby lobby, ServerPlayer splayer) {
        boardRoot.getChildren().clear();
        LobbyNetworkScheduler lns = new LobbyNetworkScheduler(appController, lobby, splayer);
        LobbyView lobbyView = new LobbyView(lns);
        boardRoot.setCenter(lobbyView);

        // dont know if i need this
        stage.sizeToScene();
    }

    public Node startMenuView(AppController appController){
        VBox vbox = new VBox();
        Button newLobbyButoon = new Button("New lobby");
        newLobbyButoon.setFont(new Font(20));
        newLobbyButoon.setPrefWidth(MIN_APP_WIDTH/2);
        newLobbyButoon.setOnAction( e -> appController.newLobby());

        Button joinButoon = new Button("Join lobby");
        joinButoon.setFont(new Font(20));
        joinButoon.setPrefWidth(MIN_APP_WIDTH/2);
        joinButoon.setOnAction( e -> appController.joinLobby());

        Button exitButoon = new Button("Exit");
        exitButoon.setFont(new Font(20));
        exitButoon.setPrefWidth(MIN_APP_WIDTH/2);
        exitButoon.setOnAction( e -> appController.exit());

        vbox.setSpacing(30);
        vbox.setPadding(new Insets(50));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(newLobbyButoon, joinButoon, exitButoon);
        return vbox;
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // XXX just in case we need to do something here eventually;
        //     but right now the only way for the user to exit the app
        //     is delegated to the exit() method in the AppController,
        //     so that the AppController can take care of that.
    }

    public static void main(String[] args) {
        launch(args);
		// There seems to be some hanging thread(s) after the window is closed
		// So make sure we actually exit by explicitly shutting down the JVM
		System.exit(0);
    }

}
