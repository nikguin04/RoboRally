package dk.dtu.compute.se.pisd.roborally.controller;

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

import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.SERVER_HTTPURL;

public class MoveNetworkScheduler extends ScheduledService<Void> {

private Lobby lobby;
private ServerPlayer splayer;

	public MoveNetworkScheduler(Lobby lobby, ServerPlayer splayer) {
		this.lobby = lobby;
		this.splayer = splayer;
	}
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				UpdatePlayersCard();
				return null;
			}
		};
	}

	public void UpdatePlayersCard() {
		Platform.runLater(() -> {
			List<MovesPlayed> playerMovesList = List.of();

			MovesPlayed move = isFinishedProgramming();
			System.out.println(move);
		});
	}

	public static MovesPlayed isFinishedProgramming() {
		final RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<MovesPlayed> response = restTemplate
			.exchange(SERVER_HTTPURL + "movesplayed/finishedprogramming", HttpMethod.GET, null, MovesPlayed.class);
		return response.getBody();
	}
}
