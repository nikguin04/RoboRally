package dk.dtu.compute.se.pisd.roborallyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;

import dk.dtu.compute.se.pisd.roborallyserver.model.MovesPlayed;

public interface MovesPlayedRepository extends JpaRepository<MovesPlayed, MovesPlayed.MovesPlayedKey> {

	@Query("select MovesPlayed from MovesPlayed where MovesPlayed.player = ? and MovesPlayed.lobby = ? and rounds = ?")
	public MovesPlayed getMovesPlayedById(int id, int lobbyid, int round);


}
