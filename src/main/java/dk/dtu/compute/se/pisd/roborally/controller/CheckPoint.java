package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;


/**
 * @author Anders Greve Sørensen, s235093@dtu.dk
 */
public class CheckPoint extends SpaceElement {
	public final int checkPointNr;

	/**
	 * Instantiate checkpoint with given checkpoint number.
	 * @author Anders Greve Sørensen, s235093@dtu.dk
	 * @param checkPointNr the checkpoint number to be assigned to checkpoint. Players must visit checkpoints
	 *                     with their numbers in ascending order. So 1 first, then 2, etc.
	 */
	public CheckPoint(int checkPointNr) {
		this.checkPointNr = checkPointNr;
	}

	/**
	 * Instantiate checkpoint with given checkpoint number.
	 * This works with strings so we can load from json easier
	 * @author Niklas Jensen, s235101@dtu.dk
	 * @param checkPointNr the checkpoint number to be assigned to checkpoint. Players must visit checkpoints
	 *                     with their numbers in ascending order. So 1 first, then 2, etc.
	 */
	public CheckPoint(String checkPointNr) {
		this.checkPointNr = (sToInt(checkPointNr));
	}

	private int sToInt(String s) {
		try {
			int parse = Integer.parseInt(s);
			return parse;
		} catch (NumberFormatException e) {
			System.out.println("Parsed number was not int, defaulting to 0");
			return 0;
		}
	}


	/**
	 * Updates checkpoint counter for player on checkpoint if player has visited all previous checkpoints.
	 * @author Anders Greve Sørnsen, s235093@dtu.dk
	 * @param space The space containing the checkpoint to be activated
	 * @param gameController The gameController. Not used in method
	 * @return True if player has visited all previous checkpoints. False otherwise.
	 */
	@Override
	public boolean doAction(GameController gameController, Space space) {
		Player spacePlayer = space.getPlayer();
		int playerCheckPointCounter = spacePlayer.getCheckPointCounter();

		if (playerCheckPointCounter == this.checkPointNr - 1) {
			spacePlayer.setCheckPointCounter(this.checkPointNr);
			return true;
		}
		return false;
	}
}
