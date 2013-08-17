package edu.cmu.lti.oaqa.openqa.dso.framework.base;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.ecd.log.AbstractLoggedComponent;
import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.KeytermJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewType;

public abstract class AbstractKeytermExtractor extends AbstractLoggedComponent {

	public abstract void initialize();

	public abstract List<String> extractKeyterms(String question);

	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			InputElement input = ((InputElement) BaseJCasHelper.getAnnotation(
					jcas, InputElement.type));
			String questionText = input.getQuestion();
			
			List<String> keyterms = extractKeyterms(questionText);
			KeytermJCasManipulator.storeKeyterms(ViewManager.getView(jcas, ViewType.KEYTERM), keyterms);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
