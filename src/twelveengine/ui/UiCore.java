package twelveengine.ui;

import static org.lwjgl.opengl.GL11.*;

import twelveengine.Engine;

public class UiCore {
	public Engine engine;
	
	public UiCore(Engine e) {
		engine = e;
	}
	
	public void step() {
		
	}	
	
	public void input(char c) {
		if(engine.game.getUiFocus() != null)
			engine.game.getUiFocus().input(c);
	}
	
	//Draws the ui to the specified FBO
	//In terms of screenspace, 0,0 = top left displayWidth,displayHeight = bottom right
	public void draw(float f, int width, int height) {
		/**Draw whatever Hud or Menu is currently active**/
		setUIProjection();
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		
		if(engine.game.menuOpen)
			engine.game.menu.draw(f, width, height);
		if(engine.game.consoleOpen)
			engine.game.console.draw(f, width, height);
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}
	
	public void setUIProjection() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0,engine.graphics.displayWidth,engine.graphics.displayHeight,0,0,1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}
}