package twelveengine.data;

import java.io.*;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.util.ObjectArrayList;

import twelveengine.Log;
import twelveengine.data.*;
import twelveutil.FileUtil;

//TODO: Pending major rework and streamlining!
//TODO: Rewrite in a manner similar to model? Doing locations and rotations on the fly might be better.
public class ConvexHull {
	public String file;
	
	public Vertex[] v;
	
	public float radius;
	
	public ConvexHull(String s) {
		file = s;
		try {
			readCollision(s);
			Log.log("Built Convex Hull: " + file, "Physics");
		} catch (Exception e) {
			System.err.println("~! Failed to read physmodel file? :" + s);
			e.printStackTrace();
		}
	}
	
	public void readCollision(String s) throws Exception{
		String currentLine;
		DataInputStream fileIn = new DataInputStream(FileUtil.getFile(s));
	    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));
	    
	    String cm = "";
	    currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
	    while(currentLine != null) {
	    	if(currentLine.startsWith("collision="))
	    		cm = currentLine.split("=")[1];
	    	currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
	    }
		
		modelImport(cm);
	}
	
	public void modelImport(String s) throws IOException {
		String currentLine;
		DataInputStream fileIn = new DataInputStream(FileUtil.getFile(s));
	    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));
	    
	    int i = 0;
	    int l = 0;
	    
	    currentLine=fileReader.readLine();
	    while(currentLine != null) {
	    	if(currentLine.startsWith("v "))
	    		i++;
	    	if(currentLine.startsWith("f "))
	    		l++;
		    currentLine=fileReader.readLine();
	    }
	    //System.out.println("Model: " + s + " " + "Vertexs: " + i + " " + j + " " + k + " Triangles: " + l + " Materials: " + g);
	    
	    v = new Vertex[i];
	    
	    i = 0;
	    
	    float x, y, z;
	    
		fileIn = new DataInputStream(FileUtil.getFile(s));
	    fileReader = new BufferedReader(new InputStreamReader(fileIn));
	    currentLine=fileReader.readLine();
	    while(currentLine != null) {
	    	currentLine = currentLine.replaceAll("  ", " ");
	    	if(currentLine.startsWith("v ")) {
	    		x = Float.parseFloat(currentLine.split(" ")[1]);
	    		y = Float.parseFloat(currentLine.split(" ")[2]);
	    		z = Float.parseFloat(currentLine.split(" ")[3]);
	    		v[i] = new Vertex(x,y,z);
	    		i++;
	    	}
		    currentLine=fileReader.readLine();
	    }
	}
	
	public ConvexHullShape makeConvexHull(float f) {
		ObjectArrayList<Vector3f> vertices = new ObjectArrayList<Vector3f>();
		int j = 0;
		while(j < v.length) {
			vertices.add(new Vector3f(v[j].x * f, v[j].y * f, v[j].z * f));
			j++;
		}
		ConvexHullShape hull = new com.bulletphysics.collision.shapes.ConvexHullShape(vertices);
		return hull;
	}
}