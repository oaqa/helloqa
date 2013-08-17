package edu.cmu.lti.oaqa.openqa.dso.passage;

import java.util.List;

import info.ephyra.nlp.OpenNLP;

public class CompleteKeyterms {
	double[][] tf;
	double score;

	public CompleteKeyterms(String documentText, List<String> keyterms) {

		documentText = documentText.toLowerCase();
		String[] sentences = OpenNLP.sentDetect(documentText);
		tf = new double[sentences.length][keyterms.size()];

		for (int i = 0; i < sentences.length; i++) {
			for (int j = 0; j < keyterms.size(); j++) {
				// search if the keyTerm can be found in the
				// sentences
				if (sentences[i].contains(keyterms.get(j).toLowerCase())) {
					tf[i][j] = 1;
					continue;
				}
			}
		}

		for (int i = 0; i < sentences.length; i++) {
			int termcounter = 0;
			for (int j = 0; j < keyterms.size(); j++) {
				termcounter += tf[i][j];
			}
			if (termcounter == keyterms.size()) {
				score = 1;
				break;
			}
		}
	}

	double getScore() {
		return score;
	}
}
