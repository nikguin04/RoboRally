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
	public static Lobby requestNewLobby(int board_map_id) {
		Lobby lobby = new Lobby((Long)null, Long.valueOf(0), Long.valueOf(board_map_id), false);
		HttpEntity<Lobby> request = new HttpEntity<>(lobby);
		ResponseEntity<Lobby> response = restTemplate
			.exchange(SERVER_HTTPURL + "lobbies/newlobby", HttpMethod.POST, request, Lobby.class);
		return response.getBody();
	}

	public static ServerPlayer[] requestPlayersByLobbyId(Long id) {

		Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("id", String.valueOf(id));

		ResponseEntity<ServerPlayer[]> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "lobbies/players?id={id}", ServerPlayer[].class, uriVariables);
		return response.getBody();
	}

	public static Lobby[] requestJoinableLobbies() {
		ResponseEntity<Lobby[]> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "lobbies/joinable", Lobby[].class);
		return response.getBody();
	}

	public static String requestStartGame(Lobby lobby) { // Dont return success or not, the client should poll itself to find out if game has started
		Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("id", String.valueOf(lobby.getId()));

		ResponseEntity<String> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "lobbies/startgame?id={id}", String.class, uriVariables);
		return response.getBody();
	}

	public static Lobby requestLobbyById(Long id) {
		Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("id", String.valueOf(id));

		ResponseEntity<Lobby>response = restTemplate
			.getForEntity(SERVER_HTTPURL + "lobbies/getlobby?id={id}", Lobby.class, uriVariables);
		return response.getBody();
	}

}
