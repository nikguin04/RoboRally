package dk.dtu.compute.se.pisd.roborallyserver.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;

import dk.dtu.compute.se.pisd.roborallyserver.controller.PlayerController.NewPlayerBody;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.PlayerRepository;

@DataJpaTest
@TestInstance(Lifecycle.PER_CLASS)
public class lobbytest {
	@Autowired
	LobbyRepository lobbyRepository;
	@Autowired
	PlayerRepository playerRepository;

	PlayerController playerController;
	LobbyController lobbyController;

	@BeforeAll
	public void setUp() {
		lobbyController = new LobbyController(lobbyRepository, playerRepository);
		playerController = new PlayerController(playerRepository, lobbyRepository);

	}


	@Test
	public void testPushToLobby() {
		Lobby newLobby = new Lobby((Long)null, 0l, 0l, false);
		Lobby insertedLobby = lobbyRepository.save(newLobby);
		assertEquals(lobbyRepository.findLobbyById(insertedLobby.getId()), newLobby);
	}


	@Test
	public void PushPlayerAfterLobbyStart() {
		Lobby newlobby = lobbyRepository.save(new Lobby((Long)null, 0l, 0l, false));
		playerController.newPlayer(new NewPlayerBody("p1", newlobby.getId()));
		playerController.newPlayer(new NewPlayerBody("p2", newlobby.getId()));

		lobbyController.startGameForLobby(newlobby.getId());
		assertNotEquals(HttpStatus.OK, playerController.newPlayer(new NewPlayerBody("p3", newlobby.getId())).getStatusCode());
	}




}
