package twelveengine;

import java.io.File;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

import twelveengine.audio.AudioCore;
import twelveengine.Game;
import twelveengine.graphics.GraphicsCore;
import twelveengine.network.NetworkCore;
import twelveengine.ui.*;
import twelveutil.*;

/** This is the big scary fucking list of doom, things that need to get DONE **/

//TODO: Add the functionality that is in Physics to Hitboxes so that hitboxes can follow parts of animated models.

//TODO: OPTIMIZE FUCKING EVERYTHING, ESPECIALLY SHADERS

//TODO: [NEED TEST ANIMATIONS TO IMPLEMENT THIS]Rigged collision models, make an exporter and importer for basically boxes that are linked to frames in a PhysModel ~ 

//TODO: I generally dislike the static camera properties tran and rot. Maybe redo that shiz.

//TODO: standardize making copies of location values and what not to the constructor of the recieving object. I dont like objects pointing at the same location value. Bad shit happens

//TODO: CLEAN UP THE FRUSTRUM FOR SHADOW MAPS, SHIT IS A LITTLE OFF AND THE RADIUS TENDS TO CLIP THE EDGES OF THE DEPTH MAP OCCASIONALLY

//TODO: complete all stub unloading files code. (any super class with a method called unload() ) (do this last as unloading may change before we finish the engine)

//TODO: make things private that don't need to be public mb mb mb mb mb (EASY)

//TODO: SPIRTES I THINK MAYBE

//TODO: OPTIONAL STATIC LIGHTMAPS W/ BLENDING INTO CSM. BUT MOST LIKELY NOT EVER

//TODO: CREATE DUMMY AI THAT JUST SORT OF EXSISTS BUT HAS NO REAL FUNCTION. A STUB OF AI FOR PEOPLE TO HAVE SEX WITH. WILL STRUCTURE SIMILAR TO PLAYER CLASS INTERACTIONS WITH GAME ACTORS

//TODO: UI TEXT RENDERING NEEDS A TRUE LIBRARY THAT USES REAL FONTS. CURRENT ONE IS A BAD JOKE

//TODO: ragdolls and complex physics objects are a luxary, don't worry about them

//TODO: GRAPHICS SETTINGS, NEED THOSE. WILL JUST HAVE FLAGS FOR DIFFERENT QUALITIES THAT CAUSE SHADER TO USE LESS NICE CODE (LIKE USING VERTEX SHADING n SHIT) AND LESS SHADOWS OR NO SHADOWS N SHIT

//TODO: AND OF COURSE! FINISH EVERY OTHER TODO THAT IS SCATTERED THROUGHOUT ALL THE REST OF THE CODE. GG SCRUB GET GOOD NO RE 420 BLAZEIT

//TODO: CHANGE THE ABCD SCALAR SYSTEM TO AN ARRAY OF FLOATS AND MAKE SHADER AUTO GENERATE AND SET UP SCALARS FROM A-Z OR BY NUMBERS

//TODO: SYSTEM TO EITHER STATICLY HARDCODE "FALLBACK ASSETS" OR A STATIC FALLBACK CLASS THAT HAS A CONFIG FILE THAT LISTS WHERE THEY ARE

//TODO: SCALAR INTERACTIONS WITH UI SHADERS. EITHER MOVE TO A MULTIPLE SHADER SYSTEM OR MAKE THIS BETTER!

//TODO: PARTICLE SYSTEMS. THE WHOLE THING.

//TODO: CULLING IS BROKEN! I"m not sure why but it has something to do with inbetweeing for some weird reason.

public class Engine {
	/** That's the name of the game, baby! */
	public final String gameTitle = "12D Client Alpha";
	public final String version = "v0.8.5 Dev";
	public final static String gameFolder = ".12d";
	/** Core Instances */
	public Console console;
	public GraphicsCore graphics;
	public UiCore ui;
	public AudioCore audio;
	public NetworkCore network;
	public Input input;
	public Game game;
	/** Menu variables */
	public boolean menuIsOpen = false;
	/** Kills the program upon enable */
	public boolean stopRuntime = false;
	/** BSP Step Variables */
	private long currentTime;
	private long lastStep = getTime();
	private long lastNet = getTime();
	private long lastDraw = getTime();

	/** Engine starts here! */
	public void start() {
		Log.log("Loading engine...", "Engine");
		gameDirectory();
		console = new Console(this);
		graphics = new GraphicsCore(this);
		ui = new UiCore(this);
		audio = new AudioCore(this);
		network = new NetworkCore(this);
		input = new Input(this);
		Log.log("Engine ready!", "Engine");
		
		game = new Game(this, "scenario/mp/portent/portent.scenario");
	}
	
	//TODO:Not sure if I like this... show it to ethan?
	public void gameDirectory() {
		try {
			File f = new File(FileUtil.dir);
			File c = new File(FileUtil.dir + "config.cfg");
			
			if(!f.exists()) {
				Log.log("Creating game directory...", "Engine");
				f.mkdir();
			}
			if(!c.exists()) {
				Log.log("Creating config file...", "Engine");
				InputStream i = FileUtil.getCfg("config.cfg");
				FileOutputStream o = new FileOutputStream(FileUtil.dir + "config.cfg");
			    int bytesRead;
			    byte[] buffer = new byte[8 * 1024];
			    while ((bytesRead = i.read(buffer)) != -1) {
			    	o.write(buffer, 0, bytesRead);
				}			    
				i.close();
				o.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean loadGame(String s, String m[]) {
		FileUtil.setMods(m);
		try {
			game.unloadGame();
			game = new Game(this, s);
		}
		catch(Exception e) {
			return false;
		}
		return true;
	}
	
	long fpsUpdateWait = 3000;
	long fpsUpdate = 0;
	int draws = 0;
	int ticks = 0;
	float lastDrawLerp;

	/** Engine runs here! */
	public void run() {
		while (!stopRuntime) {
			//Game and ui updates
			currentTime = getTime();
			if (currentTime - lastStep >= Game.stepTime) {
				//long t = getTime();
				input.mouseUpdate();
				input.keyboardUpdate();
		       	game.step();
		       	ui.step();
		       	ticks++;
				//Log.log("Tick: " + (getTime() - t));
	        	lastStep = currentTime;
	        	lastDrawLerp = 0;
			}
			
			//Network update and packets out and in. but only if we are online
			if(network.online) {
				currentTime = getTime();
				if (currentTime - lastNet >= Game.netTime) {
					network.step();
					game.netStep();
		        	lastNet = currentTime;
				}
			}
			
			//Update mouse, interpolate camera and then render game (smoother input if I update mouse on every draw)
			currentTime = getTime();
			if (currentTime - lastDraw >= Game.drawTime) {
				float f = (float)(currentTime-lastStep)/(float)Game.stepTime;
				input.mouseUpdate();
				game.player.camera(f);
				game.shaderStep(f -lastDrawLerp);
				renderGame(f);
				draws++;
	        	lastDraw = currentTime;
	        	lastDrawLerp = f;
			}
			
			//FPS counter and stuff
			if(currentTime - fpsUpdate >= fpsUpdateWait) {
				String s = "";
				if(ticks >= 89)
					s = "Normal";
				else
					s = "Slowdown";
				Display.setTitle(gameTitle + " FPS: " + draws/3 + " Engine: " + ticks/3 + ":" + s);
				ticks = 0;
				draws = 0;
				fpsUpdate = currentTime;
			}
		}
    }
	
	public void renderGame(float f) {
		audio.renderAudio();
		graphics.renderGraphics(f);
	}

	/** Engine ends here! */
	public void end() {
		Log.log("Stopping engine...", "Engine");
		if(game != null)
			game.unloadGame();
		network.close();
		graphics.endGraphics();
		audio.endAudio();
		Log.log("Game stopped!", "Engine");
	}

	/** Get method for the current time.
	 * @return The time in milliseconds, stored as a long.
	 */
    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
}
