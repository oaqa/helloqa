package edu.cmu.lti.oaqa.openqa.dso.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CandidateExtractorBySubAnswerType extends
		CandidateExtractorByAnswerType {

	public CandidateExtractorBySubAnswerType(String answerType,
			String[] sentences) {
		super(answerType, sentences);

		// get IDs of taggers for most specific NE type that can be tagged
		List<int[]> nePatterns = getNEType(answerType);

		// extract NEs of that type
		tokens = getTokens(sentences);

		List<List<String>> nesLists = new ArrayList<List<String>>();
		for (int i = 0; i < sentences.length; i++) {
			nesLists.add(new ArrayList<String>());
		}

		for (int i = 0; i < nePatterns.size() - 1; i++) {
			String[][] currentNEs = extractCandidatesUsingPatterns(
					nePatterns.get(0), sentences);
			currentNEs = refineNEs(sentences.length, nesLists, currentNEs);

			for (int j = 0; j < sentences.length; j++) {
				nesLists.get(j).addAll(Arrays.asList(currentNEs[j]));
			}
		}

		nes = new String[sentences.length][];
		for (int i = 0; i < sentences.length; i++) {
			nes[i] = nesLists.get(i)
					.toArray(new String[nesLists.get(i).size()]);
		}
	}

	private String[][] refineNEs(int sentenceLength,
			List<List<String>> previousNEs, String[][] currentNEs) {
		String[][] nes = new String[sentenceLength][];
		for (int i = 0; i < sentenceLength; i++) {
			HashSet<String> duplicate = new HashSet<String>();
			if (previousNEs != null && previousNEs.get(i) != null) {
				for (int j = 0; j < previousNEs.get(i).size(); j++) {
					if (!duplicate.contains(previousNEs.get(i).get(j)
							.toLowerCase())) {
						duplicate.add(previousNEs.get(i).get(j).toLowerCase());
					}
				}
			}

			List<String> neList = new ArrayList<String>();
			if (currentNEs[i] != null) {
				for (int j = 0; j < currentNEs[i].length; j++) {
					if (!duplicate.contains(currentNEs[i][j].toLowerCase())) {
						neList.add(currentNEs[i][j].toLowerCase());
					}
				}
			}
			nes[i] = neList.toArray(new String[neList.size()]);
		}

		return nes;
	}
}
