package twelveengine.data;

import java.util.ArrayList;

import twelveengine.Game;
import twelveutil.*;

public class Scenario {
	public Game game;
	
	public String file;
	
	public String name;
	public String bsp;
	public String script;
	
	public ArrayList<Vertex> playerSpawns = new ArrayList<Vertex>(); //Spawn points... add team spawns later
	public ArrayList<Vertex> flags = new ArrayList<Vertex>(); //Points for the script language to get coordinates of. Used to like, teleport stuff to a flag.
	
	public Scenario(Game w, String s) {
		game = w;
		file = s;
		readScenario();
	}
	
	//TODO: Support for more stuff... but get rid of unneeded shit like player spawns on client side...
	public void readScenario() {
		Tag t = game.tagger.openTag(file);
		name = t.getProperty("name", "DEFAULT");
		bsp = t.getProperty("bsp", "NULL");
		script = t.getProperty("script", "NULL");
		
		int i = 0;
		TagSubObject tso = t.getObject("player_spawns");
		int j = tso.getTotalObjects();
		while(i < tso.getTotalObjects()) {
			playerSpawns.add(TagUtil.makeVertex(tso.getObject(i)));
			i++;
		}
		
		i = 0;
		tso = t.getObject("flags");
		j = tso.getTotalObjects();
		while(i < j) {
			flags.add(TagUtil.makeVertex(tso.getObject(i)));
			i++;
		}
	}
}
