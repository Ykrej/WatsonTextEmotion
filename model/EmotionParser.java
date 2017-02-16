package model;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentEmotion;

//This class is used to access the Watson AlchemyLanguage API and received parsed/encoded data
public class EmotionParser {

	private static final String apiKey = "adff67995afa2b8426115c4f33c7c9a63498d884";// MUST
																					// HAVE
																					// VALID
																					// API
																					// KEY
	private AlchemyLanguage service;

	public EmotionParser() {

		service = new AlchemyLanguage(apiKey);

	}

	public EmotionParser(String key) {

		service = new AlchemyLanguage(key);

	}

	/**
	 * This is used to analyze strings for their emotion values
	 * 
	 * @param toAnalyze
	 *            the string which will be analyzed by watson
	 * 
	 * @return array containing of {anger, disgust, fear, joy, sadness}
	 */
	public EmotionData analyzeText(String toAnalyze) {
		Map<String, Object> input = new HashMap<String, Object>();
		input.put(AlchemyLanguage.TEXT, toAnalyze);
		double[] returnRay = new double[5];
		try {
			DocumentEmotion emotions = service.getEmotion(input).execute();

			// jsonParsing, used SimpleJson library
			JSONParser parser = new JSONParser();

			Object obj = parser.parse(emotions.toString());

			JSONObject jsonObj = (JSONObject) ((JSONObject) obj).get("docEmotions");

			returnRay[0] = (double) jsonObj.get("anger");
			returnRay[1] = (double) jsonObj.get("disgust");
			returnRay[2] = (double) jsonObj.get("fear");
			returnRay[3] = (double) jsonObj.get("joy");
			returnRay[4] = (double) jsonObj.get("sadness");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new EmotionData(returnRay, toAnalyze);

	}

}
