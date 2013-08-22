package edu.cmu.lti.oaqa.openqa.dso.framework.base;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.ecd.log.AbstractLoggedComponent;
import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.AnswerTypeJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.DocumentJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.KeytermJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewType;

public abstract class AbstractPassageRetrieval extends AbstractLoggedComponent {
	public abstract void initialize();

	public abstract List<RetrievalResult> retrieveDocuments(List<String> keyterms,
			List<String> keyphrases, String question, String answerType);

	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {

			InputElement input = ((InputElement) BaseJCasHelper.getAnnotation(
					jcas, InputElement.type));
			String questionText = input.getQuestion();
			List<String> keyterms = KeytermJCasManipulator
					.loadKeyterms(ViewManager.getView(jcas, ViewType.KEYTERM));
			List<String> keyphrases = KeytermJCasManipulator
					.loadKeyphrases(ViewManager.getView(jcas, ViewType.KEYTERM));
			String answerType = AnswerTypeJCasManipulator
					.loadAnswerType(ViewManager
							.getView(jcas, ViewType.ANS_TYPE));

			List<RetrievalResult> documents = retrieveDocuments(keyterms,
					keyphrases, questionText, answerType);

			DocumentJCasManipulator.storeDocuments(
					ViewManager.getView(jcas, ViewType.PASSAGE), documents);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
