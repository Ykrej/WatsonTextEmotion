package view;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import model.EmotionData;

public class CacheHandler {

	public CacheHandler() {

	}

	/**
	 * 
	 * will parse and return cached data
	 * 
	 * @param fileName
	 *            only the filename is needed as all of the cached data will be
	 *            located in the same directory
	 * 
	 * @return a parsed version of the cached data
	 */
	public static EmotionData[] parseCSV(String fileName) {
		System.out.println(fileName);
		try {
			CSVReader reader = new CSVReader(new FileReader("src/resources/cache/" + fileName), ',');// instantiate
																							// reader
			
			// parse using list
			List<String[]> strData = reader.readAll();

			EmotionData[] returnRay = new EmotionData[strData.size()];
			for (int i = 0; i < strData.size(); i++) {
				double[] tempRay = new double[5];
				for (int j = 0; j < tempRay.length; j++) {
					tempRay[j] = Double.parseDouble(strData.get(i)[j]);
				}
				returnRay[i] = new EmotionData(tempRay, strData.get(i)[5]);
			}
			return returnRay;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	/**
	 * caches contents of recent watson call to a CSV file
	 * 
	 * @throws IOException
	 */
	public static void writeCSV(EmotionData[] rawData) throws IOException {

		CSVWriter writer = new CSVWriter(new FileWriter(new File("src/resources/cache/" + System.currentTimeMillis() * 1000 + ".csv")),
				',');

		for (int i = 0; i < rawData.length; i++) {

			// need to convert double[] to String[]
			String[] tempRay = new String[6];

			for (int j = 0; j < tempRay.length - 1; j++) {
				tempRay[j] = Double.toString(rawData[i].getData(j));
			}
			tempRay[5] = rawData[i].getText();
			writer.writeNext(tempRay);// write to CSV

		}

		writer.close();

	}

}
