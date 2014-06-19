package twelveengine.graphics;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import twelveengine.Game;
import twelveengine.Log;
import twelveengine.data.Edge;
import twelveengine.data.Quat;
import twelveengine.data.Vertex;
import twelveutil.FileUtil;
import twelveutil.MathUtil;

public class Model {
	public Game game;
	
	public String file;
	
	public Frame[] fr;
	public Marker[] ma;
	
	public Vertex[] v;
	public Vertex[] vn;
	public Vertex[] vt;
	
	public Edge[] e;
	
	public Triangle[] f;
	
	public ArrayList<Shader> sh = new ArrayList<Shader>();
	
	public float radius;
	
	public boolean skinned;
	
	public Model(Game w, String s) {
		game = w;
		file = s;
		try {
			if(s.endsWith(".obj")) {
				objImport(s);
				skinned = false;
				createBuffers();
			}
			else if(s.endsWith(".tbj")) {
				tbjImport(s);
				skinned = true;
				createSkinnedBuffers();
			}
			else {
				throw new IOException("NOT A MODEL FILE! VALID TYPES: .obj .tbj ");
			}
			modelRadius();
			Log.log("Built model: " + file, "Model");
		} catch (IOException e) {
			Log.log("Failed to build model: " + file, "Model", 2);
			e.printStackTrace();
		}
	}

	public void objImport(String s) throws IOException {
		String currentLine;
		DataInputStream fileIn = new DataInputStream(FileUtil.getFile(s));
	    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));
	    
	    int i = 0;
	    int j = 0;
	    int k = 0;
	    int l = 0;
	    String g = "";
	    
	    currentLine=fileReader.readLine();
	    while(currentLine != null) {
	    	if(currentLine.startsWith("v "))
	    		i++;
	    	if(currentLine.startsWith("vn "))
	    		j++;
	    	if(currentLine.startsWith("vt "))
	    		k++;
	    	if(currentLine.startsWith("f "))
	    		l++;
		    currentLine=fileReader.readLine();
	    }
	    //System.out.println("Model: " + s + " " + "Vertexs: " + i + " " + j + " " + k + " Triangles: " + l + " Materials: " + g);
	    
	    v = new Vertex[i];
	    vn = new Vertex[j];
	    vt = new Vertex[k];
	    f = new Triangle[l];
	    fr = new Frame[1];
	    ma = new Marker[1];
	    
	    i = 0;
	    j = 0;
	    k = 0;
	    l = 0;
	    
	    float x, y, z;
	    String a, b, c;
	    
	    fr[0] = new Frame(0, "root", new Vertex(0,0,0), new Quat(0,0,0,1));
	    ma[0] = new Marker("root", fr[0], new Vertex(0,0,0), new Vertex(0,0,0));
	    
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
	    		v[i].addWeight(new Weight(fr[0], 1.0f));
	    		i++;
	    	}
	    	if(currentLine.startsWith("vn ")) {
	    		x = Float.parseFloat(currentLine.split(" ")[1]);
	    		y = Float.parseFloat(currentLine.split(" ")[2]);
	    		z = Float.parseFloat(currentLine.split(" ")[3]);
	    		vn[j] = new Vertex(x,y,z);
	    		j++;
	    	}
	    	if(currentLine.startsWith("vt ")) {
	    		x = Float.parseFloat(currentLine.split(" ")[1]);
	    		y = Float.parseFloat(currentLine.split(" ")[2]);
	    		if(currentLine.split(" ").length > 3)
	    			z = Float.parseFloat(currentLine.split(" ")[3]);
	    		else
	    			z = 0;
	    		vt[k] = new Vertex(x,y,z);
	    		k++;
	    	}
	    	if(currentLine.startsWith("f ")) {
	    		a = currentLine.split(" ")[1];
	    		b = currentLine.split(" ")[2];
	    		c = currentLine.split(" ")[3];
	    		f[l] = new Triangle(v[Integer.parseInt(a.split("/")[0])-1],v[Integer.parseInt(b.split("/")[0])-1],v[Integer.parseInt(c.split("/")[0])-1],vn[Integer.parseInt(a.split("/")[2])-1],vn[Integer.parseInt(b.split("/")[2])-1],vn[Integer.parseInt(c.split("/")[2])-1],vt[Integer.parseInt(a.split("/")[1])-1],vt[Integer.parseInt(b.split("/")[1])-1],vt[Integer.parseInt(c.split("/")[1])-1],getShader(g));
	    		l++;
	    	}
	    	if(currentLine.startsWith("g ") || currentLine.startsWith("usemtl ")) {
	    		g = currentLine.split(" ",2)[1].replaceAll("\"", "");
	    	}
		    currentLine=fileReader.readLine();
	    }
	}
	
	public void tbjImport(String s) throws IOException {
		String currentLine;
		DataInputStream fileIn = new DataInputStream(FileUtil.getFile(s));
	    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));
	    
	    int i = 0;
	    int j = 0;
	    int k = 0;
	    int l = 0;
	    int m = 0;
	    int n = 0;
	    String g = "";
	    
	    currentLine=fileReader.readLine();
	    while(currentLine != null) {
	    	if(currentLine.startsWith("v "))
	    		i++;
	    	if(currentLine.startsWith("vn "))
	    		j++;
	    	if(currentLine.startsWith("vt "))
	    		k++;
	    	if(currentLine.startsWith("f "))
	    		l++;
	    	if(currentLine.startsWith("fr "))
	    		m++;
	    	if(currentLine.startsWith("ma "))
	    		n++;
		    currentLine=fileReader.readLine();
	    }
	    //System.out.println("Model: " + s + " " + "Vertexs: " + i + " " + j + " " + k + " Triangles: " + l + " Materials: " + g);
	    
	    v = new Vertex[i];
	    vn = new Vertex[j];
	    vt = new Vertex[k];
	    f = new Triangle[l];
	    fr = new Frame[m];
	    ma = new Marker[n];
	    
	    i = 0;
	    j = 0;
	    k = 0;
	    l = 0;
	    m = 0;
	    n = 0;
	    
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
	    	if(currentLine.startsWith("vn ")) {
	    		x = Float.parseFloat(currentLine.split(" ")[1]);
	    		y = Float.parseFloat(currentLine.split(" ")[2]);
	    		z = Float.parseFloat(currentLine.split(" ")[3]);
	    		vn[j] = new Vertex(x,y,z);
	    		j++;
	    	}
	    	if(currentLine.startsWith("vt ")) {
	    		x = Float.parseFloat(currentLine.split(" ")[1]);
	    		y = Float.parseFloat(currentLine.split(" ")[2]);
	    		if(currentLine.split(" ").length > 3)
	    			z = Float.parseFloat(currentLine.split(" ")[3]);
	    		else
	    			z = 0;
	    		vt[k] = new Vertex(x,y,z);
	    		k++;
	    	}
	    	if(currentLine.startsWith("vw ")) {
	    		int vi = Integer.parseInt(currentLine.split(" ")[1]);
	    		String fn = currentLine.split("\"")[1];
	    		z = Float.parseFloat(currentLine.split("\"")[2].split(" ")[1]);
	    		int fi = 0;
	    		int fs = 0;
	    		while(fs < fr.length) {
	    			if(fr[fs].name.equals(fn)) {
	    				fi = fs;
	    				fs = fr.length;
	    			}
	    			else
	    				fs++;
	    		}
	    		v[vi-1].addWeight(new Weight(fr[fi], z));
	    		m++;
	    	}
	    	if(currentLine.startsWith("f ")) {
	    		a = currentLine.split(" ")[1];
	    		b = currentLine.split(" ")[2];
	    		c = currentLine.split(" ")[3];
	    		f[l] = new Triangle(v[Integer.parseInt(a.split("/")[0])-1],v[Integer.parseInt(b.split("/")[0])-1],v[Integer.parseInt(c.split("/")[0])-1],vn[Integer.parseInt(a.split("/")[2])-1],vn[Integer.parseInt(b.split("/")[2])-1],vn[Integer.parseInt(c.split("/")[2])-1],vt[Integer.parseInt(a.split("/")[1])-1],vt[Integer.parseInt(b.split("/")[1])-1],vt[Integer.parseInt(c.split("/")[1])-1],getShader(g));
	    		l++;
	    	}
	    	if(currentLine.startsWith("fr ")) {
	    		String na = currentLine.split("\"")[1];
	    		String co = currentLine.split("\"")[2];
	    		float lx = Float.parseFloat(co.split(" ")[1].split("/")[0]);
	    		float ly = Float.parseFloat(co.split(" ")[1].split("/")[1]);
	    		float lz = Float.parseFloat(co.split(" ")[1].split("/")[2]);
	    		float rx = Float.parseFloat(co.split(" ")[2].split("/")[0]);
	    		float ry = Float.parseFloat(co.split(" ")[2].split("/")[1]);
	    		float rz = Float.parseFloat(co.split(" ")[2].split("/")[2]);
	    		float rw = Float.parseFloat(co.split(" ")[2].split("/")[3]);
	    		fr[m] = new Frame(m, na, new Vertex(lx, ly, lz), new Quat(rx, ry, rz,rw));
	    		m++;
	    	}
	    	if(currentLine.startsWith("ma ")) {
	    		String na = currentLine.split("\"")[1];
	    		String pa = currentLine.split("\"")[3];
	    		String co = currentLine.split("\"")[4];
	    		float lx = Float.parseFloat(co.split(" ")[1].split("/")[0]);
	    		float ly = Float.parseFloat(co.split(" ")[1].split("/")[1]);
	    		float lz = Float.parseFloat(co.split(" ")[1].split("/")[2]);
	    		float rx = Float.parseFloat(co.split(" ")[2].split("/")[0]);
	    		float ry = Float.parseFloat(co.split(" ")[2].split("/")[1]);
	    		float rz = Float.parseFloat(co.split(" ")[2].split("/")[2]);
	    		ma[n] = new Marker(na, getFrame(pa), new Vertex(lx, ly, lz), new Vertex(rx, ry, rz));
	    		n++;
	    	}
	    	if(currentLine.startsWith("g ") || currentLine.startsWith("usemtl ")) {
	    		g = currentLine.split(" ",2)[1].replaceAll("\"", "");
	    		
	    	}
		    currentLine=fileReader.readLine();
	    }
	}
	
	public Frame getFrame(String f) {
		int i = 0;
		while(i < fr.length) {
			if(fr[i].name.equals(f))
				return fr[i];
			i++;
		}
		return fr[0];
	}
	
	public Shader getShader(String s) {
		int i = 0;
		while(i < sh.size()) {
			if(sh.get(i).file.equals(s))
				return sh.get(i);
			i++;
		}
		Shader mat = game.getShader(s);
		sh.add(mat);
		return mat;
	}
	
	public FloatBuffer buffer[];
	public int bufSize[];
	
	public void createBuffers() {
		int i = 0;
		int j = 0;
		buffer = new FloatBuffer[sh.size()];
		bufSize = new int[sh.size()];
		ByteBuffer vbb;
		
		while(j < buffer.length) {
			String n = sh.get(j).file;
			
			while(i < f.length) {
				if(n.equals(f[i].shdr.file))
					bufSize[j]++;
				i++;
			}
			
			i = 0;
			vbb = ByteBuffer.allocateDirect(bufSize[j] * ((3 + 3 + 3 + 3) * 3) * 4);
			vbb.order(ByteOrder.nativeOrder());
			buffer[j] = vbb.asFloatBuffer();

			while(i < f.length) {
				if(n.equals(f[i].shdr.file)) {
					buffer[j].put(f[i].a.x); 
					buffer[j].put(f[i].a.y); 
					buffer[j].put(f[i].a.z); 
					/*
					buffer[j].put(f[i].i.x); 
					buffer[j].put(f[i].i.y); 
					buffer[j].put(f[i].i.z); 
					*/
					buffer[j].put(f[i].normal.x); 
					buffer[j].put(f[i].normal.y); 
					buffer[j].put(f[i].normal.z); 
					buffer[j].put(f[i].u.x); 
					buffer[j].put(f[i].u.y); 
					buffer[j].put(f[i].u.z);
					buffer[j].put(f[i].m.x);
					buffer[j].put(f[i].m.y);
					buffer[j].put(f[i].m.z);
					
					buffer[j].put(f[i].b.x); 
					buffer[j].put(f[i].b.y); 
					buffer[j].put(f[i].b.z);
					/*
					buffer[j].put(f[i].j.x); 
					buffer[j].put(f[i].j.y); 
					buffer[j].put(f[i].j.z);
					*/
					buffer[j].put(f[i].normal.x); 
					buffer[j].put(f[i].normal.y); 
					buffer[j].put(f[i].normal.z); 
					buffer[j].put(f[i].v.x); 
					buffer[j].put(f[i].v.y); 
					buffer[j].put(f[i].v.z);
					buffer[j].put(f[i].n.x);
					buffer[j].put(f[i].n.y);
					buffer[j].put(f[i].n.z);
					
					buffer[j].put(f[i].c.x); 
					buffer[j].put(f[i].c.y); 
					buffer[j].put(f[i].c.z); 
					/*
					buffer[j].put(f[i].k.x); 
					buffer[j].put(f[i].k.y); 
					buffer[j].put(f[i].k.z); 
					*/
					buffer[j].put(f[i].normal.x); 
					buffer[j].put(f[i].normal.y); 
					buffer[j].put(f[i].normal.z); 
					buffer[j].put(f[i].w.x); 
					buffer[j].put(f[i].w.y); 
					buffer[j].put(f[i].w.z);
					buffer[j].put(f[i].o.x);
					buffer[j].put(f[i].o.y);
					buffer[j].put(f[i].o.z);
				}
				i++;
			}
			buffer[j].rewind();
			i = 0;
			j++;
		}
	}
	
	public void createSkinnedBuffers() {
		int i = 0;
		int j = 0;
		buffer = new FloatBuffer[sh.size()];
		bufSize = new int[sh.size()];
		ByteBuffer vbb;
		
		while(j < buffer.length) {
			String n = sh.get(j).file;
			
			while(i < f.length) {
				if(n.equals(f[i].shdr.file))
					bufSize[j]++;
				i++;
			}
			
			i = 0;
			vbb = ByteBuffer.allocateDirect(bufSize[j] * ((3 + 3 + 3 + 3 + 3) * 3) * 4);
			vbb.order(ByteOrder.nativeOrder());
			buffer[j] = vbb.asFloatBuffer();

			while(i < f.length) {
				if(n.equals(f[i].shdr.file)) {
					buffer[j].put(f[i].a.x); 
					buffer[j].put(f[i].a.y); 
					buffer[j].put(f[i].a.z); 
					buffer[j].put(f[i].i.x); 
					buffer[j].put(f[i].i.y); 
					buffer[j].put(f[i].i.z); 
					buffer[j].put(f[i].u.x); 
					buffer[j].put(f[i].u.y); 
					buffer[j].put(f[i].u.z);
					buffer[j].put(f[i].a.weights[0].frame.index);
					buffer[j].put(f[i].a.weights[1].frame.index); 
					buffer[j].put(f[i].a.weights[2].frame.index); 
					buffer[j].put(f[i].a.weights[0].weight); 
					buffer[j].put(f[i].a.weights[1].weight); 
					buffer[j].put(f[i].a.weights[2].weight); 
					buffer[j].put(f[i].b.x); 
					buffer[j].put(f[i].b.y); 
					buffer[j].put(f[i].b.z);
					buffer[j].put(f[i].j.x); 
					buffer[j].put(f[i].j.y); 
					buffer[j].put(f[i].j.z);
					buffer[j].put(f[i].v.x); 
					buffer[j].put(f[i].v.y); 
					buffer[j].put(f[i].v.z);
					buffer[j].put(f[i].b.weights[0].frame.index);
					buffer[j].put(f[i].b.weights[1].frame.index); 
					buffer[j].put(f[i].b.weights[2].frame.index); 
					buffer[j].put(f[i].b.weights[0].weight); 
					buffer[j].put(f[i].b.weights[1].weight); 
					buffer[j].put(f[i].b.weights[2].weight); 
					buffer[j].put(f[i].c.x); 
					buffer[j].put(f[i].c.y); 
					buffer[j].put(f[i].c.z); 
					buffer[j].put(f[i].k.x); 
					buffer[j].put(f[i].k.y); 
					buffer[j].put(f[i].k.z); 
					buffer[j].put(f[i].w.x); 
					buffer[j].put(f[i].w.y); 
					buffer[j].put(f[i].w.z);
					buffer[j].put(f[i].c.weights[0].frame.index);
					buffer[j].put(f[i].c.weights[1].frame.index); 
					buffer[j].put(f[i].c.weights[2].frame.index); 
					buffer[j].put(f[i].c.weights[0].weight); 
					buffer[j].put(f[i].c.weights[1].weight); 
					buffer[j].put(f[i].c.weights[2].weight); 
				}
				i++;
			}
			buffer[j].rewind();
			i = 0;
			j++;
		}
	}
	
	public void modelRadius() {
		int i = 0;
		radius = 0;
		while(i < v.length) {
			float d = MathUtil.magnitude(v[i]);
			if(radius < d)
				radius = d;
			i++;
		}
	}
	
	public Frame[] setAnimation(AnimationFrame f) {//TODO: slow?
		int i = 0;
		Frame frms[] = new Frame[fr.length];
		while(i < frms.length) {
			frms[i] = fr[i].copy();
			i++;
		}
		i = 0;
		while(i < f.nodes.length) {
			if(f.nodes[i].frame-1 < fr.length) {
				frms[f.nodes[i].frame-1].location = (f.nodes[i].location);
				frms[f.nodes[i].frame-1].rotation = (f.nodes[i].rotation);
			}
			i++;
		}
		return frms;
	}
	
	public Marker getMarker(String s) {
		int i = 0;
		while(i < ma.length) {
			if(ma[i].name.equals(s))
				return ma[i];
			i++;
		}
		return ma[0];
	}
	
	/** WITHOUT ANIMATION **/
	
	//Use the given radius (ra) here for culling. 
	//Animated models usually need their radius set manually seeing as their calculated radius is not going to be accurate during animation.
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, float scale, float ra) {
		int i = 0;
		TrianglePacket tp;
		while(i < sh.size()) {
			tp = new TrianglePacket(buffer[i], bufSize[i], sh.get(i), l, r, skinned, fr, scale, ra);
			meshes.add(tp);
			i++;
		}
	}
	
	//Use the models radius here for culling
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, float scale) {
		int i = 0;
		TrianglePacket tp;
		while(i < sh.size()) {
			tp = new TrianglePacket(buffer[i], bufSize[i], sh.get(i), l, r, skinned, fr, scale, radius*scale);
			meshes.add(tp);
			i++;
		}		
	}
	
	//Uses given radius (ra) for culling and applies scalar values to shaders for the drawing of this model. Information on scalars in Shader.java.
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, float scale, float ra, float scalar[]) {
		int i = 0;
		TrianglePacket tp;
		while(i < sh.size()) {
			tp = new TrianglePacket(buffer[i], bufSize[i], sh.get(i), l, r, skinned, fr, scale, ra, scalar);
			meshes.add(tp);
			i++;
		}
	}
	
	//Uses models radius for culling and applies scalar values to shaders for the drawing of this model. Information on scalars in Shader.java.
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, float scale, float scalar[]) {
		int i = 0;
		TrianglePacket tp;
		while(i < sh.size()) {
			tp = new TrianglePacket(buffer[i], bufSize[i], sh.get(i), l, r, skinned, fr, scale, radius*scale, scalar);
			meshes.add(tp);
			i++;
		}
	}
	
	/** WITH ANIMATION! **/
	
	//Use the given radius (ra) here for culling. 
	//Animated models usually need their radius set manually seeing as their calculated radius is not going to be accurate during animation.
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, AnimationFrame frm, float scale, float ra) {
		int i = 0;
		TrianglePacket tp;
		while(i < sh.size()) {
			tp = new TrianglePacket(buffer[i], bufSize[i], sh.get(i), l, r, skinned, setAnimation(frm), scale, ra);
			meshes.add(tp);
			i++;
		}
	}
	
	//Use the models radius here for culling
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, AnimationFrame frm, float scale) {
		int i = 0;
		TrianglePacket tp;
		while(i < sh.size()) {
			tp = new TrianglePacket(buffer[i], bufSize[i], sh.get(i), l, r, skinned, setAnimation(frm), scale, radius*scale);
			meshes.add(tp);
			i++;
		}		
	}
	
	//Uses given radius (ra) for culling and applies scalar values to shaders for the drawing of this model. Information on scalars in Shader.java.
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, AnimationFrame frm, float scale, float ra, float scalar[]) {
		int i = 0;
		TrianglePacket tp;
		while(i < sh.size()) {
			tp = new TrianglePacket(buffer[i], bufSize[i], sh.get(i), l, r, skinned, setAnimation(frm), scale, ra, scalar);
			meshes.add(tp);
			i++;
		}
	}
	
	//Uses models radius for culling and applies scalar values to shaders for the drawing of this model. Information on scalars in Shader.java.
	public void pushToDrawQueue(ArrayList<TrianglePacket> meshes, Vertex l, Quat r, AnimationFrame frm, float scale, float scalar[]) {
		int i = 0;
		TrianglePacket tp;
		while(i < sh.size()) {
			tp = new TrianglePacket(buffer[i], bufSize[i], sh.get(i), l, r, skinned, setAnimation(frm), scale, radius*scale, scalar);
			meshes.add(tp);
			i++;
		}
	}
}