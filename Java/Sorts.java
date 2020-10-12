//Written by Van Euclid Dy
//University of Utah CS

package homework5;

import java.util.Random;

public class Sorts<T> implements Comparable<T> {

	//BubbleSort, SelectionSort, InsertionSort, MergeSort, QuickSort

	public static void main(String[] args) {

		Integer a[] = new Integer[10];
		for(int i = 0; i < a.length; i++) {
			Random random = new Random();
			a[i] = random.nextInt(100);
		}
		
		for(int j = 0; j < a.length; j++)
		{
			System.out.print(a[j] + " ");
		}
		
		System.out.println();
		long starttime = System.currentTimeMillis();
		
		BubbleSort(a); //CALLS SORT

		long stoptime = System.currentTimeMillis();
		long runtime = stoptime - starttime;
		
		System.out.println();
		System.out.println("Run time is " + runtime + " miliseconds");
		System.out.println();
		
		for(int j = 0; j < a.length; j++)
		{
			System.out.print(a[j] + " ");
		}
		
		
	}

	//BUBBLESORT
	public static <T extends Comparable <? super T>> void BubbleSort(T[] a) {
		int index = a.length;
		while(index > 0)
		{
			for(int i = 0; i < index - 1; i++)
			{
				if(a[i].compareTo(a[i+1]) > 0)
				{
					T temp = a[i];
					a[i] = a[i+1];
					a[i+1] = temp;
				}
			}
			index--;
		}

	}

	//SELECTIONSORT
	public static <T extends Comparable <? super T>> void SelectionSort(T[] a) {

		int index = 0;
		int minIndex = 0;
		while(index < a.length - 1) {
			for(int i = index; i < a.length; i++) {
				if(a[minIndex].compareTo(a[i]) >= 0) {
					minIndex = i;
				}
			}
			T temp = a[index];
			a[index] = a[minIndex];
			a[minIndex] = temp; 
			index++;
			minIndex = index;
		}
	}

	//INSERTIONSORT
	public static <T extends Comparable <? super T>> void InsertionSort(T[] a) {

		for(int i = 1; i < a.length; i++) {
			T temp = a[i];
			int j = i;

			for(; j > 0 && temp.compareTo(a[j-1]) < 0; j--) 
				a[j] = a[j-1];
			a[j] = temp;
		}
	}

	//MERGESORT
	@SuppressWarnings("unchecked")
	public static <T extends Comparable <? super T>> void MergeSort(T[] a) {

		T[] temp = (T []) new Comparable[a.length];
		MergeSort( a, temp, 0, a.length - 1);
	}

	private static <T extends Comparable <? super T>> void MergeSort(T[] a, T[] temp, int left, int right) {

		if(left < right) {
			int center = (left + right) / 2;
			MergeSort( a, temp, left, center);
			MergeSort( a, temp, center + 1, right);
			merge( a, temp, left, center + 1, right);
		}
	}
	
	private static <T extends Comparable <? super T>> void merge(T[] a, T[] temp, int leftPos, int rightPos, int rightEnd) {

		int leftEnd = rightPos - 1;
		int tempPos = leftPos;
		int numEle = rightEnd - leftPos + 1;

		while(leftPos <= leftEnd && rightPos <= rightEnd) 
			if( a[leftPos].compareTo(a[rightPos]) <= 0) {
				temp[tempPos++] = a[leftPos++];
			}
			else
				temp[tempPos++] = a[rightPos++];

		while(leftPos <= leftEnd)
			temp[tempPos++] = a[leftPos++];
		while(rightPos <= rightEnd)
			temp[tempPos++] = a[rightPos++];

		for(int i = 0; i < numEle; i++, rightEnd--)
			a[rightEnd] = temp[rightEnd];

	}

	//QUICKSORT
	public static <T extends Comparable <? super T>> void QuickSort(T[] a) {
		
		QuickSort( a, 0, a.length -1);
	}
	
	private static <T extends Comparable <? super T>> void QuickSort(T[] a, int start, int end) {
		
		System.out.println();
		System.out.println("Quick Sort Called");
		System.out.println("start " + start);
		System.out.println("end " + end);
		print(a);
		System.out.println();
		
		if(start + 2 > end) { //CUT OFF FOR SMALL ARRAYS //Change to see QuickSort Run
			InsertionSort(a, start, end);
		}
		else {
			int middle = (start + end)/2;
			if(a[middle].compareTo(a[start]) < 0)
				swap(a, start, middle);
			if(a[end].compareTo(a[start]) < 0)
				swap(a, start, end);
			if(a[end].compareTo(a[middle]) < 0)
				swap(a, middle, end);
			
			swap(a, middle, end - 1);
			T pivot = a[end -1];
			
			int i;
			int j;
			for(i = start, j = end-1;;) {
				while(a[++i].compareTo(pivot) < 0)
					;
				while(pivot.compareTo(a[--j]) < 0)
					;
				if( i >= j)
					break;
				swap(a, i, j);
			}
			
			swap(a, i, end -1);
			
			QuickSort(a, start, i - 1);
			QuickSort(a, i + 1, end);
		}
		
	}
	
	private static <T extends Comparable <? super T>> void swap(T[] a, int i, int j) {
		
		T temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}
	
	private static <T extends Comparable <? super T>> void InsertionSort(T[] a, int start, int end) {
		
		System.out.println();
		System.out.println("Insertion Sort Called");
		System.out.println("start " + start);
		System.out.println("end " + end);
		print(a);
		System.out.println();
		
		for(int i = start; i < end + 1; i++) {
			T temp = a[i];
			int j = i;

			for(; j > 0 && temp.compareTo(a[j-1]) < 0; j--) 
				a[j] = a[j-1];
			a[j] = temp;
		}
	}
	
	private static <T extends Comparable <? super T>> void print(T[] a) {
		
		for(int j = 0; j < a.length; j++)
		{
			System.out.print(a[j] + " ");
		}
		
	}

	@Override
	public int compareTo(T o) {
		// TODO Auto-generated method stub

		if (this.compareTo(o) > 0) 
			return 1;
		else if (this.compareTo(o) < 0) 
			return -1;
		else
			return 0;
	}
}