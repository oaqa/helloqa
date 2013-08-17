package edu.cmu.lti.oaqa.openqa.dso.passage;

import java.util.List;

public class TermCoverage {
	private double[] tf;
	
	public TermCoverage(String documentText, List<String> keytermExpansion) {
		tf = new double[keytermExpansion.size()];
		documentText = documentText.toLowerCase();
		for (int j = 0; j < keytermExpansion.size(); j++) {
			// if the keyterm with expansions can be found in
			// the sentences
			if (documentText.contains(keytermExpansion.get(j).toLowerCase())) {
				tf[j] = 1;
			}
		}
	}
	
	double getScore() {
		double score = 0;
		for (int i = 0; i < tf.length; i++) {
			score += tf[i];
		}
		return score;
	}
}
