package com.me.cavegenerator;

import com.badlogic.gdx.math.Vector2;

public class Cell {
	private int x, y;
	private CellType cellType;
	private WallType wallType;

	public boolean isCorner() {
//		if ((wallType != WallType.LOWER_LEFT_CONVEX
//				|| wallType != WallType.LOWER_RIGHT_CONVEX
//				|| wallType != WallType.UPPER_LEFT_CONVEX 
//				|| wallType != WallType.UPPER_RIGHT_CONVEX))
		if(this.cellType == CellType.CORNER_WALL)
			return true;
		else
			return false;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public CellType getCellType() {
		return cellType;
	}

	public void setCellType(CellType type) {
		this.cellType = type;
	}

	public WallType getWallType() {
		return wallType;
	}

	public void setWallType(WallType wallType) {
		switch (wallType) {
		case LOWER_LEFT_CONVEX:
		case LOWER_RIGHT_CONVEX:
		case UPPER_LEFT_CONVEX:
		case UPPER_RIGHT_CONVEX:
			this.cellType = CellType.CORNER_WALL;
			break;	
		case NONE:
			this.cellType = CellType.EMPTY;
			break;
		default:
			this.cellType = CellType.WALL;
			break;
		}
		
		this.wallType = wallType;
	}

	public Vector2 getPos() {
		return new Vector2(x, y);
	}

	public void setPos(Vector2 pos) {
		this.x = (int) pos.x;
		this.y = (int) pos.y;
	}

	public enum CellType {
		EMPTY, WALL, CORNER_WALL, LOCKED_WALL, LOCKED_EMPTY
	}

	// convex
	public enum WallType {
		NONE, SOLID, LOWER_LEFT_CONVEX, LOWER_RIGHT_CONVEX, UPPER_LEFT_CONVEX, UPPER_RIGHT_CONVEX,
		// there is no real use for concave corners, right?
		LOWER_LEFT_CONCAVE, LOWER_RIGHT_CONCAVE, UPPER_LEFT_CONCAVE, UPPER_RIGHT_CONCAVE, 
		LONELY_LEFT, LONELY_RIGHT, LONELY_TOP, LONELY_BOTTOM, LEFT, RIGHT, LEFT_RIGHT, GROUND, CEILING, GROUND_CEILING 
	}

	public Cell(CellType type, int x, int y) {
		this.wallType = WallType.NONE;
		this.cellType = type;
		this.x = x;
		this.y = y;
		this.setPos(new Vector2(this.x, this.y));
	}

	public Cell(CellType type, Vector2 pos) {
		this.wallType = WallType.NONE;
		this.cellType = type;
		this.x = (int) pos.x;
		this.y = (int) pos.y;
		this.setPos(pos);
	}
}
