package twelveengine.script;

//Abstract-ish class. This is a super for every type of call in a script. Everything from constants to functions that return strings are a sub class of this.
//If you want to add a new script function then it'll be a subclass of this.
public class ScriptCallAdd extends ScriptCall{
	private final String name = "Add"; //For debugging. Just a name to identify this call by.
	
	public ScriptCallAdd(ScriptManager m, int c, ScriptCall a[]) {
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
		return (int)(calls[0].executeFloat() + calls[1].executeFloat());
	}
	
	//Returns a float
	public float executeFloat() {
		return calls[0].executeFloat() + calls[1].executeFloat();
	}
	
	//Returns a string
	public String executeString() {
		return calls[0].executeFloat() + calls[1].executeFloat() + "";
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