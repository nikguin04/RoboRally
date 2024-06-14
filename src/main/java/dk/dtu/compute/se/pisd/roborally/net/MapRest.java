package dk.dtu.compute.se.pisd.roborally.net;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import dk.dtu.compute.se.pisd.roborallyserver.model.Map;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.SERVER_HTTPURL;

public class MapRest {
	public static final RestTemplate restTemplate = new RestTemplate();

	public static Map getMap(Long id) {
		java.util.Map<String, String> uriVariables = java.util.Map.of("id", id.toString());
		ResponseEntity<Map> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "maps/get?id={id}", Map.class, uriVariables);
		return response.getBody();
	}

	public static Map newMap(String mapJSON, Long numberOfPlayers, String mapName) {
		Map map = new Map(0L, mapJSON, numberOfPlayers, mapName);
		HttpEntity<Map> request = new HttpEntity<>(map);
		ResponseEntity<Map> response = restTemplate
			.postForEntity(SERVER_HTTPURL + "maps/newmap", request, Map.class);
		return response.getBody();
	}
}
