package edu.cmu.lti.oaqa.openqa.dso.passage;

import java.util.List;

public enum RerankScorer {

	TF_IDF {
		public double getScore(String documentText, List<String> keyterms, double[] idf,
				List<String> keytermExpansion) {
			TFIDF tfidf = new TFIDF(documentText, idf, keytermExpansion);
			score = tfidf.getScore();
			return score;
		}
	},

	TERM_COVERAGE {
		public double getScore(String documentText, List<String> keyterms, double[] idf,
		List<String> keytermExpansion){
			TermCoverage termcoverage = new TermCoverage(documentText,
					keytermExpansion);
			score = termcoverage.getScore();
			return score;
		}
	},
	
	COMPLETE_KEYTERMS_IN_ONE_SENTENCE{
		public double getScore(String documentText, List<String> keyterms, double[] idf,
				List<String> keytermExpansion){
			CompleteKeyterms complete=new CompleteKeyterms(documentText, keyterms);
			score=complete.getScore();
			return score;
		}
	};

	double score;

	public abstract double getScore(String documentText, List<String> keyterms, double[] idf,
			List<String> keytermExpansion);
}
