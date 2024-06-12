package dk.dtu.compute.se.pisd.roborallyserver.controller;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.MovesPlayed;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.MovesPlayedRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.PlayerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    //Specific endpoint for the method
    @RequestMapping(value = "")
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
		return ResponseEntity.ok(lobby); // lobbyRepository.save(lobby)

	}

	@PostMapping("/finishedprogramming")
	public ResponseEntity<MovesPlayed> isFinishedProgramming(){

		int playerCount = playerRepository.countPlayersInLobby();
		if(counter >= playerCount){

			return ResponseEntity.ok();
		}
		return null;
	}

	public static record NewMovesPlayBody (int rounds, String move1, String move2, String move3, String move4, String move5, Long lobby_id, long player_id) {};
}
