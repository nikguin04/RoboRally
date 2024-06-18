package dk.dtu.compute.se.pisd.roborallyserver.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborallyserver.controller.PlayerController.NewPlayerBody;
import dk.dtu.compute.se.pisd.roborallyserver.model.InteractionDecisions;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import dk.dtu.compute.se.pisd.roborallyserver.repository.InteractionDecisionsRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.PlayerRepository;

@RestController
//Base endpoint
@RequestMapping("/interactiondecisions")
public class InteractionDecisionsController {

    private PlayerRepository playerRepository;
    private LobbyRepository lobbyRepository;
    private InteractionDecisionsRepository interactionDecisionsRepository;

    public InteractionDecisionsController(PlayerRepository playerRepository, LobbyRepository lobbyRepository, InteractionDecisionsRepository interactionDecisionsRepository) {
        this.playerRepository = playerRepository;
        this.lobbyRepository = lobbyRepository;
        this.interactionDecisionsRepository = interactionDecisionsRepository;
    }

    @GetMapping("/get")
    public ResponseEntity<Command> getInteractionDecision(
        @RequestParam(required = true, value="lobbyid") long lobbyid,
        @RequestParam(required = true, value="playerid") long playerid,
        @RequestParam(required = true, value="rounds") long rounds,
        @RequestParam(required = true, value="step") int step
    ) {
        InteractionDecisions intdec = interactionDecisionsRepository.getInteractionDecisionsByRoundsAndStepAndLobby_IdAndPlayer_Id(
            rounds, step, lobbyid, playerid);

        if (intdec == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(intdec.getCommand());
    }

	@PostMapping("/submit")
	public ResponseEntity<String> newPlayer(@RequestBody NewInteractionDecisionBody nidb) {
        Lobby lobby = lobbyRepository.getLobbyById(nidb.lobbyId);
        ServerPlayer player = playerRepository.getPlayerById(nidb.playerId);

        InteractionDecisions intdec = new InteractionDecisions(nidb.rounds, nidb.step, lobby, player, Command.valueOf(nidb.Command));
        interactionDecisionsRepository.saveAndFlush(intdec);
		return ResponseEntity.ok("");

	}

    public static record NewInteractionDecisionBody (Long rounds, int step, Long lobbyId, Long playerId, String Command) {};
}
