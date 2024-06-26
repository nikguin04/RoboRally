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
package dk.dtu.compute.se.pisd.roborally.view;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.NetworkController;
import dk.dtu.compute.se.pisd.roborally.controller.TimerController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class BoardView extends VBox implements ViewObserver {

    private Board board;

    private HBox gameCenteredBox;
    private VBox infoPane;
    private Label infoLabel;
    private Label mapLabel;
    private List<Label> playerStatusLabels;

    private GridPane mainBoardPane;

    private SpaceView[][] spaces;

    private PlayersView playersView;

    private Label statusLabel;
	private Label timeLabel;
	private NetworkController network;

    private SpaceEventHandler spaceEventHandler;

	private GameController gameController;

	// Subject uses weak references, so we have to keep an explicit reference ourselves
	// so the observer doesn't get garbage collected immediately
	private final Observer timerObserver;

    public BoardView(@NotNull GameController gameController, NetworkController networkController) {
		this.gameController = gameController;
		this.board = gameController.board;
		this.network = networkController;

        mainBoardPane = new GridPane();
        playersView = new PlayersView(gameController, network);
        statusLabel = new Label("<no status>");

		timeLabel = new Label("--:--");
		timerObserver = timer -> Platform.runLater(() -> {
			long timeLeft = ((TimerController) timer).getTimeLeft();
			timeLabel.setText(timeLeft >= 0L
				? String.format("%02d:%02d", timeLeft / 60, timeLeft % 60)
				: "--:--");
		});
		gameController.getTimer().attach(timerObserver);

        infoLabel = new Label("Welcome to Roborally");
        mapLabel = new Label("Current map: " + gameController.lobby.getMap().getMapName());
        playerStatusLabels = new ArrayList<Label>();
        for (int i = 0; i < gameController.players.length; i++) {
            ServerPlayer p = gameController.players[i];
            Label l = new Label();
            l.textProperty().bind(Bindings.format("%s (%s)", p.getName(), gameController.board.getPlayer(i).playerStatus));
            l.setTextFill(Color.valueOf(gameController.board.getPlayer(i).getColor()));
            playerStatusLabels.add(l);
        }
        infoPane = new VBox(infoLabel, mapLabel, timeLabel);
        infoPane.getChildren().addAll(playerStatusLabels);

        gameCenteredBox = new HBox(mainBoardPane, infoPane);

        this.getChildren().add(gameCenteredBox);
        this.getChildren().add(playersView);
        this.getChildren().add(statusLabel);

        spaces = new SpaceView[board.width][board.height];

        spaceEventHandler = new SpaceEventHandler(gameController);

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                SpaceView spaceView = new SpaceView(space);
                spaces[x][y] = spaceView;
                mainBoardPane.add(spaceView, x, y);
                //spaceView.setOnMouseClicked(spaceEventHandler);
            }
        }

        board.attach(this);
        update(board);
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            Phase phase = board.getPhase();
            statusLabel.setText(board.getStatusMessage());
        }
    }

    // XXX this handler and its uses should eventually be deleted! This is just to help test the
    //     behaviour of the game by being able to explicitly move the players on the board!
    private class SpaceEventHandler implements EventHandler<MouseEvent> {

        final public GameController gameController;

        public SpaceEventHandler(@NotNull GameController gameController) {
            this.gameController = gameController;
        }

        @Override
        public void handle(MouseEvent event) {
            Object source = event.getSource();
            if (source instanceof SpaceView spaceView) {
                Space space = spaceView.space;
                Board board = space.board;

                if (board == gameController.board) {
                    gameController.moveCurrentPlayerToSpace(space);
                    event.consume();
                }
            }
        }

    }

}
