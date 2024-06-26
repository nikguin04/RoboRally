package dk.dtu.compute.se.pisd.roborallyserver.controller;

import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.PlayerRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private PlayerRepository playerRepository;
    private LobbyRepository lobbyRepository;

    public PlayerController(PlayerRepository playerRepository, LobbyRepository lobbyRepository) {
        this.playerRepository = playerRepository;
        this.lobbyRepository = lobbyRepository;
    }

    @GetMapping
    public ResponseEntity<List<ServerPlayer>> getPlayers() {
        List<ServerPlayer> lobbyList = playerRepository.findAll();
        return ResponseEntity.ok(lobbyList);
    }

	@PostMapping
	public ResponseEntity<ServerPlayer> newPlayer(@RequestBody NewPlayerBody npb) {
        Lobby lobby = lobbyRepository.getLobbyById(npb.lobbyId);
        if (lobby == null || lobby.isGameStarted()) {
            return ResponseEntity.notFound().build();
        }
        ServerPlayer player = new ServerPlayer(null, npb.name, lobby);
        playerRepository.saveAndFlush(player);
		return ResponseEntity.ok(player);
	}

    public record NewPlayerBody(String name, Long lobbyId) {}

}
