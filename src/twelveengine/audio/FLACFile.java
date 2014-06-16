package twelveengine.audio;

/*
 * FLACFile: FLAC support for LWJGL OpenAL
 * © 2011 Ethan "flibitijibibo" Lee
 * http://www.flibitijibibo.com/
 * LWJGL: http://www.lwjgl.org/
 * jFLAC: http://jflac.sourceforge.net/
 * 
 * This Java class is free software.
 * You are free to use it and modify it for any purpose under the following conditions:
 * 1. This notice stays unmodified with the class, either in the header of the
 *    source code or in an accompanying text file (i.e. FLACFile_License.txt).
 * 2. You will credit me as the original author of this class on your project.
 * 3. This software shall be used for Good, not Evil.
 */

/** Edited this to be compliant with TwelveEngine Fileutil, Original author: Ethan Lee **/

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.PCMProcessor;
import org.kc7bfi.jflac.metadata.StreamInfo;
import org.kc7bfi.jflac.util.ByteData;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import twelveutil.FileUtil;

/**
 * @author Ethan "flibitijibibo" Lee
 */

public class FLACFile implements PCMProcessor {

	private ByteArrayOutputStream pcmByteArray;
	private ByteBuffer pcmData;
	private int format;
	private int sampleRate;
	
	/**
	 * Converts the FLAC file into PCM data and stores the data needed by alBufferData()
	 * @param filePath The location of the FLAC file in the asset tree
	 * @throws Exception If creating the PCM data fails
	 */
	public FLACFile(String filePath) throws Exception {
		// Create InputStream with filePath
		InputStream passedFile = FileUtil.getFile(filePath);
		// Read FLAC input
		FLACDecoder flacReader = new FLACDecoder(passedFile);
		// Begin decoding FLAC to PCM data
		pcmByteArray = new ByteArrayOutputStream();
		flacReader.addPCMProcessor(this);
		flacReader.decode();
		// Create ByteBuffer from ByteArrayOutputStream
		pcmData = BufferUtils.createByteBuffer(pcmByteArray.size());
		pcmData.put(pcmByteArray.toByteArray());
		pcmData.rewind();
		// Close FileInputStream
		passedFile.close();
	}
	
	/**
	 * When flacReader is decode()ing, this will be called to grab the file's info/metadata.
	 * You should NOT be using this method.
	 */
	public void processStreamInfo(StreamInfo passedTags) {
		// Store sample rate
		sampleRate = passedTags.getSampleRate();
		// Store OpenAL format (channel count/bit depth)
		int channelCount = passedTags.getChannels();
		int bitDepth = passedTags.getBitsPerSample();
		if (channelCount == 1) {
			if (bitDepth == 8)
				format = AL10.AL_FORMAT_MONO8;
			else
				format = AL10.AL_FORMAT_MONO16;
		} else {
			if (bitDepth == 8)
				format = AL10.AL_FORMAT_STEREO8;
			else
				format = AL10.AL_FORMAT_STEREO16;
		}
	}
	
	/**
	 * When flacReader is decode()ing, this will be called to grab the PCM byte[] blocks.
	 * You should NOT be using this method.
	 */
	public void processPCM(ByteData pcmInput) {
		pcmByteArray.write(pcmInput.getData(), 0, pcmInput.getLen());
	}
	
	/** Clears the PCM ByteBuffer. Use this after loading into OpenAL. */
	public void dispose() {
		pcmData.clear();
	}
	
	/** Returns the ByteBuffer needed by alBufferData() */
	public ByteBuffer getData() {
		return pcmData;
	}
	
	/** Returns the AL_FORMAT needed by alBufferData() */
	public int getFormat() {
		return format;
	}
	
	/** Returns the sample rate needed by alBufferData() */
	public int getSampleRate() {
		return sampleRate;
	}
}