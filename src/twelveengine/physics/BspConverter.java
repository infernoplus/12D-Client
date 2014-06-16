/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package twelveengine.physics;

import com.bulletphysics.util.*;
import javax.vecmath.Vector3f;

import twelveengine.Log;
import twelveengine.bsp.*;
import twelveengine.data.*;
import twelveutil.MathUtil;

/**
 *
 * @author jezek2
 */
public abstract class BspConverter {

	//Converts an array of phymodels to a jbullet bsp
	public void convertBsp(Part m[]) {
		ObjectArrayList<Vector3f> vertices = new ObjectArrayList<Vector3f>();
		int i = 0;
		int j = 0;
		int k = 0;
		while(i < m.length) {
			while(j < m[i].collision.f.length) {
				Vertex a = MathUtil.add(m[i].location, MathUtil.rotate(MathUtil.multiply(m[i].collision.f[j].a, m[i].scale), m[i].rotation));
				Vertex b = MathUtil.add(m[i].location, MathUtil.rotate(MathUtil.multiply(m[i].collision.f[j].b, m[i].scale), m[i].rotation));
				Vertex c = MathUtil.add(m[i].location, MathUtil.rotate(MathUtil.multiply(m[i].collision.f[j].c, m[i].scale), m[i].rotation));
				vertices.add(MathUtil.bConvert(a));
				vertices.add(MathUtil.bConvert(b));
				vertices.add(MathUtil.bConvert(c));
				k++;
				j++;
			}
			createTriMesh(vertices);
			vertices.clear();
			j= 0;
			i++;
		}
		Log.log("Total triangles bullet bsp is made up off: " + k, "Physics", 0);
	}
	
	public abstract void createTriMesh(ObjectArrayList<Vector3f> vertices);
	
}
