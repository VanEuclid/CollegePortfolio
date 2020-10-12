#include <cstdlib>
#include <cstdio>
#include <cmath>
#include <fstream>
#include <vector>
#include <iostream>
#include <cassert>
#include <random>
#include <algorithm>
#include <Eigen>

using namespace Eigen;

// image background color
Vector3f bgcolor(1.0f, 1.0f, 1.0f);

// lights in the scene
std::vector<Vector3f> lightPositions = { Vector3f(  0.0, 60, 60)
                                       , Vector3f(-60.0, 60, 60)
                                       , Vector3f( 60.0, 60, 60) };

class Sphere
{
public:
	Vector3f center;  // position of the sphere
	float radius;  // sphere radius
	Vector3f surfaceColor; // surface color
	
  Sphere(
		const Vector3f &c,
		const float &r,
		const Vector3f &sc) :
		center(c), radius(r), surfaceColor(sc)
	{
	}

    // line vs. sphere intersection (note: this is slightly different from ray vs. sphere intersection!)
	bool intersect(const Vector3f &rayOrigin, const Vector3f &rayDirection, float &t0, float &t1) const
	{
		Vector3f l = center - rayOrigin;
		float tca = l.dot(rayDirection);
		if (tca < 0) return false;
		float d2 = l.dot(l) - tca * tca;
		if (d2 > (radius * radius)) return false;
        float thc = sqrt(radius * radius - d2);
		t0 = tca - thc;
		t1 = tca + thc;

		return true;
	}
};

// diffuse reflection model
Vector3f diffuse(const Vector3f &L, // direction vector from the point on the surface towards a light source
	const Vector3f &N, // normal at this point on the surface
	const Vector3f &diffuseColor,
	const float kd // diffuse reflection constant
	)
{
	Vector3f resColor = Vector3f::Zero();

	// TODO: implement diffuse shading model
	resColor = 0.333 * kd * std::max(L.dot(N), 0.0f) * diffuseColor;
	return resColor;
}

// Phong reflection model
Vector3f phong(const Vector3f &L, // direction vector from the point on the surface towards a light source
               const Vector3f &N, // normal at this point on the surface
               const Vector3f &V, // direction pointing towards the viewer
               const Vector3f &diffuseColor, 
               const Vector3f &specularColor, 
               const float kd, // diffuse reflection constant
               const float ks, // specular reflection constant
               const float alpha) // shininess constant
{
	Vector3f resColor = Vector3f::Zero();

	//Vector3f R = (2 * N * N.transpose() - Vector3f::Identity()) * L;
	Vector3f R = 2 * N * (N.dot(L)) - L;
	// TODO: implement Phong shading model
	resColor = 0.333 * ks * pow(std::max(V.dot(R), 0.0f), alpha) * specularColor;

	return resColor;
}
 
Vector3f trace(
	const Vector3f &rayOrigin,
	const Vector3f &rayDirection,
	const std::vector<Sphere> &spheres)
{
	// TODO: implement ray tracing as described in the homework description
	Vector3f pixelColor = bgcolor; //defaults background pixel if no intersection happens
	float t0, t1;
	float smallest_t0 = 1e6;
	int nearestSphere = -1;
	for (int i = 0; i < spheres.size(); i++) {
		if (spheres[i].intersect(rayOrigin, rayDirection, t0, t1)) {
			if (t0 < smallest_t0) {
				smallest_t0 = t0;
				nearestSphere = i;
				//pixelColor = Vector3f(1.0, 0.0, 0.0); //for out001
				//pixelColor = spheres[i].surfaceColor; //for out002
			}
		}
	}

	if (nearestSphere != -1) { 
		float lt0, lt1;
		bool eclipse = false;
		Vector3f shadedPixel = Vector3f::Zero();
		Vector3f spherePoint = rayOrigin + (smallest_t0 * rayDirection);
		Vector3f normalLightDirection;
		for (int light = 0; light < lightPositions.size(); light++) {
			for (int j = 0; j < spheres.size(); j++) {
				normalLightDirection = lightPositions[light] - spherePoint;
				normalLightDirection.normalize();
				if (spheres[j].intersect(spherePoint, normalLightDirection, lt0, lt1)) {
					eclipse = true;
				}
			}

			if (eclipse) {
				eclipse = false;
			}
			else {
				//shadedPixel += (spheres[nearestSphere].surfaceColor * 0.333); //for out003
				Vector3f normal = spherePoint - spheres[nearestSphere].center;
				normal.normalize();
				shadedPixel += diffuse(normalLightDirection, normal, spheres[nearestSphere].surfaceColor, 1) + phong(normalLightDirection, normal, -rayDirection, spheres[nearestSphere].surfaceColor, Vector3f::Ones(), 1, 3, 100); //for out004 and out005
				eclipse = false;
			}
		}
		pixelColor = shadedPixel;
	}
	return pixelColor;
}

void render(const std::vector<Sphere> &spheres)
{
  unsigned width = 640;
  unsigned height = 480;
  Vector3f *image = new Vector3f[width * height];
  Vector3f *pixel = image;
  float invWidth  = 1 / float(width);
  float invHeight = 1 / float(height);
  float fov = 30;
  float aspectratio = width / float(height);
	float angle = tan(M_PI * 0.5f * fov / 180.f);
	
	// Trace rays
	for (unsigned y = 0; y < height; ++y) 
	{
		for (unsigned x = 0; x < width; ++x) 
		{
			float rayX = (2 * ((x + 0.5f) * invWidth) - 1) * angle * aspectratio;
			float rayY = (1 - 2 * ((y + 0.5f) * invHeight)) * angle;
			Vector3f rayDirection(rayX, rayY, -1);
			rayDirection.normalize();
			*(pixel++) = trace(Vector3f::Zero(), rayDirection, spheres);
		}
	}
	
	// Save result to a PPM image
	std::ofstream ofs("./render.ppm", std::ios::out | std::ios::binary);
	ofs << "P6\n" << width << " " << height << "\n255\n";
	for (unsigned i = 0; i < width * height; ++i) 
	{
		const float x = image[i](0);
		const float y = image[i](1);
		const float z = image[i](2);

		ofs << (unsigned char)(std::min(float(1), x) * 255) 
			  << (unsigned char)(std::min(float(1), y) * 255) 
			  << (unsigned char)(std::min(float(1), z) * 255);
	}
	
	ofs.close();
	delete[] image;
}

int main(int argc, char **argv)
{
	std::vector<Sphere> spheres;
	// position, radius, surface color
	spheres.push_back(Sphere(Vector3f(0.0, -10004, -20), 10000, Vector3f(0.50, 0.50, 0.50)));
	spheres.push_back(Sphere(Vector3f(0.0, 0, -20), 4, Vector3f(1.00, 0.32, 0.36)));
	spheres.push_back(Sphere(Vector3f(5.0, -1, -15), 2, Vector3f(0.90, 0.76, 0.46)));
	spheres.push_back(Sphere(Vector3f(5.0, 0, -25), 3, Vector3f(0.65, 0.77, 0.97)));
	spheres.push_back(Sphere(Vector3f(-5.5, 0, -13), 3, Vector3f(0.90, 0.90, 0.90)));

	render(spheres);

	return 0;
}
