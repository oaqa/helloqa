package edu.cmu.lti.oaqa.openqa.dso.extractor;

import java.util.List;

public class CandidateExtractorByMainAnswerType extends
		CandidateExtractorByAnswerType {

	public CandidateExtractorByMainAnswerType(String answerType,
			String[] sentences) {
		super(answerType, sentences);

		// get IDs of taggers for most specific NE type that can be tagged
		List<int[]> nePatterns = getNEType(answerType);

		// extract NEs of that type
		if (answerType.toLowerCase().contains("NEYES/NO".toLowerCase())) {

		} else {
			tokens = getTokens(sentences);
		}
		if (nePatterns.size() > 0) {
			nes = extractCandidatesUsingPatterns(
					nePatterns.get(nePatterns.size() - 1), sentences);
		}

		if (answerType.toLowerCase().contains(
				"NETerroristOrganization".toLowerCase())) {
			nes = getMatchOntology(sentences, nes, TerroristOrgOntologyList);
		} else if (answerType.toLowerCase().contains(
				"NEnumber".toLowerCase())) {
			nes = getMatchOntology(sentences, nes, NumberOntologyList);
		}else if (answerType.toLowerCase().contains(
				"NEterrorist".toLowerCase())) {
			nes = getMatchOntology(sentences, nes, TerroristOntologyList);
		}
	}

}
