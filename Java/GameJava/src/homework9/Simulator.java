package homework9;

import homework9.DoublyLinkedList.Iterator;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JApplet;

import com.jogamp.opengl.util.FPSAnimator;

/**
 *
 */

public class Simulator extends JApplet implements GLEventListener, MouseListener {
	GLU glu;
	int winWidth, winHeight;

	double posx = 0;
	double posy = 0;

	DoublyLinkedList<Movable> linked = new DoublyLinkedList<Movable>();
	Iterator firstPointer, secondPointer;	
	FPSAnimator animator; Thread t;
	long lastTime;

	public Simulator() {
		winWidth = 500;
		winHeight = 600;
		lastTime = System.currentTimeMillis();
	}

	public synchronized void update() {
		long time = System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();

		Iterator firstPointer = this.linked.first();

		while(firstPointer.valid() && firstPointer.getData() != null)
		{
			Movable obj1 = (Movable) firstPointer.getData();
			Iterator secondPointer = this.linked.first();

			if(obj1 instanceof Target) {
				if(obj1.posx + obj1.size > winWidth || obj1.posx - obj1.size < 0) {
						obj1.velx *= -1;
				}
				if(obj1.posy + obj1.size > winHeight || obj1.posy - obj1.size < 0) {
						obj1.vely *= -1;
				}
			}
			while(secondPointer.valid() && secondPointer.getData() != null)
			{
				Movable obj2 = (Movable) secondPointer.getData();
				if(secondPointer.getData() instanceof Bomb && firstPointer.getData() instanceof Target) {
					if(collision(obj2, obj1))
					{
						firstPointer.remove();
						firstPointer.next();
						secondPointer.remove();
					}
				}
				secondPointer.next();
			}
			obj1.update(time);
			if(obj1.remove) firstPointer.remove();
			firstPointer.next();
		}
	}

	private boolean collision(Movable bomb, Movable target) {
		if(bomb.size == target.height && (target.posx - 35 < bomb.posx && target.posx + 35 > bomb.posx) 
				&& (target.posy - 35 < bomb.posy && target.posy + 35 > bomb.posy)) 
					return true;
		return false;
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

		firstPointer = linked.first();
		while(firstPointer.valid() && firstPointer.getData() != null)
		{
			Movable current = (Movable) firstPointer.getData();
			current.display(gld); 
			firstPointer.next();
		}

		// drawing a box
		//		gl.glColor3d(0.7, 0.7, 0.0);
		//		gl.glBegin(GL2.GL_QUADS);
		//
		//		// x, y, z coordinates of the corners
		//		gl.glVertex3d(posx-size, posy-size, 0);
		//		gl.glVertex3d(posx+size, posy-size, 0);
		//		gl.glVertex3d(posx+size, posy+size, 0);
		//		gl.glVertex3d(posx-size, posy+size, 0);
		gl.glEnd();
	}

	public void displayChanged (GLAutoDrawable arg0, boolean arg1, boolean arg2)
	{
		// TODO Auto-generated method stub

	}

	public void init (GLAutoDrawable arg0)
	{
		// TODO Auto-generated method stub
		glu = new GLU();
	}

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
		create_targets();

		// create an animator to call display refreshes on the canvas
		animator = new FPSAnimator(canvas, 30);
		t = new Thread(new Runnable() {
			public void run() {
				while (true)
					update();
			}
		});
	}

	public void create_targets() {
		Random random = new Random();
		int rPosx = random.nextInt(winWidth - 40) + 40;
		int rPosy = random.nextInt(winHeight - 40) + 40;
//		linked.insert(new Target(random.nextInt(winWidth - 40) + 40, random.nextInt(winWidth - 40) + 40, glu));
		linked.insert(new Target(100, 200, glu));
//		linked.insert(new Target(400, 400, glu));
//		linked.insert(new Target(rPosx, rPosy, glu));
//		linked.insert(new Target(450, 100, glu));
	}

	public void start() {
		animator.start();
		t.start();
	}

	public void stop() {
		animator.stop();
	}

	public void mouseClicked (MouseEvent e)
	{
		// TODO Auto-generated method stub
	}

	public void mouseEntered (MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	public void mouseExited (MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	public void mousePressed (MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	public synchronized void mouseReleased (MouseEvent e)
	{
		// TODO Auto-generated method stub
		posx = e.getX();
		posy = winHeight - e.getY();

		//Bomb b = new Bomb(posx, posy, glu);
		linked.insert(new Bomb(posx, posy, glu));
		System.out.println(posx + " " + posy);
	}

	public void dispose (GLAutoDrawable arg0)
	{
		// TODO Auto-generated method stub

	}
}
