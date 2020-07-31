import static org.junit.Assert.*;

import java.util.Scanner;

import org.junit.Test;

public class PlayerTest {

	@Test
	public void SimulateGame() {
		Player program = Player.program;
		Player.Game game = program.new Game(10, 10, 5);
		Player.IA ia = program.new IA(game);

		game.setVision("_", "_", "_", "_");
		for (int i = 0; i < 5; i++) {
			int x = i;
			int y = i;
			if (i == 4) {
				x = 9;
				y = 0;
			}

			game.setPlayerPosition(i, x, y);
		}

		game.printMap();
//		System.out.println(ia.computeBestMove().getCode());

//		System.out.println(game.getEnemyPlayerDistance(0));
	}

}
