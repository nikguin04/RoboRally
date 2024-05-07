package dk.dtu.compute.se.pisd.roborally.controller;

import java.util.Arrays;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import static dk.dtu.compute.se.pisd.roborally.utils.StringUtils.intarrFromCommaStr;

public class StartTile extends SpaceElement {
    // All these variables are only initialzied when a board uses a StartTile.
	public final int x;
	public final int y;

	private Board board;

	/**
	 * Attaches the StartTile to a board
	 * The {@link Board#StartTile} element is not set automatically and needs to be set in the {@link Board} class
	 * @param board The board to attach this prio antenna to
	 */
	public void attachBoard(Board board) {
		board.getSpace(x, y).setElement(this);
		this.board = board;
	}

    /**
	 * Create a new StartTile, with set coordinates x,y
	 * @param x Coordinate x
	 * @param y Coordiante y
	 */
	public StartTile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * Create a new prio antenna, with a set coordinate x,y given as a string
	 * @param coord Stringified coordinates: "x,y"
	 */
	public StartTile(String coord) {
		int[] coordi = intarrFromCommaStr(coord);
		this.x = coordi[0];
		this.y = coordi[1];
	}
    @Override
    public boolean doAction(GameController gameController, Space space) {
		// TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'doAction'");
        return false;
    }

	@Override
	public String getArgument() {
        return String.valueOf(x + "," + y);
    }
}
