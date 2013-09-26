package edu.cmu.lti.oaqa.openqa.dso.answer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import edu.cmu.lti.oaqa.openqa.dso.extractor.CandidateExtractorBase;

public class AnswerCandidateScorer {

	private static final String[] CANDIDATE_EXTRACTOR_TYPE = { "MAIN", "SUB",
			"EXTENSION" };

	private static final double CANDIDATE_EXTENSION_WEIGHT = 0.5;

	private static final double CANDIDATE_SUB_WEIGHT = 3;

	public static List<AnswerCandidate> getAnswerCandidates(
			SupportingEvidenceArg arg, String candidateType, String[][] nes, String[] sentences, int rank) {

		List<AnswerCandidate> candidates = new ArrayList<AnswerCandidate>();
		if(nes==null){
			return candidates;
		}
		
		for (int i = 0; i < nes.length; i++) {
			if (nes[i] != null && nes[i].length > 0) {
				// update arg
				arg.updateSupportingEvidenceArg(i, nes, sentences);

				// score nes
				double[] evidenceScores = AnswerScorer
						.getFusionUnigramProximityScore(arg);
				
				// distinct candidate of answer type, and candidate extension
				if (candidateType.equals(CANDIDATE_EXTRACTOR_TYPE[1])) {
					for (int k = 0; k < evidenceScores.length; k++) {
						if (evidenceScores[k] >= CANDIDATE_SUB_WEIGHT) {
							evidenceScores[k] = CANDIDATE_SUB_WEIGHT;
						}
					}
				}

				if (candidateType.equals(CANDIDATE_EXTRACTOR_TYPE[2])
						&& !arg.getAnswerType().equals("NEnone")) {
					for (int k = 0; k < evidenceScores.length; k++) {
						if (evidenceScores[k] >= CANDIDATE_EXTENSION_WEIGHT) {
							evidenceScores[k] = CANDIDATE_EXTENSION_WEIGHT;
						}
					}
				}

				// add NEs to candidates
				int neCounter = 0;
				for (String ne : nes[i]) {
					if(ne==null){
						continue;
					}
					ne = candidateNormalization(ne, arg.getAnswerType());
					AnswerCandidate candidate = new AnswerCandidate(ne,
							new ArrayList<RetrievalResult>());
					candidate.setScore(AnswerScorer.normalization(
							evidenceScores[neCounter],
							arg.getKeywords().size(), rank));
					candidates.add(candidate);
					neCounter++;
				}
			}
		}

		return candidates;
	}

	public static String[][] refineNEs(List<String[][]> previousNELs,
			String[][] currentNEs) {
		String[][] nes = new String[currentNEs.length][];
		for (int i = 0; i < currentNEs.length; i++) {
			HashSet<String> duplicate = new HashSet<String>();
			for (String[][] previousNEs : previousNELs) {
				if (previousNEs != null && previousNEs[i] != null) {
					for (int j = 0; j < previousNEs[i].length; j++) {
						if (!duplicate
								.contains(previousNEs[i][j].toLowerCase())) {
							duplicate.add(previousNEs[i][j].toLowerCase());
						}
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

	private static String candidateNormalization(String ne, String answerType) {
		String[] segment = ne.split(",");
		String normalizedStr = "";
		for (int i = 0; i < segment.length; i++) {
			if (!segment[i].equals("") && i != segment.length - 1) {
				normalizedStr += segment[i].trim() + ", ";
			} else {
				normalizedStr += segment[i].trim();
			}
		}

		CandidateExtractorBase.getNEMatchOntology(ne, answerType);

		return normalizedStr.trim();
	}
}
