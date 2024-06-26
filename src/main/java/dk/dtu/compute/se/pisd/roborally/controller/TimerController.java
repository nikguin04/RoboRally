package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import java.util.Timer;
import java.util.TimerTask;

public class TimerController extends Subject {

	private long totalSec = 0;
	private final Timer timer = new Timer();
	private TimerTask timerTask = null;
	final private Board board;
	private final NetworkController network;

	public TimerController(Board board, NetworkController network) {
		this.board = board;
		this.network = network;
	}

	public long getTimeLeft() {
		return totalSec;
	}

	public void startTimer() {
		if (timerTask != null) {
			timerTask.cancel();
		}

		totalSec = 45;
		Player player = board.getCurrentPlayer();

		timerTask = new TimerTask() {
			@Override
			public void run() {
				totalSec--;
				notifyChange();
				if (totalSec <= 0) {
					network.sendData(player);
					stopTimer();
				}
			}
		};

		timer.scheduleAtFixedRate(timerTask, 0, 1000);
	}

	public void stopTimer() {
		timerTask.cancel();
		timerTask = null;
		totalSec = -1;
		notifyChange();
	}

}
