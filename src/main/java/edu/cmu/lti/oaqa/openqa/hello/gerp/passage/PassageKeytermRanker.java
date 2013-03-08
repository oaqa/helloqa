package edu.cmu.lti.oaqa.openqa.hello.gerp.passage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceSpecifier;

import edu.cmu.lti.oaqa.cse.basephase.gerp.ranker.AbstractPassageRanker;
import edu.cmu.lti.oaqa.framework.data.Keyterm;
import edu.cmu.lti.oaqa.framework.data.PassageCandidate;
import edu.cmu.lti.oaqa.framework.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.hello.passage.KeytermWindowScorer;

public class PassageKeytermRanker extends AbstractPassageRanker {

	private double[] weights;

	public boolean initialize(ResourceSpecifier rs, Map<String, Object> params) {
		weights = new double[]{0.4d,0.2d};
		return true;
	}

	@Override
	protected void rankPassages(String question, List<Keyterm> keyterms,
			List<RetrievalResult> documents, List<PassageCandidate> passages)
			throws AnalysisEngineProcessException {
		// Collections.sort(passages, new PassageCandidateScoreComparator());
		for (PassageCandidate p : passages) {
			List<Float> probabilities = p.getProbabilities();
			p.setProbablity(merge(probabilities));
		}

	}

	public float merge(List<Float> probabilities) {
		float sum = 0;
		float size = probabilities.size();
		for (int i = 0; i < size; i++) {
			sum += weights[i] * probabilities.get(i);
		}

		return sum;

	}

	public class PassageCandidateScoreComparator implements
			Comparator<PassageCandidate> {
		@Override
		public int compare(PassageCandidate p1, PassageCandidate p2) {
			if (p1.getProbability() > p2.getProbability())
				return 1;
			if (p1.getProbability() < p2.getProbability())
				return -1;
			return 0;
		}

	}

}
