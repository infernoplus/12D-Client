package twelveengine.script;

//Abstract-ish class. This is a super for every type of call in a script. Everything from constants to functions that return strings are a sub class of this.
//If you want to add a new script function then it'll be a subclass of this.
public class ScriptCallSleep extends ScriptCall {
	private final String name = "Sleep"; //For debugging. Just a name to identify this call by.
	private int sleep;
	private int time;
	
	public ScriptCallSleep(ScriptManager m, int c, ScriptCall a[]) {
		super(m, c, a);
		sleep = a[0].executeInt();
		time = 0;
	}
	
	public void step() {
		if(!done) {
			time++;
			if(time >= sleep) {
				done = true;
			}
		}
	}
	
	//Returns void
	public void executeVoid() {
		time = 0;
		done = false;
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
	
	public String syntax() {
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