package kryonet;

import com.esotericsoftware.kryonet.Connection;

public class NetworkPlayer {
	public float x, y;
	public Connection c;
	public int roomStatus; // TODO: should probably separate the "roomPlayer"
							// and the "ingame player"....
}
