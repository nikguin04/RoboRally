package dk.dtu.compute.se.pisd.roborally.net;

import dk.dtu.compute.se.pisd.roborallyserver.controller.MovesPlayedController;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.MovesPlayed;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static dk.dtu.compute.se.pisd.roborallyserver.controller.MovesPlayedController.NewMovesPlayBody;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.SERVER_HTTPURL;
import static dk.dtu.compute.se.pisd.roborallyserver.controller.PlayerController.NewPlayerBody;

public class MovePlayedRest {

	public static final RestTemplate restTemplate = new RestTemplate();
	public static MovesPlayed requestNewMove(int round, String move1, String move2, String move3, String move4, String move5, Long lobby_id, Long player_id) {
		HttpEntity<MovesPlayedController.NewMovesPlayBody> request = new HttpEntity<>(new NewMovesPlayBody(round, move1, move2, move3, move4, move5, lobby_id, player_id));
		ResponseEntity<MovesPlayed> response = restTemplate
			.exchange(SERVER_HTTPURL + "movesplayed/newmovesplayed", HttpMethod.POST, request, MovesPlayed.class);
		return response.getBody();
	}


}
