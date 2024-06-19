package dk.dtu.compute.se.pisd.roborally.net;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.SERVER_HTTPURL;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborallyserver.controller.InteractionDecisionsController.NewInteractionDecisionBody;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;

public class InteractionDecisionRest {

	public static final RestTemplate restTemplate = new RestTemplate();

	public static Command requestInteractionDecision(long lobbyid, long playerid, long rounds, int step) {

		Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("lobbyid", String.valueOf(lobbyid));
        uriVariables.put("playerid", String.valueOf(playerid));
        uriVariables.put("rounds", String.valueOf(rounds));
        uriVariables.put("step", String.valueOf(step));

		ResponseEntity<Command> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "interactiondecisions/get?lobbyid={lobbyid}&playerid={playerid}&rounds={rounds}&step={step}", Command.class, uriVariables);
		return response.getBody(); // Should return null on error 400
	}

	public static String postInteractionDecision(NewInteractionDecisionBody intdec) {
		HttpEntity<NewInteractionDecisionBody> request = new HttpEntity<>(intdec);
		ResponseEntity<String> response = restTemplate
			.postForEntity(SERVER_HTTPURL + "interactiondecisions/submit", request, String.class);
		return response.getBody();
	}
}
