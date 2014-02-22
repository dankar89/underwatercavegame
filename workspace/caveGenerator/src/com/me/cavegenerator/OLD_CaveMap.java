//package com.me.cavegenerator;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.utils.Json;
//import com.badlogic.gdx.utils.Json.Serializable;
//import com.badlogic.gdx.utils.JsonValue;
//import com.me.cavegenerator.Cell.CellType;
//import com.me.cavegenerator.Cell.CornerType;
//
//public class OLD_CaveMap implements Serializable {
//	public enum MapCellType {
//		EMPTY, WALL,
//	}
//
//	public enum CellDirection {
//		ABOVE, RIGHT, BELOW, LEFT
//	}
//
//	private Cell[][] mapArray;
//	private int mapWidth;
//	private int mapHeight;
//	private boolean isReady;
//
//	public int getWidth() {
//		return mapWidth;
//	}
//
//	public int getHeight() {
//		return mapHeight;
//	}
//
//	public OLD_CaveMap(int width, int height) {
//		this.isReady = false;
//		this.mapWidth = width;
//		this.mapHeight = height;
//
//		// init a map with only wall tiles
//		mapArray = new Cell[width][height];
//		for (int i = 0; i < width; i++) {
//			for (int j = 0; j < height; j++) {
//				mapArray[i][j] = new Cell(CellType.WALL, i, j);
//			}
//		}
//	}
//
//	public boolean isReady() {
//		return this.isReady;
//	}
//
//	public Cell getCellAt(int x, int y) {
//		return mapArray[x][y];
//	}
//
//	public Cell getCellAt(Vector2 cellPos) {
//		return mapArray[(int)cellPos.x][(int)cellPos.y];
//	}
//	
//	public void setCellAt(int x, int y, Cell cell) {
//		mapArray[x][y] = cell;
//	}
//
//	public void updateCell(Cell cell) {
//		mapArray[cell.getX()][cell.getY()] = cell;
//	}
//
//	public Cell[][] getArray() {
//		return mapArray;
//	}
//
//	public boolean isCorner(Cell cell) {
//		ArrayList<Cell> result = new ArrayList<Cell>(4);
//		result = getAdjacentCellsOfType(cell, CellType.EMPTY);
//
//		if (result.size() == 2 || result.size() == 3)
//			return true;
//		else
//			return false;
//
//	}
//
//	public boolean findCornerType2(Cell cell) {
//		int x = cell.getX();
//		int y = cell.getY();
//
//		HashMap<CellDirection, Cell> result = new HashMap<CellDirection, Cell>();
//		result = getAdjacentCellsMap(cell);
//		
//		// getAdjacentCells returns the cells in the order; above, right, below,
//		// left so check in that order...
//
//		// check if corner
//		if (result.size() == 2 || result.size() == 3)
//			return true;
//		else
//			return false;
//
//	}
//
//	public CornerType findCornerType(Cell cell) {
//
//		int x = cell.getX();
//		int y = cell.getY();
//
//		int right = x + 1;
//		int left = x - 1;
//		int above = y - 1;
//		int below = y + 1;
//
//		boolean emptyCellBelow = false;
//		boolean emptyCellAbove = false;
//		boolean emptyCellLeft = false;
//		boolean emptyCellRight = false;
//
//		CornerType cornerType = CornerType.NONE;
//
//		if (left > 0) {
//			// check cell to the right
//			if (getCellAt(left, y).getCellType() == CellType.EMPTY) {
//				emptyCellLeft = true;
//			}
//		}
//
//		if (right < mapWidth) {
//			// check cell to the right
//			if (getCellAt(right, y).getCellType() == CellType.EMPTY) {
//				emptyCellRight = true;
//			}
//		}
//
//		if (above > 0) {
//			// check cell above
//			if (getCellAt(x, above).getCellType() == CellType.EMPTY) {
//				emptyCellAbove = true;
//			}
//		}
//
//		if (below < mapHeight) {
//			// check cell below
//			if (getCellAt(x, below).getCellType() == CellType.EMPTY) {
//				emptyCellBelow = true;
//			}
//		}
//
//		//check for convex corners
//		if (emptyCellRight && emptyCellAbove)
//			cornerType = CornerType.UPPER_RIGHT_CONVEX;
//		if (emptyCellRight && emptyCellBelow)
//			cornerType = CornerType.LOWER_RIGHT_CONVEX;
//		if (emptyCellLeft && emptyCellAbove)
//			cornerType = CornerType.UPPER_LEFT_CONVEX;
//		if (emptyCellLeft && emptyCellBelow)
//			cornerType = CornerType.LOWER_LEFT_CONVEX;
////
////		//check for concave corners
////		if (emptyCellRight && emptyCellAbove)
////			cornerType = CornerType.UPPER_RIGHT_CONVEX;
////		if (emptyCellRight && emptyCellBelow)
////			cornerType = CornerType.LOWER_RIGHT_CONVEX;
////		if (emptyCellLeft && emptyCellAbove)
////			cornerType = CornerType.UPPER_LEFT_CONVEX;
////		if (emptyCellLeft && emptyCellBelow)
////			cornerType = CornerType.LOWER_LEFT_CONVEX;
//		
//		//find "lonely corners
//		if (emptyCellLeft && emptyCellRight && emptyCellAbove)
//			cornerType = CornerType.LONELY_TOP;
//		if (emptyCellLeft && emptyCellRight && emptyCellBelow)
//			cornerType = CornerType.LONELY_BOTTOM;
//		if (emptyCellLeft && emptyCellBelow && emptyCellAbove)
//			cornerType = CornerType.LONELY_LEFT;
//		if (emptyCellRight && emptyCellBelow && emptyCellAbove)
//			cornerType = CornerType.LONELY_RIGHT;
//		
//		cell.setCornerType(cornerType);
//		return cornerType;
//	}
//
//	public void cleanUp() {
//		ArrayList<Cell> adjacentWalls = new ArrayList<Cell>();
//		ArrayList<Cell> corners = new ArrayList<Cell>();
//
//		Cell currentCell = null;
//		for (int i = 0; i < mapWidth; i++) {
//			for (int j = 0; j < mapHeight; j++) {
//				currentCell = getCellAt(i, j);
//				if (currentCell.getCellType() != CellType.EMPTY) {
//					adjacentWalls = get8AdjacentCellsOfType(currentCell.getPos(),
//							CellType.WALL);
//
//					// if (adjacentWalls.isEmpty()) {
//					// // remove the wall if it is surrounded by empty cells
//					// getCellAt(i, j).setType(CellType.EMPTY);
//					// }
//
//					if (adjacentWalls.size() <= 1) {
//						getCellAt(i, j).setCellType(CellType.EMPTY);
//					}
//				}
//			}
//		}
//
//		// find and set corners
//		for (int i = 0; i < mapWidth; i++) {
//			for (int j = 0; j < mapHeight; j++) {
//				currentCell = getCellAt(i, j);
//				if (currentCell.getCellType() == CellType.WALL) {
////					if (isCorner(currentCell)) {
////						getCellAt(i, j).setCellType(CellType.CORNER_LOWER_LEFT);
////					}
//					if(findCornerType(currentCell) != CornerType.NONE)
//						getCellAt(i, j).setCellType(CellType.CORNER);
//				}
//			}
//		}
//
//		this.isReady = true;
//	}
//
//	public ArrayList<Cell> getAdjacentCellsOfType(Cell cell, CellType type) {
//		ArrayList<Cell> adjacentCells = new ArrayList<Cell>();
//
//		int x = cell.getX();
//		int y = cell.getY();
//
//		int right = x + 1;
//		int left = x - 1;
//		int above = y - 1;
//		int below = y + 1;
//
//		if (right < mapWidth) {
//			if (getCellAt(right, y).getCellType() == type)
//				adjacentCells.add(getCellAt(right, y));
//		}
//		if (left > 0) {
//			if (getCellAt(left, y).getCellType() == type)
//				adjacentCells.add(getCellAt(left, y));
//		}
//		if (above > 0) {
//			if (getCellAt(x, above).getCellType() == type)
//				adjacentCells.add(getCellAt(x, above));
//		}
//		if (below < mapHeight) {
//			if (getCellAt(x, below).getCellType() == type)
//				adjacentCells.add(getCellAt(x, below));
//		}
//
//		return adjacentCells;
//	}
//
//	public ArrayList<Cell> get8AdjacentCells(Vector2 cellPos) {
//		ArrayList<Cell> adjacentCells = new ArrayList<Cell>();
//
//		int x = (int) cellPos.x;
//		int y = (int) cellPos.y;
//
//		for (int dx = (x > 0 ? -1 : 0); dx <= (x < mapWidth - 1 ? 1 : 0); ++dx) {
//			for (int dy = (y > 0 ? -1 : 0); dy <= (y < mapHeight - 1 ? 1 : 0); ++dy) {
//				if (dx != 0 || dy != 0) {
//					// if (getCellAt(x + dx, y + dy).getType() == CellType.WALL)
//					adjacentCells.add(mapArray[x + dx][y + dy]);
//				}
//			}
//		}
//
//		return adjacentCells;
//	}
//
//	public ArrayList<Cell> get8AdjacentCellsOfType(Vector2 cellPos,
//			CellType type) {
//		ArrayList<Cell> adjacentCells = new ArrayList<Cell>();
//
//		int x = (int) cellPos.x;
//		int y = (int) cellPos.y;
//
//		for (int dx = (x > 0 ? -1 : 0); dx <= (x < mapWidth - 1 ? 1 : 0); ++dx) {
//			for (int dy = (y > 0 ? -1 : 0); dy <= (y < mapHeight - 1 ? 1 : 0); ++dy) {
//				if (dx != 0 || dy != 0) {
//					if (getCellAt(x + dx, y + dy).getCellType() == type)
//						adjacentCells.add(mapArray[x + dx][y + dy]);
//				}
//			}
//		}
//
//		return adjacentCells;
//	}
//
//	public ArrayList<Cell> getAdjacentCells(Cell cell) {
//		ArrayList<Cell> adjacentCells = new ArrayList<Cell>();
//
//		int x = cell.getX();
//		int y = cell.getY();
//
//		int right = x + 1;
//		int left = x - 1;
//		int above = y - 1;
//		int below = y + 1;
//
//		if (above > 0) {
//			adjacentCells.add(getCellAt(x, above));
//		}
//		if (right < mapWidth) {
//			adjacentCells.add(getCellAt(right, y));
//		}
//		if (below < mapHeight) {
//			adjacentCells.add(getCellAt(x, below));
//		}
//		if (left > 0) {
//			adjacentCells.add(getCellAt(left, y));
//		}
//
//		return adjacentCells;
//	}
//
//	public HashMap<CellDirection, Cell> getAdjacentCellsMap(Cell cell) {
//		HashMap<CellDirection, Cell> cellMap = new HashMap<CellDirection, Cell>();
//		int x = cell.getX();
//		int y = cell.getY();
//
//		int right = x + 1;
//		int left = x - 1;
//		int above = y - 1;
//		int below = y + 1;
//
//		if (above > 0) {
//			cellMap.put(CellDirection.ABOVE, getCellAt(x, above));			
//		}
//		if (right < mapWidth) {
//			cellMap.put(CellDirection.RIGHT, getCellAt(right, y));
//		}
//		if (below < mapHeight) {
//			cellMap.put(CellDirection.BELOW, getCellAt(x, below));
//		}
//		if (left > 0) {
//			cellMap.put(CellDirection.LEFT, getCellAt(left, y));
//		}
//
//		return cellMap;
//	}
//
//	public void draw(SpriteBatch batch, Texture texture) {
//		for (int i = 0; i < this.mapWidth; i++) {
//			for (int j = 0; j < this.mapHeight; j++) {
//				if (getCellAt(i, j).getCellType() != CellType.EMPTY)
//					// || getCellAt(i, j).getType() == CellType.CORNER_WALL)
//					batch.draw(texture, i * texture.getWidth(),
//							j * texture.getHeight());
//			}
//		}
//	}
//
//	public void drawCells(SpriteBatch batch, Texture texture, CellType type) {
//		for (int i = 0; i < this.mapWidth; i++) {
//			for (int j = 0; j < this.mapHeight; j++) {
//				if (getCellAt(i, j).getCellType() == type)
//					batch.draw(texture, i * texture.getWidth(),
//							j * texture.getHeight());
//			}
//		}
//	}
//
//	public void drawCells(SpriteBatch batch, Texture texture, CellType[] types) {
//		for (int i = 0; i < this.mapWidth; i++) {
//			for (int j = 0; j < this.mapHeight; j++) {
//				for (CellType cellType : types) {
//					if (getCellAt(i, j).getCellType() == cellType) {
//						batch.draw(texture, i * texture.getWidth(),
//								j * texture.getHeight());
//					}
//				}
//
//			}
//		}
//	}
//
//	@Override
//	public void write(Json json) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void read(Json json, JsonValue jsonData) {
//		// TODO Auto-generated method stub
//
//	}
//}
