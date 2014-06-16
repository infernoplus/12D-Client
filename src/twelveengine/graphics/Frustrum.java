package twelveengine.graphics;

import twelveengine.data.Vertex;
import twelveutil.MathUtil;
import static org.lwjgl.opengl.GL11.*;


public class Frustrum {
	//A view frustrum, IE a cameras field of view in a geometric form.
	public Plane planes[] = new Plane[6];
	public Vertex pos;
	
	public Vertex ftl;
	public Vertex ftr;
	public Vertex fbl;
	public Vertex fbr;
	
	public Vertex ntl;
	public Vertex ntr;
	public Vertex nbl;
	public Vertex nbr;
	
	public Vertex dir;
	public Vertex fc;
	public Vertex nc;
	
	public Vertex up;
	public Vertex right;
	public Vertex front;
	
	public Vertex center;
	
	public float dist;

	public Frustrum(float fov, float aspectRatio, float nearClip, float farClip, Vertex position, Vertex direction, Vertex cameraRotation, Vertex u) {
		dist = farClip - nearClip;
		dir = direction;
		pos = position;
		up = u;
		Vertex cam = MathUtil.add(cameraRotation, new Vertex(90,0,0));
		up = MathUtil.rotate(up, MathUtil.multiply(cam, 0.0174532925f));
		front = MathUtil.normalize(MathUtil.inverse(direction));
		
		float Hnear = (float) (2.0f * Math.tan(fov / 2) * nearClip);
		float Wnear = Hnear * aspectRatio;
		
		float Hfar = (float) (2.0f * Math.tan(fov / 2) * farClip);
		float Wfar = Hfar * aspectRatio;
		
		right = MathUtil.crossProduct(up, direction);
		
		fc = MathUtil.add(position, MathUtil.multiply(MathUtil.inverse(direction), farClip));
		
		ftl = MathUtil.subtract(MathUtil.add(fc, MathUtil.multiply(up, Hfar/2)), MathUtil.multiply(right, Wfar/2));
		ftr = MathUtil.add(MathUtil.add(fc, MathUtil.multiply(up, Hfar/2)), MathUtil.multiply(right, Wfar/2));
		fbl = MathUtil.subtract(MathUtil.subtract(fc, MathUtil.multiply(up, Hfar/2)), MathUtil.multiply(right, Wfar/2));
		fbr = MathUtil.add(MathUtil.subtract(fc, MathUtil.multiply(up, Hfar/2)), MathUtil.multiply(right, Wfar/2));

		nc = MathUtil.add(position, MathUtil.multiply(MathUtil.inverse(direction), nearClip));

		ntl = MathUtil.subtract(MathUtil.add(nc, MathUtil.multiply(up, Hnear/2)), MathUtil.multiply(right, Wnear/2));
		ntr = MathUtil.add(MathUtil.add(nc, MathUtil.multiply(up, Hnear/2)), MathUtil.multiply(right, Wnear/2));
		nbl = MathUtil.subtract(MathUtil.subtract(nc, MathUtil.multiply(up, Hnear/2)), MathUtil.multiply(right, Wnear/2));
		nbr = MathUtil.add(MathUtil.subtract(nc, MathUtil.multiply(up, Hnear/2)), MathUtil.multiply(right, Wnear/2));
		
		center = MathUtil.multiply(MathUtil.add(nc, fc), 0.5f);
		
		planes[0] = new Plane(fbl, fbr, ftr, ftl);
		planes[1] = new Plane(ftl, ftr, ntr, ntl);
		planes[2] = new Plane(nbl, nbr, fbr, fbl);
		planes[3] = new Plane(ntl, nbl, fbl, ftl);
		planes[4] = new Plane(ftr, fbr, nbr, ntr);
		planes[5] = new Plane(ntl, ntr, nbr, nbl);
	}
	
	public Frustrum(Vertex ftla, Vertex ftra, Vertex fbla, Vertex fbra, Vertex ntla, Vertex ntra, Vertex nbla, Vertex nbra, Vertex nfc, Vertex nnc, Vertex ncenter, Vertex npos) {
		ftl = ftla;
		ftr = ftra;
		fbl = fbla;
		fbr = fbra;
		ntl = ntla;
		ntr = ntra;
		nbl = nbla;
		nbr = nbra;
		nc = nnc;
		fc = nfc;
		center = ncenter;
		pos = npos;
		
		planes[0] = new Plane(fbl, fbr, ftr, ftl);
		planes[1] = new Plane(ftl, ftr, ntr, ntl);
		planes[2] = new Plane(nbl, nbr, fbr, fbl);
		planes[3] = new Plane(ntl, nbl, fbl, ftl);
		planes[4] = new Plane(ftr, fbr, nbr, ntr);
		planes[5] = new Plane(ntl, ntr, nbr, nbl);
	}
	
	//Extrudes this frustrum by the distance (d) over its normals and returns it. Used for testing if a sphere is inside or partially inside the frustrum. 
	//TODO: Look into this further... Probably slow as balls, maybe not exactly 100% accurate bounds.
	public Frustrum normalizedExtrusion(float d) {
		Vertex v = MathUtil.add(ftl, MathUtil.multiply(MathUtil.inverse(right), d));
		v = MathUtil.add(v, MathUtil.multiply(up, d));
		Vertex eftl = MathUtil.add(v, MathUtil.multiply(front, d));
		
		v = MathUtil.add(ftr, MathUtil.multiply(right, d));
		v = MathUtil.add(v, MathUtil.multiply(up, d));
		Vertex eftr = MathUtil.add(v, MathUtil.multiply(front, d));
		
		v = MathUtil.add(fbl, MathUtil.multiply(MathUtil.inverse(right), d));
		v = MathUtil.add(v, MathUtil.multiply(MathUtil.inverse(up), d));
		Vertex efbl = MathUtil.add(v, MathUtil.multiply(front, d));
		
		v = MathUtil.add(fbr, MathUtil.multiply(right, d));
		v = MathUtil.add(v, MathUtil.multiply(MathUtil.inverse(up), d));
		Vertex efbr = MathUtil.add(v, MathUtil.multiply(front, d));
		
		v = MathUtil.add(ntl, MathUtil.multiply(MathUtil.inverse(right), d));
		v = MathUtil.add(v, MathUtil.multiply(up, d));
		Vertex entl = MathUtil.add(v, MathUtil.multiply(MathUtil.inverse(front), d));
		
		v = MathUtil.add(ntr, MathUtil.multiply(right, d));
		v = MathUtil.add(v, MathUtil.multiply(up, d));
		Vertex entr = MathUtil.add(v, MathUtil.multiply(MathUtil.inverse(front), d));
		
		v = MathUtil.add(nbl, MathUtil.multiply(MathUtil.inverse(right), d));
		v = MathUtil.add(v, MathUtil.multiply(MathUtil.inverse(up), d));
		Vertex enbl = MathUtil.add(v, MathUtil.multiply(MathUtil.inverse(front), d));
		
		v = MathUtil.add(nbr, MathUtil.multiply(right, d));
		v = MathUtil.add(v, MathUtil.multiply(MathUtil.inverse(up), d));
		Vertex enbr = MathUtil.add(v, MathUtil.multiply(MathUtil.inverse(front), d));
		
		return new Frustrum(eftl, eftr, efbl, efbr, entl, entr, enbl, enbr, nc, fc, center, pos); //TODO: last four params are wrong...
	}
	
	//Checks to see if a sphere is partially or completely inside the frustrum.
	//v = location of sphere, r is the radius of the sphere
	public boolean contains(Vertex v, float r) {
		return normalizedExtrusion(r).contains(v);
	}
	
	//Checks to see if the a lies within the view frustrum.
	//v = location of the point
	public boolean contains(Vertex v) {
		int i = 0;
		while(i < planes.length) {
			Vertex c = MathUtil.subtract(v, planes[i].center);
			if(MathUtil.normalFacing(c, planes[i].normal))
				return false;
			i++;
		}
		return true;
	}
	
	//TODO: completely unused and unloved? deprecated enough that i might delete it...
	public Frustrum rotateFrustrum(Vertex rot) {
		Vertex r = MathUtil.multiply(rot, 0.0174532925f); //degrees to rads
		Vertex v[] = new Vertex[11];
		v[0] = MathUtil.add(MathUtil.rotate(MathUtil.subtract(ftl, pos), r), pos);
		v[1] = MathUtil.add(MathUtil.rotate(MathUtil.subtract(ftr, pos), r), pos);
		v[2] = MathUtil.add(MathUtil.rotate(MathUtil.subtract(fbl, pos), r), pos);
		v[3] = MathUtil.add(MathUtil.rotate(MathUtil.subtract(fbr, pos), r), pos);
		v[4] = MathUtil.add(MathUtil.rotate(MathUtil.subtract(ntl, pos), r), pos);
		v[5] = MathUtil.add(MathUtil.rotate(MathUtil.subtract(ntr, pos), r), pos);
		v[6] = MathUtil.add(MathUtil.rotate(MathUtil.subtract(nbl, pos), r), pos);
		v[7] = MathUtil.add(MathUtil.rotate(MathUtil.subtract(nbr, pos), r), pos);
		v[8] = MathUtil.add(MathUtil.rotate(MathUtil.subtract(fc, pos), r), pos);
		v[9] = MathUtil.add(MathUtil.rotate(MathUtil.subtract(nc, pos), r), pos);
		v[10] = MathUtil.add(MathUtil.rotate(MathUtil.subtract(center, pos), r), pos);
		
		return new Frustrum(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], pos);
	}
	
	//Returns the top left bottom right center x y z for light frustrum
	public float[] createLightFrustrum(Vertex sunPos) {
		float dis;
		float max = 0;
		float clip;
		float maxClip = 0;
		
		Vertex mid = MathUtil.add(MathUtil.multiply(dir, max), nc);
		Vertex floor = mid;
		
		dis = MathUtil.length(ftl, floor);
		clip = MathUtil.length(ftl, sunPos);
		if(dis > max)
			max = dis;
		if(clip > maxClip)
			maxClip = clip;
		dis = MathUtil.length(ftr, floor);
		clip = MathUtil.length(ftr, sunPos);
		if(dis > max)
			max = dis;
		if(clip > maxClip)
			maxClip = clip;
		dis = MathUtil.length(fbl, floor);
		clip = MathUtil.length(fbl, sunPos);
		if(dis > max)
			max = dis;
		if(clip > maxClip)
			maxClip = clip;
		dis = MathUtil.length(fbr, floor);
		clip = MathUtil.length(fbr, sunPos);
		if(dis > max)
			max = dis;
		if(clip > maxClip)
			maxClip = clip;
		
		dis = MathUtil.length(ntl, floor);
		clip = MathUtil.length(ntl, sunPos);
		if(dis > max)
			max = dis;
		if(clip > maxClip)
			maxClip = clip;
		dis = MathUtil.length(ntr, floor);
		clip = MathUtil.length(ntr, sunPos);
		if(dis > max)
			max = dis;
		if(clip > maxClip)
			maxClip = clip;
		dis = MathUtil.length(nbl, floor);
		clip = MathUtil.length(nbl, sunPos);
		if(dis > max)
			max = dis;
		if(clip > maxClip)
			maxClip = clip;
		dis = MathUtil.length(nbr, floor);
		clip = MathUtil.length(nbr, sunPos);
		if(dis > max)
			max = dis;
		if(clip > maxClip)
			maxClip = clip;
		
		floor = MathUtil.nearest(center, 1);
		
		return new float[]{max, -max, max, -max, floor.x, floor.y, floor.z, maxClip}; //Deprecated using an exact shadow map size
	}
	
	//As the name suggests...
	public void debugDraw() {
		
		int i = 0;
		while(i < planes.length) {
			planes[i].debugDraw();
			i++;
		}
	
		glDisable(GL_LIGHTING);
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_LINES);
		glColor3d(1.0, 1.0, 1.0);
		//glVertex3d(pos.x, pos.y, pos.z);
		//glVertex3d(nc.x, nc.y, nc.z);
		
		//glVertex3d(nc.x, nc.y, nc.z);
		//glVertex3d(fc.x, fc.y, fc.z);
		
		glColor3d(0.0, 1.0, 0.0);
		glVertex3d(ntl.x, ntl.y, ntl.z);
		glVertex3d(ftl.x, ftl.y, ftl.z);
		
		glVertex3d(ntr.x, ntr.y, ntr.z);
		glVertex3d(ftr.x, ftr.y, ftr.z);
		
		glVertex3d(nbl.x, nbl.y, nbl.z);
		glVertex3d(fbl.x, fbl.y, fbl.z);
		
		glVertex3d(nbr.x, nbr.y, nbr.z);
		glVertex3d(fbr.x, fbr.y, fbr.z);
		
		glColor3d(1.0, 0.0, 0.0);
		glVertex3d(ftl.x, ftl.y, ftl.z);
		glVertex3d(ftr.x, ftr.y, ftr.z);
		
		glVertex3d(ftr.x, ftr.y, ftr.z);
		glVertex3d(fbr.x, fbr.y, fbr.z);
		
		glVertex3d(fbr.x, fbr.y, fbr.z);
		glVertex3d(fbl.x, fbl.y, fbl.z);
		
		glVertex3d(fbl.x, fbl.y, fbl.z);
		glVertex3d(ftl.x, ftl.y, ftl.z);
		
		glColor3d(0.0, 0.0, 1.0);
		glVertex3d(ntl.x, ntl.y, ntl.z);
		glVertex3d(ntr.x, ntr.y, ntr.z);
		
		glVertex3d(ntr.x, ntr.y, ntr.z);
		glVertex3d(nbr.x, nbr.y, nbr.z);

		glVertex3d(nbr.x, nbr.y, nbr.z);
		glVertex3d(nbl.x, nbl.y, nbl.z);
		
		glVertex3d(nbl.x, nbl.y, nbl.z);
		glVertex3d(ntl.x, ntl.y, ntl.z);
		
		if(up != null) {
			glColor3d(0.0, 1.0, 1.0);
			Vertex v = MathUtil.add(ftl, MathUtil.multiply(MathUtil.inverse(right), 25));
			glVertex3d(ftl.x, ftl.y, ftl.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(ftl, MathUtil.multiply(up, 25));
			glVertex3d(ftl.x, ftl.y, ftl.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(ftl, MathUtil.multiply(front, 25));
			glVertex3d(ftl.x, ftl.y, ftl.z);
			glVertex3d(v.x, v.y, v.z);
			
			v = MathUtil.add(ftr, MathUtil.multiply(right, 25));
			glVertex3d(ftr.x, ftr.y, ftr.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(ftr, MathUtil.multiply(up, 25));
			glVertex3d(ftr.x, ftr.y, ftr.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(ftr, MathUtil.multiply(front, 25));
			glVertex3d(ftr.x, ftr.y, ftr.z);
			glVertex3d(v.x, v.y, v.z);
			
			v = MathUtil.add(fbl, MathUtil.multiply(MathUtil.inverse(right), 25));
			glVertex3d(fbl.x, fbl.y, fbl.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(fbl, MathUtil.multiply(MathUtil.inverse(up), 25));
			glVertex3d(fbl.x, fbl.y, fbl.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(fbl, MathUtil.multiply(front, 25));
			glVertex3d(fbl.x, fbl.y, fbl.z);
			glVertex3d(v.x, v.y, v.z);
			
			v = MathUtil.add(fbr, MathUtil.multiply(right, 25));
			glVertex3d(fbr.x, fbr.y, fbr.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(fbr, MathUtil.multiply(MathUtil.inverse(up), 25));
			glVertex3d(fbr.x, fbr.y, fbr.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(fbr, MathUtil.multiply(front, 25));
			glVertex3d(fbr.x, fbr.y, fbr.z);
			glVertex3d(v.x, v.y, v.z);
			
			v = MathUtil.add(ntl, MathUtil.multiply(MathUtil.inverse(right), 25));
			glVertex3d(ntl.x, ntl.y, ntl.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(ntl, MathUtil.multiply(up, 25));
			glVertex3d(ntl.x, ntl.y, ntl.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(ntl, MathUtil.multiply(MathUtil.inverse(front), 25));
			glVertex3d(ntl.x, ntl.y, ntl.z);
			glVertex3d(v.x, v.y, v.z);
			
			v = MathUtil.add(ntr, MathUtil.multiply(right, 25));
			glVertex3d(ntr.x, ntr.y, ntr.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(ntr, MathUtil.multiply(up, 25));
			glVertex3d(ntr.x, ntr.y, ntr.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(ntr, MathUtil.multiply(MathUtil.inverse(front), 25));
			glVertex3d(ntr.x, ntr.y, ntr.z);
			glVertex3d(v.x, v.y, v.z);
			
			v = MathUtil.add(nbl, MathUtil.multiply(MathUtil.inverse(right), 25));
			glVertex3d(nbl.x, nbl.y, nbl.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(nbl, MathUtil.multiply(MathUtil.inverse(up), 25));
			glVertex3d(nbl.x, nbl.y, nbl.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(nbl, MathUtil.multiply(MathUtil.inverse(front), 25));
			glVertex3d(nbl.x, nbl.y, nbl.z);
			glVertex3d(v.x, v.y, v.z);
			
			v = MathUtil.add(nbr, MathUtil.multiply(right, 25));
			glVertex3d(nbr.x, nbr.y, nbr.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(nbr, MathUtil.multiply(MathUtil.inverse(up), 25));
			glVertex3d(nbr.x, nbr.y, nbr.z);
			glVertex3d(v.x, v.y, v.z);
			v = MathUtil.add(nbr, MathUtil.multiply(MathUtil.inverse(front), 25));
			glVertex3d(nbr.x, nbr.y, nbr.z);
			glVertex3d(v.x, v.y, v.z);
		}
		
		
		glEnd();
		glEnable(GL_LIGHTING);
		glEnable(GL_TEXTURE_2D);		
	}
}
