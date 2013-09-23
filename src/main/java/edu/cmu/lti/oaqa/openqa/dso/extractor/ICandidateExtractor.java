package edu.cmu.lti.oaqa.openqa.dso.extractor;

import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;

public interface ICandidateExtractor {
	public String getTypeName();
	public void initialize();
	public String[][] generateNEs();
	public List<AnswerCandidate> getAnswerCandidates(SupportingEvidenceArg arg);
}
