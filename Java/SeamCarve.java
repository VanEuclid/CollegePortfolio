package homework3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class SeamCarve {

	private int cost[][]; private int totalcost[][];
	private BufferedImage im; private BufferedImage fil;
	private int col; private int row;

	public static void main(String args[]) {

		File file = new File("C:/Users/Van/Desktop/test.jpg");
		//		int desiredW = Integer.parseInt(args[2]);
		//		int desiredH = Integer.parseInt(args[3]); 

		BufferedImage im = null;
		

		try {
			im = ImageIO.read(file); } 
		catch (IOException exception) {
			exception.printStackTrace(); }	

		SeamCarve SC = new SeamCarve(im);
		BufferedImage fil = SC.fil;

		//		try {
		//			ImageIO.write(SC.fil, "jpg", new File(args[1]));
		//		} catch (IOException exception) {
		//			exception.printStackTrace();
		//		}

		JFrame x = new JFrame("Seam Carver");
		JFrame y = new JFrame("Seam Carver Original");
		y.setSize(new Dimension(im.getWidth() + 100, im.getHeight()+100));
		y.add(new JLabel(new ImageIcon(im)));
		y.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		y.setVisible(true);
		x.setSize(new Dimension(im.getWidth()+100, im.getHeight()+100));
		x.add(new JLabel(new ImageIcon(fil)));
		x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		x.setVisible(true);

	}

	//End of main

	/**
	 * Calculates gradiant(energy of each pixel)
	 * @param a RGB packed integer of pixel 1
	 * @param b RGB packed integer of pixel 2
	 * @return 'energy'
	 */
	private int grad(int a, int b) {
		Color c1 = new Color(a); Color c2 = new Color(b);
		return Math.abs(c1.getRed()-c2.getRed()) + Math.abs(c1.getGreen()-c2.getGreen()) + Math.abs(c1.getBlue()-c2.getBlue()); }

	public SeamCarve(BufferedImage im) {

		col = im.getWidth(); //x
		row = im.getHeight(); //y

		cost = new int[col][row];

		//Boundaries//
		for(int x = 0; x < col; x++ ) {
			cost[x][0] =  grad(im.getRGB(x, 0), im.getRGB(x, 1));
			cost[x][row-1] = grad(im.getRGB(x, row-1), im.getRGB(x, row-2)); } 

		for (int y = 0; y < row; y++) {
			cost[0][y] = grad(im.getRGB(0, y), im.getRGB(1, y));	
			cost[col-1][y] = grad(im.getRGB(col-2, y), im.getRGB(col-1, y)); }

		//Everything else
		for(int y = 1; y < row-1; y++) {
			for(int x = 1 ; x < col-1; x++) {
				cost[x][y] = grad(im.getRGB(x, y-1), im.getRGB(x, y+1)) + grad(im.getRGB(x-1, y), im.getRGB(x+1, y)) ;  //Calculate ENERGY OF EACH PIXEL IN IMAGE
			} }

		totalcost = new int[col][row];

		//First row to start
		for(int y = 0; y < row; y++) {
			totalcost[0][y] = cost[0][y]; } 

		//Rest of cost
		for(int x = 1 ; x < col ; x++) {
			totalcost[x][0] = cost[x][0] +
					Math.min(totalcost[x-1][0],totalcost[x-1][1]);  //special case for first row that looks at two elements

			totalcost[x][row-1] = cost[x][row-1] +
					Math.min(totalcost[x-1][row-1],totalcost[x-1][row-2]); //special case for last row that looks at two elements

			for(int y = 1 ; y < row - 1 ; y++) {
				totalcost[x][y] = cost[x][y] + 
						Math.min(totalcost[x-1][y], 
								Math.min(totalcost[x-1][y-1],     //middle pixels look at three elements
										totalcost[x-1][y+1]));
			} }
		
		//Finds min on right most boundary
		int min = totalcost[col-1][0];
		int minI = 0;
		for(int y = 1; y < row; y++) {
			if(min > totalcost[col-1][y]) {
				min = totalcost[col-1][y];
				minI = y;
			}
		}

		int seam[] = new int[col]; //Create seam of size column
		int rows = minI; //Start

		for(int x = col-1 ; x >= 0 ; x--) {     //start from last column all the way to the left
			int temp = rows;                    
			if(rows < row - 1 && totalcost[x][rows+1]< totalcost[x][rows]) {
				temp = rows + 1; }
			if(rows > 0 && totalcost[x][rows-1]< totalcost[x][temp]) {
				temp = rows - 1 ; }
			rows = temp;            //sets rows to temp to use minimum adjacent rows in the next iteration
			seam[x] = rows;
		}

		fil = new BufferedImage(im.getWidth(), im.getHeight()-1, BufferedImage.TYPE_INT_RGB);

		for(int x = 0; x < col; x++) {
			for(int y = 0; y < seam[x]; y++) {
				fil.setRGB(x, y, im.getRGB(x,y));
				 } for (int k = seam[x] + 1; k < row-1; k++) {
						fil.setRGB(x, k, im.getRGB(x, k+1));
					}}
	} } 