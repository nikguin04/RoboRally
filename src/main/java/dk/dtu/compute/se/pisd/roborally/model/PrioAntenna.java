package dk.dtu.compute.se.pisd.roborally.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.*;

public class PrioAntenna {
	public final List<Heading> walls = new ArrayList<>(
		Arrays.asList(SOUTH, WEST, NORTH, EAST));

	public final int x;
	public final int y;

	public final Board board;

	PrioAntenna(int x, int y, Board board) {
		this.x = x;
		this.y = y;
		this.board = board;
	}

	public Player getprioPlayer(List<Player> listOfPlayers) {
		Player prioPlayer = null;
		int prioPlayerManhattenDistance = Integer.MAX_VALUE;

		for (Player p : listOfPlayers) {

			if (p.getSpace() == null) {
				System.out.println("Player not on the board");
				continue;
			}

			int currentPlayerManhattenDistance = getManhattenDistance(this.x, this.y, p.getSpace().x, p.getSpace().y);
			if (prioPlayer == null) {
				prioPlayer = p;
				prioPlayerManhattenDistance = currentPlayerManhattenDistance;
			}
			else {
				if (currentPlayerManhattenDistance < prioPlayerManhattenDistance) {
					prioPlayer = p;
					prioPlayerManhattenDistance = currentPlayerManhattenDistance;
				}
				else if (currentPlayerManhattenDistance == prioPlayerManhattenDistance) {
					prioPlayer = prioTieBreak(prioPlayer, p);
				}
			}
		}
		return prioPlayer;
	}

	// TO FIX:
	// Does not take position of priority antenna into account. Treats (0,0) as origo, when it should be the position of antenna.
	private Player prioTieBreak(Player player1, Player player2) {
		Player prioPlayer = null;
		Space p1Space = player1.getSpace();
		Space p2Space = player2.getSpace();

		// Calculate each players angle with positive x axis in radians in the interval (0, 2π]
		double p1Angle = Math.atan2(p1Space.y, p1Space.x);
		double p2Angle = Math.atan2(p2Space.y, p2Space.x);
		if (p1Angle < 0) {
			p1Angle = p1Angle + 2 * Math.PI;
		}
		if (p2Angle < 0) {
			p2Angle = p2Angle + 2 * Math.PI;
		}

		// Check which player has largest angle with positive x axis, and assign that player prio.
		if (p1Angle > p2Angle) {
			prioPlayer = player1;
		}
		else {
			prioPlayer = player2;
		}
		return prioPlayer;
	}

	private int getManhattenDistance(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}



}
