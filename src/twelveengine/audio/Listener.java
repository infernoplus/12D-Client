/*
 * flibit2D Game Engine
 * © 2011 Ethan "flibitijibibo" Lee
 * http://www.flibitijibibo.com/
 * 
 * Listener
 * Manipulates the position that the player is listening.
 */

package twelveengine.audio;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.openal.AL10.*;

public class Listener {

	/** Listener Orientation (first 3 elements are "at", second 3 are "up") */
	private FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f });
	
	/** Constructor sets the listener's orientation. */
	public Listener() {
		listenerOri.rewind();
	    alListener(AL_ORIENTATION, listenerOri);
	}
	
	/** Sets the volume of the game by manipulating the listener's gain.
	 * @param volume The volume of the game
	 */
	public void setVolume(int volume) {
	    alListenerf(AL_GAIN, volume);
	}
	
	/** Moves the listener to a new position, with no velocity.
	 * @param x The listener's new x coordinate
	 * @param y The listener's new y coordinate
	 */
	public void setPosition(float x, float y) {
		alListener3f(AL_POSITION, x, y, -1.0f);
		alListener3f(AL_VELOCITY, 0.0f, 0.0f, 0.0f);
	}
	
	/** Moves the listener to a new position and sets its new velocity.
	 * @param coords The new coordinates of the Listener
	 * @param velocityX The horizontal velocity of the Listener
	 * @param velocityY The vertical velocity of the Listener
	 */
	public void moveListener(float[] coords, float velocityX, float velocityY) {
		alListener3f(AL_POSITION, coords[0], coords[1], -1.0f);
	    alListener3f(AL_VELOCITY, velocityX / 250, velocityY / 250, 0.0f);
	}
}