package edu.cmu.lti.oaqa.openqa.hello.passage;

public class KeytermWindowScorerSum implements KeytermWindowScorer {

	public double scoreWindow ( int begin , int end , int matchesFound , int totalMatches , int keytermsFound , int totalKeyterms , int textSize ){
		int windowSize = end - begin;
		double offsetScore = ( (double)textSize - (double)begin ) / (double)textSize;
		return ( .25d * (double)matchesFound / (double)totalMatches ) + .25d * ( (double)keytermsFound / (double)totalKeyterms) + .25d * ( 1 - ( (double)windowSize / (double)textSize ) + .25d * offsetScore );
	}

}
