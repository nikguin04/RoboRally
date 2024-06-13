package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.net.LobbyRest;
import dk.dtu.compute.se.pisd.roborallyserver.controller.MovesPlayedController;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.MovesPlayed;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import dk.dtu.compute.se.pisd.roborallyserver.repository.MovesPlayedRepository;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
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


				CheckAllPLayerMoves();
				return null;
			}
		};
	}

	public void CheckAllPLayerMoves() {
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
				}
				gameController.finishProgrammingPhase();
				// Start the autimatic step scheduler
				ProgramPhaseScheduler pps = new ProgramPhaseScheduler(gameController);
				pps.setPeriod(Duration.seconds(1));
				pps.start();
			}
		});
	}

	public static ServerPlayer[] isFinishedProgramming(long lobbyid, int round) {
		final RestTemplate restTemplate = new RestTemplate();
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("lobbyid", String.valueOf(lobbyid));
		uriVariables.put("round", String.valueOf(round));

		ResponseEntity<ServerPlayer[]> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "movesplayed/lobbyroundfinished?lobbyid={lobbyid}&round={round}", ServerPlayer[].class, uriVariables);
		return response.getBody();
	}
	public  static MovesPlayed[] requestAllPlayerMoves(long lobbyid, int round){
		final RestTemplate restTemplate = new RestTemplate();
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("lobbyid", String.valueOf(lobbyid));
		uriVariables.put("round", String.valueOf(round));

		ResponseEntity<MovesPlayed[]> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "movesplayed/getPlayersMoves?lobbyid={lobbyid}&round={round}", MovesPlayed[].class, uriVariables);
		return response.getBody();
	}
}
