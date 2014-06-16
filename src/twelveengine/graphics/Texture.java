/*
 * flibit2D Game Engine
 * © 2011 Ethan "flibitijibibo" Lee
 * http://www.flibitijibibo.com/
 * 
 * Texture
 * Each instance carries a texture and its coordinates, which it can draw on call
 */

package twelveengine.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;

public class Texture {
	public String file;
	
	/** Stores the texture's ID and its coordinates. */
	protected int textureIndex;
	protected float textureTop;
	protected float textureBottom;
	protected float textureLeft;
	protected float textureRight;
	protected float[] color;
	
	/** Constructor loads the map texture into video memory.
	 * @param fileName The name of the map texture in assets/graphics/texture/, without .png
	 * @throws Exception If creating the Texture fails
	 */
	public Texture(String fileName, boolean mip) throws Exception {
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
	public Texture(String fileName, float top, float bottom, float left, float right, boolean mip) throws Exception {
		file = fileName;
		adjustCoordinates(top, bottom, left, right);
		
		textureIndex = glGenTextures();
		PNGFile fileIn = new PNGFile(fileName, GL_RGBA);
		
		int width = fileIn.getWidth();
		int height = fileIn.getHeight();
		
		if(mip && (!powerOfTwo(width) || !powerOfTwo(height))) {
			mip = false;
			System.err.println("Texture is not power of 2! Can't mip those maps!~" + fileName);
		}
		
		glBindTexture(GL_TEXTURE_2D, textureIndex);
		if(mip)
			glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		if(mip)
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		else
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, 8);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, fileIn.getWidth(), fileIn.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, fileIn.getData());
		fileIn.dispose();

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
		glDeleteTextures(textureIndex);
	}
	
	/** Returns the index of the texture.
	 * @return The index of the texture
	 */
	public int getIndex() {
		return textureIndex;
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
		glBindTexture(GL_TEXTURE_2D, textureIndex);
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