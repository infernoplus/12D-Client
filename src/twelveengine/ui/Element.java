package twelveengine.ui;

import static org.lwjgl.opengl.GL11.*;
import twelveengine.graphics.Shader;

public class Element {
	public Ui ui;
	
	public String name;
	public int frames[];
	
	public char align[];
	public float position[];
	public float size[];
	
	public Shader shader;
	public float scalars[];
	
	public boolean visible = false;
	public boolean focus = false;
	public Element(Ui u, String n, int r[], String f, float x, float y, float w, float h, String s, String t) {
		ui = u;
		
		name = n;
		frames = r;
		
		align = new char[2];
		position = new float[2];
		size = new float[2];
		scalars = new float[4];
		
		setPosition(x,y);
		setSize(w,h);
		setAlign(s,t);
		setScalars(0,0,0,0);
		shader = ui.game.getShader(f);
	}
	
	public Element(Ui u, String n, int r[], String f, float x, float y, float w, float h, String s, String t, float a, float b, float c, float d) {
		ui = u;
		
		name = n;
		frames = r;
		
		align = new char[2];
		position = new float[2];
		size = new float[2];
		scalars = new float[4];
		
		setPosition(x,y);
		setSize(w,h);
		setAlign(s,t);
		setScalars(a,b,c,d);
		shader = ui.game.getShader(f);
	}
	
	//On each step
	public void step() {
		
	}
	
	//When not being moused over or clicked on
	public void untouched() {
		
	}
	
	//When moused over
	public void over() {
		
	}
	
	//When clicked on
	public void clicked() {
		
	}
	
	//When is the focus of input
	public void focus() {
		
	}
	
	//When this element recieves a keystroke
	public void input(char c) {
		
	}
	
	public void setPosition(float x, float y) {
		position[0] = x; position[1] = y;
	}
	
	public void setSize(float w, float h) {
		size[0] = w; size[1] = h;
	}
	
	//When the setInput() command is invoked through the UI script on this element. Set relevant input field or text item to the value s.
	public void setInput(String s) {
		
	}
	
	//When the getInput() command is invoked through the UI script on this element. Return input such as text in a text field or state of a button.
	public String getInput() {
		return "";
	}
	
	//When the clear() command is invoked through the UI script on this element. Clear any input stuff.
	public void clear() {
		
	}
	
	//Top, Left, Bottom, Right, Center = tlbrc
	public void setAlign(String s, String t) {	
		if(s.contains("left"))
			align[0] = 'l';
		else if(s.contains("right"))
			align[0] = 'r';
		else if(s.contains("center"))
			align[0] = 'c';
		else 
			System.err.println("UI ERROR: Bad alignment type: " + s + " in ui " + ui.file + ". ~ Was looking for left, right, or center.");
		
		if(t.contains("top"))
			align[1] = 't';
		else if(t.contains("bottom"))
			align[1] = 'b';
		else if(t.contains("center"))
			align[1] = 'c';
		else 
			System.err.println("UI ERROR: Bad alignment type: " + t + " in ui " + ui.file + ". ~ Was looking for top, bottom, or center.");
	}
	
	public void setScalars(float a, float b, float c, float d) {
		scalars[0] = a;
		scalars[1] = b;
		scalars[2] = c;
		scalars[3] = d;
	}
	
	//Test to see if coordinates x, y are inside the elements bounds.
	public boolean contains(int x, int y) {
		float f[] = getScreenSpaceCoordinates(ui.screenWidth, ui.screenHeight);
		if(x > f[0] && x < f[2] && y > f[1] && y < f[3])
			return true;
		return false;
	}
	
	public boolean inFrame(int f) {
		int i = 0;
		while(i < frames.length) {
			if(f == frames[i])
				return true;
			i++;
		}
		return false;
	}
	
	//Returns the element in screen space (topleft = 0,0) accounting for screen size, resolution, UI scale, alignment and size. For drawing and testing bounds and stuff.
	//Returns an array of 4 floats: tl.x, tl.y, br.x, br.y
	public float[] getScreenSpaceCoordinates(int width, int height) {
		float ax = 0, ay = 0;
		float bx = 0, by = 0;
		
		//X coords
		if(align[0] == 'l') {
			ax = position[0];
			bx = position[0]  + size[0];
		}
		if(align[0] == 'r') {
			ax = width - (position[0] + size[0]);
			bx = width - position[0];
		}
		if(align[0] == 'c') {
			ax = ((width/2) - (size[0]/2)) + position[0];
			bx = ((width/2) + (size[0]/2)) + position[0];
		}
		
		//Y coords
		if(align[1] == 't') {
			ay = position[1];
			by = position[1] + size[1];
		}
		if(align[1] == 'b') {
			ay = height - (position[1] + size[1]);
			by = height - position[1];
		}
		if(align[1] == 'c') {
			ay = ((height/2) - (size[1]/2)) + position[1];
			by = ((height/2) + (size[1]/2)) + position[1];
		}
		return new float[] {ax, ay, bx, by};
	}
	
	public void draw(float f, int width, int height) {
		float v[] = getScreenSpaceCoordinates(width, height);
		shader.glMaterialSet(ui.game.engine.graphics.activeProgram(shader.glslProgram, f),scalars);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);	glVertex3f(v[0],v[1],0);
		glTexCoord2f(1, 0);	glVertex3f(v[2],v[1],0);
		glTexCoord2f(1, 1);	glVertex3f(v[2],v[3],0);
		glTexCoord2f(0, 1);	glVertex3f(v[0],v[3],0);
		glEnd();
	}
}