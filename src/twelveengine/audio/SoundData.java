package twelveengine.audio;

import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alGenBuffers;

public class SoundData {
	public String file;
	
	public int bufferIndex;
	
	public SoundData(String s) {
		try {
			file = s;
			FLACFile fileIn;
			fileIn = new FLACFile(s);
			bufferIndex = alGenBuffers();
		    alBufferData(bufferIndex, fileIn.getFormat(), fileIn.getData(), fileIn.getSampleRate());
			
			fileIn.dispose();
		}
		catch(Exception e) {
			System.err.println("Failed to read FLAC file:" + file);
			e.printStackTrace();
		}
	}
}