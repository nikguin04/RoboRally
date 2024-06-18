package dk.dtu.compute.se.pisd.roborallyserver.controller;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.MovesPlayed;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.MovesPlayedRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.PlayerRepository;

@RestController
//Base endpoint
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

    @GetMapping()
    public ResponseEntity<List<MovesPlayed>> getMovesPlayed(){

        List<MovesPlayed> lobbyList = movesPlayedRepository.findAll();

        return ResponseEntity.ok(lobbyList);
    }

	@PostMapping("/newmovesplayed")
	public ResponseEntity<Lobby> newMovesPlayed(@RequestBody NewMovesPlayBody nmp) {
		counter++;
		Lobby lobby = lobbyRepository.findLobbyById(nmp.lobby_id);
		ServerPlayer player = playerRepository.findPlayerById(nmp.player_id);
		MovesPlayed played = new MovesPlayed(lobby.getRounds(), nmp.move1, nmp.move2, nmp.move3, nmp.move4, nmp.move5, lobby, player);

		movesPlayedRepository.saveAndFlush(played);
		// Check if all moves are submitted now, then increment round id
		int countPlayers = playerRepository.countPlayersInLobby(lobby.getId());
		int movesPlayed = movesPlayedRepository.getMovesPlayedInLobbyByRound(lobby.getId(), lobby.getRounds()).length;
		if (countPlayers == movesPlayed) {
			lobby.setRounds(lobby.getRounds() + 1);
			lobbyRepository.saveAndFlush(lobby);
		}

		return ResponseEntity.ok(lobby); // lobbyRepository.save(lobby)

	}
	@GetMapping("/getPlayersMoves")
	public ResponseEntity<MovesPlayed[]> isFinishedProgramming(@RequestParam(required = true, value="lobbyid") long lobbyid, @RequestParam(required = true, value="round") long round ){
		int playerCount = playerRepository.countPlayersInLobby(lobbyid);

		MovesPlayed[] movesplayed = movesPlayedRepository.getMovesPlayedInLobbyByRound(lobbyid, round);

		if (playerCount == movesplayed.length) {
			return ResponseEntity.ok(movesplayed);
		} else {
			return ResponseEntity.badRequest().build();
		}

	}

	@GetMapping("/lobbyroundfinished")
	public ResponseEntity<List<ServerPlayer>> getPlayersFinishedProgramming(
			@RequestParam(required=true,value="lobbyid") Long lobbyid, @RequestParam(required = true, value="round") long round) {

		Lobby lobby = lobbyRepository.findLobbyById(lobbyid);

		List<ServerPlayer> playersDone = new ArrayList<ServerPlayer>();
		MovesPlayed[] moves = movesPlayedRepository.getMovesPlayedInLobbyByRound(lobbyid, round);
		for (MovesPlayed move: moves) {
			playersDone.add(move.getPlayer());
		}
		return ResponseEntity.ok(playersDone);

	}

	public static record NewMovesPlayBody (Long rounds, String move1, String move2, String move3, String move4, String move5, Long lobby_id, long player_id) {};
}
