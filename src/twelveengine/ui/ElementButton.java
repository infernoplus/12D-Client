package twelveengine.ui;

public class ElementButton extends Element {
	public String command;
	
	public ElementButton(Ui u, String n, int r[], String f, float x, float y, float w, float h, String s, String t, String com) {
		super(u, n, r, f, x, y, w, h, s, t);
		command = com;
	}
	
	public ElementButton(Ui u, String n, int r[], String f, float x, float y, float w, float h, String s, String t, float a, float b, float c, float d, String com) {
		super(u, n, r, f, x, y, w, h, s, t, a, b, c, d);
		command = com;
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
		if(scalars[2] > 0)
			scalars[2] = 0;
		else
			scalars[2] = 1;
		ui.runScript(command);
	}
	
	//When is the focus of input
	public void focus() {

	}
	
	//Test to see if coordinates x, y are inside the buttons bounds.
	public boolean contains(int x, int y) {
		float v[] = getScreenSpaceCoordinates(ui.screenWidth, ui.screenHeight);
		if(x > v[0] && x < v[2] && y > v[1] && y < v[3])
			return true;
		return false;
	}
}