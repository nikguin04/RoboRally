package dk.dtu.compute.se.pisd.roborallyserver.repository;

import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.MovesPlayed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovesPlayedRepository extends JpaRepository<MovesPlayed, Long> {
	public MovesPlayed findMovesPlayedByStepId(long stepId);
}
