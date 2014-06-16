package twelveengine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.lang.reflect.*;

import twelveengine.actors.*;
import twelveengine.audio.*;
import twelveengine.data.*;
import twelvelib.net.packets.*;
import twelveutil.FileUtil;

public class Console {
	
	Engine engine;
	
	public Console(Engine passedGame) {
		Log.log("Initializing console...", "Console");
		engine = passedGame;
		Log.log("Executing config.cfg...", "Console");
		try {
			InputStream fileIn = FileUtil.getCfg("config.cfg");
		    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));
		    String line = fileReader.readLine();
		    while (line != null) {
		    	parse(line);
		    	line = fileReader.readLine();
		    }
		    fileReader.close();
		} catch(Exception e) {
			Log.log("Could not read config file.", "Console", 2);
		}
	}
	
	//Takes a user input console command and breaks it into correct format and then attempts to execute the command.
	//Returns true if parsed without error. False if error.
	public boolean parse(String in) {
		String com = "";
		String param[];
		if(!in.contains(" ")) { //If just a single param...
			com = in;
			execute(com, new String[0]);
			return true;
		}
		else if(in.contains("\"")) {//If there are " then we parse those as single params (IE chat message)
			in += " ";
			ArrayList<String> split = new ArrayList<String>();
			String c[] = in.split("\"");
			if(c.length % 2 == 0) { //Even number of " means that there is an unclosed param... bad.
				Log.log("Bad Syntax: " + in, "Console", 2);
				return false;
			}
			int i = 0;
			boolean b = true;
			while(i < c.length) {
				if(b) {
					int j = 0;
					String d[] = c[i].split(" ");
					while(j < d.length) {
						if(!d[j].equals("") && !d[j].equals(" "))
							split.add(d[j]);
						j++;
					}
				}
				else
					split.add(c[i]);
				b = !b;
				i++;
			}
			i = 0;
			com = split.get(0);
			param = new String[split.size()-1];
			while(i < param.length) {
				param[i] = split.get(i+1);
				i++;
			}
		}
		else { //If just spaces then no need to worry about parsing "
			in += " ";
			String c[] = in.split(" ");
			com = c[0];
			param = new String[c.length-1];
			int i = 0;
			while(i < param.length) {
				param[i] = c[i+1];
				i++;
			}
		}
		execute(com, param);		
		return true;
	}
	
	//For commands with params
	public void execute(String command, String[] params) {
		try{ 
			if(command.equals("load")) {
				engine.network.close();
				if(params.length > 1)
					engine.loadGame(params[0], new String[0]);
				else {
					int i = 1;
					String m[] = new String[params.length-1];
					while(i < params.length) {
						m[i-1] = params[i];
						i++;
					}
					engine.loadGame(params[0], m);
				}
				return;
			}
			if(command.equals("set")) {
				Settings.set(params[0], params[1]);
				return;
			}
			if(command.equals("get")) {
				Log.log(Settings.getString(params[0]), "Console");
				return;
			}
			if(command.equals("wake")) {
				engine.game.scripts.wakeScript(params[0]);
				return;
			}
			if(command.equals("spawn")) {
				engine.game.addActor(engine.game.createTag(params[0], Integer.parseInt(params[1]), new Vertex(Float.parseFloat(params[2]),Float.parseFloat(params[3]),Float.parseFloat(params[4])), new Vertex(), new Quat())); //Add a SID maybe?
				
				return;
			}
			if(command.equals("play")) {
				SoundData sd = engine.game.getSound(params[0]);
				Sound s = new Sound(sd, new Vertex(0,0,0), Float.parseFloat(params[1]), false);
				s.playSound();
				return;
			}
			if(command.equals("animation")) {
				Physical p = (Physical)engine.game.getActor(Integer.parseInt(params[0]));
				p.playAnimation(params[1], Boolean.parseBoolean(params[2]));
				return;
			}
			if(command.equals("effect")) {
				Actor a = engine.game.getActor(Integer.parseInt(params[0]));
				a.playEffect(params[1], params[2]);
				return;
			}
			if(command.equals("connect")) {
				if(params.length <= 3)
					engine.network.connect(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]), "");
				else
					engine.network.connect(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]), params[3]);
				return;
			}
			if(command.equals("say")) {
				String m = "";
				int i = 0;
				while(i < params.length) {
					m += params[i] + " ";
					i++;
				}
				engine.network.message(m);
				return;
			}
			if(command.equals("whisper")) {
				String m = "";
				int i = 1;
				while(i < params.length) {
					m += params[i] + " ";
					i++;
				}
				engine.network.whisper(m, params[0]);
				return;
			}
			if(command.equals("log")) {
				Log.log(params[0], "Console");
				return;
			}
			if(command.equals("test")) {
				Method method = this.getClass().getMethod("test", String.class);
				method.invoke(this, params[0]);
				return;
			}
			if(command.equals("objects")) {
				int i = 0;
				Log.log("Objects in world: " + engine.game.actors.size(), "Console");
				while(i < engine.game.actors.size()) {
					Log.log(engine.game.actors.get(i).toString(), "Console");
					i++;
				}
				return;
			}
			if(command.equals("netready")) {
				engine.network.send(new Packet40Ready());
				return;
			}
			if(command.equals("quit") || command.equals("exit")) {
				engine.stopRuntime = true;
				return;
			}
			Log.log("No such command: " + command, "Console", 1);
		}
		catch(Exception e) {
			String s = command;
			int i = 0;
			while(i < params.length) {
				s += " " + params[i];
				i++;
			}
			Log.log("Syntax error: " + s, "Console", 2);
			e.printStackTrace();
		}
	}
	
	public void test(String s) {
		Log.log(s, "Console");
	}
}
