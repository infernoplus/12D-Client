package twelveengine.graphics;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import twelveengine.Log;
import twelveutil.FileUtil;

public class AnimationGroup {
	public String file;
	public Animation[] animations;
	public AnimationGroup(String f) {
		file = f;
		try {
			tafImport(f);
			Log.log("AnimationGroup" + f + " built sucsessfully.", "Animation");
		} catch (IOException e) {
			Log.log("AnimationGroup" + f + " failed to build.", "Animation", 2);
			e.printStackTrace();
		}
	}
	
	public void tafImport(String s) throws IOException {
		String currentLine;
		DataInputStream fileIn = new DataInputStream(FileUtil.getFile(s));
	    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));
	    
	    int i = 0;
	    
	    currentLine=fileReader.readLine();
	    while(currentLine != null) {
	    	if(currentLine.startsWith("begin "))
	    		i++;
		    currentLine=fileReader.readLine();
	    }
	
	    animations = new Animation[i];
	    i = 0;
	    int j = 0;
	    
		fileIn = new DataInputStream(FileUtil.getFile(s));
	    fileReader = new BufferedReader(new InputStreamReader(fileIn));
	    
	    currentLine=fileReader.readLine();
	    while(currentLine != null) {
	    	if(currentLine.startsWith("begin ")) {
	    		String name = currentLine.split(" ")[1];
	    		ArrayList<AnimationFrame> nfr = new ArrayList<AnimationFrame>();
			    currentLine=fileReader.readLine();
	    		while(!currentLine.startsWith("end")) {
	    			nfr.add(new AnimationFrame(currentLine));
				    currentLine=fileReader.readLine();
		    		i++;
	    		}
	    		i = 0;
	    		AnimationFrame afrms[] = new AnimationFrame[nfr.size()];
	    		while(i < nfr.size()) {
	    			afrms[i] = nfr.get(i);
	    			i++;
	    		}
	    		animations[j] = new Animation(name, afrms);
	    	    i = 0;
		    	j++;
	    	}
		    currentLine=fileReader.readLine();
	    }	    
	}

	public Animation getAnimation(String s) {
		int i = 0;
		while(i < animations.length) {
			if(animations[i].name.equals(s)) {
				return animations[i];
			}
			i++;
		}
		return animations[0];
	}
}