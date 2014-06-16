package twelveengine.actors;
import java.util.ArrayList;

import twelveengine.Game;
import twelveengine.data.*;
import twelveengine.graphics.*;
import twelveutil.*;

public class Actor {
	public Game game;
	
	public String file;
	public String name;//Name of the tag generally.
	
	public String internalName; //Clas names
	public int nid; //Network ID, used to sync actors over the network. //No 2 actors should ever have the same nid for any reason. //nid < 0 == don't sync. (negative numbers)
	public String sid; //Script ID, used to get actors by a name.
	
	public Vertex location, lastLocation;
	public Quat rotation, lastRotation;
	public Vertex velocity, lastVelocity;
	
	public ArrayList<Effect> effects = new ArrayList<Effect>();
	
	//When true, stops the client from doing come calculations. This allows the server or player cloud to do them instead.
	//Used for a variety of things, EX for deciding whether to do physics on non-local players or doing local damage calculations
	//If it is not set in the tag file then it defaults to false. 
	public boolean noClientSimulate;
	
	public boolean dead = false;
	public boolean garbage = false;
	
	public float radius;
	
	protected Tag tag;
	
	//This the "abstract" super class for all actors (aka objects) in the game world.
	//All actors will take a tag file of the same class or super class in their constructor, data to generate the actor will be read from that file. 
	//All actors will also take a network ID (NID) and a pointer to the game they are in.
	//Additionally all actors will take !class specfic values like location rotation and velocity
	public Actor(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		internalName = "Actor";
		game = w;
		nid = n;
		
		tag = game.tagger.openTag(f);
		
		file = f;
		name = tag.getProperty("name", "DEFAULT");
		noClientSimulate = tag.getProperty("noclientsimulate", false);
		radius = tag.getProperty("radius", 10);
		
		location = l;
		velocity = v;
		rotation = r;
		
		sid = "";
		
		TagSubObject efcts = tag.getObject("effects");
		
		int i = 0;
		int j = efcts.getTotalObjects();
		while(i < j) {
			TagSubObject efct = efcts.getObject(i);
			String name = efct.getProperty("name", "default name"); //TODO: This "name" is not particularly useful unless we remember it somehow and can pull effects by it.
			String tag = efct.getProperty("tag", "null");
			String atch = efct.getProperty("attach", "root");
			boolean auto = efct.getProperty("auto", true);
			Effect fx = new Effect(game, this, atch, tag);
			effects.add(fx);
			if(auto)
				fx.playEffect();
			i++;
		}
		
		lastFrame();
	}
	
	//Called on each game tick (basically an update)
	public void step() {
		lastFrame();
		effects();
	}
	
	//Steps all effects attached to this actor
	public void effects() {
		int i = 0;
		while(i < effects.size()) {
			effects.get(i).step();
			i++;
		}
	}
	
	//Used for interpolating graphics between game steps.
	public void lastFrame() {
		lastLocation = location;
		lastRotation = rotation;
		lastVelocity = velocity;
	}
	
	//Do not modify location rotation and velocity directly, use these. Some actors may handle these differntly VIA overide.
	public void move(Vertex a) {
		location = MathUtil.add(location, a);
	}
	
	public void rotate(Quat a) {
		rotation = MathUtil.add(rotation, a);
	}
	
	public void push(Vertex a) {
		velocity = MathUtil.add(velocity, a);
	}
	
	public void setLocation(Vertex a) {
		location = a;
	}
	
	public void setRotation(Quat a) {
		rotation = a;
	}
	
	public void setVelocity(Vertex a) {
		velocity = a;
	}
	
	public void playEffect(String e, String m) {
		Effect fx = new Effect(game, this, m, e);
		effects.add(fx);
		fx.playEffect();
	}
	
	public void damage(float d, Actor a) {
		
	}

	//Returns a named point on the actor. For example, a point on the model. The tip of a wing. The barrel of a gun. ETC
	//If it doesnt match anything just return the location.
	public Vertex getAttachmentPoint(String attachment) {
		return location.copy();
	}
	
	/** Register any new script invokes here**/
	//Invoke is called from scripts or the console to modify an instance of an actor ingame.
	//Always include the super class invoke. The most basic functions of this are in the top levels.
	public void invoke(String m, String p[]) {
		if(m.equals("location")) {
			setLocation(new Vertex(Float.parseFloat(p[0]),Float.parseFloat(p[1]),Float.parseFloat(p[2])));
			return;
		}
		if(m.equals("velocity")) {
			setVelocity(new Vertex(Float.parseFloat(p[0]),Float.parseFloat(p[1]),Float.parseFloat(p[2])));
			return;
		}
		if(m.equals("rotation")) {
			setRotation(new Quat(Float.parseFloat(p[0]),Float.parseFloat(p[1]),Float.parseFloat(p[2]),Float.parseFloat(p[3])));
			return;
		}
		if(m.equals("move")) {
			move(new Vertex(Float.parseFloat(p[0]),Float.parseFloat(p[1]),Float.parseFloat(p[2])));
			return;
		}
		if(m.equals("push")) {
			push(new Vertex(Float.parseFloat(p[0]),Float.parseFloat(p[1]),Float.parseFloat(p[2])));
			return;
		}
		if(m.equals("rotate")) {
			rotate(new Quat(Float.parseFloat(p[0]),Float.parseFloat(p[1]),Float.parseFloat(p[2]),Float.parseFloat(p[3])));
			return;
		}
		if(m.equals("sid")) {
			sid = p[0];
			return;
		}
		if(m.equals("effect")) {
			playEffect(p[0], p[1]);
			return;
		}
		if(m.equals("kill")) {
			kill();
			return;
		}
		if(m.equals("destroy")) {
			destroy();
			return;
		}
	}
	
	//Name of the object, sometimes this will be unique. Mostly not.
	public String getName() {
		return name;
	}
	
	//Information about the object.
	public String toString() {
		return internalName + "." + name + "@" + MathUtil.toString(location) + ":" + nid + ":" + sid;
	}
	
	//Type gives the class that this is and all its super classes in order. Such as Actor:Item:Weapon:PlasmaWeapon
	public String getType() {
		return internalName;
	}
	
	//Puts actor in a state of "dead" which will eventually be destroyed and removed from the game. EX: killed player plays death animation and lays on ground for a while then is destroyed.
	public void kill() {
		dead = true;
		destroy();
	}
	
	//Flags this item to get lost, specifically for the game to remove it on the next step during garbage clean up
	public void destroy() {
		unload();
		dead = true;
		garbage = true;
	}
	
	//Unloads any files that need to go away.
	//Important stuff: Delete audio sources! Empty any inventories! Inform objects that are attached to this that this object is gone.
	public void unload() {
		
	}
	
	//Draws all geometry for the actor with the interpolation f between oldLocation and location, called by GraphicsCore during a draw.
	public void draw(ArrayList<TrianglePacket> meshes, float f) {
		int i = 0;
		while(i < effects.size()) {
			effects.get(i).draw(meshes, f);
			i++;
		}
	}
}
