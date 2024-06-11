package dk.dtu.compute.se.pisd.roborally.controller;

import java.util.List;

import dk.dtu.compute.se.pisd.roborally.net.LobbyRest;
import dk.dtu.compute.se.pisd.roborally.view.LobbyView;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class LobbyNetworkScheduler extends ScheduledService<Void> {


    public final ObservableList<ServerPlayer> playersFetched;
	private LobbyView view;
	public LobbyNetworkScheduler(LobbyView view) {
		this.view = view;

        playersFetched = FXCollections.observableArrayList();
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
               	updatePlayersInLobby();

				return null;
            }
        };
	}

	public void updatePlayersInLobby() {
        Platform.runLater(() -> {
            List<ServerPlayer> pList = List.of(LobbyRest.requestPlayersByLobbyId(view.getLobbyId()));
            // Edit local player name with appendix: (you)
            for (ServerPlayer sp: pList)
                if (sp.getId().equals(view.getPlayerId())) { sp.setName(sp.getName() + " (you)");}
            playersFetched.setAll(pList);
        });
	}

}
