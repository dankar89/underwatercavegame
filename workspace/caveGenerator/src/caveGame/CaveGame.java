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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.me.cavegenerator.MapManager;
import common.GameResources;
import common.Globals;
import common.HUD;

public class CaveGame implements ApplicationListener {
	private OrthographicCamera camera;
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
	private RayHandler rayHandler;
	private PointLight playerlight;
	private ConeLight flashlight;
	private DirectionalLight sunlight;

	// bod2d stuff
	PhysicsManager physics;
	World box2dWorld;
	Box2DDebugRenderer debugRenderer;

	@Override
	public void create() {
		GameResources.init();

		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		mapWidth = 200;
		mapHeight = 200;

		physics = new PhysicsManager();
//		box2dWorld = new World(new Vector2(0, 0), true);
//		debugRenderer = new Box2DDebugRenderer();

		camPos = new Vector2(mapWidth / 2, mapHeight / 2);

		mapManager = new MapManager(mapWidth, mapHeight, camPos);
		mapManager.generateMap(physics.getWorld());
//		mapManager.createBodies(box2dWorld);
//		mapManager.createTileMap();

		camera = new OrthographicCamera();
		camera.setToOrtho(true, w / Globals.PIXELS_PER_METER, h / Globals.PIXELS_PER_METER);

		player = new Player(physics.getWorld(), new Vector2(mapWidth / 2 + .5f, -.5f));

		hud = new HUD(w, h);

		batch = new SpriteBatch();

		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		rayHandler = new RayHandler(box2dWorld);
		rayHandler.setCombinedMatrix(camera.combined);

		playerlight = new PointLight(rayHandler, 11, new Color(1, 1, 1, 0.2f),
				11.5f, player.getBody().getPosition().x, player.getBody()
						.getPosition().y);

		Vector2 lightPos = new Vector2(player.getBody().getPosition().x, player
				.getBody().getPosition().y);
		Color lightColor = new Color(0.2f, 0.8f, 1, 0.55f);
		flashlight = new ConeLight(rayHandler, 5, lightColor, 22.0f,
				lightPos.x, lightPos.y, 0, 20.0f);
		flashlight.attachToBody(player.getBody(), (player.getSprite()
				.getWidth() / 2) / 32,
				-((player.getSprite().getHeight() / 2) / 32));

		backgoundColor = new Color(0, 0.25f, 0.45f, 1.0f);

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
		
		camera.setToOrtho(true, width / Globals.PIXELS_PER_METER, height
				/ Globals.PIXELS_PER_METER);
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
//			physics.reset();
			mapManager.generateMap(physics.getWorld());
		}

		if (GameResources.isReady()) {
			mapManager.render();

			if (hud.getTouchpad().isTouched()) {
				player.move(hud.getTouchpad().getKnobPercentX() / 18, hud
						.getTouchpad().getKnobPercentY() / 18);
				player.getBody().setTransform(
						player.getBody().getPosition(),
						(float) Math.atan2(hud.getTouchpad().getKnobPercentY(),
								hud.getTouchpad().getKnobPercentX()));
			}

			batch.begin();
			player.draw(batch);
			batch.end();

			player.update(0, new Vector2(mapManager.getCamera().position.x, mapManager.getCamera().position.y));
			
//			setCameraPos(player.getPos().x, player.getPos().x);
//			camera.position.set(player.getPos(), 0);
			
			mapManager.update(player.getPos());

			if(Globals.lightsEnabled) {
				flashlight.setActive(player.isFlashlightEnabled());
				rayHandler.updateAndRender();	
			}

			hud.update(Gdx.graphics.getDeltaTime());
			if (!Globals.hideUI) {
				hud.draw();
				hud.drawMiniMap(mapManager.getCaveMap(), player.getPos());

				if (Globals.debug) {
					hud.drawDebug(player, mapManager);
					physics.renderDebug(mapManager.getCamera().combined);
				}
			}
			physics.update(1/60f, camera);
		}
	}
	
//	private void setCameraPos(float x, float y) {	
//		if (x < minCamPos.x)
//			camera.position.x = minCamPos.x;
//		else if (x > maxCamPos.x)
//			camera.position.x = maxCamPos.x;
//		else
//			camera.position.x = x;
//		
//		if (y < minCamPos.y)
//			camera.position.y = minCamPos.y;
//		else if (y > maxCamPos.y)
//			camera.position.y = maxCamPos.y;
//		else
//			camera.position.y = y;		
//		
//		camRect.x = camera.position.x - ((w / tileMapManager.getTileSize()) / 2);
//		camRect.y = camera.position.y - ((h / tileMapManager.getTileSize()) / 2);			
//	}

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
	}
}
