package com.me.cavegenerator;

import java.util.ArrayList;
import java.util.Iterator;

import caveGame.TileShapeData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
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
import com.me.cavegenerator.Cell.CellType;
import com.me.cavegenerator.Cell.WallType;
import common.Assets;
import common.GameConstants;
import common.Globals;

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

	private ArrayList<Miner> miners = new ArrayList<Miner>();
	private ArrayList<Miner> newMiners = new ArrayList<Miner>();
	private Miner startMiner;
	private Miner currentMiner;
	private Iterator<Miner> minerIter;
	private int digCounter, digFailure;
	private int createdMiners = 1;
	private int waterLevel = 10;

	private int mapWidth, mapHeight;

	private Vector2 playerStartPos;

	private int[] backgroundLayers = { GameConstants.BACKGROUND_LAYER_1_INDEX,
			GameConstants.BACKGROUND_LAYER_2_INDEX,
			GameConstants.BACKGROUND_LAYER_3_INDEX };
	private int[] foregroundLayers = { GameConstants.FOREGROUND_LAYER_1_INDEX,
			GameConstants.FOREGROUND_LAYER_2_INDEX,
			GameConstants.FOREGROUND_LAYER_3_INDEX,
			GameConstants.FOREGROUND_LAYER_4_INDEX };

	private MapLayers layers;

	// BOX2D tmp stuff
	Body body;
	PolygonShape boxShape;
	BodyDef bodyDef;

	public MapManager(int mapWidth, int mapHeight, OrthographicCamera cam) {
		boxShape = new PolygonShape();
		bodyDef = new BodyDef();

		flipX = false;
		flipY = false;
		mapGenerationDone = false;
		mapCreationDone = false;

		digCounter = 0;
		digFailure = 0;

		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		caveMap = new CaveMap(mapWidth, mapHeight, 0);

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
						// System.out.println("got here");
						currentMiner.findWall(caveMap, 0, true);
					} else {
						minerIter.remove();
					}
				}
				int rndInt = Globals.random.nextInt(100);

				if (rndInt < 5) {
					int xDelta = 0;
					int yDelta = 0;
					if ((currentMiner.getCurrentPos().x > 0 && currentMiner
							.getCurrentPos().x < this.mapWidth - 1)
							&& (currentMiner.getCurrentPos().y > 0 && currentMiner
									.getCurrentPos().y < this.mapHeight - 1)) {
						xDelta = Globals.random.nextBoolean() ? 1 : -1;
						yDelta = Globals.random.nextBoolean() ? 1 : -1;
					}
					newMiners.add(new Miner(currentMiner.getCurrentPos().add(
							xDelta, yDelta)));
					createdMiners++;
				}
			}

			miners.addAll(newMiners);
			minerIter = miners.iterator();
			newMiners.clear();
		}

		caveMap.cleanUp(5);
		int maxWaterLevel = 40;
		int minWaterLevel = 15;
		waterLevel = Globals.random
				.nextInt((maxWaterLevel - minWaterLevel) + 1) + minWaterLevel;

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
			break;
		case GameConstants.FOREGROUND_LAYER_1_INDEX:
			layer.setName("foregroundLayer1");
			break;
		case GameConstants.FOREGROUND_LAYER_2_INDEX:
			layer.setName("foregroundLayer2");
			break;
		case GameConstants.FOREGROUND_LAYER_3_INDEX:
			layer.setName("foregroundLayer3");
			break;
		case GameConstants.FOREGROUND_LAYER_4_INDEX:
			layer.setName("foregroundLayer4");
			shapeDataMap = Assets.shapeDataMap;
			lineSegments = new ArrayList<EdgeShape>();
			break;
		}

		// System.out.println("creating layer: " + layer.getName());

		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				caveCell = caveMap.getCellAt(x, y);

				if (caveCell.getWallType() != WallType.SOLID) {
					if (layerIndex == GameConstants.BACKGROUND_LAYER_1_INDEX) { // rocks
																				// background
						if (caveCell.getProperty() == "entrance") {
							tmpRegion = Assets.tilesAtlas
									.findRegion("entrance");
							flipX = false;
							flipY = true;
							type = "entrance";
						} else {
							tmpRegion = Assets.rockTiles.get(Globals.random
									.nextInt(Assets.rockTiles.size));
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
							tmpRegion = Assets.tilesAtlas.findRegion("shop");
							flipX = false;
							flipY = true;
							layer.setCell(
									x,
									y,
									createCellforTiledMap(tmpRegion, flipX,
											flipY, type));
						}
					} else if (layerIndex == GameConstants.BACKGROUND_LAYER_3_INDEX) {

					} else if (layerIndex == GameConstants.FOREGROUND_LAYER_1_INDEX) {

					} else if (layerIndex == GameConstants.FOREGROUND_LAYER_2_INDEX) { // water
																						// overlay

						if (y > waterLevel) {
							type = "water";
							if (y == waterLevel + 1) {
								tmpRegion = Assets.waterSurfaceTexture;
								flipX = Globals.random.nextBoolean();
								flipY = true;

							} else {
								tmpRegion = Assets.waterTexture;
							}
							layer.setCell(
									x,
									y,
									createCellforTiledMap(tmpRegion, flipX,
											flipY, type));
						}
					} else if (layerIndex == GameConstants.FOREGROUND_LAYER_3_INDEX) { // water
																						// foreground
						if (y > waterLevel) {
							type = "foreground_object";

							if (y + 1 < mapHeight) {
								if (caveMap.getCellAt(x, y + 1).getWallType() == WallType.GROUND) {
									float rndFloat = Globals.random.nextFloat();
									if (rndFloat > 0.7f) {
										tmpRegion = Assets.stuffInTheWater
												.get(Globals
														.random.nextInt(Assets.stuffInTheWater.size));
										flipX = Globals.random.nextBoolean();
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
					if (layerIndex == GameConstants.FOREGROUND_LAYER_4_INDEX
							&& world != null) { // walls
						lineSegments.clear();

						if (caveCell.getWallType() == WallType.LEFT) {
							flipX = true;
							flipY = Globals.random.nextBoolean();
							tmpRegion = Assets.verticalTiles.get(Globals
									.random.nextInt(Assets.verticalTiles.size));
						} else if (caveCell.getWallType() == WallType.LONELY_LEFT) {
							flipX = false;
							flipY = Globals.random.nextBoolean();
							tmpRegion = Assets.lonelyVerticalTiles
									.get(Globals
											.random.nextInt(Assets.lonelyVerticalTiles.size));
						} else if (caveCell.getWallType() == WallType.RIGHT) {
							flipX = false;
							flipY = Globals.random.nextBoolean();
							tmpRegion = Assets.verticalTiles.get(Globals
									.random.nextInt(Assets.verticalTiles.size));
						} else if (caveCell.getWallType() == WallType.LONELY_RIGHT) {
							flipX = true;
							flipY = Globals.random.nextBoolean();
							tmpRegion = Assets.lonelyVerticalTiles
									.get(Globals
											.random.nextInt(Assets.lonelyVerticalTiles.size));
						} else if (caveCell.getWallType() == WallType.CEILING) {
							flipX = Globals.random.nextBoolean();
							flipY = false;
							tmpRegion = Assets.horizontalTiles.get(Globals
									.random.nextInt(Assets.horizontalTiles.size));
						} else if (caveCell.getWallType() == WallType.LONELY_TOP) {
							flipX = Globals.random.nextBoolean();
							flipY = true;
							tmpRegion = Assets.lonelyHorizontalTiles
									.get(Globals
											.random.nextInt(Assets.lonelyHorizontalTiles.size));
						} else if (caveCell.getWallType() == WallType.GROUND) {
							flipX = Globals.random.nextBoolean();
							flipY = true;
							if (y > waterLevel) {
								tmpRegion = Assets.horizontalTiles
										.get(Globals
												.random.nextInt(Assets.horizontalTiles.size));
							} else {
								tmpRegion = Assets.horizontalTiles.get(0);
							}
						} else if (caveCell.getWallType() == WallType.LONELY_BOTTOM) {
							flipX = Globals.random.nextBoolean();
							flipY = false;
							tmpRegion = Assets.lonelyHorizontalTiles
									.get(Globals
											.random.nextInt(Assets.lonelyHorizontalTiles.size));
						} else if (caveCell.getWallType() == WallType.LEFT_RIGHT) {
							flipX = false;
							flipY = Globals.random.nextBoolean();
							tmpRegion = Assets.thinVerticalTiles.get(Globals
									.random.nextInt(Assets.thinVerticalTiles.size));
						} else if (caveCell.getWallType() == WallType.GROUND_CEILING) {
							flipX = Globals.random.nextBoolean();
							flipY = false;
							tmpRegion = Assets.thinHorizontalTiles
									.get(Globals
											.random.nextInt(Assets.thinHorizontalTiles.size));
						} else if (caveCell.getWallType() == WallType.SOLID) {
							tmpRegion = Assets.wallRegion;
						} else { // corner tiles
							tmpRegion = Assets.cornerTiles.get(Globals
									.random.nextInt(Assets.cornerTiles.size));

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
							System.out.println("[" + x + "][" + y
									+ " 	   texture name: " + textureName);
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

			if (Assets.isReady()) {
				layers.add(createLayer(GameConstants.BACKGROUND_LAYER_1_INDEX));
				layers.add(createLayer(GameConstants.BACKGROUND_LAYER_2_INDEX));
				layers.add(createLayer(GameConstants.BACKGROUND_LAYER_3_INDEX));
				layers.add(createLayer(GameConstants.FOREGROUND_LAYER_1_INDEX));
				layers.add(createLayer(GameConstants.FOREGROUND_LAYER_2_INDEX));
				layers.add(createLayer(GameConstants.FOREGROUND_LAYER_3_INDEX));
				layers.add(createLayer(GameConstants.FOREGROUND_LAYER_4_INDEX,
						world));

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
					// System.out.println("Tile [" + x + "," + y
					// + "] has no property named '" + prop + "'.");
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

	public void renderBackgroundlayers(OrthographicCamera cam) {
		if (mapCreationDone) {
			mapRenderer.setView(cam);

			render(backgroundLayers);
		}
	}

	public void renderForegroundlayers(OrthographicCamera cam) {
		if (mapCreationDone) {
			mapRenderer.setView(cam);

			render(foregroundLayers);
		}
	}

	private void render(int[] layers) {
		mapRenderer.getSpriteBatch().begin();
		for (int layerIdx : layers) {
			MapLayer layer = map.getLayers().get(layerIdx);
			if (layer.isVisible()) {
				if (layer instanceof TiledMapTileLayer) {
					// if water layer
					if (layerIdx == GameConstants.FOREGROUND_LAYER_2_INDEX) {
						mapRenderer.getSpriteBatch().setColor(1, 1, 1, 0.6f);
					} else {
						mapRenderer.getSpriteBatch().setColor(1, 1, 1, 1);
					}
					mapRenderer.renderTileLayer((TiledMapTileLayer) layer);
				} else {
					for (MapObject object : layer.getObjects()) {
						mapRenderer.renderObject(object);
					}
				}
			}
		}
		mapRenderer.getSpriteBatch().end();
	}

	public void reset(Vector2 startPos) {
		miners.clear();
		map = null;
		caveMap = new CaveMap(mapWidth, mapHeight, 0);
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
}
