package edu.cmu.lti.oaqa.openqa.dso.scorer;

import java.util.ArrayList;
import java.util.HashMap;

import info.ephyra.nlp.SnowballStemmer;
import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;

public class LeafAnswerValidation implements IAnswerScorer {

	@Override
	public Score[] getScore(SupportingEvidenceArg arg) {
		SnowballStemmer.create();

		Score[] scores = new Score[arg.getNEs().length];
		for (int i = 0; i < scores.length; i++) {
			scores[i] = new Score(arg.getKeywords().size());
		}

		String[] sentences = arg.getPassageSentences();

		String[] normedKeywords = new String[arg.getKeywords().size()];
		for (int i = 0; i < arg.getKeywords().size(); i++) {
			String keyword = arg.getKeywords().get(i);
			keyword = SnowballStemmer.stem(keyword).toLowerCase();
			normedKeywords[i] = keyword.toLowerCase();
		}

		String[] normedSentences = new String[sentences.length];
		for (int i = 0; i < sentences.length; i++) {
			String currentSentence = SnowballStemmer.stemAllTokens(sentences[i]
					.replaceAll("\\p{Punct}+", " "));
			normedSentences[i] = " " + currentSentence.toLowerCase() + " ";

		}

		String[] normedNEs = new String[arg.getNEs().length];
		for (int i = 0; i < arg.getNEs().length; i++) {
			String ne = arg.getNEs()[i];
			ne = SnowballStemmer.stemAllTokens(ne);
			normedNEs[i] = ne.toLowerCase();
		}

		HashMap<String, ArrayList<Integer>> keywordPosHash = detectKeywordPos(
				normedKeywords, normedSentences);
		int[] nePos = detectNEPos(normedNEs, normedSentences);

		for (int i = 0; i < normedNEs.length; i++) {
			if (normedNEs[i].trim().length() > 0) {

				// init the validation score cs=1;
				double cs = 1.0;

				// for each question keyword k
				for (int j = 0; j < normedKeywords.length; j++) {
					if (!keywordPosHash.containsKey(normedKeywords[j])
							|| nePos[i] == -1) {
						continue;
					}

					ArrayList<Integer> keywordPosLs = keywordPosHash
							.get(normedKeywords[j]);

					// compute distance d
					double d = findMinDist(keywordPosLs, nePos[i]);
					String distInfo = normedKeywords[j] + ":" + (int) d;
					scores[i].addDistInfo(distInfo);

					cs = cs * Math.pow(2, 1.0 / (1 + d));
				}

				scores[i].setScore(cs);
				scores[i].setPsgID(arg.getPsgID());
			}
		}

		return scores;
	}

	int[] detectNEPos(String[] normedInput, String[] normedSentences) {
		int[] posArray = new int[normedInput.length];
		for (int i = 0; i < posArray.length; i++) {
			posArray[i] = -1;
		}
		int i = 0;
		int pos = 0;
		for (int j = 0; j < normedSentences.length; j++) {
			String str = normedSentences[j];
			int count = 0;
			while (i < normedInput.length) {
				int idx = str.indexOf(" " + normedInput[i] + " ");
				if (idx == -1) {
					break;
				}
				char previous = ' ';

				for (int k = 0; k < idx + 1; k++) {
					if (str.charAt(k) == ' ' && previous != ' ') {
						count++;
					}
					previous = str.charAt(k);
				}

				posArray[i] = pos + count;
				str = str.substring(idx + normedInput[i].length() + 2);
				i++;
			}

			String[] temp = normedSentences[j].split(" ");
			for (int k = 0; k < temp.length; k++) {
				if (temp[k].trim().equals("")) {
					continue;
				}
				pos++;
			}
		}

		return posArray;
	}

	HashMap<String, ArrayList<Integer>> detectKeywordPos(String[] normedInput,
			String[] normedSentences) {
		HashMap<String, ArrayList<Integer>> keywordPosHash = new HashMap<String, ArrayList<Integer>>();
		for (int i = 0; i < normedInput.length; i++) {
			int pos = 0;
			for (int j = 0; j < normedSentences.length; j++) {
				String str = normedSentences[j];
				int count = 0;
				while (true) {
					int idx = str.indexOf(" " + normedInput[i] + " ");
					if (idx == -1) {
						break;
					}
					char previous = ' ';

					for (int k = 0; k < idx + 1; k++) {
						if (str.charAt(k) == ' ' && previous != ' ') {
							count++;
						}
						previous = str.charAt(k);
					}

					if (keywordPosHash.containsKey(normedInput[i])) {
						keywordPosHash.get(normedInput[i]).add(pos + count);
					} else {
						ArrayList<Integer> posLs = new ArrayList<Integer>();
						posLs.add(pos + count);
						keywordPosHash.put(normedInput[i], posLs);
					}

					str = str.substring(idx + normedInput[i].length() + 2);
				}

				String[] temp = normedSentences[j].split(" ");
				for (int k = 0; k < temp.length; k++) {
					if (temp[k].trim().equals("")) {
						continue;
					}
					pos++;
				}

			}
		}

		return keywordPosHash;
	}

	int findMinDist(ArrayList<Integer> keywordPosLs, int nePos) {
		int d = 1000;
		for (int j = 0; j < keywordPosLs.size(); j++) {
			int current = Math.abs(keywordPosLs.get(j) - nePos);
			if (d > current) {
				d = current;
			}
		}
		return d;
	}

}
