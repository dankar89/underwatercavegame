package com.me.cavegenerator;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.me.cavegenerator.Cell.CellType;

public class Miner {
	private boolean alive;
	private Vector2 currentPos;
	private Cell currentCell;
	private Random rnd;
	private int digCounter = 0;
	private float chanceToDigVertically;
	private float chanceToDigHorizontally;
	private float maxChance = 0.75f;

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
		
		chanceToDigHorizontally = MathUtils.clamp(rnd.nextFloat(), 0, maxChance);
		chanceToDigVertically = MathUtils.clamp(rnd.nextFloat(), 0, maxChance);
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

	public boolean findWall(CaveMap caveMap, int numOfTries) {
		if (isAlive()) {
			ArrayList<Cell> adjacentCells = new ArrayList<Cell>();
			// Vector2 newPos = Vector2.Zero;
			for (int i = 0; i < numOfTries; i++) {
				if (!caveMap.getAdjacentCellsOfType(currentCell, CellType.WALL)
						.isEmpty()) {
					return true;
				}

				adjacentCells = caveMap.getAdjacentCells(currentCell);
				Cell rndCell = adjacentCells.get(rnd.nextInt(adjacentCells
						.size()));
				this.currentCell = rndCell;
			}
		}
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
				adjacentCells = caveMap.getAdjacentCellsOfType(currentPos,
						CellType.WALL);
			}

			if (adjacentCells.isEmpty()) {
				kill();
				digSucccess = false;
			} else {
				try {
					// Dig the current cell

					int rndIndex = rnd.nextInt(adjacentCells.size());					

					Vector2 rndCellPos = adjacentCells.get(rndIndex).getPos();
					caveMap.getCellAt(rndCellPos).setCellType(CellType.EMPTY);

					// move the miner to the newly dug cell
					currentPos = rndCellPos;
					digSucccess = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block

					e.printStackTrace();
				}

			}
		}

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
