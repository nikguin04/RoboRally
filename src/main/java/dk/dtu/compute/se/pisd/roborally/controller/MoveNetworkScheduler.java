package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class MoveNetworkScheduler extends ScheduledService<Void> {

private Lobby lobby;
private ServerPlayer splayer;

	public MoveNetworkScheduler(Lobby lobby, ServerPlayer splayer) {
		this.lobby = lobby;
		this.splayer = splayer;
	}
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				UpdatePlayersCard();
				return null;
			}
		};
	}

	public void UpdatePlayersCard(){
		Platform.runLater(() -> {



		});
	}
}
