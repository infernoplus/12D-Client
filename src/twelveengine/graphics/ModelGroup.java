package twelveengine.graphics;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import twelveengine.Game;
import twelveengine.Log;
import twelveengine.data.Quat;
import twelveengine.data.Vertex;
import twelveutil.FileUtil;
import twelveutil.MathUtil;


//A ModelGroup is just what the name implies. It is a Model and it's different lods (levels of detail). 
//When you call pushToDrawQueue() ModelGroup will automatically check the distance to the model and pick a level of detail to use. Don't worry.
//If you are wondering why there are 8 versions of pushToDrawQueue(), there are comments explaining the difference between the different versions on top of each method.

public class ModelGroup {
	public Game game;
	
	public String file;
	
	public Model models[];
	public float distances[];
	
	public ModelGroup(Game w, String s) {
		game = w;
		file = s;
		
		try {
			create(s);
		} catch (Exception e) {
		    Log.log("Failed to build ModelGroup: " + s, "Model", 2);
			e.printStackTrace();
		}
	}
	
	public void create(String s) throws Exception {
		String currentLine;
		DataInputStream fileIn = new DataInputStream(FileUtil.getFile(s));
	    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));
	    
	    ArrayList<String> l = new ArrayList<String>();
	    ArrayList<String> d = new ArrayList<String>();
	    int i = 0;
	    int k = 0;
	    
	    currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
	    while(currentLine != null) {
	    	if(currentLine.startsWith("lod")) {
	    		l.add(currentLine);
	    		i++;
	    	}
	    	if(currentLine.startsWith("distance")) {
	    		d.add(currentLine);
	    		k++;
	    	}
		    currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
	    }
	    
	    models = new Model[i];
	    distances = new float[k];
	    
	    i = 0;
	    while(i < l.size()) {
	    	int j = Integer.parseInt(l.get(i).charAt(3) + "");
	    	String f = l.get(i).split("=")[1];
	    	models[j] = game.getModel(f);
	    	i++;
	    }
	    i = 0;
	    while(i < d.size()) {
	    	int j = Integer.parseInt(d.get(i).charAt(8) + "");
	    	float dis = Float.parseFloat(d.get(i).split("=")[1]);
	    	distances[j] = dis;
	    	i++;
	    }
	    
	    Log.log("Generated model group: " + s, "Model");
	}
	
	//Returns the highest detail model (the first lod)
	public Model getModel() {
		return models[0];
	}
	
	//Returns the specified lod model
	public Model getLod(int i) {
		if(i < models.length)
			return models[i];
		else
			return models[models.length-1];
	}
	
	 //TODO: account for FOV so that things don't get weird when zooming or when using custom FOV setting.
	public float getDistance(Vertex l) {
		float d = MathUtil.length(game.player.location, l);
		return d;
	}
	
	/** WITHOUT ANIMATION! **/
	
	//Use the given radius (ra) here for culling. 
	//Animated models usually need their radius set manually seeing as their calculated radius is not going to be accurate during animation.
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, float scale, float ra) {
		float dis = getDistance(l);
		
		if(distances.length > 0) {
			int i = 0;
			while(i < distances.length) {
				if(dis < distances[i]) {
					models[i].pushToDrawQueue(meshes, l, r, scale, ra);
					return;
				}
				i++;
			}
			return; //The model is farther away than the maximum lod distance then we don't render it.
		}
		else {
			models[0].pushToDrawQueue(meshes, l, r, scale, ra);
		}
	}

	//Use the models radius here for culling
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, float scale) {
		float dis = getDistance(l);
		
		if(distances.length > 0) {
			int i = 0;
			while(i < distances.length) {
				if(dis < distances[i]) {
					models[i].pushToDrawQueue(meshes, l, r, scale);
					return;
				}
				i++;
			}
			return; //The model is farther away than the maximum lod distance then we don't render it.
		}
		else {
			models[0].pushToDrawQueue(meshes, l, r, scale);
		}
	}
	
	//Uses given radius (ra) for culling and applies scalar values to shaders for the drawing of this model. Information on scalars in Shader.java.
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, float scale, float ra, float scalar[]) {
		float dis = getDistance(l);
		
		if(distances.length > 0) {
			int i = 0;
			while(i < distances.length) {
				if(dis < distances[i]) {
					models[i].pushToDrawQueue(meshes, l, r, scale, ra, scalar);
					return;
				}
				i++;
			}
			return; //The model is farther away than the maximum lod distance then we don't render it.
		}
		else {
			models[0].pushToDrawQueue(meshes, l, r, scale, ra, scalar);
		}
	}
	
	//Uses models radius for culling and applies scalar values to shaders for the drawing of this model. Information on scalars in Shader.java.
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, float scale, float scalar[]) {
		float dis = getDistance(l);
		
		if(distances.length > 0) {
			int i = 0;
			while(i < distances.length) {
				if(dis < distances[i]) {
					models[i].pushToDrawQueue(meshes, l, r, scale, scalar);
					return;
				}
				i++;
			}
			return; //The model is farther away than the maximum lod distance then we don't render it.
		}
		else {
			models[0].pushToDrawQueue(meshes, l, r, scale, scalar);
		}
	}
	
	/** WITH ANIMATION! **/
	
	//Use the given radius (ra) here for culling. 
	//Animated models usually need their radius set manually seeing as their calculated radius is not going to be accurate during animation.
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, AnimationFrame frm, float scale, float ra) {
		float dis = getDistance(l);
		
		if(distances.length > 0) {
			int i = 0;
			while(i < distances.length) {
				if(dis < distances[i]) {
					models[i].pushToDrawQueue(meshes, l, r, frm, scale, ra);
					return;
				}
				i++;
			}
			return; //The model is farther away than the maximum lod distance then we don't render it.
		}
		else {
			models[0].pushToDrawQueue(meshes, l, r, frm, scale, ra);
		}
	}

	//Use the models radius here for culling
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, AnimationFrame frm, float scale) {
		float dis = getDistance(l);
		
		if(distances.length > 0) {
			int i = 0;
			while(i < distances.length) {
				if(dis < distances[i]) {
					models[i].pushToDrawQueue(meshes, l, r, frm, scale);
					return;
				}
				i++;
			}
			return; //The model is farther away than the maximum lod distance then we don't render it.
		}
		else {
			models[0].pushToDrawQueue(meshes, l, r, frm, scale);
		}
	}
	
	//Uses given radius (ra) for culling and applies scalar values to shaders for the drawing of this model. Information on scalars in Shader.java.
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, AnimationFrame frm, float scale, float ra, float scalar[]) {
		float dis = getDistance(l);
		
		if(distances.length > 0) {
			int i = 0;
			while(i < distances.length) {
				if(dis < distances[i]) {
					models[i].pushToDrawQueue(meshes, l, r, frm, scale, ra, scalar);
					return;
				}
				i++;
			}
			return; //The model is farther away than the maximum lod distance then we don't render it.
		}
		else {
			models[0].pushToDrawQueue(meshes, l, r, frm, scale, ra, scalar);
		}
	}
	
	//Uses models radius for culling and applies scalar values to shaders for the drawing of this model. Information on scalars in Shader.java.
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, AnimationFrame frm, float scale, float scalar[]) {
		float dis = getDistance(l);
		
		if(distances.length > 0) {
			int i = 0;
			while(i < distances.length) {
				if(dis < distances[i]) {
					models[i].pushToDrawQueue(meshes, l, r, frm, scale, scalar);
					return;
				}
				i++;
			}
			return; //The model is farther away than the maximum lod distance then we don't render it.
		}
		else {
			models[0].pushToDrawQueue(meshes, l, r, frm, scale, scalar);
		}
	}
	
}