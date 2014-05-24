package common;

import caveGame.PhysicsDataJsonParser;
import caveGame.TileShapeData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Assets {
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
	
	//hud regions
	public static Array<AtlasRegion> flashLight = new Array<AtlasRegion>();
	public static Array<AtlasRegion> fuelGauge = new Array<AtlasRegion>();
	public static Array<AtlasRegion> heart = new Array<AtlasRegion>();

	// parsed vertices for creation of box2d shapes for the tiles
	public static ObjectMap<String, TileShapeData> shapeDataMap = new ObjectMap<String, TileShapeData>();

	// player sprite regions
	public static Array<AtlasRegion> diverSprites = new Array<AtlasRegion>();
	public static Array<AtlasRegion> playerSprites = new Array<AtlasRegion>();

	public static TextureAtlas tilesAtlas;
	public static TextureAtlas spriteAtlas;
	public static TextureAtlas playerSpriteAtlas;
	public static TextureAtlas mapTextureAtlas;
	public static TextureAtlas hudTextureAtlas;

	public static BitmapFont font1;

	public static LabelStyle labelStyle;
	public static TextButtonStyle buttonStyle;
	public static SelectBoxStyle selectBoxStyle;
	public static TextFieldStyle textFieldStyle;

	public static ParticleEffect jetpackEffect;

	public static Skin defaultSkin;

	public static void load() {
		assetManager = new AssetManager();
		loadResources();
		getResources();
	}

	private static void loadResources() {

		font1 = new BitmapFont(Gdx.files.internal("data/myFont.fnt"),
				Gdx.files.internal("data/myFont.png"), false);
		font1.setColor(Color.GREEN);
		font1.getRegion().getTexture()
				.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		defaultSkin = new Skin(Gdx.files.internal("skins/uiskin.json"));

		labelStyle = new LabelStyle(font1, Color.YELLOW);
		buttonStyle = new TextButtonStyle();
		buttonStyle.font = font1;
		buttonStyle.overFontColor = Color.RED;
		buttonStyle.fontColor = Color.WHITE;

		selectBoxStyle = new SelectBoxStyle();
		selectBoxStyle.font = font1;
		selectBoxStyle.fontColor = Color.WHITE;

		textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = font1;
		textFieldStyle.fontColor = Color.WHITE;

		// queue stuff for loading
		assetManager.load("textures/terrain/tiles128.txt", TextureAtlas.class);
		
		assetManager.load("hud/map/map.txt", TextureAtlas.class);
		assetManager.load("hud/hud.txt", TextureAtlas.class);
		
		assetManager
				.load("textures/diver/test/diver64.txt", TextureAtlas.class);
		assetManager.load("textures/player/player64.txt", TextureAtlas.class);

		assetManager.load("data/whitesquare.png", Texture.class);

		assetManager.load("data/test.png", Texture.class);

		assetManager.load("hud/onePixel.png", Texture.class);

		assetManager.load("textures/player6.png", Texture.class);

		assetManager.load("effects/jetpack2.p", ParticleEffect.class);

		// do the actual loading
		assetManager.finishLoading();
	}

	private static void getResources() {
		// load the vertex data for the tiles
		shapeDataMap = PhysicsDataJsonParser.parse("data/physicsData.json");

		testPlayer = assetManager.get("data/test.png");
		onePixelTexture = assetManager.get("hud/onePixel.png");
		tilesAtlas = assetManager.get("textures/terrain/tiles128.txt");
		mapTextureAtlas = assetManager.get("hud/map/map.txt");
		hudTextureAtlas = assetManager.get("hud/hud.txt");
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
		waterTexture = tilesAtlas.findRegion("water");
		waterSurfaceTexture = tilesAtlas.findRegion("waterSurface");
		
		flashLight = hudTextureAtlas.findRegions("flashlight");
		fuelGauge = hudTextureAtlas.findRegions("fuel_gauge");
		heart = hudTextureAtlas.findRegions("heart_anim");

		// player sprites

		// TODO: Remove this test!!
		playerSprites = playerSpriteAtlas.findRegions("player");
		diverSprites = spriteAtlas.findRegions("diver");
		
		jetpackEffect = assetManager.get("effects/jetpack2.p");
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
