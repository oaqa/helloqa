package edu.cmu.lti.oaqa.openqa.dso.framework.eval;

import java.sql.SQLException;

import org.apache.uima.resource.Resource;

import com.google.common.collect.Multimap;

import edu.cmu.lti.oaqa.framework.eval.ExperimentKey;
import edu.cmu.lti.oaqa.framework.eval.Key;

public interface DSOMeasureEvalPersistenceProviderInterface extends Resource {

  void deleteAggrEval(Key key, String sequenceId) throws SQLException;

  void insertPartialCounts(Key key, String sequenceId, DSOMeasureCounts cnt) throws SQLException;

  Multimap<Key, DSOMeasureCounts> retrievePartialCounts(ExperimentKey experiment);

  void deleteMeasureEval(ExperimentKey experiment);

  void insertMeasureEval(Key key, String eName, DSOEvaluationData eval) throws SQLException;

}
