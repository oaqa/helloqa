package edu.cmu.lti.oaqa.openqa.dso.xmiretriever;

import opennlp.tools.sentdetect.SentenceDetector;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

public class OAQACompatSentDetectAnnotator extends JCasAnnotator_ImplBase{

	private static final String PARAM_SENTDETECT_MODEL = "SentDetectModel";
	
	private String mSentDetectModel = "";
	
	private static final String PARAM_CONTAINER = "Container";
	
	private String mContainer = "";
	
	private static final String PARAM_SENTENCE_TYPE = "SentenceAnnotationType";
	
	private String mSentenceType = "";
	
	private SentenceDetector sentDetector = null;
	
	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		
		mSentDetectModel = aContext.getConfigParameterValue(PARAM_SENTDETECT_MODEL) == null ? "" : (String) aContext.getConfigParameterValue(PARAM_SENTDETECT_MODEL);
		mContainer = aContext.getConfigParameterValue(PARAM_CONTAINER) == null ? "" : (String) aContext.getConfigParameterValue(PARAM_CONTAINER);
		mSentenceType = aContext.getConfigParameterValue(PARAM_SENTENCE_TYPE) == null ? "" : (String) aContext.getConfigParameterValue(PARAM_SENTENCE_TYPE);
		
		try{
			sentDetector = new opennlp.tools.lang.english.SentenceDetector(mSentDetectModel);	
		}catch(Exception e){
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Type container_t = jcas.getTypeSystem().getType(mContainer);
		Type sentence_t = jcas.getTypeSystem().getType(mSentenceType);
		
		FSIterator<Annotation> container_itr = jcas.getAnnotationIndex(container_t).iterator();
		while(container_itr.hasNext()){
			Annotation container = container_itr.next();
			String text = container.getCoveredText();
			
			int[] pos = sentDetector.sentPosDetect(text);
			
			int start = 0;
			
			for(int i = 0; i < pos.length; i++){
				int begin = start;
				int end = pos[i];
				AnnotationFS sentenceAnnotation = jcas.getCas().createAnnotation(sentence_t, container.getBegin() + begin, container.getBegin() + end);
				jcas.addFsToIndexes(sentenceAnnotation);
			}
		}
		
	}

}
