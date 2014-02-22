package common;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;

public final class Globals {
	public static final float WORLD_TO_BOX = 0.032f; // 0.01
	public static final float BOX_TO_WORLD = 100f;
	public static final float PIXELS_PER_METER = 64;
//	public static final float PIXELS_PER_METER = 128;
	public static final float TILE_SIZE = 128;
	public static boolean debug = true;
	public static boolean drawMiniMap = true;
	public static boolean hideUI = false;
	public static boolean lightsEnabled = true;	
	public static float cameraZoom = 0;

	public static boolean isAndroid = (Gdx.app.getType() == ApplicationType.Android) ? true
			: false;
	
	

}
