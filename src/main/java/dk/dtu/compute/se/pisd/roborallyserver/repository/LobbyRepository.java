package dk.dtu.compute.se.pisd.roborallyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;

import java.util.List;

public interface LobbyRepository extends JpaRepository<Lobby, Long> {

	Lobby getLobbyById(long id);

	List<Lobby> getAllByGameStartedFalse();

}
