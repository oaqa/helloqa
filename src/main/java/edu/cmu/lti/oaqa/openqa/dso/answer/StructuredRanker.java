package edu.cmu.lti.oaqa.openqa.dso.answer;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;


import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

/**
 * Reranks score of structured answers based on similarity
 * @author Junyang Ng
 *
 */
public class StructuredRanker {
	
	/**
	 * Simple reranking heuristic based on cosine similarity. We're not merging dates
	 * @param candidates
	 * @return
	 */
	public static List<AnswerCandidate> rerankStructured(List<AnswerCandidate> candidates) {
		
		CosineSimilarity cosine = new CosineSimilarity();
		Levenshtein lev = new Levenshtein();
		
		List<AnswerCandidate> rerankedList = new ArrayList<AnswerCandidate>();
		
		for(int i = 0; i < candidates.size(); i++) {
			
			AnswerCandidate current = candidates.get(i);
			
			DateTimeFormatter fmt = DateTimeFormat.forPattern("MMMM dd, YYYY");
			DateTimeFormatter fmt2 = DateTimeFormat.forPattern("MMMM dd YYYY");
			
			DateTime dt = null;
			
			try {
				dt = fmt.parseDateTime(current.getText());
			} catch (Exception e) {
				//do nothing
			}
			
			//If a date!
			if(dt != null) {
				rerankedList.add(current);
				continue;
			}
			
			dt = null;
			
			try {
				dt = fmt2.parseDateTime(current.getText());
			} catch (Exception e) {
				//do nothing
			}
			
			if(dt != null) {
				rerankedList.add(current);
				continue;
			}
			
			for(int j = 0; j < candidates.size(); j++) {
				
				if(j==i) continue;
				
				if(current.getText() == null || candidates.get(j).getText() == null) continue;
			
				DateTime dt2 = null;
				
				try {
					dt2 = fmt.parseDateTime(current.getText());
				} catch (Exception e) {
					//do nothing
				}
				
				//If a date!
				if(dt2 != null) {
					continue;
				}
				
				double cosScore = cosine.getSimilarity(current.getText(), candidates.get(j).getText());
				double levScore = lev.getSimilarity(current.getText(), candidates.get(j).getText());
				
				if(cosScore >= 0.50 || levScore >= 0.50) {
					
					double currentScore = current.getScore();
					double otherScore = candidates.get(j).getScore();
					
					double newScore = currentScore + otherScore;
					
					current.setScore(newScore);
					
				}
				
			}
			
			rerankedList.add(current);
			
		}
		
		return rerankedList;
		
	}

}
