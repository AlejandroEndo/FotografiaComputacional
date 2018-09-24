package desenfoque;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Main extends PApplet {

	private PImage img, img2;
	
	private int index;
	private int alto;
	
	public static void main(String[] args) {
		PApplet.main("desenfoque.Main");
	}

	// float[][] matrix = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 }, };

	float[][] matrix = { { 1, 2, 1 }, { 2, 4, 2 }, { 1, 2, 1 }, };

	// float[][] matrix = {
	// {0,0,0,0,0} ,
	// {0,0,0,0,0} ,
	// {0,0,0,0,0} ,
	// {0,0,0,0,0} ,
	// {0,0,0,0,0} ,
	// } ;

	public void settings() {
		size(250, 800);
	}

	public void setup() {
		index = 1;
		img = loadImage("../data/desenfoque/florColor.jpg");
		img2 = loadImage("../data/desenfoque/florColor.jpg");
		
		image(img2, 0, 0);
		filtrado();
	}

	private void filtrado() {
		int matrixsize = 3;
		
		img.loadPixels();
		for (int x = 0; x < img.width; x++) {
			for (int y = 0; y < img.height; y++) {
				PVector c = convolution(x, y, matrix, matrixsize, img);
				int loc = x + y * img.width;
				img.pixels[loc] = color(c.x, c.y, c.z);
			}
		}
		img.updatePixels();
		img.resize(img.width / index, img.height / index);
		image(img, 0, img.height * index + alto);
		alto += img.height * index;
		index++;
		if(index < 5) {
			filtrado();
		}
		
	}

	private PVector convolution(int x, int y, float[][] matrix, int matrixsize, PImage img) {
		float rtotal = 0.0f;
		float gtotal = 0.0f;
		float btotal = 0.0f;
		int offset = matrixsize / 2;

		for (int i = 0; i < matrixsize; i++) {
			for (int j = 0; j < matrixsize; j++) {
				int xloc = x + i - offset;
				int yloc = y + j - offset;
				int loc = xloc + img.width * yloc;

				// Make sure we haven't walked off the edge of the pixel array
				// It is often good when looking at neighboring pixels to make sure we have not
				// gone off the edge of the pixel array by accident.
				loc = constrain(loc, 0, img.pixels.length - 1);

				// Calculate the convolution
				// We sum all the neighboring pixels multiplied by the values in the convolution
				// matrix.
				rtotal += (red(img.pixels[loc]) * matrix[i][j]);
				gtotal += (green(img.pixels[loc]) * matrix[i][j]);
				btotal += (blue(img.pixels[loc]) * matrix[i][j]);
			}
		}

		// Make sure RGB is within range
		rtotal = constrain(rtotal / 16, 0, 255);
		gtotal = constrain(gtotal / 16, 0, 255);
		btotal = constrain(btotal / 16, 0, 255);

		// Return the resulting color
		return new PVector(rtotal, gtotal, btotal);
	}

}
