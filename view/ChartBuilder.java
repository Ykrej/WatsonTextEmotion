package view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ShapeUtilities;

import flanagan.analysis.CurveSmooth;
import flanagan.interpolation.CubicSplineFast;
import model.EmotionData;

public class ChartBuilder {

	JFreeChart[] charts;
	Map<String, BufferedImage> chartImages = new HashMap<String, BufferedImage>();

	EmotionData[] data;// {anger, disgust, fear, joy, sadness}
	int width, height;

	Color[] colors = { Color.red, new Color(150, 200, 0), Color.black, Color.yellow, Color.blue };

	/**
	 * 
	 * @param d
	 *            data in the format {anger, disgust, fear, joy, sadness}
	 * @param width
	 *            the desired pixel width of the chart
	 * @param height
	 *            the desired pixel height of the chart
	 */
	public ChartBuilder(EmotionData[] d, int w, int h) {

		data = d;

		width = w;
		height = h;

		chartImages.put("pie", buildPieChart());

	}

	private BufferedImage chartToImage(JFreeChart c) {

		return c.createBufferedImage(width, height);

	}

	public BufferedImage buildPieChart() {
		// sums each column to get the total value of each trait
		double[] netValues = new double[5];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < netValues.length; j++) {
				netValues[j] += data[i].getData(j);
			}
		}

		// calculate the percents
		double[] percents = new double[5];
		double total = 0;
		for (double i : netValues) {
			total += i;
		}
		for (int i = 0; i < percents.length; i++) {
			percents[i] = (netValues[i] / total) * 100;
		}

		// JFreeChart Pie chart Data formatting
		DefaultPieDataset pieData = new DefaultPieDataset();
		pieData.setValue(String.format("Anger %.2f", percents[0]) + "%", new Double(netValues[0]));
		pieData.setValue(String.format("Disgust %.2f", percents[1]) + "%", new Double(netValues[1]));
		pieData.setValue(String.format("Fear %.2f", percents[2]) + "%", new Double(netValues[2]));
		pieData.setValue(String.format("Joy %.2f", percents[3]) + "%", new Double(netValues[3]));
		pieData.setValue(String.format("Sadness %.2f", percents[4]) + "%", new Double(netValues[4]));

		// generate chart object
		JFreeChart chart = ChartFactory.createPieChart("Hobbit", pieData, false, true, false);
		chart.setBackgroundPaint(Color.white);// set background color of the
												// chart

		// color the slices
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionPaint(pieData.getKey(0), colors[0]);
		plot.setSectionPaint(pieData.getKey(1), colors[1]);
		plot.setSectionPaint(pieData.getKey(2), colors[2]);
		plot.setSectionPaint(pieData.getKey(3), colors[3]);
		plot.setSectionPaint(pieData.getKey(4), colors[4]);

		return chartToImage(chart);
	}

	//builds a line graph containing all the emotions
	public BufferedImage buildLineGraph() {

		// create XY DataSet
		XYSeriesCollection lineData = new XYSeriesCollection();

		//instantiate dataSet Pieces
		XYSeries[] lineSeries = new XYSeries[5];
		lineSeries[0] = new XYSeries("Anger");
		lineSeries[1] = new XYSeries("Disgust");
		lineSeries[2] = new XYSeries("Fear");
		lineSeries[3] = new XYSeries("Joy");
		lineSeries[4] = new XYSeries("Sadness");

		for (int i = 0; i < lineSeries.length; i++) {
			XYSeries currentSeries = lineSeries[i];
			double xVal = 0;
			double xIncrement = ((double) width) / ((double) data.length);
			for (int j = 0; j < data.length; j++) {

				currentSeries.add(xVal, data[j].getData(i));
				xVal++;

			}
			lineData.addSeries(currentSeries);
		}

		XYSeries[] splineSeries = new XYSeries[5];
		splineSeries[0] = new XYSeries("AngerTrend");
		splineSeries[1] = new XYSeries("DisgustTrend");
		splineSeries[2] = new XYSeries("FearTrend");
		splineSeries[3] = new XYSeries("JoyTrend");
		splineSeries[4] = new XYSeries("SadnessTrend");

		for (int i = 0; i < data.length; i++) {
			XYSeries currentSeries = splineSeries[i];
			double[] currentEmotion = new double[data.length];
			for (int j = 0; j < currentEmotion.length; j++) {//format emotion data to fit getSplinePoints() args
				currentEmotion[j] = data[j].getData(i);
			}
			for (double[] d : getSplinePoints(currentEmotion)) {
				currentSeries.add(d[1], d[0]);//add SplinePoints to graph
			}
			lineData.addSeries(currentSeries);
		}

		JFreeChart chart = ChartFactory.createXYLineChart("The Hobbit CH1", "Time", "Magnitude", lineData,
				PlotOrientation.VERTICAL, true, true, false);

		XYPlot plot = chart.getXYPlot();//get context of chart for customization

		XYSplineRenderer renderer = new XYSplineRenderer();
		renderer.setSeriesShape(0, ShapeUtilities.createDiamond(1f));
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesShapesVisible(5, false);
		renderer.setSeriesFillPaint(0, Color.red);
		renderer.setSeriesLinesVisible(0, false);

		//temporary
		renderer.setSeriesLinesVisible(1, false);
		renderer.setSeriesLinesVisible(2, false);
		renderer.setSeriesLinesVisible(3, false);
		renderer.setSeriesLinesVisible(4, false);
		renderer.setSeriesLinesVisible(6, false);
		renderer.setSeriesLinesVisible(7, false);
		renderer.setSeriesLinesVisible(8, false);
		renderer.setSeriesLinesVisible(9, false);
		renderer.setSeriesShapesVisible(1, false);
		renderer.setSeriesShapesVisible(2, false);
		renderer.setSeriesShapesVisible(3, false);
		renderer.setSeriesShapesVisible(4, false);
		renderer.setSeriesShapesVisible(6, false);
		renderer.setSeriesShapesVisible(7, false);
		renderer.setSeriesShapesVisible(8, false);
		renderer.setSeriesShapesVisible(9, false);
		//temporary

		plot.setRenderer(renderer);

		return chartToImage(chart);
	}

	//builds a line graph containing the specified emotion's data
	public BufferedImage buildLineGraph(int emotionChoice) {
		//{anger, disgust, fear, joy, sadness}
		double[] inputData = new double[data.length];
		for (int i = 0; i < inputData.length; i++) {

			inputData[i] = data[i].getData(emotionChoice);

		}

		// create XY DataSet Collection
		XYSeriesCollection lineData = new XYSeriesCollection();

		//add points to dataSet
		XYSeries lineSeries = new XYSeries("Data Points");
		for (int i = 0; i < inputData.length; i++) {

			lineSeries.add(i, inputData[i]);

		}
		//construct trend spline
		XYSeries trendSeries = new XYSeries("Trend");
		for (double[] d : getSplinePoints(inputData)) {
			trendSeries.add(d[1], d[0]);//add SplinePoints to graph
		}

		lineData.addSeries(trendSeries);
		lineData.addSeries(lineSeries);

		JFreeChart chart = ChartFactory.createXYLineChart("The Hobbit CH1: " + EmotionData.names[emotionChoice], "Time",
				"Magnitude", lineData, PlotOrientation.VERTICAL, true, true, false);

		XYSplineRenderer renderer = new XYSplineRenderer();//for customization
		//customize trendLine
		renderer.setSeriesShapesVisible(0, false);
		renderer.setSeriesPaint(0, colors[emotionChoice]);
		//customize raw data points
		renderer.setSeriesShape(1, ShapeUtilities.createDiamond(1f));
		renderer.setSeriesLinesVisible(1, false);
		renderer.setSeriesPaint(1, colors[emotionChoice]);

		XYPlot plot = chart.getXYPlot();

		plot.getRangeAxis().setRange(0.0, 1.0);

		plot.setRenderer(renderer);

		return chartToImage(chart);
	}

	//helper method to buildLineGraph()
	/**
	 * This method takes the data of a single emotion and calculates a spline
	 * which fits to the trend of emotion This implements
	 * flanagan.analysis.CurveSmooth in order to smooth the data which will be
	 * put into the spline
	 * 
	 * @param d
	 *            the input data to calculate off of (all values pertaining to a
	 *            certain emotion ex. data[0][i] = anger values)
	 * 
	 * @return x,y arrays grouped in a single parent array
	 */
	private static double[][] getSplinePoints(double[] d) {
		double[] xVals = new double[d.length];// will be whole numbers

		for (int i = 0; i < xVals.length; i++) {

			xVals[i] = i;

		}

		double[] yVals = smoothData(d);

		//splinePoints once data is smoothed
		CubicSplineFast spline = new CubicSplineFast(xVals, yVals);

		double[] splineX = new double[xVals.length * 5];

		for (int i = 0; i < splineX.length; i++) {

			splineX[i] = ((double) i) * 0.2;

		}

		double[] splineY = new double[splineX.length];

		for (int i = 0; i < splineY.length; i++) {

			splineY[i] = spline.interpolate(splineX[i]);

		}

		double[][] returnRay = new double[splineX.length][];// formatting to contain 2 element arrays of {double x, double y}

		for (int i = 0; i < splineY.length; i++) {

			returnRay[i] = new double[] { splineY[i], splineX[i] };

		}

		return returnRay;

	}

	/**
	 * Smooths Data points in order to create a nonlinear trendline this
	 * implements flanagan.analysis.CurveSmooth in order to smooth
	 * 
	 * @param d
	 *            input data which the users wishes to be smoothed
	 * @return an array of smoothed data
	 */
	private static double[] smoothData(double[] d) {
		CurveSmooth smoother = new CurveSmooth(d);
		return smoother.movingAverage(25);
	}

	public BufferedImage buildBlendChart() {
		//calculate blended colors for each piece of EmotionData
		Color[] blendedColors = new Color[data.length];
		for (int i = 0; i < data.length; i++) {
			double[] currentData = data[i].getDataArray();
			double sum = 0.0;
			Color[] weightedColors = new Color[5];
			for (double d : currentData) {
				sum += d;
			}
			double[] colorWeights = new double[5];
			for (int j = 0; j < colorWeights.length; j++) {
				colorWeights[j] = currentData[j] / sum;
			}
			for (int j = 0; j < colorWeights.length; j++) {
				weightedColors[j] = applyWeight(EmotionData.colors[j], colorWeights[j]);
			}
			blendedColors[i] = mixColors(weightedColors);
		}
		//calculate width of each rectangle
		double increment = (double) width / (double) blendedColors.length;

		BufferedImage image = new BufferedImage(blendedColors.length*2,300,BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D context = image.createGraphics();
		for(int i = 0;i < blendedColors.length;i++){
			context.setColor(blendedColors[i]);
			context.fillRect(i*2, 0, i*2 + 2, image.getHeight());
		}
		return image;
	}
	
	public BufferedImage buildBlendedHistogram() {
		//calculate blended colors for each piece of EmotionData
		Color[] blendedColors = new Color[data.length];
		for (int i = 0; i < data.length; i++) {
			double[] currentData = data[i].getDataArray();
			double sum = 0.0;
			Color[] weightedColors = new Color[5];
			for (double d : currentData) {
				sum += d;
			}
			double[] colorWeights = new double[5];
			for (int j = 0; j < colorWeights.length; j++) {
				colorWeights[j] = currentData[j] / sum;
			}
			for (int j = 0; j < colorWeights.length; j++) {
				weightedColors[j] = applyWeight(EmotionData.colors[j], colorWeights[j]);
			}
			blendedColors[i] = mixColors(weightedColors);
		}
		//calculate width of each rectangle
		double increment = (double) width / (double) blendedColors.length;

		//JFreeChart DataSet
		HistogramDataset dataset = new HistogramDataset();
		dataset.setType(HistogramType.RELATIVE_FREQUENCY);
		dataset.addSeries("Emotions", new double[blendedColors.length], 1);
		JFreeChart chart = ChartFactory.createHistogram("Emotion Color Blend", "", "", dataset,
				PlotOrientation.VERTICAL, true, false, false);
		XYBarRenderer renderer = new XYBarRenderer(){
			public Paint getItemPaint(int row, int column){
				return blendedColors[row];
			}
		};
		
		chart.getXYPlot().setRenderer(renderer);
		
		return chartToImage(chart);
	}
	//helper method for buildBlendedHistogram() & buildBlendChart()
	private Color applyWeight(Color c, double d) {
		return new Color((int) ((double) c.getRed() * d), (int) ((double) c.getGreen() * d),
				(int) ((double) c.getBlue() * d));
	}

	//helper method for buildBlendedHistogram() & buildBlendChart()
	private Color mixColors(Color[] c) {
		Color mix = mixColors(c[0], c[1]);
		for (int i = 2; i < c.length; i++) {
			mix = mixColors(mix, c[i]);
		}
		return mix;
	}

	//helper method for buildBlendedHistogram() & buildBlendChart()
	private Color mixColors(Color c1, Color c2) {
		int[] newColor = new int[3];//Arrays holds RGB data, sum of both colors
		newColor[0] = c1.getRed() + c2.getRed();
		newColor[1] = c1.getGreen() + c2.getGreen();
		newColor[2] = c1.getBlue() + c2.getBlue();

		if (getLargest(newColor) > 255) {//if overflow
			double divisor = 255.0 / getLargest(newColor);
			for (int i = 0; i < newColor.length; i++) {
				System.out.println(((double) newColor[i]) * divisor);
				newColor[i] = (int) (((double) newColor[i]) * divisor);
			}
		}
		return new Color(newColor[0], newColor[1], newColor[2]);
	}

	//helper method for buildBlendedHistogram() & buildBlendChart()
	private int getLargest(int[] nums) {

		int largest = nums[0];
		for (int i = 0; i < nums.length; i++) {
			if (nums[i] > largest)
				largest = nums[i];
		}
		return largest;
	}
}
