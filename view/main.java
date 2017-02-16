package view;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.ImageIO;

import flanagan.interpolation.CubicSplineFast;
import model.EmotionData;
import model.Model;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		EmotionData[] data = CacheHandler.parseCSV("HobbitCH1WithStrings.csv");

		ChartBuilder builder = new ChartBuilder(data, 500, 500);
		
		
		try {
			BufferedImage img = builder.buildBlendChart();

			File f = new File("src/lineGraphPara.png");

			ImageIO.write(img, "PNG", f);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//queries watson, will take a long time
	public static void writeCSV() {
		Model model = new Model(new File("src/resources/inputText/inputText"));

		try {
			CacheHandler.writeCSV(model.getEmotionData());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void buildAllLineGraphs(ChartBuilder builder) {
		for (int i = 0; i < 5; i++) {
			try {
				BufferedImage img = builder.buildLineGraph(i);

				File f = new File("src/" + System.currentTimeMillis() / 1000 + EmotionData.names[i] + ".png");

				ImageIO.write(img, "PNG", f);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static double average(double[] d) {

		double total = 0;
		for (double i : d) {
			total += i;
		}
		return total / (double) d.length;

	}

	public static double[][] getSplinePoints(double[] d) {

		double[] xVals = new double[d.length];// will be whole numbers

		for (int i = 0; i < xVals.length; i++) {

			xVals[i] = i;

		}
		double[] yVals = d;

		CubicSplineFast spline = new CubicSplineFast(xVals, yVals);

		double[] splineX = new double[xVals.length * 5];

		for (int i = 0; i < splineX.length; i++) {

			splineX[i] = ((double) i) * 0.2;

		}

		double[] splineY = new double[splineX.length];

		for (int i = 0; i < splineY.length; i++) {

			splineY[i] = spline.interpolate(splineX[i]);

		}

		double[][] returnRay = new double[splineX.length][];// formatting to
															// contain 2 element
															// arrays of {double
															// x, double y}

		for (int i = 0; i < splineY.length; i++) {

			returnRay[i] = new double[] { splineY[i], splineX[i] };

		}

		return returnRay;
	}

}
