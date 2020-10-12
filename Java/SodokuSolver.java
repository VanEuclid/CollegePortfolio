//Programmed by Van Euclid Dy
//University of Utah CS

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class SodokuSolver {

	private int[][] b; private int back;
	ArrayList<Integer> x;
	ArrayList<Integer> y;


	SodokuSolver(int[] board) {

		b = new int[9][9]; //[row][col] //[i][j]
		x = new ArrayList<Integer>();
		y = new ArrayList<Integer>();

		int index = 0;
		for(int i = 0; i < 9; i++)
			for(int j = 0; j < 9; j++) {
				b[i][j] = board[index];
				index++; //Check this number that it reaches 81
			}
	}

	public static void main(String[] args) {

		//Could use Scanner to parse input Sodoku file

		//Scanner file = new Scanner(args[0]); //For command line argument

		Scanner file = new Scanner(System.in); //Input Scanner

		int[] board = new int[81];
		int i = 0;
		
		while(file.hasNextInt()) {
			int o = file.nextInt();
			System.out.println(o);
			board[i] = o;
			i++;
		}

		SodokuSolver s = new SodokuSolver(board);

		//Need print clauses

		int w = s.solver(0, 0);
		if(w == 1) {
			System.out.println("Sodoku solved");
			System.out.println(s.back + ": Number of backtracks");
		}
		else if (w == 0) 
			System.out.println("Sodoku not solved");
	}


	public int solver(int row, int col) {

		if(col == 9) {
			col = 0;
			row++;
		}
		else
			for(int num = 1; num < 9; num++) {
				if(!rowcheck(num, row) && !colcheck(num, col)) { //If current num has a duplicate moves to the next num
					b[row][col] = num; // Try num in puzzle
					solver(row, col +1); //Solve next square with last value in place

				}
				else {
					back++; // keeps track of number of backtracks
					return 0; // If it reaches this line that means that there are no valid number insertions and the recursion returns
				}
			}


		//NEED CASE HANDLES HERE

		return 0; 
	}

	/**
	 * Checks row for duplicates by adding check num to and an array and adding all the elements in a row
	 * @param num = number to check
	 * @param row = row to check
	 * @return true for duplicate, false otherwise
	 */
	private boolean rowcheck(int num, int row) {

		x.clear(); //Clears array for a new row check
		x.add(num);
		for (int i = 0; i < 8; i ++) 
			x.add(b[row][i]);


		return duplicates(x);
	}

	/**
	 * Checks col for duplicates by adding check num to and an array and adding all the elements in a row
	 * @param num = number to check
	 * @param col0 = col to check
	 * @return true for duplicate, false otherwise
	 */
	private boolean colcheck(int num, int col) {

		y.clear(); //Clears array for a new column check
		y.add(num);
		for (int i = 0; i < 8; i ++) 
			y.add(b[i][col]);


		return duplicates(y);
	}

	/**
	 * Sorts array of numbers and checks for duplications by checking index and index plus 1
	 * @param x Array of the numbers in a row or column
	 * @return True if duplicates exist, false otherwise
	 */
	private boolean duplicates(ArrayList<Integer> x) {

		Collections.sort(x);

		for(int i = 0; i < x.size(); i++) {
			if(x.get(i) == 0) continue; //Moves to the first non 0 element
			else if (x.get(i) == x.get(i+1)) return true; //Checks the first non 0 element to the next element
		}

		return false;
	}
}