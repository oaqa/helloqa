package edu.cmu.lti.oaqa.openqa.dso.framework.eval;

import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.Resource_ImplBase;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.framework.eval.Key;
import edu.cmu.lti.oaqa.framework.eval.retrieval.EvaluationHelper;

public class DSOEvalAggregator extends Resource_ImplBase implements
		EvaluationAggregator<String> {

	private DSOEvalPersistenceProvider persistence;

	public static Pattern CompileGSPattern(String gsStr) {
		return Pattern.compile("\\b" + gsStr + "\\b", Pattern.CASE_INSENSITIVE);
	}

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier,
			Map<String, Object> tuples) throws ResourceInitializationException {
		String pp = (String) tuples.get("persistence-provider");
		if (pp == null) {
			throw new ResourceInitializationException(
					new IllegalArgumentException(
							"Must provide a parameter of type <persistence-provider>"));
		}
		this.persistence = BaseExperimentBuilder.loadProvider(pp,
				DSOEvalPersistenceProvider.class);
		return true;
	}

	@Override
	public void update(Key key, String sequenceId, List<String> docs,
			List<String> answ, List<String> gs, Ordering<String> ordering,
			Function<String, String> toIdString)
			throws AnalysisEngineProcessException {
		DSOMeasureCounts cnt = count(docs, answ, gs, ordering, toIdString);
		try {
			persistence.deleteAggrEval(key, sequenceId);
			persistence.insertPartialCounts(key, sequenceId, cnt);
		} catch (SQLException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	private DSOMeasureCounts count(List<String> passages, List<String> answ,
			List<String> gs, Ordering<String> ordering,
			Function<String, String> toIdString) {
		EvaluationHelper.getStringSet(gs, toIdString);

		int pos = 1;
		float reciprocalRank = 0;
		float accuracy = 0;
		float ansRecall = 0;
		float passageRecall = 0;

		for (String ans_pattern : gs) {
			Pattern pat = CompileGSPattern(ans_pattern);
			pos = 1;
			for (String oneAns : answ) {
				Matcher m = pat.matcher(oneAns);
				if (m.matches()) {
					if (pos == 1)
						accuracy = 1;
					reciprocalRank = Math.max(reciprocalRank, 1 / (float) pos);
					ansRecall = 1;
					System.out.println("Match: " + oneAns + " for " + ans_pattern
							+ " ReciprocalRank: " + reciprocalRank);
				}
				++pos;
			}
		}
		
		for(String passage:passages){
			for (String ans_pattern : gs) {
				Pattern pattern = Pattern.compile(ans_pattern);
				Matcher matcher = pattern.matcher(passage);
				if (matcher.find()) {
					passageRecall=1;
				    System.out.println(matcher.group(0));
				    break;
				} 
			}
		}

		System.out.println("Reciprocal rank: " + reciprocalRank);
		System.out.println("Accuracy: " + accuracy);
		System.out.println("Answer recall: " + ansRecall);
		System.out.println("Passage recall: " + passageRecall);

		return new DSOMeasureCounts(reciprocalRank, accuracy, ansRecall,
				passageRecall, 1);
	}

}