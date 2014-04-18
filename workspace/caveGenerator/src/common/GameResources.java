package common;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import caveGame.PhysicsDataJsonParser;
import caveGame.TileShapeData;

public class GameResources {
	private static AssetManager assetManager;

	// tilemap regions
	public static AtlasRegion waterTexture;
	public static AtlasRegion waterSurfaceTexture;
	public static Texture testPlayer;
	public static Texture onePixelTexture;
	public static Array<AtlasRegion> cornerTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> verticalTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> horizontalTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> lonelyHorizontalTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> lonelyVerticalTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> thinHorizontalTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> thinVerticalTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> rockTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> stuffInTheWater = new Array<AtlasRegion>();
	public static AtlasRegion wallRegion;

	// parsed vertices for creation of box2d shapes for the tiles
	public static ObjectMap<String, TileShapeData> shapeDataMap = new ObjectMap<String, TileShapeData>();

	// player sprite regions
	public static Array<AtlasRegion> diverSprites = new Array<AtlasRegion>();
	public static Array<AtlasRegion> playerSprites = new Array<AtlasRegion>();

	public static TextureAtlas tilesAtlas;
	public static TextureAtlas spriteAtlas;
	public static TextureAtlas playerSpriteAtlas;

	public static void init() {
		assetManager = new AssetManager();
		loadResources();
		getResources();
	}

	private static void loadResources() {

		// queue stuff for loading
		assetManager.load("textures/terrain/tiles128.txt", TextureAtlas.class);
		assetManager.load("textures/diver/test/diver64.txt", TextureAtlas.class);
		assetManager.load("textures/player/player64.txt", TextureAtlas.class);

		assetManager.load("data/whitesquare.png", Texture.class);

		assetManager.load("hud/onePixel.png", Texture.class);

		assetManager.load("textures/player6.png", Texture.class);

		// do the actual loading
		assetManager.finishLoading();
	}

	private static void getResources() {
		// load the vertex data for the tiles
		shapeDataMap = PhysicsDataJsonParser.parse("data/physicsData.json");
				
//		testPlayer = assetManager.get("textures/player6.png");
		onePixelTexture = assetManager.get("hud/onePixel.png");
		tilesAtlas = assetManager.get("textures/terrain/tiles128.txt");
		spriteAtlas = assetManager.get("textures/diver/test/diver64.txt");
		playerSpriteAtlas = assetManager.get("textures/player/player64.txt");

		// tilemap textures
		cornerTiles = tilesAtlas.findRegions("corner");
		horizontalTiles = tilesAtlas.findRegions("horizontalTile");
		verticalTiles = tilesAtlas.findRegions("verticalTile");
		lonelyHorizontalTiles = tilesAtlas.findRegions("lonelyTile");
		lonelyVerticalTiles = tilesAtlas.findRegions("lonelyVerticalTile");
		thinVerticalTiles = tilesAtlas.findRegions("thinVerticalTile");
		thinHorizontalTiles = tilesAtlas.findRegions("thinHorizontalTile");
		rockTiles = tilesAtlas.findRegions("rocks");
		stuffInTheWater = tilesAtlas.findRegions("seaweed");
		wallRegion = tilesAtlas.findRegion("empty");
		waterTexture =  tilesAtlas.findRegion("water");
		waterSurfaceTexture =  tilesAtlas.findRegion("waterSurface");
		
		// player sprites
		
		//TODO: Remove this test!!
		playerSprites = playerSpriteAtlas.findRegions("player");
		diverSprites = spriteAtlas.findRegions("diver");		
	}

	public static void dispose() {
		assetManager.dispose();
	}

	public static boolean isReady() {
		if (assetManager.update())
			return true;
		return false;
	}
}
