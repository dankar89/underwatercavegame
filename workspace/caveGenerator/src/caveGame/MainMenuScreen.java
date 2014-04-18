package caveGame;

import com.badlogic.gdx.Screen;

public class MainMenuScreen implements Screen {

	CaveGame mGame;

	public MainMenuScreen(CaveGame game) {
		this.mGame = game;
	}

	@Override
	public void render(float delta) {
		// if (Gdx.input.justTouched()) {
		//
		// }
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

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
		// never called automatically
	}

}
