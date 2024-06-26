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

import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborallyserver.controller.MovesPlayedController.NewMovesPlayBody;
import dk.dtu.compute.se.pisd.roborallyserver.controller.PlayerController.NewPlayerBody;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.Map;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import dk.dtu.compute.se.pisd.roborallyserver.repository.LobbyRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.MapRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.MovesPlayedRepository;
import dk.dtu.compute.se.pisd.roborallyserver.repository.PlayerRepository;

@DataJpaTest
@TestInstance(Lifecycle.PER_CLASS)
public class LobbyTest {
	@Autowired
	LobbyRepository lobbyRepository;
	@Autowired
	PlayerRepository playerRepository;
	@Autowired
	MovesPlayedRepository movesPlayedRepository;
	@Autowired
	MapRepository mapRepository;
	Map m;

	PlayerController playerController;
	LobbyController lobbyController;
	MovesPlayedController movesPlayedController;

	@BeforeAll
	public void setUp() {
		lobbyController = new LobbyController(lobbyRepository, playerRepository);
		playerController = new PlayerController(playerRepository, lobbyRepository);
		movesPlayedController = new MovesPlayedController(movesPlayedRepository, lobbyRepository, playerRepository);

		m = new Map("", "", 0l);
		mapRepository.save(m);
	}


	@Test
	public void testPushToLobby() {
		Lobby newLobby = new Lobby((Long)null, 0l, m, false, null);
		Lobby insertedLobby = lobbyRepository.save(newLobby);
		assertEquals(lobbyRepository.findById(insertedLobby.getId()).get(), newLobby);
	}


	@Test
	public void PushPlayerAfterLobbyStart() {
		Lobby newLobby = new Lobby(null, 0l, m, false, null);
		newLobby = lobbyController.newLobby(newLobby).getBody();
		playerController.newPlayer(new NewPlayerBody("p1", newLobby.getId()));
		playerController.newPlayer(new NewPlayerBody("p2", newLobby.getId()));

		lobbyController.startGameForLobby(newLobby.getId());
		assertNotEquals(HttpStatus.OK, playerController.newPlayer(new NewPlayerBody("p3", newLobby.getId())).getStatusCode());
	}

	@Test
	public void TestMoveGetRestrictions() {
		Lobby newLobby = new Lobby(null, 0l, m, false, null);
		newLobby = lobbyController.newLobby(newLobby).getBody();
		ServerPlayer p1 = playerController.newPlayer(new NewPlayerBody("p1", newLobby.getId())).getBody();
		ServerPlayer p2 = playerController.newPlayer(new NewPlayerBody("p2", newLobby.getId())).getBody();

		movesPlayedController.newMovesPlayed(new NewMovesPlayBody(0l, Command.FWD1, Command.FWD2, Command.FWD3, Command.LEFT, Command.Back, newLobby.getId(), p1.getId()));

		assertEquals(HttpStatus.BAD_REQUEST, movesPlayedController.isFinishedProgramming(newLobby.getId(), 0l).getStatusCode());

		movesPlayedController.newMovesPlayed(new NewMovesPlayBody(0l, Command.FWD1, Command.FWD2, Command.FWD3, Command.LEFT, Command.Back, newLobby.getId(), p2.getId()));

		assertEquals(HttpStatus.OK, movesPlayedController.isFinishedProgramming(newLobby.getId(), 0l).getStatusCode());
	}




}
