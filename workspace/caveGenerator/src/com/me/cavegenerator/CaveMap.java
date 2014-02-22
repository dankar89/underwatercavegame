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
import com.me.cavegenerator.OLD_Cell.CornerType;

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

	public int getWidth() {
		return mapWidth;
	}

	public int getHeight() {
		return mapHeight;
	}

	public CaveMap(int width, int height, int smoothness) {
		this.isReady = false;
		this.mapWidth = width;
		this.mapHeight = height;

		// init a map with only wall tiles
		mapArray = new Cell[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				mapArray[i][j] = new Cell(CellType.WALL, i, j);
			}
		}
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
		else if(!emptyCellLeft && !emptyCellRight && !emptyCellAbove && !emptyCellBelow)
			wallType = wallType.SOLID;
		
		cell.setWallType(wallType);
		return cell;
	}

	public void cleanUp(int smoothness) {
		ArrayList<Cell> adjacentWalls = new ArrayList<Cell>();
		// ArrayList<Cell> corners = new ArrayList<Cell>();

		Cell currentCell = null;
		for (int i = 0; i < smoothness; i++) {
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {
					currentCell = getCellAt(x, y);
					if (currentCell.getCellType() == CellType.WALL) {
						// adjacentWalls = get8AdjacentCellsOfType(
						// currentCell.getPos(), CellType.WALL);
						// if (adjacentWalls.size() <= 2
						// || adjacentWalls.isEmpty()) {
						// getCellAt(x, y).setCellType(CellType.EMPTY);
						// } else if (adjacentWalls.size() >= 6
						// || adjacentWalls.isEmpty()) {
						// getCellAt(x, y).setCellType(CellType.WALL);
						// }

						// FIXME: Still get single, lonely tiles! Why the f**k
						// is that?!

						// FIXME: this code also removes the corners of the map
						// since they only have 3 adjacent cells...
						adjacentWalls = get8AdjacentCellsOfType(
								currentCell.getPos(), CellType.WALL);
						if (adjacentWalls.size() <= 3
								|| adjacentWalls.isEmpty()) {
							if ((currentCell.getX() > 0 && currentCell.getX() < mapWidth)
									&& (currentCell.getY() > 0 && currentCell
											.getY() < mapHeight)) {
								getCellAt(x, y).setCellType(CellType.EMPTY);
							}

						} else if (adjacentWalls.size() >= 5) {
							getCellAt(x, y).setCellType(CellType.WALL);
						}
					}
				}
			}
		}

		// find and set corners
		// TODO: this loop can probably be removed....
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				currentCell = getCellAt(x, y);
				if (currentCell.getCellType() == CellType.WALL) {
					// if (findCornerType(currentCell) != CornerType.NONE)
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

		if (right < mapWidth) {
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
		if (below < mapHeight) {
			if (getCellAt(x, below).getCellType() == type)
				adjacentCells.add(getCellAt(x, below));
		}

		return adjacentCells;
	}

	public ArrayList<Cell> getAdjacentCellsOfType(Vector2 cellPos, CellType type) {
		ArrayList<Cell> adjacentCells = new ArrayList<Cell>();

		int x = (int) cellPos.x;
		int y = (int) cellPos.y;

		int right = x + 1;
		int left = x - 1;
		int above = y - 1;
		int below = y + 1;

		if (right < mapWidth) {
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
		if (below < mapHeight) {
			if (getCellAt(x, below).getCellType() == type)
				adjacentCells.add(getCellAt(x, below));
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
					// if (getCellAt(x + dx, y + dy).getType() == CellType.WALL)
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

		if (above > 0) {
			adjacentCells.add(getCellAt(x, above));
		}
		if (right < mapWidth) {
			adjacentCells.add(getCellAt(right, y));
		}
		if (below < mapHeight) {
			adjacentCells.add(getCellAt(x, below));
		}
		if (left > 0) {
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
}
