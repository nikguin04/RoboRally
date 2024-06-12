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

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyNetworkScheduler;
import dk.dtu.compute.se.pisd.roborally.net.LobbyRest;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */

@Component
public class LobbyView extends VBox implements ViewObserver {

    private TableView<ServerPlayer> playerListView;

    private Label lobbyLabel;
    private Label statusLabel;

    private Button startButton;


    public LobbyView(LobbyNetworkScheduler lns) {


        lobbyLabel = new Label("Welcome to the Roborally lobby");

        playerListView = new TableView<ServerPlayer>();
        playerListView.setItems(lns.playersFetched);

        TableColumn<ServerPlayer,String> nameCol = new TableColumn<ServerPlayer,String>("Player name");
        nameCol.setCellValueFactory(new PropertyValueFactory<ServerPlayer,String>("name"));
        TableColumn<ServerPlayer,Integer> idCol = new TableColumn<ServerPlayer,Integer>("Player id");
        idCol.setCellValueFactory(new PropertyValueFactory<ServerPlayer,Integer>("id"));

        playerListView.getColumns().setAll(nameCol, idCol);

        lns.updatePlayersInLobby();

        lns.setPeriod(Duration.seconds(1));
        lns.start();


        startButton = new Button("Start game");
        startButton.setOnAction( e -> lns.requestStartGame() );



        statusLabel = new Label("<no status>");

        this.getChildren().add(lobbyLabel);
        this.getChildren().add(playerListView);
        this.getChildren().addAll(startButton);
        this.getChildren().add(statusLabel);

    }


    @Override
    public void updateView(Subject subject) {
        // TODO: Make this update view tick every once in a whilem and request new lobby info with REST

    }


}
