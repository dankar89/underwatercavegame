package multiplayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kryonet.Network.PlayerRoomStatus;
import kryonet.NetworkClient;
import kryonet.NetworkPlayer;
import kryonet.PacketPlayerRoomStatus;

import caveGame.CaveGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import common.Assets;

public class MultiplayerRoomScreen implements Screen {

	private CaveGame mGame;
	private Stage mStage;

	private TextButton mLeaveButton;
	private TextButton mReadyButton;
	
	private List mPlayerList;
//	private Object[] mListItems;
	private ArrayList<String> mListItems;
	
	private Label mNameLabel;

	private PlayerRoomStatus mStatus = PlayerRoomStatus.WAITING_FOR_PLAYERS;
	
	public MultiplayerRoomScreen(CaveGame game) {
		mGame = game;

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		mStage = new Stage(w, h, true);
		Gdx.input.setInputProcessor(mStage);

		Table table = new Table();
		table.setFillParent(true);
		table.debug();
		mStage.addActor(table);		
		mLeaveButton = new TextButton("Leave game", Assets.defaultSkin);
		mLeaveButton.setX(20);
		mLeaveButton.setY(20);
		mStage.addActor(mLeaveButton);

		String name = NetworkData.roomProperties.get("name");
		mNameLabel = new Label(name, Assets.defaultSkin);
		
		
		for (Object p : NetworkData.players.values()) {
			System.out.println("instance of NetworkPlayer: " + (p instanceof NetworkPlayer));
//			mListItems.add(Integer.toString(((NetworkPlayer) p).c.getID()));
		}
//		mPlayerList = new List(mListItems.toArray(), Assets.defaultSkin);
		
		mReadyButton = new TextButton("Ready", Assets.defaultSkin);
		
		table.columnDefaults(0).spaceRight(10f);		
		table.add(mNameLabel).right();
		table.row().spaceTop(40);
		table.add(mPlayerList);
		table.row().spaceTop(40f);
		table.add(mReadyButton);
	}

	public void update() {
		mStage.act();
		
		if (mLeaveButton.isPressed()) {
			NetworkClient.disconnect();
			mGame.setScreen(new StartMultiplayerScreen(mGame));
		} else if(mReadyButton.isPressed()) {
			PacketPlayerRoomStatus packet = new PacketPlayerRoomStatus();
			mStatus = PlayerRoomStatus.READY;
			packet.status = mStatus.ordinal();
			NetworkClient.client.sendUDP(packet);
			
			refresh();
			
			//TEMP TEMP TEMP TEMP TEMP TEMP
			mGame.setScreen(new MultiplayerGameScreen(mGame));
		}
				
		
	}
	
	private static void refresh(){
		final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
//				if(NetworkData.players.size() != mListItems.size()) {
//					mListItems.clear();
//					for (NetworkPlayer p : NetworkData.players.values()) {
//						mListItems.add(Integer.toString(p.c.getID()));
//					}
//					mPlayerList.setItems(mListItems.toArray());
//					System.out.println((mListItems.toString()));	
//				}
//				System.out.println(new Date());
//				System.out.println("size1 :" + NetworkData.players.size());
//				System.out.println("size2 :" + mListItems.size());
			}
		}, 0, 3, TimeUnit.SECONDS);
	}

	public void draw() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mStage.draw();
		Table.drawDebug(mStage);
	}

	@Override
	public void render(float delta) {
		update();
		draw();
	}

	@Override
	public void resize(int width, int height) {
		mStage.setViewport(width, height, true);
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		mStage.dispose();
	}
}
