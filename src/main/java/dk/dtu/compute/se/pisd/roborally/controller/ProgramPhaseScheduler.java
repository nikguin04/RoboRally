package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Phase;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class ProgramPhaseScheduler extends ScheduledService<Void> {

	private GameController gameController;

	public ProgramPhaseScheduler(GameController gameController) {
		this.gameController = gameController;
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
				}
				return null;
            }
        };
	}
}
