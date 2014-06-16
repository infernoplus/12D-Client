package twelveengine.script;

//Abstract-ish class. This is a super for every type of call in a script. Everything from constants to functions that return strings are a sub class of this.
//If you want to add a new script function then it'll be a subclass of this.
public class ScriptCallWhile extends ScriptCall{
	private final String name = "While"; //For debugging. Just a name to identify this call by.
	
	public ScriptCallWhile(ScriptManager m, int c, ScriptCall a[]) {
		super(m, c, a);
	}
	
	public void step() {
		if(!done) {
			calls[1].step();
			if(calls[1].done) {
				executeVoid();
			}
		}
	}
	
	//Returns void
	public void executeVoid() {
		if(calls[0].executeBool()) {
			calls[1].executeVoid();
			done = false;
		}
		else
			done = true;
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