package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Gears extends SpaceElement {

	public final boolean anticlockwise;

	@Override
	public boolean doAction(GameController gameController, Space space) {
		Player player = space.getPlayer();
		if (anticlockwise)
			gameController.turnLeft(player);
		else
			gameController.turnRight(player);
		return true;
	}

}
