package twelveengine.ui;

import java.util.ArrayList;

import twelveengine.Game;
import twelveengine.Settings;
import twelveengine.audio.Sound;
import twelveutil.Tag;
import twelveutil.TagSubObject;

public class Ui {
	public Game game;
	
	public String name;
	public String file;
	
	public boolean useMouse; //Used in cases like the console ui where we don't want mouse input messing with stuff
	
	public ArrayList<UiFrame> frames;
	public UiFrame current;
	
	public ArrayList<Element> elements;
	public ArrayList<Sound> sounds;
	
	public Element focus;
	
	public int screenWidth = 0;
	public int screenHeight = 0;
	
	public Ui(Game w, String s) {
		game = w;
		try {
			createHud(s);
		}
		catch(Exception e) {
			System.err.println("Failed to create hud ~" + s);
			e.printStackTrace();
		}
		setFrame(0);
	}
	
	public void step() {
		int i = 0;
		Input in = game.engine.input;
		Element e;
		boolean clicked = false;
		while(i < elements.size()) {
			e = elements.get(i);
			if(e.visible) {
				if(!in.mouseCaptured && useMouse) //We are not taking input during gameplay
					if(e.contains(in.mousePosition[0], screenHeight - in.mousePosition[1])) { //Remember to convert mouse to topleft = 0,0 screen space *wink*
						e.over();
						if(in.mouseClick[0]) {
							setFocus(e);
							clicked = true;
						}
				}
				else
					e.untouched();
				if(e.focus)
					e.focus();
			}
			else
				e.untouched();
			e.step();
			i++;
		}
		if(!in.mouseCaptured && useMouse) //We are not taking input during gameplay
			if(clicked)
				focus.clicked();
			else if(in.mouseClick[0])
				setFocus(null);
			
	}
	
	public void input(char c) {
		if(focus != null)
			focus.input(c);
	}
	
	// Reads the given hud file and creates it, or dies trying
	public void createHud(String f) throws Exception {
		Tag tag = game.tagger.openTag(f);
		
		name = tag.getProperty("name", "defaulthud");
		useMouse = tag.getProperty("useMouse", true);
		
		//Creates frames...
		frames = new ArrayList<UiFrame>();
		TagSubObject frms = tag.getObject("frames");
		int i = 0;
		int j = frms.getTotalObjects();
		while(i < j) {
			TagSubObject frm = frms.getObject(i);
			UiFrame uifrm = new UiFrame(frm.getProperty("name", "defaultFrame"), i, frm.getProperty("initscript", " "), frm.getProperty("activescript", " "));
			frames.add(uifrm);
			i++;
		}
		
		//Creates sounds
		sounds = new ArrayList<Sound>();
		//TODO: something goes here.
		
		//Creates elements
		elements = new ArrayList<Element>();
		TagSubObject elms = tag.getObject("elements");
		i = 0;
		j = elms.getTotalObjects();
		while(i < j) {
			elements.add(createElement(elms.getObject(i)));
			i++;
		}
	}
	
	//Takes a tagsubobject of an element and creates it. this is it's own method for organization. there are a lot of types of elements and this is sort of messy.
	private Element createElement(TagSubObject elm) {
		String type = elm.getProperty("type", "element"); //Find the type of element we are creating.
		
		/** We go ahead and get all the properties that are shared between ALL types elements. Then we get the unique ones in the relative if(type) block **/
		String na = elm.getProperty("name", "default"); //name
		String shd = elm.getProperty("shader", "default"); //shader
		int i = 0;
		TagSubObject frms = elm.getObject("frames"); //frames that this is drawn in
		int r[] = new int[frms.getTotalProperties()];
		while(i < r.length) {
			r[i] = frms.getProperty(i, 0);
			i++;
		}
		TagSubObject bnds = elm.getObject("bounds"); //bounds, pos x,y size w,h in pixels
		int x = bnds.getProperty("x", 0);
		int y = bnds.getProperty("y", 0);
		int w = bnds.getProperty("w", 32);
		int h = bnds.getProperty("h", 32);
		TagSubObject algn = elm.getObject("align"); //alignment top,bottom,left,right,center
		String hor = algn.getProperty("horizontal", "top");
		String ver = algn.getProperty("vertical", "left");
		
		/** Now for values of specific element types **/
		if(type.equals("input")){
			String txt = elm.getProperty("text", ""); //text in this box by default
			String cs = elm.getProperty("clickscript", ""); //script when clicked on
			String as = elm.getProperty("activescript", ""); //script when activated "enter"
			return new ElementInput(this, na, r, shd, x, y, w, h, hor, ver, txt, as, cs);
		}
		else if(type.equals("button")) {
			String cs = elm.getProperty("clickscript", ""); //script when clicked on
			return new ElementButton(this, na, r, shd, x, y, w, h, hor, ver, cs);
		}
		else if(type.equals("text")) {
			return null;
		}
		//If all else fails, it's an element.			
		return new Element(this, na, r, shd, x, y, w, h, hor, ver);
	}
	
	//Sets frame by index <i>
	public void setFrame(int i) {
		if(i < frames.size()) {
			current = frames.get(i);
			int j = 0;
			Element e;
			while(j < elements.size()) {
				e = elements.get(j);
				e.visible = e.inFrame(current.entry);
				if(e.focus && !e.visible)
					setFocus(null);
				j++;
			}
			if(!current.init)
				runScript(current.initScript);
			runScript(current.activeScript);
		}
	}
	
	//Sets frame by name <s>
	public void setFrame(String s) {
		int i = 0;
		while(i < frames.size()) {
			if(s.equals(frames.get(i).name)) {
				setFrame(i);
				return;
			}
			i++;
		}
	}
	
	public void setFocus(Element e) {
		if(focus != null)
			focus.focus = false;
		focus = e;
		if(focus != null)
			focus.focus = true;
	}
	
	//Gets an element by it's name and returns it.
	public Element getElement(String s) {
		int i = 0;
		while(i < elements.size()) {
			if(elements.get(i).name.equals(s))
				return elements.get(i);
			i++;
		}
		return null;
	}
	
	//Finds the element with the name <s> and then set its scalars to the values listed.
	public void setScalar(String s, float a, float b, float c, float d) {
		int i = 0;
		while(i < elements.size()) {
			if(s.equals(elements.get(i).name)) {
					elements.get(i).setScalars(a,b,c,d);
					return;
			}
			i++;
		}
	}
	
	//TODO: jesus christ how horrifying.
	//Reads and executes a UI script. UI scripts generally use the console to change settings or use special commands to manipulate the UI.
	//UI scripts are somewhat limited but can perform many operations.
	//UI scripts are method based. Example: setInput(input mp name, getSetting(userName));
	public void runScript(String s) {
		try {
		String c[] = s.split("/");
		int i = 0;
		while(i < c.length) {
			String sub[] = c[i].trim().split("\\(");
			String com = sub[sub.length-1];
			int j = sub.length-2;
			while(j >= 0) {
				if(sub[j].contains("getInput")) {
					String par = com.split("\\)")[0].trim();
					String rtrn = getElement(par).getInput();
					com = sub[j].split("getInput", -2)[0] + rtrn + com.split("\\)", 2)[1];
				}
				else if(sub[j].contains("getSetting")) {
					String par = com.split("\\)")[0];
					String rtrn = Settings.getString(par);
					com = sub[j].split("getSetting", -2)[0] + rtrn + com.split("\\)", 2)[1];
				}
				else
					com = sub[j] + "(" + com;
				j--;
			}
			
			if(com.startsWith("setInput("))
				getElement(com.split("\\(")[1].split(",")[0].trim()).setInput(com.split(",")[1].split("\\)")[0].trim());
			else if(com.startsWith("console("))
				game.engine.console.parse(com.split("\\(")[1].split("\\)")[0].trim());
			else if(com.startsWith("focus("))
				setFocus(getElement(com.split("\\(")[1].split("\\)")[0].trim()));
			else if(com.startsWith("goto("))
				setFrame(com.split("\\(")[1].split("\\)")[0].trim());
			else if(com.startsWith("clear("))
				getElement(com.split("\\(")[1].split("\\)")[0].trim()).clear();
			else if(com.startsWith("sound(")) {
				sounds.get(Integer.parseInt(com.split("\\(")[1].split("\\)")[0].trim())).playSound();
			}
			i++;
		}
		}
		catch(Exception e) {
			System.err.println("UI." + name + " Failed to parse script: " + s);
		}
	}
	
	public void draw(float f, int width, int height) {
		screenWidth = width;
		screenHeight = height;
		
		int i = 0;
		Element e;
		while(i < elements.size()) {
			e = elements.get(i);
			if(e.inFrame(current.entry))
				e.draw(f, width, height);
			i++;
		}
	}
}