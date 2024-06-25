package dk.dtu.compute.se.pisd.roborally.net;

import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborallyserver.controller.MovesPlayedController;
import dk.dtu.compute.se.pisd.roborallyserver.controller.MovesPlayedController.NewMovesPlayBody;
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
	public static MovesPlayed requestNewMove(Long round, Command move1, Command move2, Command move3, Command move4, Command move5, Long lobby_id, Long player_id) {
		HttpEntity<MovesPlayedController.NewMovesPlayBody> request = new HttpEntity<>(new NewMovesPlayBody(round, move1, move2, move3, move4, move5, lobby_id, player_id));
		ResponseEntity<MovesPlayed> response = restTemplate
			.postForEntity(SERVER_HTTPURL + "movesplayed/newmovesplayed", request, MovesPlayed.class);
		return response.getBody();
	}


}
