package multiplayer;

import java.util.ArrayList;
import java.util.HashMap;

import kryonet.NetworkPlayer;

import org.json.JSONObject;

import box2dLight.RayHandler;

import caveGame.CaveGame;
import caveGame.GameInputProcessor;
import caveGame.MainMenuScreen;
import caveGame.PhysicsManager;
import caveGame.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.me.cavegenerator.MapManager;
import common.Assets;
import common.GameConstants;
import common.Globals;
import common.HUD;

public class MultiplayerGameScreen implements Screen {

	public static enum GameState {
		GAME_READY, GAME_RUNNING, GAME_PAUSED, GAME_LEVEL_END, GAME_OVER
	}

	private GameState state;

	private CaveGame game;

	private static OrthographicCamera camera;
	private static SpriteBatch batch;

	private MapManager mapManager;

	private HUD hud;

	private Player player;
	private ArrayList<RemotePlayer> networkPlayers = new ArrayList<RemotePlayer>();
	// private RemotePlayer remotePlayer1;

	private Vector2 minCamPos, maxCamPos, camPos, mouseWorldPos;

	private int w, h;
	private int mapWidth, mapHeight;

	private InputMultiplexer inputMultiplexer;

	PhysicsManager physics;

	private ShapeRenderer shapeRenderer;

	private RayHandler rayHandler;

	public MultiplayerGameScreen(CaveGame game) {
		this.game = game;

		Globals.isCurrentGameMultiplayer = true;
		state = GameState.GAME_READY;

		shapeRenderer = new ShapeRenderer();

		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		mapWidth = 100;
		mapHeight = 100;

		String waterLevelString = NetworkData.roomProperties.get("waterLevel");
		System.out.println(waterLevelString);
		int maxWaterLevel = GameConstants.WATER_LEVEL_MEDIUM_MAX;
		int minWaterLevel = GameConstants.WATER_LEVEL_MEDIUM_MIN;
		if (waterLevelString.equals("High")) {
			maxWaterLevel = GameConstants.WATER_LEVEL_HIGH_MAX;
			minWaterLevel = GameConstants.WATER_LEVEL_HIGH_MIN;
		} else if (waterLevelString.equals("Medium")) {
			maxWaterLevel = GameConstants.WATER_LEVEL_MEDIUM_MAX;
			minWaterLevel = GameConstants.WATER_LEVEL_MEDIUM_MIN;
		} else if (waterLevelString.equals("Low")) {
			maxWaterLevel = GameConstants.WATER_LEVEL_LOW_MAX;
			minWaterLevel = GameConstants.WATER_LEVEL_LOW_MIN;
		}

//		int waterLevel = Globals.random
//				.nextInt((minWaterLevel - maxWaterLevel) + 1) + maxWaterLevel;

		camera = new OrthographicCamera();
		camera.setToOrtho(true, w / GameConstants.TILE_SIZE, h
				/ GameConstants.TILE_SIZE);

		physics = new PhysicsManager();

		setupLighting();

		camPos = new Vector2(mapWidth / 2, mapHeight / 2);

		batch = new SpriteBatch();

		mapManager = new MapManager(mapWidth, mapHeight, camera);
		mapManager.generateMap(physics.getWorld());

		minCamPos = new Vector2(w / (GameConstants.TILE_SIZE * 2), h
				/ (GameConstants.TILE_SIZE * 2));
		maxCamPos = new Vector2(mapWidth - minCamPos.x, mapWidth - minCamPos.y);

		player = new Player(physics.getWorld(), rayHandler, new Vector2(
				((mapWidth / 2) - 3) + .5f, 1.5f));

		for (NetworkPlayer p : NetworkData.players.values()) {
			networkPlayers.add(new RemotePlayer(physics.getWorld(), rayHandler,
					new Vector2(((mapWidth / 2) - 3) + .5f, 1.5f)));
		}

		hud = new HUD(w, h);

		batch = new SpriteBatch();

		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(hud.getStage());
		inputMultiplexer.addProcessor(player);
		inputMultiplexer.addProcessor(mapManager);
		inputMultiplexer.addProcessor(new GameInputProcessor());
		Gdx.input.setInputProcessor(inputMultiplexer);

		createCrosshair();
	}

	private void setupLighting() {
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		rayHandler = new RayHandler(physics.getWorld());
		rayHandler.getLightMapTexture().setFilter(TextureFilter.Nearest,
				TextureFilter.Nearest);
		rayHandler.setBlur(false);
		rayHandler.setCulling(true);

		rayHandler.setCombinedMatrix(camera.combined);
	}

	private void createCrosshair() {
		Pixmap.setFilter(Filter.NearestNeighbour);
		Pixmap pixmap = new Pixmap(
				Gdx.files.internal("textures/crosshair2.png"));
		Gdx.input.setCursorImage(pixmap, pixmap.getWidth() / 2,
				pixmap.getHeight() / 2);
	}

	public void update(float deltaTime) {
		switch (state) {
		case GAME_READY:
			updateReady();
			break;
		case GAME_RUNNING:
			updateRunning(deltaTime);
			break;
		case GAME_PAUSED:
			updatePaused();
			break;
		case GAME_LEVEL_END:
			updateLevelEnd();
			break;
		case GAME_OVER:
			updateGameOver();
			break;
		}
	}

	private void updateReady() {
		if (Assets.isReady()) {
			state = GameState.GAME_RUNNING;
		}
	}

	private void updateRunning(float deltaTime) {

		mouseWorldPos = new Vector2(
				(camera.position.x - ((Gdx.graphics.getWidth() / 2) / (float) GameConstants.TILE_SIZE))
						+ (Gdx.input.getX() / (float) GameConstants.TILE_SIZE),
				(camera.position.y - ((Gdx.graphics.getHeight() / 2) / (float) GameConstants.TILE_SIZE))
						+ (Gdx.input.getY() / (float) GameConstants.TILE_SIZE));

		player.update(0, mouseWorldPos, mapManager.getWaterLevel());

		for (RemotePlayer p : networkPlayers) {
			p.update(mapManager.getWaterLevel());
		}

		if (Gdx.input.isKeyPressed(Keys.C)) {
			camera.zoom -= 0.02f;
		}

		if (Gdx.input.isKeyPressed(Keys.X)) {
			camera.zoom += 0.02f;
		}

		hud.update(Gdx.graphics.getDeltaTime(), player);

		// Should this be done after rendering?
		physics.update(1 / 60f, camera);

	}

	private void updatePaused() {

	}

	private void updateLevelEnd() {

		game.setScreen(new MainMenuScreen(game));
	}

	private void updateGameOver() {
		game.setScreen(new MainMenuScreen(game));
	}

	public void draw() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		setCameraPos(player.getPos().x, player.getPos().y);
		mapManager.renderBackgroundlayers(camera);

		batch.begin();
		for (RemotePlayer p : networkPlayers) {
			p.draw(batch);
		}
		player.draw(batch);
		batch.end();

		mapManager.renderForegroundlayers(camera);

		if (Globals.lightsEnabled) {
			rayHandler.updateAndRender();
		}

		if (!Globals.hideUI) {
			hud.draw(player);

			Vector2[] posArray = new Vector2[3];
			int idx = 0;
			for (RemotePlayer p : networkPlayers) {
				posArray[idx] = p.getPos();
				idx++;
			}

			hud.drawMiniMap(mapManager.getCaveMap(), player.getPos(), posArray,
					mapManager.getWaterLevel());

			if (Globals.debug) {
				hud.drawDebug(player, mapManager, physics.getWorld());
				physics.renderDebug(camera.combined);
			}
		}

		batch.begin();
		switch (state) {
		case GAME_READY:
			// presentReady();
			break;
		case GAME_RUNNING:
			// presentRunning();
			break;
		case GAME_PAUSED:
			// presentPaused();
			break;
		case GAME_LEVEL_END:
			// presentLevelEnd();
			break;
		case GAME_OVER:
			// presentGameOver();
			break;
		}
		batch.end();
	}

	@Override
	public void render(float delta) {
		update(delta);
		draw();
	}

	@Override
	public void resize(int width, int height) {
		hud.resize(width, height);

		camera.setToOrtho(true, width / GameConstants.TILE_SIZE, height
				/ GameConstants.TILE_SIZE);
		camera.update();

		physics.resize(camera);

		rayHandler.setCombinedMatrix(camera.combined);
		batch.setProjectionMatrix(camera.combined);
	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {
		if (state == GameState.GAME_RUNNING)
			state = GameState.GAME_PAUSED;
	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		batch.dispose();
		hud.dispose();
		mapManager.dispose();
		physics.dispose();
		rayHandler.dispose();
		shapeRenderer.dispose();
	}

	private void setCameraPos(float x, float y) {
		if (x < minCamPos.x)
			camera.position.x = minCamPos.x;
		else if (x > maxCamPos.x)
			camera.position.x = maxCamPos.x;
		else
			camera.position.x = x;

		if (y < minCamPos.y)
			camera.position.y = minCamPos.y;
		else if (y > maxCamPos.y)
			camera.position.y = maxCamPos.y;
		else
			camera.position.y = y;

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		rayHandler.setCombinedMatrix(camera.combined);
	}
}
