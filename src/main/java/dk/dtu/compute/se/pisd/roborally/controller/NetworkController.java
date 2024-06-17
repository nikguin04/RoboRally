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
		gameController.AutoSelectCard(player);
		MovePlayedRest.requestNewMove(gameController.board.lobby.getRounds(), player.getProgramField(0).getCardEnumName(),
			player.getProgramField(1).getCardEnumName(), player.getProgramField(2).getCardEnumName(), player.getProgramField(3).getCardEnumName(),
			player.getProgramField(4).getCardEnumName(), gameController.board.lobby.getId(), player.getNetworkId());
	}
}
