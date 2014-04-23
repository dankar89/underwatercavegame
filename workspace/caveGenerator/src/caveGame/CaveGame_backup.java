//package caveGame;
//
//import box2dLight.ConeLight;
//import box2dLight.DirectionalLight;
//import box2dLight.PointLight;
//import box2dLight.RayHandler;
//
//import com.badlogic.gdx.ApplicationListener;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input.Keys;
//import com.badlogic.gdx.InputMultiplexer;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL10;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.Pixmap;
//import com.badlogic.gdx.graphics.Pixmap.Filter;
//import com.badlogic.gdx.graphics.Texture.TextureFilter;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
//import com.badlogic.gdx.math.MathUtils;
//import com.badlogic.gdx.math.Vector2;
//import com.me.cavegenerator.MapManager;
//import common.GameConstants;
//import common.Assets;
//import common.Globals;
//import common.HUD;
//
////import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
//
//public class CaveGame_backup implements ApplicationListener {
//	private OrthographicCamera camera;
//	private SpriteBatch batch;
//
//	private MapManager mapManager;
//
//	private HUD hud;
//
//	private Player player;
//
//	private Vector2 minCamPos, maxCamPos, camPos, mouseWorldPos;
//
//	private int w, h;
//	private int mapWidth, mapHeight;
//
//	private InputMultiplexer inputMultiplexer;
//
//	private Color backgoundColor;
//
//	private float delta;
//
//	// box2dlights test stuff
//	// add these to physicsManager
//	private RayHandler rayHandler;
////	private PointLight playerlight;
////	private ConeLight flashlight;
//
//	PhysicsManager physics;
//
//	private ShapeRenderer shapeRenderer;
//
//	@Override
//	public void create() {
//		Assets.load();
//
//		PhysicsDataJsonParser.parse("data/physicsData.json");
//
//		// Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width,
//		// Gdx.graphics.getDesktopDisplayMode().height, Globals.fullscreen);
//		shapeRenderer = new ShapeRenderer();
//
//		w = Gdx.graphics.getWidth();
//		h = Gdx.graphics.getHeight();
//
//		// mapWidth = 200;
//		// mapHeight = 200;
//		mapWidth = 100;
//		mapHeight = 100;
//		
//		camera = new OrthographicCamera();
//		camera.setToOrtho(true, w / GameConstants.TILE_SIZE, h
//				/ GameConstants.TILE_SIZE);
//		
//		physics = new PhysicsManager();
//
//		RayHandler.setGammaCorrection(true);		
//		RayHandler.useDiffuseLight(true);
//		rayHandler = new RayHandler(physics.getWorld());
//		rayHandler.getLightMapTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
//		rayHandler.setBlur(false);
//		rayHandler.setCulling(true);
//		
//		rayHandler.setCombinedMatrix(camera.combined);
//		
//		camPos = new Vector2(mapWidth / 2, mapHeight / 2);
//
//		batch = new SpriteBatch();
////		 batch.enableBlending();
//
//		mapManager = new MapManager(mapWidth, mapHeight, camera);
//		mapManager.generateMap(physics.getWorld());
//
//		minCamPos = new Vector2(w / (GameConstants.TILE_SIZE * 2), h
//				/ (GameConstants.TILE_SIZE * 2));
//		maxCamPos = new Vector2(mapWidth - minCamPos.x, mapWidth - minCamPos.y);
//
//		player = new Player(physics.getWorld(), rayHandler, new Vector2(
//				((mapWidth / 2) - 3) + .5f, 1.5f));
//
//		hud = new HUD(w, h);
//
//		batch = new SpriteBatch();
//
//		backgoundColor = Color.GRAY;
//
//		inputMultiplexer = new InputMultiplexer();
//		inputMultiplexer.addProcessor(hud.getStage());
//		inputMultiplexer.addProcessor(player);
//		inputMultiplexer.addProcessor(mapManager);
//		inputMultiplexer.addProcessor(new GameInputProcessor());
//		Gdx.input.setInputProcessor(inputMultiplexer);
//		
//		Pixmap.setFilter(Filter.NearestNeighbour);
////		Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
//		Pixmap pixmap = new Pixmap(Gdx.files.internal("textures/crosshair2.png"));
////		pixmap.setColor(Color.YELLOW);
////		pixmap.
////		pixmap.drawCircle(pixmap.getWidth()/2, pixmap.getHeight()/2, 6);
//		Gdx.input.setCursorImage(pixmap, pixmap.getWidth()/2, pixmap.getHeight()/2);
//	}
//
//	@Override
//	public void resize(int width, int height) {
//		hud.resize(width, height);
//		// mapManager.resize(width, height);
//
//		camera.setToOrtho(true, width / GameConstants.TILE_SIZE, height
//				/ GameConstants.TILE_SIZE);
//		camera.update();
//
//		physics.resize(camera);
//
//		rayHandler.setCombinedMatrix(camera.combined);
//		batch.setProjectionMatrix(camera.combined);
//	}
//
//	@Override
//	public void render() {
//		Gdx.gl.glClearColor(backgoundColor.r, backgoundColor.g,
//				backgoundColor.b, 1);
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//
//
//		if (Assets.isReady()) {
//			mouseWorldPos = new Vector2(
//					(camera.position.x - ((Gdx.graphics.getWidth() / 2) / (float)GameConstants.TILE_SIZE))
//							+ (Gdx.input.getX() / (float)GameConstants.TILE_SIZE),
//					(camera.position.y - ((Gdx.graphics.getHeight() / 2) / (float)GameConstants.TILE_SIZE))
//							+ (Gdx.input.getY() / (float)GameConstants.TILE_SIZE));
//
//
//
//			player.update(0, mouseWorldPos, mapManager.getWaterLevel());
//			
//
//			setCameraPos(player.getPos().x, player.getPos().y);
//			mapManager.renderBackgroundlayers(camera);
//
//			batch.begin();
//			player.draw(batch);
//			batch.end();
//
//			mapManager.renderForegroundlayers(camera);
//
//			if (Globals.lightsEnabled) {
//				rayHandler.updateAndRender();
//			}
//
//			if (Gdx.input.isKeyPressed(Keys.C)) {
//				camera.zoom -= 0.02f;
//			}
//
//			if (Gdx.input.isKeyPressed(Keys.X)) {
//				camera.zoom += 0.02f;
//			}
//
//			hud.update(Gdx.graphics.getDeltaTime());
//			if (!Globals.hideUI) {
//				hud.draw();
//				hud.drawMiniMap(mapManager.getCaveMap(), player.getPos(), mapManager.getWaterLevel());
//
//				if (Globals.debug) {
//					hud.drawDebug(player, mapManager, physics.getWorld());
//					physics.renderDebug(camera.combined);
//
//					// shapeRenderer.setProjectionMatrix(camera.combined);
//					// shapeRenderer.begin(ShapeType.Filled);
//					// shapeRenderer.setColor(Color.RED);
//					// shapeRenderer.rect(camera.position.x - 0.04f,
//					// camera.position.y - 0.04f, 0.08f, 0.08f);
//					// shapeRenderer.end();
//					//
//					// shapeRenderer.begin(ShapeType.Line);
//					//
//					// shapeRenderer.end();
//				}
//			}
//			physics.update(1 / 60f, camera);
//		}
//	}
//
//	public void setCameraPos(float x, float y) {
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
//		camera.update();
//		batch.setProjectionMatrix(camera.combined);
//		rayHandler.setCombinedMatrix(camera.combined);
//		// mapRenderer.setView(cam);
//	}
//
//	@Override
//	public void pause() {
//
//	}
//
//	@Override
//	public void resume() {
//
//	}
//
//	@Override
//	public void dispose() {
//		batch.dispose();
//		hud.dispose();
//		mapManager.dispose();
//		Assets.dispose();
//		physics.dispose();
//		rayHandler.dispose();
//		shapeRenderer.dispose();
//	}
//}
