package edu.cmu.lti.oaqa.openqa.dso.xmiretriever;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.CasCopier;
import org.apache.uima.util.CasPool;
import org.apache.uima.util.XMLInputSource;
import org.gale.DocID;
import org.gale.EntityMention;

public class OnDemandAnnotator {

	private static OnDemandAnnotator singleton = null;

	public static OnDemandAnnotator getAnnotator() {
		if (singleton == null) {
			singleton = new OnDemandAnnotator();
			try {
				singleton.initialize();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return singleton;
	}

	private AnalysisEngine analysisEngine;
	private CasPool casPool;

	public void initialize() throws ResourceInitializationException {
		try {
			URL resource = getClass()
					.getResource(
							"/edu/cmu/lti/oaqa/experimental_impl/xmiretriever/OnDemandAnnotationAggregate.xml");
			XMLInputSource in = new XMLInputSource(resource);
			ResourceSpecifier specifier = UIMAFramework.getXMLParser()
					.parseResourceSpecifier(in);
			analysisEngine = UIMAFramework.produceAnalysisEngine(specifier, 1,
					0);
			casPool = new CasPool(4, analysisEngine);
			
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	// For question annotations. Not saving
	public List<EntityMention> annotateQuestion(String question) throws AnalysisEngineProcessException {
		
		String setDocId = "question-annotation";
		
		CAS cas = casPool.getCas();
		
		try {
			
			JCas jcas = cas.getJCas();
			jcas.setDocumentText(question);
			
			DocID docID = new DocID(jcas);
			docID.setValue(setDocId);
			docID.addToIndexes();

			SourceDocumentInformation sdi = new SourceDocumentInformation(jcas);
			sdi.setBegin(0);
			sdi.setEnd(jcas.getDocumentText().length());
			sdi.addToIndexes();

			analysisEngine.process(cas);
			
			//Create new CAS and return
			CAS newCas = analysisEngine.newCAS(); 
			CasCopier.copyCas(cas, newCas, true);

			casPool.releaseCas(cas);
			
			FSIterator<Annotation> entityIterator = newCas.getJCas().getAnnotationIndex(
					EntityMention.type).iterator();
			
			List<EntityMention> listEntities = new ArrayList<EntityMention>();
			
			while(entityIterator.hasNext()) {
				
				EntityMention em = (EntityMention) entityIterator.next();
				listEntities.add(em);
				
			}
			
			return listEntities;

		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
		
	}

	public void annotatedAndSave(String text, String set, String docId)
			throws AnalysisEngineProcessException {
		if (XmiCASRetriever.hasXmiResource(set, docId))
			return;
		try {
			CAS cas = casPool.getCas(0);
			JCas jcas = cas.getJCas();
			jcas.setDocumentText(text);

			String setDocId = set + "-" + docId;
			String path = "xmirepo/" + set + "/xmi/" + setDocId + ".xmi";

			DocID docID = new DocID(jcas);
			docID.setValue(setDocId);
			docID.addToIndexes();

			SourceDocumentInformation sdi = new SourceDocumentInformation(jcas);
			sdi.setBegin(0);
			sdi.setEnd(jcas.getDocumentText().length());
			sdi.setUri("file:./" + path);
			sdi.addToIndexes();

			System.err.println("Started processing: " + set + "-" + docId);

			analysisEngine.process(cas);

			System.err.println("Finished processing: " + set + "-" + docId);

			File f = new File("xmirepo/" + set + "/xmi/");
			if (!f.exists())
				f.mkdirs();

			XmiCasSerializer.serialize(cas, new FileOutputStream(path));

			casPool.releaseCas(cas);

		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	public void destroy() {
		analysisEngine.destroy();
	}

	public static void main(String[] args)
			throws CASException, ResourceProcessException {

		OnDemandAnnotator a = OnDemandAnnotator.getAnnotator();
		List<EntityMention> listAnnotations = a.annotateQuestion("When did 2007 Baghlan sugar factory bombing happen?");
		a.destroy();
	

		for(EntityMention em: listAnnotations) {
			
			System.out.println(em.getEntityType() + " - " + em.getCoveredText());
			
		}
		
	}
}
