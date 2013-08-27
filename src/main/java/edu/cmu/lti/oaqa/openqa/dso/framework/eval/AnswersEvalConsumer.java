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

import java.util.List;
import java.util.ArrayList;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.CasConsumer_ImplBase;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.ecd.phase.ProcessingStepUtils;
import edu.cmu.lti.oaqa.ecd.phase.Trace;
import edu.cmu.lti.oaqa.framework.eval.Key;
import edu.cmu.lti.oaqa.framework.eval.retrieval.EvaluationAggregator;
import edu.cmu.lti.oaqa.framework.types.ExperimentUUID;
import edu.cmu.lti.oaqa.framework.types.ProcessingStep;
import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.AnsGSJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.AnsJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewType;

public class AnswersEvalConsumer extends CasConsumer_ImplBase {
	private final class AnswerToIdString implements Function<String, String> {
		@Override
		public String apply(String answ) {
			// Answers are very small, so the text of an answer
			// is a unique identifier
			return answ;
		}
	}

	private final class AnswerOrdering extends Ordering<String> {
		@Override
		public int compare(String left, String right) {
			return left.compareTo(right);
		}
	}

	private final Ordering<String> ordering = new AnswerOrdering();

	private final Function<String, String> toIdString = new AnswerToIdString();

	@SuppressWarnings("rawtypes")
	private List<EvaluationAggregator> aggregators;

	private int limit;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		try {
			limit = (Integer) context.getConfigParameterValue("limit");
		} catch (NullPointerException e) {
			limit = Integer.MAX_VALUE;
		}
		Object aggregatorNames = (Object) context
				.getConfigParameterValue("aggregators");
		if (aggregatorNames != null) {
			this.aggregators = BaseExperimentBuilder.createResourceList(
					aggregatorNames, EvaluationAggregator.class);
		}
	}

	@Override
	public void process(CAS aCAS) throws AnalysisEngineProcessException {
		try {
			JCas jcas = aCAS.getJCas();
			ExperimentUUID experiment = ProcessingStepUtils
					.getCurrentExperiment(jcas);
			AnnotationIndex<Annotation> steps = jcas
					.getAnnotationIndex(ProcessingStep.type);
			Trace trace = ProcessingStepUtils.getTrace(steps);
			JCas candidateView = ViewManager.getView(jcas, ViewType.ANS);
			JCas gsView = ViewManager.getView(jcas, ViewType.ANS_GS);
			if (gsView != null) {
				ArrayList<String> gs = AnsGSJCasManipulator.loadAnsGS(gsView);
				List<AnswerCandidate> _answ = (candidateView != null) ? AnsJCasManipulator
						.loadAnswerCandidates(candidateView)
						: new ArrayList<AnswerCandidate>();
				List<String> answ = new ArrayList<String>();

				for (AnswerCandidate w : _answ) {
					answ.add(w.getText());
				}

				answ = answ.subList(0, Math.min(answ.size(), limit));
				String sequenceId = ProcessingStepUtils.getSequenceId(jcas);
				for (EvaluationAggregator<String> aggregator : aggregators) {
					Key key = new Key(experiment.getUuid(), trace,
							experiment.getStageId());
					aggregator.update(key, sequenceId, answ, gs, ordering,
							toIdString);
				}
			}
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}