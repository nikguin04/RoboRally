package dk.dtu.compute.se.pisd.roborally.net;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.SERVER_HTTPURL;
import static dk.dtu.compute.se.pisd.roborallyserver.controller.PlayerController.NewPlayerBody;

public class PlayerRest {

	public static final RestTemplate restTemplate = new RestTemplate();
	public static ServerPlayer PushPlayerToLobby(Long lobby_id, String player_name) {
		HttpEntity<NewPlayerBody> request = new HttpEntity<>(new NewPlayerBody(player_name, lobby_id));
		ResponseEntity<ServerPlayer> response = restTemplate
			.exchange(SERVER_HTTPURL + "players/newplayer", HttpMethod.POST, request, ServerPlayer.class);
		return response.getBody();
	}


}
