package desenfoque;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Main extends PApplet {

	private PImage img;
	private PImage imgDif;

	private int matrixsize;
	private int index;
	private int alto;
	private int matrixValue;
	
	private float gaus;

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
		size(500, 800);
	}

	public void setup() {
		index = 1;
		img = loadImage("../data/desenfoque/florColor.jpg");
		
		matrixsize = 3;
		gaus = 1;
		
		for (int i = 0; i < matrixsize; i++) {
			for (int j = 0; j < matrixsize; j++) {
				matrix[i][j] *= gaus;
				matrixValue += matrix[i][j];
			}
		}

		imgDif = createImage(img.width, img.height, RGB);

		image(img, 0, 0);
		filtrado();
	}

	private void applyBlur() {
		for (int i = 0; i < img.width; i++) {
			for (int j = 0; j < img.height; j++) {
				int index = i + j * img.width;

				PVector c = convolution(i, j, matrix, matrixsize, img);

				int r = (int) (red(img.get(i, j)) - c.x);
				int g = (int) (green(img.get(i, j)) - c.x);
				int b = (int) (blue(img.get(i, j)) - c.x);

				imgDif.pixels[index] = color(r, g, b);
				img.pixels[index] = color(c.x, c.y, c.z);
			}
		}
	}
	
	private void filtrado() {
		img.loadPixels();
		imgDif.loadPixels();

		for (int i = 0; i < 1; i++) {
			applyBlur();
		}

		imgDif.updatePixels();
		img.updatePixels();

		img.resize(img.width / index, img.height / index);
		imgDif.resize(imgDif.width / index, imgDif.height / index);

		image(img, 0, img.height * index + alto);
		image(imgDif, imgDif.width, imgDif.height * index + alto);

		alto += img.height * index;

		index++;
		if (index < 5) {
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
		rtotal = constrain(rtotal / matrixValue, 0, 255);
		gtotal = constrain(gtotal / matrixValue, 0, 255);
		btotal = constrain(btotal / matrixValue, 0, 255);

		// Return the resulting color
		return new PVector(rtotal, gtotal, btotal);
	}

}
