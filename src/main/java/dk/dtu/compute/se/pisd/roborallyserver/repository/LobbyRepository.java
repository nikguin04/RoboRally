package dk.dtu.compute.se.pisd.roborallyserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;

public interface LobbyRepository  extends JpaRepository<Lobby, Long> {

    public Lobby findLobbyById(long id);

    // TODO: Make a function that only returns joinable lobbies
    // public Lobby[] findJoinableLobbies



}
