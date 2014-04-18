package com.me.cavegenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import sun.java2d.pipe.SolidTextRenderer;

import caveGame.TileShapeData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.me.cavegenerator.Cell.CellType;
import com.me.cavegenerator.Cell.WallType;
import com.sun.org.apache.xml.internal.serialize.LineSeparator;

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
	private float alpha = 1f;

	private ArrayList<Miner> miners = new ArrayList<Miner>();
	private ArrayList<Miner> newMiners = new ArrayList<Miner>();
	private Miner startMiner;
	private Miner currentMiner;
	private Iterator<Miner> minerIter;
	private int digCounter, digFailure;
	private int createdMiners = 1;
	private int minersResetCounter;
	private int minersAdded;
	private int waterLevel = 10;

	private int mapWidth, mapHeight;
	private Vector2 minCamPos, maxCamPos;

	private Vector2 playerStartPos;

	private int[] rockLayerIndex = { GameConstants.BACKGROUND_LAYER_1_INDEX };
	// GameConstants.BACKGROUND_LAYER_2_INDEX,
	// GameConstants.BACKGROUND_LAYER_3_INDEX };
	private int[] waterLayerIndex = { GameConstants.BACKGROUND_LAYER_2_INDEX };
	private int[] wallLayerIndex = { GameConstants.BACKGROUND_LAYER_3_INDEX };
	private int[] foregroundLayers = { GameConstants.FOREGROUND_LAYER_1_INDEX };

	private MapLayers layers;

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
		digFailure = 0;
		minersAdded = 0;
		minersResetCounter = 0;

		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		// playerStartPos = new Vector2(this.mapWidth / 2, 1);

		caveMap = new CaveMap(mapWidth, mapHeight, 0);

		tileColor = new Color(0.20f, 0.10f, 0, 1.0f);

		startMiner = new Miner(caveMap.getMinerStartPos());
		miners.add(startMiner);

		this.map = new TiledMap();
	}

	public void generateMap(World world) {
		// TODO: prevent the minerslist from being be emptied infinitely
		minerIter = miners.iterator();
		while (createdMiners < /* 350 */150 && digCounter < 2500) {
			while (minerIter.hasNext()) {
				currentMiner = minerIter.next();

				// If dig is successful, increment digCounter, else the dig
				// fails, remove miner
				caveMap = currentMiner.dig(caveMap, false, 0);
				if (currentMiner.digSucccess) {
					digCounter++;
				} else {
					if (miners.size() == 1) {
						System.out.println("got here");
						currentMiner.findWall(caveMap, 0, true);
					} else {
						minerIter.remove();
					}
				}
				int rndInt = rnd.nextInt(100);

				if (rndInt < 5) {
					int xDelta = 0;
					int yDelta = 0;
					if ((currentMiner.getCurrentPos().x > 0 && currentMiner
							.getCurrentPos().x < this.mapWidth - 1)
							&& (currentMiner.getCurrentPos().y > 0 && currentMiner
									.getCurrentPos().y < this.mapHeight - 1)) {
						xDelta = rnd.nextBoolean() ? 1 : -1;
						yDelta = rnd.nextBoolean() ? 1 : -1;
					}
					newMiners.add(new Miner(currentMiner.getCurrentPos().add(
							xDelta, yDelta)));
					createdMiners++;
					System.out.println("createdMiners: " + createdMiners);
				}

				System.out.println("digCounter " + digCounter);
				System.out.println("digFail " + digFailure);
				System.out.println("miners: " + miners.size());

				// if (miners.size() == 0) {
				// minersResetCounter++;
				// ArrayList<com.me.cavegenerator.Cell> adjacentCells = caveMap
				// .get8AdjacentCells(currentMiner.getCurrentPos());
				// Vector2 rndPos = adjacentCells.get(
				// rnd.nextInt(adjacentCells.size())).getPos();
				// newMiners.add(new Miner(rndPos));
				// }
			}

			miners.addAll(newMiners);
			minerIter = miners.iterator();
			newMiners.clear();
			// try {
			// Thread.sleep(250);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}

		caveMap.cleanUp(5);
		int maxWaterLevel = 30;
		int minWaterLevel = 15;
		waterLevel = rnd.nextInt((maxWaterLevel - minWaterLevel) + 1)
				+ minWaterLevel;

		// remove this!
		// waterLevel = 95;

		System.out.println("waterlevel " + waterLevel);

		createTileMap(world);
		mapGenerationDone = true;
	}

	private TiledMapTileLayer createLayer(int layerIndex) {
		return createLayer(layerIndex, null);
	}

	private TiledMapTileLayer createLayer(int layerIndex, World world) {
		TiledMapTileLayer layer = new TiledMapTileLayer(mapWidth, mapHeight,
				GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);

		String textureName = "noname";
		String type = "";
		com.me.cavegenerator.Cell caveCell;
		ObjectMap<String, TileShapeData> shapeDataMap = null;
		boolean flipX = false;
		boolean flipY = false;
		AtlasRegion tmpRegion = null;

		ArrayList<EdgeShape> lineSegments = null;

		switch (layerIndex) {
		case GameConstants.BACKGROUND_LAYER_1_INDEX:
			layer.setName("backgroundLayer1");
			break;
		case GameConstants.BACKGROUND_LAYER_2_INDEX:
			layer.setName("backgroundLayer2");
			break;
		case GameConstants.BACKGROUND_LAYER_3_INDEX:
			layer.setName("backgroundLayer3");
			shapeDataMap = GameResources.shapeDataMap;
			lineSegments = new ArrayList<EdgeShape>();
			break;
		case GameConstants.FOREGROUND_LAYER_1_INDEX:
			layer.setName("foregroundLayer1");
			break;
		}

		System.out.println("creating layer: " + layer.getName());

		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				caveCell = caveMap.getCellAt(x, y);

				if (caveCell.getWallType() != WallType.SOLID) {
					if (layerIndex == GameConstants.BACKGROUND_LAYER_1_INDEX) { // rocks
																				// background
						if (caveCell.getProperty() == "entrance") {
							tmpRegion = GameResources.tilesAtlas
									.findRegion("entrance");
							flipX = false;
							flipY = true;
							type = "entrance";
						} else {
							tmpRegion = GameResources.rockTiles.random();
							flipX = false;
							flipY = true;
						}

						layer.setCell(
								x,
								y,
								createCellforTiledMap(tmpRegion, flipX, flipY,
										type));

					} else if (layerIndex == GameConstants.BACKGROUND_LAYER_2_INDEX) {

						if (caveCell.getProperty() == "shop") {
							tmpRegion = GameResources.tilesAtlas
									.findRegion("shop");
							flipX = false;
							flipY = true;
							layer.setCell(
									x,
									y,
									createCellforTiledMap(tmpRegion, flipX,
											flipY, type));
						}

						else if (y > waterLevel) {
							type = "water";
							if (y == waterLevel + 1) {
								tmpRegion = GameResources.waterSurfaceTexture;
								flipX = rnd.nextBoolean();
								flipY = true;

							} else {
								tmpRegion = GameResources.waterTexture;
							}
							layer.setCell(
									x,
									y,
									createCellforTiledMap(tmpRegion, flipX,
											flipY, type));
						}
					} else if (layerIndex == GameConstants.FOREGROUND_LAYER_1_INDEX) { // water
																						// foreground
						if (y > waterLevel) {
							type = "foreground_object";

							if (y + 1 < mapHeight) {
								if (caveMap.getCellAt(x, y + 1).getWallType() == WallType.GROUND) {
									if (rnd.nextFloat() > 0.7f) {
										tmpRegion = GameResources.stuffInTheWater
												.random();
										flipX = rnd.nextBoolean();
										flipY = true;

										layer.setCell(
												x,
												y,
												createCellforTiledMap(
														tmpRegion, flipX,
														flipY, type));

										// TODO: this is just an ugly fix for
										// ground tiles that are not flat
										layer.setCell(
												x,
												y + 1,
												createCellforTiledMap(
														tmpRegion, flipX,
														false, type));
									}
								}
							}
						}
					}
				}

				if (caveCell.getCellType() == CellType.WALL
						|| caveCell.getCellType() == CellType.CORNER_WALL) {
					if (layerIndex == GameConstants.BACKGROUND_LAYER_3_INDEX
							&& world != null) { // walls
						lineSegments.clear();

						if (caveCell.getWallType() == WallType.LEFT) {
							flipX = true;
							flipY = rnd.nextBoolean();
							tmpRegion = GameResources.verticalTiles.random();
						} else if (caveCell.getWallType() == WallType.LONELY_LEFT) {
							flipX = false;
							flipY = rnd.nextBoolean();
							tmpRegion = GameResources.lonelyVerticalTiles
									.random();
						} else if (caveCell.getWallType() == WallType.RIGHT) {
							flipX = false;
							flipY = rnd.nextBoolean();
							tmpRegion = GameResources.verticalTiles.random();
						} else if (caveCell.getWallType() == WallType.LONELY_RIGHT) {
							flipX = true;
							flipY = rnd.nextBoolean();
							tmpRegion = GameResources.lonelyVerticalTiles
									.random();
						} else if (caveCell.getWallType() == WallType.CEILING) {
							flipX = rnd.nextBoolean();
							flipY = false;
							tmpRegion = GameResources.horizontalTiles.random();
						} else if (caveCell.getWallType() == WallType.LONELY_TOP) {
							flipX = rnd.nextBoolean();
							flipY = true;
							tmpRegion = GameResources.lonelyHorizontalTiles
									.random();
						} else if (caveCell.getWallType() == WallType.GROUND) {
							flipX = rnd.nextBoolean();
							flipY = true;
							if (y > waterLevel) {
								tmpRegion = GameResources.horizontalTiles
										.random();
							} else {
								tmpRegion = GameResources.horizontalTiles
										.get(0);
							}
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
						} else if (caveCell.getWallType() == WallType.SOLID) {
							tmpRegion = GameResources.wallRegion;
						} else { // corner tiles
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
						}

						// create box2d body for the wall
						if (caveCell.getWallType() != WallType.SOLID) {
							textureName = tmpRegion.name + "_"
									+ tmpRegion.index + ".png";
							createTileBody(world,
									shapeDataMap.get(textureName), x, y, flipX,
									flipY);
						}

						layer.setCell(
								x,
								y,
								createCellforTiledMap(tmpRegion, flipX, flipY,
										"wall"));
					}
				}
			}
		}

		return layer;
	}

	public void createTileMap(World world) {
		if (this.caveMap.isReady()) {
			this.map = new TiledMap();
			layers = map.getLayers();

			if (GameResources.isReady()) {
				layers.add(createLayer(GameConstants.BACKGROUND_LAYER_1_INDEX));
				layers.add(createLayer(GameConstants.BACKGROUND_LAYER_2_INDEX));
				layers.add(createLayer(GameConstants.BACKGROUND_LAYER_3_INDEX,
						world));
				layers.add(createLayer(GameConstants.FOREGROUND_LAYER_1_INDEX));

				float unitScale = 1f / (float) GameConstants.TILE_SIZE;
				mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale);
				mapRenderer.getSpriteBatch().enableBlending();
				mapCreationDone = true;
			}
		} else {
			System.out.println("The cave generation is not ready!");
		}
	}

	private Cell createCellforTiledMap(AtlasRegion texture, boolean flipX,
			boolean flipY, String type) {
		MyTiledMapTile mapTile = new MyTiledMapTile(texture);
		mapTile.getProperties().put("type", type);
		Cell cell = new Cell();
		cell.setFlipHorizontally(flipX);
		cell.setFlipVertically(flipY);
		cell.setTile(mapTile);

		return cell;
	}

	public Object getTileProperty(int x, int y, String prop, int layerIndex) {
		TiledMapTileLayer layer = (TiledMapTileLayer) this.layers
				.get(layerIndex);
		if (layer.getCell(x, y) != null
				&& layer.getCell(x, y).getTile() != null) {
			TiledMapTile tile = layer.getCell(x, y).getTile();
			MapProperties props = tile.getProperties();
			if (props != null) {
				if (props.containsKey(prop)) {
					return props.get(prop);
				} else {
					System.out.println("Tile [" + x + "," + y
							+ "] has no property named '" + prop + "'.");
				}
			}
		}
		return null;
	}

	// TODO: create one BIG body and just attach shapes to it
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
			body.createFixture(tmpLineSegment, 20f);
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

	public void renderBackgroundlayers(OrthographicCamera cam) {
		if (mapCreationDone) {
			mapRenderer.setView(cam);

			// render rock background
			mapRenderer.getSpriteBatch().setColor(1, 1, 1, 1);
			// mapRenderer.getSpriteBatch().enableBlending();
			mapRenderer.render(rockLayerIndex);

			// render water background...
			mapRenderer.getSpriteBatch().setColor(1, 1, 1, 0.7f);
			mapRenderer.render(waterLayerIndex);

			mapRenderer.getSpriteBatch().setColor(1, 1, 1, 1);
			// mapRenderer.getSpriteBatch().enableBlending();
			mapRenderer.render(wallLayerIndex);
		}
	}

	public void renderForegroundlayers(OrthographicCamera cam) {
		if (mapCreationDone) {
			mapRenderer.setView(cam);

			// render water foreground...
			mapRenderer.getSpriteBatch().setColor(1, 1, 1, 0.4f);
			mapRenderer.render(waterLayerIndex);

			mapRenderer.getSpriteBatch().setColor(1, 1, 1, 1);
			mapRenderer.render(foregroundLayers);
			mapRenderer.render(wallLayerIndex);
			// mapRenderer.render(foregroundLayers);
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

	public int getWaterLevel() {
		return this.waterLevel;
	}

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
