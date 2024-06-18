package dk.dtu.compute.se.pisd.roborallyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;

public interface PlayerRepository extends JpaRepository<ServerPlayer, Long> {

	ServerPlayer findPlayerById(long id);

	//https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
	@Query("select count(id) from ServerPlayer where lobby.id = ?1")
	int countPlayersInLobby(Long lobbyid);

}
