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
import org.springframework.http.ResponseEntity;

import dk.dtu.compute.se.pisd.roborallyserver.controller.MovesPlayedController.NewMovesPlayBody;
import dk.dtu.compute.se.pisd.roborallyserver.controller.PlayerController.NewPlayerBody;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.MovesPlayedRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.PlayerRepository;

@DataJpaTest
@TestInstance(Lifecycle.PER_CLASS)
public class lobbytest {
	@Autowired
	LobbyRepository lobbyRepository;
	@Autowired
	PlayerRepository playerRepository;
	@Autowired
	MovesPlayedRepository movesPlayedRepository;

	PlayerController playerController;
	LobbyController lobbyController;
	MovesPlayedController movesPlayedController;

	@BeforeAll
	public void setUp() {
		lobbyController = new LobbyController(lobbyRepository, playerRepository);
		playerController = new PlayerController(playerRepository, lobbyRepository);
		movesPlayedController = new MovesPlayedController(movesPlayedRepository, lobbyRepository, playerRepository);
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

	@Test
	public void TestMoveGetRestrictions() {
		Lobby newlobby = lobbyRepository.save(new Lobby((Long)null, 0l, 0l, false));
		ServerPlayer p1 = playerController.newPlayer(new NewPlayerBody("p1", newlobby.getId())).getBody();
		ServerPlayer p2 = playerController.newPlayer(new NewPlayerBody("p2", newlobby.getId())).getBody();

		movesPlayedController.newMovesPlayed(new NewMovesPlayBody(0l, "FWD1", "FWD2", "FWD3", "LEFT", "Back", newlobby.getId(), p1.getId()));

		assertEquals(HttpStatus.BAD_REQUEST, movesPlayedController.isFinishedProgramming(newlobby.getId(), 0l).getStatusCode());

		movesPlayedController.newMovesPlayed(new NewMovesPlayBody(0l, "FWD1", "FWD2", "FWD3", "LEFT", "Back", newlobby.getId(), p2.getId()));

		assertEquals(HttpStatus.OK, movesPlayedController.isFinishedProgramming(newlobby.getId(), 0l).getStatusCode());
	}




}
