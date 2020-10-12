package homework9;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

public class Bomb extends Movable {
	
	GLU glu;
    double color = 0.7;

	public Bomb(double x, double y, GLU gl) {
	        velx = 0;
	        vely = 0;
	        posx = x;
	        posy = y;
	        remove = false;
	        size = 50;	        
	}
	public synchronized void update(Long time) {

		if(size < 4) remove = true;
	}
	
	public synchronized void display(GLAutoDrawable gld) {
		final GL2 gl = gld.getGL().getGL2();
        // TODO Auto-generated method stub
       
        // clearing the window
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        // drawing a bomb
        gl.glColor3d(color +=.007, 0.7, 0);
        gl.glBegin(GL2.GL_QUADS);
        
        // x, y, z coordinates of the corners
        size -= 0.5;
        gl.glVertex3d(posx-size, posy-size, 0);
        gl.glVertex3d(posx+size, posy-size, 0);
        gl.glVertex3d(posx+size, posy+size, 0);
        gl.glVertex3d(posx-size, posy+size, 0);

	}

}
