package twelveengine.graphics;

import java.util.ArrayList;

import twelveengine.Game;
import twelveengine.actors.Actor;
import twelveengine.audio.Sound;
import twelveengine.audio.SoundData;
import twelveengine.data.Vertex;
import twelveutil.Tag;
import twelveutil.TagSubObject;

public class Effect {
	public Game game;
	
	public Actor owner;
	public String attachment;
	
	public String name;
	public String file;
	
	public Vertex location;
	public Vertex velocity;
	
	public ArrayList<Integer> age = new ArrayList<Integer>();
	
	ArrayList<EffectEvent> events = new ArrayList<EffectEvent>();
	
	public Effect(Game w, Actor a, String s, String f) {
		game = w;
		
		owner = a;
		attachment = s;
		
		file = f;

		setLocation(owner.getAttachmentPoint(attachment).copy());
		setVelocity(owner.velocity.copy());
		
		age = new ArrayList<Integer>();
		events = new ArrayList<EffectEvent>();
		
		try { 
			build(f);
		}
		catch(Exception e) {
			System.err.println("Can't find effect: " + f);
			e.printStackTrace();
		}
	}
	
	public void step() {
		//Stay attached to owner of this effect
		setLocation(owner.getAttachmentPoint(attachment).copy());
		setVelocity(owner.velocity.copy());
		
		//Step events of this effect
		int i = 0;
		while(i < events.size()) {
			events.get(i).step();
			i++;
		}
		
		//Age each trigger of this effect
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
	
	public void playEffect() {
		age.add(0);
	}
	
	//Takes the tag and builds the effect and all prerequisite particle systems.
	private void build(String f) throws Exception {
		Tag efct = game.tagger.openTag(f);
		name = efct.getProperty("name", "default effect");
		
		TagSubObject parts = efct.getObject("parts");
		int i = 0;
		int j = parts.getTotalObjects();
		while(i < j) {
			TagSubObject prt = parts.getObject(i);
			String type = prt.getProperty("type", "null");
			String tag = prt.getProperty("tag", "null");
			boolean lp = prt.getProperty("loop", false);
			int strt = prt.getProperty("start", 0);
			int end = prt.getProperty("repeat", 30);
			
			//More of this?
			if(type.equals("particles")) {
				ParticleSystem p = new ParticleSystem(this, tag, location, velocity); //TODO: Pulling from a cache of particle systems? u wot mayte?
				events.add(new EffectEvent(this, p, lp, strt, end));
			}
			else if(type.equals("sound")) {
				float vol = prt.getProperty("volume", 1.0f);
				float rad = prt.getProperty("radius", 10.0f);
				SoundData sd = game.getSound(tag);
				Sound snd = new Sound(sd, location, game.player.location, game.player.look, vol, rad, false);
				events.add(new EffectEvent(this, snd, lp, strt, end));
			}
			
			i++;
		}
		

		
		
	}
	
	/** DEPRECATED, KEEPING FOR REFERENCE, REMOVE AT A LATER DATE **/
	/*private void build(String f) throws Exception {
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
	
	//Creates an effect event from the tag file
	private void begin(ArrayList<String> s) {
		try {
			if(s.get(0).split(" ")[1].equals("particles")) {
				ParticleSystem p = new ParticleSystem(this, s.get(1), location, velocity);
				events.add(new EffectEvent(this, p, Boolean.parseBoolean(s.get(2)), Integer.parseInt(s.get(3)), Integer.parseInt(s.get(4))));
				return;
			}
			if(s.get(0).split(" ")[1].equals("sound")) {
				SoundData sd = game.getSound(s.get(1));
				Sound snd = new Sound(sd, location, game.player.location, game.player.look, Float.parseFloat(s.get(2)), Float.parseFloat(s.get(3)), false);
				events.add(new EffectEvent(this, snd, Boolean.parseBoolean(s.get(4)), Integer.parseInt(s.get(5)), Integer.parseInt(s.get(6))));
				return;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public String toString() {
		return "Effect: " + name + " - "  + file;
	}
	
	//Delete this effect and everything attached to it.
	public void destroy() {
		
	}
	
	public void draw(ArrayList<TrianglePacket> meshes, float f) {
		int i = 0;
		while(i < events.size()) {
			events.get(i).draw(meshes, f);
			i++;
		}
	}
}