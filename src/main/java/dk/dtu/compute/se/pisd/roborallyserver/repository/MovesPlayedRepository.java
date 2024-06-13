package dk.dtu.compute.se.pisd.roborallyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;

import dk.dtu.compute.se.pisd.roborallyserver.model.MovesPlayed;

public interface MovesPlayedRepository extends JpaRepository<MovesPlayed, MovesPlayed.MovesPlayedKey> {

	@Query("select m from MovesPlayed m where m.player = ?1 and m.lobby = ?2 and m.rounds = ?3")
	public MovesPlayed getMovesPlayedById(int id, int lobbyid, int round);

	@Query("select m from MovesPlayed m where m.lobby.id = ?1 and m.rounds = ?2")
	public MovesPlayed[] getMovesPlayedInLobbyByRound(Long lobbyID, Long round);




}
