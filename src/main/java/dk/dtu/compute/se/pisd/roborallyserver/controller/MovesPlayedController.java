package dk.dtu.compute.se.pisd.roborallyserver.controller;

import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.MovesPlayed;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.MovesPlayedRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.PlayerRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/movesplayed")
public class MovesPlayedController {

    private MovesPlayedRepository movesPlayedRepository;
	private LobbyRepository lobbyRepository;
	private PlayerRepository playerRepository;
	int counter = 0;

    public MovesPlayedController(MovesPlayedRepository movesPlayedRepository, LobbyRepository lobbyRepository, PlayerRepository playerRepository) {
        this.movesPlayedRepository = movesPlayedRepository;
		this.lobbyRepository = lobbyRepository;
		this.playerRepository = playerRepository;
    }

    @GetMapping
    public ResponseEntity<List<MovesPlayed>> getMovesPlayed() {
        List<MovesPlayed> lobbyList = movesPlayedRepository.findAll();
        return ResponseEntity.ok(lobbyList);
    }

	@PostMapping
	public ResponseEntity<Lobby> newMovesPlayed(@RequestBody NewMovesPlayBody nmp) {
		counter++;
		Lobby lobby = lobbyRepository.getLobbyById(nmp.lobbyId);
		ServerPlayer player = playerRepository.getPlayerById(nmp.playerId);
		MovesPlayed played = new MovesPlayed(lobby.getRounds(), nmp.move1, nmp.move2, nmp.move3, nmp.move4, nmp.move5, lobby, player);

		movesPlayedRepository.saveAndFlush(played);
		// Check if all moves are submitted now, then increment round id
		int countPlayers = playerRepository.countPlayersByLobby_Id(lobby.getId());
		int movesPlayed = movesPlayedRepository.countMovesPlayedByLobby_IdAndRound(lobby.getId(), lobby.getRounds());
		if (countPlayers == movesPlayed) {
			lobby.setRounds(lobby.getRounds() + 1);
			lobbyRepository.saveAndFlush(lobby);
		}

		return ResponseEntity.ok(lobby);
	}

	@GetMapping("/roundmoves")
	public ResponseEntity<List<MovesPlayed>> isFinishedProgramming(@RequestParam("lobbyid") long lobbyId, @RequestParam("round") long round) {
		int playerCount = playerRepository.countPlayersByLobby_Id(lobbyId);
		List<MovesPlayed> movesPlayed = movesPlayedRepository.getMovesPlayedByLobby_IdAndRound(lobbyId, round);
		if (playerCount != movesPlayed.size())
			return ResponseEntity.badRequest().build();
		return ResponseEntity.ok(movesPlayed);
	}

	@GetMapping("/finishedplayers")
	public ResponseEntity<List<ServerPlayer>> getPlayersFinishedProgramming(@RequestParam("lobbyid") Long lobbyId, @RequestParam("round") long round) {
		List<ServerPlayer> playersDone = new ArrayList<>();
		List<MovesPlayed> moves = movesPlayedRepository.getMovesPlayedByLobby_IdAndRound(lobbyId, round);
		for (MovesPlayed move : moves) {
			playersDone.add(move.getPlayer());
		}
		return ResponseEntity.ok(playersDone);
	}

	public record NewMovesPlayBody(Long round, Command move1, Command move2, Command move3, Command move4, Command move5, Long lobbyId, long playerId) {}

}
