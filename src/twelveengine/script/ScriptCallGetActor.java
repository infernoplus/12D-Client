package twelveengine.script;

import twelveengine.actors.Actor;

//Abstract-ish class. This is a super for every type of call in a script. Everything from constants to functions that return strings are a sub class of this.
//If you want to add a new script function then it'll be a subclass of this.
public class ScriptCallGetActor extends ScriptCall{
	private final String name = "GetActor"; //For debugging. Just a name to identify this call by.
	
	public ScriptCallGetActor(ScriptManager m, int c, ScriptCall a[]) {
		super(m, c, a);
	}
	
	public void step() {
	
	}
	
	//Returns void
	public void executeVoid() {
		
	}
	
	//Returns a bool
	public boolean executeBool() {
		return false;
	}
	
	//Returns a int
	public int executeInt() {
		Actor a = manager.game.getActor(calls[0].executeString());
		if(a != null)
			return a.nid;
		else
			return -214783646; //Sloppy but effective.
	}
	
	//Returns a float
	public float executeFloat() {
		return 0;
	}
	
	//Returns a string
	public String executeString() {
		Actor a = manager.game.getActor(calls[0].executeString());
		if(a != null)
			return a.toString();
		else
			return "No such actor."; //Sloppy but effective.
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