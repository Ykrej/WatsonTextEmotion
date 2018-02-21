package model;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputHandler {

	String input = "";

	/**
	 * The user's input and delimiter choice will be interpreted here.
	 * 
	 * @param in
	 *            the input string which will be split
	 * 
	 * @param s
	 *            the split parameter(0 = sentence, 1 = paragraph)
	 */
	public InputHandler(String in) {

		input = in;

	}

	/**
	 * call this to split the inputdata contained in this object
	 * 
	 * @return a String array containing the split string
	 */
	public String[] split() {
		
		// Splitting by sentence
		ArrayList<String> sentences = new ArrayList<String>();
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		
		iterator.setText(input);

		int lastIndex = iterator.first();

		while (lastIndex != BreakIterator.DONE) {

			int firstIndex = lastIndex;
			lastIndex = iterator.next();

			if (lastIndex != BreakIterator.DONE) {

				sentences.add(input.substring(firstIndex, lastIndex));

			}

		}
		
		
		String[] returnRay = new String[sentences.size()];//used to convert ArrayList to Array

		return sentences.toArray(returnRay);

	}
	public String[] splitByPara(){
		return input.split("\r\n");
	}

}
