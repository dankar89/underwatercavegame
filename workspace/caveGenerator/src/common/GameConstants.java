package common;

public final class GameConstants {
	public static final int PIXELS_PER_METER = 64;
	public static final int TILE_SIZE = 128;
	public static final int METER_PER_TILE = TILE_SIZE / PIXELS_PER_METER;

	public static final int BACKGROUND_LAYER_1_INDEX = 0; // background wall
															// (rocks and stuff)
	public static final int BACKGROUND_LAYER_2_INDEX = 1; // background water
															// and stuff on top
															// of the background
															// wall
	public static final int BACKGROUND_LAYER_3_INDEX = 2; // objects behind
															// player
	public static final int FOREGROUND_LAYER_1_INDEX = 3; // Objects in front of
															// player
	public static final int FOREGROUND_LAYER_2_INDEX = 4; // water overlay
	public static final int FOREGROUND_LAYER_3_INDEX = 5; // stuff that is "in"
															// the water. should
															// probably be
															// rendered above
															// the water texture
															// to make colors
															// stand out more.
	public static final int FOREGROUND_LAYER_4_INDEX = 6; // walls overlay
}
