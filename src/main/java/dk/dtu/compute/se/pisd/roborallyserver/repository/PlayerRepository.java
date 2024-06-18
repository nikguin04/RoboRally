package dk.dtu.compute.se.pisd.roborallyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;

import java.util.List;

public interface PlayerRepository extends JpaRepository<ServerPlayer, Long> {

	ServerPlayer getPlayerById(Long id);

	List<ServerPlayer> getPlayersByLobby_Id(Long id);

	int countPlayersByLobby_Id(Long lobbyId);

}
