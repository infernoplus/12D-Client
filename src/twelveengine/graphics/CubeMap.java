

package twelveengine.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.ARBTextureCubeMap.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;

import twelveutil.FileUtil;

public class CubeMap {
	public String file;
	
	/** Stores the texture's ID and its coordinates. */
	protected int cubeMapIndex;
	protected int cubeIndex[];
	protected float textureTop;
	protected float textureBottom;
	protected float textureLeft;
	protected float textureRight;
	protected float[] color;
	
	/** Constructor loads the map texture into video memory.
	 * @param fileName The name of the map texture in assets/graphics/texture/, without .png
	 * @throws Exception If creating the Texture fails
	 */
	public CubeMap(String fileName, boolean mip) throws Exception {
		this(fileName, 0.0f, 0.0f, 0.0f, 0.0f, mip);
	}
	
	/** Constructor loads the map texture into video memory and places it into the BSPGrid based on the passed XML data.
	 * @param fileName The name of the map texture in assets/graphics/texture/, without .png
	 * @param top The Y coordinate for the top of the map texture
	 * @param bottom The Y coordinate for the bottom of the map texture
	 * @param left The X coordinate for the left of the map texture
	 * @param right The X coordinate for the right of the map texture
	 * @throws Exception If creating the Texture fails
	 */
	public CubeMap(String fileName, float top, float bottom, float left, float right, boolean mip) throws Exception {
		file = fileName;
		
		String currentLine;
		DataInputStream fileIn = new DataInputStream(FileUtil.getFile(fileName));
	    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));	    
	    currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
	    
	    String maps[] = new String[6];
	    while(currentLine != null) {
	    	String t = currentLine.split("=")[0].trim();
	    	String f = currentLine.split("=")[1].trim();
	    	
	    	if(t.equals("left"))
	    		maps[0]=f;
	    	if(t.equals("right"))
	    		maps[1]=f;
	    	if(t.equals("front"))
	    		maps[2]=f;
	    	if(t.equals("back"))
	    		maps[3]=f;
	    	if(t.equals("top"))
	    		maps[4]=f;
	    	if(t.equals("bottom"))
	    		maps[5]=f;
	    	
		    currentLine=fileReader.readLine(); if(currentLine != null) currentLine = currentLine.trim();
	    }
		
		cubeIndex = new int[6];
		adjustCoordinates(top, bottom, left, right);
		PNGFile fileIn1 = new PNGFile(maps[0], GL_RGBA);
		PNGFile fileIn2 = new PNGFile(maps[1], GL_RGBA);
		PNGFile fileIn3 = new PNGFile(maps[2], GL_RGBA);
		PNGFile fileIn4 = new PNGFile(maps[3], GL_RGBA);
		PNGFile fileIn5 = new PNGFile(maps[4], GL_RGBA);
		PNGFile fileIn6 = new PNGFile(maps[5], GL_RGBA);
		
		int width = fileIn1.getWidth();
		int height = fileIn1.getHeight();
		
		if(mip && (!powerOfTwo(height) || !powerOfTwo(width))) {
			mip = false;
			System.err.println("Texture is not power of 2! Can't mip those maps!~" + width + ", " + height + " ~ " + fileName);
		}
		
		//Setup cubemap params
		glTexParameteri(GL_TEXTURE_CUBE_MAP_ARB, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_CUBE_MAP_ARB, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_CUBE_MAP_ARB, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP_ARB, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glTexGeni(GL_S, GL_TEXTURE_GEN_MODE, GL_REFLECTION_MAP_ARB);
		glTexGeni(GL_T, GL_TEXTURE_GEN_MODE, GL_REFLECTION_MAP_ARB);
		glTexGeni(GL_R, GL_TEXTURE_GEN_MODE, GL_REFLECTION_MAP_ARB);

		glEnable(GL_TEXTURE_GEN_S);
		glEnable(GL_TEXTURE_GEN_T);
		glEnable(GL_TEXTURE_GEN_R);


		glEnable(GL_TEXTURE_CUBE_MAP_ARB);
		glEnable(GL_NORMALIZE);
	    
		cubeMapIndex = glGenTextures();
		
		glBindTexture(GL_TEXTURE_CUBE_MAP, cubeMapIndex);

		glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL_RGBA, fileIn1.getWidth(), fileIn1.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, fileIn1.getData());
		glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL_RGBA, fileIn2.getWidth(), fileIn2.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, fileIn2.getData());
		glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL_RGBA, fileIn3.getWidth(), fileIn3.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, fileIn3.getData());
		glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL_RGBA, fileIn4.getWidth(), fileIn4.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, fileIn4.getData());
		glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL_RGBA, fileIn5.getWidth(), fileIn5.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, fileIn5.getData());
		glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL_RGBA, fileIn6.getWidth(), fileIn6.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, fileIn6.getData());

		
		glTexParameteri (GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri (GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		fileIn1.dispose();
		fileIn2.dispose();
		fileIn3.dispose();
		fileIn4.dispose();
		fileIn5.dispose();
		fileIn6.dispose();

		setColor(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public boolean powerOfTwo(int x) {
		if(x == 0)
			return false;
		 while(x % 2 == 0)
			 x = x / 2;
		 if(x > 1)
			 return false;
		 return true;
	}
	
	/** Unloads the map texture from video memory. */
	public void killTexture() {
		glDeleteTextures(cubeMapIndex);
	}
	
	/** Returns the index of the texture.
	 * @return The index of the texture
	 */
	public int getIndex() {
		return cubeMapIndex;
	}
	
	/** Sets the color/tint of the texture.
	 * @param r The R value used by glColor4f().
	 * @param g The G value used by glColor4f().
	 * @param b The B value used by glColor4f().
	 * @param a The A value used by glColor4f().
	 */
	public void setColor(float r, float g, float b, float a) {
		color = new float[] {r, g, b, a};
	}
	
	/** Adjusts the dimensions of the texture to passed coordinates.
	 * @param newTop The new y coordinate of the texture's top.
	 * @param newBottom The new y coordinate of the texture's bottom.
	 * @param newLeft The new x coordinate of the texture's left.
	 * @param newRight The new x coordinate of the texture's right.
	 */
	public void adjustCoordinates(float newTop, float newBottom, float newLeft, float newRight) {
		textureTop = newTop;
		textureBottom = newBottom;
		textureLeft = newLeft;
		textureRight = newRight;
	}
	
	/** Draws the texture on a quad according to its coordinates. */
	public void drawTexture() {
		drawTexture(textureTop, textureBottom, textureLeft, textureRight);
	}
	
	/** Draws the texture on a quad according to passed coordinates.
	 * @param top The y value of the top of the quad
	 * @param bottom The y value of the bottom of the quad
	 * @param left The x value of the left of the quad
	 * @param right The x value of the right of the quad
	 */
	public void drawTexture(float top, float bottom, float left, float right) {
		glColor4f(color[0], color[1], color[2], color[3]);
		glBindTexture(GL_TEXTURE_CUBE_MAP_POSITIVE_X, cubeIndex[0]);
		glBegin(GL_QUADS);
			glTexCoord2f(0,0);
				glVertex2f(left, top);
			glTexCoord2f(1,0);
				glVertex2f(right, top);
			glTexCoord2f(1,1);
				glVertex2f(right, bottom);
			glTexCoord2f(0,1);
				glVertex2f(left, bottom);
		glEnd();
	}
}