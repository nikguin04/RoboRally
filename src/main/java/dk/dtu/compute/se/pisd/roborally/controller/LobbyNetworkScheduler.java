package dk.dtu.compute.se.pisd.roborally.controller;

import java.util.List;

import dk.dtu.compute.se.pisd.roborally.net.LobbyRest;
import dk.dtu.compute.se.pisd.roborally.view.LobbyView;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class LobbyNetworkScheduler extends ScheduledService<Void> {


    public final ObservableList<ServerPlayer> playersFetched;

	private AppController appController;
	private Lobby lobby;
    private ServerPlayer splayer;
	public LobbyNetworkScheduler(AppController appController, Lobby lobby, ServerPlayer splayer) {
		this.lobby = lobby;
		this.splayer = splayer;
		this.appController = appController;

        playersFetched = FXCollections.observableArrayList();
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
               	updatePlayersInLobby();
				updateLobbyState();
				return null;
            }
        };
	}

	public void updatePlayersInLobby() {
        Platform.runLater(() -> {
            List<ServerPlayer> pList = List.of(LobbyRest.requestPlayersByLobbyId(lobby.getId()));
            // Edit local player name with appendix: (you)
            for (ServerPlayer sp: pList)
                if (sp.getId().equals(splayer.getId())) { sp.setName(sp.getName() + " (you)");}
            playersFetched.setAll(pList);
        });
	}

	// Update lobby state. ex: game is started
	public void updateLobbyState() {
		Platform.runLater(() -> {
			lobby = LobbyRest.requestLobbyById(lobby.getId());
			if (lobby.isGame_started()) {
				this.cancel();
				appController.initGameFromLobbyStart(lobby, playersFetched.toArray(ServerPlayer[]::new), splayer);
			}

		});
	}

	public void requestStartGame() {
		LobbyRest.requestStartGame(lobby);
	}

}
