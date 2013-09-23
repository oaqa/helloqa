package edu.cmu.lti.oaqa.openqa.dso.extractor;

import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;

public class LeafEphyraMainType extends CandidateExtractorByAnswerType {

	public LeafEphyraMainType(SupportingEvidenceArg arg) {
		super(arg);
		
		// get IDs of taggers for most specific NE type that can be tagged
		List<int[]> nePatterns = getNEType(arg.getAnswerType());

		// extract NEs of that type
		if (arg.getAnswerType().toLowerCase()
				.contains("NEYES/NO".toLowerCase())) {

		} else {
			tokens = getTokens(arg.getSentences());
		}
		if (nePatterns.size() > 0) {
			nes = extractCandidatesUsingPatterns(
					nePatterns.get(nePatterns.size() - 1), arg.getSentences());
		}

		if (arg.getAnswerType().toLowerCase()
				.contains("NETerroristOrganization".toLowerCase())) {
			nes = getMatchOntology(arg.getSentences(), nes,
					TerroristOrgOntologyList);
		} else if (arg.getAnswerType().toLowerCase()
				.contains("NEnumber".toLowerCase())) {
			nes = getMatchOntology(arg.getSentences(), nes, NumberOntologyList);
		} else if (arg.getAnswerType().toLowerCase()
				.contains("NEterrorist".toLowerCase())) {
			nes = getMatchOntology(arg.getSentences(), nes,
					TerroristOntologyList);
		}
	}

	@Override
	public String getTypeName() {
		return "ephyra_main_type";
	}

}
