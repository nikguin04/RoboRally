package dk.dtu.compute.se.pisd.roborally.net;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.SERVER_HTTPURL;
import static dk.dtu.compute.se.pisd.roborallyserver.controller.PlayerController.NewPlayerBody;

public class PlayerRest {

	public static final RestTemplate restTemplate = new RestTemplate();

	public static ServerPlayer pushPlayerToLobby(Long lobbyId, String playerName) {
		HttpEntity<NewPlayerBody> request = new HttpEntity<>(new NewPlayerBody(playerName, lobbyId));
		ResponseEntity<ServerPlayer> response = restTemplate
			.postForEntity(SERVER_HTTPURL + "players", request, ServerPlayer.class);
		return response.getBody();
	}

}
