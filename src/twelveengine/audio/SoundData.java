package twelveengine.audio;

import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alGenBuffers;

import com.flibitijibibo.flibitFile.FLACFile;

public class SoundData {
	public String file;
	
	public int bufferIndex;
	
	public SoundData(String s) {
		try {
			file = s;
			FLACFile fileIn;
			fileIn = new FLACFile(s); //TODO: flac file does not conform to FileUtil standards. fixit.		
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