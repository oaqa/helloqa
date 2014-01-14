package edu.cmu.lti.oaqa.openqa.dso.scorer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import edu.cmu.lti.oaqa.openqa.dso.extractor.CandidateExtractorBase;

public class AnswerCandidateScorer {

	public static List<AnswerCandidate> getAnswerCandidates(
			SupportingEvidenceArg arg, String candidateType, String[][] nes, int rank) {

		List<AnswerCandidate> candidates = new ArrayList<AnswerCandidate>();
		if(nes==null){
			return candidates;
		}
		
		for (int i = 0; i < nes.length; i++) {
			if (nes[i] != null && nes[i].length > 0) {
				// update arg
				arg.updateSupportingEvidenceArg(i, nes);

				// score nes
				Score[] evidenceScores = CompositeAnswerScorer
						.getAnswerValidationScore(arg);

				// add NEs to candidates
				for (int j=0;j< nes[i].length;j++) {
					if(nes[i][j] ==null){
						continue;
					}

					AnswerCandidate candidate = new AnswerCandidate(candidateNormalization(nes[i][j], arg.getAnswerType()),
							new ArrayList<RetrievalResult>());
					candidate.setScore(evidenceScores[j].getScore());
					candidates.add(candidate);
					candidate.setKeytermDistances(evidenceScores[j].getDistInfo());
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
