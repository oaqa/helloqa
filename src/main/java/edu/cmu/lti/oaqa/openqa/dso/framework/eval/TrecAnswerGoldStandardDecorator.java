package edu.cmu.lti.oaqa.openqa.dso.framework.eval;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.ecd.BaseExperimentBuilder;
import edu.cmu.lti.oaqa.framework.CasUtils;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewType;

/**
* A gold standard decorator: read the TREC answers and save them in a CAS.
*
* @author Di Wang
*
*/

public class TrecAnswerGoldStandardDecorator extends JCasAnnotator_ImplBase {

  private GoldStandardPersistenceProvider persistence;

  @Override
  public void initialize(UimaContext c) throws ResourceInitializationException {
    String pp = (String) c.getConfigParameterValue("persistence-provider");
    if (pp == null) {
      throw new ResourceInitializationException(new IllegalArgumentException(
              "Must provide a parameter of type <persistence-provider>"));
    }
    this.persistence = BaseExperimentBuilder
            .loadProvider(pp, GoldStandardPersistenceProvider.class);
  }

  /**
* Retrieves the gold standard data from the database for the retrieval task. And stores FSArray
* of Search on the DOCUMENT_GS view of the JCas.
*/
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    
    try {
      final InputElement input = (InputElement) CasUtils.getFirst(aJCas,
              InputElement.class.getName());
      final JCas gsView = ViewManager.getOrCreateView(aJCas, ViewType.ANS_GS);
      final String dataset = input.getDataset();
      final String sequenceId = input.getSequenceId();
      
      persistence.populateRetrievalGS(dataset, sequenceId, gsView);
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }
 

}