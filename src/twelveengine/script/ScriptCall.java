package twelveengine.script;

//Abstract-ish class. This is a super for every type of call in a script. Everything from constants to functions that return strings are a sub class of this.
//If you want to add a new script function then it'll be a subclass of this.
public class ScriptCall {
	public ScriptManager manager;
	
	private final String name = "Void Function"; //For debugging. Just a name to identify this call by.
	
	public int cid;
	public boolean done;
	public String code; //DEBUG
	
	public ScriptCall calls[];
	
	public ScriptCall(ScriptManager m, int c, ScriptCall a[]) {
		manager = m;
		cid = c;
		calls = a;
		
		done = true;
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