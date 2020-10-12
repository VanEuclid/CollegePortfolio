package homework9;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JApplet;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * 
 */

/**
 * @author adamb
 *
 */
public class homework9 extends JApplet implements GLEventListener, MouseListener
{
    GLU glu;
    int winWidth, winHeight;
    double posx = 0;
    double posy = 0;
    int velx, vely;
    int size = 50;
    FPSAnimator animator;
    Thread t;
    long lastTime;
    /* (non-Javadoc)
     * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
     */
    public homework9() {
        winWidth = 500;
        winHeight = 500;
        velx = 0;
        vely = -1;
        posx = 200;
        posy = 200;
        lastTime = System.currentTimeMillis();
    }
    
    public synchronized void update() {
        long time = System.currentTimeMillis() - lastTime;
        lastTime = System.currentTimeMillis();
        posx += ((double)velx )*time/10;
        posy += ((double)vely)*time/10;
    }
    
    public synchronized void display (GLAutoDrawable gld)
    {
        // TODO Auto-generated method stub
        final GL2 gl = gld.getGL().getGL2();
        
        // clearing the window
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        // setting up the camera 
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(0.0, winWidth, 0.0, winHeight);
        
        // initializing world transformations
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        // drawing a box
        gl.glColor3d(0.7, 0.7, 0.0);
        gl.glBegin(GL2.GL_QUADS);
        // x, y, z coordinates of the corners
        gl.glVertex3d(posx-size, posy-size, 0);
        gl.glVertex3d(posx+size, posy-size, 0);
        gl.glVertex3d(posx+size, posy+size, 0);
        gl.glVertex3d(posx-size, posy+size, 0);
        gl.glEnd();
    }

    /* (non-Javadoc)
     * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable, boolean, boolean)
     */
    public void displayChanged (GLAutoDrawable arg0, boolean arg1, boolean arg2)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
     */
    public void init (GLAutoDrawable arg0)
    {
        // TODO Auto-generated method stub
        glu = new GLU();
    }

    /* (non-Javadoc)
     * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
     */
    public void reshape (GLAutoDrawable gld, int x, int y, int width, int height)
    {
        // TODO Auto-generated method stub
        GL gl = gld.getGL();
        winWidth = width;
        winHeight = height;
        // sets the mapping from camera space to the window
        gl.glViewport(0,0, width, height);
    }

    public void init() {
        setLayout(new FlowLayout());
        // create a gl drawing canvas
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
	GLCanvas canvas = new GLCanvas(caps);
        canvas.setPreferredSize(new Dimension(winWidth, winHeight));
        canvas.addGLEventListener(this);
        canvas.addMouseListener(this);
        add(canvas);
        setSize(winWidth, winHeight);
        // create an animator to call display refreshes on the canvas
        animator = new FPSAnimator(canvas, 30);
        t = new Thread(new Runnable() {
		public void run() {
		    while (true)
			update();
		}
	    });
    }

    public void start() {
        animator.start();
        t.start();
    }
    
    public void stop() {
        animator.stop();
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked (MouseEvent e)
    {
        // TODO Auto-generated method stub
   
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered (MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited (MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed (MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public synchronized void mouseReleased (MouseEvent e)
    {
        // TODO Auto-generated method stub
        posx = e.getX();
        posy = winHeight - e.getY();
        
    }

    /* (non-Javadoc)
     * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
     */
	public void dispose (GLAutoDrawable arg0)
    {
        // TODO Auto-generated method stub
        
    }
}