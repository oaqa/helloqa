package edu.cmu.lti.oaqa.openqa.hello.passage;

public class KeytermWindowScorerProduct implements KeytermWindowScorer {

	public double scoreWindow ( int begin , int end , int matchesFound , int totalMatches , int keytermsFound , int totalKeyterms , int textSize ){
		int windowSize = end - begin;
		double offsetScore = ( (double)textSize - (double)begin ) / (double)textSize;
		return ( (double)matchesFound / (double)totalMatches ) * ( (double)keytermsFound / (double)totalKeyterms) * ( 1 - ( (double)windowSize / (double)textSize ) * offsetScore );
	}

}
