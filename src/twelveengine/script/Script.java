package twelveengine.script;

public class Script {
	public ScriptManager manager;
	
	public String code; //DEBUG
	
	public String name;
	
	public boolean running;
	public boolean loop;
	
	public boolean client;
	public boolean server;
	
	public ScriptCall calls[];
	
	public Script(ScriptManager w, String r, String t, String n, ScriptCall c[]) {
		manager = w;
		name = n;
		calls = c;
		
		if(r.equals("client"))
			client = true;
		else if(r.equals("server"))
			server = true;
		else {
			client = true;
			server = true;
		}
		
		running = false;
		
		if(t.equals("initial")) {
			loop = false;
			execute();
		}
		else if(t.equals("continuous")) {
			loop = true;
			execute();
		}
		else { //Seeing as it's not the first two, it's the third.
			loop = false;
		}
		
	}
	
	private int e = 0;
	private boolean wait = false;
	public void step() {
		if(running) {
			if(e < calls.length) {
				if(wait) {
					if(calls[e].done) {
						wait = false;
						e++;
					}
					else
						calls[e].step();
				}
				else {
					while(e < calls.length && calls[e].done && !wait) {
						calls[e].executeVoid();
						if(calls[e].done)
							e++;
						else {
							wait = true;
							calls[e].step();
						}
					}
				}
			}
		}
		
		if(e >= calls.length) {
			running = false;
		}
		if(!running && loop)
			execute();
	}
	
	//Start a script that is not running. Only really applies dormant scripts for obv reasons.
	public void execute() {
		if(client) {
			e = 0;
			running = true;
		}
	}
	
	public void debugTree(int i) {
    	int j = 0;
    	System.out.println(" == " + name);
		System.out.println("- " + code);
    	while(j < calls.length) {
    		calls[j].debugTree(2);
    		j++;
    	}
    	System.out.println("");
	}
}