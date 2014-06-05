package common;

import multiplayer.NetworkData;
import caveGame.FlashLight;
import caveGame.GameMap;
import caveGame.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
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
	private Sprite miniMapRemotePlayer1Sprite;
	private Sprite miniMapRemotePlayer2Sprite;
	private Sprite miniMapRemotePlayer3Sprite;
	private Sprite miniMapWallSprite;
	private float miniMapOffsetX = 0;
	private float miniMapOffsetY = 0;

	private int flashLightIconIndex = 0;

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

		miniMapPlayerSprite = new Sprite(Assets.onePixelTexture);
		miniMapPlayerSprite.setColor(Color.RED);
		miniMapPlayerSprite.scale(3f);

		if (Globals.isCurrentGameMultiplayer) {
			miniMapRemotePlayer1Sprite = new Sprite(Assets.onePixelTexture);
			miniMapRemotePlayer1Sprite.setColor(Color.ORANGE);
			miniMapRemotePlayer1Sprite.scale(3f);

			// miniMapRemotePlayer2Sprite = new Sprite(Assets.onePixelTexture);
			// miniMapRemotePlayer2Sprite.setColor(Color.CYAN);
			// miniMapRemotePlayer2Sprite.scale(3f);
			//
			// miniMapRemotePlayer3Sprite = new Sprite(Assets.onePixelTexture);
			// miniMapRemotePlayer3Sprite.setColor(Color.MAGENTA);
			// miniMapRemotePlayer3Sprite.scale(3f);
		}
	}

	public void draw(Player player) {
		if (Globals.isAndroid)
			stage.draw();

		hudBatch.begin();
		hudBatch.draw(Assets.flashLight.get(flashLightIconIndex), 20, 20);
		hudBatch.end();
	}

	public void drawMiniMap(CaveMap caveMap, Vector2 playerPos,
			Vector2[] remotePlayersPos, int waterLevel) {
		// minimapCamera.update();
		// minimapBatch.setProjectionMatrix(minimapCamera.combined);

		miniMapOffsetX = (w - (caveMap.getWidth() + (w / 50)));
		miniMapOffsetY = h / 50;

		minimapBatch.begin();
		for (int x = 0; x < caveMap.getWidth(); x++) {
			for (int y = 0; y < caveMap.getHeight(); y++) {
				if (caveMap.getCellAt(x, y).getCellType() == CellType.WALL) {
					minimapBatch.setColor(Color.BLACK);
				} else if (y >= waterLevel) {
					minimapBatch.setColor(0, 166, 255, 255);
				} else {
					minimapBatch.setColor(Color.WHITE);
				}
				minimapBatch.draw(Assets.onePixelTexture, x + miniMapOffsetX, y
						+ miniMapOffsetY);
			}
		}

		miniMapPlayerSprite.setPosition(miniMapOffsetX + playerPos.x,
				miniMapOffsetY + playerPos.y);
		miniMapPlayerSprite.draw(minimapBatch);

		if (remotePlayersPos[0] != null) {
			miniMapRemotePlayer1Sprite.setPosition(miniMapOffsetX
					+ remotePlayersPos[0].x, miniMapOffsetY
					+ remotePlayersPos[0].y);
			// System.out.println("remote player pos: " + remotePlayersPos[0]);
			miniMapRemotePlayer1Sprite.draw(minimapBatch);
		}
		//
		// if (remotePlayersPos[1] != null) {
		// miniMapRemotePlayer2Sprite.setPosition(miniMapOffsetX
		// + remotePlayersPos[1].x, miniMapOffsetY
		// + remotePlayersPos[1].y);
		// miniMapRemotePlayer2Sprite.draw(minimapBatch);
		// }
		//
		// if (remotePlayersPos[2] != null) {
		// miniMapRemotePlayer3Sprite.setPosition(miniMapOffsetX
		// + remotePlayersPos[2].x, miniMapOffsetY
		// + remotePlayersPos[2].y);
		// miniMapRemotePlayer3Sprite.draw(minimapBatch);
		// }
		minimapBatch.end();
	}

	public void drawMiniMap(CaveMap caveMap, Vector2 playerPos, int waterLevel) {
		miniMapOffsetX = (w - (caveMap.getWidth() + (w / 50)));
		miniMapOffsetY = h / 50;

		minimapBatch.begin();
		for (int x = 0; x < caveMap.getWidth(); x++) {
			for (int y = 0; y < caveMap.getHeight(); y++) {
				if (caveMap.getCellAt(x, y).getCellType() == CellType.WALL) {
					minimapBatch.setColor(Color.BLACK);
				} else if (y >= waterLevel) {
					minimapBatch.setColor(0, 166, 255, 255);
				} else {
					minimapBatch.setColor(Color.WHITE);
				}
				minimapBatch.draw(Assets.onePixelTexture, x + miniMapOffsetX, y
						+ miniMapOffsetY);
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
		if (Globals.isCurrentGameMultiplayer) {
			font.draw(hudBatch,
					"num of other players: " + NetworkData.players.size(), 20,
					h - 60);
			font.draw(hudBatch,
					"roomProps: " + NetworkData.roomProperties.toString(), 20,
					h - 80);
		}
		font.draw(hudBatch, "bodies: " + world.getBodyCount(), 20, h - 100);

		hudBatch.end();
	}

	public void update(float deltaTime, Player player) {
		if (Globals.isAndroid) {
			stage.act(deltaTime);
		} else {
			flashLightIconIndex = MathUtils
					.floorPositive(((player.getFlashLight().getActiveTime() / player
							.getFlashLight().BATTERY_LIFE) * (Assets.flashLight.size -1)));
			
//			flashLightIconIndex = MathUtils.clamp(flashLightIconIndex, 0, Assets.flashLight.size);
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
