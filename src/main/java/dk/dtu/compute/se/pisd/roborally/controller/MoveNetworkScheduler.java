package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Player.PlayerStatus;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.MovesPlayed;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.SERVER_HTTPURL;

public class MoveNetworkScheduler extends ScheduledService<Void> {

	private Lobby lobby;
	private ServerPlayer splayer;

	private Board board;
	private GameController gameController;

	public MoveNetworkScheduler(Lobby lobby, ServerPlayer splayer, GameController gameController) {
		this.lobby = lobby;
		this.splayer = splayer;
		this.board = gameController.board;
		this.gameController = gameController;
	}

	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				checkAllPLayerMoves();
				return null;
			}
		};
	}

	public void checkAllPLayerMoves() {
		Platform.runLater(() -> {
			ServerPlayer[] finishPlayers = isFinishedProgramming(lobby.getId(), lobby.getRounds().intValue());
			for (ServerPlayer sp: finishPlayers) {
				board.getPlayerByNetworkId(sp.getId()).playerStatus.set(Player.PlayerStatus.READY);
			}

			if (finishPlayers.length == board.getPlayersNumber()) {
				cancel(); // Cancel task timer
				MovesPlayed[] playersMovesToClient = requestAllPlayerMoves(lobby.getId(), lobby.getRounds().intValue());

				// TODO: Dont increment lobby round here, request the lobby again, probably?
				lobby.setRounds(lobby.getRounds() + 1);

				for (MovesPlayed moves: playersMovesToClient) {
					Player p = board.getPlayerByNetworkId(moves.getPlayerId());
					p.parseServerMovesToProgram(moves);
					p.playerStatus.set(PlayerStatus.IDLE);
				}
				gameController.finishProgrammingPhase();
				// Start the autimatic step scheduler
				gameController.startAutoActivationExecution();
			}
		});
	}

	public static ServerPlayer[] isFinishedProgramming(long lobbyId, int round) {
		final RestTemplate restTemplate = new RestTemplate();
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("lobbyid", String.valueOf(lobbyId));
		uriVariables.put("round", String.valueOf(round));

		ResponseEntity<ServerPlayer[]> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "movesplayed/finishedplayers?lobbyid={lobbyid}&round={round}", ServerPlayer[].class, uriVariables);
		return response.getBody();
	}

	public static MovesPlayed[] requestAllPlayerMoves(long lobbyId, int round) {
		final RestTemplate restTemplate = new RestTemplate();
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("lobbyid", String.valueOf(lobbyId));
		uriVariables.put("round", String.valueOf(round));

		ResponseEntity<MovesPlayed[]> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "movesplayed/roundmoves?lobbyid={lobbyid}&round={round}", MovesPlayed[].class, uriVariables);
		return response.getBody();
	}

}
