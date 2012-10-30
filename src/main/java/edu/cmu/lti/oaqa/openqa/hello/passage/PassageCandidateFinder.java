/*
 *  Copyright 2012 Carnegie Mellon University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package edu.cmu.lti.oaqa.openqa.hello.passage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import edu.cmu.lti.oaqa.framework.data.PassageCandidate;

public class PassageCandidateFinder {
	private String text;
	private String docId;
	
	private int textSize;      // values for the entire text
	private int totalMatches;  
	private int totalKeyterms;
	
	private KeytermWindowScorer scorer;
	
	public PassageCandidateFinder( String docId , String text , KeytermWindowScorer scorer ) {
		super();
		this.text = text;
		this.docId = docId;
		this.textSize = text.length();
		this.scorer = scorer;
	}
	public List<PassageCandidate> extractPassages( String[] keyterms ) {
		List<List<PassageSpan>> matchingSpans = new ArrayList<List<PassageSpan>>();
		List<PassageSpan> matchedSpans = new ArrayList<PassageSpan>();
		
		// Find all keyterm matches.
		for ( String keyterm : keyterms ) {
			Pattern p = Pattern.compile( keyterm );
			Matcher m = p.matcher( text );
			while ( m.find() ) {
				PassageSpan match = new PassageSpan( m.start() , m.end() ) ;
				matchedSpans.add( match );
				totalMatches++;
			}
			if (! matchedSpans.isEmpty() ) {
				matchingSpans.add( matchedSpans );
				totalKeyterms++;
			}
		}
		
		// create set of left edges and right edges which define possible windows.
		List<Integer> leftEdges = new ArrayList<Integer>();
		List<Integer> rightEdges = new ArrayList<Integer>();
		for ( List<PassageSpan> keytermMatches : matchingSpans ) {
			for ( PassageSpan keytermMatch : keytermMatches ) {
				Integer leftEdge = keytermMatch.begin;
				Integer rightEdge = keytermMatch.end; 
				if (! leftEdges.contains( leftEdge ))
					leftEdges.add( leftEdge );
				if (! rightEdges.contains( rightEdge ))
					rightEdges.add( rightEdge );
			}
		}
		
		// For every possible window, calculate keyterms found, matches found; score window, and create passage candidate.
		List<PassageCandidate> result = new ArrayList<PassageCandidate>();
		for ( Integer begin : leftEdges ) {
			for ( Integer end : rightEdges ) {
				if ( end <= begin ) continue; 
				// This code runs for each window.
				int keytermsFound = 0;
				int matchesFound = 0;
				for ( List<PassageSpan> keytermMatches : matchingSpans ) {
					boolean thisKeytermFound = false;
					for ( PassageSpan keytermMatch : keytermMatches ) {
						if ( keytermMatch.containedIn( begin , end ) ){
							matchesFound++;
							thisKeytermFound = true;
						}
					}
					if ( thisKeytermFound ) keytermsFound++;
				}
				double score = scorer.scoreWindow( begin , end , matchesFound , totalMatches , keytermsFound , totalKeyterms , textSize );
				PassageCandidate window = null;
				try {
					window = new PassageCandidate( docId , begin , end , (float) score , null );
				} catch (AnalysisEngineProcessException e) {
					e.printStackTrace();
				}
				result.add( window );
			}
		}
		
		// Sort the result in order of decreasing score.
		// Collections.sort ( result , new PassageCandidateComparator() );
		return result;

	}
	private class PassageCandidateComparator implements Comparator {
		// Ranks by score, decreasing.
		public int compare( Object o1 , Object o2 ) {
			PassageCandidate s1 = (PassageCandidate)o1;
			PassageCandidate s2 = (PassageCandidate)o2;
			if ( s1.getProbability() < s2.getProbability() ) {
				return 1;
			} else if ( s1.getProbability() > s2.getProbability() ) {
				return -1;
			}
			return 0;
		}		
	}

	class PassageSpan {
		private int begin, end;
		public PassageSpan( int begin , int end ) {
			this.begin = begin;
			this.end = end;
		}
		public boolean containedIn ( int begin , int end ) {
			if ( begin <= this.begin && end >= this.end ) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public static void main ( String[] args ) {
		PassageCandidateFinder passageFinder1 = new PassageCandidateFinder( "1" , "The quick brown fox jumped over the quick brown fox." ,
				new KeytermWindowScorerProduct() );
		PassageCandidateFinder passageFinder2 = new PassageCandidateFinder( "1" , "The quick brown fox jumped over the quick brown fox." ,
				new KeytermWindowScorerSum() );
		String[] keyterms = { "quick" , "jumped" };
		List<PassageCandidate> windows1 = passageFinder1.extractPassages( keyterms );
		System.out.println( "Windows (product scoring): " + windows1 );
		List<PassageCandidate> windows2 = passageFinder2.extractPassages( keyterms );
		System.out.println( "Windows (sum scoring): " + windows2 );
	}
	

}