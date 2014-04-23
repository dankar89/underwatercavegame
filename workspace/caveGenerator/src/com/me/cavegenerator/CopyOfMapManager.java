package com.me.cavegenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.me.cavegenerator.Cell.CellType;
import com.me.cavegenerator.Cell.WallType;

import common.GameConstants;
import common.Assets;
import common.Globals;

public class CopyOfMapManager extends InputAdapter {
	private CaveMap caveMap;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	AtlasRegion tmpRegion;
	int numOfLayers = 1;
	boolean flipX;
	boolean flipY;
	boolean mapGenerationDone;
	boolean mapCreationDone;

	private Random rnd;

	private SpriteBatch mapBatch;

	private Color tileColor;

	private ArrayList<Miner> miners = new ArrayList<Miner>();
	private ArrayList<Miner> newMiners = new ArrayList<Miner>();
	private Miner startMiner;
	private Miner currentMiner;
	private Iterator<Miner> minerIter;
	private int digCounter;
	private int createdMiners = 1;
	private int minersResetCounter;

	private int mapWidth, mapHeight;
	private Vector2 minCamPos, maxCamPos;

	private Vector2 playerStartPos;

	private OrthographicCamera tiledMapCamera;

	// BOX2D tmp stuff
	PolygonShape boxShape;
	BodyDef bodyDef;

	public CopyOfMapManager(int mapWidth, int mapHeight, Vector2 camPos) {
		rnd = new Random();

		boxShape = new PolygonShape();
		bodyDef = new BodyDef();

		flipX = false;
		flipY = false;
		mapGenerationDone = false;
		mapCreationDone = false;

		digCounter = 0;
		minersResetCounter = 0;

		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		playerStartPos = new Vector2(this.mapWidth / 2, 0);

		caveMap = new CaveMap(mapWidth, mapHeight, 0);

		tileColor = new Color(0.20f, 0.10f, 0, 1.0f);

		mapBatch = new SpriteBatch();
		mapBatch.setColor(tileColor);

		startMiner = new Miner(new Vector2(mapWidth / 2, 0));
		miners.add(startMiner);

		this.map = new TiledMap();

		float unitScale = 1f / (float) GameConstants.TILE_SIZE;
		mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale, mapBatch);

		tiledMapCamera = new OrthographicCamera();
		tiledMapCamera.setToOrtho(true, Gdx.graphics.getWidth() / GameConstants.TILE_SIZE,
				Gdx.graphics.getHeight() / GameConstants.TILE_SIZE);

		tiledMapCamera.update();
	}

	public void generateMap() {
		// TODO: prevent the minerslist from being be emptied infinitely
		minerIter = miners.iterator();
		while (createdMiners < 350) {
			while (minerIter.hasNext()) {
				currentMiner = minerIter.next();

				// If dig is successful, increment digCounter, else the dig
				// fails, remove miner
				caveMap = currentMiner.dig(caveMap, false, 0);
				if (currentMiner.digSucccess)
					digCounter++;
				else
					minerIter.remove();

				int rndInt = rnd.nextInt(100);

				if (rndInt < 4) {
					newMiners.add(new Miner(currentMiner.getCurrentPos()));
					createdMiners++;
				}

				if (miners.size() == 0) {
					minersResetCounter++;
					ArrayList<com.me.cavegenerator.Cell> adjacentCells = caveMap
							.get8AdjacentCells(currentMiner.getCurrentPos());
					Vector2 rndPos = adjacentCells.get(
							rnd.nextInt(adjacentCells.size())).getPos();
					newMiners.add(new Miner(rndPos));
				}
			}

			miners.addAll(newMiners);
			minerIter = miners.iterator();
			newMiners.clear();
		}

		caveMap.cleanUp(5);
		createTileMap();
		mapGenerationDone = true;
	}

	public void createTileMap() {
		if (this.caveMap.isReady()) {
			this.map = new TiledMap();
			MapLayers layers = map.getLayers();
			AtlasRegion tmpRegion;
			int numOfLayers = 2; //1 = background, 2 = middle, 3 = foreground
			boolean flipX = false;
			boolean flipY = false;

			Cell cell = new Cell();
			com.me.cavegenerator.Cell caveCell;
			MyTiledMapTile mapTile = null;

			if (Assets.isReady()) {
				for (int i = 0; i < numOfLayers; i++) {
					TiledMapTileLayer layer = new TiledMapTileLayer(mapWidth,
							mapHeight, 32, 32);
					for (int x = 0; x < mapWidth; x++) {
						for (int y = 0; y < mapHeight; y++) {
							caveCell = caveMap.getCellAt(x, y);

							switch (caveCell.getCellType()) {
							case EMPTY:
								mapTile = null;
								break;
							case WALL:
								if (caveCell.getWallType() == WallType.LEFT) {
									flipX = true;
									flipY = rnd.nextBoolean();
									tmpRegion = Assets.verticalTiles
											.get(rnd.nextInt(Assets.verticalTiles.size));
								} else if (caveCell.getWallType() == WallType.LONELY_LEFT) {
									flipX = false;
									flipY = rnd.nextBoolean();
									tmpRegion = Assets.lonelyVerticalTiles
											.get(rnd.nextInt(Assets.lonelyVerticalTiles.size));
								} else if (caveCell.getWallType() == WallType.RIGHT) {
									flipX = false;
									flipY = rnd.nextBoolean();
									tmpRegion = Assets.verticalTiles
											.get(rnd.nextInt(Assets.verticalTiles.size));
								} else if (caveCell.getWallType() == WallType.LONELY_RIGHT) {
									flipX = true;
									flipY = rnd.nextBoolean();
									tmpRegion = Assets.lonelyVerticalTiles
											.get(rnd.nextInt(Assets.lonelyVerticalTiles.size));
								} else if (caveCell.getWallType() == WallType.CEILING) {
									flipX = rnd.nextBoolean();
									flipY = false;
									tmpRegion = Assets.horizontalTiles
											.get(rnd.nextInt(Assets.horizontalTiles.size));
								} else if (caveCell.getWallType() == WallType.LONELY_TOP) {
									flipX = rnd.nextBoolean();
									flipY = true;
									tmpRegion = Assets.lonelyHorizontalTiles
											.get(rnd.nextInt(Assets.lonelyHorizontalTiles.size));
								} else if (caveCell.getWallType() == WallType.GROUND) {
									flipX = rnd.nextBoolean();
									flipY = true;
									tmpRegion = Assets.horizontalTiles
											.get(rnd.nextInt(Assets.horizontalTiles.size));
								} else if (caveCell.getWallType() == WallType.LONELY_BOTTOM) {
									flipX = rnd.nextBoolean();
									flipY = false;
									tmpRegion = Assets.lonelyHorizontalTiles
											.get(rnd.nextInt(Assets.lonelyHorizontalTiles.size));
								} else if (caveCell.getWallType() == WallType.LEFT_RIGHT) {
									flipX = false;
									flipY = rnd.nextBoolean();
									tmpRegion = Assets.thinVerticalTiles
											.get(rnd.nextInt(Assets.thinVerticalTiles.size));
								} else if (caveCell.getWallType() == WallType.GROUND_CEILING) {
									flipX = rnd.nextBoolean();
									flipY = false;
									tmpRegion = Assets.thinHorizontalTiles
											.get(rnd.nextInt(Assets.thinHorizontalTiles.size));
								} else {
									tmpRegion = Assets.wallRegion;
								}

								mapTile = new MyTiledMapTile(tmpRegion);

								cell = new Cell();
								cell.setFlipHorizontally(flipX);
								cell.setFlipVertically(flipY);
								cell.setTile(mapTile);
								layer.setCell(x, y, cell);
								break;
							case CORNER_WALL:
								if (caveCell.getWallType() == WallType.UPPER_LEFT_CONVEX) {
									flipX = false;
									flipY = false;
								} else if (caveCell.getWallType() == WallType.UPPER_RIGHT_CONVEX) {
									flipX = true;
									flipY = false;
								} else if (caveCell.getWallType() == WallType.LOWER_RIGHT_CONVEX) {
									flipX = true;
									flipY = true;
								} else if (caveCell.getWallType() == WallType.LOWER_LEFT_CONVEX) {
									flipX = false;
									flipY = true;
								}

								mapTile = new MyTiledMapTile(
										Assets.cornerTiles.get(rnd
												.nextInt(Assets.cornerTiles.size)));

								cell = new Cell();
								cell.setFlipHorizontally(flipX);
								cell.setFlipVertically(flipY);
								cell.setTile(mapTile);
								layer.setCell(x, y, cell);
								break;
							}
						}
					}
					layers.add(layer);

					float unitScale = 1 / (float) 32;
					mapRenderer = new OrthogonalTiledMapRenderer(map,
							unitScale, mapBatch);

					mapCreationDone = true;
				}
			}
		} else {
			System.out.println("The cave generation is not ready!");
		}
	}

	public void createBodies(World world) {
//////		Rectangle camRect = new Rectangle(tiledMapCamera.position.x
//////				- (tiledMapCamera.viewportWidth / 2), tiledMapCamera.position.y
//////				- (tiledMapCamera.viewportHeight / 2),
//////				tiledMapCamera.viewportWidth, tiledMapCamera.viewportHeight);
////		for (int x = 0; x < mapWidth; x++) {
////			for (int y = 0; y < mapHeight; y++) {
//				if (camRect.contains(x, y)) {
//					if (caveMap.getCellAt(x, y).getCellType() != CellType.EMPTY) {
						bodyDef.type = BodyType.StaticBody;
						bodyDef.position.set(15, 5);
						boxShape.setAsBox(.5f, .5f);
						world.createBody(bodyDef).createFixture(boxShape, 10f);
						boxShape.dispose();
//					}
//				}
//			}
//		}
	}

	public void render() {
		if (mapCreationDone) {
			tiledMapCamera.update();
			mapRenderer.setView(tiledMapCamera);
			mapRenderer.render();
		}
	}

	public void update(Vector2 newCameraPos) {
		tiledMapCamera.position.x = newCameraPos.x;
		tiledMapCamera.position.y = -newCameraPos.y;
		tiledMapCamera.update();
	}

	public void reset(Vector2 startPos) {
		miners.clear();
		map = null;
		caveMap = new CaveMap(mapWidth, mapHeight, 0);
		minersResetCounter = 0;
		digCounter = 0;
		createdMiners = 0;
		mapCreationDone = false;
		mapGenerationDone = false;
		startMiner = new Miner(startPos);
		miners.add(startMiner);
	}

	public void dispose() {
		mapBatch.dispose();
		map.dispose();
	}

	public SpriteBatch getSpriteBatch() {
		return (SpriteBatch) mapRenderer.getSpriteBatch();
	}

	public OrthographicCamera getCamera() {
		return tiledMapCamera;
	}

	public CaveMap getCaveMap() {
		return caveMap;
	}

	public float getZoom() {
		return tiledMapCamera.zoom;
	}

	public OrthogonalTiledMapRenderer getRenderer() {
		return mapRenderer;
	}
	
	public Vector2 getPlayerStartPos()
	{
		return playerStartPos;
	}

	@Override
	public boolean scrolled(int amount) {
		tiledMapCamera.zoom = Globals.cameraZoom;
		return false;
	}
}
