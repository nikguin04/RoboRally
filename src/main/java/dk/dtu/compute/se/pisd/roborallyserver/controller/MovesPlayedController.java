package dk.dtu.compute.se.pisd.roborallyserver.controller;


import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//Base endpoint
@RequestMapping("/lobbies")
public class MovesPlayedController {

    private LobbyRepository lobbyRepository;

    public MovesPlayedController(LobbyRepository lobbyRepository) {
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
