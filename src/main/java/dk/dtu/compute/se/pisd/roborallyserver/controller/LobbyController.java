package dk.dtu.compute.se.pisd.roborallyserver.controller;


import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//Base endpoint
@RequestMapping("/lobbies")
public class LobbyController {

    private LobbyRepository lobbyRepository;

    public LobbyController(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    @GetMapping
    //Specific endpoint for the method
    @RequestMapping(value = "")
    public ResponseEntity<List<Lobby>> getLobbies(){
        List<Lobby> lobbyList = lobbyRepository.findAll();
        return ResponseEntity.ok(lobbyList);
    }

	@PostMapping("/newlobby")
	public ResponseEntity<Lobby> newLobby(@RequestBody Lobby lobby) {

        lobbyRepository.saveAndFlush(lobby);
		return ResponseEntity.ok(lobby); // lobbyRepository.save(lobby)

	}
}
