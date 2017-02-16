package model;
import java.awt.Color;

//anger, disgust, fear, joy, sadness
public class EmotionData {
	
	public static final String[] names = {"Anger", "Disgust", "Fear", "Joy", "Sadness"};
	public static final Color[] colors = { Color.red, new Color(150, 200, 0), Color.black, Color.yellow, Color.blue };
	
	private double[] data;
	
	private String text;
	
	public EmotionData(double[] d, String t){
		
		data = d;
		text = t;
		
	}	
	
	//getters
	public String getText(){
		return text;
	}
	public double anger(){
		return data[0];
	}
	public double disgust(){
		return data[1];
	}
	public double fear(){
		return data[2];
	}
	public double joy(){
		return data[3];
	}
	public double sadness(){
		return data[4];
	}
	public double getData(int index){
		return data[index];
	}
	public double[] getDataArray(){
		return data;
	}
}
