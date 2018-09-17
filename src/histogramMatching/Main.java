package histogramMatching;

import processing.core.PApplet;
import processing.core.PImage;

public class Main extends PApplet {

	private PImage[] images;

	private boolean change;
	private int toShow;

	private int[] r1;
	private int[] g1;
	private int[] b1;

	private int[] r2;
	private int[] g2;
	private int[] b2;

	public static void main(String[] args) {
		PApplet.main("histogramMatching.Main");
	}

	@Override
	public void settings() {
//		size(1900, 1200);
		fullScreen();
	}

	@Override
	public void setup() {
		background(0);
		change = true;

		images = new PImage[2];

		images[0] = loadImage("../data/matching/900.jpg");
		images[1] = loadImage("../data/matching/540.jpg");

		pushStyle();
		colorMode(RGB, 255, 255, 255, 100);

		popStyle();
	}

	@Override
	public void draw() {
		if (change) {
			change = false;
			loadMethod(toShow);
			toShow++;
		}
	}

	@Override
	public void keyPressed() {
		change = true;
	}

	private void loadMethod(int index) {
		switch (index) {
		case 0:
			pushStyle();
			colorMode(RGB, 255, 255, 255, 100);
			histogramaAcumuladoRGB(images[0], 0, false);
			histogramaAcumuladoRGB(images[1], images[0].height, true);
			combinarHistogramas();
			popStyle();
			break;
		}
	}

	private void combinarHistogramas() {
		int[] redDef = new int[256];
		int[] greenDef = new int[256];
		int[] blueDef = new int[256];

		for (int i = 0; i < 256; i++) {
			int rr = comparador(r2[i], r1);
			int gg = comparador(g2[i], g1);
			int bb = comparador(b2[i], b1);

			redDef[i] = rr;
			greenDef[i] = gg;
			blueDef[i] = bb;
		}

		nuevaImagen(redDef, greenDef, blueDef, images[1]);
	}

	private void nuevaImagen(int[] red, int[] green, int[] blue, PImage img) {
		img.loadPixels();
		for (int i = 0; i < img.width; i++) {
			for (int j = 0; j < img.height; j++) {
				int index = i + j * img.width;

				int r = red[(int) red(img.pixels[index])];
				int g = green[(int) green(img.pixels[index])];
				int b = blue[(int) blue(img.pixels[index])];

				img.pixels[index] = color(r, g, b);
			}
		}
		img.updatePixels();
		image(img, 0, img.height);
	}

	private int comparador(int value, int[] toCompare) {
		boolean compare = true;
		int index = 0;
		while (compare) {
			if (value <= toCompare[index]) {
				System.out.println(value + " : " + toCompare[index]);
				return index;
			}
			if (index < 255) {
				index++;
			} else {
				compare = false;
			}
		}
		System.err.println("[ERROR: " + value + "]");
		return value;
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
	
	private void histogramaAcumuladoRGB(PImage img, float altura, boolean compare) {

		int[] red = new int[256];
		int[] green = new int[256];
		int[] blue = new int[256];

		int[] redA = new int[256];
		int[] greenA = new int[256];
		int[] blueA = new int[256];

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

		for (int i = 0; i < 256; i++) {
			if (i != 0) {
				redA[i] = redA[i - 1] + red[i];
				greenA[i] = greenA[i - 1] + green[i];
				blueA[i] = blueA[i - 1] + blue[i];
			}
		}

		if (compare) { // Segunda imagen
			r2 = redA;
			g2 = greenA;
			b2 = blueA;
		} else { // Primera imagen
			r1 = redA;
			g1 = greenA;
			b1 = blueA;
		}

		int maximo = img.pixels.length;

		for (int i = img.width; i < width; i += 3) {
			int which = (int) map(i, img.width, width, 0, 255);

			int y = (int) map(redA[which], 0, maximo, img.height, 0);
			stroke(255, 0, 0);
			line(i, img.height + altura, i, y + altura); // red

			y = (int) map(greenA[which], 0, maximo, img.height, 0);
			stroke(0, 255, 0);
			line(i + 1, img.height + altura, i + 1, y + altura); // green

			y = (int) map(blueA[which], 0, maximo, img.height, 0);
			stroke(0, 0, 255);
			line(i + 2, img.height + altura, i + 2, y + altura); // blue
		}

		image(img, 0, altura);
	}

}
