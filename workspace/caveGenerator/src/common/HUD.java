package common;

import caveGame.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.me.cavegenerator.CaveMap;
import com.me.cavegenerator.Cell.CellType;
import com.me.cavegenerator.MapManager;

public class HUD {

	private BitmapFont font;
	private SpriteBatch hudBatch;
	private SpriteBatch minimapBatch;

	private Stage stage;
	private Touchpad touchpad;
	private TouchpadStyle touchpadStyle;
	private Skin touchpadSkin;

	private OrthographicCamera minimapCamera;

	private Sprite miniMapPlayerSprite;
	private Sprite miniMapWallSprite;
	private float miniMapOffsetX = 0;
	private float miniMapOffsetY = 0;

	public Touchpad getTouchpad() {
		return touchpad;
	}

	public Stage getStage() {
		return stage;
	}

	private int w, h;

	public HUD(int w, int h) {
		this.w = w;
		this.h = h;

		font = new BitmapFont();
		font.setColor(Color.YELLOW);

		stage = new Stage(w, h, true);
		hudBatch = (SpriteBatch) stage.getSpriteBatch();
		// hudBatch = new SpriteBatch();

		// TODO: load this in assetmanager!!
		TextureAtlas touchpadAtlas = new TextureAtlas("hud/touchpad.txt");
		touchpadSkin = new Skin(touchpadAtlas);

		touchpadStyle = new TouchpadStyle();

		touchpadStyle.background = touchpadSkin
				.getDrawable("touchpad_background");
		touchpadStyle.knob = touchpadSkin.getDrawable("touchpad_knob");

		touchpad = new Touchpad(20, touchpadStyle);

		touchpad.setBounds(20, 20, 300, 300);
		touchpad.setOrigin(touchpad.getWidth() / 2, touchpad.getHeight() / 2);
		stage.addActor(touchpad);

		// setup our mini map camera
		minimapCamera = new OrthographicCamera();
		minimapCamera.setToOrtho(true, w, h);
		minimapCamera.zoom = 25;
		minimapBatch = new SpriteBatch();

		miniMapPlayerSprite = new Sprite(GameResources.onePixelTexture);
		miniMapPlayerSprite.setColor(Color.RED);
		// miniMapPlayerSprite.setOrigin(2, 2);
		miniMapPlayerSprite.scale(3f);
	}

	public void draw() {

		if (GameConstants.isAndroid)
			stage.draw();
		// hudBatch.begin();

		// if (Globals.debug) {
		// font.draw(
		// hudBatch,
		// "FPS: "
		// + Integer.toString(Gdx.graphics
		// .getFramesPerSecond()), 20, h - 20);
		// }
		// hudBatch.end();

	}

	public void drawMiniMap(CaveMap caveMap, Vector2 playerPos) {
		// minimapCamera.update();
		// minimapBatch.setProjectionMatrix(minimapCamera.combined);

		miniMapOffsetX = (w - (caveMap.getWidth() + (w / 50)));
		miniMapOffsetY = h / 50;

		minimapBatch.begin();
		for (int x = 0; x < caveMap.getWidth(); x++) {
			for (int y = 0; y < caveMap.getHeight(); y++) {
				if (caveMap.getCellAt(x, y).getCellType() == CellType.WALL) {
					minimapBatch.setColor(Color.BLACK);
				} else {
					minimapBatch.setColor(Color.WHITE);
				}
				minimapBatch.draw(GameResources.onePixelTexture, x
						+ miniMapOffsetX, y + miniMapOffsetY);
			}
		}

		miniMapPlayerSprite.setPosition(miniMapOffsetX + playerPos.x,
				miniMapOffsetY + playerPos.y);
		miniMapPlayerSprite.draw(minimapBatch);
		minimapBatch.end();
	}

	public void drawDebug(Player player, MapManager mapManager, World world) {
		hudBatch.begin();

		font.draw(hudBatch,
				"FPS: " + Integer.toString(Gdx.graphics.getFramesPerSecond()),
				20, h - 20);

		font.draw(hudBatch, "player body pos: "
				+ player.getBody().getPosition(), 20, h - 40);
		font.draw(hudBatch, "angleRad: " + player.getBody().getAngle(), 20,
				h - 60);
		font.draw(hudBatch,
				"angleDeg: " + Math.toDegrees(player.getBody().getAngle()), 20,
				h - 80);
		 font.draw(hudBatch, "bodies: " + world.getBodyCount(),
		 20,
		 h - 100);

		hudBatch.end();
	}

	public void update(float deltaTime) {
		if (GameConstants.isAndroid) {
			stage.act(deltaTime);
		} else {
			// TODO: unfocus touchpad!!!
		}
	}

	public void dispose() {

		font.dispose();
		stage.dispose();
		minimapBatch.dispose();
		// hudBatch.dispose();
	}

	public void resize(int w, int h) {
		stage.setViewport(w, h, true);
		minimapBatch.setProjectionMatrix(minimapCamera.combined);
	}
}
