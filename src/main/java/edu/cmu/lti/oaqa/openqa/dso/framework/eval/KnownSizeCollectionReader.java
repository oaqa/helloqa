package edu.cmu.lti.oaqa.openqa.dso.framework.eval;

import org.apache.uima.collection.CollectionReader;

public interface KnownSizeCollectionReader extends CollectionReader {
  public int size();
}
