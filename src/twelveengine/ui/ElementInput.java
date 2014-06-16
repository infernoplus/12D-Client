package twelveengine.ui;

import static org.lwjgl.opengl.GL11.*;

public class ElementInput extends Element {
	public String text;
	public String activeScript;
	public String clickScript;
	
	public ElementInput(Ui u, String n, int r[], String f, float x, float y, float w, float h, String s, String t, String txt, String as, String cs) {
		super(u, n, r, f, x, y, w, h, s, t);
		text = txt;
		activeScript = as;
		clickScript = cs;
	}
	
	public ElementInput(Ui u, String n, int r[], String f, float x, float y, float w, float h, String s, String t, float a, float b, float c, float d, String txt, String as, String cs) {
		super(u, n, r, f, x, y, w, h, s, t, a, b, c, d);
		text = txt;
		activeScript = as;
		clickScript = cs;
	}
	
	//On each step
	public void step() {
		
	}
	
	//When not being moused over or clicked on
	public void untouched() {
		scalars[3] = 0;
	}
	
	//When moused over
	public void over() {
		scalars[3] = 1;
	}
	
	//When clicked on
	public void clicked() {
		ui.runScript(clickScript);
	}
	
	//When is the focus of input
	public void focus() {
		scalars[3] = 1;
	}
	
	//When this element recieves a keystroke
	public void input(char c) {
		if(c == '\b') {
			char d[] = text.toCharArray();
			text = "";
			int i = 0;
			while(i < d.length - 1) {
				text += d[i];
				i++;
			}
		}
		else if(c == '\n' || c == '\r')
			ui.runScript(activeScript);
		else
			text = text + c;
	}
	
	//When the setInput() command is invoked through the UI script on this element. Set relevant input field or text item to the value s.
	public void setInput(String s) {
		text = s;
	}
	
	//When the getInput() command is invoked through the UI script on this element. Return input such as text in a text field or state of a button.
	public String getInput() {
		return text;
	}
	
	//When the clear() command is invoked through the UI script on this element. Clear any input stuff.
	public void clear() {
		text = "";
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
		
		glDisable(GL_TEXTURE_2D);
		ui.game.engine.graphics.activeProgram(0, f);
		glColor4f(0.466f, 0.654f, 1.0f, 1.0f);
		SimpleText.drawString(text, (int)v[0] + 1, (int)v[3] - 2);
		glEnable(GL_TEXTURE_2D);
		
	}
}