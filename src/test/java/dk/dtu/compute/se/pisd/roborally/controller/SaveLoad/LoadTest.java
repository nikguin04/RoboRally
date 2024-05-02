package dk.dtu.compute.se.pisd.roborally.controller.SaveLoad;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.view.LoadDialog;
import javafx.application.Platform;

/**
 * DEVELOPERS NOTE: This function is still under development.
 * The tests will pass, but a save module is needed to be implemented before this can be completed
 */
public class LoadTest {


	@Test
	public void testLoad() throws InterruptedException, ExecutionException {
		CompletableFuture<Boolean> passed = new CompletableFuture<>();

		Platform.startup(new Runnable() {
			@Override
			public void run() {



				LoadDialog<Board> ld = new LoadDialog<>();
				ld.LoadBoardFromFile("gamedata/TempTest.json");
				Board loadedBoard = ld.getResult();

				passed.complete(false);
			}
		});



		assertTrue(passed.get());
	}

	@Test
	public void Testctb() {
		CreateTestBoard();
	}

	public Board CreateTestBoard()  {

		Board defaultBoard = new Board(4,4);

		Board b = new Board(4,4); // initialize totally blank board
		//b.getSpace(1, 1).setElement(new ConveyorBelt()); // This will make the assertion fail
		//assertTrue(compareBoard(b, defaultBoard));
		compareBoardSimilarity(b, defaultBoard);

		System.out.println("Breakpoint");
		return b;
	}

	public boolean compareBoard(Board b_one, Board b_two) {
		Field[] board_fields = Board.class.getDeclaredFields();

		for (int i = 0; i < board_fields.length; i++) {
			if (board_fields[i].getName().startsWith("$SWITCH_TABLE")) { continue; } // ignore switch tables which is counted with fields
			System.out.print("Checking: " + board_fields[i].getName() + " - ");
			try {
				board_fields[i].setAccessible(true);
				Object comp = board_fields[i].get(b_one);
				Object def = board_fields[i].get(b_two);

				if (comp == null && def == null) { // Check if both are null
					continue;
				} else if (comp == null || def == null) {
					return false;
				} else { // No null pointers
					if (def.getClass().isArray()) {
						if (!compareArray(def, comp)) return false;
					} else {
						if (!comp.equals(def)) return false;
					}
				}
			} catch (IllegalAccessException e) {
				System.out.println("Debug: " + board_fields[i].getName() + " Error: " + e.getMessage());
				return false;
			}
		}
		return true;
	}

	/**
	 * Very similar to compare board, but returns false is any variable inside board is similar
	 * Note: This does not check all nested array variables, but only if one of the checks for them fail.
	 * This essentailly fails if a variable is similar to between b_one and b_two
	 * @param b_one
	 * @param b_two
	 * @return
	 */
	public void compareBoardSimilarity(Board b_one, Board b_two) throws AssertionError {
		Field[] board_fields = Board.class.getDeclaredFields();

		for (int i = 0; i < board_fields.length; i++) {
			if (board_fields[i].getName().startsWith("$SWITCH_TABLE")) { continue; } // ignore switch tables which is counted with fields
			System.out.print("Checking: " + board_fields[i].getName() + " - ");
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
						if (compareArray(def, comp)) throw new AssertionError("The two boards are similar in array variable: " + board_fields[i].getName()); //return true;
					} else {
						if (comp.equals(def)) throw new AssertionError("The two boards are similar in variable: " + board_fields[i].getName()); //return true;
					}
				}
			} catch (IllegalAccessException e) {
				System.out.println("Debug: " + board_fields[i].getName() + " Error: " + e.getMessage());
			}
		}
		//return false;
	}

	public boolean compareArray(Object def, Object comp) {
		if (def.getClass() != comp.getClass()) { return false; }
		if (def.getClass().isArray() && comp.getClass().isArray()) {
			Object[] defarray = (Object[])def;
			Object[] comparray = (Object[])comp;
			if (defarray.length != comparray.length) { return false; }
			if (defarray[0].getClass().isArray() && comparray[0].getClass().isArray()) { // check for nested array
				for (int i = 0; i < defarray.length; i++) {
					if (!compareArray(defarray[i], comparray[i])) return false;
				}
			} else {
				for (int i = 0; i < defarray.length; i++) {
					boolean single_object_comparison = defarray[i].equals(comparray[i]);
					//System.out.println("Comp result" + i + ": " + single_object_comparison);
					if (!single_object_comparison) return false;
				}
				return true;
			}
			return true;
		}
		return false;
	}
}
