import java.util.*;

import javax.swing.text.Position;

import java.io.*;
import java.math.*;

class Player {

	public static Player program = new Player();

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int height = in.nextInt();
		int width = in.nextInt();
		int nbPlayer = in.nextInt();
		if (in.hasNextLine()) {
			in.nextLine();
		}
		Game game = program.new Game(width, height, nbPlayer);
		IA ia = program.new IA(game);

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

			game.printMap();
			System.out.println(ia.computeBestMove().getCode());
		}
	}

	public enum Direction {
		UP("C", 0, -1), RIGHT("A", 1, 0), DOWN("D", 0, 1), LEFT("E", -1, 0), STAY("B", 0, 0);
		private String code;
		private int xModifyer;
		private int yModifyer;

		Direction(String code, int xModifyer, int yModifyer) {
			this.code = code;
			this.xModifyer = xModifyer;
			this.yModifyer = yModifyer;
		}

		public String getCode() {
			return this.code;
		}

		public int applyToX(int x) {
			return x + xModifyer;
		}

		public int applyToY(int y) {
			return y + yModifyer;
		}
	}

	public enum CellType {
		WALL("#"), PATH("_"), UNKNOWN(" ");

		private String code;

		CellType(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

		public static CellType fromCode(String code) {
			for (CellType cellType : CellType.values()) {
				if (cellType.getCode().equals(code)) {
					return cellType;
				}
			}
			return null;
		}

	}

	public class IA {

		private Game game;

		public IA(Game game) {
			this.game = game;
		}

		public Direction computeBestMove() {
			Random random = new Random();
			List<Direction> mainPlayerPossibleDirections = this.game.mainPlayerPossibleDirections();
			MovableEntity player = game.getPlayer();
			int nearestEnemyNumber = game.getNearestEnemyNumber();
//			int nearestEnemyNumber = 1 - 1;

			System.err.println("nearest enemy = " + (nearestEnemyNumber+1));
			
			Comparator<Direction> compa = new Comparator<Player.Direction>() {
				@Override
				public int compare(Direction d1, Direction d2) {
					int x1 = d1.applyToX(player.getX());
					int y1 = d1.applyToY(player.getY());
					int x2 = d2.applyToX(player.getX());
					int y2 = d2.applyToY(player.getY());
					
					int dist1 = game.getEnemyCoordDistance(nearestEnemyNumber, x1, y1);
					int dist2 = game.getEnemyCoordDistance(nearestEnemyNumber, x2, y2);

					int comparison = dist2 - dist1;
					System.err.println("comparison between "+d1+" and "+d2+" = " +comparison);
					
					return comparison;
				}
			};
			
			mainPlayerPossibleDirections.sort(compa);
			System.err.println("sorted mainPlayerPossibleDirections : "+mainPlayerPossibleDirections);

			Direction bestMove = mainPlayerPossibleDirections.get(0);
//			Direction bestMove = mainPlayerPossibleDirections.get(random.nextInt(mainPlayerPossibleDirections.size()));

			return bestMove;
		}

	}

	public class Game {

		private int width;
		private int height;
		private int nbPlayer;

		private CellType up;
		private CellType right;
		private CellType down;
		private CellType left;

		private List<MovableEntity> enemies;
		private MovableEntity player;

		private List<List<CellType>> cellTypeMap;

		public Game(int width, int height, int nbPlayer) {
			this.width = width;
			this.height = height;
			this.nbPlayer = nbPlayer;

			enemies = new ArrayList<>();
			for (int i = 0; i < nbPlayer - 1; i++) {
				enemies.add(new MovableEntity());
			}
			player = new MovableEntity();

			cellTypeMap = new ArrayList<>();
			for (int x = 0; x < width; x++) {
				cellTypeMap.add(new ArrayList<>());
				for (int y = 0; y < height; y++) {
					cellTypeMap.get(x).add(CellType.UNKNOWN);
				}
			}

			System.err.println("width = " + width);
			System.err.println("height = " + height);
			System.err.println("nbPlayer = " + nbPlayer);
		}

		public void setVision(String up, String right, String down, String left) {
			this.up = CellType.fromCode(up);
			this.right = CellType.fromCode(right);
			this.down = CellType.fromCode(down);
			this.left = CellType.fromCode(left);

//			System.err.println("left = " + up);
//			System.err.println("right = " + right);
//			System.err.println("down = " + down);
//			System.err.println("left = " + left);
		}

		public void setPlayerPosition(int playerNumber, int x, int y) {
			System.err.println("position of " + playerNumber + " : x=" + x + " y=" + y);
			if (playerNumber < 4) {
				enemies.get(playerNumber).setPosition(x, y);
				this.setCellType(x, y, CellType.PATH);
			} else {
				player.setPosition(x, y);
				this.setCellType(x - 1, y, left);
				this.setCellType(x, y - 1, up);
				this.setCellType(x + 1, y, right);
				this.setCellType(x, y + 1, down);
			}
		}

		public int getEnemyPlayerDistance(int enemyNumber) {
			MovableEntity enemy = enemies.get(enemyNumber);
//			return Math.sqrt((enemy.getX()-player.getX()) + (enemy.getY()-player.getY()));
			return Math.abs((enemy.getX() - player.getX())) + Math.abs((enemy.getY() - player.getY()));
		}

		public int getEnemyCoordDistance(int enemyNumber, int x, int y) {
			MovableEntity enemy = enemies.get(enemyNumber);
//			return Math.sqrt((enemy.getX()-x) + (enemy.getY()-y));
			return Math.abs((enemy.getX() - x)) + Math.abs((enemy.getY() - y));
		}

		public MovableEntity getPlayer() {
			return player;
		}

		public int getNearestEnemyNumber() {
			int nearestEnemyNumber = 1000;
			int minDistance = 10000;
			for (int num = 0; num < enemies.size(); num++) {
				int enemyDistance = getEnemyPlayerDistance(num);
				if (enemyDistance < (minDistance-2)) {
					minDistance = enemyDistance;
					nearestEnemyNumber = num;
				}
			}
			return nearestEnemyNumber;
		}

		public void setCellType(int x, int y, CellType cellType) {
			if (x >= 0 && x < width && y >= 0 && y < height)
				cellTypeMap.get(x).set(y, cellType);
		}

		public List<Direction> mainPlayerPossibleDirections() {
			ArrayList<Direction> list = new ArrayList<>();

			if (up == CellType.PATH)
				list.add(Direction.UP);
			if (right == CellType.PATH)
				list.add(Direction.RIGHT);
			if (down == CellType.PATH)
				list.add(Direction.DOWN);
			if (left == CellType.PATH)
				list.add(Direction.LEFT);
			list.add(Direction.STAY);

			return list;
		}

		public void printMap() {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					CellType cellType = cellTypeMap.get(x).get(y);
					String charToPrint = "";

					charToPrint = cellType.getCode();

					for (int i = 0; i < enemies.size(); i++) {
						if (enemies.get(i).getX() == x && enemies.get(i).getY() == y) {
							charToPrint = String.valueOf(i + 1);
						}
					}

					if (player.getX() == x && player.getY() == y) {
						charToPrint = String.valueOf("X");
					}

					System.err.print(charToPrint);
				}
				System.err.println("");
			}

		}
	}

	public class MovableEntity {
		int x;
		int y;

		public MovableEntity() {

		}

		public void setPosition(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

	}

}
