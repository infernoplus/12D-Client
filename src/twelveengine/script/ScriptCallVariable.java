package twelveengine.script;

//Abstract-ish class. This is a super for every type of call in a script. Everything from constants to functions that return strings are a sub class of this.
//If you want to add a new script function then it'll be a subclass of this.
public class ScriptCallVariable extends ScriptCall {
	private final String name = "Variable"; //For debugging. Just a name to identify this call by.
	public String variable;
	
	public ScriptCallVariable(ScriptManager m, int c, ScriptCall a[], String s) {
		super(m, c, a);
		variable = s;
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
		return manager.getBoolean(variable);
	}
	
	//Returns a int
	public int executeInt() {
		return manager.getInt(variable);
	}
	
	//Returns a float
	public float executeFloat() {
		return manager.getFloat(variable);
	}
	
	//Returns a string
	public String executeString() {
		return manager.getString(variable);
	}
	
	public String variableName() {
		return variable;
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