package dk.dtu.compute.se.pisd.roborallyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import jakarta.transaction.Transactional;

import java.util.List;

public interface PlayerRepository extends JpaRepository<ServerPlayer, Long> {

	ServerPlayer getPlayerById(Long id);

	List<ServerPlayer> getPlayersByLobby_Id(Long id);

	int countPlayersByLobby_Id(Long lobbyId);

	@Transactional
	void deleteByIdAndLobby_Id(Long id, Long lobbyId);

	// delete from PLAYERS where ID = 2 and LOBBY_ID = 1
    // @Query("delete from ServerPlayer where lobby.id = :lobbyid and id = :playerid")
    // public void removePlayerFromLobby(@Param("lobbyid") Long lobbyid, @Param("playerid") Long playerid);
}
