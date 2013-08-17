package edu.cmu.lti.oaqa.openqa.dso.framework.base;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.ecd.log.AbstractLoggedComponent;
import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.AnswerTypeJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.AnsJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.KeytermJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewType;

public abstract class AbstractAnswerGenerator extends AbstractLoggedComponent {

	public abstract void initialize();

	public abstract List<AnswerCandidate> generateFinalAnswers(
			String answerType, List<String> keyterms,
			List<AnswerCandidate> answerCandidates);

	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			String answerType = AnswerTypeJCasManipulator
					.loadAnswerType(ViewManager
							.getView(jcas, ViewType.ANS_TYPE));

			List<AnswerCandidate> answerCandidates = AnsJCasManipulator
					.loadAnswerCandidates(ViewManager
							.getView(jcas, ViewType.IE));
			List<String> keyterms = KeytermJCasManipulator
					.loadKeyterms(ViewManager.getView(jcas, ViewType.KEYTERM));

			List<AnswerCandidate> finalAnswers = generateFinalAnswers(
					answerType, keyterms, answerCandidates);
			AnsJCasManipulator.storeCandidates(
					ViewManager.getView(jcas, ViewType.ANS), finalAnswers);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
