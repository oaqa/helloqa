package edu.cmu.lti.oaqa.openqa.dso.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.answer.AnswerCandidateScorer;
import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;

public class LeafEphyraSubType extends
		CandidateExtractorByAnswerType {

	public LeafEphyraSubType(SupportingEvidenceArg arg) {
		super(arg);
	}
	
	@Override
	public List<AnswerCandidate> getAnswerCandidates(SupportingEvidenceArg arg) {
		List<AnswerCandidate> candidates = new ArrayList<AnswerCandidate>();

		// get IDs of taggers for most specific NE type that can be tagged
		List<int[]> nePatterns = getNEType(arg.getAnswerType());

		int rank = 1;
		for (RetrievalResult document : arg.getPassages()) {
			// split search result into sentences and tokenize sentences
			String documentText = document.getText().replace(" ... ", " ! ");

			// get refined sentence
			String[] sentences = detectSentences(documentText);

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

			candidates.addAll(AnswerCandidateScorer.getAnswerCandidates(arg,
					getTypeName(), nes, sentences, rank));
			rank++;
		}

		return candidates;
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

	@Override
	public String getTypeName() {
		return "ephyra_sub_type";
	}
}
