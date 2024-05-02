package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import static org.junit.jupiter.api.Assertions.*;

public class TestUtils {
	public static void assertPossibleMove(GameController gameController, Board board, Player player) {
		Space target = board.getNeighbour(player.getSpace(), player.getHeading());
		assertNotNull(target);
		assertDoesNotThrow(() -> gameController.moveToSpace(player, target, player.getHeading()));
	}

	public static void assertImpossibleMove(GameController gameController, Board board, Player player) {
		Space target = board.getNeighbour(player.getSpace(), player.getHeading());
		assertNotNull(target);
		assertThrows(GameController.ImpossibleMoveException.class, () ->
			gameController.moveToSpace(player, target, player.getHeading()));
	}
}
