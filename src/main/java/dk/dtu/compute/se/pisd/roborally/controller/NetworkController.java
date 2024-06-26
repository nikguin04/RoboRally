package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.net.MovePlayedRest;

public class NetworkController {

	private GameController gameController;

	public NetworkController(GameController gameController) {
		this.gameController = gameController;
	}

	public void sendData(Player player) {
		gameController.autoSelectCards(player);
		MovePlayedRest.requestNewMove(
			gameController.board.lobby.getRounds(),
			player.getProgramField(0).getCommand(),
			player.getProgramField(1).getCommand(),
			player.getProgramField(2).getCommand(),
			player.getProgramField(3).getCommand(),
			player.getProgramField(4).getCommand(),
			gameController.board.lobby.getId(), player.getNetworkId()
		);
	}

}
