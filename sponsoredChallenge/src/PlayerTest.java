import static org.junit.Assert.*;

import java.util.Scanner;

import org.junit.Test;


public class PlayerTest {

	@Test
	public void SimulateGame() {
		Player program = Player.program;
		Player.Game game = program.new Game(10, 10, 10);
		Player.IA ia = program.new IA(game);

		// game loop
		while (true) {
			game.setVision("_", "_", "_", "_");
			for (int i = 0; i < 2; i++) {
				int x = 5;
				int y = 5;
				game.setPlayerPosition(i, x, y);
			}
			
			game.printMap();
			System.out.println(ia.computeBestMove().getCode());
		}
	}

}
