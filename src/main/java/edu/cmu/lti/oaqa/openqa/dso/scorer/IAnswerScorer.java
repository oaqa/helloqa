package edu.cmu.lti.oaqa.openqa.dso.scorer;

import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;

public interface IAnswerScorer {
	Score[] getScore(SupportingEvidenceArg arg);
}
