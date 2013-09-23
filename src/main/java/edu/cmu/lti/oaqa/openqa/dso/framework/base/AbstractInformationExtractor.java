package edu.cmu.lti.oaqa.openqa.dso.framework.base;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.ecd.log.AbstractLoggedComponent;
import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.framework.DSOLogEntry;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.AnswerTypeJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.DocumentJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.AnsJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.KeytermJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewType;

public abstract class AbstractInformationExtractor extends AbstractLoggedComponent {
	public abstract void initialize();

	public abstract List<AnswerCandidate> extractAnswerCandidates(
			String questionText, String answerType, List<String> keyterms,
			List<String> keyphrases, List<RetrievalResult> documents);

	@Override
	public final void process(JCas jcas) throws AnalysisEngineProcessException {
		super.process(jcas);
		try {
			// prepare input
			InputElement input = ((InputElement) BaseJCasHelper.getAnnotation(
					jcas, InputElement.type));
			String questionText = input.getQuestion();

			String answerType = AnswerTypeJCasManipulator.loadAnswerType(
					ViewManager.getView(jcas, ViewType.ANS_TYPE));

	        List<String> keyterms = KeytermJCasManipulator
					.loadKeyterms(ViewManager.getView(jcas, ViewType.KEYTERM));
			List<String> keyphrases = KeytermJCasManipulator
					.loadKeyphrases(ViewManager.getView(jcas, ViewType.KEYTERM));
	        List<RetrievalResult> documents = DocumentJCasManipulator.loadDocuments(ViewManager.getView(jcas, ViewType.PASSAGE));
	        List<AnswerCandidate> ansCandidates = extractAnswerCandidates(
	      		  questionText, answerType, keyterms, keyphrases, documents);
	        
	        AnsJCasManipulator.storeCandidates(ViewManager.getView(jcas, ViewType.IE), ansCandidates);

		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	protected final void log(String message) {
		super.log(DSOLogEntry.IE, message);
	}

}
