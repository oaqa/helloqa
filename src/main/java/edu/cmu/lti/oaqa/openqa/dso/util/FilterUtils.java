package edu.cmu.lti.oaqa.openqa.dso.util;

import info.ephyra.nlp.indices.FunctionWords;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class FilterUtils {

	private static Pattern yearPattern = Pattern.compile("\\d{4}");

	private static Pattern timestampPattern = Pattern
			.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z");

	private static Pattern punctuationPattern = Pattern.compile("\\p{Punct}+");

	private static List<String> sourceNames = Arrays.asList(new String[] {
			"question", "questions", "answers", "answer", "wikipedia", "wiki",
			"yahoo", "ask"});

	public static boolean isYear(String text) {
		return yearPattern.matcher(text).matches();
	}

	public static boolean hasTimestamp(String text) {
		return timestampPattern.matcher(text).find();
	}

	public static boolean isPunctuation(String text) {
		return punctuationPattern.matcher(text).matches();
	}

	public static boolean isSourceName(String text) {
		return sourceNames.contains(text.toLowerCase());
	}

	public static boolean shouldIgnoreWord(String word) {
		word = word.toLowerCase();
		return isPunctuation(word) || FunctionWords.lookup(word);
	}
}
