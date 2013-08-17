package edu.cmu.lti.oaqa.openqa.dso.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import edu.cmu.lti.oaqa.openqa.dso.framework.base.ICEventExtractor_ImplBase;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.IndexSearcher;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;

public class ICEventDetector extends ICEventExtractor_ImplBase {

	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());
	private static final String EVENT_STOP_LIST = "res" + File.separator + "experimental" + File.separator + "events" + File.separator + "event_stop_list.txt";
	private Map<String, Integer> stopList;
	private IndexSearcher searcher = null;

	@Override
	public String getComponentId() {
		return "IC Event Detector";
	}

	@Override
	public void initialize() {
		
		stopList = new HashMap<String, Integer>();
		
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(EVENT_STOP_LIST));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while(scanner.hasNextLine()) {
			
			String word = scanner.nextLine();
			stopList.put(word, 1);
			
		}
		
		scanner.close();
		
		searcher = new IndexSearcher();
		
	}


	/**
	 * Uses the keyterms to extract events
	 */
	@Override
	public String extractICEvent(String question, List<String> keyterms) {
		
		//identify month too
		
		String identifiedYear = "";
		String identifiedMonth = "";
		String focusEvent = "";
		
		/**
		 * Remove event stopwords from keyterms
		 */
		
		Iterator<String> iter = keyterms.iterator();
		while(iter.hasNext()) {
			
			String word = iter.next().toLowerCase();
			
			if(stopList.containsKey(word.toLowerCase())) {
				iter.remove();
			}
			
			if(word.matches("\\d\\d\\d\\d")) {
				identifiedYear = word;
				iter.remove();
			}
			
			if(word.matches("\\b(january|february|march|april|may|june|july|august|september|october|november|december|jan|feb|mar|apr|jun|jul|aug|sep|oct|nov|dec)\\b")) {
				identifiedMonth = word;
				iter.remove();
			}
			
		}
		
		/**
		 * Construct INDRI query for event identification
		 */

		String query = "";
		double tWeight = 1.0 / keyterms.size();
		double yWeight = tWeight * 3;
		
		for(String word : keyterms) {
			word = word.replaceAll("\\p{Punct}", "").replaceAll("[^A-Za-z0-9]", "");
			query += tWeight + " " + word + " ";
		}
		
		if(!identifiedYear.isEmpty()) {
			query += yWeight + " " + identifiedYear + " ";
		}
		
		if(!identifiedMonth.isEmpty()) {
			
			if(identifiedMonth.equalsIgnoreCase("january") || identifiedMonth.equalsIgnoreCase("jan")) {
				query += yWeight + " " + "#equals(month 1)";
			}
			
			if(identifiedMonth.equalsIgnoreCase("february") || identifiedMonth.equalsIgnoreCase("feb")) {
				query += yWeight + " " + "#equals(month 2)";
			}
			
			if(identifiedMonth.equalsIgnoreCase("march") || identifiedMonth.equalsIgnoreCase("mar")) {
				query += yWeight + " " + "#equals(month 3)";
			}
			
			if(identifiedMonth.equalsIgnoreCase("april") || identifiedMonth.equalsIgnoreCase("apr")) {
				query += yWeight + " " + "#equals(month 4)";
			}

			if(identifiedMonth.equalsIgnoreCase("may")) {
				query += yWeight + " " + "#equals(month 5)";
			}
			
			if(identifiedMonth.equalsIgnoreCase("june") || identifiedMonth.equalsIgnoreCase("jun")) {
				query += yWeight + " " + "#equals(month 6)";
			}
			
			if(identifiedMonth.equalsIgnoreCase("july") || identifiedMonth.equalsIgnoreCase("jul")) {
				query += yWeight + " " + "#equals(month 7)";
			}
			
			if(identifiedMonth.equalsIgnoreCase("august") || identifiedMonth.equalsIgnoreCase("aug")) {
				query += yWeight + " " + "#equals(month 8)";
			}
			
			if(identifiedMonth.equalsIgnoreCase("september") || identifiedMonth.equalsIgnoreCase("sep")) {
				query += yWeight + " " + "#equals(month 9)";
			}
			
			if(identifiedMonth.equalsIgnoreCase("october") || identifiedMonth.equalsIgnoreCase("oct")) {
				query += yWeight + " " + "#equals(month 10)";
			}
			
			if(identifiedMonth.equalsIgnoreCase("november") || identifiedMonth.equalsIgnoreCase("nov")) {
				query += yWeight + " " + "#equals(month 11)";
			}
			
			if(identifiedMonth.equalsIgnoreCase("december") || identifiedMonth.equalsIgnoreCase("dec")) {
				query += yWeight + " " + "#equals(month 12)";
			}
			
		}
		
		query = "#weight(" + query + ")";
		
		query = query.toLowerCase();
		
//		System.err.println(query);
		
		String[] docNos = null;
		
		try {
			docNos = searcher.doSearch(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(docNos.length != 0) {
			focusEvent = docNos[0];
		}
		
		/***
		 * Question Annotations
		 ***/
		
//		OnDemandAnnotator qAnnotator = OnDemandAnnotator.getAnnotator();
//		
//		List<EntityMention> mentions = null;
//		
//		try {
//			mentions = qAnnotator.annotateQuestion(question);
//		} catch (AnalysisEngineProcessException e) {
//			e.printStackTrace();
//		}
//		
//		for(EntityMention em : mentions) {
//			System.err.println("Entity Detected: " + em.getCoveredText() + " - " + em.getEntityType());
//		}
		
		return focusEvent;
		
	}

}
