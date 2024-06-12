package dk.dtu.compute.se.pisd.roborallyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;

import dk.dtu.compute.se.pisd.roborallyserver.model.MovesPlayed;

public interface MovesPlayedRepository extends JpaRepository<MovesPlayed, Long> {

}
