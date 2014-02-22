//package com.me.cavegenerator;
//
//import java.util.ArrayList;
//import java.util.Random;
//
//import com.badlogic.gdx.ApplicationListener;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input.Keys;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL10;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.Sprite;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.maps.MapLayers;
//import com.badlogic.gdx.maps.tiled.TiledMap;
//import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
//import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
//import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
//import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
//import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.utils.Array;
//import com.me.cavegenerator.Cell.CellType;
//import com.me.cavegenerator.Cell.CornerType;
//
//public class OLD_CaveGenerator implements ApplicationListener {
//	private OrthographicCamera camera;
//	private OrthographicCamera tiledMapCamera;
//	private SpriteBatch batch;
//	private SpriteBatch hudBatch;
//	private SpriteBatch mapBatch;
//	private Texture texture;
//	private Sprite sprite;
//	private BitmapFont font;
//	private Random rnd;
//	private TiledMap map;
//	private TiledMapRenderer mapRenderer;
//	Texture tiles;
//	TextureRegion[][] splitTiles;
//	Array<AtlasRegion> cornerTiles = new Array<TextureAtlas.AtlasRegion>();
//	AtlasRegion wallRegion;
//	TextureAtlas tilesAtlas;
//
//	private CaveMap caveMap;
//	private ArrayList<Miner> miners = new ArrayList<Miner>();
//	private Miner startMiner;
//	private com.me.cavegenerator.Cell startCell;
//	private int w, h;
//	private boolean isDone = false;
//	private boolean tileMapCreated = false;
//
//	private int mapWidth, mapHeight;
//
//	@Override
//	public void create() {
//		w = Gdx.graphics.getWidth();
//		h = Gdx.graphics.getHeight();
//
//		mapWidth = w / 8;
//		mapHeight = h / 8;
//
//		camera = new OrthographicCamera();
//		camera.setToOrtho(true, w, h);
//		batch = new SpriteBatch();
//		hudBatch = new SpriteBatch();
//
//		tiledMapCamera = new OrthographicCamera();
//		tiledMapCamera.setToOrtho(true, w / 32, h / 32);
//		tiledMapCamera.update();
//		tiledMapCamera.zoom = 5.0f;
//		mapBatch = new SpriteBatch();
//		mapBatch.setColor(Color.YELLOW);
//
//		font = new BitmapFont();
//		font.setColor(Color.YELLOW);
//		rnd = new Random(1234567890);
//
//		caveMap = new CaveMap(mapWidth, mapHeight);
//		// caveMap = new CaveMap((int) w / 8, (int) h / 8);
//
//		// create the first miner
//		// com.me.cavegenerator.Cell startCell = caveMap.getCellAt(
//		// caveMap.getWidth() / 2, 0);
//		reset(new Vector2(mapWidth / 2, 0));
//
//		texture = new Texture(Gdx.files.internal("data/whitesquare.png"));
//
//		// {
//		// tiles = new Texture(Gdx.files.internal("textures/tiles.png"));
//		// splitTiles = TextureRegion.split(tiles, 32, 32);
//
//		tilesAtlas = new TextureAtlas(Gdx.files.internal("textures/tiles.txt"));
//		cornerTiles = tilesAtlas.findRegions("corner");
//		wallRegion = tilesAtlas.findRegion("empty");
//		// map = new TiledMap();
//		// MapLayers layers = map.getLayers();
//		// int numOfLayers = 1;
//		// for (int i = 0; i < numOfLayers; i++) {
//		// TiledMapTileLayer layer = new TiledMapTileLayer(150, 100, 32, 32);
//		// // for (int x = 0; x < 150; x++) {
//		// // for (int y = 0; y < 100; y++) {
//		// // int ty = (int)(Math.random() * splitTiles.length);
//		// // int tx = (int)(Math.random() * splitTiles[ty].length);
//		// // Cell cell = new Cell();
//		// // cell.setTile(new StaticTiledMapTile(splitTiles[ty][tx]));
//		// // layer.setCell(x, y, cell);
//		// // }
//		// // }
//		// layers.add(layer);
//		// }
//		// }
//
//		// texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//
//		// TextureRegion region = new TextureRegion(texture, 64, 64);
//		//
//		// sprite = new Sprite(region);
//		// // sprite.setSize(0.9f, 0.9f * sprite.getHeight() /
//		// sprite.getWidth());
//		// sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
//		// sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
//
//	}
//
//	public void createTileMap() {
//		if (this.caveMap.isReady()) {
//			this.map = new TiledMap();
//			MapLayers layers = map.getLayers();
//			int numOfLayers = 1;
//			int cellRotation = 0;
//
//			Cell cell = new Cell();
//			com.me.cavegenerator.Cell caveCell;
//			StaticTiledMapTile mapTile = null;
//			for (int i = 0; i < numOfLayers; i++) {
//				TiledMapTileLayer layer = new TiledMapTileLayer(mapWidth,
//						mapHeight, 32, 32);
//				for (int x = 0; x < mapWidth; x++) {
//					for (int y = 0; y < mapHeight; y++) {
//						caveCell = caveMap.getCellAt(x, y);
//						if (caveCell.getCellType() == CellType.WALL) {
//							mapTile = new StaticTiledMapTile(wallRegion);
//						} else if (caveCell.getCellType() == CellType.CORNER) {
//							if (caveCell.getCornerType() == CornerType.UPPER_LEFT_CONVEX) {
//								cellRotation = 90;
//							} else if (caveCell.getCornerType() == CornerType.UPPER_RIGHT_CONVEX) {
//								cellRotation = 180;
//							} else if (caveCell.getCornerType() == CornerType.LOWER_RIGHT_CONVEX) {
//								cellRotation = 270;
//							} else if (caveCell.getCornerType() == CornerType.LOWER_LEFT_CONVEX) {
//								cellRotation = 0;
//							}
//
//							mapTile = new StaticTiledMapTile(
//									cornerTiles.get(rnd
//											.nextInt(cornerTiles.size)));
//
//						}
//
//						if (mapTile != null) {
//							cell.setRotation(cellRotation);
//							cell.setTile(mapTile);
//							layer.setCell(x, y, cell);
//						}
//					}
//				}
//				layers.add(layer);
//
//				float unitScale = 1 / (float) 32;
//				mapRenderer = new OrthogonalTiledMapRenderer(map, unitScale, mapBatch);
//				tiledMapCamera.position.x = (mapWidth / 2);
//				tiledMapCamera.position.y = (mapHeight / 2);
//				System.out.println(layer.getTileHeight());
//				isDone = true;
//			}
//		} else {
//			System.out.println("The cave generation is not ready!");
//		}
//	}
//
//	@Override
//	public void dispose() {
//		batch.dispose();
//		hudBatch.dispose();
//		texture.dispose();
//		mapBatch.dispose();
//		// TOOD: dont forget to dispose stuff!
//	}
//
//	@Override
//	public void render() {
//		Gdx.gl.glClearColor(1, 1, 1, 1);
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//
//		// if (Gdx.input.isTouched()) {
//		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
//			reset(new Vector2(mapWidth / 2, 0));
//		}
//		if (miners.size() < (w / 2)) {
//			int aliveMiners = 1;
//
//			if (aliveMiners > 0) {
//				batch.setColor(Color.RED);
//				batch.begin();
//
//				for (Miner miner : miners) {
//					if (miner.isAlive()) {
//						aliveMiners++;
//						caveMap = miner.dig(caveMap);
//
//						if (rnd.nextInt(100) < 15) {
//							miners.add(new Miner(miner.getCurrentCell(),
//									caveMap));
//							break;
//						}
//
////						miner.draw(batch, texture);
//					}
//
//					// TODO: THIS DOES NOT WORK!
////					if (aliveMiners == 1) {
////						if (miner.findWall(caveMap, 50))
////							caveMap = miner.dig(caveMap);
////					}
//
//					if (!miner.isAlive() && aliveMiners > 1) {
//						aliveMiners--;
//					}
//				}
//				batch.end();
//			}
//		} else // Done diggin, now its time to clean up the map
//		{
//			if (!isDone) {
//				caveMap.cleanUp();
////				createTileMap();
//			}
//		}
//
//		// try {
//		// Thread.sleep(100);
//		// } catch (InterruptedException e) {
//		// // TODO Auto-generated catch block
//		// e.printStackTrace();
//		// }
//
//		batch.setProjectionMatrix(camera.combined);
//		batch.setColor(Color.BLACK);
//		batch.begin();
//		// caveMap.drawCells(batch, texture, CellType.WALL);
//		caveMap.draw(batch, texture);
//		batch.end();
//
//		if (Gdx.input.isKeyPressed(Keys.C)) {
//			batch.setColor(Color.GREEN);
//		} else {
//			batch.setColor(Color.BLACK);
//		}
//
//		if (Gdx.input.isTouched()) {
//			Vector2 clickedCell = new Vector2(Gdx.input.getX() / 8,
//					Gdx.input.getY() / 8);
//			if (clickedCell.x >= 0 && clickedCell.x < mapWidth) {
//				if (clickedCell.y >= 0 && clickedCell.y < mapHeight) {
//					System.out.println(caveMap.getCellAt(clickedCell)
//							.getCornerType().toString()
//							+ ": " + clickedCell.toString());
//				}
//			}
//		}
//
//		batch.begin();
//		caveMap.drawCells(batch, texture, CellType.CORNER);
////		batch.draw(cornerTiles.get(0), 500, 10);
//		batch.end();
//
////		if (isDone) {
////			tiledMapCamera.update();
////			mapRenderer.setView(tiledMapCamera);
////			mapRenderer.render();
////			
////		}
//
//		hudBatch.begin();
//		font.draw(hudBatch,
//				Integer.toString(Gdx.graphics.getFramesPerSecond()), 20, 20);
////		font.draw(hudBatch, Integer.toString(miners.size()), 20, 40);
//		hudBatch.end();
//	}
//
//	@Override
//	public void resize(int width, int height) {
//	}
//
//	@Override
//	public void pause() {
//	}
//
//	@Override
//	public void resume() {
//	}
//
//	private void reset(Vector2 startPos) {
//		miners.clear();
//		caveMap = new CaveMap(mapWidth, mapHeight);
//		
//		// startPos = new Vector2(caveMap.getWidth() / 2,
//		// 0);
//		startMiner = new Miner(startPos, caveMap);
//		miners.add(startMiner);
//	}
//}
