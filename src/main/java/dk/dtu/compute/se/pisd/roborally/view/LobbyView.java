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

import org.springframework.stereotype.Component;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyNetworkScheduler;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */

@Component
public class LobbyView extends VBox implements ViewObserver {

    private Label lobbyLabel;
    private Button startButton;
    private HBox top;
    private TableView<ServerPlayer> playerListView;

    public LobbyView(LobbyNetworkScheduler lns) {
        lobbyLabel = new Label("Welcome to RoboRally lobby " + lns.getLobbyId());
        lobbyLabel.setFont(new Font(16));

        playerListView = new TableView<>();
        playerListView.setItems(lns.playersFetched);

        TableColumn<ServerPlayer, String> nameCol = new TableColumn<>("Player Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<ServerPlayer, Integer> idCol = new TableColumn<>("Player ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        playerListView.getColumns().addAll(nameCol, idCol);

        lns.updatePlayersInLobby();
        lns.setPeriod(Duration.seconds(GameController.POLLING_RATE));
        lns.start();

        startButton = new Button("Start Game");
        startButton.setOnAction( e -> lns.requestStartGame() );
        startButton.setFont(new Font(14));
        startButton.setPadding(new Insets(4, 30, 4, 30));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        top = new HBox(lobbyLabel, spacer, startButton);
        top.setPadding(new Insets(6));
        top.setAlignment(Pos.CENTER);

        this.getChildren().addAll(top, playerListView);
    }

    @Override
    public void updateView(Subject subject) {
        // TODO: Make this update view tick every once in a whilem and request new lobby info with REST
    }

}
