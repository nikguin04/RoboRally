package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.net.MovePlayedRest;
import dk.dtu.compute.se.pisd.roborally.view.CardFieldView;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.NetworkController;

public class NetworkController {
	private CardFieldView[] programCardViews;
	private GameController gameController;
//	private Player player;

	public NetworkController(GameController gameController, Player player) {
		this.gameController = gameController;
//		this.player = player;
	}

	public void sendData(Player player){
		MovePlayedRest.requestNewMove(gameController.board.lobby.getRounds().intValue(), player.getProgramField(0).getCardName(),
			player.getProgramField(1).getCardName(), player.getProgramField(2).getCardName(), player.getProgramField(3).getCardName(),
			player.getProgramField(4).getCardName(), gameController.board.lobby.getId(), 1L);
	}
}
