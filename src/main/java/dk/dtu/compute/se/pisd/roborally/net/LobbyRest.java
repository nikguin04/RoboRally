package dk.dtu.compute.se.pisd.roborally.net;

import org.hibernate.mapping.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.SERVER_HTTPURL;

import java.util.HashMap;
import java.util.Map;

public class LobbyRest {

	public static final RestTemplate restTemplate = new RestTemplate();

	public static Lobby requestNewLobby(dk.dtu.compute.se.pisd.roborallyserver.model.Map map) {
		Lobby lobby = new Lobby(null, 0L, map, false, null);
		HttpEntity<Lobby> request = new HttpEntity<>(lobby);
		ResponseEntity<Lobby> response = restTemplate
			.postForEntity(SERVER_HTTPURL + "lobbies", request, Lobby.class);
		return response.getBody();
	}

	public static ServerPlayer[] requestPlayersByLobbyId(Long id) {

		Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("id", String.valueOf(id));

		ResponseEntity<ServerPlayer[]> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "lobbies/{id}/players", ServerPlayer[].class, uriVariables);
		return response.getBody();
	}

	public static Lobby[] requestJoinableLobbies() {
		ResponseEntity<Lobby[]> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "lobbies/joinable", Lobby[].class);
		return response.getBody();
	}

	// Don't return success or not, the client should poll itself to find out if game has started
	public static void requestStartGame(Lobby lobby) {
		Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("id", String.valueOf(lobby.getId()));

		restTemplate.postForEntity(SERVER_HTTPURL + "lobbies/{id}/startgame", null, String.class, uriVariables);
	}

	public static Lobby requestLobbyById(Long id) {
		Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("id", String.valueOf(id));

		ResponseEntity<Lobby>response = restTemplate
			.getForEntity(SERVER_HTTPURL + "lobbies/{id}", Lobby.class, uriVariables);
		return response.getBody();
	}

}
