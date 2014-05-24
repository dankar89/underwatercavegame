package com.me.cavegenerator;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.me.cavegenerator.Cell.CellType;
import com.me.cavegenerator.Cell.WallType;
import common.GameConstants;
import common.Globals;

public class CaveMap implements Serializable {
	public enum MapCellType {
		EMPTY, WALL,
	}

	public enum CellDirection {
		ABOVE, RIGHT, BELOW, LEFT
	}

	private Cell[][] mapArray;
	private int mapWidth;
	private int mapHeight;
	private boolean isReady;

	private ArrayList<Miner> miners = new ArrayList<Miner>();
	private Miner startMiner;
	private Vector2 playerStartPos;
	
	private int waterLevel;

	// 1 = wall, 0 = empty, 2 = entrance, 3 = shop, 4 = player start pos, 9 = connects to the map
	private int[][] startArea = { 
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{9, 0, 1, 0, 0, 1, 0, 0, 0, 9 }, 
			{9, 0, 0, 2, 0, 3, 0, 0, 0, 9 },
			{9, 0, 1, 1, 1, 1, 1, 1, 0, 9 }, 
			{9, 0, 1, 1, 1, 1, 1, 1, 0, 9 },
			{9, 0, 0, 1, 1, 1, 1, 0, 0, 9 }, };
	private int startAreaWidth;
	private int startAreaHeight;
	private int mapHalfWidth;
	private int startAreaHalfWidth;
	private int startX;
	private int endX;
	private int startY;
	private int endY;

	public int getWidth() {
		return mapWidth;
	}

	public int getHeight() {
		return mapHeight;
	}

	public Vector2 getMinerStartPos() {
		int[] array = startArea[startAreaHeight - 1];
		for (int i = 0; i < array.length; i++) {
			if (array[i] == 0)
				return new Vector2(startX + i, endY - 1);
		}

		return new Vector2(mapWidth / 2, 1);
	}
	
	public Vector2 getPlayerStartPos(){
		return playerStartPos;
	}

	public Vector2 getHighestPointInCenter(){
		int xOffset = mapWidth / 5;
		int yOffset = startAreaHeight+1;
		for (int y = yOffset; y < this.mapHeight; y++) {
			for (int x = xOffset; x < this.mapWidth - xOffset; x++) {
				if (mapArray[x][y].getCellType() == CellType.EMPTY){
					System.out.println("highest point in center: " + mapArray[x][y].getPos());
					return mapArray[x][y].getPos();
				}
			}
		}
		//should never get here!
		return null;
	}
	
	public Vector2 getDeepestPointInCenter(){
		int xOffset = mapWidth / 5;
		for (int y =mapHeight-1; y > 1; y--) {
			for (int x = xOffset; x < this.mapWidth - xOffset; x++) {
				if (mapArray[x][y].getCellType() == CellType.EMPTY){
					System.out.println("deepest point in center: " + mapArray[x][y].getPos());
					return mapArray[x][y].getPos();
				}
			}
		}
		//should never get here!
		return null;
	}

	public CaveMap(int width, int height, int smoothness) {
		this.isReady = false;
		this.mapWidth = width;
		this.mapHeight = height;

		startAreaWidth = startArea[0].length;
		startAreaHeight = startArea.length;
		mapHalfWidth = mapWidth / 2;
		startAreaHalfWidth = startAreaWidth / 2;
		startX = (mapHalfWidth - startAreaHalfWidth) - 1;
		endX = mapHalfWidth + (startAreaHalfWidth - 1);
		startY = 0;
		endY = startAreaHeight - 1;

		// init a map with only wall tiles
		mapArray = new Cell[width][height];

		// createStartArea();

		CellType cellType = null;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (mapArray[i][j] == null) {
					// if(i == 0 || i == mapWidth-1 || j == 0 || j ==
					// mapHeight-1){
					// cellType = CellType.LOCKED_WALL;
					// } else {
					// cellType = CellType.WALL;
					// }
					cellType = CellType.WALL;
					mapArray[i][j] = new Cell(cellType, i, j);
				}
			}
		}
	}

	private void createStartArea() {

		Cell cell = null;
		int startAreaX = 0, startAreaY = 0;
		
		Vector2 highestPoint = getHighestPointInCenter();
		
		
		//not needed
		if(highestPoint.x + startAreaWidth < mapWidth){
			startX = (int) highestPoint.x; //this assumes that the lower-left cell in the start area is empty.
		} else {
			startX = (int) (highestPoint.x - startAreaWidth-1); //this assumes that the bottom-right cell in the start area is empty.
		}
		
		startY = (int) highestPoint.y;

		
		startY = (int) (highestPoint.y - startAreaHeight);
		endX = startX + startAreaWidth-1;
		endY = startY + startAreaHeight-1;
		
		
//		if ((startX >= 0 && endX < mapWidth) && startY >= 0 && endY < mapHeight) {
			for (int x = startX; x <= endX; x++) {
				for (int y = startY; y <= endY; y++) {
					if (startArea[startAreaY][startAreaX] == 1) {
						cell = new Cell(CellType.WALL, x, y);
					} else {
						String prop = "";
						if (startArea[startAreaY][startAreaX] == 2) {
							prop = "entrance";
							playerStartPos = new Vector2(x, y);
						} else if (startArea[startAreaY][startAreaX] == 3) {
							prop = "shop";
						}

						cell = new Cell(CellType.EMPTY, x, y, prop);
					}
					mapArray[x][y] = cell;
					startAreaY++;
				}
				startAreaY = 0;
				startAreaX++;
			}
//		}
	}

	public boolean isReady() {
		return this.isReady;
	}

	public Cell getCellAt(int x, int y) {
		return mapArray[x][y];
	}

	public Cell getCellAt(Vector2 cellPos) {
		return mapArray[(int) cellPos.x][(int) cellPos.y];
	}

	public void setCellAt(int x, int y, Cell cell) {
		mapArray[x][y] = cell;
	}

	public void updateCell(Cell cell) {
		mapArray[cell.getX()][cell.getY()] = cell;
	}

	public Cell[][] getArray() {
		return mapArray;
	}

	public boolean isCorner(Cell cell) {
		ArrayList<Cell> result = new ArrayList<Cell>(4);
		result = getAdjacentCellsOfType(cell, CellType.EMPTY);

		if (result.size() == 2 || result.size() == 3)
			return true;
		else
			return false;

	}

	public boolean findCornerType2(Cell cell) {
		int x = cell.getX();
		int y = cell.getY();

		HashMap<CellDirection, Cell> result = new HashMap<CellDirection, Cell>();
		result = getAdjacentCellsMap(cell);

		// getAdjacentCells returns the cells in the order; above, right, below,
		// left so check in that order...

		// check if corner
		if (result.size() == 2 || result.size() == 3)
			return true;
		else
			return false;

	}


	public WallType findWallType(Cell cell) {

		int x = cell.getX();
		int y = cell.getY();

		int right = x + 1;
		int left = x - 1;
		int above = y - 1;
		int below = y + 1;

		boolean emptyCellBelow = false;
		boolean emptyCellAbove = false;
		boolean emptyCellLeft = false;
		boolean emptyCellRight = false;

		WallType wallType = WallType.NONE;

		if (left > 0) {
			// check cell to the right
			if (getCellAt(left, y).getCellType() == CellType.EMPTY) {
				emptyCellLeft = true;
			}
		}

		if (right < mapWidth) {
			// check cell to the right
			if (getCellAt(right, y).getCellType() == CellType.EMPTY) {
				emptyCellRight = true;
			}
		}

		if (above > 0) {
			// check cell above
			if (getCellAt(x, above).getCellType() == CellType.EMPTY) {
				emptyCellAbove = true;
			}
		}

		if (below < mapHeight) {
			// check cell below
			if (getCellAt(x, below).getCellType() == CellType.EMPTY) {
				emptyCellBelow = true;
			}
		}

		// check for convex corners
		if (emptyCellRight && emptyCellAbove)
			wallType = WallType.UPPER_RIGHT_CONVEX;
		if (emptyCellRight && emptyCellBelow)
			wallType = WallType.LOWER_RIGHT_CONVEX;
		if (emptyCellLeft && emptyCellAbove)
			wallType = WallType.UPPER_LEFT_CONVEX;
		if (emptyCellLeft && emptyCellBelow)
			wallType = WallType.LOWER_LEFT_CONVEX;
		//
		// //check for concave corners
		// if (emptyCellRight && emptyCellAbove)
		// cornerType = CornerType.UPPER_RIGHT_CONVEX;
		// if (emptyCellRight && emptyCellBelow)
		// cornerType = CornerType.LOWER_RIGHT_CONVEX;
		// if (emptyCellLeft && emptyCellAbove)
		// cornerType = CornerType.UPPER_LEFT_CONVEX;
		// if (emptyCellLeft && emptyCellBelow)
		// cornerType = CornerType.LOWER_LEFT_CONVEX;

		// find "lonely corners
		if (emptyCellLeft && emptyCellRight && emptyCellAbove)
			wallType = WallType.LONELY_TOP;
		if (emptyCellLeft && emptyCellRight && emptyCellBelow)
			wallType = WallType.LONELY_BOTTOM;
		if (emptyCellLeft && emptyCellBelow && emptyCellAbove)
			wallType = WallType.LONELY_RIGHT;
		if (emptyCellRight && emptyCellBelow && emptyCellAbove)
			wallType = WallType.LONELY_LEFT;

		cell.setWallType(wallType);
		return wallType;
	}

	public Cell findAndUpdateWallType(Cell cell) {
		int x = cell.getX();
		int y = cell.getY();

		int right = x + 1;
		int left = x - 1;
		int above = y - 1;
		int below = y + 1;

		boolean emptyCellBelow = false;
		boolean emptyCellAbove = false;
		boolean emptyCellLeft = false;
		boolean emptyCellRight = false;

		WallType wallType = WallType.SOLID;

		if (left >= 0) {
			// check cell to the right
			if (getCellAt(left, y).getCellType() == CellType.EMPTY) {
				emptyCellLeft = true;
			}
		}

		if (right < mapWidth) {
			// check cell to the right
			if (getCellAt(right, y).getCellType() == CellType.EMPTY) {
				emptyCellRight = true;
			}
		}

		if (above >= 0) {
			// check cell above
			if (getCellAt(x, above).getCellType() == CellType.EMPTY) {
				emptyCellAbove = true;
			}
		}

		if (below < mapHeight) {
			// check cell below
			if (getCellAt(x, below).getCellType() == CellType.EMPTY) {
				emptyCellBelow = true;
			}
		}

		// check for convex corners
		if (emptyCellRight && emptyCellAbove)
			wallType = WallType.UPPER_RIGHT_CONVEX;
		if (emptyCellRight && emptyCellBelow)
			wallType = WallType.LOWER_RIGHT_CONVEX;
		if (emptyCellLeft && emptyCellAbove)
			wallType = WallType.UPPER_LEFT_CONVEX;
		if (emptyCellLeft && emptyCellBelow)
			wallType = WallType.LOWER_LEFT_CONVEX;

		// find "lonely corners
		if (emptyCellLeft && emptyCellRight && emptyCellAbove)
			wallType = WallType.LONELY_TOP;
		if (emptyCellLeft && emptyCellRight && emptyCellBelow)
			wallType = WallType.LONELY_BOTTOM;
		if (emptyCellLeft && emptyCellBelow && emptyCellAbove)
			wallType = WallType.LONELY_LEFT;
		if (emptyCellRight && emptyCellBelow && emptyCellAbove)
			wallType = WallType.LONELY_RIGHT;

		// find wall types

		// find right-sided wall
		if (emptyCellLeft && !emptyCellRight && !emptyCellAbove
				&& !emptyCellBelow)
			wallType = wallType.RIGHT;
		// find left-sided wall
		if (!emptyCellLeft && emptyCellRight && !emptyCellAbove
				&& !emptyCellBelow)
			wallType = wallType.LEFT;
		// find cells that are both left and right walls
		if (emptyCellLeft && emptyCellRight && !emptyCellAbove
				&& !emptyCellBelow)
			wallType = wallType.LEFT_RIGHT;

		// find ground
		if (!emptyCellLeft && !emptyCellRight && emptyCellAbove
				&& !emptyCellBelow)
			wallType = wallType.GROUND;
		// find ceiling
		if (!emptyCellLeft && !emptyCellRight && !emptyCellAbove
				&& emptyCellBelow)
			wallType = wallType.CEILING;
		// find cells that ar both ceiling and ground
		if (!emptyCellLeft && !emptyCellRight && emptyCellAbove
				&& emptyCellBelow)
			wallType = wallType.GROUND_CEILING;

		if (emptyCellLeft && emptyCellRight && emptyCellAbove && emptyCellBelow)
			wallType = wallType.NONE;
		else if (!emptyCellLeft && !emptyCellRight && !emptyCellAbove
				&& !emptyCellBelow)
			wallType = wallType.SOLID;

		cell.setWallType(wallType);
		return cell;
	}

	public void cleanUp(int smoothness) {
		ArrayList<Cell> adjacentWalls = new ArrayList<Cell>();

		Cell currentCell = null;
		
		Vector2 deepestPoint = getDeepestPointInCenter();
		Vector2 highestPoint = getHighestPointInCenter();
		int caveDepth = (int) (deepestPoint.y - highestPoint.y);
		System.out.println("caveDepth: " + caveDepth);
		int maxWaterLevel = (int) highestPoint.y + (caveDepth/8);			
		int minWaterLevel = (int) deepestPoint.y - (caveDepth/4);
//		int maxWaterLevel = 20;			
//		int minWaterLevel = 130;
		
		System.out.println("max: " + maxWaterLevel + ", min: " + minWaterLevel);
		waterLevel = Globals.random
				.nextInt((minWaterLevel - maxWaterLevel) + 1) + maxWaterLevel;
		System.out.println(waterLevel);
		
		for (int i = 0; i < smoothness; i++) {
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {
					currentCell = getCellAt(x, y);
					if (currentCell.getCellType() == CellType.WALL) {

						adjacentWalls = get8AdjacentCellsOfType(
								currentCell.getPos(), CellType.WALL);
						if (adjacentWalls.size() <= 2 /* 3 */
								|| adjacentWalls.isEmpty()) {
							if ((currentCell.getX() > 0 && currentCell.getX() < mapWidth)
									&& (currentCell.getY() > 0 && currentCell
											.getY() < mapHeight)) {
								getCellAt(x, y).setCellType(CellType.EMPTY);
							}

						} else if (adjacentWalls.size() >= 5 /* 5 */) {
							getCellAt(x, y).setCellType(CellType.WALL);
						}
					}
				}
			}
		}

		createStartArea();

		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				currentCell = getCellAt(x, y);
				if (currentCell.getCellType() == CellType.WALL) {
					setCellAt(x, y, findAndUpdateWallType(currentCell));
				}
			}
		}

		this.isReady = true;
	}

	public void cleanUp2(int smoothness) {
		ArrayList<Cell> adjacentWalls = new ArrayList<Cell>();
		ArrayList<Cell> corners = new ArrayList<Cell>();
		int oneStepWallCount = 0;
		boolean isBorder = false;
		boolean atLeast5Walls = false;

		// TODO:
		// http://www.avanderw.co.za/making-a-cave-like-structure-with-worms/
		for (int i = 0; i < smoothness; i++) {
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {
					// get8AdjacentCellsOfType(new Vector2(x, y),
					// CellType.WALL);
					oneStepWallCount = calcNumWallsNStepsFromPoint(x, y, 1);
					if (x == 0 || y == 0 || x == mapWidth - 1
							|| y == mapHeight - 1)
						isBorder = true;
					else
						isBorder = false;

					if (oneStepWallCount >= 5
							|| (oneStepWallCount >= 4 && getCellAt(x, y)
									.getCellType() == CellType.WALL))
						atLeast5Walls = true;
					else
						atLeast5Walls = false;

					if (isBorder || atLeast5Walls) {
						getCellAt(x, y).setCellType(CellType.WALL);
					} else {
						// getCellAt(x, y).setCellType(CellType.EMPTY);
					}
				}
			}
		}
	}

	public void fillGapsAndSmooth(int smoothness) {
		// public function fillGapsAndSmooth(map:Vector.<Vector.>,
		// smoothness:int):void
		// {
		// var i:int, x:int, y:int;
		// var height:int = map.length;
		// var width:int = map[0].length;
		//
		// var mapBuffer:Vector.<Vector.>;
		// var oneStepWallCount:int;
		// var twoStepWallCount:int;
		//
		// var isBorder:Boolean;
		// var atLeast5Walls:Boolean;
		// var atMost2Walls:Boolean;
		//
		// for (i = 0; i < smoothness; i++)
		// {
		// mapBuffer = createIntVectorMatrix(height, width, 0);
		// for (y = 0; y < height; y++)
		// {
		// for (x = 0; x < width; x++) {
		// oneStepWallCount = calcNumWallsNStepsFromPoint(map, x, y, 1);
		// twoStepWallCount = calcNumWallsNStepsFromPoint(map, x, y, 2);
		// isBorder = x == 0 || y == 0 || x == width - 1 || y == height - 1;
		// atLeast5Walls = oneStepWallCount >= 5 || (oneStepWallCount +
		// map[y][x]) >= 5;
		// atMost2Walls = (twoStepWallCount + map[y][x]) <= 2;
		//
		// mapBuffer[y][x] = (isBorder || atLeast5Walls || atMost2Walls) ? 1 :
		// 0;
		// }
		// }
		// copyIntVectorMatrixInto(mapBuffer, map);
		// }
		// }

		boolean isBorder = false;
		boolean atLeast5Walls = false;
		boolean atMost2Walls = false;
		int oneStepWallCount = 0;
		int twoStepWallCount = 0;

		for (int i = 0; i < smoothness; i++) {
			for (int y = 0; y < mapHeight; y++) {
				for (int x = 0; x < mapWidth; x++) {
					oneStepWallCount = calcNumWallsNStepsFromPoint(x, y, 1);
					twoStepWallCount = calcNumWallsNStepsFromPoint(x, y, 2);

					if (x == 0 || y == 0 || x == mapWidth - 1
							|| y == mapHeight - 1)
						isBorder = true;
					else
						isBorder = false;

					if (oneStepWallCount >= 5
							|| (oneStepWallCount >= 4 && getCellAt(x, y)
									.getCellType() == CellType.WALL))
						atLeast5Walls = true;
					else
						atLeast5Walls = false;

					if (twoStepWallCount <= 1
							&& getCellAt(x, y).getCellType() == CellType.WALL)
						atMost2Walls = true;
					else
						atMost2Walls = false;

					if (isBorder || atLeast5Walls || atMost2Walls)
						getCellAt(x, y).setCellType(CellType.WALL);
					// else
					// getCellAt(x, y).setCellType(CellType.EMPTY);
				}
			}
		}
	}

	private int calcNumWallsNStepsFromPoint(int x, int y, int steps) {
		int[] xRange = { Math.max(0, x - steps),
				Math.min(mapWidth - 1, x + steps) };
		int[] yRange = { Math.max(0, y - steps),
				Math.min(mapHeight - 1, y + steps) };

		int wallCount = 0;

		for (int xi = xRange[0]; xi <= xRange[1]; xi++) {
			for (int yi = yRange[0]; yi <= yRange[1]; yi++) {
				if (xi != x && yi != y) {
					if (getCellAt(xi, yi).getCellType() == CellType.WALL)
						wallCount++;
				}
			}
		}

		return wallCount;
	}

	public ArrayList<Cell> getAdjacentCellsOfType(Cell cell, CellType type) {
		ArrayList<Cell> adjacentCells = new ArrayList<Cell>();

		int x = cell.getX();
		int y = cell.getY();

		int right = x + 1;
		int left = x - 1;
		int above = y - 1;
		int below = y + 1;

		if (right < mapWidth - 1) {
			if (getCellAt(right, y).getCellType() == type)
				adjacentCells.add(getCellAt(right, y));
		}
		if (left > 0) {
			if (getCellAt(left, y).getCellType() == type)
				adjacentCells.add(getCellAt(left, y));
		}
		if (above > 0) {
			if (getCellAt(x, above).getCellType() == type)
				adjacentCells.add(getCellAt(x, above));
		}
		if (below < mapHeight - 1) {
			if (getCellAt(x, below).getCellType() == type)
				adjacentCells.add(getCellAt(x, below));
		}

		return adjacentCells;
	}

	public ArrayList<Cell> getAdjacentCellsOfType(Vector2 cellPos,
			CellType... types) {
		ArrayList<Cell> adjacentCells = new ArrayList<Cell>();

		int x = (int) cellPos.x;
		int y = (int) cellPos.y;

		int right = x + 1;
		int left = x - 1;
		int above = y - 1;
		int below = y + 1;

		// FIXME: Why do I need -3 here?
		for (CellType cellType : types) {
			if (right < mapWidth - 3) {
				if (getCellAt(right, y).getCellType() == cellType)
					adjacentCells.add(getCellAt(right, y));
			}
			if (left > 1) {
				if (getCellAt(left, y).getCellType() == cellType)
					adjacentCells.add(getCellAt(left, y));
			}
			if (above > 1) {
				if (getCellAt(x, above).getCellType() == cellType)
					adjacentCells.add(getCellAt(x, above));
			}
			if (below < mapHeight - 3) {
				if (getCellAt(x, below).getCellType() == cellType)
					adjacentCells.add(getCellAt(x, below));
			}
		}

		return adjacentCells;
	}

	public ArrayList<Cell> getAdjacentCellsOfTypeInDirection(Vector2 cellPos,
			CellType type, Vector2 dir) {
		ArrayList<Cell> adjacentCells = new ArrayList<Cell>();

		int x = (int) cellPos.x;
		int y = (int) cellPos.y;

		int right = (x + 1);
		int left = x - 1;
		int above = y - 1;
		int below = y + 1;

		if (dir.x == 1) {
			if (right < mapWidth - 1) {
				if (getCellAt(right, y).getCellType() == type)
					adjacentCells.add(getCellAt(right, y));
			}
		} else if (dir.x == -1) {
			if (left > 0) {
				if (getCellAt(left, y).getCellType() == type)
					adjacentCells.add(getCellAt(left, y));
			}
		}

		if (dir.y == 1) {
			if (below < mapHeight - 1) {
				if (getCellAt(x, below).getCellType() == type)
					adjacentCells.add(getCellAt(x, below));
			}
		} else if (dir.y == -1) {
			if (above > 0) {
				if (getCellAt(x, above).getCellType() == type)
					adjacentCells.add(getCellAt(x, above));
			}
		}
		return adjacentCells;
	}

	public ArrayList<Cell> get8AdjacentCells(Vector2 cellPos) {
		ArrayList<Cell> adjacentCells = new ArrayList<Cell>();

		int x = (int) cellPos.x;
		int y = (int) cellPos.y;

		for (int dx = (x > 0 ? -1 : 0); dx <= (x < mapWidth - 1 ? 1 : 0); ++dx) {
			for (int dy = (y > 0 ? -1 : 0); dy <= (y < mapHeight - 1 ? 1 : 0); ++dy) {
				if (dx != 0 || dy != 0) {
					adjacentCells.add(mapArray[x + dx][y + dy]);
				}
			}
		}

		return adjacentCells;
	}

	public ArrayList<Cell> get8AdjacentCellsOfType(Vector2 cellPos,
			CellType type) {
		ArrayList<Cell> adjacentCells = new ArrayList<Cell>();

		int x = (int) cellPos.x;
		int y = (int) cellPos.y;

		for (int dx = (x > 0 ? -1 : 0); dx <= (x < mapWidth - 1 ? 1 : 0); ++dx) {
			for (int dy = (y > 0 ? -1 : 0); dy <= (y < mapHeight - 1 ? 1 : 0); ++dy) {
				if (dx != 0 || dy != 0) {
					if (getCellAt(x + dx, y + dy).getCellType() == type)
						adjacentCells.add(mapArray[x + dx][y + dy]);
				}
			}
		}

		return adjacentCells;
	}

	public ArrayList<Cell> getAdjacentCells(Cell cell) {
		ArrayList<Cell> adjacentCells = new ArrayList<Cell>();

		int x = cell.getX();
		int y = cell.getY();

		int right = x + 1;
		int left = x - 1;
		int above = y - 1;
		int below = y + 1;

		if (above > 1) {
			adjacentCells.add(getCellAt(x, above));
		}
		if (right < mapWidth - 2) {
			adjacentCells.add(getCellAt(right, y));
		}
		if (below < mapHeight - 2) {
			adjacentCells.add(getCellAt(x, below));
		}
		if (left > 1) {
			adjacentCells.add(getCellAt(left, y));
		}

		return adjacentCells;
	}

	public ArrayList<Cell> getAdjacentCells(Vector2 cellPos) {
		ArrayList<Cell> adjacentCells = new ArrayList<Cell>();

		int x = (int) cellPos.x;
		int y = (int) cellPos.y;

		int right = x + 1;
		int left = x - 1;
		int above = y - 1;
		int below = y + 1;

		if (above > 1) {
			adjacentCells.add(getCellAt(x, above));
		}
		if (right < mapWidth - 2) {
			adjacentCells.add(getCellAt(right, y));
		}
		if (below < mapHeight - 2) {
			adjacentCells.add(getCellAt(x, below));
		}
		if (left > 1) {
			adjacentCells.add(getCellAt(left, y));
		}

		return adjacentCells;
	}

	public HashMap<CellDirection, Cell> getAdjacentCellsMap(Cell cell) {
		HashMap<CellDirection, Cell> cellMap = new HashMap<CellDirection, Cell>();
		int x = cell.getX();
		int y = cell.getY();

		int right = x + 1;
		int left = x - 1;
		int above = y - 1;
		int below = y + 1;

		if (above > 0) {
			cellMap.put(CellDirection.ABOVE, getCellAt(x, above));
		}
		if (right < mapWidth) {
			cellMap.put(CellDirection.RIGHT, getCellAt(right, y));
		}
		if (below < mapHeight) {
			cellMap.put(CellDirection.BELOW, getCellAt(x, below));
		}
		if (left > 0) {
			cellMap.put(CellDirection.LEFT, getCellAt(left, y));
		}

		return cellMap;
	}

	public void draw(SpriteBatch batch, Texture texture) {
		for (int i = 0; i < this.mapWidth; i++) {
			for (int j = 0; j < this.mapHeight; j++) {
				if (getCellAt(i, j).getCellType() != CellType.EMPTY)
					// || getCellAt(i, j).getType() == CellType.CORNER_WALL)
					batch.draw(texture, i * texture.getWidth(),
							j * texture.getHeight());
			}
		}
	}

	public void drawCells(SpriteBatch batch, Texture texture, CellType type) {
		for (int i = 0; i < this.mapWidth; i++) {
			for (int j = 0; j < this.mapHeight; j++) {
				if (getCellAt(i, j).getCellType() == type)
					batch.draw(texture, i * texture.getWidth(),
							j * texture.getHeight());
			}
		}
	}

	public void drawCells(SpriteBatch batch, Texture texture, CellType[] types) {
		for (int i = 0; i < this.mapWidth; i++) {
			for (int j = 0; j < this.mapHeight; j++) {
				for (CellType cellType : types) {
					if (getCellAt(i, j).getCellType() == cellType) {
						batch.draw(texture, i * texture.getWidth(),
								j * texture.getHeight());
					}
				}

			}
		}
	}

	public void drawCells(SpriteBatch batch, Texture texture, WallType[] types) {
		for (int i = 0; i < this.mapWidth; i++) {
			for (int j = 0; j < this.mapHeight; j++) {
				for (WallType wallType : types) {
					if (getCellAt(i, j).getWallType() == wallType) {
						batch.draw(texture, i * texture.getWidth(),
								j * texture.getHeight());
					}
				}

			}
		}
	}

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub

	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub

	}

	public int getWaterLevel() {
		return waterLevel;
	}
}
