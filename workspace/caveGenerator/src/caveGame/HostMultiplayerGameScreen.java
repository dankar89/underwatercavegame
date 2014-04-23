package caveGame;

import java.util.HashMap;

import net.dermetfan.utils.libgdx.AnnotationAssetManager.Asset;
import kryonet.NetworkClient;
import kryonet.NetworkServer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

import common.Assets;
import common.Globals;

public class HostMultiplayerGameScreen implements Screen {

	private CaveGame mGame;
	private Stage mStage;

	private TextButton mBackButton;
	private SelectBox mMaxPlayersDropdown;
	private static final int MAX_PLAYERS = 4;
	private TextButton mHostButton;
	private Label mMessageLabel;
	private Label mMaxPlayersLabel;
	private Label mWorldSeedLabel;
	private TextField mWorldSeedTextField;

	private Label mNameLabel;
	private TextField mNameTextField;

	private Label mTypeLabel;
	private SelectBox mTypeDropdown;

	private String mWorldSeed;
	private String mRndString;

	public HostMultiplayerGameScreen(CaveGame game) {
		mGame = game;

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		mStage = new Stage(w, h, true);
		Gdx.input.setInputProcessor(mStage);

		Table table = new Table();
		table.setFillParent(true);
		table.debug();
		mStage.addActor(table);

		mBackButton = new TextButton("Back", Assets.defaultSkin);
		mBackButton.setX(20);
		mBackButton.setY(20);
		mStage.addActor(mBackButton);

		mNameLabel = new Label("Room name:", Assets.defaultSkin);
		mNameTextField = new TextField("room 1", Assets.defaultSkin);

		mTypeLabel = new Label("Type:", Assets.defaultSkin);
		String[] typeArray = { "Local", "Internet" };
		mTypeDropdown = new SelectBox(typeArray, Assets.defaultSkin);
		mTypeDropdown.setSelection(0);

		mWorldSeedLabel = new Label("World seed: ", Assets.defaultSkin);

		mRndString = Globals.getRandomHexString(15);
		mWorldSeedTextField = new TextField(mRndString, Assets.defaultSkin);

		mMaxPlayersLabel = new Label("Players:", Assets.defaultSkin);
		String[] numPlayersArray = { "2", "3", "4" };
		mMaxPlayersDropdown = new SelectBox(numPlayersArray, Assets.defaultSkin);
		mMaxPlayersDropdown.setSelection(0);

		mHostButton = new TextButton("Host game", Assets.defaultSkin);
		table.columnDefaults(0).spaceRight(10f);
		table.add(mNameLabel).right();
		table.add(mNameTextField).left();
		table.row().spaceTop(10);
		table.add(mTypeLabel).right();
		table.add(mTypeDropdown).left();
		table.row().spaceTop(10);
		table.add(mWorldSeedLabel).right();
		table.add(mWorldSeedTextField).left();
		table.row().spaceTop(10);
		table.add(mMaxPlayersLabel).right();
		table.add(mMaxPlayersDropdown).left();
		table.row().spaceTop(40f).colspan(2);
		table.add(mHostButton);

	}

	public void update() {
		mStage.act();

		if (mBackButton.isPressed() || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			mGame.setScreen(new StartMultiplayerScreen(mGame));
		} else if (mHostButton.isPressed()) { // create server
			HashMap<String, String> roomProperties = new HashMap<String, String>();

			String str = mWorldSeedTextField.getText();
			if (str != "") {
				mWorldSeed = mWorldSeedTextField.getText();
			} else {
				mWorldSeed = mRndString;
			}
			roomProperties.put("randSeed", mWorldSeed);

			if (mNameTextField.getText() != null) {
				roomProperties.put("name", mNameTextField.getText());
			} else {
				System.out.println("Name field is empty!");
				return;
			}

			roomProperties.put("type", mTypeDropdown.getSelection());
			roomProperties
					.put("maxPlayers", mMaxPlayersDropdown.getSelection());

			NetworkServer.init(roomProperties);
			NetworkClient.init("localhost"); // TODO: wil this work over
												// internet?
			mGame.setScreen(new MultiplayerRoomScreen(mGame));
		}
	}

	public void draw() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mStage.draw();
		// Table.drawDebug(mStage);
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
