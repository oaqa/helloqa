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
import edu.cmu.lti.oaqa.framework.eval.passage.PassageMAPCounts;
import edu.cmu.lti.oaqa.framework.eval.passage.PassageMAPEvalPersistenceProvider;
import edu.cmu.lti.oaqa.framework.eval.retrieval.EvaluationAggregator;
import edu.cmu.lti.oaqa.framework.eval.retrieval.EvaluationHelper;

/**
* The class to compute the mean reciprocal rank and the accuracy.
*
* @author Leonid Boytsov
* TODO: use a separate persistency provider, not the one that
* was designed to store MAP values.
*/
public class AnswerMRREvalAggregator extends Resource_ImplBase implements EvaluationAggregator<String> {

  private PassageMAPEvalPersistenceProvider persistence;

  public static Pattern CompileGSPattern(String gsStr) {
      return Pattern.compile("\\b" + gsStr + "\\b", Pattern.CASE_INSENSITIVE);
  }

  @Override
  public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> tuples)
          throws ResourceInitializationException {
    String pp = (String) tuples.get("persistence-provider");
    if (pp == null) {
      throw new ResourceInitializationException(new IllegalArgumentException(
              "Must provide a parameter of type <persistence-provider>"));
    }
    this.persistence = BaseExperimentBuilder.loadProvider(pp,
            PassageMAPEvalPersistenceProvider.class);
    return true;
  }
  
  @Override
  public void update(Key key, String sequenceId, List<String> answ, List<String> gs,
                     Ordering<String> ordering, Function<String, String> toIdString) throws AnalysisEngineProcessException {
    PassageMAPCounts cnt = count(answ, gs, ordering, toIdString);
    try {
      persistence.deletePassageAggrEval(key, sequenceId);
      persistence.insertPartialCounts(key, sequenceId, cnt);
    } catch (SQLException e) {
      throw new AnalysisEngineProcessException(e);
    }
  }

  private PassageMAPCounts count(List<String> answ, List<String> gs,
                                 Ordering<String> ordering, Function<String, String> toIdString) {
    EvaluationHelper.getStringSet(gs, toIdString);

    int pos = 1;
    float ReciprocalRank = 0;
    float Accuracy = 0;
    float BinaryRecall = 0;

    for (String AnswPat : gs) {
      Pattern pat = CompileGSPattern(AnswPat);
      pos = 1;
      for (String oneAns : answ) {
        Matcher m = pat.matcher(oneAns);
        if (m.matches()) {
          if (pos == 1)
            Accuracy = 1;
          ReciprocalRank = Math.max(ReciprocalRank, 1 / (float) pos);
          BinaryRecall = 1;
          System.out.println("Match: " + oneAns + " for " + AnswPat + " ReciprocalRank: "
                  + ReciprocalRank);
        }
        ++pos;
      }
    }

    System.out.println("Reciprocal rank: " + ReciprocalRank);
    System.out.println("Accuracy: " + Accuracy);
    System.out.println("Binary recall: " + BinaryRecall);

    return new PassageMAPCounts(ReciprocalRank, Accuracy, BinaryRecall, 1);
  }

}