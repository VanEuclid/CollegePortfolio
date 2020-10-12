package homework9;

import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;


public class Target extends Movable {  
	
	GLU glu;
    double color1 = 0.0, color2 = 0.2, color3 = 0.3;


	public Target(double x, double y, GLU gl) {
	        posx = x;
	        posy = y;
	        remove = false;
	        size = 35;
	        height = 20;
	        velx = 1;
	        vely = 1;
	        
	        Random random = new Random();
	        int num = random.nextInt(2) + 1;
	        System.out.print(num + " ");
//	        switch(num) {
//	        	case 1:
//	        	color1 = .7;
//	        	color2 = .2;
//	        	height  = 30;
//	        	velx = num;
//		        vely = num;
//	        	break;
//	        	case 2:
//	        	color2 = .7;
//	        	color3 = .2;
//	        	height = 20;
//	        	velx = num;
//		        vely = num;
//	        	break;
//	        	case 3:
//	        	color1 = .7;
//	        	color3 = .2;
//	        	height = 10;
//	        	velx = num;
//		        vely = num;
//	        	break;
//	        }
//	        
//	        Random rng = new Random();
//	        int speed = random.nextInt(1);
//
//	        switch(speed) {
//	        case 0:
//	        	velx *= -1;
//	        case 1:
//	        	vely *= -1;       	
//	        }
	}
	public synchronized void update(Long time) {
	    posx += ((double)velx)*time/10;
        posy += ((double)vely)*time/10;
	}
	
	public synchronized void display(GLAutoDrawable gld) {
		final GL2 gl = gld.getGL().getGL2();
        // TODO Auto-generated method stub
       
        // clearing the window
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        // drawing a bomb
        
        gl.glColor3d(color1, color2, color3);
        gl.glBegin(GL2.GL_QUADS);
        
        // x, y, z coordinates of the corners
        gl.glVertex3d(posx-size, posy-size, 0);
        gl.glVertex3d(posx+size, posy-size, 0);
        gl.glVertex3d(posx+size, posy+size, 0);
        gl.glVertex3d(posx-size, posy+size, 0);

	}
}
