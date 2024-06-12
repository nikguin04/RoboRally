package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.view.LobbyView;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class LobbyNetworkScheduler extends ScheduledService<Void> {

	LobbyView view;
	public LobbyNetworkScheduler(LobbyView view) {
		this.view = view;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
               	view.updatePlayersInLobby();
                return null;
            }
        };
	}

}
