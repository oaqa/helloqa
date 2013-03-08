package edu.cmu.lti.oaqa.openqa.hello.gerp.passage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;
import edu.cmu.lti.oaqa.cse.basephase.gerp.generator.AbstractPassageGenerator;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.hello.passage.KeytermWindowScorerSum;
import edu.cmu.lti.oaqa.openqa.hello.passage.PassageCandidateFinder;
import edu.cmu.lti.oaqa.openqa.hello.util.SolrProvider;

public class PassageKeytermWindowGenerator extends AbstractPassageGenerator {

	private SolrWrapper wrapper;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier,
			Map<String, Object> parameters) {

		return true;
	}

	@Override
	protected List<PassageCandidate> generatePassages(String question,
			List<Keyterm> keyterms, List<RetrievalResult> documents)
			throws AnalysisEngineProcessException {
		List<PassageCandidate> result = new ArrayList<PassageCandidate>();
		for (RetrievalResult document : documents) {
			System.out.println("RetrievalResult: " + document.toString());
			String id = document.getDocID();
			String text = SolrProvider.getDocText(id);
			PassageCandidateFinder finder = new PassageCandidateFinder(id,
					text, new KeytermWindowScorerSum());
			System.out.println(text);
			result.addAll(finder.extractPassages(keyterms));
		}
		return result;

	}
}
