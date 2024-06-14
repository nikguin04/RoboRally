package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class ProgramPhaseScheduler extends ScheduledService<Void> {

	private GameController gameController;
	private Player player;

	public ProgramPhaseScheduler(GameController gameController, Player player) {
		this.gameController = gameController;
		this.player = player;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
				if (gameController.board.getPhase().equals(Phase.ACTIVATION)) {
					gameController.executeStep();
				} else {
					cancel(); // Cancel the step task
					//gameController.StartProgrammingPhase(true);
					gameController.ResetPlayerProgramCards(player);
//					System.out.println()
				}
				return null;
            }
        };
	}
}
