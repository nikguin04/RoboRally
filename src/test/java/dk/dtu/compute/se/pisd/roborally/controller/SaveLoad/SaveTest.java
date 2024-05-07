package dk.dtu.compute.se.pisd.roborally.controller.SaveLoad;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;

public class SaveTest{

    @Test
    public void testSave() throws InterruptedException, ExecutionException{
        LoadTest testBoard = new LoadTest();
        AppController appController = new AppController(null);
        appController.saveFile(testBoard.CreateTestBoard(), "SaveTestUnit");

    }
}
