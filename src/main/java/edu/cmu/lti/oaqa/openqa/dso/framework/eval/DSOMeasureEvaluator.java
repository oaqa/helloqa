package edu.cmu.lti.oaqa.openqa.dso.framework.eval;

/*
 * Copyright 2012 Carnegie Mellon University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.Resource_ImplBase;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.framework.eval.ExperimentKey;
import edu.cmu.lti.oaqa.framework.eval.ExperimentListener;
import edu.cmu.lti.oaqa.framework.eval.Key;

public class DSOMeasureEvaluator extends Resource_ImplBase implements
		ExperimentListener {

	private DSOEvalPersistenceProvider persistence;

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

	private final Map<Key, DSOMeasureCounts> countMap = Maps.newHashMap();

	@Override
	public void process(ExperimentKey experiment)
			throws AnalysisEngineProcessException {
		persistence.deleteMeasureEval(experiment);
		Multimap<Key, DSOMeasureCounts> counts = persistence
				.retrievePartialCounts(experiment);
		for (Map.Entry<Key, DSOMeasureCounts> me : counts.entries()) {
			update(me.getKey(), me.getValue());
		}
		doEvaluate();
	}

	public void doEvaluate() throws AnalysisEngineProcessException {
		for (Map.Entry<Key, DSOMeasureCounts> me : countMap.entrySet()) {
			Key key = me.getKey();
			String eName = getClass().getSimpleName();
			DSOEvaluationData eval = evaluate(me.getValue());
			try {
				persistence.insertMeasureEval(key, eName, eval);
			} catch (Exception e) {
				e.printStackTrace();
				throw new AnalysisEngineProcessException(e);
			}
		}
	}

	private DSOEvaluationData evaluate(DSOMeasureCounts counts) {
		float ans_mrr = counts.getAnsMRR() / counts.getCount();
		System.out.println("MRR:++++++++++++++: " + counts.getAnsMRR());
		float ans_acc = counts.getAnsAcc() / counts.getCount();
		System.out.println("Acc:++++++++++++++: " + counts.getAnsAcc());
		float ans_recall = counts.getsAnsRecall() / counts.getCount();
		System.out.println("Recall:++++++++++++++: " + counts.getsAnsRecall());
		float passage_recall = counts.getsPassageRecall() / counts.getCount();
		System.out.println("Recall:++++++++++++++: "
				+ counts.getsPassageRecall());

		return new DSOEvaluationData(ans_mrr, ans_acc, ans_recall,
				passage_recall, counts.getCount());
	}

	private void update(Key key, DSOMeasureCounts cnt) {
		DSOMeasureCounts globals = countMap.get(key);
		if (globals == null) {
			globals = new DSOMeasureCounts();
			countMap.put(key, globals);
		}
		globals.update(cnt);
	}

}