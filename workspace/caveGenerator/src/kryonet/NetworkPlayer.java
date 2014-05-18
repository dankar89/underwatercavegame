package kryonet;

import com.esotericsoftware.kryonet.Connection;

public class NetworkPlayer {
	public float x, y;
	public float mouseX, mouseY;
	public int lastMouseButtonClicked;
	public boolean flashLightActivated;
	public int lastKeyUp, lastKeyDown;
	public Connection c;
	public int roomStatus; // TODO: should probably separate the "roomPlayer"
							// and the "ingame player"....
}
