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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Player.PlayerStatus;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import dk.dtu.compute.se.pisd.roborally.controller.NetworkController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


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

    public BoardView(@NotNull GameController gameController, NetworkController networkController) {
        board = gameController.board;
		this.gameController = gameController;


		this.network = networkController;

        mainBoardPane = new GridPane();
        playersView = new PlayersView(gameController, network);
        statusLabel = new Label("<no status>");
		timeLabel = new Label();
		setTimer();
        infoLabel = new Label("Hello from right side");
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
                spaceView.setOnMouseClicked(spaceEventHandler);
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
	private long min, sec, totalSec = 0;

	private String format(long value){
		if(value < 10){
			return 0 + "" + value;
		}
		return value + "";

	}

	public void convertTime(){
		min = TimeUnit.SECONDS.toMinutes(totalSec);
		sec = totalSec - (min * 60);

		timeLabel.setText(format(min) + ":"+ format(sec));

		totalSec--;
	}

	private void setTimer(){
		totalSec = 5;
		Player p = board.getCurrentPlayer();
		Timer timer = new Timer();

		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					convertTime();
					if (totalSec <= 0) {


						gameController.AutoSelectCard(board.getCurrentPlayer());// TODO make this run the say done with programming cae


					}


				});
			}
		};

		timer.schedule(timerTask, 0, 1000);
	}

}
