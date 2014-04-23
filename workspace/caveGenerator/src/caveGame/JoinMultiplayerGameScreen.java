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
import common.IpValidator;

public class JoinMultiplayerGameScreen implements Screen {

	private CaveGame mGame;
	private Stage mStage;

	private TextButton mBackButton;
	private TextButton mJoinButton;
	
	private Label mIpLabel;
	private TextField mIpTextField;

	public JoinMultiplayerGameScreen(CaveGame game) {
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

		mIpLabel = new Label("IP Address:", Assets.defaultSkin);
		mIpTextField = new TextField("localhost", Assets.defaultSkin);
		
		mJoinButton = new TextButton("Join", Assets.defaultSkin);
		
		table.columnDefaults(0).spaceRight(10f);		
		table.add(mIpLabel).right();
		table.add(mIpTextField).left();
		table.row().spaceTop(40f).colspan(2);
		table.add(mJoinButton);

	}

	public void update() {
		mStage.act();

		if (mBackButton.isPressed() || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			mGame.setScreen(new StartMultiplayerScreen(mGame));
		} else if (mJoinButton.isPressed()) { // create server
			String ipAddress = mIpTextField.getText();
			if(IpValidator.validateIpV4(ipAddress) || ipAddress.equals("localhost")){
				NetworkClient.init(ipAddress); 
				mGame.setScreen(new MultiplayerRoomScreen(mGame));
			} else {
				System.out.println("IP address is invalid!");
				return;
			}
		}
	}

	public void draw() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mStage.draw();
//		Table.drawDebug(mStage);
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
