package twelveengine.graphics;

import static org.lwjgl.opengl.GL11.*;
import twelveengine.data.Vertex;
import twelveutil.MathUtil;

public class Plane {
	//A plane...
	public Vertex a;
	public Vertex b;
	public Vertex c;
	public Vertex d;
	public Vertex normal;
	public Vertex center;
	public Plane(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
		a = v1;
		b = v2;
		c = v3;
		d = v4;
		
        normal = new Vertex();
		
        Vertex U = MathUtil.subtract(b, a);
        Vertex V = MathUtil.subtract(c, a);

        normal.x = (U.y * V.z) - (U.z * V.y);
        normal.y = (U.z * V.x) - (U.x * V.z);
        normal.z = (U.x * V.y) - (U.y * V.x);
        normal = MathUtil.normalize(normal);
        center = MathUtil.multiply(MathUtil.add(MathUtil.add(MathUtil.add(v1, v2), v3), v4),0.25f);
	}
	
	public void debugDraw() {
		glDisable(GL_LIGHTING);
		glDisable(GL_TEXTURE_2D);
		/*glBegin(GL_QUADS);
		glColor3d(1.0, 1.0, 1.0);
		glNormal3d(normal.x, normal.y, normal.z);
		glVertex3d(a.x, a.y, a.z);
		glNormal3d(normal.x, normal.y, normal.z);
		glVertex3d(b.x, b.y, b.z);

		glNormal3d(normal.x, normal.y, normal.z);
		glVertex3d(b.x, b.y, b.z);
		glNormal3d(normal.x, normal.y, normal.z);
		glVertex3d(c.x, c.y, c.z);
		
		glNormal3d(normal.x, normal.y, normal.z);
		glVertex3d(c.x, c.y, c.z);
		glNormal3d(normal.x, normal.y, normal.z);
		glVertex3d(d.x, d.y, d.z);
		
		glNormal3d(normal.x, normal.y, normal.z);
		glVertex3d(d.x, d.y, d.z);
		glNormal3d(normal.x, normal.y, normal.z);
		glVertex3d(a.x, a.y, a.z);
		glEnd();*/
		
		glBegin(GL_LINES);
		glColor3d(1.0, 1.0, 0.0);
		glVertex3d(center.x, center.y, center.z);
		glVertex3d(center.x + normal.x*25, center.y + normal.y*25, center.z + normal.z*25);
		glEnd();
		glEnable(GL_LIGHTING);
		glEnable(GL_TEXTURE_2D);
	}
}
