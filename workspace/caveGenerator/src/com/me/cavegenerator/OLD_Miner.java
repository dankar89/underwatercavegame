//package com.me.cavegenerator;
//
//import java.util.ArrayList;
//import java.util.Random;
//
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.math.Vector2;
//import com.me.cavegenerator.Cell.CellType;
//
//public class OLD_Miner {
//	private boolean alive;
////	private Vector2 currentPos;
//	private Cell currentCell;
//	private Random rnd;
//	private int digCounter = 0;
//
////	public Vector2 getCurrentPos() {
////		return this.currentPos;
////	}
//	
//	public Cell getCurrentCell()
//	{
//		return this.currentCell;
//	}
//
//	public OLD_Miner(Vector2 startPos, CaveMap caveMap) {
//		this.alive = true;
//		this.currentCell = caveMap.getCellAt(startPos);
//		this.rnd = new Random();
//	}
//	
//	public OLD_Miner(Cell startCell, CaveMap caveMap) {
//		this.alive = true;
//		this.currentCell = startCell;
//		this.rnd = new Random();
//	}
//
//	public boolean isAlive() {
//		return alive;
//	}
//
//	public void awaken() {
//		this.alive = true;
//	}
//
//	public boolean findWall(CaveMap caveMap, int numOfTries) {
//		if (isAlive()) {
//			ArrayList<Cell> adjacentCells = new ArrayList<Cell>();
//			// Vector2 newPos = Vector2.Zero;
//			for (int i = 0; i < numOfTries; i++) {
//				if (!caveMap.getAdjacentCellsOfType(currentCell, CellType.WALL).isEmpty()) {
//					return true;
//				}
//
//				adjacentCells = caveMap.getAdjacentCells(currentCell);
//				Cell rndCell = adjacentCells.get(rnd.nextInt(adjacentCells
//						.size()));
//				this.currentCell = rndCell;
//			}
//		}
//		return false;
//	}
//
//	public CaveMap dig(CaveMap caveMap) {
//		if (this.alive) {
//			// will hold adjacent cells
//			ArrayList<Cell> adjacentCells = caveMap.getAdjacentCellsOfType(currentCell, CellType.WALL);
//
//			if (adjacentCells.isEmpty()) {
//				kill();
//				// System.out.println(digCounter);
//			} else {
//				// pick a random adjacent cell
//				int tmpIndex = rnd.nextInt(adjacentCells.size());
//				Cell rndCell = adjacentCells.get(tmpIndex);
//
//				// set the selected cell to empty
//				rndCell.setCellType(CellType.EMPTY);
//				caveMap.updateCell(rndCell);
////				caveMap.getCellAt(rndCell.getX(), rndCell.getY()).type = CellType.EMPTY;
//
//				// move miner to new pos
//				this.currentCell = rndCell;
//
//				digCounter++;
//			}
//		}
//		// System.out.println("The miner is dead!");
//
//		return caveMap;
//	}
//
//	public void kill() {
//		this.alive = false;
//	}
//	
//	public void draw(SpriteBatch batch, Texture texture)
//	{
////		batch.setColor(Color.RED);
//		batch.draw(texture, currentCell.getX() * texture.getWidth(), currentCell.getY() * texture.getHeight());
//	}
//}
