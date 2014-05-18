package common;

import java.security.SecureRandom;
import java.util.Random;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

public final class Globals {
	public static boolean debug = true;
	public static boolean drawMiniMap = true;
	public static boolean hideUI = false;
	public static boolean lightsEnabled = true;
	public static boolean fullscreen = false;
	public static float cameraZoom = 0;

	public static SecureRandom random = new SecureRandom();

	public static boolean isAndroid = (Gdx.app.getType() == ApplicationType.Android) ? true
			: false;

	public static String getRandomHexString(int numchars) {
		StringBuffer sb = new StringBuffer();
		while (sb.length() < numchars) {
			sb.append(Integer.toHexString(MathUtils.random.nextInt()));
		}
		return sb.toString().substring(0, numchars);
	}

	public static boolean isCurrentGameMultiplayer = false;

	public enum FixtureUserData {
		PLAYER_FOOT_SENSOR,
	}
	
	public static int numOfFootContacts = 0; 
}
