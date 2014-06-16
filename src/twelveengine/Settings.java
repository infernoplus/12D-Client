/*
 * flibit2D Game Engine
 * Â© 2011 Ethan "flibitijibibo" Lee
 * http://www.flibitijibibo.com/
 * 
 * Settings
 * Stores assorted configurations for the engine Cores.
 */

//Modified by Pat for 12D

package twelveengine;

import java.util.HashMap;

public class Settings {
	
	/** Stores the game's settings */
	private static HashMap<String, String> settings = new HashMap<String, String>();
	
	public static boolean getBool(String cvar) {
		return Boolean.parseBoolean(settings.get(cvar));
	}
	
	public static int getInt(String cvar) {
		return Integer.parseInt(settings.get(cvar));
	}
	
	public static float getFloat(String cvar) {
		return Float.parseFloat(settings.get(cvar));
	}
	
	public static String getString(String cvar) {
		return settings.get(cvar);
	}
	
	public static void set(String cvar, String value) {
		settings.put(cvar, value);
	}
}