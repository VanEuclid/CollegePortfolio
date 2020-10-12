#include "GL/glew.h"
#include "GLFW/glfw3.h"
#include <string>
#include <vector>
#include <iostream>
#include <fstream>

#define M_PI 3.141592654f

unsigned int g_windowWidth = 600;
unsigned int g_windowHeight = 600;
char* g_windowName = "HW2-Rasterization";

GLFWwindow* g_window;

const int g_image_width = g_windowWidth;
const int g_image_height = g_windowHeight;

std::vector<float> g_image;

struct color
{
	unsigned char r, g, b;
};

int ReadLine(FILE *fp, int size, char *buffer)
{
	int i;
	for (i = 0; i < size; i++) {
		buffer[i] = fgetc(fp);
		if (feof(fp) || buffer[i] == '\n' || buffer[i] == '\r') {
			buffer[i] = '\0';
			return i + 1;
		}
	}
	return i;
}

//-------------------------------------------------------------------------------

void glfwErrorCallback(int error, const char* description)
{
	std::cerr << "GLFW Error " << error << ": " << description << std::endl;
	exit(1);
}

void glfwKeyCallback(GLFWwindow* p_window, int p_key, int p_scancode, int p_action, int p_mods)
{
	if (p_key == GLFW_KEY_ESCAPE && p_action == GLFW_PRESS)
	{
		glfwSetWindowShouldClose(g_window, GL_TRUE);
	}
}

void initWindow()
{
	// initialize GLFW
	glfwSetErrorCallback(glfwErrorCallback);
	if (!glfwInit())
	{
		std::cerr << "GLFW Error: Could not initialize GLFW library" << std::endl;
		exit(1);
	}

	g_window = glfwCreateWindow(g_windowWidth, g_windowHeight, g_windowName, NULL, NULL);
	if (!g_window)
	{
		glfwTerminate();
		std::cerr << "GLFW Error: Could not initialize window" << std::endl;
		exit(1);
	}

	// callbacks
	glfwSetKeyCallback(g_window, glfwKeyCallback);

	// Make the window's context current
	glfwMakeContextCurrent(g_window);

	// turn on VSYNC
	glfwSwapInterval(1);
}

void initGL()
{
	glClearColor(1.f, 1.f, 1.f, 1.0f);
}

void render()
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glDrawPixels(g_image_width, g_image_height, GL_LUMINANCE, GL_FLOAT, &g_image[0]);
}

void renderLoop()
{
	while (!glfwWindowShouldClose(g_window))
	{
		// clear buffers
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		render();

		// Swap front and back buffers
		glfwSwapBuffers(g_window);

		// Poll for and process events
		glfwPollEvents();
	}
}

void initImage()
{
	g_image.resize(g_image_width * g_image_height);
}

bool writeImage()
{
	std::vector<color> tmpData;
	tmpData.resize(g_image_width * g_image_height);

	for (int i = 0; i < g_image_height; i++)
	{
		for (int j = 0; j < g_image_width; j++)
		{
			// make sure the value will not be larger than 1 or smaller than 0, which might cause problem when converting to unsigned char
			float tmp = g_image[i* g_image_width + j];
			if (tmp < 0.0f)	tmp = 0.0f;
			if (tmp > 1.0f)	tmp = 1.0f;

			tmpData[(g_image_height - i - 1)* g_image_width + j].r = unsigned char(tmp * 255.0);
			tmpData[(g_image_height - i - 1)* g_image_width + j].g = unsigned char(tmp * 255.0);
			tmpData[(g_image_height - i - 1)* g_image_width + j].b = unsigned char(tmp * 255.0);
		}
	}

	FILE *fp = fopen("data/out.ppm", "wb");
	if (!fp) return false;

	fprintf(fp, "P6\r");
	fprintf(fp, "%d %d\r", g_image_width, g_image_height);
	fprintf(fp, "255\r");
	fwrite(tmpData.data(), sizeof(color), g_image_width * g_image_height, fp);
	fclose(fp);

	return true;
}

//-------------------------------------------------------------------------------

void putPixel(int x, int y)
{
	// clamp
	if (x >= g_image_width || x < 0 || y >= g_image_height || y < 0) return;

	// write
	g_image[y* g_image_width + x] = 1.0f;
}

void drawLine(int x1, int y1, int x2, int y2)
{
	// Task 1
	// This function should draw a line from pixel (x1, y1) to pixel (x2, y2)

	if (x1 > x2) { //reconfigure lines going right to left
		int tempx = x2;
		x2 = x1;
		x1 = tempx;
	}

	int dx, dy, D, inc0, inc1;
	dx = x2 - x1;
	dy = y2 - y1;
	D = 2 * dy - dx;
	inc0 = 2 * dy;
	inc1 = 2 * (dy - dx);
	putPixel(x1, y1);

	int absy = abs(y2 - y1);
	int absx = abs(x2 - x1);

	if (x1 < x2 && absy < absx && y1 < y2) { //slanted up lines
		while (x1 < x2) {
			if (D <= 0) {
				D += inc0;
			}
			else {
				D += inc1;
				y1 += 1;
			}
			x1 += 1;
			putPixel(x1, y1);
		}
	}
	else if (x1 == x2 && y1 < y2) { //vertical lines
		while (y1 < y2) {
			y1 += 1;
			putPixel(x1, y1);
		}
	}
	else if (y1 == y2 && x1 < x2) { //horizontal lines
		while (x1 < x2) {
			x1 += 1;
			putPixel(x1, y1);
		}
	}
	else { //slanted down lines
		inc1 = 2 * (dy + dx);
		while (x1 < x2) {
			if (D > 0) {
				D += inc0;
			}
			else {
				D += inc1;
				y1 -= 1;
			}
			x1 += 1;
			putPixel(x1, y1);
		}
	}
}

void drawCircle(int x0, int y0, int R)
{ 
	// Task 2
	// This function should draw a circle,
	// where (x0, y0) is the center of the circle and R is the radius
	int x, y, D;
	x = 0;
	y = R;
	D = 1 - R;
	putPixel(x0, y0 + y);
	putPixel(x0 + R, y0);
	putPixel(x0 - R, y0);
	putPixel(x0, y0 - R);
	while (y > x) {
		if (D < 0) {
			D +=  2 * x + 3;
		}
		else {
			D += + 2 * (x - y) + 5;
			y -= 1;
		}
		x += 1;

		putPixel(x0 + x, y0 + y);
		putPixel(x0 + y, y0 + x);
		putPixel(x0 + x, y0 - y);
		putPixel(x0 + y, y0 - x);
		putPixel(x0 - x, y0 + y);
		putPixel(x0 - y, y0 + x);
		putPixel(x0 - x, y0 - y);
		putPixel(x0 - y, y0 - x);
	}
}

void drawImage()
{	
	drawLine(150, 10, 450, 10); //should draw a line straight across
	drawLine(150, 310, 450, 310); //should draw a line straight across

	drawLine(450, 250, 150, 310); //should draw a slant line right to left
	drawLine(450, 250, 150, 250); //should draw a line straight across right to left
	drawLine(450, 310, 150, 250); //should draw a slant line right to left

	drawLine(150, 10, 150, 310); //should draw a line straight up
	drawLine(450, 10, 450, 310); //should draw a line straight up
	drawLine(150, 310, 300, 410); //should slant up
	drawLine(300, 410, 450, 310); //should slant down

	drawCircle(500, 500, 50);
	drawCircle(400, 450, 30);
	drawCircle(450, 400, 30);
}

int main()
{
	initImage();
	drawImage();
	writeImage();

	// render loop
	initWindow();
	initGL();
	renderLoop();

	return 0;
}
