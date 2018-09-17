package whitePatch;

import processing.core.PApplet;
import processing.core.PImage;

public class Main extends PApplet {

	private PImage[] images;
	private PImage[] newImage;

	private float maxR;
	private float maxG;
	private float maxB;

	public static void main(String[] args) {
		PApplet.main("whitePatch.Main");
	}

	@Override
	public void settings() {
		size(1180, 630);
	}

	@Override
	public void setup() {
		background(0);
		
		images = new PImage[6];
		newImage = new PImage[6];

		for (int i = 0; i < images.length; i++) {
			images[i] = loadImage("data/whitePatch/hand" + i + ".bmp");
			newImage[i] = createImage(images[i].width, images[i].height, RGB);
		}

		for (int i = 0; i < 6; i++) {
			image(images[i], images[0].width * 0, images[0].height * i);
			whitePatch(i);
			image(newImage[i], images[0].width * 1, images[0].height * i);
			coordenadaCromatica(i);
			image(newImage[i], images[0].width * 2, images[0].height * i);
			clasificador(i);
			image(newImage[i], images[0].width * 3, images[0].height * i);
		}
	}
	
	private void whitePatch(int index) {
		newImage[index].loadPixels();

		float pixiesR[] = new float[images[index].pixels.length];
		float pixiesG[] = new float[images[index].pixels.length];
		float pixiesB[] = new float[images[index].pixels.length];

		for (int i = 0; i < images[index].width; i++) {
			for (int j = 0; j < images[index].height; j++) {
				int a = i + (j * images[index].width);

				float r = red(images[index].get(i, j));
				float g = green(images[index].get(i, j));
				float b = blue(images[index].get(i, j));

				pixiesR[a] = r;
				pixiesG[a] = g;
				pixiesB[a] = b;
			}
		}

		maxR = max(pixiesR);
		maxG = max(pixiesG);
		maxB = max(pixiesB);

		for (int i = 0; i < images[index].width; i++) {
			for (int j = 0; j < images[index].height; j++) {
				int a = i + (j * images[index].width);

				float r = red(images[index].get(i, j));
				float g = green(images[index].get(i, j));
				float b = blue(images[index].get(i, j));

				float newR = (255 / maxR) * r;
				float newG = (255 / maxG) * g;
				float newB = (255 / maxB) * b;

				newImage[index].pixels[a] = color(newR, newG, newB);
				newImage[index].updatePixels();
			}
		}
	}
	
	private void coordenadaCromatica(int index) {
		newImage[index].loadPixels();

		for (int i = 0; i < images[index].width; i++) {
			for (int j = 0; j < images[index].height; j++) {
				int a = i + (j * images[index].width);

				float r = red(newImage[index].get(i, j));
				float g = green(newImage[index].get(i, j));
				float b = blue(newImage[index].get(i, j));

				float nr = r / (r + g + b);
				float ng = g / (r + g + b);
				float nb = b / (r + g + b);

				newImage[index].pixels[a] = color(255 * nr, 255 * ng, 255 * nb);
			}
		}

		newImage[index].updatePixels();
	}
	
	private void clasificador(int index) {
		newImage[index].loadPixels();

		for (int i = 0; i < newImage[index].width; i++) {
			for (int j = 0; j < newImage[index].height; j++) {
				int a = i + (j * newImage[index].width); // formula para transformar a una dimencion

				float r = red(newImage[index].get(i, j));
				float g = green(newImage[index].get(i, j));
				float b = blue(newImage[index].get(i, j));

				if (r > 61 && r < 174)
					r = 1;
				else
					r = 0;

				if (g > 26 && g < 106)
					g = 1;
				else
					g = 0;

				if (b > 0 && b < 77)
					b = 1;
				else
					b = 0;

				int v = (r + g + b > 2) ? 255 : 0;

				newImage[index].pixels[a] = color(v);
			}
		}

		newImage[index].updatePixels();
	}

}
