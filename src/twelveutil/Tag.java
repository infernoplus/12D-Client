package twelveutil;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import twelveengine.Log;

public class Tag {
	public String file;
	public String data[];
	
	public ArrayList<TagProperty> properties;
	public TagSubObject objects[];
	
	public Tag(String f) {
		file = f;
		
		openTag(file);
		buildTag();	
	}
	
	//Open the tag file and fill the data array.
	public void openTag(String s) {
		ArrayList<String> file = new ArrayList<String>();
	    try {
			String currentLine;
			DataInputStream fileIn = new DataInputStream(FileUtil.getFile(s));
		    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));
	
		    currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
		    while(currentLine != null) {
		    	file.add(currentLine);
			    currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
		    }
	    }
	    catch(Exception e) {
	    	Log.log("FAILED TO READ TAG: " + s, "Tag", 3);
	    	e.printStackTrace();
	    }
	    data = file.toArray(new String[file.size()]);
	}
	
	//Take the data array and build hashmaps and sub objects.
	public void buildTag() {
		int i = 0; 
		String raw = "";
		while(i < data.length) {
			raw += data[i];
			i++;
		}
		
		properties = new ArrayList<TagProperty>();
		ArrayList<TagSubObject> sub = new ArrayList<TagSubObject>();
		String tag[] = raw.replaceAll("\\}", "};").replaceAll("\\{", "{;").split(";");
		i = 0;
		while(i < tag.length) {
			String type = splitByEntry(tag[i], 0);
			String name = splitByEntry(tag[i], 1);
			if(type.equals("property"))
				putProp(name, splitByEntry(tag[i], 2));
			else if(type.replaceAll("\\{", "").equals("object")) {
				ArrayList<String> obj = new ArrayList<String>();
				i++;
				int left = 1;
				int right = 0;
				while(right < left) {
					if(tag[i].equals("}"))
						right++;
					if(tag[i].contains("{"))
						left++;
					if(right < left)
						obj.add(tag[i] + ";");
					i++;
				}
				i--;
				TagSubObject tso = new TagSubObject(name, obj.toArray(new String[obj.size()]));
				sub.add(tso);
			}
			i++;
		}
		objects = sub.toArray(new TagSubObject[sub.size()]);
	}
	
	//Returns the v'th important entry in that string. Splits basically by whitespace. Accounts for quotation marks.
	public String splitByEntry(String s, int v) {
		int i = 0;
		int k = 0;
		char in[] = s.toCharArray();
		String entry = "";
		while(k <= v && i < in.length) {
			if(in[i] == '{' || in[i] == '}')
				entry = in[i] + "";
			else {
				entry = "";
				while(i < in.length && Character.isWhitespace(in[i]))
					i++;
				while(i < in.length && !Character.isWhitespace(in[i]) && in[i] != '{' && in[i] != '}') {
					if(in[i] == '"') {
						i++;
						while(i < in.length && in[i] != '"' && in[i] != '{' && in[i] != '}') {
							if(in[i] == ';')
								return entry;
							entry += in[i];
							i++;
						}
					}
					else {
						if(in[i] == ';')
							return entry;
						else
							entry += in[i];
						i++;
					}
				}
			}

			k++;
		}
		return entry;
		
	}	
	
	/**DEPRECATED**/
	//TODO: :/  not really a fan of this right now...
	/*public void buildTag() {
		int i = 0;
		properties = new HashMap<String, String>();
		ArrayList<TagSubObject> sub = new ArrayList<TagSubObject>();
		while(i < data.length) {
			if(data[i].startsWith("property")) {
				String p[] = data[i].split("property")[1].trim().split(" ", 2);
				properties.put(p[0].trim(), p[1].trim());
			}
			else if(data[i].startsWith("object") && data[i].contains("{")) {
				String n = data[i].split("object", 2)[1].trim().split("\\{")[0].trim();
				ArrayList<String> obj = new ArrayList<String>();
				i++;
				
				int left = 1;
				int right = 0;
				while(right < left) {
					if(data[i].contains("}"))
						right++;
					if(data[i].contains("{"))
						left++;
					obj.add(data[i]);
					i++;
				}
				TagSubObject tso = new TagSubObject(n, obj.toArray(new String[obj.size()]));
				sub.add(tso);
			}
			i++;
		}
		objects = sub.toArray(new TagSubObject[sub.size()]);
	}*/
	
	//Because hashmap is a fuck, I made my own simple version of it using an arraylist so that i can get pairs by index. fuck you niggers. wasting my time :/
	private void putProp(String k, String v) {
		properties.add(new TagProperty(k,v));
	}
	
	private boolean containsProp(String k) {
		int i = 0;
		while(i < properties.size()) {
			if(k.equals(properties.get(i).key))
				return true;
			i++;
		}
		return false;
	}
	
	private String getProp(String k) {
		int i = 0;
		while(i < properties.size()) {
			if(k.equals(properties.get(i).key))
				return properties.get(i).value;
			i++;
		}
		return "";
	}
	
	@SuppressWarnings("unused")
	private boolean containsValue(String v) {
		int i = 0;
		while(i < properties.size()) {
			if(v.equals(properties.get(i).value))
				return true;
			i++;
		}
		return false;
	}
	
	//For all getProperty methods, the parameters are the name of the property and a default value to return if the property cannot be found.
	//The default type specifies the kind of property we are going to return.
	
	/**Get a property as a String**/
	//By string name...
	public String getProperty(String s, String def) {
		if(containsProp(s))
			return getProp(s);
		return def;
	}
	//By index...
	public String getProperty(int i, String def) {
		if(i < properties.size())
			return properties.get(i).value;
		return def;
	}
	
	/**Get a property as a float**/
	//By string name...
	public float getProperty(String s, float def) {
		if(containsProp(s))
			return Float.parseFloat(getProp(s));
		return def;
	}
	//By index...
	public float getProperty(int i, float def) {
		if(i < properties.size())
			return Float.parseFloat(properties.get(i).value);
		return def;
	}
	
	/**Get a property as a int**/
	//By string name...
	public int getProperty(String s, int def) {
		if(containsProp(s))
			return Integer.parseInt(getProp(s));
		return def;
	}
	//By index...
	public int getProperty(int i, int def) {
		if(i < properties.size())
			return Integer.parseInt(properties.get(i).value);
		return def;
	}
	
	/**Get a property as a boolean**/
	//By string name...
	public boolean getProperty(String s, boolean def) {
		if(containsProp(s))
			return Boolean.parseBoolean(getProp(s));
		return def;
	}	
	//By index...
	public boolean getProperty(int i, boolean def) {
		if(i < properties.size())
			return Boolean.parseBoolean(properties.get(i).value);
		return def;
	}
	
	/** Get a sub object of this tag **/
	public TagSubObject getObject(String s) {
		int i = 0;
		while(i < objects.length) {
			if(objects[i].name.equals(s))
				return objects[i];
			i++;
		}
		Log.log("Failed to get sub-object '" + s + "' in tag '" + file + "' ~ Using a fallback, please fix this!", "Tag", 2);
		return new TagSubObject("NULL", new String[0]);
	}
	
	/** Get a sub object of this tag by index **/
	public TagSubObject getObject(int i) {
		return objects[i];
	}
	
	/** Get total number of sub objects in this tag **/
	public int getTotalObjects() {
		return objects.length;
	}
	
	/** Get total number of properties in this tag **/
	public int getTotalProperties() {
		return properties.size();
	}
}