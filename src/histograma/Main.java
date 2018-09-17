package histograma;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Main extends PApplet {

	private PImage image;

	public static void main(String[] args) {
		PApplet.main("histograma.Main");
	}

	@Override
	public void settings() {
		size(2000, 720);
	}

	@Override
	public void setup() {
		 background(0);

		image = loadImage("../data/histograma/johnny.jpg");
		// image = loadImage("../data/histograma/HUMANRAMEN.jpg");

		pushStyle();
		// tint(255, 100);
		image(image, 0, 0);
		popStyle();

		pushStyle();
		colorMode(RGB, 255, 255, 255, 100);
//		 historgramaRGB(image);
		popStyle();

		pushStyle();
		colorMode(HSB, 255, 100, 100, 100);
		// histogramaHSB(image);
		popStyle();

		pushStyle();
		colorMode(RGB, 255, 255, 255, 100);
		 histogramaYxy(image);
		popStyle();

		pushStyle();
		colorMode(RGB, 255, 255, 255, 100);
//		graficaYxy(image);
		popStyle();
	}

	private void graficaYxy(PImage img) {

		float[] mx = { 0.490f, 0.310f, 0.200f };
		float[] my = { 0.177f, 0.813f, 0.011f };
		float[] mz = { 0.000f, 0.010f, 0.990f };

		for (int i = 0; i < img.width; i++) {
			for (int j = 0; j < img.height; j++) {
				int index = i + j * img.width;

				float r = (int) red(img.pixels[index]);
				float g = (int) green(img.pixels[index]);
				float b = (int) blue(img.pixels[index]);

				// RGB -> XYZ

				float Xi = r * mx[0] + g * mx[1] + b * mx[2];
				float Yi = r * my[0] + g * my[1] + b * my[2];
				float Zi = r * mz[0] + g * mz[1] + b * mz[2];

				float xx = Xi / (Xi + Yi + Zi);
				float yy = Yi / (Xi + Yi + Zi);

				PVector pos = new PVector(map(xx, 0, 1, img.width, width), map(yy, 0, 1, img.height, 0));

				stroke(r, g, b);
				point(pos.x, pos.y);

				// println(Xi, Yi);
			}
		}
	}

	private void histogramaYxy(PImage img) {

		float[] mx = { 0.490f, 0.310f, 0.200f };
		float[] my = { 0.177f, 0.813f, 0.011f };
		float[] mz = { 0.000f, 0.010f, 0.990f };

		int[] Y = new int[256];
		int[] x = new int[1000];
		int[] y = new int[1000];

		for (int i = 0; i < img.width; i++) {
			for (int j = 0; j < img.height; j++) {
				int index = i + j * img.width;

				float r = (int) red(img.pixels[index]);
				float g = (int) green(img.pixels[index]);
				float b = (int) blue(img.pixels[index]);

				// RGB -> XYZ

				float Xi = r * mx[0] + g * mx[1] + b * mx[2];
				float Yi = r * my[0] + g * my[1] + b * my[2];
				float Zi = r * mz[0] + g * mz[1] + b * mz[2];

				int Yy = (int) Yi;
				int xx = (int) ((Xi / (Xi + Yi + Zi)) * 1000);
				int yy = (int) ((Yi / (Xi + Yi + Zi)) * 1000);

				Y[Yy]++;
				x[xx]++;
				y[yy]++;
			}
		}

		int maxY = max(Y);
		int maxx = max(x);
		int maxy = max(y);

		int maxxy = max(maxx, maxy);

		for (int i = img.width; i < width; i += 3) {
			int wy = (int) map(i, img.width, width, 0, 255);
			int wx = (int) map(i, img.width, width, 0, 1000);

			int k = (int) map(Y[wy], maxY, 0, 0, img.height);
			stroke(255, 255, 0);
			line(i, img.height, i, k);

			k = (int) map(x[wx], maxxy, 0, 0, img.height);
			stroke(0, 255, 255);
			line(i + 1, img.height, i + 1, k);

			k = (int) map(y[wx], maxxy, 0, 0, img.height);
			stroke(255, 0, 255);
			line(i + 2, img.height, i + 2, k);
		}
	}

	private void histogramaHSB(PImage img) {
		int[] hue = new int[256];
		int[] saturation = new int[256];
		int[] brightness = new int[256];

		for (int i = 0; i < img.width; i++) {
			for (int j = 0; j < img.height; j++) {
				int index = i + j * img.width;

				int h = (int) hue(img.pixels[index]);
				int s = (int) saturation(img.pixels[index]);
				int b = (int) brightness(img.pixels[index]);

				hue[h]++;
				saturation[s]++;
				brightness[b]++;
			}
		}

		int maxH = max(hue);
		int maxS = max(saturation);
		int maxB = max(brightness);

		int maximo = (int) max(maxH, maxS, maxB);

		for (int i = img.width; i < width; i += 3) {
			int which = (int) map(i, img.width, width, 0, 255);

			int y = (int) map(hue[which], maximo, 0, 0, img.height);
			stroke(which, 100, 100);
			line(i, img.height, i, y); // hue

			y = (int) map(saturation[which], 0, maximo, img.height, 0);
			stroke(127);
			line(i + 1, img.height, i + 1, y); // saturation

			y = (int) map(brightness[which], 0, maximo, img.height, 0);
			stroke(255);
			line(i + 2, img.height, i + 2, y); // brightness
		}
	}

	private void historgramaRGB(PImage img) {
		int[] red = new int[256];
		int[] green = new int[256];
		int[] blue = new int[256];

		for (int i = 0; i < img.width; i++) {
			for (int j = 0; j < img.height; j++) {
				int index = i + j * img.width;

				int r = (int) red(img.pixels[index]);
				int g = (int) green(img.pixels[index]);
				int b = (int) blue(img.pixels[index]);

				red[r]++;
				green[g]++;
				blue[b]++;
			}
		}

		int maxR = max(red);
		int maxG = max(green);
		int maxB = max(blue);

		int maximo = (int) max(maxR, maxG, maxB);

		for (int i = img.width; i < width; i += 3) {
			int which = (int) map(i, img.width, width, 0, 255);

			int y = (int) map(red[which], maximo, 0, 0, img.height);
			stroke(255, 0, 0);
			line(i, img.height, i, y); // red

			y = (int) map(green[which], 0, maximo, img.height, 0);
			stroke(0, 255, 0);
			line(i + 1, img.height, i + 1, y); // green

			y = (int) map(blue[which], 0, maximo, img.height, 0);
			stroke(0, 0, 255);
			line(i + 2, img.height, i + 2, y); // blue
		}
	}
}
