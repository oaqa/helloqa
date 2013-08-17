package edu.cmu.lti.oaqa.openqa.dso.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.gale.EntityMention;
import org.gale.Sentence;

import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;
import edu.cmu.lti.oaqa.openqa.dso.xmiretriever.HashUtil;
import edu.cmu.lti.oaqa.openqa.dso.xmiretriever.OnDemandAnnotator;
import edu.cmu.lti.oaqa.openqa.dso.xmiretriever.XmiCASRetriever;

public class CandidateExtractorByXmi extends CandidateExtractorBase {
	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());
	private static XmiCASRetriever xmiCasRetriever = new XmiCASRetriever();
	private static Map<String, CAS> casCache = new LinkedHashMap<String, CAS>(){
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Entry<String, CAS> arg0) {
			return size() > 100;
		}
		
	};
	
	private static Map<String, String[]> typesMapping = new HashMap<String, String[]>();
	
	private List<String> candidates = new ArrayList<String>();
	private String answerCandidates[][];
	private String rawDocument;
	private String[] sentenceArrays; 
	
	static {
		typesMapping.put("NEdate", new String[]{"DATE:PRONOMINAL"});
		typesMapping.put("NEduration", new String[]{"DURATION:PRONOMINAL"});
		typesMapping.put("NEnumber", new String[]{"CARDINAL:PRONOMINAL"});
		typesMapping.put("NEnumber->NEquantity", new String[]{"CARDINAL:PRONOMINAL"});
		typesMapping.put("NElocation->NEcountry", new String[]{"GEOPOLITICAL_ENTITY->NATION_STATE:NAMED"});
		typesMapping.put("NElocation", new String[]{"GEOPOLITICAL_ENTITY:NAMED", "LOCATION:NAMED", "LOCATION:NOMINAL"});
		typesMapping.put("NEproperName->NEperson", new String[]{"PERSON:NAMED"});
		typesMapping.put("NEterrorist", new String[]{"PERSON:NAMED"});
		typesMapping.put("NEproperName->NEorganization->NETerroristOrganization", new String[]{"ORGANIZATION->TERRORIST:NAMED", "PEOPLE:NAMED"});
		typesMapping.put("NEproperName->NEtype->NEattacktype->NEexplosives", new String[]{"EVENT_ATTACK_BOMB:NOMINAL"});
		typesMapping.put("NEproperName->NEtype->NEattackType", new String[]{"EVENT_ATTACK_BOMB:NOMINAL", "EVENT_ATTACK_KILL:NOMINAL", "EVENT_ATTACK_OTHER:NOMINAL"});
		typesMapping.put("NEtransportation", new String[]{"VEHICLE:PRONOMINAL"});
		typesMapping.put("NEmoney", new String[]{"CARDINAL:PRONOMINAL"});
	}
	
	public CandidateExtractorByXmi(String answerType, RetrievalResult document) {
		
		String answerHierarchy[] = answerType.split("->");
		
		boolean answerTypeInMapping = false;
		
		String[] targetTypes = null;
		
		for(String aType: answerHierarchy){
			if(typesMapping.containsKey(aType)){
				answerTypeInMapping = true;
				targetTypes = typesMapping.get(aType);
				break;
			}
		}
		
		if(!answerTypeInMapping && !typesMapping.containsKey(answerType))
			return;
		
		if(!answerTypeInMapping && typesMapping.containsKey(answerType)){
			targetTypes = typesMapping.get(answerType);
		}
		
		String docID = document.getDocID().toLowerCase();
		
//		System.out.println("Article ID: " + docID);
		
		
		Matcher matcher = Pattern.compile("^(.+)-([0-9_]+?)$").matcher(docID);
		
		if(!matcher.matches()){
			docID = "wikipedia-"+ docID + "_" + HashUtil.getHash(document.getText()); 
//			return;
		}
		
		if(!casCache.containsKey(docID)){
			casCache.put(docID, xmiCasRetriever.retrieveCAS(docID));
		}else{
			//LOGGER.info("Cache Hit for Cas");
		}
		CAS cas = casCache.get(docID);
		
		if(cas == null && docID.startsWith("wikipedia")){
			matcher = Pattern.compile("^(.+)-([0-9a-z_]+?)$").matcher(docID);
			matcher.matches();
			OnDemandAnnotator annotator = OnDemandAnnotator.getAnnotator();
			try {
				annotator.annotatedAndSave(document.getText(), matcher.group(1), matcher.group(2));
			} catch (AnalysisEngineProcessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			cas =  xmiCasRetriever.retrieveCAS(docID);
			casCache.put(docID, cas);
		}
		
		if(cas == null){
			LOGGER.info("Skipping No CAS found for docNo: " + docID);
			return;
		}else{
		}
		
		String candidatePassage = document.getText();
		
		rawDocument = cas.getDocumentText();
		
		int windowBegin = rawDocument.indexOf(candidatePassage.substring(10, (candidatePassage.length() >= 60) ? 60 : candidatePassage.length()-5)) - 10;
		
		if(windowBegin == -1)
			return;
		
		int windowEnd = windowBegin + candidatePassage.length();
		
		

		try{
			JCas jcas = cas.getJCas();
			List<EntityMention> mentions = new ArrayList<EntityMention>();
			List<Sentence> sentences = new ArrayList<Sentence>();
			FSIterator<?> sentenceFS = jcas.getJFSIndexRepository().getAllIndexedFS(Sentence.type);
			FSIterator<?> entityMentionFS = jcas.getJFSIndexRepository().getAllIndexedFS(EntityMention.type);
			while(entityMentionFS.hasNext()){
				EntityMention entityMention = (EntityMention) entityMentionFS.next();
				for(String targetType : targetTypes){
					String target[] = targetType.split(":");
					
					String type = target[0];
					String specificity = target[1];
					
					if(
							entityMention.getEntityType().equals(type) && 
							entityMention.getSpecificity().equals(specificity)  
							&& (entityMention.getBegin() >= windowBegin && entityMention.getEnd() <= windowEnd)
					){
//						candidates.add(entityMention.getCoveredText());
						mentions.add(entityMention);
	 				}
				}
			}
			
			List<String> sentenceText = new ArrayList<String>();
			
			while(sentenceFS.hasNext()){
				Sentence sentence = (Sentence) sentenceFS.next();
				if(sentence.getBegin() >= windowBegin && sentence.getEnd() <= windowEnd){
					sentences.add(sentence);
					sentenceText.add(sentence.getCoveredText());
				}
			}
			
			sentenceArrays = (String[]) sentenceText.toArray(new String[sentenceText.size()]);
			
			answerCandidates = new String[sentences.size()][];
			
			for(int i=0; i < sentences.size(); i++){
				Sentence s = sentences.get(i);
				List<String> sentenceCandidates = new ArrayList<String>();
				for(EntityMention e: mentions){
					if(e.getBegin() >= s.getBegin() && e.getEnd() <= s.getEnd()){
						sentenceCandidates.add(e.getCoveredText());
						System.err.print(".");
					}
				}
				answerCandidates[i] = (String[]) sentenceCandidates
						.toArray(new String[sentenceCandidates.size()]);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
//
//	public List<String> getCandidates(){
////		System.out.println(candidates);
//		return candidates;
//	}
//	
	
	
	public String getDocumentText() {
		return rawDocument;
	}

	public String[] getSentences() {
		return sentenceArrays;
	}

	@Override
	public String[][] getAnswerCandidates() {
		return answerCandidates;
	}

}
