package twelveengine.script;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import twelveengine.Game;
import twelveutil.FileUtil;

public class ScriptManager {
	public Game game;
	public String file;
	
	//TODO: Someone should definitely write some System.err.print's for compiling problems. Right now if a script doesn't compile it usually doesn't say anything and just doesn't work :P
	//Variable names and their values
	public String[] values;	public String[] valueNames;
	
	public Script scripts[];
	
	public ScriptManager(Game w, String f) {
		game = w;
		file = f;

		try {
			create(f);
		} catch (Exception e) {
			System.err.println("Failed to read script file ~ : " + f);
			scripts = new Script[0];
			e.printStackTrace();
		}
	}
	
	public void wakeScript(String name) {
		int i = 0;
		while(i < scripts.length) {
			if(scripts[i].name.equals(name)) {
				scripts[i].execute();
				return;
			}
			i++;
		}
		
		System.out.println("nothing matching: " + name);
	}
	
	public void step() {
		int i = 0;
		while(i < scripts.length) {
			scripts[i].step();
			i++;
		}
	}
	
	public void create(String f) throws Exception {
		ArrayList<String> raw = new ArrayList<String>();
		
		int p = 0;
		String s = "";
		
		char c[];
		
		String currentLine;
		DataInputStream fileIn = new DataInputStream(FileUtil.getFile(f));
	    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));
	    
    	currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
	    while(currentLine != null) {
	    	//Read till we have a script, then put it in raw so we can parse it.
	    	c = currentLine.toCharArray();
	    	int i = 0;
	    	while(i < c.length) {
	    		if(c[i] == '(')
	    			p++;
	    		if(c[i] == ')')
	    			p--;
	    		i++;
	    	}
	    	s += currentLine;
	    	
	    	if(p == 0) {
	    		if(!s.trim().isEmpty())
	    			raw.add(s);
	    		s = "";
	    	}
	    	
	    	currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
	    }
	    fileReader.close();
	    
    	System.out.println("------------RAW SCRIPT-----------");
	    int i = 0;
	    while(i < raw.size()) {
	    	System.out.println(raw.get(i));
	    	i++;
	    }
    	System.out.println("------------END-----------");
	    
	    i = 0;
	    
	    int x = 0;
	    
	    //How many vars do we need?
	    while(i < raw.size()) {
	    	String sc[] = scriptSpaceSplit(raw.get(i));
    		sc[0] = sc[0].replaceAll("\\(", "");
    		sc[0] = sc[0].replaceAll("\\)", "");
    		if(sc[0].trim().equals("var"))
    			x++;
	    	i++;
	    }
	    
	    //This Many!
	    values = new String[x];	valueNames = new String[x];
	    
	    i = 0;
	    x = 0;
	    
	    //Now lets fill those arrays with the vars
	    while(i < raw.size()) {
	    	String sc[] = scriptSpaceSplit(raw.get(i));
	    	int j = 0;
	    	while(j < sc.length) {
	    		sc[j] = sc[j].replaceAll("\\(", "");
	    		sc[j] = sc[j].replaceAll("\\)", "");
	    		j++;
	    	}
	    	if(sc[0].startsWith("var")) {
		    	values[x] = sc[3]; valueNames[x] = sc[2];
		    	x++;
	    	}
	    		
	    	i++;
	    }
	    
	    System.out.println("---------Variables----------");
	    i = 0;
	    while(i < values.length) {
	    	System.out.println(valueNames[i] + " = " + values[i]);
	    	i++;
	    }
	    System.out.println("---------END----------");
	   
	    //Begin breaking scripts apart and interpreting them into ScriptCalls
	    ArrayList<Script> scpts = new ArrayList<Script>();
	    i = 0;
	    while(i < raw.size()) {
	    	if(scriptSpaceSplit(raw.get(i))[0].equals("(script")) {
		    	String script = raw.get(i);
	    	    ArrayList<ScriptCall> calls = new ArrayList<ScriptCall>(); //ArrayList<String> cid = new ArrayList<String>();
		    	int cid = 0;
	    	    int it = 0;
    			System.out.println("--------------BEGIN-----------------");
	    		while(script.replaceFirst("\\(", "").contains("(")) {
	    			String start = "";
	    			String func = "";
	    			String end = "";
	    			
	    			char a[] = script.toCharArray();
	    			
	    			int u = 0; boolean bu = true;
	    			int v = 0; boolean bv = true;
	    			int j = 0;
	    			while(bu) {
	    				if(a[j] == ')') {
	    					u = j;
	    					bu = false;
	    				}
	    				else
	    					j++;
	    			}
	    			while(bv) {
	    				if(a[j] == '(') {
	    					v = j;
	    					bv = false;
	    				}
	    				j--;
	    			}
	    			
	    			j = 0;
	    			while(j < v) {
	    				start += a[j];
	    				j++;
	    			}
	    			while(j < u+1) {
	    				func += a[j];
	    				j++;
	    			}
	    			while(j < a.length) {
	    				end += a[j];
	    				j++;
	    			}
	    			ScriptCall call = createScriptCall(func, calls, cid);
	    			calls.add(call);
	    			System.out.println("--------------Iteration:" + it + "-----------------");
	    			System.out.println(start + " || " + func + " || " + end);
	    			script = start + " <" + cid + "> " + end;
	    			cid++;
	    			it++;
	    		}
    			System.out.println("--------------DONE-----------------");
	    		System.out.println(script);
	    		String fs[] = scriptSpaceSplit(script.replaceAll("\\(", "").replaceAll("\\)", "").trim());
	    		int cids[] = new int[fs.length - 4];
	    		int j = 0;
	    		while(j < cids.length) {
	    			cids[j] = Integer.parseInt(fs[j+4].replaceAll("<","").replaceAll(">","").trim());
	    			j++;
	    		}
	    		ScriptCall scs[] = new ScriptCall[cids.length];
	    		j = 0;
	    		while(j < scs.length) {
	    			scs[j] = getCall(calls, cids[j]);
	    			j++;
	    		}
	    		Script fnl = new Script(this, fs[1], fs[2], fs[3], scs);
	    		fnl.code = script; // DEBUG
	    		scpts.add(fnl);
    			System.out.println("--------------END-----------------");
	    	}
	    	i++;
	    }
	   
	    //Put finished scripts into array. DONE~!
	    scripts = new Script[scpts.size()];
	    i = 0;
	    while(i < scripts.length) {
	    	scripts[i] = scpts.get(i);
	    	i++;
	    }
	    
	    System.out.println("-----------FINAL SCRIPTS-----------");
	    i = 0;
	    while(i < scripts.length) {
	    	scripts[i].debugTree(0);
	    	i++;
	    }
	    System.out.println("-----------END-----------");
	}
	
	public ScriptCall getCall(ArrayList<ScriptCall> calls, int cid) {
		int i = 0;
		while(i < calls.size()) {
			if(calls.get(i).cid == cid)
				return calls.get(i);
			i++;
		}
		return null;
		
	}
	
	public ScriptCall createScriptCall(String s, ArrayList<ScriptCall> calls, int cid) {
		int lcids = -1;
		ArrayList<ScriptCall> lsc = new ArrayList<ScriptCall>();
		
		String fs[] = scriptSpaceSplit(s.replaceAll("\\(", "").replaceAll("\\)", "").trim());
		int cids[] = new int[fs.length - 1];
		int j = 0;
		String code = "(" + fs[0]; //DEBUG
		while(j < cids.length) {
			if(fs[j+1].trim().indexOf("<") == 0 && fs[j+1].trim().indexOf(">") == fs[j+1].trim().length()-1) {
				cids[j] = Integer.parseInt(fs[j+1].replaceAll("<","").replaceAll(">","").trim());
				code += " " + fs[j+1];
			}
			else {
				lsc.add(createVariableCall(fs[j+1],lcids,0));
				cids[j] = lcids;
				code += " <" + lcids + ">";
				lcids--;
			}
			j++;
		}
		code += ")";
		ScriptCall scs[] = new ScriptCall[cids.length];
		j = 0;
		while(j < scs.length) {
			if(cids[j] >= 0)
				scs[j] = getCall(calls, cids[j]);
			else {
				int i = 0;
				while(i < lsc.size()) {
					if(lsc.get(i).cid == cids[j]) {
						scs[j] = lsc.get(i);
					}
					i++;
				}
			}	
			j++;
		}
		ScriptCall fin = createCallType(fs[0], cid, scs);
		fin.code = code;
		return fin;
	}
	
	//Looks up the function class that this will use.
	/**REGISTER ANY NEW SCRIPTCALLS HERE!**/
	//TODO: More of these. We need moar.
	public ScriptCall createCallType(String s, int cid, ScriptCall scs[]) {
		if(s.equals("log"))
			return new ScriptCallLog(this, cid, scs);
		if(s.equals("sleep"))
			return new ScriptCallSleep(this, cid, scs);
		if(s.equals("until"))
			return new ScriptCallSleepUntil(this, cid, scs);
		if(s.equals("if"))
			return new ScriptCallIf(this, cid, scs);
		if(s.equals("begin"))
			return new ScriptCallBegin(this, cid, scs);
		if(s.equals("while"))
			return new ScriptCallWhile(this, cid, scs);
		if(s.equals("<"))
			return new ScriptCallLess(this, cid, scs);
		if(s.equals(">"))
			return new ScriptCallGreater(this, cid, scs);
		if(s.equals("<="))
			return new ScriptCallLequal(this, cid, scs);
		if(s.equals(">="))
			return new ScriptCallGrequal(this, cid, scs);
		if(s.equals("=="))
			return new ScriptCallEquals(this, cid, scs);
		if(s.equals("add"))
			return new ScriptCallAdd(this, cid, scs);
		if(s.equals("set"))
			return new ScriptCallSet(this, cid, scs);
		if(s.equals("actor"))
			return new ScriptCallGetActor(this, cid, scs);
		if(s.equals("spawn"))
			return new ScriptCallSpawn(this, cid, scs);
		if(s.equals("invoke"))
			return new ScriptCallInvoke(this, cid, scs);
		
		return new ScriptCall(this, cid, scs);
	}
	
	public ScriptCall createVariableCall(String s, int cid, int type) {
		int i = 0;
		while(i < valueNames.length) {
			if(s.equals(valueNames[i])) { //Do we have a variable for this?
				ScriptCall v = new ScriptCallVariable(this, cid, new ScriptCall[0], s);
				v.code = s;
				return v;
			}
			i++;
		}
		
		//If no variable matches this it's a constant...
		ScriptCall v = new ScriptCallConstant(this, cid, new ScriptCall[0], s);
		v.code = s;
		return v;
	}
	
	public String[] scriptSpaceSplit(String s) {
		int i = 0;
		boolean b = true;
		String q[] = s.split("\"");
		ArrayList<String> split = new ArrayList<String>();
		while(i < q.length) {
			if(b) {
				String x[] = q[i].split(" ");
				int j = 0;
				while(j < x.length) {
					if(!x[j].equals("") && !x[j].equals(" "))
						split.add(x[j]);
					j++;
				}
			}
			else {
				if(!q[i].equals("") && !q[i].equals(" "))
					split.add(q[i]);
			}
			i++;
			b = !b;
		}
		i = 0;
		String fn[] = new String[split.size()];
		while(i < fn.length) {
			fn[i] = split.get(i);
			i++;
		}
		return fn;
	}
	
	//Automatically trys to cast to requested data type. If fails then returns a default value.
	public boolean getBoolean(String s) {
		try {
			int i = 0;
			while(i < valueNames.length) {
				if(valueNames[i].equals(s))
					return Boolean.parseBoolean(values[i]);
				i++;
			}
		}
		catch(Exception e) {
			System.err.println("##ERROR## Script tried to cast value to boolean and failed");
		}
		return false;
	}
	
	public int getInt(String s) {
		try {
			int i = 0;
			while(i < valueNames.length) {
				if(valueNames[i].equals(s))
					return Integer.parseInt(values[i]);
				i++;
			}
		}
		catch(Exception e) {
			System.err.println("##ERROR## Script tried to cast value to int and failed");
		}
		return 0;
	}
	
	public float getFloat(String s) {
		try {
			int i = 0;
			while(i < valueNames.length) {
				if(valueNames[i].equals(s))
					return Float.parseFloat(values[i]);
				i++;
			}
		}
		catch(Exception e) {
			System.err.println("##ERROR## Script tried to cast value to float and failed");
		}
		return 0;
	}
	
	public String getString(String s) {
		int i = 0;
		while(i < valueNames.length) {
			if(valueNames[i].equals(s))
				return values[i];
			i++;
		}
		return "";
	}
	
	public void setVariable(String var, String val) {
		int i = 0;
		while(i < valueNames.length) {
			if(valueNames[i].equals(var)) {
				values[i] = val;
				return;
			}
			i++;
		}
	}
}