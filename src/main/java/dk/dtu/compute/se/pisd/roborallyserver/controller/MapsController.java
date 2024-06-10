package dk.dtu.compute.se.pisd.roborallyserver.controller;


import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.Maps;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.MapsRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.PlayerRepository;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.RSocket.Server;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//Base endpoint
@RequestMapping("/maps")
public class MapsController {

    private MapsRepository MapsRepository;
    private LobbyRepository lobbyRepository;

    public MapsController(PlayerRepository playerRepository, LobbyRepository lobbyRepository) {
        this.MapsRepository = MapsRepository;
        this.lobbyRepository = lobbyRepository;
    }

    @GetMapping
    //Specific endpoint for the method
    @RequestMapping(value = "")
    public ResponseEntity<List<Maps>> FindAllMaps(){
        List<Maps> lobbyList = MapsRepository.findAll() ;
        return ResponseEntity.ok(lobbyList);
    }

	@PostMapping("/newMap")
	public ResponseEntity<Maps> newMap(Long id, String MapString, Long NumberOfPlayers, String MapName) {
        return ResponseEntity.ok(new Maps(id, MapString, NumberOfPlayers, MapName));
	}

    public static record newMap (Long id, String MapString, Long NumberOfPlayers, String MapName) {};
}
