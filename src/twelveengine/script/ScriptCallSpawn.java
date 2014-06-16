package twelveengine.script;

import twelveengine.actors.Actor;
import twelveengine.data.Quat;
import twelveengine.data.Vertex;

//Abstract-ish class. This is a super for every type of call in a script. Everything from constants to functions that return strings are a sub class of this.
//If you want to add a new script function then it'll be a subclass of this.
public class ScriptCallSpawn extends ScriptCall{
	private final String name = "Spawn"; //For debugging. Just a name to identify this call by.
	
	public ScriptCallSpawn(ScriptManager m, int c, ScriptCall a[]) {
		super(m, c, a);
	}
	
	public void step() {
	
	}
	
	//Returns void
	public void executeVoid() {
		Actor a = manager.game.createTag(calls[0].executeString(), manager.game.getNid(), new Vertex(calls[2].executeFloat(), calls[3].executeFloat(), calls[4].executeFloat()), new Vertex(), new Quat());
		a.sid = calls[1].executeString();
		manager.game.addActor(a);
	}
	
	//Returns a bool
	public boolean executeBool() {
		return false;
	}
	
	//Returns a int
	public int executeInt() {
		return 0;
	}
	
	//Returns a float
	public float executeFloat() {
		return 0;
	}
	
	//Returns a string
	public String executeString() {
		return "";
	}
	
	public void debugTree(int i) {
		int j = 0;
		while(j < i) {
			System.out.print("-");
			j++;
		}
		j = 0;
		System.out.println(" Call:" + name + " ID#" + cid + " Src: " + code);
		while(j < calls.length) {
			calls[j].debugTree(i+1);
		j++;
		}
	}
}