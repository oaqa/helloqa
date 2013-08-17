package edu.cmu.lti.oaqa.openqa.dso.answer;

import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;

public interface IAnswerScorer {
	double[] getScore(SupportingEvidenceArg arg);
}
