package edu.cmu.lti.oaqa.openqa.hello.gerp.passage;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceSpecifier;

import edu.cmu.lti.oaqa.cse.basephase.gerp.evidencer.AbstractPassageEvidencer;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.hello.passage.KeytermWindowScorer;
import edu.cmu.lti.oaqa.openqa.hello.util.SolrProvider;

public class PassageKeytermWindowEvidencer extends AbstractPassageEvidencer {

	private KeytermWindowScorer scorer;

	public boolean initialize(ResourceSpecifier rs, Map<String, Object> params) {
		Class classDefinition =null;
		try {
			classDefinition = Class.forName((String) params.get("scorer"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			scorer = (KeytermWindowScorer) classDefinition.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	protected void evidencePassages(String question, List<Keyterm> keyterms,
			List<RetrievalResult> documents, List<PassageCandidate> passages)
			throws AnalysisEngineProcessException {
		// Put in Map so can aggregate passages for a given documentID
		Map<String, List<PassageCandidate>> mapDoc = new HashMap<String, List<PassageCandidate>>();

		for (PassageCandidate p : passages) {
			if (mapDoc.containsKey(p.getDocID()))
				mapDoc.get(p.getDocID()).add(p);
			else {
				List<PassageCandidate> passageList = new ArrayList<PassageCandidate>();
				passageList.add(p);
				mapDoc.put(p.getDocID(), passageList);
			}
		}

		for (RetrievalResult document : documents) {
			String text = SolrProvider.getDocText(document.getDocID());
			DocumentPassageAnalyzer analyzer = new DocumentPassageAnalyzer(
					text, keyterms);
			analyzer.computeMatches(mapDoc.get(document.getDocID()), scorer);
		}

	}
}
