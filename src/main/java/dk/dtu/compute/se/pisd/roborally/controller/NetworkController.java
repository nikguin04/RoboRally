package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.net.MovePlayedRest;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

public class NetworkController {
	private GameController gameController;
	public NetworkController(GameController gameController) {
		this.gameController = gameController;
	}

	public void sendData(Player player){
		MovePlayedRest.requestNewMove(gameController.board.lobby.getRounds().intValue(), player.getProgramField(0).getCardName(),
			player.getProgramField(1).getCardName(), player.getProgramField(2).getCardName(), player.getProgramField(3).getCardName(),
			player.getProgramField(4).getCardName(), gameController.board.lobby.getId(), player.getNetworkId());
	}
}
