package twelveengine.ui;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import twelveengine.Engine;

public class Input {
	public Engine engine;
	
	public boolean mouseCaptured = false;
	public int mousePosition[] = new int[2];
	public int mouseMovement[] = new int[2];
	public int mouseWheel = 0;
	public boolean mouseButton[] = new boolean[3];
	public boolean mouseClick[] = new boolean[3];
	public boolean mouseRelease[] = new boolean[3];

	public boolean keyboard[] = new boolean[89];
	
	public Input(Engine e) {
		engine = e;
		try {
			Mouse.create();
			Keyboard.create();
			Keyboard.enableRepeatEvents(true);
		} catch (LWJGLException ex) {
			System.err.println("Can't initialize mouse and keyboard...");
			ex.printStackTrace();
		}
	}
	
	public void mouseUpdate() {
		//Mouse pointer positions
		mouseWheel += Mouse.getDWheel();
		mouseMovement[0] = mousePosition[0] - Mouse.getX();
		mouseMovement[1] = mousePosition[1] - Mouse.getY();
		
		Mouse.setGrabbed(mouseCaptured);
		if(mouseCaptured)
			Mouse.setCursorPosition(engine.graphics.displayWidth/2, engine.graphics.displayHeight/2);
		
		mousePosition[0] = Mouse.getX();
		mousePosition[1] = Mouse.getY();
		
		//Apply mouse changes to stuff
		if(engine.game != null && mouseCaptured)
			engine.game.player.readMouse(mouseButton[0], mouseButton[1], mouseButton[2], mouseMovement[0], mouseMovement[1]);
	}
	
	//I assume this is correct, all standard keys are between 1 and 89. So we just check those and nothing else for gameplay.
	//TODO: maybe optimize to only check keys that are used in config... dunno...
	public void keyboardUpdate() {		
		//Mouse buttons
		int i = 0;
		boolean b = false;
		while(i < 3) {
			b = Mouse.isButtonDown(i);
			//Check for a mouse button click
			if(!mouseButton[i] && b)
				mouseClick[i] = true;
			else
				mouseClick[i] = false;
			
			//Check for a mouse button release
			if(mouseButton[i] && !b)
				mouseRelease[i] = true;
			else
				mouseRelease[i] = false;
			
			//Record mouse button state
			mouseButton[i] = b;
			i++;
		}
		
		//Keyboard keys
		i = 0;
		while(i < 89) {
			//Record key state
			keyboard[i] = Keyboard.isKeyDown(i);
			i++;
		}
		
		//Record keystrokes
		//Keystrokes, this is for input into text boxes and crap. Not used for gameplay. Ever. Got it scrub?
		while(Keyboard.next()) {
			if(Keyboard.isKeyDown(Keyboard.getEventKey())) {
				char c = Keyboard.getEventCharacter();
				if(Character.isLetterOrDigit(c)) {
					if(keyboard[42]) //if left shift is held down
						keystroke(Character.toUpperCase(c));
					else
						keystroke(Character.toLowerCase(c));
				}
				else if(isPunctuation(c))
					keystroke(c);
				else if(Character.isWhitespace(c))
					keystroke(c);
				else if(Keyboard.getEventKey() == 14)
					keystroke(c);
				else if(Keyboard.getEventKey() == 28)
					keystroke('\n');
				//System.out.println(Keyboard.getEventKey());
			}
		}
	}
	
	final char punc[] = new char[] {
			'.', '/', '?', '<', '>', ',', ':', ';', '\'',
			'\"', '!', '@', '#', '$', '%', '^', '&', '*',
			'(', ')', '-', '=', '_', '+', '[', '{',  ']',
			'}', '\\', '|', '`', '~'
	};
	public boolean isPunctuation(char c) {
		int i = 0;
		while(i < punc.length) {
			if(c == punc[i])
				return true;
			i++;
		}
		return false;
	}
	
	public void keystroke(char c) {
		if(!mouseCaptured)
			engine.ui.input(c);
	}
}