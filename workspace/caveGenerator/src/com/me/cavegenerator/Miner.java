package com.me.cavegenerator;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.me.cavegenerator.Cell.CellType;

public class Miner {
	private boolean alive;
	private Vector2 currentPos;
	private Cell currentCell;
	private Random rnd;
	private int digCounter = 0;
	private float chanceToDigVertically;
	private float chanceToDigHorizontally;
	private float maxChanceHorizontal = 0.5f;
	private float maxChanceVertical = 0.75f;
	private Vector2 direction = Vector2.Zero;
	public boolean digSucccess;

	public Vector2 getCurrentPos() {
		return this.currentPos;
	}

	public Cell getCurrentCell() {
		return this.currentCell;
	}

	public Miner(Vector2 startPos) {
		digSucccess = false;

		this.alive = true;
		this.currentPos = startPos;
		// this.currentCell = caveMap.getCellAt(startPos);
		this.rnd = new Random();

		chanceToDigHorizontally = 0.5f;
		chanceToDigVertically = 0.5f;
		
		direction.x = rnd.nextInt(3) -1;
		direction.y = rnd.nextInt(3) -1;
	}

	public Miner(Cell startCell, CaveMap caveMap) {
		digSucccess = false;

		this.alive = true;
		this.currentCell = startCell;
		this.rnd = new Random();
	}

	public boolean isAlive() {
		return alive;
	}

	public void awaken() {
		this.alive = true;
	}

	public boolean findWall(CaveMap caveMap, int numOfTries, boolean randPos) {
//		if (isAlive()) {
			ArrayList<Cell> adjacentCells = new ArrayList<Cell>();
			// Vector2 newPos = Vector2.Zero;
			
			// loop until wall is found
			if (numOfTries == 0) {
				while (true) { // maybe add timer instead??
					if(randPos) {
						//select a random cell and see if it is empty
						int x = rnd.nextInt(caveMap.getWidth()-2) +1;
						int y = rnd.nextInt(caveMap.getHeight()-2) +1;
						
						Cell cell = caveMap.getCellAt(x, y);
						if(cell.getCellType() == CellType.EMPTY) {
							this.currentPos.set(x, y);
							return true;
						}	
					} else {
						adjacentCells = caveMap.getAdjacentCellsOfType(this.currentPos,
								CellType.WALL);
						if (!adjacentCells.isEmpty()) {
							this.currentPos = adjacentCells.get(rnd
									.nextInt(adjacentCells.size())).getPos();
							return true;
						}
						adjacentCells = caveMap.getAdjacentCells(this.currentPos);
						Cell rndCell = adjacentCells.get(rnd.nextInt(adjacentCells
								.size()));
						this.currentPos = rndCell.getPos();
					}										
				}
			} else {
				for (int i = 0; i < numOfTries; i++) {
					if (!caveMap.getAdjacentCellsOfType(this.currentPos,
							CellType.WALL).isEmpty()) {
						return true;
					}

					adjacentCells = caveMap.getAdjacentCells(this.currentPos);
					Cell rndCell = adjacentCells.get(rnd.nextInt(adjacentCells
							.size()));
					this.currentPos = rndCell.getPos();
				}
			}
//		}
		return false;
	}

	public CaveMap dig(CaveMap caveMap, boolean digDiagonally, int maxDigs) {
		if (isAlive()) {

			// dig the current cell
			if (caveMap.getCellAt(currentPos).getCellType() != CellType.EMPTY)
				caveMap.getCellAt(currentPos).setCellType(CellType.EMPTY);

			// get adjacent cells
			ArrayList<Cell> adjacentCells;
			if (digDiagonally) {
				adjacentCells = caveMap.get8AdjacentCellsOfType(currentPos,
						CellType.WALL);
			} else {
//				if(rnd.nextFloat() < chanceToDigVertically){
//					direction.x = rnd.nextInt(3) - 1;
//					direction.y = rnd.nextInt(2);
//					adjacentCells = caveMap.getAdjacentCellsOfTypeInDirection(currentPos,
//							CellType.WALL, direction);
//				} else {
//					adjacentCells = caveMap.getAdjacentCellsOfType(currentPos,
//							CellType.WALL);	
//				}
				adjacentCells = caveMap.getAdjacentCellsOfType(currentPos,
						CellType.WALL);	
//				adjacentCells = caveMap.getAdjacentCellsOfTypeInDirection(currentPos,
//						CellType.WALL, direction);
			}

			if (adjacentCells.isEmpty()) {
				kill();
				digSucccess = false;
			} else {
				try {
					// Dig the current cell
					int rndIndex = rnd.nextInt(adjacentCells.size());

					Vector2 rndCellPos = adjacentCells.get(rndIndex).getPos();
					if(caveMap.getCellAt(rndCellPos).getCellType() == CellType.LOCKED_WALL){
						System.out.println("DUG LOCKED WALL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					}
					caveMap.getCellAt(rndCellPos).setCellType(CellType.EMPTY);

					// move the miner to the newly dug cell
					currentPos = rndCellPos;
					digSucccess = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
//		float delay = 0.5f; // seconds
//
//		Timer.schedule(new Task(){
//		    @Override
//		    public void run() {
//		        // Do your work
//		    }
//		}, delay);
		return caveMap;
	}

	public void kill() {
		this.alive = false;
	}

	public void draw(SpriteBatch batch, Texture texture) {
		// batch.setColor(Color.RED);
		batch.draw(texture, currentCell.getX() * texture.getWidth(),
				currentCell.getY() * texture.getHeight());
	}
}
