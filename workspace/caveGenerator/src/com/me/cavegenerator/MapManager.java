package com.me.cavegenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import caveGame.TileShapeData;

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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;
import com.me.cavegenerator.Cell.CellType;
import com.me.cavegenerator.Cell.WallType;
import common.GameConstants;
import common.GameResources;

public class MapManager extends InputAdapter {
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

	// private OrthographicCamera tiledMapCamera;

	// BOX2D tmp stuff
	Body body;
	PolygonShape boxShape;
	BodyDef bodyDef;

	public MapManager(int mapWidth, int mapHeight, OrthographicCamera cam) {
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

		startMiner = new Miner(new Vector2(mapWidth / 2, 0));
		miners.add(startMiner);

		this.map = new TiledMap();

		// float unitScale = 1f / (float) Globals.TILE_SIZE;
		// mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale,
		// mapBatch);

		// tiledMapCamera = cam;
		// tiledMapCamera = new OrthographicCamera();
		// tiledMapCamera.setToOrtho(true, w / GameConstants.TILE_SIZE, h
		// / GameConstants.TILE_SIZE);
		// //
		// tiledMapCamera.update();
	}

	public void generateMap(World world) {
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
		createTileMap(world);
		mapGenerationDone = true;
	}

	public void createTileMap(World world) {
		if (this.caveMap.isReady()) {
			this.map = new TiledMap();
			MapLayers layers = map.getLayers();
			AtlasRegion tmpRegion = null;
			int numOfLayers = 1;
			boolean flipX = false;
			boolean flipY = false;

			Cell cell = new Cell();
			com.me.cavegenerator.Cell caveCell;
			MyTiledMapTile mapTile = null;

			if (GameResources.isReady()) {
				ArrayList<EdgeShape> lineSegments = new ArrayList<EdgeShape>();

				ObjectMap<String, TileShapeData> shapeDataMap = GameResources.shapeDataMap;
				TileShapeData tmpTileShapeData = null;
				String textureName = "noname";

				for (int i = 0; i < numOfLayers; i++) {
					TiledMapTileLayer layer = new TiledMapTileLayer(mapWidth,
							mapHeight, GameConstants.TILE_SIZE,
							GameConstants.TILE_SIZE);

					for (int x = 0; x < mapWidth; x++) {
						for (int y = 0; y < mapHeight; y++) {
							caveCell = caveMap.getCellAt(x, y);

							// if (caveCell.getCellType() != CellType.EMPTY
							// && caveCell.getWallType() != WallType.SOLID
							// && caveCell.getWallType() != WallType.NONE) {
							// if (x >= 98 && x <= 102 && y >= 2 && y <= 7)
							// createTileBody(world, x, y);
							// }

							lineSegments.clear();

							switch (caveCell.getCellType()) {
							case EMPTY:
								mapTile = null;
								break;
							case WALL:
								if (caveCell.getWallType() == WallType.LEFT) {
									flipX = true;
									flipY = rnd.nextBoolean();
									tmpRegion = GameResources.verticalTiles
											.random();
								} else if (caveCell.getWallType() == WallType.LONELY_LEFT) {
									flipX = false;
									flipY = rnd.nextBoolean();
									tmpRegion = GameResources.lonelyVerticalTiles
											.random();
								} else if (caveCell.getWallType() == WallType.RIGHT) {
									flipX = false;
									flipY = rnd.nextBoolean();
									tmpRegion = GameResources.verticalTiles
											.random();
								} else if (caveCell.getWallType() == WallType.LONELY_RIGHT) {
									flipX = true;
									flipY = rnd.nextBoolean();
									tmpRegion = GameResources.lonelyVerticalTiles
											.random();
								} else if (caveCell.getWallType() == WallType.CEILING) {
									flipX = rnd.nextBoolean();
									flipY = false;
									tmpRegion = GameResources.horizontalTiles
											.random();
								} else if (caveCell.getWallType() == WallType.LONELY_TOP) {
									flipX = rnd.nextBoolean();
									flipY = true;
									tmpRegion = GameResources.lonelyHorizontalTiles
											.random();
								} else if (caveCell.getWallType() == WallType.GROUND) {
									flipX = rnd.nextBoolean();
									flipY = true;
									tmpRegion = GameResources.horizontalTiles
											.random();
								} else if (caveCell.getWallType() == WallType.LONELY_BOTTOM) {
									flipX = rnd.nextBoolean();
									flipY = false;
									tmpRegion = GameResources.lonelyHorizontalTiles
											.random();
								} else if (caveCell.getWallType() == WallType.LEFT_RIGHT) {
									flipX = false;
									flipY = rnd.nextBoolean();
									tmpRegion = GameResources.thinVerticalTiles
											.random();
								} else if (caveCell.getWallType() == WallType.GROUND_CEILING) {
									flipX = rnd.nextBoolean();
									flipY = false;
									tmpRegion = GameResources.thinHorizontalTiles
											.random();
								} else {
									tmpRegion = GameResources.wallRegion;
								}

								mapTile = new MyTiledMapTile(tmpRegion);

								cell = new Cell();
								cell.setFlipHorizontally(flipX);
								cell.setFlipVertically(flipY);
								cell.setTile(mapTile);
								layer.setCell(x, y, cell);

								// create box2d body for the wall
								if (tmpRegion.name != GameResources.wallRegion.name) {
									textureName = tmpRegion.name + "_"
											+ tmpRegion.index + ".png";
									createTileBody(world,
											shapeDataMap.get(textureName), x,
											y, flipX, flipY);
								}
								break;
							case CORNER_WALL:
								tmpRegion = GameResources.cornerTiles.random();

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

								// create box2d body for the corner wall
								textureName = tmpRegion.name + "_"
										+ tmpRegion.index + ".png";
								createTileBody(world,
										shapeDataMap.get(textureName), x, y,
										flipX, flipY);

								mapTile = new MyTiledMapTile(tmpRegion);

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

					float unitScale = 1f / (float) GameConstants.TILE_SIZE;
					mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale);
					mapRenderer.getSpriteBatch().setColor(tileColor);

					mapCreationDone = true;
					// tmpLineSegment.dispose();
				}
			}
		} else {
			System.out.println("The cave generation is not ready!");
		}
	}

	public void createTileBody(World world, TileShapeData tileShapeData, int x,
			int y, boolean flipX, boolean flipY) {

		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(x, y);
		Body body = world.createBody(bodyDef);

		for (int v = 0; v < tileShapeData.vertices.size() - 1; v++) {
			Vector2 v1 = tileShapeData.vertices.get(v);
			Vector2 v2 = tileShapeData.vertices.get(v + 1);

			if (flipX) {
				Vector2 tmp = v1.cpy();
				v1 = v2.cpy();
				v2 = tmp;
				v1.x = 1 - v1.x;
				v2.x = 1 - v2.x;
			}
			if (flipY) {
				Vector2 tmp = v1.cpy();
				v1 = v2.cpy();
				v2 = tmp;
				v1.y = 1 - v1.y;
				v2.y = 1 - v2.y;
			}

			EdgeShape tmpLineSegment = new EdgeShape();
			tmpLineSegment.set(v1, v2);
			body.createFixture(tmpLineSegment, 100f);
			tmpLineSegment.dispose();
		}
		body.setAwake(false);
	}

	public void render(OrthographicCamera cam) {
		if (mapCreationDone) {
			mapRenderer.setView(cam);
			mapRenderer.render();
		}
	}

	// public void update(Vector2 newCameraPos) {
	// setCameraPos(newCameraPos.x,
	// newCameraPos.y);
	// }

	// public void resize(int width, int height) {
	// tiledMapCamera.setToOrtho(true, width / GameConstants.TILE_SIZE, height
	// / GameConstants.TILE_SIZE);
	//
	// mapRenderer.setView(tiledMapCamera);
	// tiledMapCamera.update();
	// }

	// public void setCameraPos(float x, float y, OrthographicCamera cam) {
	// if (x < minCamPos.x)
	// cam.position.x = minCamPos.x;
	// else if (x > maxCamPos.x)
	// cam.position.x = maxCamPos.x;
	// else
	// cam.position.x = x;
	//
	// if (y < minCamPos.y)
	// cam.position.y = minCamPos.y;
	// else if (y > maxCamPos.y)
	// cam.position.y = maxCamPos.y;
	// else
	// cam.position.y = y;
	//
	// cam.update();
	// mapRenderer.setView(cam);
	// }

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
		// mapBatch.dispose();
		mapRenderer.dispose();
		map.dispose();
	}

	public SpriteBatch getSpriteBatch() {
		return (SpriteBatch) mapRenderer.getSpriteBatch();
	}

	// public OrthographicCamera getCamera() {
	// return tiledMapCamera;
	// }

	public CaveMap getCaveMap() {
		return caveMap;
	}

	// public float getZoom() {
	// return tiledMapCamera.zoom;
	// }

	public OrthogonalTiledMapRenderer getRenderer() {
		return mapRenderer;
	}

	public Vector2 getPlayerStartPos() {
		return playerStartPos;
	}

	// @Override
	// public boolean scrolled(int amount) {
	// tiledMapCamera.zoom = Globals.cameraZoom;
	// return false;
	// }
}
