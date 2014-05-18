package kryonet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kryonet.Network.PlayerRoomStatus;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class NetworkServer extends Listener {
	static Server server;
	static Map<Integer, NetworkPlayer> players = new HashMap<Integer, NetworkPlayer>();
	static HashMap<String, String> roomProperties = new HashMap<String, String>();

	public static void init(HashMap<String, String> roomProperties) {
		NetworkServer.roomProperties = roomProperties;
		try {
			server = new Server();
			Network.register(server);
			server.bind(Network.tcp_port, Network.udp_port);
			server.start();
			server.addListener(new NetworkServer());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("roomProperties: " + roomProperties.toString());
		System.out.println("The server is ready");
	}

	public void connected(Connection c) {
		NetworkPlayer player = new NetworkPlayer();
		// player.x = 50;
		// player.y = 2;
		player.c = c;

		PlayerAddRequest playerAddReq = new PlayerAddRequest();
		playerAddReq.id = c.getID();

		// Add new player to other connected players
		server.sendToAllExceptTCP(c.getID(), playerAddReq);

		// add connected players to new player
		for (NetworkPlayer p : players.values()) {
			PlayerAddRequest playerAddReq2 = new PlayerAddRequest();
			playerAddReq2.id = p.c.getID();
			c.sendTCP(playerAddReq2);
		}

		// send properties to new player
		// if(players.size() > 1) {
		PropertiesResponse resp = new PropertiesResponse();

		resp.roomProperties = roomProperties;
		resp.id = c.getID();
		c.sendTCP(resp);
		// }

		players.put(c.getID(), player);
		System.out.println("Player " + c.getID() + " has connected");
	}

	public void disconnected(Connection c) {
		players.remove(c.getID());
		PlayerRemoveRequest playerRemoveReq = new PlayerRemoveRequest();
		playerRemoveReq.id = c.getID();
		server.sendToAllExceptTCP(c.getID(), playerRemoveReq);
		System.out.println("Player " + c.getID() + " disconnected");
	}

	public void idle(Connection c) {
	}

	public void received(Connection c, Object o) {
		if (o instanceof PlayerPositionUpdateRequest) {
			PlayerPositionUpdateRequest req = (PlayerPositionUpdateRequest) o;
			players.get(c.getID()).x = req.x;
			players.get(c.getID()).y = req.y;

			req.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), req);
			// System.out.println("Received and sent a playerUpdateRequest");
		} else if (o instanceof PlayerMouseUpdateRequest) {
			PlayerMouseUpdateRequest req = (PlayerMouseUpdateRequest) o;
			players.get(c.getID()).mouseX = req.mouseX;
			players.get(c.getID()).mouseY = req.mouseY;
			req.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), req);
		} else if (o instanceof PlayerKeyDownUpdateRequest) {
			PlayerKeyDownUpdateRequest req = (PlayerKeyDownUpdateRequest) o;
			players.get(c.getID()).lastKeyDown = req.lastKeyDown;
			req.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), req);
		} else if (o instanceof PlayerKeyUpUpdateRequest) {
			PlayerKeyUpUpdateRequest req = (PlayerKeyUpUpdateRequest) o;
			players.get(c.getID()).lastKeyUp = req.lastKeyUp;
			req.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), req);
		} else if (o instanceof PacketPlayerRoomStatus) {
			// receive room status update from one player and pass it on to the
			// other players in the room
			PacketPlayerRoomStatus packet = (PacketPlayerRoomStatus) o;
			packet.id = c.getID();
			server.sendToAllExceptTCP(c.getID(), packet);

			// TODO: check if all players are ready
			boolean startGame = true;
			for (NetworkPlayer p : players.values()) {
				if (p.roomStatus != PlayerRoomStatus.READY.ordinal()) {
					startGame = false;
				}
			}

		}
	}
}
