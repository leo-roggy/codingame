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

		public Coord applyToCoord(Coord player) {
			return program.new Coord(player.x + xModifyer, player.y + yModifyer);
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

		public double computeCellScore(Coord cell) {

			int enemy = game.getNearestEnemyNumber(cell);
			int dist = game.getEnemyCoordDistance(enemy, cell);
			
			double distScore = (-1d / dist)*50d;
			double exploreScore = game.isExplored(cell) ? 0 : 10;

			double score = distScore + exploreScore;

			System.err.println("computeCellScore of x=" + cell.x + ",y=" + cell.y + " nearestEnemy = " + (enemy + 1)
					+ ", dist=" + dist + ", distScore = " + distScore + ", exploreScore = " + exploreScore + ", score = " + score);

			return score;
		}

		public Direction computeBestMove() {
			Random random = new Random();
			List<Direction> mainPlayerPossibleDirections = this.game.mainPlayerPossibleDirections();
			Coord player = game.getPlayer();

			Comparator<Direction> compa = new Comparator<Player.Direction>() {
				@Override
				public int compare(Direction d1, Direction d2) {
					Coord coord1 = d1.applyToCoord(player);
					Coord coord2 = d2.applyToCoord(player);

					double coord1Score = computeCellScore(coord1);
					double coord2Score = computeCellScore(coord2);

					int comparison = (int) (coord2Score - coord1Score);
					System.err.println("comparison between " + d1 + " and " + d2 + " = " + comparison);

					return comparison;
				}
			};

			mainPlayerPossibleDirections.sort(compa);
			System.err.println("sorted mainPlayerPossibleDirections : " + mainPlayerPossibleDirections);

			Direction bestMove = mainPlayerPossibleDirections.get(0);

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

		private List<Coord> enemies;
		private Coord player;

		private List<List<CellType>> cellTypeMap;
		
		private Set<Coord> exploredCoord;

		public Game(int width, int height, int nbPlayer) {
			this.width = width;
			this.height = height;
			this.nbPlayer = nbPlayer;

			enemies = new ArrayList<>();
			for (int i = 0; i < nbPlayer - 1; i++) {
				enemies.add(new Coord());
			}
			player = new Coord();

			cellTypeMap = new ArrayList<>();
			for (int x = 0; x < width; x++) {
				cellTypeMap.add(new ArrayList<>());
				for (int y = 0; y < height; y++) {
					cellTypeMap.get(x).add(CellType.UNKNOWN);
				}
			}
			
			exploredCoord = new HashSet<>();

			System.err.println("width = " + width);
			System.err.println("height = " + height);
			System.err.println("nbPlayer = " + nbPlayer);
		}

		public void setVision(String up, String right, String down, String left) {
			this.up = CellType.fromCode(up);
			this.right = CellType.fromCode(right);
			this.down = CellType.fromCode(down);
			this.left = CellType.fromCode(left);
		}

		public void setPlayerPosition(int playerNumber, int x, int y) {
//			System.err.println("position of " + playerNumber + " : x=" + x + " y=" + y);
			if (playerNumber < 4) {
				enemies.get(playerNumber).setPosition(x, y);
				this.setCellType(x, y, CellType.PATH);
			} else {
				player.setPosition(x, y);
				this.setCellType(x - 1, y, left);
				this.setCellType(x, y - 1, up);
				this.setCellType(x + 1, y, right);
				this.setCellType(x, y + 1, down);
				
				exploredCoord.add(new Coord(player.x, player.y));
			}
		}
		
		public boolean isExplored(Coord coord) {
			return exploredCoord.contains(coord);
		}

		public int getEnemyPlayerDistance(int enemyNumber) {
			Coord enemy = enemies.get(enemyNumber);
			return player.distance(enemy);
		}

		public int getEnemyCoordDistance(int enemyNumber, Coord coord) {
			Coord enemy = enemies.get(enemyNumber);
			return coord.distance(enemy);
		}

		public Coord getPlayer() {
			return player;
		}

		public int getNearestEnemyNumber(Coord coord) {
			int nearestEnemyNumber = 1000;
			int minDistance = 10000;
			for (int num = 0; num < enemies.size(); num++) {
				int enemyDistance = getEnemyCoordDistance(num, coord);
				if (enemyDistance < minDistance) {
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

	public class Coord {
		int x;
		int y;

		public Coord() {

		}

		public Coord(int x, int y) {
			this.x = x;
			this.y = y;
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

		public int distance(Coord coord2) {
			return Math.abs(coord2.x - x) + Math.abs(coord2.y - y);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Coord other = (Coord) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		private Player getEnclosingInstance() {
			return Player.this;
		}
		
		

	}

}
