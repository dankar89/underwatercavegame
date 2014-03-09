package common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;

public final class GameConstants {
	public static final int PIXELS_PER_METER = 32;
	public static final int TILE_SIZE = 128;
	public static final int METER_PER_TILE = TILE_SIZE / PIXELS_PER_METER;
	public static boolean isAndroid = (Gdx.app.getType() == ApplicationType.Android) ? true
			: false;

	public static final int BACKGROUND_LAYER_1_INDEX = 0;
	public static final int BACKGROUND_LAYER_2_INDEX = 1;
	public static final int BACKGROUND_LAYER_3_INDEX = 2;
	public static final int FOREGROUND_LAYER_1_INDEX = 3;
}
