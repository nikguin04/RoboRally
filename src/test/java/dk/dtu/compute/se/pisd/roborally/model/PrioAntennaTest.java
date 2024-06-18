package dk.dtu.compute.se.pisd.roborally.model;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.PrioAntenna;

public class PrioAntennaTest {


	@Test
	public void testManhattanDistance() {
		Board board = new Board(8, 8);
		GameController gameController = new GameController(board, null, null, null);
		PrioAntenna prioAntenna = new PrioAntenna();
		board.setPrioAntenna(prioAntenna, board.getSpace(5, 5));
        assertEquals(2, prioAntenna.getManhattanDistance(1, 1, 2, 2));
		assertEquals(2, prioAntenna.getManhattanDistance(-1, -1, -2, -2));
		assertEquals(6, prioAntenna.getManhattanDistance(-1, -1, 2, 2));
	}


	@Test
	public void testPrioTieBreak() {
		Board board = new Board(8, 8);
		GameController gameController = new GameController(board, null, null, null);
		PrioAntenna prioAntenna = new PrioAntenna(5, 5);
		board.setPrioAntenna(prioAntenna);
		Player player1 = new Player(board, "red", "Test Player1", 0l);
		Player player2 = new Player(board, "blue", "Test Player2", 0l);
		Player player3 = new Player(board, "yellow", "Test Player3", 0l);
		Player player4 = new Player(board, "green", "Test Player4", 0l);
		board.addPlayer(player1);
		board.addPlayer(player2);
		board.addPlayer(player3);
		board.addPlayer(player4);
		player1.setSpace(board.getSpace(3, 5));
		player2.setSpace(board.getSpace(5, 7));
		player3.setSpace(board.getSpace(7, 5));
		player4.setSpace(board.getSpace(5, 3));

		assertEquals(player2, prioAntenna.prioTieBreak(player1, player2));
		assertEquals(player3, prioAntenna.prioTieBreak(player1, player3));
		assertEquals(player1, prioAntenna.prioTieBreak(player1, player4));
		assertEquals(player3, prioAntenna.prioTieBreak(player2, player3));
		assertEquals(player2, prioAntenna.prioTieBreak(player2, player4));
		assertEquals(player3, prioAntenna.prioTieBreak(player3, player4));
	}


	@Test
	public void testGetPrioPlayer() {
		Board board = new Board(8, 8);
		GameController gameController = new GameController(board, null, null, null);
		PrioAntenna prioAntenna = new PrioAntenna(5, 5);
		board.setPrioAntenna(prioAntenna);
		Player player1 = new Player(board, "red", "Test Player1", 0l);
		Player player2 = new Player(board, "blue", "Test Player2", 0l);
		Player player3 = new Player(board, "yellow", "Test Player3", 0l);
		Player player4 = new Player(board, "green", "Test Player4", 0l);
		board.addPlayer(player1);
		board.addPlayer(player2);
		board.addPlayer(player3);
		board.addPlayer(player4);
		player1.setSpace(board.getSpace(3, 5));
		player2.setSpace(board.getSpace(5, 7));
		player3.setSpace(board.getSpace(7, 5));
		player4.setSpace(board.getSpace(5, 3));

		assertEquals(player3, prioAntenna.getprioPlayer(board.getPlayers()));
		player3.setSpace(board.getSpace(0, 0));
		assertEquals(player2, prioAntenna.getprioPlayer(board.getPlayers()));
		player3.setSpace(board.getSpace(5, 6));
		assertEquals(player3, prioAntenna.getprioPlayer(board.getPlayers()));
	}

	@Test
	public void testGetPrioPlayerList() {
		Board board = new Board(8, 8);
		GameController gameController = new GameController(board, null, null, null);
		PrioAntenna prioAntenna = new PrioAntenna(5, 5);
		board.setPrioAntenna(prioAntenna);
		Player player1 = new Player(board, "red", "Test Player1", 0l);
		Player player2 = new Player(board, "blue", "Test Player2", 0l);
		board.addPlayer(player1);
		board.addPlayer(player2);
		player1.setSpace(board.getSpace(3, 5));
		player2.setSpace(board.getSpace(5, 7));

		assertEquals(player2,prioAntenna.getPrioPlayerList(board.getPlayers()).get(0));
		assertEquals(player1,prioAntenna.getPrioPlayerList(board.getPlayers()).get(1));

		Player player3 = new Player(board, "yellow", "Test Player3", 0l);
		Player player4 = new Player(board, "green", "Test Player4", 0l);
		board.addPlayer(player3);
		board.addPlayer(player4);
		player3.setSpace(board.getSpace(7, 5));
		player4.setSpace(board.getSpace(5, 3));

		assertEquals(player3,prioAntenna.getPrioPlayerList(board.getPlayers()).get(0));
		assertEquals(player2,prioAntenna.getPrioPlayerList(board.getPlayers()).get(1));
		assertEquals(player1,prioAntenna.getPrioPlayerList(board.getPlayers()).get(2));
		assertEquals(player4,prioAntenna.getPrioPlayerList(board.getPlayers()).get(3));
	}


	@Test
	public void testUpdatePlayerPrio() {
		Board board = new Board(8, 8);
		GameController gameController = new GameController(board, null, null, null);
		PrioAntenna prioAntenna = new PrioAntenna(5, 5);
		board.setPrioAntenna(prioAntenna);
		Player player1 = new Player(board, "red", "Test Player1", 0l);
		Player player2 = new Player(board, "blue", "Test Player2", 0l);
		Player player3 = new Player(board, "yellow", "Test Player3", 0l);
		Player player4 = new Player(board, "green", "Test Player4", 0l);
		board.addPlayer(player1);
		board.addPlayer(player2);
		board.addPlayer(player3);
		board.addPlayer(player4);
		player1.setSpace(board.getSpace(3, 5));
		player2.setSpace(board.getSpace(5, 7));
		player3.setSpace(board.getSpace(7, 5));
		player4.setSpace(board.getSpace(5, 3));
		board.getPrioritizedPlayers().add(player1);
		board.getPrioritizedPlayers().add(player2);
		board.getPrioritizedPlayers().add(player3);
		board.getPrioritizedPlayers().add(player4);

		prioAntenna.updatePlayerPrio();

		assertEquals(player3, board.getPrioritizedPlayers().get(0));
		assertEquals(player2, board.getPrioritizedPlayers().get(1));
		assertEquals(player1, board.getPrioritizedPlayers().get(2));
		assertEquals(player4, board.getPrioritizedPlayers().get(3));
	}


}
