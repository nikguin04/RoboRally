package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.Player.PlayerStatus;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import dk.dtu.compute.se.pisd.roborallyserver.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyserver.model.ServerPlayer;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import dk.dtu.compute.se.pisd.roborally.controller.TimerController;

import java.util.Timer;

public class TimerController {
	private long min, sec, totalSec = 0;
	private Label timeLabel;
	private Timer timer;
	final private Board board;
	private final NetworkController network;

	public TimerController(Board board, NetworkController network) {
		this.board = board;
		this.network = network;


	}

	private String format(long value) {
		if (value < 10) {
			return 0 + "" + value;
		}
		return value + "";

	}

	public void convertTime() {
		totalSec--;
		min = TimeUnit.SECONDS.toMinutes(totalSec);
		sec = totalSec - (min * 60);
		timeLabel.setText(format(min) + ":" + format(sec));

	}

	void setTimer(){
		totalSec = 5;
		Player p = board.getCurrentPlayer();
		Timer timer = new Timer();

		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					convertTime();
					if (totalSec <= 0) {

						network.sendData(p);
						timer.cancel();
					}


				});
			}
		};

		timer.schedule(timerTask, 0, 1000);

	}
}
