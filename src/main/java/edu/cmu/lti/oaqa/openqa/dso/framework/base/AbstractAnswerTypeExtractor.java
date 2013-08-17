package edu.cmu.lti.oaqa.openqa.dso.framework.base;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.ecd.log.AbstractLoggedComponent;
import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import edu.cmu.lti.oaqa.openqa.dso.framework.DSOLogEntry;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.AnswerTypeJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewType;

public abstract class AbstractAnswerTypeExtractor extends
		AbstractLoggedComponent {

	public abstract void initialize();

	public abstract String extractAnswerTypes(String question);

	@Override
	public final void process(JCas jcas) throws AnalysisEngineProcessException {
		super.process(jcas);
		try {
			// prepare input
			InputElement input = ((InputElement) BaseJCasHelper.getAnnotation(
					jcas, InputElement.type));
			String questionText = input.getQuestion();

			// do task
			String answerType = extractAnswerTypes(questionText);
			log("TYPE_DETECTED: " + answerType);

			// save output
			AnswerTypeJCasManipulator.storeAnswerType(
					ViewManager.getView(jcas, ViewType.ANS_TYPE), answerType);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	protected final void log(String message) {
		super.log(DSOLogEntry.ANS_TYPE, message);
	}

}
