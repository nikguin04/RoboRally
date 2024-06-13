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
			ServerPlayer[] finishPlayers = isFinishedProgramming(lobby.getId());
			for (ServerPlayer sp: finishPlayers) {
				board.getPlayerByNetworkId(sp.getId()).playerStatus.set(Player.PlayerStatus.READY);
			}

			if (finishPlayers.length == board.getPlayersNumber()) {
				cancel(); // Cancel task timer
				MovesPlayed[] playersMovesToClient = requestAllPlayerMoves(lobby.getId());

				for (MovesPlayed moves: playersMovesToClient) {
					Player p = board.getPlayerByNetworkId(moves.getPlayerId());
					p.parseServerMovesToProgram(moves);
				}
				gameController.finishProgrammingPhase();
			}
		});
	}

	public static ServerPlayer[] isFinishedProgramming(long lobbyid) {
		final RestTemplate restTemplate = new RestTemplate();
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("lobbyid", String.valueOf(lobbyid));

		ResponseEntity<ServerPlayer[]> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "movesplayed/lobbyroundfinished?lobbyid={lobbyid}", ServerPlayer[].class, uriVariables);
		return response.getBody();
	}
	public  static MovesPlayed[] requestAllPlayerMoves(long lobbyid){
		final RestTemplate restTemplate = new RestTemplate();
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("lobbyid", String.valueOf(lobbyid));

		ResponseEntity<MovesPlayed[]> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "movesplayed/getPlayersMoves?lobbyid={lobbyid}", MovesPlayed[].class, uriVariables);
		return response.getBody();
	}
}