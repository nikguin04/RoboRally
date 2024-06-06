package dk.dtu.compute.se.pisd.roborally.controller;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import static dk.dtu.compute.se.pisd.roborally.utils.StringUtils.intarrFromCommaStr;

/**
 * @author Anders Greve Sørensen, s235093@dtu.dk
 */
public class PrioAntenna extends SpaceElement {

	// All these variables are only initialised when a board uses a priority antenna.
	public final int x;
	public final int y;

	private Board board;

	/**
	 * Attaches the prio antenna to a board.
	 * The {@link Board#prioAntenna} element is not set automatically and needs to be set in the {@link Board} class
	 * @param board The board to attach this prio antenna to
	 */
	public void attachBoard(Board board) {
		board.getSpace(x, y).setElement(this);
		this.board = board;
	}

	/**
	 * Create a new prio antenna, with set coordinates x,y
	 * @param x Coordinate x
	 * @param y Coordinate y
	 */
	public PrioAntenna(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a new prio antenna, with a set coordinate x,y given as a string
	 * @param coord Stringified coordinates: "x,y"
	 */
	public PrioAntenna(String coord) {
		int[] coordi = intarrFromCommaStr(coord);
		this.x = coordi[0];
		this.y = coordi[1];
	}

	/**
	 * Creates an instance of the priority antenna at a given position, on a given board.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param x The x coordinate of the priority antenna.
	 * @param y The y coordinate of the priority antenna.
	 * @param board The board that the priority antenna is associated with.
	 */
	/*public PrioAntenna(int x, int y, Board board) {
		this.x = x;
		this.y = y;
		this.board = board;
	}*/

	/**
	 * Updates the associated boards player priority list.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 */
	public void updatePlayerPrio() {
		this.board.setPrioritizedPlayers(getPrioPlayerList(this.board.getPrioritizedPlayers()));
	}


	/**
	 * Calculates an ordered list of players in descending order of priority. So the first player in the array has
	 * the highest priority, the next player has second highest and so on.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param listOfPlayers The list of players to prioritize.
	 * @return An ordered list of players in descending order of priority.
	 */
	public List<Player> getPrioPlayerList(List<Player> listOfPlayers) {
		List<Player> prioPlayerList = new ArrayList<>();
        List<Player> listOfPlayersCopy = new ArrayList<>(listOfPlayers);

		while (!listOfPlayersCopy.isEmpty()) {
			Player prioPlayer = getprioPlayer(listOfPlayersCopy);
			prioPlayerList.add(prioPlayer);
			listOfPlayersCopy.remove(prioPlayer);
		}
		return prioPlayerList;
	}

	/**
	 * Find the player with the highest priority, given a list of players.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param listOfPlayers List of players from which you wish to find the one with the highest priority.
	 * @return Player with the highest priority.
	 */
	public Player getprioPlayer(List<Player> listOfPlayers) {
		Player prioPlayer = null;
		int prioPlayerManhattanDistance = Integer.MAX_VALUE;

		for (Player p : listOfPlayers) {

			if (p.getSpace() == null) {
				System.out.println("Player not on the board");
				continue;
			}

			int currentPlayerManhattanDistance = getManhattanDistance(this.x, this.y, p.getSpace().x, p.getSpace().y);
			if (prioPlayer == null) {
				prioPlayer = p;
				prioPlayerManhattanDistance = currentPlayerManhattanDistance;
			} else {
				if (currentPlayerManhattanDistance < prioPlayerManhattanDistance) {
					prioPlayer = p;
					prioPlayerManhattanDistance = currentPlayerManhattanDistance;
				} else if (currentPlayerManhattanDistance == prioPlayerManhattanDistance) {
					prioPlayer = prioTieBreak(prioPlayer, p);
				}
			}
		}
		return prioPlayer;
	}

	/**
	 * Calculates the player with priority given two players who are equally far from the priority antenna.
	 * In other words, performs a tiebreak.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param player1 The first player in the tiebreak.
	 * @param player2 The second player in the tiebreak.
	 * @return The player with priority.
	 */
	public Player prioTieBreak(Player player1, Player player2) {
		Player prioPlayer = null;
		Space p1Space = player1.getSpace();
		Space p2Space = player2.getSpace();
		// Calculate each players angle with positive x-axis in radians in the interval (0, 2π]
		// Players position is being adjusted to take the prio antennas position into account.
		double p1Angle = Math.atan2(-(p1Space.y - this.y), p1Space.x - this.x);
		double p2Angle = Math.atan2(-(p2Space.y - this.y), p2Space.x - this.x);
		if (p1Angle < 0) {
			p1Angle = p1Angle + 2 * Math.PI;
		}
		if (p2Angle < 0) {
			p2Angle = p2Angle + 2 * Math.PI;
		}
		// Check which player has the largest angle with positive x-axis, and assign that player prio.
		// An exception is if the players angle is 0, at which point that player has prio.
		if (p1Angle == 0) return player1;
		if (p2Angle == 0) return player2;
		if (p1Angle > p2Angle) {
			prioPlayer = player1;
		} else {
			prioPlayer = player2;
		}
		return prioPlayer;
	}

	/**
	 * Calculates the Manhattan distance from one point to another. Meaning, the amount of spaces
	 * between the two points without going diagonally.
	 * @author Anders Greve Sørensen, s235093@dtu.dk.
	 * @param x1 the x coordinate of the first point.
	 * @param y1 the y coordinate of the first point.
	 * @param x2 the x coordinate of the second point.
	 * @param y2 the y coordinate of the second point.
	 * @return the Manhattan distance from one point to another.
	 */
	public int getManhattanDistance(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

	@Override
	public boolean doAction(GameController gameController, Space space) {
		throw new UnsupportedOperationException("Unimplemented method 'doAction' for priority antenna. This point should never be reached as the priority antenna is solid. If you get this message, there is a flaw in the game move logic");
	}

	@Override
	public String getArgument() {
        return x + "," + y;
    }
}
