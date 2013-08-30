package edu.cmu.lti.oaqa.openqa.dso.framework.eval;

import java.sql.SQLException;

import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.Resource;

public interface GoldStandardPersistenceProvider extends Resource {

  void populateRetrievalGS(String dataset, String sequenceId, JCas docGSView) throws SQLException, Exception;

}