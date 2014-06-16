package twelveengine;

import java.util.ArrayList;

public class Log {
	public static ArrayList<Entry> entries = new ArrayList<Entry>();
	
	public static synchronized void log(String txt, String id, int lvl) {
		if(txt.isEmpty())
			return;
		Entry e = new Entry(txt, id, lvl);
		entries.add(e);
		if(Settings.getBool("log2console"))
			if(e.level() >= Settings.getInt("loglevel"))
				if(e.level() >= 2)
					System.err.println(e.get());
				else
					System.out.println(e.get());
	}
	
	public static synchronized void log(String txt, String id) {
		if(txt.isEmpty())
			return;
		Entry e = new Entry(txt, id);
		entries.add(e);
		if(Settings.getBool("log2console"))
			if(e.level() >= Settings.getInt("loglevel"))
				if(e.level() >= 2)
					System.err.println(e.get());
				else
					System.out.println(e.get());
	}
}

class Entry {
	private String identifier;
	private String text;
	private int level; //0 normal, 1 warning, 2 error, 3 critical error
	private String lvltxt;
	
	public Entry(String t, String id, int lvl) {
		identifier = id;
		text = t;
		level = lvl;
		if(lvl == 1)
			lvltxt = "[Warning]";
		else if(lvl == 2)
			lvltxt = "[Error]";
		else if(lvl > 2)
			lvltxt = "[Critical]";
		else
			lvltxt = "";
	}
	
	public Entry(String t, String id) {
		identifier = id;
		text = t;
		level = 0;
		lvltxt = "";
	}
	
	public boolean is(String s) {
		return identifier.equals(s);
	}
	
	public int level() {
		return level;
	}
	
	public String get() {
		return lvltxt + "[" + identifier + "]" + text;
	}
}