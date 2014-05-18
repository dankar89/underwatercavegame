package kryonet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

// This class is a convenient place to keep things common to both the client and server.
public class Network {
	static public final int tcp_port = 54555;
	static public final int udp_port = 54777;

	public static enum PlayerRoomStatus {
		READY, NOT_READY, WAITING_FOR_PLAYERS,
	}

	// This registers objects that are going to be sent over the network.
	static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(PlayerAddRequest.class);
		kryo.register(PlayerAddResponse.class);
		kryo.register(PlayerRemoveRequest.class);
		kryo.register(PlayeRemoveResponse.class);
		kryo.register(PlayerPositionUpdateRequest.class);
		kryo.register(PlayerMouseUpdateRequest.class);
		kryo.register(PlayerKeyDownUpdateRequest.class);
		kryo.register(PlayerKeyUpUpdateRequest.class);
		kryo.register(PlayerUpdateResponse.class);
		kryo.register(PropertiesResponse.class);
		kryo.register(PacketPlayerRoomStatus.class);
		kryo.register(java.util.HashMap.class);
	}
}
