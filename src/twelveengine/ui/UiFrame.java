package twelveengine.ui;

public class UiFrame {
	//This class is a container for frames in a Ui object. This is simply so I don't have to use a bunch of seperate arrays or linked lists etc etc etc
	
	public String name;
	public int entry; //The index in the array of frames for this Ui. //TODO: kind of dumb , maybe rewrite so unnecessary.
	public String initScript; //Script that's run the first time the frame becomes "active".
	public String activeScript; //Script that's run each time this frame becomes the "active" frame of ui (via the Ui.setFrame() method)
	
	public boolean init = false;
	
	public UiFrame(String n, int e, String i, String a) {
		name = n;
		entry = e;
		initScript = i;
		activeScript = a;
	}
}