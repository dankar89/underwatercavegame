package caveGame;

import kryonet.NetworkClient;
import kryonet.NetworkServer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import common.Assets;

public class StartMultiplayerScreen implements Screen {
	private CaveGame mGame;

	private Screen mPrevScreen;

	private Stage mStage;

	private TextButton mBackButton;
	private TextButton mJoinButton;
	private TextButton mHostButton;
	private Label mMessageLabel;

	// private String mErrorMessage = "Connection failed!";
	// private String mConnectMessage = "Connecting to appWarp...";
	// private String mWinMessage = "Level completed!";
	// private String mLooseMessage = "You died!";
	// private String mWaitingMessage = "Waiting for other player";

	public StartMultiplayerScreen(CaveGame game) {
		mGame = game;
		// mPrevScreen = prevScreen;

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		mStage = new Stage(w, h, true);
		Gdx.input.setInputProcessor(mStage);

		mBackButton = new TextButton("Back", Assets.buttonStyle);
		mBackButton.setX(20);
		mBackButton.setY(20);

		mJoinButton = new TextButton("Join game", Assets.buttonStyle);
		mHostButton = new TextButton("Host game", Assets.buttonStyle);

		// mMessageLabel = new Label(mConnectMessage, Assets.labelStyle);
		// mMessageLabel.setAlignment(Align.center);

		Table table = new Table();
		table.setFillParent(true);
		table.debug();

		// table.row();
		// table.add(mMessageLabel);
		table.row();
		table.add(mJoinButton);
		table.row();
		table.add(mHostButton);

		mStage.addActor(mBackButton);
		mStage.addActor(table);
	}

	public void update() {
		mStage.act();

		if (mBackButton.isPressed() || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			mGame.setScreen(new MainMenuScreen(mGame));
		} else if (mJoinButton.isPressed()) { // create client
			mGame.setScreen(new JoinMultiplayerGameScreen(mGame));
		} else if (mHostButton.isPressed()) { // create server
			mGame.setScreen(new HostMultiplayerGameScreen(mGame));
			// NetworkServer.init();
			// NetworkClient.init();
		}
	}

	public void draw() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mStage.draw();
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
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		mStage.dispose();
	}

}
