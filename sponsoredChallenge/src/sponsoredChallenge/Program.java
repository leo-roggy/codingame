package sponsoredChallenge;

import java.util.*;
import java.io.*;
import java.math.*;

public class Program {

	static Program program = new Program();

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
				System.err.println("x = " + x);
				System.err.println("y = " + y);
			}
			in.nextLine();

			// Write an action using System.out.println()
			// To debug: System.err.println("Debug messages...");

			// System.out.println("A, B, C, D or E");
			System.out.println("A");
		}
	}

	enum Direction {
		UP("C"), RIGHT("A"), DOWN("D"), LEFT("E"), STAY("B");
		private String code;

		Direction(String Code) {
			this.code = code;
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
		
		private List<Player> players;

		public Game(int width, int height, int nbPlayer) {
			this.width = width;
			this.height = height;
			this.nbPlayer = nbPlayer;

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
	}
	
	class Player{
		int x;
		int y;
		
		public Player() {
			
		}
		public void setPosition(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		
	}

}
