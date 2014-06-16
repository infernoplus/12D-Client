package twelveengine.script;

import java.util.ArrayList;

import twelveengine.actors.Actor;

//Abstract-ish class. This is a super for every type of call in a script. Everything from constants to functions that return strings are a sub class of this.
//If you want to add a new script function then it'll be a subclass of this.
public class ScriptCallInvoke extends ScriptCall{
	private final String name = "Invoke"; //For debugging. Just a name to identify this call by.
	
	public ScriptCallInvoke(ScriptManager m, int c, ScriptCall a[]) {
		super(m, c, a);
	}
	
	public void step() {
	
	}
	
	//Returns void
	public void executeVoid() {
		Actor a = manager.game.getActor(calls[0].executeInt());
		if(a != null) {
			ArrayList<String> cr = new ArrayList<String>();
			int i = 2;
			while(i < calls.length) {
				cr.add(calls[i].executeString());
				i++;
			}
			a.invoke(calls[1].executeString(), cr.toArray(new String[cr.size()]));
		}
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