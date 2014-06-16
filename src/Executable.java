/*
 * 12characters Snake Engine
 * © 2011 12characters Games
 * http://www.12charactersgames.com/
 * 
 * Executable
 * Runs the game.
 */

import twelveengine.Engine;

public class Executable {
    public static void main(String[] str) {
        Engine theGame = new Engine();
        theGame.start();
        theGame.run();
        theGame.end();
    }
}