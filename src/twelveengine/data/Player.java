package twelveengine.data;

import java.util.ArrayList;

import twelveengine.Game;
import twelveengine.actors.*;
import twelveengine.graphics.*;
import twelveengine.ui.*;
import twelvelib.net.packets.Packet20Location;
import twelvelib.net.packets.Packet21Rotation;
import twelvelib.net.packets.Packet31Pickup;
import twelvelib.net.packets.Packet32Drop;
import twelveutil.MathUtil;

public class Player {
	public Game game;
	public Input input;
	
	public int i = 0;
	public int j = 0;
	public boolean lmb;
	public boolean rmb;
	public boolean mmb;
	
	//Points to the Ui that we are currently focusing on with input.
	public Ui uiFocus;
	
	//Types of actions, and what key code they are bound too
	//TODO: create the config file for keys so that we can change the keybinds based one what users specify in config
	public int moveForward = 17;
	public int moveBackward = 31;
	public int moveLeft = 30;
	public int moveRight = 32;
	
	public int jump = 57;
	
	public int interact = 18;
	public int drop = 36;
	
	public int info = 24; // DEBUG KEY FOR INFO
	public int test = 20; //DEBUG KEY FOR PHYSICS
	
	public int toggleConsole = 41;
	public int toggleMenu = 1;
	
	public int captureMouse = 42;
	//89 is corresponding to the amount of keys available in Input.java
	boolean pressed[] = new boolean[89]; 
	
	public Vertex location = new Vertex(0, 0, 0);
	public Vertex lastLocation = new Vertex(0, 0, 0);
	public Vertex rotation = new Vertex(0, 0, 0);
	public Vertex lastRotation = new Vertex(0, 0, 0);
	
	public Vertex look = new Vertex(0, 0, 0);
	public Vertex lookRad = new Vertex(0, 0, 0);
	
	public Pawn pawn;
	
	public Player(Game w) {
		game = w;
		input = game.engine.input; //Maybe a bad idea.
		uiFocus = game.menu;
	}
	
	public void input() {
		lastFrame();
		mouse();
		keys();
		look();
		if(game.consoleOpen)
			uiFocus = game.console;
		else if(game.menuOpen)
			uiFocus = game.menu;
		else
			uiFocus = null;
		input.mouseCaptured = !game.menuOpen && !game.consoleOpen;
	}
	
	public void post() {
		if(pawn != null) {
			if(pawn.garbage) {
				pawn = null;
			}
		}
	}
	
	public void netStep() {
		if(pawn != null) {
			game.sendPacket(new Packet20Location(pawn.nid, true, pawn.location.x, pawn.location.y, pawn.location.z, pawn.velocity.x, pawn.velocity.y, pawn.velocity.z));
			game.sendPacket(new Packet21Rotation(pawn.nid, true, pawn.rotation.x, pawn.rotation.y, pawn.rotation.z, pawn.rotation.w));
		}
	}
	
	public void lastFrame() {
		lastLocation = location.copy();
		lastRotation = rotation.copy();
	}
	
	public void readMouse(boolean l, boolean r, boolean m, int a, int b) {
		i += a;
		j += b;
		lmb = l;
		rmb = r;
		mmb = m;
	}
	
	public void mouse() {
		if(pawn != null) {
			pawn.primary = lmb;
			pawn.secondary = rmb;
		}
	}
	
	//TODO: Eewwwwwww
	public void look() {
		float yrotrad = (float)(GraphicsCore.rot.z/180*3.141592654);
		float z = (float)(GraphicsCore.rot.x/180*3.141592654);
		float x = (float)(Math.abs(Math.sin((GraphicsCore.rot.x)/180*3.141592654))*Math.sin(yrotrad));
		float y = (float)(Math.abs(Math.sin((GraphicsCore.rot.x)/180*3.141592654))*Math.cos(yrotrad));
		lookRad = MathUtil.normalize(new Vertex(x,y,(float)(-Math.cos(z))));
		if(pawn != null) {
			pawn.look = lookRad;
			pawn.move = MathUtil.normalize(new Vertex(x,y,0));
		}
	}
	
	//Based on keys pressed, perform what task they are bound too...
	//We only take input for player when the mouse is captured, when the mouse isn't captured the menu is open or something.
	//TODO: Thsi can be made better.
	public void keys() {
		if(input.mouseCaptured) {
			Vertex m = new Vertex(0,0,0);
			if(input.keyboard[moveForward]) {
				m = MathUtil.add(new Vertex(1,0,0), m);
			}
			if(input.keyboard[moveLeft]) {
				m = MathUtil.add(new Vertex(0,1,0), m);
			}
			if(input.keyboard[moveBackward]) {
				m = MathUtil.add(new Vertex(-1,0,0), m);
			}
			if(input.keyboard[moveRight]) {
				m = MathUtil.add(new Vertex(0,-1,0), m);
			}		
			float d = Math.abs(m.x + m.y);
			if(d != 0) {
				m.x = m.x/d;
				m.y = m.y/d;
			}
			movement(m);
		}
		
		if(input.keyboard[toggleMenu] && !pressed[toggleMenu]) {
			pressed[toggleMenu] = true;
			game.menuOpen = !game.menuOpen;
		}
		else if(!input.keyboard[toggleMenu] && pressed[toggleMenu])
			pressed[toggleMenu] = false;
		
		if(input.keyboard[toggleConsole] && !pressed[toggleConsole]) {
			pressed[toggleConsole] = true;
			game.consoleOpen = !game.consoleOpen;
			game.console.setFrame(0);
		}
		else if(!input.keyboard[toggleConsole] && pressed[toggleConsole])
			pressed[toggleConsole] = false;
		
		/**Keybinds for some testing stuff, just uncomment and give it functions if you want to test something by binding it to a key**/
		/*if(input.keyboard[info] && !pressed[info]) {
			if(pawn != null) {
				Actor a = game.createTag("item/weapon/test/test.hitscan", -1, MathUtil.add(pawn.location, new Vertex(0,0,pawn.eye)), pawn.look, new Quat());
				game.addActor(a);
			}
			game.randomPhysicsBox();
			//if(pawn != null)
				//Log.log("vertex<REQUIREDINFORMATION>" + pawn.location.x + "," + pawn.location.y + "," + (pawn.location.z + 3), "PlayerCore");
			//else
				//Log.log("vertex<REQUIREDINFORMATION>" + location.x + "," + location.y + "," + location.z, "PlayerCore");
			pressed[info] = true;
		}
		else if(!input.keyboard[info] && pressed[info])
			pressed[info] = false;
		
		if(input.keyboard[test] && !pressed[test]) {
			//game.randomPhysicsBox();
			pressed[test] = true;
		}
		else if(!input.keyboard[test] && pressed[test])
			pressed[test] = false;*/
		
		if(input.keyboard[jump] && !pressed[jump]) {
			jump();
			pressed[jump] = true;
		}
		else if(!input.keyboard[jump] && pressed[jump])
			pressed[jump] = false;
		if(input.keyboard[interact] && !pressed[interact]) {
			interact();
			pressed[interact] = true;
		}
		else if(!input.keyboard[interact] && pressed[interact])
			pressed[interact] = false;
		if(input.keyboard[drop] && !pressed[drop]) {
			drop();
			pressed[drop] = true;
		}
		else if(!input.keyboard[drop] && pressed[drop])
			pressed[drop] = false;
	}
	
	public float cameraSpeed = 2f;
	public void movement(Vertex a) {
		if(pawn != null)
			pawn.movement(a);
		else {
			Vertex m = new Vertex(0,0,0);
			m = MathUtil.add(m, moveOnLookX(lookRad, a.x));
			m = MathUtil.add(m, moveOnLookY(lookRad, a.y));
			m = MathUtil.normalize(m);
			m.z = ((look.x/180) + 0.5f)*a.x*-1;
			m = MathUtil.multiply(m, cameraSpeed);
			location = MathUtil.add(location, m);
		}
	}
	
	public Vertex moveOnLookX(Vertex b, float d) {
		Vertex c = MathUtil.normalize(MathUtil.multiply(b, new Vertex(d,d,d)));
		return c;
	}
	
	public Vertex moveOnLookY(Vertex b, float d) {
		Vertex e = new Vertex(-b.y, b.x, b.z);
		Vertex c = MathUtil.normalize(MathUtil.multiply(e, new Vertex(d,d,d)));
		return c;
	}
	
	public void interact() {
		if(pawn != null) {
			//Look for switches or buttons to push
			
			//Look for items to pick up
			Item i = lookForPick();
			if(i != null) {
				if(game.isOnline())
					game.sendPacket(new Packet31Pickup(pawn.nid, i.nid));
				else
					pawn.pickup(i);
				return;
			}
		}
	}
	
	private Item lookForPick() {
		int i = 0;
		while(i < game.actors.size()) {
			Actor a = game.actors.get(i);
			if(a.internalName.contains("Item")) {
				Item b = (Item) a;
				if(!b.autoPick && b.owner == null) {
					float d = MathUtil.length(b.location, pawn.location);
					if(d < pawn.reach) {
						return b;
					}
				}
			
			}
			i++;
		}
		return null;
	}
	
	public void drop() {
		if(pawn != null) { 
			if(game.isOnline()) {
				if(pawn.inventory.size() > 0)
				game.sendPacket(new Packet32Drop(pawn.nid, pawn.inventory.get(0).nid));
			}
			else {
				if(pawn.inventory.size() > 0) {
					pawn.drop(pawn.inventory.get(0), MathUtil.add(pawn.location, new Vertex(0,0,pawn.eye)), MathUtil.multiply(pawn.look, pawn.toss), pawn.rotation);
				}
			}
		}
	}
	
	public void jump() {
		if(pawn != null)
			pawn.jump();
	}
	
	//TODO: ALL OF MY HATE
	public void camera(float f) {
		//Apply mouse  movements to camera
		rotation.x+=i*0.2;
		rotation.y+=j*-0.2;
		i = 0;
		j = 0;
		
		//This is the position that we will give to the graphics core.
		Vertex cameraPosition = new Vertex(0,0,0);
		
		//If there is a pawn, get its camera state and then apply it to the value we will be giving to the graphics core.
		if(pawn != null) {
			if(pawn.fp) {
				cameraPosition = pawn.camera(f);
			}
			else {
				cameraPosition = MathUtil.lerp(MathUtil.inverse(pawn.lastLocation), MathUtil.inverse(pawn.location), f);
				cameraPosition.x += 5;
				cameraPosition.y += 15;
				cameraPosition.z -= 10;
			}
			location = cameraPosition;
		}
		//If there is not a pawn, interpolate our last camera position and use that.
		else {
			cameraPosition = MathUtil.lerp(MathUtil.inverse(lastLocation), MathUtil.inverse(location), f);
		}
		
		//Finish up
		GraphicsCore.tran.x = cameraPosition.x;
		GraphicsCore.tran.y = cameraPosition.y;
		GraphicsCore.tran.z = cameraPosition.z;
		game.engine.graphics.cameraVector((float)rotation.x, (float)rotation.y);
		
		rotation.x=0;
		rotation.y=0;

		look.x = GraphicsCore.rot.x;
		look.y = GraphicsCore.rot.y;
		look.z = GraphicsCore.rot.z;
		
		Vertex vv = MathUtil.normalize(lookRad);
		GraphicsCore.look.x = vv.x;
		GraphicsCore.look.y = vv.y;
		GraphicsCore.look.z = vv.z;
		
		GraphicsCore.up.x = 0;
		GraphicsCore.up.y = 0;
		GraphicsCore.up.z = 1;
	}
	
	public void givePawn(Pawn p) {
		if(p != null) {
			p.fp = false;
			p.noClientSimulate = false;
		}
		p.fp = true;
		p.noClientSimulate = false;
		pawn = p;
	}
	
	public void pawnKilled() {
		location = new Vertex(0,0,0);
		pawn = null;
	}
	
	public void drawFP(ArrayList<TrianglePacket> fp, float f) {
		if(pawn != null) {
			pawn.drawFP(fp, f);
		}
	}

	public void drawHud() {

	}
}
