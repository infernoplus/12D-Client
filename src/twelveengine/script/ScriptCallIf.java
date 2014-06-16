package twelveengine.script;

//Abstract-ish class. This is a super for every type of call in a script. Everything from constants to functions that return strings are a sub class of this.
//If you want to add a new script function then it'll be a subclass of this.
public class ScriptCallIf extends ScriptCall{
	private final String name = "If"; //For debugging. Just a name to identify this call by.
	
	private boolean doElse;
	
	public ScriptCallIf(ScriptManager m, int c, ScriptCall a[]) {
		super(m, c, a);
		
		if(a.length > 2)
			doElse = true;
		else
			doElse = false;
	}
	
	public void step() {
		if(executed != null) {
			executed.step();
			done = executed.done;
		}
	}
	
	//Returns void
	private ScriptCall executed;
	public void executeVoid() {
		if(calls[0].executeBool())
			executed = calls[1];
		else if(doElse)
			executed = calls[2];
		executed.executeVoid();
		done = executed.done;
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