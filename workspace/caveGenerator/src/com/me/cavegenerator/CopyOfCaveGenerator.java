//package com.me.cavegenerator;
//
//import com.badlogic.gdx.ApplicationListener;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input.Keys;
//import com.badlogic.gdx.InputMultiplexer;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL10;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.math.Vector2;
//import common.GameResources;
//import common.HUD;
//
//public class CopyOfCaveGenerator implements ApplicationListener {
//	private OrthographicCamera camera;
//	private SpriteBatch batch;
//
//	private MapManager mapManager;
//
//	private HUD hud;
//
//	private int w, h;
//
//	private Color backgoundColor;
//	private int mapWidth, mapHeight;
//
//	private GameInputProcessor inputProcessor;
//	private InputMultiplexer inputMultiplexer;
//
//	@Override
//	public void create() {
//		w = Gdx.graphics.getWidth();
//		h = Gdx.graphics.getHeight();
//
//		GameResources.init();
//
//		mapWidth = w / 8;
//		mapHeight = h / 8;
//		// int tileSize = 128;
//
//		mapManager = new MapManager(mapWidth, mapHeight);
//		mapManager.generateMap();
//		mapManager.createTileMap();
//
//		inputMultiplexer = new InputMultiplexer();
//		inputMultiplexer.addProcessor(new GameInputProcessor());
//		inputMultiplexer.addProcessor(mapManager);
//		Gdx.input.setInputProcessor(inputMultiplexer);
//
//		camera = new OrthographicCamera();
//		camera.setToOrtho(true, w, h);
//		batch = new SpriteBatch();
//
//		backgoundColor = new Color(0, 0.30f, 0.50f, 1.0f);
//
//		batch.setColor(backgoundColor);
//
//		hud = new HUD(w, h);
//	}
//
//	@Override
//	public void dispose() {
//		batch.dispose();
//		hud.dispose();
//		GameResources.dispose();
//	}
//
//	@Override
//	public void render() {
//		Gdx.gl.glClearColor(backgoundColor.r, backgoundColor.g,
//				backgoundColor.b, 1);
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//
//		if (Gdx.input.isKeyPressed(Keys.SPACE)
//				|| (Gdx.input.isTouched(0) && Gdx.input.isTouched(1))) {
//			mapManager.reset(new Vector2(mapWidth / 2, 0));
//			mapManager.generateMap();
//			mapManager.createTileMap();
//		}
//
//		if (GameResources.isReady()) {
//			mapManager.render();
//			hud.draw();
//		}
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
//}
