package dk.dtu.compute.se.pisd.roborallyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;

public interface LobbyRepository extends JpaRepository<Lobby, Long> {

	Lobby getLobbyById(long id);

	// TODO: Make a function that only returns joinable lobbies

}
