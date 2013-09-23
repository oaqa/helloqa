package edu.cmu.lti.oaqa.openqa.dso.extractor;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.answer.AnswerCandidateScorer;
import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import edu.cmu.lti.oaqa.openqa.dso.util.ClassUtil;

public class CompositeCandidateExtractor implements ICandidateExtractor {

	private List<ICandidateExtractor> childCandidateExtractors = new ArrayList<ICandidateExtractor>();
	
	private String[] classNames;
	
	public CompositeCandidateExtractor(SupportingEvidenceArg arg){
		initialize();
		this.classNames=arg.getClassNames().split(",");
		for(String className:this.classNames){
			childCandidateExtractors.add(ClassUtil.factory(className, arg));
		}
	}

	@Override
	public void initialize() {
		
	}

	@Override
	public String[][] generateNEs() {
		return null;
	}

	@Override
	public List<AnswerCandidate> getAnswerCandidates(SupportingEvidenceArg arg) {
		List<AnswerCandidate> candidates = new ArrayList<AnswerCandidate>();
		List<String[][]> previousNELs = new ArrayList<String[][]>();
		for (ICandidateExtractor extractor : childCandidateExtractors) {
			String[][] nes = extractor.generateNEs();
			nes = AnswerCandidateScorer.refineNEs(previousNELs, nes);
			candidates.addAll(AnswerCandidateScorer.getAnswerCandidates(arg,
					extractor.getTypeName(), nes));
			previousNELs.add(nes);
		}
		return candidates;
	}

	@Override
	public String getTypeName() {
		String componentID = "comp";
		for (ICandidateExtractor extractor : childCandidateExtractors) {
			componentID += "-" + extractor.getTypeName();
		}
		return componentID;
	}

}
