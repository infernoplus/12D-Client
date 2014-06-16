/*
 * flibit2D Game Engine
 * © 2011 Ethan "flibitijibibo" Lee
 * http://www.flibitijibibo.com/
 * 
 * Music
 * Each instance carries a music file (FLAC format) and its name.
 */

package twelveengine.audio;

import java.nio.ByteBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.EFX10.*;

import com.flibitijibibo.flibitFile.FLACFile;

public class Music {
	
	/** Stock music properties */
	private int musicBufferIndex;
	private int musicSourceIndex;
	private int musicSize;
	private String songName;
	
	/** Reverse music properties */
	private int reverseBufferIndex;
	private int reverseSourceIndex;
	private boolean isReverse;
	
	/** Flanging music properties */
	private int flangeSourceIndex;
	private int revFlangeSourceIndex;
	private int flangeOffset;
	private int flangeMS;
	private boolean flangeForward;
	private boolean isFlanging;
	
	/** Wahwah music properties */
	private int wahBufferIndex;
	private int revWahBufferIndex;
	private int wahSourceIndex;
	private int revWahSourceIndex;
	private boolean isWah;
	
	/** Distorted music properties */
	private int distortBufferIndex;
	private int revDistortBufferIndex;
	private int distortSourceIndex;
	private int revDistortSourceIndex;
	private boolean isDistorted;
	
	/** Flanging and Wahwah combined */
	private int wahFlangeSourceIndex;
	private int revWahFlangeSourceIndex;
	
	/** Flanging and Distortion combined */
	private int distortFlangeSourceIndex;
	private int revDistortFlangeSourceIndex;
	
	/** Wahwah and Distortion combined */
	private int wahDistortBufferIndex;
	private int revWahDistortBufferIndex;
	private int wahDistortSourceIndex;
	private int revWahDistortSourceIndex;
	
	/** Flanging Wahwah Distortion S-S-S-SUPER combination! */
	private int wahDistortFlangeSourceIndex;
	private int revWahDistortFlangeSourceIndex;
	
	/** Loads the music into OpenAL and plays it.
	 * @param Level The name of the music file in assets/audio/music/, without .flac
	 * @throws Exception If creating the Music fails
	 */
	public Music(String Level, float volume) throws Exception {
		songName = Level;
		isReverse = false;
		flangeForward = true;
		isFlanging = false;
		isWah = false;
		isDistorted = false;
		// Load music file
		FLACFile fileIn = new FLACFile("audio/music/" + Level + ".flac");
		
		// Create the reverse form
		ByteBuffer forwardBuffer = fileIn.getData();
		musicSize = forwardBuffer.limit();
		ByteBuffer reverseBuffer = FilterProcessing.createReverseData(forwardBuffer);
		// Create the wah'd forms
		ByteBuffer wahBuffer = FilterProcessing.createWahData(forwardBuffer, fileIn.getSampleRate());
		ByteBuffer revWahBuffer = FilterProcessing.createReverseData(wahBuffer);
		// Create the distorted forms
		ByteBuffer distortBuffer = FilterProcessing.createDistortionData(forwardBuffer, fileIn.getSampleRate());
		ByteBuffer revDistortBuffer = FilterProcessing.createReverseData(distortBuffer);
		// Create the distorted wah forms
		ByteBuffer wahDistortBuffer = FilterProcessing.createDistortionData(wahBuffer, fileIn.getSampleRate());
		ByteBuffer revWahDistortBuffer = FilterProcessing.createReverseData(wahDistortBuffer);
		
		// Get the flanger data
		flangeMS = fileIn.getSampleRate() / 1000;
		flangeOffset = flangeMS * 10;
		
		// Create the AL buffers
		musicBufferIndex = alGenBuffers();
		reverseBufferIndex = alGenBuffers();
		wahBufferIndex = alGenBuffers();
		revWahBufferIndex = alGenBuffers();
		distortBufferIndex = alGenBuffers();
		revDistortBufferIndex = alGenBuffers();
		wahDistortBufferIndex = alGenBuffers();
		revWahDistortBufferIndex = alGenBuffers();
	    alBufferData(musicBufferIndex, fileIn.getFormat(), fileIn.getData(), fileIn.getSampleRate());
	    alBufferData(reverseBufferIndex, fileIn.getFormat(), reverseBuffer, fileIn.getSampleRate());
	    alBufferData(wahBufferIndex, fileIn.getFormat(), wahBuffer, fileIn.getSampleRate());
	    alBufferData(revWahBufferIndex, fileIn.getFormat(), revWahBuffer, fileIn.getSampleRate());
	    alBufferData(distortBufferIndex, fileIn.getFormat(), distortBuffer, fileIn.getSampleRate());
	    alBufferData(revDistortBufferIndex, fileIn.getFormat(), revDistortBuffer, fileIn.getSampleRate());
	    alBufferData(wahDistortBufferIndex, fileIn.getFormat(), wahDistortBuffer, fileIn.getSampleRate());
	    alBufferData(revWahDistortBufferIndex, fileIn.getFormat(), revWahDistortBuffer, fileIn.getSampleRate());
	    
	    // Clear the ByteBuffers
	    fileIn.dispose();
	    reverseBuffer.clear();
	    wahBuffer.clear();
	    revWahBuffer.clear();
	    distortBuffer.clear();
	    revDistortBuffer.clear();
	    wahDistortBuffer.clear();
	    revWahDistortBuffer.clear();
	    
	    // Create sources
	    musicSourceIndex = alGenSources();
	    reverseSourceIndex = alGenSources();
	    flangeSourceIndex = alGenSources();
	    revFlangeSourceIndex = alGenSources();
	    wahSourceIndex = alGenSources();
	    revWahSourceIndex = alGenSources();
	    wahFlangeSourceIndex = alGenSources();
	    revWahFlangeSourceIndex = alGenSources();
		distortSourceIndex = alGenSources();
		revDistortSourceIndex = alGenSources();
	    distortFlangeSourceIndex = alGenSources();
	    revDistortFlangeSourceIndex = alGenSources();
		wahDistortSourceIndex = alGenSources();
		revWahDistortSourceIndex = alGenSources();
		wahDistortFlangeSourceIndex = alGenSources();
		revWahDistortFlangeSourceIndex = alGenSources();
	    alSourcei(musicSourceIndex, AL_BUFFER, musicBufferIndex);
	    alSourcei(reverseSourceIndex, AL_BUFFER, reverseBufferIndex);
	    alSourcei(flangeSourceIndex, AL_BUFFER, musicBufferIndex);
	    alSourcei(revFlangeSourceIndex, AL_BUFFER, reverseBufferIndex);
	    alSourcei(wahSourceIndex, AL_BUFFER, wahBufferIndex);
	    alSourcei(revWahSourceIndex, AL_BUFFER, revWahBufferIndex);
	    alSourcei(wahFlangeSourceIndex, AL_BUFFER, wahBufferIndex);
	    alSourcei(revWahFlangeSourceIndex, AL_BUFFER, revWahBufferIndex);
	    alSourcei(distortSourceIndex, AL_BUFFER, distortBufferIndex);
	    alSourcei(revDistortSourceIndex, AL_BUFFER, revDistortBufferIndex);
	    alSourcei(distortFlangeSourceIndex, AL_BUFFER, distortBufferIndex);
	    alSourcei(revDistortFlangeSourceIndex, AL_BUFFER, revDistortBufferIndex);
	    alSourcei(wahDistortSourceIndex, AL_BUFFER, wahDistortBufferIndex);
	    alSourcei(revWahDistortSourceIndex, AL_BUFFER, revWahDistortBufferIndex);
	    alSourcei(wahDistortFlangeSourceIndex, AL_BUFFER, wahDistortBufferIndex);
	    alSourcei(revWahDistortFlangeSourceIndex, AL_BUFFER, revWahDistortBufferIndex);
	    
	    // Set music source properties
	    setVolume(volume);
	    alSourcei(musicSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(flangeSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(reverseSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(revFlangeSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(wahSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(revWahSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(wahFlangeSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(revWahFlangeSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(distortSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(revDistortSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(distortFlangeSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(revDistortFlangeSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(wahDistortSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(revWahDistortSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(wahDistortFlangeSourceIndex, AL_LOOPING, AL_TRUE);
	    alSourcei(revWahDistortFlangeSourceIndex, AL_LOOPING, AL_TRUE);
	    playMusic();
	}
	
	/** Unloads the music from OpenAL. */
	public void killMusic() {
		// Stop the sources
		alSourceStop(musicSourceIndex);
		alSourceStop(reverseSourceIndex);
		alSourceStop(flangeSourceIndex);
		alSourceStop(revFlangeSourceIndex);
		alSourceStop(wahSourceIndex);
		alSourceStop(revWahSourceIndex);
		alSourceStop(wahFlangeSourceIndex);
		alSourceStop(revWahFlangeSourceIndex);
		alSourceStop(distortSourceIndex);
		alSourceStop(revDistortSourceIndex);
		alSourceStop(distortFlangeSourceIndex);
		alSourceStop(revDistortFlangeSourceIndex);
		alSourceStop(wahDistortSourceIndex);
		alSourceStop(revWahDistortSourceIndex);
		alSourceStop(wahDistortFlangeSourceIndex);
		alSourceStop(revWahDistortFlangeSourceIndex);
		// Delete the sources
		alDeleteSources(musicSourceIndex);
		alDeleteSources(reverseSourceIndex);
		alDeleteSources(flangeSourceIndex);
		alDeleteSources(revFlangeSourceIndex);
		alDeleteSources(wahSourceIndex);
		alDeleteSources(revWahSourceIndex);
		alDeleteSources(wahFlangeSourceIndex);
		alDeleteSources(revWahFlangeSourceIndex);
		alDeleteSources(distortSourceIndex);
		alDeleteSources(revDistortSourceIndex);
		alDeleteSources(distortFlangeSourceIndex);
		alDeleteSources(revDistortFlangeSourceIndex);
		alDeleteSources(wahDistortSourceIndex);
		alDeleteSources(revWahDistortSourceIndex);
		alDeleteSources(wahDistortFlangeSourceIndex);
		alDeleteSources(revWahDistortFlangeSourceIndex);
		// Delete the buffers
		alDeleteBuffers(musicBufferIndex);
		alDeleteBuffers(reverseBufferIndex);
		alDeleteBuffers(wahBufferIndex);
		alDeleteBuffers(revWahBufferIndex);
		alDeleteBuffers(distortBufferIndex);
		alDeleteBuffers(revDistortBufferIndex);
		alDeleteBuffers(wahDistortBufferIndex);
		alDeleteBuffers(revWahDistortBufferIndex);
	}
	
	/** Sets the volume of the music source.
	 * @param volume The volume percentage of the source
	 */
	public void setVolume(float volume) {
	    alSourcef(musicSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(reverseSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(flangeSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(revFlangeSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(wahSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(revWahSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(wahFlangeSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(revWahFlangeSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(distortSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(revDistortSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(distortFlangeSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(revDistortFlangeSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(wahDistortSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(revWahDistortSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(wahDistortFlangeSourceIndex, AL_GAIN, volume / 100.0f);
	    alSourcef(revWahDistortFlangeSourceIndex, AL_GAIN, volume / 100.0f);
	}
	
	/** Plays the music source. */
	public void playMusic() {
		if (!isReverse) {
			if (!isWah && !isDistorted)
				alSourcePlay(musicSourceIndex);
			if (isWah && isDistorted)
				alSourcePlay(wahDistortSourceIndex);
			else if (isWah)
				alSourcePlay(wahSourceIndex);
			else if (isDistorted)
				alSourcePlay(distortSourceIndex);
			if (isFlanging) {
				if (!isWah && !isDistorted)
					alSourcePlay(flangeSourceIndex);
				if (isWah && isDistorted)
					alSourcePlay(wahDistortFlangeSourceIndex);
				else if (isWah)
					alSourcePlay(wahFlangeSourceIndex);
				else if (isDistorted)
					alSourcePlay(distortFlangeSourceIndex);
			}
		}
		else {
			if (!isWah && !isDistorted)
				alSourcePlay(reverseSourceIndex);
			else if (isWah && isDistorted)
				alSourcePlay(revWahDistortSourceIndex);
			else if (isWah)
				alSourcePlay(revWahSourceIndex);
			else if (isDistorted)
				alSourcePlay(revDistortSourceIndex);
			if (isFlanging) {
				if (!isWah && !isDistorted)
					alSourcePlay(revFlangeSourceIndex);
				if (isWah && isDistorted)
					alSourcePlay(revWahDistortFlangeSourceIndex);
				else if (isWah)
					alSourcePlay(revWahFlangeSourceIndex);
				else if (isDistorted)
					alSourcePlay(revDistortFlangeSourceIndex);
			}
		}
	}
	
	/** Pauses the music source. */
	public void pauseMusic() {
		alSourcePause(musicSourceIndex);
		alSourcePause(reverseSourceIndex);
		alSourcePause(flangeSourceIndex);
		alSourcePause(revFlangeSourceIndex);
		alSourcePause(wahSourceIndex);
		alSourcePause(revWahSourceIndex);
		alSourcePause(wahFlangeSourceIndex);
		alSourcePause(revWahFlangeSourceIndex);
		alSourcePause(distortSourceIndex);
		alSourcePause(revDistortSourceIndex);
		alSourcePause(distortFlangeSourceIndex);
		alSourcePause(revDistortFlangeSourceIndex);
		alSourcePause(wahDistortSourceIndex);
		alSourcePause(revWahDistortSourceIndex);
		alSourcePause(wahDistortFlangeSourceIndex);
		alSourcePause(revWahDistortFlangeSourceIndex);
	}
	
	/** Sets the music to play forward or backward.
	 * @param reverse Whether or not the music should be in reverse
	 */
	public void setReverse(boolean reverse) {
		if (isReverse != reverse) {
			isReverse = reverse;
			pauseMusic();
			int position;
			if (isReverse) {
				if (!isWah && !isDistorted) {
					position = alGetSourcei(musicSourceIndex, AL_BYTE_OFFSET);
					alSourcei(reverseSourceIndex, AL_BYTE_OFFSET, musicSize - position);
				}
				else if (isWah && isDistorted) {
					position = alGetSourcei(wahDistortSourceIndex, AL_BYTE_OFFSET);
					alSourcei(revWahDistortSourceIndex, AL_BYTE_OFFSET, musicSize - position);
				}
				else if (isWah) {
					position = alGetSourcei(wahSourceIndex, AL_BYTE_OFFSET);
					alSourcei(revWahSourceIndex, AL_BYTE_OFFSET, musicSize - position);
				}
				else if (isDistorted) {
					position = alGetSourcei(distortSourceIndex, AL_BYTE_OFFSET);
					alSourcei(revDistortSourceIndex, AL_BYTE_OFFSET, musicSize - position);
				}
			}
			else {
				if (!isWah && !isDistorted) {
					position = alGetSourcei(reverseSourceIndex, AL_BYTE_OFFSET);
					alSourcei(musicSourceIndex, AL_BYTE_OFFSET, musicSize - position);
				}
				else if (isWah && isDistorted) {
					position = alGetSourcei(revWahDistortSourceIndex, AL_BYTE_OFFSET);
					alSourcei(wahDistortSourceIndex, AL_BYTE_OFFSET, musicSize - position);
				}
				else if (isWah) {
					position = alGetSourcei(revWahSourceIndex, AL_BYTE_OFFSET);
					alSourcei(wahSourceIndex, AL_BYTE_OFFSET, musicSize - position);
				}
				else if (isDistorted) {
					position = alGetSourcei(revDistortSourceIndex, AL_BYTE_OFFSET);
					alSourcei(distortSourceIndex, AL_BYTE_OFFSET, musicSize - position);
				}
			}
			playMusic();
		}
	}
	
	/** Enables/disables the flanging track.
	 * @param flanging Whether or not a flanging effect should be heard
	 */
	public void setFlanging(boolean flanging) {
		if (isFlanging != flanging) {
			pauseMusic();
			isFlanging = flanging;
			playMusic();
		}
	}
	
	/** Sets the spot of the flanging track. */
	public void setFlangeSpot() {
		int position;
		if (!isReverse) {
			if (!isWah && !isDistorted) {
				position = alGetSourcei(musicSourceIndex, AL_SAMPLE_OFFSET);
				alSourcei(flangeSourceIndex, AL_SAMPLE_OFFSET, position - flangeOffset);
			}
			else if (isWah && isDistorted) {
				position = alGetSourcei(wahDistortSourceIndex, AL_SAMPLE_OFFSET);
				alSourcei(wahDistortFlangeSourceIndex, AL_SAMPLE_OFFSET, position - flangeOffset);
			}
			else if (isWah) {
				position = alGetSourcei(wahSourceIndex, AL_SAMPLE_OFFSET);
				alSourcei(wahFlangeSourceIndex, AL_SAMPLE_OFFSET, position - flangeOffset);
			}
			else if (isDistorted) {
				position = alGetSourcei(distortSourceIndex, AL_SAMPLE_OFFSET);
				alSourcei(distortFlangeSourceIndex, AL_SAMPLE_OFFSET, position - flangeOffset);
			}
		}
		else {
			if (!isWah && !isDistorted) {
				position = alGetSourcei(reverseSourceIndex, AL_SAMPLE_OFFSET);
				alSourcei(revFlangeSourceIndex, AL_SAMPLE_OFFSET, position - flangeOffset);
			}
			else if (isWah && isDistorted) {
				position = alGetSourcei(revWahDistortSourceIndex, AL_SAMPLE_OFFSET);
				alSourcei(revWahDistortFlangeSourceIndex, AL_SAMPLE_OFFSET, position - flangeOffset);
			}
			else if (isWah) {
				position = alGetSourcei(revWahSourceIndex, AL_SAMPLE_OFFSET);
				alSourcei(revWahFlangeSourceIndex, AL_SAMPLE_OFFSET, position - flangeOffset);
			}
			else if (isDistorted) {
				position = alGetSourcei(revDistortSourceIndex, AL_SAMPLE_OFFSET);
				alSourcei(revDistortFlangeSourceIndex, AL_SAMPLE_OFFSET, position - flangeOffset);
			}
		}
		if (flangeForward) {
			if (++flangeOffset >= flangeMS * 20)
				flangeForward = false;
		}
		else {
			if (--flangeOffset <=  flangeMS * 10)
				flangeForward = true;
		}
	}
	
	/** Enables/disables the wahwah track.
	 * @param wah Whether or not a wahwah effect should be heard
	 */
	public void setWah(boolean wah) {
		if (isWah != wah) {
			isWah = wah;
			pauseMusic();
			int position;
			if (wah) {
				if (!isReverse && !isDistorted) {
					position = alGetSourcei(musicSourceIndex, AL_BYTE_OFFSET);
					alSourcei(wahSourceIndex, AL_BYTE_OFFSET, position);
				}
				else if (isReverse && !isDistorted) {
					position = alGetSourcei(reverseSourceIndex, AL_BYTE_OFFSET);
					alSourcei(revWahSourceIndex, AL_BYTE_OFFSET, position);
				}
				if (!isReverse && isDistorted) {
					position = alGetSourcei(distortSourceIndex, AL_BYTE_OFFSET);
					alSourcei(wahDistortSourceIndex, AL_BYTE_OFFSET, position);
				}
				else if (isReverse && isDistorted) {
					position = alGetSourcei(revDistortSourceIndex, AL_BYTE_OFFSET);
					alSourcei(revWahDistortSourceIndex, AL_BYTE_OFFSET, position);
				}
			}
			else {
				if (!isReverse && !isDistorted) {
					position = alGetSourcei(wahSourceIndex, AL_BYTE_OFFSET);
					alSourcei(musicSourceIndex, AL_BYTE_OFFSET, position);
				}
				else if (isReverse && !isDistorted) {
					position = alGetSourcei(revWahSourceIndex, AL_BYTE_OFFSET);
					alSourcei(reverseSourceIndex, AL_BYTE_OFFSET, position);
				}
				if (!isReverse && isDistorted) {
					position = alGetSourcei(wahDistortSourceIndex, AL_BYTE_OFFSET);
					alSourcei(distortSourceIndex, AL_BYTE_OFFSET, position);
				}
				else if (isReverse && isDistorted) {
					position = alGetSourcei(revWahDistortSourceIndex, AL_BYTE_OFFSET);
					alSourcei(revDistortSourceIndex, AL_BYTE_OFFSET, position);
				}
			}
			playMusic();
		}
	}
	
	/** Enables/disables the distortion track.
	 * @param distorted Whether or not the music should be distorted
	 */
	public void setDistortion(boolean distorted) {
		if (isDistorted != distorted) {
			isDistorted = distorted;
			pauseMusic();
			int position;
			if (distorted) {
				if (!isReverse && !isWah) {
					position = alGetSourcei(musicSourceIndex, AL_BYTE_OFFSET);
					alSourcei(distortSourceIndex, AL_BYTE_OFFSET, position);
				}
				else if (isReverse && !isWah) {
					position = alGetSourcei(reverseSourceIndex, AL_BYTE_OFFSET);
					alSourcei(revDistortSourceIndex, AL_BYTE_OFFSET, position);
				}
				if (!isReverse && isWah) {
					position = alGetSourcei(wahSourceIndex, AL_BYTE_OFFSET);
					alSourcei(wahDistortSourceIndex, AL_BYTE_OFFSET, position);
				}
				else if (isReverse && isWah) {
					position = alGetSourcei(revWahSourceIndex, AL_BYTE_OFFSET);
					alSourcei(revWahDistortSourceIndex, AL_BYTE_OFFSET, position);
				}
			}
			else {
				if (!isReverse && !isWah) {
					position = alGetSourcei(distortSourceIndex, AL_BYTE_OFFSET);
					alSourcei(musicSourceIndex, AL_BYTE_OFFSET, position);
				}
				else if (isReverse && !isWah) {
					position = alGetSourcei(revDistortSourceIndex, AL_BYTE_OFFSET);
					alSourcei(reverseSourceIndex, AL_BYTE_OFFSET, position);
				}
				if (!isReverse && isWah) {
					position = alGetSourcei(wahDistortSourceIndex, AL_BYTE_OFFSET);
					alSourcei(wahSourceIndex, AL_BYTE_OFFSET, position);
				}
				else if (isReverse && isWah) {
					position = alGetSourcei(revWahDistortSourceIndex, AL_BYTE_OFFSET);
					alSourcei(revWahSourceIndex, AL_BYTE_OFFSET, position);
				}
			}
			playMusic();
		}
	}
	
	/** Adjusts the pitch of the music source.
	 * @param pitch The AL_PITCH modification to be applied
	 */
	public void setPitch(float pitch) {
		alSourcef(musicSourceIndex, AL_PITCH, pitch);
		alSourcef(reverseSourceIndex, AL_PITCH, pitch);
		alSourcef(flangeSourceIndex, AL_PITCH, pitch);
		alSourcef(revFlangeSourceIndex, AL_PITCH, pitch);
		alSourcef(wahSourceIndex, AL_PITCH, pitch);
		alSourcef(revWahSourceIndex, AL_PITCH, pitch);
		alSourcef(wahFlangeSourceIndex, AL_PITCH, pitch);
		alSourcef(revWahFlangeSourceIndex, AL_PITCH, pitch);
		alSourcef(distortSourceIndex, AL_PITCH, pitch);
		alSourcef(revDistortSourceIndex, AL_PITCH, pitch);
		alSourcef(distortFlangeSourceIndex, AL_PITCH, pitch);
		alSourcef(revDistortFlangeSourceIndex, AL_PITCH, pitch);
		alSourcef(wahDistortSourceIndex, AL_PITCH, pitch);
		alSourcef(revWahDistortSourceIndex, AL_PITCH, pitch);
		alSourcef(wahDistortFlangeSourceIndex, AL_PITCH, pitch);
		alSourcef(revWahDistortFlangeSourceIndex, AL_PITCH, pitch);
	}
	
	/** Returns the name of the current song. */
	public String getName() {
		return songName;
	}
	
	/** Applies an EFX effect and filter to the music source.
	 * @param effectIndex The EFX10 effect index
	 * @param filterIndex The EFX10 filter index
	 */
	public void setEffect(int effectIndex, int filterIndex) {
		alSource3i(musicSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(musicSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(reverseSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(reverseSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(flangeSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(flangeSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(revFlangeSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(revFlangeSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(wahSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(wahSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(revWahSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(revWahSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(wahFlangeSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(wahFlangeSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(revWahFlangeSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(revWahFlangeSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(distortSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(distortSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(revDistortSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(revDistortSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(distortFlangeSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(distortFlangeSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(revDistortFlangeSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(revDistortFlangeSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(wahDistortSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(wahDistortSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(revWahDistortSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(revWahDistortSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(wahDistortFlangeSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(wahDistortFlangeSourceIndex, AL_DIRECT_FILTER, filterIndex);
		alSource3i(revWahDistortFlangeSourceIndex, AL_AUXILIARY_SEND_FILTER, effectIndex, 0, AL_FILTER_NULL);
		alSourcei(revWahDistortFlangeSourceIndex, AL_DIRECT_FILTER, filterIndex);
	}
}