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

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.NetworkController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.scene.control.TabPane;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class PlayersView extends TabPane implements ViewObserver {

    private Board board;
	private NetworkController network;

    private PlayerView[] playerViews;

    public PlayersView(GameController gameController, NetworkController networkController) {
        board = gameController.board;
		this.network = networkController;
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        PlayerView focus = null;

        playerViews = new PlayerView[board.getPlayersNumber()];
        for (int i = 0; i < board.getPlayersNumber();  i++) {
            playerViews[i] = new PlayerView(gameController, board.getPlayer(i), network);
            this.getTabs().add(playerViews[i]);
            if (gameController.splayer.getId() != board.getPlayer(i).getNetworkId()) { playerViews[i].setDisable(true); }
            else { focus = playerViews[i]; }
        }
        board.attach(this);
        update(board);
        this.getSelectionModel().select(focus); // We can only focus after the board attach, therefore we store the PlayerView focus till after the attach is called
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            Player current = board.getCurrentPlayer();
            this.getSelectionModel().select(board.getPlayerNumber(current));
        }
    }

}
