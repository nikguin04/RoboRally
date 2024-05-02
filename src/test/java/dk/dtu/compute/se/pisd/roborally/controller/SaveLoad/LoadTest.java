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
		b.getSpace(1, 1).setElement(new ConveyorBelt());

		// invalidate all variables
		Field[] board_fields = Board.class.getDeclaredFields();

		for (int i = 0; i < board_fields.length; i++) {
			if (board_fields[i].getName().startsWith("$SWITCH_TABLE")) { continue; } // ignore switch tables which is counted with fields
			System.out.println("Checking: " + board_fields[i].getName());
			try {
				board_fields[i].setAccessible(true);
				Object comp = board_fields[i].get(b);
				Object def = board_fields[i].get(defaultBoard);
				if (comp == null && def == null) {
					System.out.println("Comparison: true");
					continue;
				}
				System.out.println("class: " + def.getClass().getName());
				if (def.getClass().isArray()) {
					boolean arrcomp = compareArray(def, comp);
					System.out.println("ARRAY Comparison: " + arrcomp);
				}

				boolean compare = (comp == null && def == null) ? true : comp.equals(def);
				System.out.println("Comparison: " + compare);



			} catch (IllegalAccessException e) {
				System.out.println("Debug: " + board_fields[i].getName() + " Error: " + e.getMessage());
			}
		}


		System.out.println("Breakpoint");
		return b;
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

				System.out.println("her1123e");
			} else {
				for (int i = 0; i < defarray.length; i++) {
					boolean single_object_comparison = defarray[i].equals(comparray[i]);
					System.out.println("Comp result" + i + ": " + single_object_comparison);
					if (!single_object_comparison) return false;
				}
				return true;
			}
			System.out.println("here");
			return true;
		}
		return false;
	}
}
