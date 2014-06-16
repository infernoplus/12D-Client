package twelveengine.script;

//Abstract-ish class. This is a super for every type of call in a script. Everything from constants to functions that return strings are a sub class of this.
//If you want to add a new script function then it'll be a subclass of this.
public class ScriptCallConstant extends ScriptCall {
	private final String name = "Constant"; //For debugging. Just a name to identify this call by.
	public String value;
	
	public ScriptCallConstant(ScriptManager m, int c, ScriptCall a[], String s) {
		super(m, c, a);
		value = s;
	}
	
	public void step() {
		int i = 0;
		while(i < calls.length) {
			calls[i].step();
			i++;
		}
	}
	
	//Returns void
	public void executeVoid() {
		
	}
	
	//Returns a bool
	public boolean executeBool() {
		return Boolean.parseBoolean(value);
	}
	
	//Returns a int
	public int executeInt() {
		return Integer.parseInt(value);
	}
	
	//Returns a float
	public float executeFloat() {
		return Float.parseFloat(value);
	}
	
	//Returns a string
	public String executeString() {
		return value;
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