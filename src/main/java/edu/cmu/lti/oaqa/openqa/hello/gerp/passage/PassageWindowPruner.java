package edu.cmu.lti.oaqa.openqa.hello.gerp.passage;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import edu.cmu.lti.oaqa.cse.basephase.gerp.pruner.AbstractPassagePruner;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;

public class PassageWindowPruner extends AbstractPassagePruner {

	@Override
	protected void prunePassages(String question, List<Keyterm> keyterms,
			List<RetrievalResult> documents, List<PassageCandidate> passages)
			throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub

	}

}
