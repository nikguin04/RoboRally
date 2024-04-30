package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.SpaceElement;

public class CheckPoint extends SpaceElement {
	public final int checkPointNr;

	public CheckPoint(int checkPointNr) {
		this.checkPointNr = checkPointNr;
	}

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
