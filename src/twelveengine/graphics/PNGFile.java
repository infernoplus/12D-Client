/*
* Copyright (C) 2011 Ethan "flibitijibibo" Lee
*
* This file is part of flibitFile.
*
* flibitFile is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* flibitFile is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
* You should have received a copy of the GNU Lesser General Public License
* along with flibitFile. If not, see <http://www.gnu.org/licenses/>.
*/

package twelveengine.graphics;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;

import twelveutil.FileUtil;

/**
* @author Ethan "flibitijibibo" Lee
*/

public class PNGFile {

private ByteBuffer imageData;
private int glFormat;
private int width;
private int height;

/**
* Decodes the PNG file and stores the data needed by glTexImage2D().
* @param filePath The location of the PNG file in the asset tree
* @param passedFormat Can be GL11.ALPHA, LUMINANCE, LUMINANCE_ALPHA, RGB, or RBGA
* @throws Exception If creating the image data fails
*/
public PNGFile(String filePath, int passedFormat) throws Exception {
// Create InputStream with filePath
InputStream passedFile = FileUtil.getFile(filePath);
// Determine the format of the PNG
glFormat = passedFormat;
Format pngFormat;
int channelCount = 1;
switch (glFormat) {
case GL11.GL_ALPHA: pngFormat = Format.ALPHA; break;
case GL11.GL_LUMINANCE: pngFormat = Format.LUMINANCE; break;
case GL11.GL_LUMINANCE_ALPHA: pngFormat = Format.LUMINANCE_ALPHA; break;
case GL11.GL_RGB: pngFormat = Format.RGB; channelCount = 3; break;
case GL11.GL_RGBA: pngFormat = Format.RGBA; channelCount = 4; break;
default: throw new Exception("OpenGL image format not found!");
}
// Read PNG input
PNGDecoder pngReader = new PNGDecoder(passedFile);
width = pngReader.getWidth();
height = pngReader.getHeight();
// Create the ByteBuffer
imageData = ByteBuffer.allocateDirect(width * height * channelCount);
// Decode the PNG image data and put it in the ByteBuffer
pngReader.decode(imageData, width * channelCount, pngFormat);
imageData.flip();
// Close FileInputStream
passedFile.close();
}

/** Clears the ByteBuffer. Use this after loading into OpenGL. */
public void dispose() {
imageData.clear();
}

/** Returns the ByteBuffer needed by glTexImage2D(). */
public ByteBuffer getData() {
return imageData;
}

/** Returns the OpenGL image format needed by glTexImage2D(). */
public int getFormat() {
return glFormat;
}

/** Returns the image width needed by glTexImage2D(). */
public int getWidth() {
return width;
}

/** Returns the image height needed by glTexImage2D(). */
public int getHeight() {
return height;
}

public void rewind() {
	imageData.rewind();
}

}