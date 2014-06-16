/*
 * 12characters Snake Engine
 * © 2011 12characters Games
 * http://www.12charactersengines.com/
 * 
 * AudioCore
 * Renders sound files (FLAC format) as music and positional sound
 */

package twelveengine.audio;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

import twelveengine.Engine;
import twelveengine.Log;
import twelveengine.Settings;

public class AudioCore {

	/** Passed Engine instance */
	private Engine engine;
	
	/** Audio settings variables */
	private int musicVolume;
	private int soundVolume;
	
	/** Music variable */
	private Music music;

	/** Listener variable */
	public Listener listener;
	
	/** Constructor for the AudioCore. Creates the OpenAL sound system.
	 * @param passedGame The Engine instance
	 */
	public AudioCore(Engine passedGame){
		Log.log("Initializing OpenAL...", "Audio");
		engine = passedGame;
		musicVolume = Settings.getInt("musicVolume");
		soundVolume = Settings.getInt("soundVolume");
		// Load OpenAL
		try {
			AL.create();
			// Clear error buffer
			alGetError();
			// Get audio device information
			Log.log("Loaded OpenAL device: " + alcGetString(AL.getDevice(), ALC_DEVICE_SPECIFIER), "Audio");
			Log.log("OpenAL Renderer: " + alGetString(AL_RENDERER) + ", Version " + alGetString(AL_VERSION), "Audio");
			Log.log("OpenAL Vendor: " + alGetString(AL_VENDOR), "Audio");
		} catch (LWJGLException e) {
		      e.printStackTrace();
			  System.exit(0);
		}
		Log.log("Setting OpenAL features...", "Audio");
		// Initialize Listener
		listener = new Listener();
	    // Adjust volume/distance attenuation for BSP
	    listener.setVolume(100);
	    alDistanceModel(AL_INVERSE_DISTANCE_CLAMPED);
	    Log.log(" Done!", "Audio");
	    Log.log("OpenAL started!", "Audio");
	}
	
	/** Ends the OpenAL sound system. */
	public void endAudio() {
		if (music != null) {
			Log.log("Stopping music...", "Audio");
			killMusic();
		}
		Log.log("Stopping OpenAL...", "Audio");
		AL.destroy();
		Log.log(" Stopped!", "Audio");
	}
	
	/** Updates the sound effects. */
	public void renderAudio() {
		engine.game.checkSoundArray();
	}
	
	/** Pauses the music, if it exists, and the sounds in the BSP. */
	public void pauseGameSounds() {
		if (music != null)
			music.setVolume(musicVolume / 2);
		engine.game.pauseSounds();
	}
	
	/** Resumes the music, if it exists, and the sounds in the BSP. */
	public void resumeGameSounds() {
		if (music != null)
			music.setVolume(musicVolume);
		engine.game.resumeSounds();
	}
	
	/** Adjusts the volume of the music.
	 * @param newMusicVolume The new volume of the music.
	 */
	public void setMusicVolume(int newMusicVolume) {
		musicVolume = newMusicVolume;
		music.setVolume(musicVolume);
	}
	
	/** Adjusts the volume of the sound.
	 * @param newSoundVolume The new volume of the sound.
	 */
	public void setSoundVolume(int newSoundVolume) {
		soundVolume = newSoundVolume;
		engine.game.adjustSoundVolume();
	}
	
	/** Returns the volume the sounds should be. */
	public int getSoundVolume() {
		return soundVolume;
	}
	
	/** Creates a new music instance, if needed.
	 * @param Level The file name of the music file in assets/audio/music/
	 */
	public void loadMusic(String Level) {
		try {
			if (music == null)
				music = new Music(Level, musicVolume);
			else if (music != null && !Level.equals(music.getName())) {
				killMusic();
				music = new Music(Level, musicVolume);
			}
		} catch(Exception e) {
			System.err.println("~SOUND ERROR~");
			e.printStackTrace();
		}
	}
	
	/** Unloads the music instance. */
	public void killMusic() {
		music.killMusic();
		music = null;
	}
}