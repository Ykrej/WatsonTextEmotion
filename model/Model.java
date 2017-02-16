package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Model {
	
	String[] splitInput;
	
	public Model(File input){
		String inputStr = "";
		try {
			Scanner fileReader = new Scanner(input);
			while(fileReader.hasNextLine()){
				inputStr += fileReader.nextLine() + "\n\r";
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		InputHandler splitter = new InputHandler(inputStr);
		
		splitInput = splitter.splitByPara();
		System.out.println(splitInput.length);	
	}
	
	public Model(String input){
		
		InputHandler splitter = new InputHandler(input);
		
		splitInput = splitter.splitByPara();
		System.out.println(splitInput.length);

	}
	
	/**
	 * grabs each piece of emotion data and then returns it, calls watson for each data piece
	 * 
	 * @return the data from each piece being analyzed
	 */
	public EmotionData[] getEmotionData(){
		long startTime = System.currentTimeMillis();
		EmotionParser analyzer = new EmotionParser();
		EmotionData[] returnRay = new EmotionData[splitInput.length];
		
		for(int i = 0;i < splitInput.length;i++){
			
			returnRay[i] = analyzer.analyzeText(splitInput[i]);
			System.out.println((System.currentTimeMillis() - startTime)/1000.0 + "Seconds Passed for " + i +" analyzations.");
			
		}
		
		return returnRay;
		
	}
	
}
