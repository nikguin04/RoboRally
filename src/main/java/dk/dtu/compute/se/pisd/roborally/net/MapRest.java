package dk.dtu.compute.se.pisd.roborally.net;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import dk.dtu.compute.se.pisd.roborallyserver.model.Map;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.SERVER_HTTPURL;

public class MapRest {
	public static final RestTemplate restTemplate = new RestTemplate();

	public static Map getMap(String mapName) {
		java.util.Map<String, String> uriVariables = java.util.Map.of("name", mapName);
		ResponseEntity<Map> response = restTemplate
			.getForEntity(SERVER_HTTPURL + "maps/{name}", Map.class, uriVariables);
		return response.getBody();
	}

	public static Map newMap(String mapName, String mapJSON, Long numberOfPlayers) {
		Map map = new Map(mapName, mapJSON, numberOfPlayers);
		HttpEntity<Map> request = new HttpEntity<>(map);
		ResponseEntity<Map> response = restTemplate
			.postForEntity(SERVER_HTTPURL + "maps", request, Map.class);
		return response.getBody();
	}
}
