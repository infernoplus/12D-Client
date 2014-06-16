/*
 * flibit2D Game Engine
 * © 2011 Ethan "flibitijibibo" Lee
 * http://www.flibitijibibo.com/
 * 
 * Sound
 * Each instance carries a sound (FLAC format) and its coordinates
 */

package twelveengine.audio;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.EFX10.*;
import twelveengine.data.Vertex;
import twelveutil.MathUtil;

public class Sound {

	/** Sound properties (location in BSP and location in buffer/source array */
	private Vertex location;
	public float radius;
	public float volume;
	private int soundBufferIndex;
	private int soundSourceIndex;
	
	/** Constructor loads the sound file into OpenAL and places it into the BSPGrid based on the passed XML data.
	 * @param fileName The name of the sound file in assets/audio/sound/, without .flac
	 * @param xCoord The x coordinate of the sound effect's location
	 * @param yCoord The y coordinate of the sound effect's location
	 * @param volume The starting volume of the sound effect
	 * @throws Exception If creating the Sound fails
	 */
	//Uses view space coordiantes for sound
	public Sound(SoundData snd, Vertex l, Vertex c, Vertex v, float r, float vol, boolean looping) throws Exception {
		// Assign the passed game and BSP coordinates
		radius = r;
		volume = vol;
	    // Create source
	    soundSourceIndex = alGenSources();
	    alSourcei(soundSourceIndex, AL_BUFFER, snd.bufferIndex);
	    // Set source properties
	    setVolume(volume);
	    setViewSpaceLocation(l,c,v);
		if (looping)
		    alSourcei(soundSourceIndex, AL_LOOPING, AL_TRUE);
	}
	
	//Uses world space coordiantes for sound.
	public Sound(SoundData snd, Vertex l, float v, boolean looping) throws Exception {
		// Assign the passed game and BSP coordinates
		volume = v;
	    // Create source
	    soundSourceIndex = alGenSources();
	    alSourcei(soundSourceIndex, AL_BUFFER, snd.bufferIndex);
	    // Set source properties
	    setVolume(volume);
	    setWorldSpaceLocation(l);
		if (looping)
		    alSourcei(soundSourceIndex, AL_LOOPING, AL_TRUE);
	}
	
	//Set the location of the sound in world space. For music and announcer "global" sounds.
	public void setWorldSpaceLocation(Vertex l) {
		location = l;
	}
	
	//Set the sounds location in viewspace. hard to explain but basically 3d sound space instead of just a world space coordinate.
	//Sort of like a viewspace transformation for sounds
	//Sound location l, Camera location c, Camera look v
	public void setViewSpaceLocation(Vertex l, Vertex c, Vertex v) {
		Vertex o = MathUtil.subtract(MathUtil.inverse(c), l);
		o.x = -o.x;
		o = MathUtil.rotateZ(o, (v.z) * 0.0174532925f);
		o = MathUtil.multiply(o, 1/radius);
		
		moveSound(o.x, o.y, o.z);
		setVolume(volume);
	}
	
	public void setSound(SoundData snd) {
		stopSound();
	    alSourcei(soundSourceIndex, AL_BUFFER, snd.bufferIndex);
	}
	
	/** Sets sound location and resulting velocity.
	 * @param newX The new x coordinate of the sound effect
	 * @param newY The new y coordinate of the sound effect
	 */
	public void moveSound(float newX, float newY, float newZ) {
		alSource3f(soundSourceIndex, AL_POSITION, newX, newY, newZ);
		alSource3f(soundSourceIndex, AL_VELOCITY, (newX - location.x) / 250f, (newY - location.y) / 250f, (newZ - location.z) / 250f);
		location = new Vertex(newX, newY, newZ);
	}
	
	/** Sets the volume of the music source.
	 * @param volume The volume percentage of the source
	 */
	public void setVolume(float volume) {
		alSourcef(soundSourceIndex, AL_GAIN, volume / 100.0f);
	}
	
	/** Checks to make sure it's still playing. */
	public boolean checkSound() {
		return (alGetSourcei(soundSourceIndex, AL_SOURCE_STATE) == AL_STOPPED);
	}
	
	/** Removes sound from buffer/source, releases buffer/sound array index. */
	public void killSound() {
		stopSound();
		alDeleteSources(soundSourceIndex);
		alDeleteBuffers(soundBufferIndex);
	}
	
	/** Plays the source of the sound effect. */
	public void playSound() {
		alSourcePlay(soundSourceIndex);
	}
	
	/** Stops the source of the sound effect. */
	public void stopSound() {
		alSourceStop(soundSourceIndex);
	}
	
	/** Pauses the source of the sound effect. */
	public void pauseSound() {
		alSourcePause(soundSourceIndex);
	}
	
	/** Adjusts the pitch of the sound effect.
	 * @param pitch The AL_PITCH modification to be applied
	 */
	public void setPitch(float pitch) {
		alSourcef(soundSourceIndex, AL_PITCH, pitch);
	}
	
	/** Applies an EFX effect and filter to the sound source.
	 * @param effectIndex The EFX10 effect index
	 * @param filterIndex The EFX10 filter index
	 */
	public void setEffect(int effectIndex, int filterIndex) {
		alSource3i(soundSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(soundSourceIndex, AL_DIRECT_FILTER, filterIndex);
	}
}