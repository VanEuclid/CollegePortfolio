/*******************************************
 * Solutions for the CS:APP Performance Lab
 ********************************************/

#include <stdio.h>
#include <stdlib.h>
#include "defs.h"

/* 
 * Please fill in the following student struct 
 */
student_t student = {
  "Van Dy",     /* Full name */
  "u0567427@utah.edu",  /* Email address */
};

/***************
 * COMPLEX KERNEL
 ***************/

/******************************************************
 * Your different versions of the complex kernel go here
 ******************************************************/

/* 
 * naive_complex - The naive baseline version of complex 
 */
char naive_complex_descr[] = "naive_complex: Naive baseline implementation";
void naive_complex(int dim, pixel *src, pixel *dest)
{
  int i, j;

  for(i = 0; i < dim; i++)
    for(j = 0; j < dim; j++)
    {

      dest[RIDX(dim - j - 1, dim - i - 1, dim)].red = ((int)src[RIDX(i, j, dim)].red +
						      (int)src[RIDX(i, j, dim)].green +
						      (int)src[RIDX(i, j, dim)].blue) / 3;
      
      dest[RIDX(dim - j - 1, dim - i - 1, dim)].green = ((int)src[RIDX(i, j, dim)].red +
							(int)src[RIDX(i, j, dim)].green +
							(int)src[RIDX(i, j, dim)].blue) / 3;
      
      dest[RIDX(dim - j - 1, dim - i - 1, dim)].blue = ((int)src[RIDX(i, j, dim)].red +
						       (int)src[RIDX(i, j, dim)].green +
						       (int)src[RIDX(i, j, dim)].blue) / 3;
    }
}

/* 
 * complex - Your current working version of complex
 * IMPORTANT: This is the version you will be graded on
 */
char complex_descr[] = "complex: (High: 1.8) turn in version";
void complex(int dim, pixel *src, pixel *dest)
{
 int i, j, ii, jj;

  pixel* srcAlias = src;
  pixel* destAlias = dest;

  pixel tempPixel, destPixel;
  int firstArg, secondArg, plug, plug2, plug2CM, blocksize;
  long pixelParse;

  if(dim == 1024) {
    blocksize = 32;
  }
  else if (dim == 256 || dim == 512) {
    blocksize = 16;
  }
  else {
    blocksize = 8;
  }

  for(j = 0; j < dim; j += blocksize) {
    for(i = 0; i < dim; i += blocksize) {  
      for(jj = j; jj < j + blocksize; jj++) {

	firstArg = dim - jj - 1;
	plug2CM = firstArg * dim;

        for(ii = i; ii < i + blocksize; ii++) {
 
	  secondArg = dim - ii - 1;

	  plug = ii * dim + jj;
	  plug2 = plug2CM + secondArg;

	  tempPixel = srcAlias[plug];

	  pixelParse = ((int)tempPixel.red + (int)tempPixel.green + (int)tempPixel.blue) / 3;

	  destPixel = destAlias[plug2];

	  destPixel.red = pixelParse;
	  destPixel.green = pixelParse;
	  destPixel.blue = pixelParse;

	  destAlias[plug2] = destPixel;
	}
      }
    }
  }
}

/*
 *Version 10
 *
char complex_descr10[] = "complex: complex v10 (cache blocking)";
void complex10(int dim, pixel* src, pixel* dest)
{
  int i, j, ii, jj;
  pixel* srcAlias = src;
  pixel* destAlias = dest;

  long pixelParse;
  int firstArg, secondArg, blocksize;

  if(dim == 1024) {
    blocksize = 32;
  }
  else if (dim == 256 || dim == 512) {
    blocksize = 16;
  }
  else {
    blocksize = 8;
  }

  for(j = 0; j < dim; j += blocksize) {
    for(i = 0; i < dim; i += blocksize) {
      for(jj = j; jj < j + blocksize; jj++) {

         firstArg = dim - jj - 1;

	 for(ii = i; ii < i + blocksize; ii++) {
 
	  secondArg = dim - ii - 1;

	  pixelParse = ((int)srcAlias[RIDX(ii, jj, dim)].red + (int)srcAlias[RIDX(ii, jj, dim)].green + (int)srcAlias[RIDX(ii, jj, dim)].blue) / 3;

	  destAlias[RIDX(firstArg, secondArg, dim)].red = pixelParse;
	  destAlias[RIDX(firstArg, secondArg, dim)].green = pixelParse;
	  destAlias[RIDX(firstArg, secondArg, dim)].blue = pixelParse;
	 }
      }
    }
  }
}
 */

/*
 *Version 11
 *
char complex_descr11[] = "complex: complex v11 (cache blocking + v8)";
void complex11(int dim, pixel *src, pixel *dest)
{
  int i, j, ii, jj;

  pixel* srcAlias = src;
  pixel* destAlias = dest;

  pixel tempPixel, destPixel;

  int firstArg, secondArg, plug, plug2, plug2CM, blocksize;

  long pixelParse;

  if(dim == 1024) {
    blocksize = 32;
  }
  else if (dim == 256 || dim == 512) {
    blocksize = 16;
  }
  else {
    blocksize = 8;
  }

  for(j = 0; j < dim; j += blocksize) {
    for(i = 0; i < dim; i += blocksize) {  
      for(jj = j; jj < j + blocksize; jj++) {

	firstArg = dim - jj - 1;
	plug2CM = firstArg * dim; //code motion

        for(ii = i; ii < i + blocksize; ii++) {
 
	  secondArg = dim - ii - 1;

	  plug = ii * dim + jj;
	  plug2 = plug2CM + secondArg;

	  tempPixel = srcAlias[plug]; //reduce memory lookup

	  pixelParse = ((int)tempPixel.red + (int)tempPixel.green + (int)tempPixel.blue) / 3;

	  destPixel = destAlias[plug2];

	  destPixel.red = pixelParse;
	  destPixel.green = pixelParse;
	  destPixel.blue = pixelParse;

	  destAlias[plug2] = destPixel;
	}
      }
    }
  }
}
 */

/*
 *Version 12
 *
char complex_descr12[] = "complex: complex v12 (compete versions)";
void complex12(int dim, pixel* src, pixel* dest)
{
   int i, j, ii, jj;

  pixel* srcAlias = src;
  pixel* destAlias = dest;

  pixel srcPix, destPix;
  int firstArg, secondArg, blocksize;
  long pixelParse;

  if(dim == 1024) {
    blocksize = 32;
  }
  else if (dim == 256 || dim == 512) {
    blocksize = 16;
  }
  else {
    blocksize = 8;
  }

  for(j = 0; j < dim; j += blocksize) {
    for(i = 0; i < dim; i += blocksize) {
      for(jj = j; jj < j + blocksize; jj++) {

         firstArg = dim - jj - 1;

	 for(ii = i; ii < i + blocksize; ii++) {
 
	  secondArg = dim - ii - 1;

	  srcPix = srcAlias[RIDX(ii, jj, dim)];
	  pixelParse = ((int)srcPix.red + (int)srcPix.green + (int)srcPix.blue) / 3;

	  destPix = destAlias[RIDX(firstArg, secondArg, dim)];

	  destPix.red = pixelParse;
	  destPix.green = pixelParse;
	  destPix.blue = pixelParse;

	  destAlias[RIDX(firstArg, secondArg, dim)] = destPix;

	 }
      }
    }
  }
}
 */

/*********************************************************************
 * register_complex_functions - Register all of your different versions
 *     of the complex kernel with the driver by calling the
 *     add_complex_function() for each test function. When you run the
 *     driver program, it will test and report the performance of each
 *     registered test function.  
 *********************************************************************/

void register_complex_functions() {
  add_complex_function(&complex, complex_descr);
  add_complex_function(&naive_complex, naive_complex_descr);

  //add_complex_function(&complex10, complex_descr10);
  //add_complex_function(&complex11, complex_descr11);
  //add_complex_function(&complex12, complex_descr12);
}

/***************
 * MOTION KERNEL
 ***************/

/***************************************************************
 * Various helper functions for the motion kernel
 * You may modify these or add new ones any way you like.
 **************************************************************/

/* 
 * weighted_combo - Returns new pixel value at (i,j) 
 */
__attribute__((always_inline)) static pixel weighted_combo(int dim, int i, int j, pixel *src) 
{
  int ii, jj;
  pixel current_pixel;

  int red, green, blue;
  red = green = blue = 0;

  int num_neighbors = 0;

  for(ii=0; ii < 3; ii++)
    for(jj=0; jj < 3; jj++) 
      if ((i + ii < dim) && (j + jj < dim)) 
      {
	num_neighbors++;
	red += (int) src[RIDX(i+ii,j+jj,dim)].red;
	green += (int) src[RIDX(i+ii,j+jj,dim)].green;
	blue += (int) src[RIDX(i+ii,j+jj,dim)].blue;
      }
  
  current_pixel.red = (unsigned short) (red / num_neighbors);
  current_pixel.green = (unsigned short) (green / num_neighbors);
  current_pixel.blue = (unsigned short) (blue / num_neighbors);
  
  return current_pixel;
}

/*
 * handles special edge pixels
__attribute__((always_inline)) static pixel weighted_comboFull(int dim, int i, int j, pixel *src) 
{
  pixel current_pixel, temp, temp1, temp2, temp3, temp4, temp5, temp6, temp7, temp8;

  int red, green, blue;
  red = green = blue = 0;

  int num_neighbors = 0; 

  int jArg0 = j;
  int jArg1 = j + 1;
  int jArg2 = j + 2;

  int iArg0 = i;
  int iArg1 = i + 1;
  int iArg2 = i + 2;

  if ((iArg0 < dim) && (jArg0 < dim)){

    temp = src[RIDX(iArg0, jArg0, dim)];

    num_neighbors++;
    red += (int) temp.red;
    green += (int) temp.green;
    blue += (int) temp.blue;
  }
      
  if ((iArg1 < dim) && (jArg0 < dim)){
 
    temp1 = src[RIDX(iArg1, jArg0, dim)];

    num_neighbors++;
    red += (int) temp1.red;
    green += (int) temp1.green;
    blue += (int) temp1.blue;
  }

  if ((iArg2 < dim) && (jArg0 < dim)){
 
    temp2 = src[RIDX(iArg2, jArg0, dim)];

    num_neighbors++;
    red += (int) temp2.red;
    green += (int) temp2.green;
    blue += (int) temp2.blue;
  }

  if ((iArg0 < dim) && (jArg1 < dim)){

    temp3 = src[RIDX(iArg0, jArg1, dim)];

    num_neighbors++;
    red += (int) temp3.red;
    green += (int) temp3.green;
    blue += (int) temp3.blue;
  }
      
  if ((iArg1 < dim) && (jArg1 < dim)){

    temp4 = src[RIDX(iArg1, jArg1, dim)];

    num_neighbors++;
    red += (int) temp4.red;
    green += (int) temp4.green;
    blue += (int) temp4.blue;
  }

  if ((iArg2 < dim) && (jArg1 < dim)){
	 
    temp5 = src[RIDX(iArg2, jArg1, dim)];

    num_neighbors++;
    red += (int) temp5.red;
    green += (int) temp5.green;
    blue += (int) temp5.blue;
  }

  if ((iArg0 < dim) && (jArg2 < dim)){

    temp6 = src[RIDX(iArg0, jArg2, dim)];

    num_neighbors++;
    red += (int) temp6.red;
    green += (int) temp6.green;
    blue += (int) temp6.blue;
  }
      
  if ((iArg1 < dim) && (jArg2 < dim)){

    temp7 = src[RIDX(iArg1, jArg2, dim)];

    num_neighbors++;
    red += (int) temp7.red;
    green += (int) temp7.green;	
    blue += (int) temp7.blue;
  }

  if ((iArg2 < dim) && (jArg2 < dim)){
	 
    temp8  = src[RIDX(iArg2, jArg2, dim)];

    num_neighbors++;
    red += (int) temp8.red;
    green += (int) temp8.green;
    blue += (int) temp8.blue;
  }

  current_pixel.red = (unsigned short) (red / num_neighbors);
  current_pixel.green = (unsigned short) (green / num_neighbors);
  current_pixel.blue = (unsigned short) (blue / num_neighbors);
  
  return current_pixel;
}
 */

/*
 * handles inside pixels three at a time
__attribute__((always_inline)) static pixel weighted_inside3(int dim, int i, int j, pixel *src){
 
  int ii;
  pixel current_pixel;

  int red, green, blue, iPlug;
  red = green = blue = 0;

  int jPlug = j + 1;
  int jPlug2 = j + 2;

  for(ii = 0; ii < 3; ii++) {

    iPlug = i + ii;

    int pixel1 = RIDX(iPlug, j, dim);
    red += (int) src[pixel1].red;
    green += (int) src[pixel1].green;
    blue += (int) src[pixel1].blue;

    int pixel2 = RIDX(iPlug, jPlug, dim);
    red += (int) src[pixel2].red;
    green += (int) src[pixel2].green;
    blue += (int) src[pixel2].blue;

    int pixel3 = RIDX(iPlug, jPlug2, dim);
    red += (int) src[pixel3].red;
    green += (int) src[pixel3].green;
    blue += (int) src[pixel3].blue;
  }
  
  current_pixel.red = (unsigned short) (red / 9);
  current_pixel.green = (unsigned short) (green / 9);
  current_pixel.blue = (unsigned short) (blue / 9);
  
  return current_pixel;
}
 */

/******************************************************
 * Your different versions of the motion kernel go here
 ******************************************************/

/*
 * naive_motion - The naive baseline version of motion 
 */
char naive_motion_descr[] = "naive_motion: Naive baseline implementation";
void naive_motion(int dim, pixel *src, pixel *dst) 	
{
  int i, j;
    
  for (i = 0; i < dim; i++)
    for (j = 0; j < dim; j++)
      dst[RIDX(i, j, dim)] = weighted_combo(dim, i, j, src);
}

/*
 * motion - Your current working version of motion. 
 * IMPORTANT: This is the version you will be graded on
 */
char motion_descr[] = "motion: (High: 3.1) turn in version";
void motion(int dim, pixel *src, pixel *dst)
{
  int i, j;
  int red, green, blue;
    
  for (i = 0; i < dim - 2; i++) {
    for (j = 0; j < dim - 2; j++) {

      red = green = blue = 0;

      red += src[RIDX(i,j,dim)].red; //TOP PIXELS
      green += src[RIDX(i,j,dim)].green;
      blue += src[RIDX(i,j,dim)].blue;

      red += src[RIDX(i+1,j,dim)].red;
      green += src[RIDX(i+1,j,dim)].green;
      blue += src[RIDX(i+1,j,dim)].blue;

      red += src[RIDX(i+2,j,dim)].red;
      green += src[RIDX(i+2,j,dim)].green;
      blue += src[RIDX(i+2,j,dim)].blue;

      red += src[RIDX(i,j+1,dim)].red; //MID PIXELS
      green += src[RIDX(i,j+1,dim)].green;
      blue += src[RIDX(i,j+1,dim)].blue;

      red += src[RIDX(i+1,j+1,dim)].red;
      green += src[RIDX(i+1,j+1,dim)].green;
      blue += src[RIDX(i+1,j+1,dim)].blue;

      red += src[RIDX(i+2,j+1,dim)].red;
      green += src[RIDX(i+2,j+1,dim)].green;
      blue += src[RIDX(i+2,j+1,dim)].blue;

      red += src[RIDX(i,j+2,dim)].red; //BOTTOM PIXELS
      green += src[RIDX(i,j+2,dim)].green;
      blue += src[RIDX(i,j+2,dim)].blue;

      red += src[RIDX(i+1,j+2,dim)].red;
      green += src[RIDX(i+1,j+2,dim)].green;
      blue += src[RIDX(i+1,j+2,dim)].blue;

      red += src[RIDX(i+2,j+2,dim)].red;
      green += src[RIDX(i+2,j+2,dim)].green;
      blue += src[RIDX(i+2,j+2,dim)].blue;

      dst[RIDX(i, j, dim)].red = (unsigned short) (red / 9);
      dst[RIDX(i, j, dim)].green = (unsigned short) (green / 9);
      dst[RIDX(i, j, dim)].blue = (unsigned short) (blue / 9);
    }
  }

  for(i = 0; i < dim; i++) {
    for(j = dim - 2; j < dim; j++) {
      dst[RIDX(i, j, dim)] = weighted_combo(dim, i, j, src);
    }
  }

  for(i = dim - 2; i < dim; i++) {
    for(j = 0; j < dim - 2; j++) {
      dst[RIDX(i, j, dim)] = weighted_combo(dim, i, j, src);
    }
  }
}

/*
 *Version 1
char motion_descr1[] = "motion: motion v1 (cache blocking)";
void motion1(int dim, pixel *src, pixel *dst)
{
  int i, j, ii, jj;

  pixel* srcAlias = src;
  pixel* dstAlias = dst;

  int blocksize = 8;

  for (j = 0; j < dim; j += blocksize) {
    for (i = 0; i < dim; i += blocksize) {
      for(jj = j; jj < j + blocksize; jj++) {
	for(ii = i; ii < i + blocksize; ii++) {
	  dstAlias[RIDX(ii, jj, dim)] = weighted_combo(dim, ii, jj, srcAlias);
	}
      }
    }
  }
}
 */

/*
 *Version 4
char motion_descr4[] = "motion: motion v4 (unroll)";
void motion4(int dim, pixel *src, pixel *dst)
{
  int i, j;

  pixel* srcAlias = src;
  pixel* dstAlias = dst;

  for(j = 0; j < dim - 2; j += 3) {
    for(i = 0; i < dim - 2; i++) {
      dstAlias[RIDX(i, j, dim)] = weighted_inside3(dim, i, j, srcAlias);
      dstAlias[RIDX(i, j + 1, dim)] = weighted_inside3(dim, i, j + 1, srcAlias);
      dstAlias[RIDX(i, j + 2, dim)] = weighted_inside3(dim, i, j + 2, srcAlias);
    }
  }

  for(j = dim - 2; j < dim; j++) {
    for(i = 0; i < dim; i++) {
      dstAlias[RIDX(i, j, dim)] = weighted_combo(dim, i, j, srcAlias);
    }
  }

  for(j = 0; j < dim - 2; j++) {
    for(i = dim - 2; i < dim; i++) {
      dstAlias[RIDX(i, j, dim)] = weighted_combo(dim, i, j, srcAlias);
    }
  }
}
 */

/*
 *Version 7
char motion_descr7[] = "motion: motion v7 (flipped order i and j, removed loop for right edge, quad access)";
void motion7(int dim, pixel *src, pixel *dst)
{
  int i, j;

  pixel* srcAlias = src;
  pixel* dstAlias = dst;

  for(j = 0; j < dim - 2; j++) {
    for (i = 0; i < dim - 4; i += 4) {
      dstAlias[RIDX(i, j, dim)] = weighted_inside3(dim, i, j, srcAlias);
      dstAlias[RIDX(i + 1, j, dim)] = weighted_inside3(dim, i + 1, j, srcAlias);
      dstAlias[RIDX(i + 2, j, dim)] = weighted_inside3(dim, i + 2, j, srcAlias);
      dstAlias[RIDX(i + 3, j, dim)] = weighted_inside3(dim, i + 3, j, srcAlias);
    }

    dstAlias[RIDX(i, j, dim)] = weighted_comboFull(dim, i, j, srcAlias);
    dstAlias[RIDX(i + 1, j, dim)] = weighted_comboFull(dim, i + 1, j, srcAlias);
    dstAlias[RIDX(i + 2, j, dim)] = weighted_comboFull(dim, i + 2, j, srcAlias);
    dstAlias[RIDX(i + 3, j, dim)] = weighted_comboFull(dim, i + 3, j, srcAlias);
  }

  for(j = dim - 2; j < dim; j++) {
    for(i = 0; i < dim; i++) {
      dstAlias[RIDX(i, j, dim)] = weighted_comboFull(dim, i, j, srcAlias);
    }
  }
}
 */

/*
 *Version 11 
char motion_descr11[] = "motion: v11 (rethink)";
void motion11(int dim, pixel *src, pixel *dst) 	
{
  int i, j;
  int red, green, blue;

  pixel topLeft, topMid, topRight, midLeft, midMid, midRight, botLeft, botMid, botRight;
    
  for (i = 0; i < dim - 2; i++) {
    for (j = 0; j < dim - 2; j++) {

      red = green = blue = 0;

      topLeft = src[RIDX(i, j, dim)];
      topMid = src[RIDX(i + 1, j, dim)];
      topRight = src[RIDX(i + 2, j, dim)];

      midLeft = src[RIDX(i, j + 1, dim)];
      midMid = src[RIDX(i + 1, j + 1, dim)];
      midRight = src[RIDX(i + 2, j + 1, dim)];

      botLeft = src[RIDX(i, j + 2, dim)];
      botMid = src[RIDX(i + 1, j + 2, dim)];
      botRight = src[RIDX(i + 2, j + 2, dim)];

      red += topLeft.red; //TOP PIXELS
      green += topLeft.green;
      blue += topLeft.blue;

      red += topMid.red;
      green += topMid.green;
      blue += topMid.blue;

      red += topRight.red;
      green += topRight.green;
      blue += topRight.blue;

      red += midLeft.red; //MID PIXELS
      green += midLeft.green;
      blue += midLeft.blue;

      red += midMid.red;
      green += midMid.green;
      blue += midMid.blue;

      red += midRight.red;
      green += midRight.green;
      blue += midRight.blue;

      red += botLeft.red; //BOTTOM PIXELS
      green += botLeft.green;
      blue += botLeft.blue;

      red += botMid.red;
      green += botMid.green;
      blue += botMid.blue;

      red += botRight.red;
      green += botRight.green;
      blue += botRight.blue;

      dst[RIDX(i, j, dim)].red = (unsigned short) (red / 9);
      dst[RIDX(i, j, dim)].green = (unsigned short) (green / 9);
      dst[RIDX(i, j, dim)].blue = (unsigned short) (blue / 9);
    }
  }

  for(i = 0; i < dim; i++) {
    for(j = dim - 2; j < dim; j++) {
      dst[RIDX(i, j, dim)] = weighted_combo(dim, i, j, src);
    }
  }

  for(i = dim - 2; i < dim; i++) {
    for(j = 0; j < dim - 2; j++) {
      dst[RIDX(i, j, dim)] = weighted_combo(dim, i, j, src);
    }
  }
}
 */

/*
 *Version 12 
char motion_descr12[] = "motion: v12 (src pull direct)";
void motion12(int dim, pixel *src, pixel *dst) 	
{
  int i, j;
  int red, green, blue;
    
  for (i = 0; i < dim - 2; i++) {
    for (j = 0; j < dim - 2; j++) {

      red = green = blue = 0;

      red += src[RIDX(i,j,dim)].red; //TOP PIXELS
      green += src[RIDX(i,j,dim)].green;
      blue += src[RIDX(i,j,dim)].blue;

      red += src[RIDX(i+1,j,dim)].red;
      green += src[RIDX(i+1,j,dim)].green;
      blue += src[RIDX(i+1,j,dim)].blue;

      red += src[RIDX(i+2,j,dim)].red;
      green += src[RIDX(i+2,j,dim)].green;
      blue += src[RIDX(i+2,j,dim)].blue;

      red += src[RIDX(i,j+1,dim)].red; //MID PIXELS
      green += src[RIDX(i,j+1,dim)].green;
      blue += src[RIDX(i,j+1,dim)].blue;

      red += src[RIDX(i+1,j+1,dim)].red;
      green += src[RIDX(i+1,j+1,dim)].green;
      blue += src[RIDX(i+1,j+1,dim)].blue;

      red += src[RIDX(i+2,j+1,dim)].red;
      green += src[RIDX(i+2,j+1,dim)].green;
      blue += src[RIDX(i+2,j+1,dim)].blue;

      red += src[RIDX(i,j+2,dim)].red; //BOTTOM PIXELS
      green += src[RIDX(i,j+2,dim)].green;
      blue += src[RIDX(i,j+2,dim)].blue;

      red += src[RIDX(i+1,j+2,dim)].red;
      green += src[RIDX(i+1,j+2,dim)].green;
      blue += src[RIDX(i+1,j+2,dim)].blue;

      red += src[RIDX(i+2,j+2,dim)].red;
      green += src[RIDX(i+2,j+2,dim)].green;
      blue += src[RIDX(i+2,j+2,dim)].blue;

      dst[RIDX(i, j, dim)].red = (unsigned short) (red / 9);
      dst[RIDX(i, j, dim)].green = (unsigned short) (green / 9);
      dst[RIDX(i, j, dim)].blue = (unsigned short) (blue / 9);
    }
  }

  for(i = 0; i < dim; i++) {
    for(j = dim - 2; j < dim; j++) {
      dst[RIDX(i, j, dim)] = weighted_combo(dim, i, j, src);
    }
  }

  for(i = dim - 2; i < dim; i++) {
    for(j = 0; j < dim - 2; j++) {
      dst[RIDX(i, j, dim)] = weighted_combo(dim, i, j, src);
    }
  }
}
 */

/********************************************************************* 
 * register_motion_functions - Register all of your different versions
 *     of the motion kernel with the driver by calling the
 *     add_motion_function() for each test functifgon.  When you run the
 *     driver program, it will test and report the performance of each
 *     registered test function.  
 *********************************************************************/

void register_motion_functions() {
  add_motion_function(&motion, motion_descr);
  add_motion_function(&naive_motion, naive_motion_descr);

  //add_motion_function(&motion1, motion_descr1);
  //add_motion_function(&motion4, motion_descr4);
  //add_motion_function(&motion7, motion_descr7);
  //add_motion_function(&motion11, motion_descr11);
  //add_motion_function(&motion12, motion_descr12);
}
