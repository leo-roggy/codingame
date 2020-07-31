import java.util.*;

import javax.swing.text.Position;

import java.io.*;
import java.math.*;

class Player {

	static Player program = new Player();

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int width = in.nextInt();
		int height = in.nextInt();
		int nbPlayer = in.nextInt();
		if (in.hasNextLine()) {
			in.nextLine();
		}
		Game game = program.new Game(width, height, nbPlayer);

		// game loop
		while (true) {
			String up = in.nextLine();
			String right = in.nextLine();
			String down = in.nextLine();
			String left = in.nextLine();
			game.setVision(up, right, down, left);
			for (int i = 0; i < nbPlayer; i++) {
				int x = in.nextInt();
				int y = in.nextInt();
				game.setPlayerPosition(i, x, y);
			}
			in.nextLine();
			System.out.println(game.printBestMove());
		}
	}

	enum Direction {
		UP("C"), RIGHT("A"), DOWN("D"), LEFT("E"), STAY("B");
		private String code;

		Direction(String code) {
			this.code = code;
		}

		public String getCode() {
			return this.code;
		}
	}

	class Game {

		private int width;
		private int height;
		private int nbPlayer;

		private String up;
		private String right;
		private String down;
		private String left;
		
		private List<MovableEntity> players;

		public Game(int width, int height, int nbPlayer) {
			this.width = width;
			this.height = height;
			this.nbPlayer = nbPlayer;
			
			players = new ArrayList<>();
			for(int i=0 ; i<nbPlayer ; i++) {
				players.add(new MovableEntity());
			}

			System.err.println("width = " + width);
			System.err.println("height = " + height);
			System.err.println("nbPlayer = " + nbPlayer);
		}

		public void setVision(String up, String right, String down, String left) {
			this.up = up;
			this.right = right;
			this.down = down;
			this.left = left;

			System.err.println("left = " + up);
			System.err.println("right = " + right);
			System.err.println("down = " + down);
			System.err.println("left = " + left);
		}
		
		public void setPlayerPosition(int playerNumber, int x, int y) {
			System.err.println("x = " + x);
			System.err.println("y = " + y);
			players.get(playerNumber).setPosition(x, y);
		}


		public String printBestMove() {
			return Direction.STAY.getCode();
		}
		
	}
	
	class MovableEntity{
		int x;
		int y;
		
		public MovableEntity() {
			
		}
		public void setPosition(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		
	}

}
