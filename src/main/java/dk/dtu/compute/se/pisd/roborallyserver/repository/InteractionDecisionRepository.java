package dk.dtu.compute.se.pisd.roborallyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.dtu.compute.se.pisd.roborallyserver.model.InteractionDecision;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;

public interface InteractionDecisionRepository extends JpaRepository<InteractionDecision, InteractionDecision.InteractionDecisionKey> {

	InteractionDecision getInteractionDecisionsByRoundAndStepAndLobby_IdAndPlayer_Id(Long round, int step, Long lobbyId, Long playerId);

	int countMovesPlayedByLobby_IdAndRound(Long lobbyID, Long round);
}
