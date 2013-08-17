package edu.cmu.lti.oaqa.openqa.dso.passage;

import java.util.List;

public class TFIDF {
	private double[] tf;
	private double[] idf;

	public TFIDF(String documentText, double[] idf,
			List<String> keytermExpansion) {
		tf = new double[keytermExpansion.size()];
		this.idf = idf;

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
			if (i < idf.length) {
				score += tf[i] * idf[i];
			} else {
				score += tf[i];
			}
		}
		return Math.log(score);
	}
}
