package edu.cmu.lti.oaqa.openqa.dso.phase.keyterm;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.util.StopWords;

public class KeytermParser {
	
	public static List<String> parseUnigrams(String question) {
		String[] tokens = question.split("\\s+");
		List<String> words = new ArrayList<String>();
		for (String token : tokens)
			words.add(token);
		return words;
	}

	public static List<String> parseBigrams(List<String> words) {
		List<String> bigrams = new ArrayList<String>();
		for (int i = 0; i < words.size() - 1; i++) {
			String bigram = words.get(i) + " " + words.get(i + 1);
			bigram = bigram.replaceAll("\\p{Punct}", " ").trim();
			if (isBigram(bigram)) {
				bigrams.add(bigram);
			}
		}
		return bigrams;
	}

	private static boolean isBigram(String token) {
		if ((token.contains("What ") || token.contains("When ")
				|| token.contains("How ") || token.contains("what ") || token
					.contains("Who "))) {
			return false;
		}

		String[] subtokens = token.split(" ");
		for (String subtoken : subtokens) {
			subtoken = subtoken.trim();
			if (subtoken.equals("") || StopWords.contains(subtoken)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
