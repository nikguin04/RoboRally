package dk.dtu.compute.se.pisd.roborallyserver.controller;

import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborallyserver.model.InteractionDecision;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import dk.dtu.compute.se.pisd.roborallyserver.repository.InteractionDecisionRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.PlayerRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interactiondecisions")
public class InteractionDecisionController {

    private PlayerRepository playerRepository;
    private LobbyRepository lobbyRepository;
    private InteractionDecisionRepository interactionDecisionRepository;

    public InteractionDecisionController(PlayerRepository playerRepository, LobbyRepository lobbyRepository, InteractionDecisionRepository interactionDecisionRepository) {
        this.playerRepository = playerRepository;
        this.lobbyRepository = lobbyRepository;
        this.interactionDecisionRepository = interactionDecisionRepository;
    }

    @GetMapping
    public ResponseEntity<Command> getInteractionDecision(
		@RequestParam("lobbyid") long lobbyid, @RequestParam("playerid") long playerid,
		@RequestParam("round") long round, @RequestParam("step") int step
    ) {
        InteractionDecision decision = interactionDecisionRepository
            .getInteractionDecisionsByRoundAndStepAndLobby_IdAndPlayer_Id(round, step, lobbyid, playerid);

        if (decision == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(decision.getCommand());
    }

	@PostMapping
	public ResponseEntity<Void> newPlayer(@RequestBody NewInteractionDecisionBody nidb) {
        Lobby lobby = lobbyRepository.getLobbyById(nidb.lobbyId);
        ServerPlayer player = playerRepository.getPlayerById(nidb.playerId);

        InteractionDecision decision = new InteractionDecision(nidb.round, nidb.step, lobby, player, Command.valueOf(nidb.command));
        interactionDecisionRepository.saveAndFlush(decision);
		return ResponseEntity.ok(null);

	}

    public record NewInteractionDecisionBody(Long round, int step, Long lobbyId, Long playerId, String command) {}

}
