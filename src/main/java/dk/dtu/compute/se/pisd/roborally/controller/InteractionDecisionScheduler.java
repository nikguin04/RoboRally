package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Player.PlayerStatus;
import dk.dtu.compute.se.pisd.roborally.net.InteractionDecisionRest;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class InteractionDecisionScheduler extends ScheduledService<Void> {

	private GameController gameController;
	private Lobby lobby;
    private Long splayerIdToDecide;
	public InteractionDecisionScheduler(GameController gameController, Lobby lobby, Long splayerIdToDecide) {
		this.lobby = lobby;
		this.splayerIdToDecide = splayerIdToDecide;
		this.gameController= gameController;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
               	fetchInteractionDecision();
				return null;
            }
        };
	}

	private void fetchInteractionDecision() {
		Command cmd = InteractionDecisionRest.requestInteractionDecision(lobby.getId(), splayerIdToDecide, lobby.getRounds(), gameController.board.getStep());

		if (cmd != null) {
			Platform.runLater(() -> {
				cancel(); // Cancel the timer, since we got a proper answer
				Board board = gameController.board;
				for (int i = 0; i < board.getPlayersNumber(); i++) {
                    Player p = board.getPlayer(i);
                    p.playerStatus.set(PlayerStatus.IDLE);
                }
				gameController.executeCommandOptionAndContinue(cmd);
			});
		}
	}


}
