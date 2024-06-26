package dk.dtu.compute.se.pisd.roborally.net;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.SERVER_HTTPURL;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborallyserver.controller.InteractionDecisionController.NewInteractionDecisionBody;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;

public class InteractionDecisionRest {

	public static final RestTemplate restTemplate = new RestTemplate();

	public static Command requestInteractionDecision(long lobbyId, long playerId, long round, int step) {
		Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("lobbyid", String.valueOf(lobbyId));
        uriVariables.put("playerid", String.valueOf(playerId));
        uriVariables.put("round", String.valueOf(round));
        uriVariables.put("step", String.valueOf(step));

		ResponseEntity<Command> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "interactiondecisions?lobbyid={lobbyid}&playerid={playerid}&round={round}&step={step}", Command.class, uriVariables);
		return response.getBody(); // Should return null on error 400
	}

	public static String postInteractionDecision(NewInteractionDecisionBody intdec) {
		HttpEntity<NewInteractionDecisionBody> request = new HttpEntity<>(intdec);
		ResponseEntity<String> response = restTemplate
			.postForEntity(SERVER_HTTPURL + "interactiondecisions", request, String.class);
		return response.getBody();
	}

}
