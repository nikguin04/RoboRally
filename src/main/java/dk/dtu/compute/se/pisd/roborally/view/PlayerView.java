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

import java.util.List;

import org.jetbrains.annotations.NotNull;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.NetworkController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Player.PlayerStatus;
import dk.dtu.compute.se.pisd.roborally.net.InteractionDecisionRest;
import dk.dtu.compute.se.pisd.roborallyserver.controller.InteractionDecisionsController.NewInteractionDecisionBody;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class PlayerView extends Tab implements ViewObserver {

    private Player player;

	private StackPane overlayPane;
    private VBox top;

    private Label programLabel;
    private GridPane programPane;
    private Label cardsLabel;
    private GridPane cardsPane;

    private CardFieldView[] programCardViews;
    private CardFieldView[] cardViews;

    private VBox buttonPanel;

    private Button finishButton;
    private Button executeButton;
    private Button stepButton;

    private VBox playerInteractionPanel;

    private GameController gameController;
	private NetworkController networkController;

    public PlayerView(@NotNull GameController gameController, @NotNull Player player, @NotNull NetworkController networkController) {
        super(player.getName());
        this.setStyle("-fx-text-base-color: " + player.getColor() + ";");

		top = new VBox();
		overlayPane = new StackPane(top);
		this.setContent(overlayPane);

        this.gameController = gameController;
        this.player = player;
		this.networkController = networkController;

        programLabel = new Label("Program");

        programPane = new GridPane();
        programPane.setVgap(2.0);
        programPane.setHgap(2.0);
        programCardViews = new CardFieldView[Player.NO_REGISTERS];
        for (int i = 0; i < Player.NO_REGISTERS; i++) {
            CommandCardField cardField = player.getProgramField(i);
            if (cardField != null) {
                programCardViews[i] = new CardFieldView(gameController, cardField);
                programPane.add(programCardViews[i], i, 0);
            }
        }

        // XXX  the following buttons should actually not be on the tabs of the individual
        //      players, but on the PlayersView (view for all players). This should be
        //      refactored.

        finishButton = new Button("Finish Programming");
//        finishButton.setOnAction( e -> gameController.finishProgrammingPhase());
		finishButton.setOnAction( e -> networkController.sendData(player));


        //executeButton = new Button("Execute Program");
        //executeButton.setOnAction( e-> gameController.executePrograms());

        //stepButton = new Button("Execute Current Register");
        //stepButton.setOnAction( e-> gameController.executeStep());

        //buttonPanel = new VBox(finishButton, executeButton, stepButton);
        buttonPanel = new VBox(finishButton);
        buttonPanel.setAlignment(Pos.CENTER_LEFT);
        buttonPanel.setSpacing(3.0);
        // programPane.add(buttonPanel, Player.NO_REGISTERS, 0); done in update now

        playerInteractionPanel = new VBox();
        playerInteractionPanel.setAlignment(Pos.CENTER_LEFT);
        playerInteractionPanel.setSpacing(3.0);

        cardsLabel = new Label("Command Cards");
        cardsPane = new GridPane();
        cardsPane.setVgap(2.0);
        cardsPane.setHgap(2.0);
        cardViews = new CardFieldView[Player.NO_CARDS];
        for (int i = 0; i < Player.NO_CARDS; i++) {
            CommandCardField cardField = player.getCardField(i);
            if (cardField != null) {
                cardViews[i] = new CardFieldView(gameController, cardField);
                cardsPane.add(cardViews[i], i, 0);
            }
        }

        top.getChildren().add(programLabel);
        top.getChildren().add(programPane);
        top.getChildren().add(cardsLabel);
        top.getChildren().add(cardsPane);

        if (player.board != null) {
            player.board.attach(this);
            update(player.board);
        }
    }

	@Override
	public void updateView(Subject subject) {
		if (subject != player.board) return;

		for (int i = 0; i < Player.NO_REGISTERS; i++) {
			CardFieldView cardFieldView = programCardViews[i];
			if (cardFieldView == null) continue;
			Background background;
			if (player.board.getPhase() == Phase.PROGRAMMING) {
				background = CardFieldView.BG_DEFAULT;
			} else if (i > player.board.getStep()) {
				background = CardFieldView.BG_DEFAULT;
			} else if (i < player.board.getStep()) {
				background = CardFieldView.BG_DONE;
			} else {
				if (player.board.getCurrentPlayer() == player) {
					background = CardFieldView.BG_ACTIVE;
				} else if (player.board.getPriorityPlayerNumber(player.board.getCurrentPlayer()) > player.board.getPriorityPlayerNumber(player)) {
					background = CardFieldView.BG_DONE;
				} else {
					background = CardFieldView.BG_DEFAULT;
				}
			}
			cardsPane.setBackground(background);
		}

		if (player.board.getPhase() != Phase.PLAYER_INTERACTION) { // Check that we are the serverplayer who needs to move
			if (!programPane.getChildren().contains(buttonPanel)) {
				programPane.getChildren().remove(playerInteractionPanel);
				programPane.add(buttonPanel, Player.NO_REGISTERS, 0);
			}
			switch (player.board.getPhase()) {
				case INITIALISATION -> {
					finishButton.setDisable(true);
					// XXX just to make sure that there is a way for the player to get
					//     from the initialization phase to the programming phase somehow!
					/*executeButton.setDisable(false);
					stepButton.setDisable(true);*/
				}
				case PROGRAMMING -> {
					finishButton.setDisable(false);
					/*executeButton.setDisable(true);
					stepButton.setDisable(true);*/
				}
				case ACTIVATION -> {
					finishButton.setDisable(true);
					/*executeButton.setDisable(false);
					stepButton.setDisable(false);*/
				}
				default -> {
					finishButton.setDisable(true);
					/*executeButton.setDisable(true);
					stepButton.setDisable(true);*/
				}
			}
			if (player.board.getPhase() == Phase.GAME_OVER) {
				Player winner = player.board.getWinner();
				Text playerName = new Text(winner.getName());
				Text text = new Text(" won!");
				TextFlow flow = new TextFlow(playerName, text);
				playerName.setFill(Color.valueOf(winner.getColor()));
				flow.setStyle("-fx-font-size: 4em;");
				flow.setTextAlignment(TextAlignment.CENTER);
				// VBox required to vertically centre text
				VBox box = new VBox(flow);
				box.setAlignment(Pos.CENTER);
				overlayPane.getChildren().add(box);
				// Decrease the opacity of everything else to make the text clearer
				// and to further mark that the game is over
				top.setOpacity(0.25);
			}
		} else {
			if (!programPane.getChildren().contains(playerInteractionPanel)) {
				programPane.getChildren().remove(buttonPanel);
				programPane.add(playerInteractionPanel, Player.NO_REGISTERS, 0);
			}
			playerInteractionPanel.getChildren().clear();

			if (player.board.getCurrentPlayer() == player) {
				// TODO Assignment A3: these buttons should be shown only when there is
				//      an interactive command card, and the buttons should represent
				//      the player's choices of the interactive command card. The
				//      following is just a mockup showing two options
				// TODO EMIL
				List<Command> options = player.getProgramField(player.board.getStep()).getCard().command.getOptions();
				for (int i = 0; i < options.size(); i++) {
					Button optionButton = new Button(player.getProgramField(player.board.getStep()).getCard().command.getOptions().get(i).displayName);
					final Integer hardI = i;
					optionButton.setOnAction(e -> {
						NewInteractionDecisionBody intdec = new NewInteractionDecisionBody(
							gameController.lobby.getRounds(), player.board.getStep(), gameController.lobby.getId(), player.getNetworkId(), options.get(hardI).name());
						InteractionDecisionRest.postInteractionDecision(intdec);
						Board board = gameController.board;
						for (int a = 0; a < board.getPlayersNumber(); a++) {
							Player player = board.getPlayer(a);
							player.playerStatus.set(PlayerStatus.IDLE);
						}
						gameController.executeCommandOptionAndContinue(player.getProgramField(player.board.getStep()).getCard().command.getOptions().get(hardI));
					});
					optionButton.setDisable(false);
					playerInteractionPanel.getChildren().add(optionButton);
				}
			}
		}
	}

}
