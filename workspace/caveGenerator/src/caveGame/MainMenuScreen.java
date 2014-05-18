package caveGame;

import multiplayer.StartMultiplayerScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import common.Assets;

public class MainMenuScreen implements Screen {

	private CaveGame mGame;

	private static final int BUTTON_WIDTH = 250;
	private static final int BUTTON_HEIGHT = 40;
	private static final int BUTTON_SPACING = 10;

	private Stage mStage;

	private TextButton mStartGameButton;
	private TextButton mMultiplayersButton;
	private TextButton mOptionsButton;
	private TextButton mQuitButton;

	private Label mGameLabel;

	public MainMenuScreen(CaveGame game) {
		mGame = game;

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		mStage = new Stage(w, h, true);
		Gdx.input.setInputProcessor(mStage);

		mGameLabel = new Label("Welcome to my cave game!", Assets.labelStyle);

		mStartGameButton = new TextButton("Start game", Assets.buttonStyle);
		mStartGameButton.setColor(Color.YELLOW);

		mMultiplayersButton = new TextButton("Multiplayer", Assets.buttonStyle);
		mMultiplayersButton.setColor(Color.YELLOW);

		mOptionsButton = new TextButton("Options", Assets.buttonStyle);
		mOptionsButton.setColor(Color.YELLOW);

		mQuitButton = new TextButton("Quit", Assets.buttonStyle);
		mQuitButton.setColor(Color.YELLOW);

		Table table = new Table();
		table.setFillParent(true);
		table.debug();

		table.row();
		table.add(mGameLabel).height(BUTTON_HEIGHT).spaceBottom(50);
		table.row();
		table.add(mStartGameButton);
		table.row();
		table.add(mMultiplayersButton);
		table.row();
		table.add(mOptionsButton);
		table.row();
		table.add(mQuitButton);
		table.layout();

		mStage.addActor(table);
	}

	public void update() {
		mStage.act(Gdx.graphics.getDeltaTime());

		if (mStartGameButton.isPressed()) {
			mGame.setScreen(new GameScreen(mGame));
		} else if (mMultiplayersButton.isPressed()) {
			// WarpController.getInstance().startApp(
			// Globals.getRandomHexString(10));
			mGame.setScreen(new StartMultiplayerScreen(mGame));
		} else if (mOptionsButton.isPressed()) {

		} else if (mQuitButton.isPressed()) {
			mGame.quitGame();
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
		// called when this screen is set as the screen with game.setScreen();
	}

	@Override
	public void hide() {
		// called when current screen changes from this to a different screen
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

	// private String getRandomHexString(int numchars) {
	// Random r = new Random();
	// StringBuffer sb = new StringBuffer();
	// while (sb.length() < numchars) {
	// sb.append(Integer.toHexString(r.nextInt()));
	// }
	// return sb.toString().substring(0, numchars);
	// }
}
