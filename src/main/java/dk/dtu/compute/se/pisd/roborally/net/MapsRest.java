package dk.dtu.compute.se.pisd.roborally.net;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.Maps;

import static dk.dtu.compute.se.pisd.roborally.RoboRally.SERVER_HTTPURL;





public class MapsRest {
    public static final RestTemplate restTemplate = new RestTemplate();
    public static Maps newMap(Long id, String MapString, Long NumberOfPlayers, String MapName) {
        Maps map = new Maps(Long.valueOf(0), "Hej",Long.valueOf(5), "Dig" );
        HttpEntity<Maps> request = new HttpEntity<>(map);
        ResponseEntity<Maps> response = restTemplate
            .exchange(SERVER_HTTPURL + "maps/newmaps", HttpMethod.POST, request, Maps.class);
        return response.getBody();
    }
}    


