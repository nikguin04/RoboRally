package dk.dtu.compute.se.pisd.roborallyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.dtu.compute.se.pisd.roborallyserver.model.MovesPlayed;

import java.util.List;

public interface MovesPlayedRepository extends JpaRepository<MovesPlayed, MovesPlayed.MovesPlayedKey> {

	List<MovesPlayed> getMovesPlayedByLobby_IdAndRound(Long lobbyID, Long round);

	int countMovesPlayedByLobby_IdAndRound(Long lobbyID, Long round);

}
