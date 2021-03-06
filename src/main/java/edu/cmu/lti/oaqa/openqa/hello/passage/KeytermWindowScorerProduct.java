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

public class KeytermWindowScorerProduct implements KeytermWindowScorer {

	public double scoreWindow ( int begin , int end , int matchesFound , int totalMatches , int keytermsFound , int totalKeyterms , int textSize ){
		int windowSize = end - begin;
		double offsetScore = ( (double)textSize - (double)begin ) / (double)textSize;
		return ( (double)matchesFound / (double)totalMatches ) * ( (double)keytermsFound / (double)totalKeyterms) * ( 1 - ( (double)windowSize / (double)textSize ) * offsetScore );
	}

}
