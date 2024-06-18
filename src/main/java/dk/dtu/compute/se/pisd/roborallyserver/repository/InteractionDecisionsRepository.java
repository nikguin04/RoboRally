package dk.dtu.compute.se.pisd.roborallyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.dtu.compute.se.pisd.roborallyserver.model.InteractionDecisions;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;

public interface InteractionDecisionsRepository extends JpaRepository<InteractionDecisions, InteractionDecisions.InteractionDecisionsKey> {

	InteractionDecisions getInteractionDecisionsByRoundsAndStepAndLobby_IdAndPlayer_Id(Long rounds, int step, Long lobbyId, Long playerId);

	int countMovesPlayedByLobby_IdAndRounds(Long lobbyID, Long round);
}
