package caveGame;

import box2dLight.ConeLight;
import box2dLight.DirectionalLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.me.cavegenerator.MapManager;
import common.GameConstants;
import common.GameResources;
import common.Globals;
import common.HUD;

//import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class CaveGame implements ApplicationListener {
	private OrthographicCamera camera;
	private OrthographicCamera debugCamera;
	private SpriteBatch batch;

	private MapManager mapManager;

	private HUD hud;

	private Player player;
	private Vector2 playerStartPos;

	private Vector2 minCamPos, maxCamPos, camPos;

	private int w, h;
	private int mapWidth, mapHeight;

	private InputMultiplexer inputMultiplexer;

	private Color backgoundColor;

	private float delta;

	// box2dlights test stuff
	// add these to physicsManager
	private RayHandler rayHandler;
	private PointLight playerlight;
	private ConeLight flashlight;
	private DirectionalLight sunlight;

	PhysicsManager physics;

	private ShapeRenderer shapeRenderer;

	@Override
	public void create() {
		GameResources.init();

		PhysicsDataJsonParser.parse("data/physicsData.json");

		// Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width,
		// Gdx.graphics.getDesktopDisplayMode().height, Globals.fullscreen);
		shapeRenderer = new ShapeRenderer();

		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		mapWidth = 200;
		mapHeight = 200;

		physics = new PhysicsManager();

		camPos = new Vector2(mapWidth / 2, mapHeight / 2);

		batch = new SpriteBatch();
//		batch.enableBlending();

		camera = new OrthographicCamera();
		camera.setToOrtho(true, w / GameConstants.TILE_SIZE, h
				/ GameConstants.TILE_SIZE);

		mapManager = new MapManager(mapWidth, mapHeight, camera);
		mapManager.generateMap(physics.getWorld());

		minCamPos = new Vector2(w / (GameConstants.TILE_SIZE * 2), h
				/ (GameConstants.TILE_SIZE * 2));
		maxCamPos = new Vector2(mapWidth - minCamPos.x, mapWidth - minCamPos.y);

		player = new Player(physics.getWorld(), new Vector2(mapWidth / 2 + .5f,
				.5f));
		// player = new Player(physics.getWorld(), new Vector2(2, 2));

		hud = new HUD(w, h);

		batch = new SpriteBatch();

		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		rayHandler = new RayHandler(physics.getWorld());
		rayHandler.setCombinedMatrix(camera.combined);
		// rayHandler.getLightMapTexture().setFilter(TextureFilter.Nearest,
		// TextureFilter.Nearest);

		playerlight = new PointLight(rayHandler, 11, new Color(1, 1, 1, 0.2f),
				11.5f, player.getBody().getPosition().x, player.getBody()
						.getPosition().y);
		playerlight.attachToBody(player.getBody(), 0, 0);

		Vector2 lightPos = new Vector2(player.getBody().getPosition().x, player
				.getBody().getPosition().y);
		Color lightColor = new Color(0.2f, 0.5f, 0.5f, 0.55f);
		flashlight = new ConeLight(rayHandler, 5, lightColor, 22.0f,
				lightPos.x, lightPos.y, 0, 20.0f);
		flashlight.attachToBody(player.getBody(), (player.getSprite()
				.getWidth() / 2) / (GameConstants.TILE_SIZE / 2), -((player
				.getSprite().getHeight() / 2) / (GameConstants.TILE_SIZE / 2)));

		backgoundColor = Color.GRAY;
		// backgoundColor = new Color(1, 1f, 1f, 1.0f);

		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(hud.getStage());
		inputMultiplexer.addProcessor(player);
		inputMultiplexer.addProcessor(mapManager);
		inputMultiplexer.addProcessor(new GameInputProcessor());
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void resize(int width, int height) {
		hud.resize(width, height);
		// mapManager.resize(width, height);

		camera.setToOrtho(true, width / GameConstants.TILE_SIZE, height
				/ GameConstants.TILE_SIZE);
		camera.update();

		rayHandler.setCombinedMatrix(camera.combined);
		batch.setProjectionMatrix(camera.combined);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(backgoundColor.r, backgoundColor.g,
				backgoundColor.b, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		if (Gdx.input.isKeyPressed(Keys.SPACE)
				|| (Gdx.input.isTouched(0) && Gdx.input.isTouched(1))) {
			mapManager.reset(new Vector2(mapWidth / 2, 0));
			// physics.reset();
			mapManager.generateMap(physics.getWorld());
		}

		if (GameResources.isReady()) {
			if (hud.getTouchpad().isTouched()) {
				player.move(hud.getTouchpad().getKnobPercentX() / 18, hud
						.getTouchpad().getKnobPercentY() / 18);
			}

			// mapManager.update(player.getPos());
			player.update(0, Vector2.Zero);

			if ((String) mapManager.getTileProperty((int) player.getPos().x,
					(int) player.getPos().y, "type", GameConstants.BACKGROUND_LAYER_2_INDEX) == "water") {
//				System.out.println("WATER!!!!");
				player.setGravityScale(0);
			} else {
				player.setGravityScale(1f / player.getBody().getMass());
			}

			setCameraPos(player.getPos().x, player.getPos().y);
			mapManager.renderBackgroundlayers(camera);

			batch.begin();
			player.draw(batch);
			batch.end();
			
			mapManager.renderForegroundlayers(camera);

			if (Globals.lightsEnabled) {
				flashlight.setActive(player.isFlashlightEnabled());
				rayHandler.updateAndRender();
			}

			if (Gdx.input.isKeyPressed(Keys.C)) {
				System.out.println("camerapos: " + camera.position);
				System.out.println("playerpos: " + player.getPos());
				// System.out.println("mapbounds: "
				// +mapManager.getRenderer().getViewBounds());
			}

			hud.update(Gdx.graphics.getDeltaTime());
			if (!Globals.hideUI) {
				hud.draw();
				hud.drawMiniMap(mapManager.getCaveMap(), player.getPos());

				if (Globals.debug) {
					hud.drawDebug(player, mapManager, physics.getWorld());
					physics.renderDebug(camera.combined);

					shapeRenderer.setProjectionMatrix(camera.combined);
					shapeRenderer.begin(ShapeType.Filled);
					shapeRenderer.setColor(Color.RED);
					shapeRenderer.rect(camera.position.x - 0.04f,
							camera.position.y - 0.04f, 0.08f, 0.08f);
					shapeRenderer.end();

					shapeRenderer.begin(ShapeType.Line);

					shapeRenderer.end();
				}
			}
			physics.update(1 / 60f, camera);
		}
	}

	public void setCameraPos(float x, float y) {
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
		// mapRenderer.setView(cam);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		batch.dispose();
		hud.dispose();
		mapManager.dispose();
		GameResources.dispose();
		physics.dispose();
		rayHandler.dispose();
		shapeRenderer.dispose();
	}
}
