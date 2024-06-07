package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.controller.TestUtils.*;

public class WallTest {
	@Test
	public void testWall() {
		// Setup
		Board board = new Board(3, 3);
		GameController gameController = new GameController(board);
		Player player = new Player(board, "red", "Test Player");
		board.addPlayer(player);
		player.setSpace(board.getSpace(1, 1));
		// Testing
		board.getSpace(1, 1).getWalls().add(Heading.SOUTH);
		assertImpossibleMove(gameController, board, player);
		board.getSpace(1, 1).getWalls().clear();
		board.getSpace(1, 2).getWalls().add(Heading.NORTH);
		assertImpossibleMove(gameController, board, player);
		board.getSpace(1, 2).getWalls().clear();
		board.getSpace(1, 1).getWalls().addAll(List.of(Heading.WEST, Heading.NORTH, Heading.EAST));
		assertPossibleMove(gameController, board, player);
	}
}
