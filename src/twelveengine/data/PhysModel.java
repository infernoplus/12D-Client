package twelveengine.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import twelveengine.data.Vertex;
import twelveutil.FileUtil;

//TODO: Pending major rework and streamlining!
//TODO: Rewrite in a manner similar to model? Doing locations and rotations on the fly might be better.
public class PhysModel {
	public String file;
	
	public Vertex[] v;
	public Vertex[] vn;
	public Vertex[] vt;
	
	public PhysTriangle[] f;
	
	public float radius;
	
	public PhysModel(String s) {
		file = s;
		try {
			readCollision(s);
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
	    f = new PhysTriangle[l];
	    String g = "default";
	    
	    i = 0;
	    l = 0;
	    
	    float x, y, z;
	    String a, b, c;
	    
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
	    	if(currentLine.startsWith("f ")) {
	    		a = currentLine.split(" ")[1];
	    		b = currentLine.split(" ")[2];
	    		c = currentLine.split(" ")[3];
	    		f[l] = new PhysTriangle(v[Integer.parseInt(a.split("/")[0])-1],v[Integer.parseInt(b.split("/")[0])-1],v[Integer.parseInt(c.split("/")[0])-1],g,this);
	    		l++;
	    	}
	    	if(currentLine.startsWith("g ") || currentLine.startsWith("usemtl ")) {
	    		g = currentLine.split(" ",2)[1].replaceAll("\"", "");
	    	}
		    currentLine=fileReader.readLine();
	    }
	}
	
	/** It's not just Deprecated, it's SUPER DEPRECATED **/
	//public void buildCollisionModel() {
		//System.out.println("");
		//System.out.println("Building collision model...");
		//ArrayList<Edge> r = new ArrayList<Edge>();
		//int i = 0;
		//int j = 0;
/*		boolean k = false;
		Edge l;
		while(i < f.length) {
			j = 0;
			k = true;
			l = new Edge(f[i].a,f[i].b);
			while(j < r.size()) {
				if(r.get(j).edgeEquals(l))
					k = false;
				j++;
			}
			if(k)
				r.add(l);
			
			j = 0;
			k = true;
			l = new Edge(f[i].b,f[i].c);
			while(j < r.size()) {
				if(r.get(j).edgeEquals(l))
					k = false;
				j++;
			}
			if(k)
				r.add(l);
			
			j = 0;
			k = true;
			l = new Edge(f[i].c,f[i].a);
			while(j < r.size()) {
				if(r.get(j).edgeEquals(l))
					k = false;
				j++;
			}
			if(k)
				r.add(l);
			
			i++;
		}
	*/	/*
		i=0;
		e = new Edge[r.size()];
		while(i < e.length) {
			e[i] = r.get(i);
			i++;
		}
		
		i=0;
		while(i < f.length) {
			f[i].collisionData();
			i++;
		}
		
		i = 0;
		radius = 0;
		while(i < v.length) {
			Vertex zero = new Vertex(0, 0, 0);
			if(radius < MathUtil.length(zero, v[i]))
				radius = MathUtil.length(zero, v[i]);
			i++;
		}
		*/
		
		//System.out.println("Complete!");
		//System.out.println("Total edges: " + f.length*3);
		//System.out.println("Unique edges: " + r.size());
		//System.out.println("");
	//}
}