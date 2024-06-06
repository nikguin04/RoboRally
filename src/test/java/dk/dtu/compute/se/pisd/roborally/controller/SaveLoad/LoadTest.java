package dk.dtu.compute.se.pisd.roborally.controller.SaveLoad;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static dk.dtu.compute.se.pisd.roborally.utils.ArrayCompare.compareArray;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import com.mysql.cj.exceptions.AssertionFailedException;

import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.PrioAntenna;
import dk.dtu.compute.se.pisd.roborally.controller.StartTile;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.utils.CompareException;
import dk.dtu.compute.se.pisd.roborally.utils.FieldsCompare;
import dk.dtu.compute.se.pisd.roborally.view.LoadDialog;
import javafx.application.Platform;

/**
 * DEVELOPERS NOTE: This function is still under development.
 * The tests will pass, but a save module is needed to be implemented before this can be completed
 */
public class LoadTest {

	@Test
	public void testLoad() throws InterruptedException, ExecutionException {
		CompletableFuture<CompareException> passed = new CompletableFuture<>();
		//passed.completeOnTimeout(new AssertionFailedException("Timeout"), 5000, TimeUnit.MILLISECONDS);

		Platform.startup(() -> {
			Board testboard = CreateTestBoard();

			// TODO: Save test board and load

			LoadDialog<Board> ld;
			try {
				ld = new LoadDialog<>();
			} catch (Exception e) {
				passed.complete(new CompareException("Could not open load dialog, check deserializer"));
				return; // for warning messages
			}
			ld.LoadBoardFromFile("gamedata/TempTest.json");
			Board loadedBoard = ld.getCurrentBoard();
			List<String> ignoreVariables = Arrays.asList(
				"step",
				"stepMode");
			try {
				CompareBoard(testboard, loadedBoard, ignoreVariables);
				passed.complete(null);
			} catch (CompareException ae) {
				passed.complete(ae);
			}

		});
		assertNull(passed.get());
	}

	@Test
	public void TestTestBoardValidity() {
		Board defaultBoard = new Board(8,8);
		Board test = CreateTestBoard();

		List<String> ignoreVariables = Arrays.asList(
			"width",
			"height");
		AssureBoardIndifference(test, defaultBoard, ignoreVariables);
	}

	@Test
	public void TestTestBoardInvaliditySimple() {
		Board defaultBoard = new Board(8,8);
		Board test = CreateTestBoard();

		List<String> ignoreVariables = Arrays.asList(
			"width",
			"height");
		test.setStep(0);
		assertThrows(AssertionError.class,
			() -> AssureBoardIndifference(test, defaultBoard, ignoreVariables),
			"Board is not indifferent in all variables (did not throw exception)c");
	}

	@Test
	public void TestTestBoardInvalidityNested() {
		Board defaultBoard = new Board(8,8);
		Board test = CreateTestBoard();

		List<String> ignoreVariables = Arrays.asList(
			"width",
			"height");
		test.getSpace(0, 4).setElement(null);
		test.getSpace(2, 2).setElement(null);
		test.getSpace(5, 5).setElement(null);
		test.getSpace(5, 5).getWalls().clear();
		assertThrows(AssertionError.class,
			() -> AssureBoardIndifference(test, defaultBoard, ignoreVariables),
			"Board is not indifferent in all variables (did not throw exception)");
	}

	public Board CreateTestBoard() throws AssertionError {
		Board b = new Board(8,8); // initialize totally blank board
		// add variables to board for saving and loading successfully
		Player p = new Player(b, "red", "test player 1", new Command[] {Command.FWD2, Command.FWD1, Command.LEFT, Command.FWD1, Command.FWD1, Command.FWD2, Command.FWD1, Command.RIGHT});
		p.setSpace(b.getSpace(1, 3));
		p.setHeading(SOUTH);

		b.addPlayer(p);
		b.setCurrentPlayer(p);
		b.setGameId(69);
		b.getSpace(2, 2).setElement(new ConveyorBelt(Heading.EAST)); // note: currently not directly checked by heading.
		b.setPhase(Phase.ACTIVATION);
		b.setStep(1);
		b.setStepMode(true);
		b.incMoveCount();
		b.setStartTile(new StartTile(0, 4));


		b.setPrioAntenna(new PrioAntenna(5, 5));
		b.addPrioPlayer(p);

		return b;
	}

	public boolean CompareBoard(Board b_one, Board b_two, List<String> ignoreVariables) throws CompareException {
		FieldsCompare<Board> fc = new FieldsCompare<Board>();
		// Don't test player for an equal board, since the player can be identical but on another board.
		fc.CompareFields(b_one, b_two, ignoreVariables);
		return true;

	}

	/**
	 * Very similar to compare board, but returns false is any variable inside board is similar
	 * Note: This does not check all nested array variables, but only if one of the checks for them fail.
	 * This essentially fails if a variable is similar to between b_one and b_two
	 * Ignores width and height integers, which gets checked by the spaces variable
	 * @param b_one
	 * @param b_two
	 * @return
	 */
	public void AssureBoardIndifference(Board b_one, Board b_two, List<String> ignoreVariables) throws AssertionError {
		Field[] board_fields = Board.class.getDeclaredFields();

		for (int i = 0; i < board_fields.length; i++) {
			// Ignore predetermined variables so our other functions can test it
			if (ignoreVariables.contains(board_fields[i].getName())) { continue; }
			if (board_fields[i].getName().startsWith("$SWITCH_TABLE")) { continue; } // ignore switch tables which is counted with fields
			System.out.println("Checking: " + board_fields[i].getName() + " - ");
			try {
				board_fields[i].setAccessible(true);
				Object comp = board_fields[i].get(b_one);
				Object def = board_fields[i].get(b_two);

				if (comp == null && def == null) { // Check if both are null
					throw new AssertionError("The two boards are similar (both null) in variable: " + board_fields[i].getName()); //return true;
				} else if (comp == null || def == null) {
					continue;
				} else { // No null pointers
					if (def.getClass().isArray()) {
						try {
							if (compareArray(def, comp))
								throw new AssertionError("The two boards are similar in array variable: " + board_fields[i].getName()); //return true;
						} catch (AssertionFailedException e) {
							// The comparison has a difference, so we can continue
						}
					} else {
						if (comp.equals(def))
							throw new AssertionError("The two boards are similar in variable: " + board_fields[i].getName()); //return true;
					}
				}
			} catch (IllegalAccessException e) {
				System.out.println("Debug: " + board_fields[i].getName() + " Error: " + e.getMessage());
			}
		}
		//return false;
	}

}
