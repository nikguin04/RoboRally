package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CheckPointTest {

	@Test
	public void testDoAction() {
		Board board = new Board(8, 8);
		GameController gameController = new GameController(board);
		board.getSpace(3, 3).setElement(new CheckPoint(1));
		board.getSpace(5,5).setElement(new CheckPoint(2));
		board.getSpace(4,4).setElement(new CheckPoint(3));



		Player player1 = new Player(board, "red", "Test player1");
		board.addPlayer(player1);
		player1.setSpace(board.getSpace(2,2));
		assertEquals(0, player1.getCheckPointCounter());

		player1.setSpace(board.getSpace(3,3));
		board.getSpace(3,3).getElement().doAction(gameController, board.getSpace(3,3));
		assertEquals(1, player1.getCheckPointCounter());


		player1.setSpace(board.getSpace(4,4));
		board.getSpace(4,4).getElement().doAction(gameController, board.getSpace(4,4));
		assertEquals(1, player1.getCheckPointCounter());

		player1.setSpace(board.getSpace(5,5));
		board.getSpace(5,5).getElement().doAction(gameController, board.getSpace(5,5));
		assertEquals(2, player1.getCheckPointCounter());

		player1.setSpace(board.getSpace(4,4));
		board.getSpace(4,4).getElement().doAction(gameController, board.getSpace(4,4));
		assertEquals(3, player1.getCheckPointCounter());
	}




}
