package com.me.cavegenerator;

import com.badlogic.gdx.math.Vector2;

public class OLD_Cell {
	private int x, y;
	private CellType cellType;
	private CornerType cornerType;

	public boolean isCorner() {
		if (cornerType != CornerType.NONE)
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

	public CornerType getCornerType() {
		return cornerType;
	}

	public void setCornerType(CornerType cornerType) {
		this.cornerType = cornerType;
	}

	public Vector2 getPos() {
		return new Vector2(x, y);
	}

	public void setPos(Vector2 pos) {
		this.x = (int) pos.x;
		this.y = (int) pos.y;
	}

	public enum CellType {
		EMPTY, WALL, CORNER,
	}

	// convex
	public enum CornerType {
		NONE, 
		LOWER_LEFT_CONVEX, 
		LOWER_RIGHT_CONVEX, 
		UPPER_LEFT_CONVEX, 
		UPPER_RIGHT_CONVEX, 
		//there is no real use for concave corners, right?
		LOWER_LEFT_CONCAVE, 
		LOWER_RIGHT_CONCAVE, 
		UPPER_LEFT_CONCAVE, 
		UPPER_RIGHT_CONCAVE, 
		LONELY_LEFT, 
		LONELY_RIGHT, 
		LONELY_TOP, 
		LONELY_BOTTOM
	}

	public OLD_Cell(CellType type, int x, int y) {
		this.cornerType = CornerType.NONE;
		this.cellType = type;
		this.x = x;
		this.y = y;
		this.setPos(new Vector2(this.x, this.y));
	}

	public OLD_Cell(CellType type, Vector2 pos) {
		this.cornerType = CornerType.NONE;
		this.cellType = type;
		this.x = (int) pos.x;
		this.y = (int) pos.y;
		this.setPos(pos);
	}
}
