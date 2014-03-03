package common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;

public final class GameConstants {
	public static final int PIXELS_PER_METER = 64;
	public static final int TILE_SIZE = 128;
	public static final int METER_PER_TILE = TILE_SIZE / PIXELS_PER_METER; 
	public static boolean isAndroid = (Gdx.app.getType() == ApplicationType.Android) ? true
			: false;
}
