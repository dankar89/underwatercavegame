package kryonet;

import java.io.IOException;

import caveGame.NetworkData;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import common.Globals;

public class NetworkClient extends Listener {
	public static Client client;

	// static String ip = "localhost";

	public static enum NetworkGameState {
		WAITING, NOT_READY, READY,
	};

	public static NetworkGameState state = NetworkGameState.NOT_READY;

	public static void init(String ipAddress) {
		client = new Client();

		Network.register(client);
		client.addListener(new NetworkClient());

		client.start();
		try {
			System.out.println("Client connecting to " + ipAddress);
			client.connect(5000, ipAddress, Network.tcp_port, Network.udp_port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void received(Connection c, Object o) {
		if (o instanceof PlayerAddRequest) {
			PlayerAddRequest req = (PlayerAddRequest) o;
			NetworkPlayer newPlayer = new NetworkPlayer();
			NetworkData.players.put(req.id, newPlayer);
		} else if (o instanceof PlayerRemoveRequest) {
			PlayerRemoveRequest req = (PlayerRemoveRequest) o;
			NetworkData.players.remove(req.id);
		} else if (o instanceof PlayerUpdateRequest) {
			PlayerUpdateRequest req = (PlayerUpdateRequest) o;
			NetworkData.players.get(req.id).x = req.x;
			NetworkData.players.get(req.id).y = req.y;
		} else if (o instanceof PropertiesResponse) {
			PropertiesResponse resp = (PropertiesResponse) o;
			NetworkData.roomProperties = resp.roomProperties;
			Globals.random.setSeed(NetworkData.roomProperties.get("randSeed")
					.hashCode());
			state = NetworkGameState.READY;
			System.out.println("Got settingsResponse. randSeed = "
					+ NetworkData.roomProperties.get("randSeed"));
		} else if (o instanceof PacketPlayerRoomStatus) {
			PacketPlayerRoomStatus packet = (PacketPlayerRoomStatus) o;
			NetworkData.players.get(packet.id).roomStatus = packet.status;
		}
	}

	public void connected(Connection arg0) {
		System.out.println("Client connected");
	}

	public static void disconnect() {
		if (client.isConnected()) {
			client.stop();
		}

	}
}
