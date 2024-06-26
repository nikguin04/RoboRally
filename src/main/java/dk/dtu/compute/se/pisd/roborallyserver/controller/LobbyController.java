package dk.dtu.compute.se.pisd.roborallyserver.controller;

import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.PlayerRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/lobbies")
public class LobbyController {

    private LobbyRepository lobbyRepository;
    private PlayerRepository playerRepository;

    public LobbyController(LobbyRepository lobbyRepository, PlayerRepository playerRepository) {
        this.lobbyRepository = lobbyRepository;
        this.playerRepository = playerRepository;
    }

    @GetMapping
    public ResponseEntity<List<Lobby>> getAllLobbies() {
        List<Lobby> lobbyList = lobbyRepository.findAll();
        return ResponseEntity.ok(lobbyList);
    }

    @GetMapping("/{id}/players")
    public ResponseEntity<List<ServerPlayer>> getPlayersInLobby(@PathVariable("id") Long id) {
        List<ServerPlayer> playerList = playerRepository.getPlayersByLobby_Id(id);
        return ResponseEntity.ok(playerList);
    }

    @GetMapping("/joinable")
    public ResponseEntity<List<Lobby>> getJoinableLobbies() {
        return ResponseEntity.ok(lobbyRepository.getAllByGameStartedFalse());
    }

	@PostMapping
	public ResponseEntity<Lobby> newLobby(@RequestBody Lobby lobby) {
        lobbyRepository.saveAndFlush(lobby);
		return ResponseEntity.ok(lobby);
	}

    @PostMapping("/{id}/startgame")
    public ResponseEntity<Void> startGameForLobby(@PathVariable("id") Long id) {
        Lobby lobby = lobbyRepository.getLobbyById(id);
        if (lobby.isGameStarted())
			return ResponseEntity.badRequest().build();
		lobby.setGameStarted(true);
		lobbyRepository.saveAndFlush(lobby);
		return ResponseEntity.ok(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lobby> getLobby(@PathVariable("id") Long id) {
        Lobby lobby = lobbyRepository.getLobbyById(id);
        return ResponseEntity.ok(lobby);
    }

}
