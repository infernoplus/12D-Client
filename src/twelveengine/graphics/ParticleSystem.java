package twelveengine.graphics;

import java.util.ArrayList;

import twelveengine.data.Vertex;
import twelveutil.*;

//TODO: I think I coded this while channeling the spirit of an autistic zebra so.... yeah... maybe rewrite it?
//TODO: make it so particles keep up with themselves by giving them a pointer to whatever they are "connected" to.
//TODO: the entire "event" system is kind of fucking janky. the idea is good it just needs some love and care from mommy.

public class ParticleSystem {
	public Effect effect;
	
	public String name;
	public String file;
	
	public Vertex location;
	public Vertex velocity;
	
	public ArrayList<Integer> age = new ArrayList<Integer>(); //So we can play this effect during itself and have it not reset, but actually play two of itself.
	
	public ArrayList<ParticleEvent> events = new ArrayList<ParticleEvent>();
	public ArrayList<Particle> particles = new ArrayList<Particle>();
	
	public ParticleSystem(Effect x, String f, Vertex l, Vertex v) {
		effect = x;
		
		file = f;

		setLocation(l.copy());
		setVelocity(v.copy());
		
		try { 
			build(f);
		}
		catch(Exception e) {
			System.err.println("Can't find particle system: " + f);
		}
	}
	
	//Step particles first so new particles don't skip their first frame immediately.
	public void step() {
		//Staying attached to effect
		setLocation(effect.location.copy());
		setVelocity(effect.velocity.copy());
		
		//Step particles
		int i = 0;
		while(i < particles.size()) {
			Particle p = particles.get(i);
			if(p.dead) {
				p.destroy();
				particles.remove(i);
				i--;
			}
			else {
				p.step();
			}
			i++;
		}
		
		//Step events
		i = 0;
		while(i < events.size()) {
			events.get(i).step();
			i++;
		}
		
		//Age each trigger of this system
		i = 0;
		while(i < age.size()) {
			age.set(i, age.get(i) + 1);
			i++;
		}
	}
	
	public void setLocation(Vertex v) {
		location = v;
	}

	public void setVelocity(Vertex v) {
		velocity = v;
	}
	
	//TODO: cache the particle and particle system TAGS in engine.game so that we don't read from file as much.
	//This method take the .particlesytem tag and makes a useable particle system out of it.
	public void build(String f) throws Exception {
		Tag sys = effect.game.tagger.openTag(f);
		name = sys.getProperty("name", "default part sys");
		TagSubObject prts = sys.getObject("particles");
		
		int i = 0;
		int j = prts.getTotalObjects();
		while(i < j) { //Collecint information to make the system.
			TagSubObject prt = prts.getObject(i);
			String tag = prt.getProperty("tag", "null");
			int start = prt.getProperty("start", 0);
			int end = prt.getProperty("end", 30);
			TagSubObject sub = prt.getObject("amount");
			int amin = sub.getProperty("min", 1);
			int amax = sub.getProperty("max", 1);
			sub = prt.getObject("velocity");
			float vmin = sub.getProperty("min", 1.0f);
			float vmax = sub.getProperty("max", 1.0f);
			sub = prt.getObject("distribution");
			String shape = sub.getProperty("shape", "radial");
			float spread = sub.getProperty("spread", 0.0f);
			String equation = sub.getProperty("equation", "random");
			Vertex angle = TagUtil.makeVertex(sub.getObject("angle"));
			Vertex center = TagUtil.makeVertex(sub.getObject("center"));//TODO: EULER NONSENSE! BEGONE WITH YOU FOUL DEMON!
			float radius = sub.getProperty("radius", 3.0f);
			
			ParticleEvent pe = new ParticleEvent(this, tag, amin, amax, start, end, shape, angle, spread, equation, center, radius, vmin, vmax);
			events.add(pe);
			
			i++;
		}
	}
	
	//DEPRECATED, KEEPING AS REFERENCE, DELETE LATER WHEN NOT NEEDED
	/**
	public void build(String f) throws Exception {
		String currentLine;
		DataInputStream fileIn = new DataInputStream(FileUtil.getFile(f));
	    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));	    
    	currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
	    while(currentLine != null) {
	    	if(currentLine.startsWith("name"))
	    		name = currentLine.split("=")[1];
	    	if(currentLine.startsWith("begin")) {
	    		ArrayList<String> b = new ArrayList<String>();
	    		while(!currentLine.equals("end")) {
	    			b.add(currentLine);
	    		    currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
	    		}
    			begin(b);
	    	}
	    	currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
	    }
		
	}
	
	//Creates a particle event from the tag file
	private void begin(ArrayList<String> s) {
		try {
			int min = Integer.parseInt(s.get(2).split(",")[0]);
			int max = Integer.parseInt(s.get(2).split(",")[1]);
			int start = Integer.parseInt(s.get(3).split(",")[0]);
			int end = Integer.parseInt(s.get(3).split(",")[1]);
			String sp[] = s.get(5).split(",");
			Vertex v = new Vertex(Float.parseFloat(sp[0]), Float.parseFloat(sp[1]), Float.parseFloat(sp[2]));
			float low = Float.parseFloat(s.get(8).split(",")[0]);
			float high = Float.parseFloat(s.get(8).split(",")[1]);
			sp = s.get(9).split(",");
			Vertex c = new Vertex(Float.parseFloat(sp[0]), Float.parseFloat(sp[1]), Float.parseFloat(sp[2]));
			ParticleEvent pe = new ParticleEvent(this, s.get(1), min, max, start, end, s.get(4), v, Float.parseFloat(s.get(6)), s.get(7), c, Float.parseFloat(s.get(10)), low, high);
			events.add(pe);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}**/
	
	public String toString() {
		return "Particle System: " + name + " - "  + file;
	}
	

	public void playEffect() {
		age.add(0);
	}
	
	public void draw(ArrayList<TrianglePacket> meshes, float f) {
		int i = 0;
		while(i < particles.size()) {
			particles.get(i).draw(meshes, f);
			i++;
		}
	}
}