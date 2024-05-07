package dk.dtu.compute.se.pisd.roborally.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dk.dtu.compute.se.pisd.roborally.model.Board;

import org.junit.jupiter.api.Test;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;

public class CardsTest {
    @Test
    public void testTurnRight() {
        
        Board board = new Board(8, 8);
		GameController gameController = new GameController(board);
        Player player = new Player(board, "red", "Test player1");
        
        Space initialSpace = new Space(board, 0, 0); 
        player.setSpace(initialSpace);
        player.setHeading(Heading.NORTH);
        
        gameController.turnRight(player);
        
        Space newSpace = player.getSpace();
        
        assertNotNull(newSpace);
        
        Heading newHeading = player.getHeading();
        assertEquals(Heading.EAST, newHeading);
        
    }
    @Test
    public void testMoveForward2() {
        
        Board board = new Board(8, 8);
		GameController gameController = new GameController(board);
        Player player = new Player(board, "red", "Test player1");
        
        Space initialSpace = new Space(board, 0, 0); 
        player.setSpace(initialSpace);
        player.setHeading(Heading.SOUTH);
        
        gameController.moveForward(player);
        gameController.moveForward(player);
        
        Space newSpace = player.getSpace();
        
        assertNotNull(newSpace);
        
        Heading newHeading = player.getHeading();
        assertEquals(Heading.SOUTH, newHeading);
        
    }
    @Test
    public void testMoveForward3() {
        
        Board board = new Board(8, 8);
		GameController gameController = new GameController(board);
        Player player = new Player(board, "red", "Test player1");
        
        Space initialSpace = new Space(board, 0, 0); 
        player.setSpace(initialSpace);
        player.setHeading(Heading.SOUTH);
        
        gameController.moveForward(player);
        gameController.moveForward(player);
        gameController.moveForward(player);
        Space newSpace = player.getSpace();
        
        assertNotNull(newSpace);
        
        Heading newHeading = player.getHeading();
        assertEquals(Heading.SOUTH, newHeading);
        
    }
    @Test
    public void testMoveForward1() {
        
        Board board = new Board(8, 8);
        GameController gameController = new GameController(board);
        Player player = new Player(board, "red", "Test player1");
    
        Space initialSpace = new Space(board, 0, 0); 
        
        player.setSpace(initialSpace);
        player.setHeading(Heading.NORTH);
    
        gameController.moveForward(player);
        
        Space newSpace = player.getSpace();
        
        assertNotNull(newSpace);
     
        Heading newHeading = player.getHeading();
        assertEquals(Heading.NORTH, newHeading);
        
    }
    @Test
    public void testTurnLeft() {
        
        Board board = new Board(8, 8);
        GameController gameController = new GameController(board);
        Player player = new Player(board, "red", "Test player1");
    
        Space initialSpace = new Space(board, 0, 0); 
        player.setSpace(initialSpace);
        player.setHeading(Heading.NORTH);
    
        gameController.turnLeft(player);
        
        Space newSpace = player.getSpace();
        
        assertNotNull(newSpace);
     
        Heading newHeading = player.getHeading();
        assertEquals(Heading.WEST, newHeading);
        
    }
    @Test
    public void testUTurn() {
        
        Board board = new Board(8, 8);
        GameController gameController = new GameController(board);
        Player player = new Player(board, "red", "Test player1");
    
        Space initialSpace = new Space(board, 0, 0); 
        player.setSpace(initialSpace);
        player.setHeading(Heading.NORTH);
    
        gameController.turnLeft(player);
        gameController.turnLeft(player);

        Space newSpace = player.getSpace();
        
        assertNotNull(newSpace);
     
        Heading newHeading = player.getHeading();
        assertEquals(Heading.SOUTH, newHeading);
        
    }
    @Test
    public void testBackUp() {
        
        Board board = new Board(8, 8);
        GameController gameController = new GameController(board);
        Player player = new Player(board, "red", "Test player1");
    
        Space initialSpace = new Space(board, 0, 0); 
        player.setSpace(initialSpace);
        player.setHeading(Heading.NORTH);
    
        gameController.turnLeft(player);
        gameController.turnLeft(player);
        gameController.moveForward(player);
        gameController.turnLeft(player);
        gameController.turnLeft(player);


        Space newSpace = player.getSpace();
        
        assertNotNull(newSpace);
     
        Heading newHeading = player.getHeading();
        assertEquals(Heading.NORTH, newHeading);
        
    }
    //@Test
    //public void testAgain() {
    //    
    //    Board board = new Board(8, 8);
    //    GameController gameController = new GameController(board);
    //    Player player = new Player(board, "red", "Test player1");
    //
    //    Space initialSpace = new Space(board, 0, 0); 
    //    player.setSpace(initialSpace);
    //    player.setHeading(Heading.NORTH);
//
//
    //    gameController.turnLeft(player);
    //    player.setLastCardPlayed(null);
//
    //    player.getLastCardPlayed();
    //    
    //    Space newSpace = player.getSpace();
    //    
    //    assertNotNull(newSpace);
    // 
    //    Heading newHeading1 = player.getHeading();
    //    assertEquals(Heading.SOUTH, newHeading1);
    //    
    //}

    @Test
    public void testOptinalTurnLeft() {
        
        Board board = new Board(8, 8);
		GameController gameController = new GameController(board);
        Player player = new Player(board, "red", "Test player1");
        
        Space initialSpace = new Space(board, 0, 0); 
        player.setSpace(initialSpace);
        player.setHeading(Heading.NORTH);
        
        gameController.executeCommandOptionAndContinue(player.getProgramField(player.board.getStep()).getCard().command.getOptions().get(1));
        
        Space newSpace = player.getSpace();
        
        assertNotNull(newSpace);
        
        Heading newHeading = player.getHeading();
        assertEquals(Heading.EAST, newHeading);
        
    }

    @Test
    public void testOptinalTurnRight() {
        
        Board board = new Board(8, 8);
		GameController gameController = new GameController(board);
        Player player = new Player(board, "red", "Test player1");
        
        Space initialSpace = new Space(board, 0, 0); 
        player.setSpace(initialSpace);
        player.setHeading(Heading.NORTH);
        
        gameController.executeCommandOptionAndContinue(player.getProgramField(player.board.getStep()).getCard().command.getOptions().get(1));
        
        Space newSpace = player.getSpace();
        
        assertNotNull(newSpace);
        
        Heading newHeading = player.getHeading();
        assertEquals(Heading.EAST, newHeading);
        
    }
    
}
