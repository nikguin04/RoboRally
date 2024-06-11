package dk.dtu.compute.se.pisd.roborally.net;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import dk.dtu.compute.se.pisd.roborallyserver.model.Map;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.SERVER_HTTPURL;

public class MapRest {
	public static final RestTemplate restTemplate = new RestTemplate();

	public static Map newMap(Long id, String mapString, Long numberOfPlayers, String mapName) {
		Map map = new Map(0L, "Hej", 5L, "Dig");
		HttpEntity<Map> request = new HttpEntity<>(map);
		ResponseEntity<Map> response = restTemplate
			.exchange(SERVER_HTTPURL + "maps/newmap", HttpMethod.POST, request, Map.class);
		return response.getBody();
	}
}
