package edu.cmu.lti.oaqa.openqa.dso.framework.eval;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.Resource;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import edu.cmu.lti.oaqa.framework.eval.Key;

public interface EvaluationAggregator<T> extends Resource {

  void update(Key key, String sequenceId, List<T> docs, List<T> ans, List<T> goldStandard, Ordering<T> ordering, Function<T, String> toIdString) throws AnalysisEngineProcessException;

}
  