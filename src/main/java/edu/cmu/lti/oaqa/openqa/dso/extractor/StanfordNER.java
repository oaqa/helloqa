package edu.cmu.lti.oaqa.openqa.dso.extractor;

import java.util.ArrayList;
import java.util.HashMap;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;

import edu.stanford.nlp.ling.CoreLabel;


public class StanfordNER {

	private static AbstractSequenceClassifier<CoreLabel> classifier;
	public static String defaultSerializedClassifier ="./res/ephyra/nlp/netagger/stanford/english.all.3class.distsim.crf.ser.gz";
	
	/**
	 * Checks whether the model for the StanfordNeTagger has been loaded.
	 * 
	 * @return <code>true</code> iff the StanfordNeTagger is initialized
	 */
	public static boolean isInitialized() {
		return (classifier != null);
	}
	
	/**
	 * Initializes the StanfordNeTagger (loads the model).
	 */
	public static boolean init() {
		return init(defaultSerializedClassifier);
	}
	
	/**
	 * Gets the path of the current serialized classifier.
	 * 
	 * @return path of the serializedClassifier
	 */
	public static AbstractSequenceClassifier<CoreLabel>  getCurrentClassifier() {
		return classifier;
	}
	
	/**
	 * Initializes the StanfordNeTagger with a custom model.
	 * 
	 * @param customSerializedClassifier path of the custom classifier to load
	 */
	public static boolean init(String customSerializedClassifier) {
		try {
			classifier = CRFClassifier
					.getClassifierNoExceptions(customSerializedClassifier);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Extracts NEs from an array of sentences.
	 * 
	 * @param sentences array of sentences
	 * @return NEs per sentence and NE type, using a HashMap since the types
	 *         might change with the model
	 */
	public static HashMap<String, String[][]> extractNEs(String[] sentences) {
		HashMap<String, String[][]> results = new HashMap<String, String[][]>();
		
		for (int s = 0; s < sentences.length; s++) {
			HashMap<String, String[]> sentenceNEs = extractNEs(sentences[s]);
			ArrayList<String> sentenceNeTypes =
				new ArrayList<String>(sentenceNEs.keySet());
			for (int t = 0; t < sentenceNeTypes.size(); t++) {
				String type = sentenceNeTypes.get(t);
				String[][] nes = results.get(type);
				if (nes == null) {
					nes = new String[sentences.length][];
					for (int i = 0; i < sentences.length; i++)
						nes[i] = new String[0];
					results.put(type, nes);
				}
				nes[s] = sentenceNEs.get(type);
			}
		}
		return results;
	}
	
	/**
	 * Extracts NEs from an individual sentence. Initializes the
	 * StanfordNeTagger if not done before.
	 * 
	 * @param sentence the sentence
	 * @return NEs per NE type, using a HashMap since the types might change
	 *         with the model
	 */
	public static HashMap<String, String[]> extractNEs(String sentence) {
		if (!isInitialized() ||
				sentence.length() <=1 ||
				sentence.matches("\\W*+"))
			return new HashMap<String, String[]>();
		
		String neString = classifier.classifyToString(sentence);
		
		String[] neTokens = neString.split("\\s");
		String mark = "O";
		String ne = "";
		HashMap <String, ArrayList<String>> nesByType =
			new HashMap<String, ArrayList<String>>();
		for (int i = 0; i < neTokens.length; i++) {
			String[] s = neTokens[i].split("\\/");
			if (s.length == 1) s = new String[] {s[0], "O"};
			if (mark.equals(s[1])) {
				ne += " " + s[0];
			} else {
				if (!"O".equals(mark)) {
					ArrayList<String> nes = nesByType.get(mark);
					if (nes == null) {
						nes = new ArrayList<String>();
						nesByType.put(mark, nes);
					}
					nes.add(ne.trim());
				}
				ne = s[0];
				mark = s[1];
			}
		}
		if (!"O".equals(mark)) {
			ArrayList<String> nes = nesByType.get(mark);
			if (nes == null) {
				nes = new ArrayList<String>();
				nesByType.put(mark, nes);
			}
			nes.add(ne.trim());
		}
		
		HashMap<String, String[]> results = new HashMap<String, String[]>();
		ArrayList<String> neTypes = new ArrayList<String>(nesByType.keySet());
		for (int t = 0; t < neTypes.size(); t++) {
			String type = neTypes.get(t);
			ArrayList<String> nes = nesByType.get(type);
			results.put("NE" + type.toLowerCase(), nes.toArray(new String[nes.size()]));
		}
		
		return results;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
