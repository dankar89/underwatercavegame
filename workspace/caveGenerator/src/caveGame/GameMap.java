package caveGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.me.cavegenerator.CaveMap;
import com.me.cavegenerator.Cell.CellType;
import common.Assets;

public class GameMap {

	private CaveGame mGame;
	private Stage mStage;

	private TextureRegion playerIcon;
	private TextureRegion entranceIcon;
	private TextureRegion shopIcon;
	private TextureRegion mapBackgroundTexture;
	private TextureRegion mapWallTexture;
	private TextureRegion mapWaterTexture;

	private float startX = 0;
	private float startY = 0;
	private SpriteBatch mapBatch;
	private OrthographicCamera camera;

	private CaveMap caveMap;

	private int w, h;

	private int mapTileSize;

	public GameMap(CaveGame game, CaveMap caveMap) {
		playerIcon = Assets.mapTextureAtlas.findRegion("player1_icon");
		entranceIcon = Assets.mapTextureAtlas.findRegion("entrance_icon");
		shopIcon = Assets.mapTextureAtlas.findRegion("shop_icon");
		mapBackgroundTexture = Assets.mapTextureAtlas.findRegion("background");
		mapWallTexture = Assets.mapTextureAtlas.findRegion("wall");
		mapWaterTexture = Assets.mapTextureAtlas.findRegion("water");

		// setup our mini map camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);
//		 camera.zoom = 25;
		mapBatch = new SpriteBatch();
		
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();

		mapTileSize = mapWallTexture.getRegionWidth();

		this.caveMap = caveMap;

		mapBatch.setColor(new Color(1, 1, 1, 1));
		
		startX = (w / 2) - (caveMap.getWidth() / 2);
		startY = (h / 2) - (caveMap.getHeight() / 2);
	}

	public void draw() {
		mapBatch.begin();
		for (int x = 0; x < caveMap.getWidth(); x++) {
			for (int y = 0; y < caveMap.getHeight(); y++) {
				if (caveMap.getCellAt(x, y).getCellType() == CellType.WALL) {
					mapBatch.draw(mapWallTexture, startX + (x * mapTileSize),
							startY + (y * mapTileSize));
				} else if (y >= caveMap.getWaterLevel()) {
					mapBatch.draw(mapWaterTexture, startX + (x * mapTileSize),
							startY + (y * mapTileSize));
				} else {
					mapBatch.draw(mapBackgroundTexture, startX
							+ (x * mapTileSize), startY + (y * mapTileSize));
				}
				
				if(caveMap.getCellAt(x, y).getProperty() == "entrance"){
					mapBatch.draw(entranceIcon, startX + (x * mapTileSize),
							startY + (y * mapTileSize));
				} else if(caveMap.getCellAt(x, y).getProperty() == "shop"){
					mapBatch.draw(shopIcon, startX + (x * mapTileSize),
							startY + (y * mapTileSize));
				}
			}
		}

		// miniMapPlayerSprite.setPosition(mapOffsetX + playerPos.x,
		// mapOffsetY + playerPos.y);
		// miniMapPlayerSprite.draw(minimapBatch);
		mapBatch.end();
	}

	public void resize(int width, int height) {
		camera.setToOrtho(false, w, h);
		camera.update();
		mapBatch.setProjectionMatrix(camera.combined);
	}

	public void dispose() {
		mapBatch.dispose();
	}
}
