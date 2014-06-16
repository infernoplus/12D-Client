package twelveengine.graphics;

import java.util.ArrayList;

import twelveengine.data.*;
import twelveutil.*;

public class ParticleEvent {
	public ParticleSystem partSys;
	
	public String file;
	
	/** Values for the particle system and how we will spawn our particles **/
	//Amount
	public int min;
	public int max;
	//Time
	public int start;
	public int end;
	//Distribution
	public String method;
	public String equation;
	public Vertex angle;
	public float spread;
	//Speed
	public float low;
	public float high;
	//Area
	public Vertex center;
	public float radius;
	
	/** Values for the particle itself **/
	public String name;
	
	public Triangle geometry[];
	public boolean screenFacing;
	public Shader shader;
	
	public boolean collision;
	public boolean attached;
	public float gravity;
	public float drag;
	public float bounce;
	
	public int minLife;
	public int maxLife;
	
	public float minSize;
	public float maxSize;
	public float scaling; 
	
	
	
	public ParticleEvent(ParticleSystem prt, String f, int m, int n, int s, int d, String o, Vertex a, float p, String e, Vertex c, float r, float v, float z) {
		partSys = prt;
		
		file = f;
		
		min = m;
		max = n;
		
		start = s;
		end = d;
		method = o;
		equation = e;
		angle = MathUtil.normalize(a);
		spread = p;
		
		low = v;
		high = z;
		
		center = c;
		radius = r;
		
		try {
			build(file);
		}
		catch(Exception ex) {
			System.err.println("Failed to read particle: " + file);
		}
	}
	
	public ArrayList<Particle> inactive = new ArrayList<Particle>();
	public ArrayList<Integer> timers = new ArrayList<Integer>();
	
	public void step() {
		int j = 0;
		while(j < partSys.age.size()) {
			if(partSys.age.get(j) == start)
				trigger();
			j++;
		}
		
		j = 0;
		while(j < inactive.size()) {
			if(timers.get(j) <= 0) {
				activateParticle(j);
				j--;
			}
			else
				timers.set(j, timers.get(j) - 1);
				
			j++;
		}
	}

	public void trigger() {
		int total = min + (int)(Math.random()*(max - min)); //TODO:make sure that this is correct, might need to floor / ceil
		int length = end - start;
		
		if(end - start == 0) { //If all the particles spawn at the same time no need for distribution methods.
			while(total > 0) {
				createParticle(0);
				total--;	
			}
		}
		else {
			int i = 0;
			if(equation.equals("linear")) {
				 //TODO: implement
			}
			else if(equation.equals("falloff")) {
				 //TODO: implement
			}
			else if(equation.equals("buildup")) {
				 //TODO: implement
			}
			else { //If no match use random
				while(total > 0) { 
					if(Math.random() >= 1/length) { //TODO: WRONG
						createParticle(i);
						total--;
					}
					if(i >= length-1)
						i = 0;
					else
						i++;
				}
			}
		}
	}
	
	//Makes a particle when called.
	private void createParticle(int t) {
		Vertex l;
		Vertex v;
		
		l = MathUtil.add(MathUtil.multiply(MathUtil.normalize(MathUtil.randomVertex(-1.0f, 1.0f)), radius), center);
		int life = minLife + (int)(Math.random()*(maxLife - minLife));
		float size = MathUtil.random(minSize, maxSize);
		float speed = MathUtil.random(low, high);
		if(method.equals("direction")) {
			v = MathUtil.multiply(MathUtil.normalize(MathUtil.add(MathUtil.multiply(MathUtil.normalize(MathUtil.randomVertex(-1.0f, 1.0f)), spread), angle)), speed);
		}
		else if(method.equals("plate")) {
			v = new Vertex(0,0,0); //TODO: implement
		}
		else { //If no match then use radial
			v = MathUtil.multiply(MathUtil.normalize(MathUtil.randomVertex(-1.0f, 1.0f)), speed);
		}
		
		Particle p = new Particle(this, l, v, name, shader, collision, attached, gravity, drag, bounce, life, size, scaling);
		
		if(t == 0) {
			p.setLocation(MathUtil.add(p.location, partSys.location));
			p.lastFrame();
			partSys.particles.add(p);
		}
		else {
			inactive.add(p);
			timers.add(t);
		}
	}
	
	private void activateParticle(int j) {
		inactive.get(j).setLocation(MathUtil.add(partSys.location, inactive.get(j).location));
		inactive.get(j).lastFrame();
		partSys.particles.add(inactive.get(j));
		inactive.remove(j);
		timers.remove(j);
	}
	
	//Takes params from the .particle tag to define particles that this spawns when triggered.
	private void build(String f) throws Exception {
		Tag part = partSys.effect.game.tagger.openTag(f);
		name = part.getProperty("name", "default particle");
		createGeometry(part.getProperty("geometry", "facing"));
		shader = partSys.effect.game.getShader(part.getProperty("shader", "null"));
		
		collision = part.getProperty("collision", false);
		attached = part.getProperty("attached", false);
		gravity = part.getProperty("gravity", 0.1f);
		drag = part.getProperty("drag", 0.98f);
		bounce = part.getProperty("bounce", 0.9f);
		
		TagSubObject sub = part.getObject("life");
		minLife = sub.getProperty("min", 30);
		maxLife = sub.getProperty("max", 30);
		sub = part.getObject("size");
		minSize = sub.getProperty("min", 1f);
		maxSize = sub.getProperty("max", 1f);
		
		scaling = part.getProperty("scale", 1f);
	}
	
	//DEPRECATED, KEEPING AS REFERENCE, DELETE LATER WHEN NOT NEEDED
	/**
	private void build(String f) throws Exception {
		String currentLine;
		DataInputStream fileIn = new DataInputStream(FileUtil.getFile(f));
	    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));	    
	    currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
	    while(currentLine != null) {
	    	if(currentLine.startsWith("name"))
	    		name = currentLine.split("=")[1];
	    	if(currentLine.startsWith("geometry"))
	    		createGeometry(currentLine.split("=")[1]);
	    	if(currentLine.startsWith("shader"))
	    		shader = partSys.effect.game.getShader(currentLine.split("=")[1]);
	    	if(currentLine.startsWith("collision"))
	    		collision = Boolean.parseBoolean(currentLine.split("=")[1]);
	    	if(currentLine.startsWith("attached"))
	    		attached = Boolean.parseBoolean(currentLine.split("=")[1]);
	    	if(currentLine.startsWith("gravity"))
	    		gravity = Float.parseFloat(currentLine.split("=")[1]);
	    	if(currentLine.startsWith("drag"))
	    		drag = Float.parseFloat(currentLine.split("=")[1]);
	    	if(currentLine.startsWith("bounce"))
	    		bounce = Float.parseFloat(currentLine.split("=")[1]);
	    	if(currentLine.startsWith("scaling"))
	    		scaling = Float.parseFloat(currentLine.split("=")[1]);
	    	if(currentLine.startsWith("life")) {
	    		minLife = Integer.parseInt(currentLine.split("=")[1].split(",")[0]);
	    		maxLife = Integer.parseInt(currentLine.split("=")[1].split(",")[1]);
	    	}
	    	if(currentLine.startsWith("size")) {
	    		minSize = Float.parseFloat(currentLine.split("=")[1].split(",")[0]);
	    		maxSize = Float.parseFloat(currentLine.split("=")[1].split(",")[1]);
	    	}
		    currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
	    }
	}
	**/
	
	private void createGeometry(String g) {
		if(g.equals("cross")) {
			//TODO: implement this
		}
		else { //If no match then use screen facing geometry
			geometry = new Triangle[2];
			Vertex a = new Vertex(0,1,1);
			Vertex b = new Vertex(0,-1,1);
			Vertex c = new Vertex(0,-1,-1);
			Vertex d = new Vertex(0,1,-1);
			
			Vertex i = new Vertex(1,0,0);
			
			Vertex u = new Vertex(0,0,0);
			Vertex v = new Vertex(1,0,0);
			Vertex w = new Vertex(1,1,0);
			Vertex x = new Vertex(0,1,0);
			
			geometry[0] = new Triangle(a,b,c,i,i,i,u,v,w,null);
			geometry[1] = new Triangle(c,d,a,i,i,i,w,x,u,null);
			
			screenFacing = true;
		}
	}
	
	public Triangle[] getGeometry() {
		return geometry;
	}
}