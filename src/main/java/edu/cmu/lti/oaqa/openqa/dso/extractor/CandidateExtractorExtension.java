package edu.cmu.lti.oaqa.openqa.dso.extractor;

import info.ephyra.nlp.NETagger;
import info.ephyra.nlp.OpenNLP;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.util.FilterUtils;
import edu.cmu.lti.oaqa.openqa.dso.util.StopWords;

public class CandidateExtractorExtension extends CandidateExtractorBase {
	private static HashSet<String> tabooCandidateSet;

	@Override
	public String[][] getAnswerCandidates() {
		return nes;
	}

	private void generateAnswerCandidates(String answerType,
			String[] sentences, List<String> keyterms, String questionText) {

		nes = new String[sentences.length][];

		if (activeAnswerExtractor(answerType)) {
			for (int i = 0; i < sentences.length; i++) {
				List<String> candidates = new ArrayList<String>();
				candidates = getAnwserCandidates(sentences[i], keyterms,
						questionText);
				candidates=filter(candidates);
				nes[i] = candidates.toArray(new String[candidates.size()]);
			}
		}
	}

	private List<String> filter(List<String> candidates) {
		List<String> refinedCandidates = new ArrayList<String>();
		for (String candidate : candidates) {
			if ((FilterUtils.isSourceName(candidate) || candidate.length() <= 1)) {
				continue;
			} else {
				refinedCandidates.add(candidate);
			}
		}
		return refinedCandidates;
	}

	public CandidateExtractorExtension(String answerType, String[] sentences,
			List<String> keyterms, String questionText) {
		StopWords.getInstance();
		tokens = getTokens(sentences);
		// remove date type answer candidates because they are accurate.
		tabooCandidateSet = new HashSet<String>();
		int[] neId = NETagger.getNeIds("NEdate");
		String[][] nes = NETagger.extractNes(tokens, neId[0]);

		for (int i = 0; i < nes.length; i++) {
			for (int j = 0; j < nes[i].length; j++) {
				if (nes[i][j] != null && !nes[i][j].equals(""))
					tabooCandidateSet.add(nes[i][j]);
			}
		}

		tabooCandidateSet.addAll(generateTabooAnswerCandidates(tokens,
				answerType));

		generateAnswerCandidates(answerType, sentences, keyterms, questionText);
	}

	private HashSet<String> generateTabooAnswerCandidates(String[][] tokens,
			String answerType) {
		HashSet<String> tabooCandiates = new HashSet<String>();
		ArrayList<String> tabooAnswerTypeList = new ArrayList<String>();
		String[] answerTypes = { "NEdate", "NEperson", "NEcountry" };

		for (int i = 0; i < answerTypes.length; i++) {
			if (!answerType.equals("NEnone")
					&& !answerType.contains(answerTypes[i])) {
				tabooAnswerTypeList.add(answerTypes[i]);
			} else if (answerType.equals("NEnone")) {
				tabooAnswerTypeList.add("NEdate");
			}
		}

		for (int i = 0; i < tabooAnswerTypeList.size(); i++) {
			int[] neId = NETagger.getNeIds(tabooAnswerTypeList.get(i));
			String[][] nes = NETagger.extractNes(tokens, neId[0]);
			for (int k = 0; k < nes.length; k++) {
				for (int j = 0; j < nes[k].length; j++) {
					if (nes[k][j] != null && !nes[k][j].equals(""))
						tabooCandiates.add(nes[k][j].toLowerCase());
				}
			}
		}

		// all casted to lower case
		return tabooCandiates;
	}

	private boolean activeAnswerExtractor(String answerType) {
		boolean isactive = false;
		if (!filterByQuestionType(answerType)) {
			isactive = true;
		}
		return isactive;
	}

	private boolean filterByQuestionType(String answerType) {
		String[] tabooType = { "NEdate" };
		for (int i = 0; i < tabooType.length; i++) {
			if (answerType.contains(tabooType[i])) {
				return true;
			}
		}
		return false;
	}

	private List<String> getAnwserCandidates(String sentence,
			List<String> keyterms, String question) {

		String tempstr = sentence;
		ArrayList<String> tokens = new ArrayList<String>();
		ArrayList<String> bracktetokens = new ArrayList<String>();
		tempstr = tempstr.replace("(", " ( ");
		String[] temp = tempstr.split(" ");
		for (int i = 0; i < temp.length; i++) {
			if (!temp[i].equals("") && !temp[i].equals("(")) {
				tokens.add(temp[i]);
			}
			if (temp[i].equals("(") && tokens.size() > 0) {
				bracktetokens.add(tokens.get(tokens.size() - 1));
			}
		}

		tempstr = tempstr.replaceAll("\\p{Punct}", " ");

		String sentencepos = OpenNLP.tagPos(tempstr);

		String[] wordswithtag = sentencepos.split(" ");
		ArrayList<String> taggedlist = new ArrayList<String>();

		// only keep words of Noun and Capital words
		for (int j = 0; j < wordswithtag.length; j++) {
			String[] tagspliter = wordswithtag[j].split("/");
			if (tagspliter.length > 1
					&& !tagspliter[tagspliter.length - 1].equals("")) {
				tagspliter[0] = tagspliter[0].replaceAll("\\p{Punct}", "");

				if (tagspliter[0] != null && !tagspliter[0].trim().equals("")) {
					// change the brackets' pos here
					if (bracktetokens.contains(tagspliter[0].trim())) {
						tagspliter[tagspliter.length - 1] = "NN";
					}
					if (tagspliter[tagspliter.length - 1].charAt(0) == 'N'
							|| ((tagspliter[0].charAt(0) >= 'A' && tagspliter[0]
									.charAt(0) <= 'Z')) && (j != 0)) {
						taggedlist.add(tagspliter[0].toLowerCase());
					}
					if (question.toLowerCase().contains("how many")
							|| question.toLowerCase().contains("how much")) {
						if (tagspliter[tagspliter.length - 1].length() >= 2
								&& tagspliter[tagspliter.length - 1].substring(
										0, 2).equals("CD")) {
							taggedlist.add(tagspliter[0].toLowerCase());
						}
					}
				}
			}
		}

		ArrayList<String> answercandidates = new ArrayList<String>();
		int termcounter = 0;
		for (int i = 0; i < keyterms.size(); i++) {
			if (tempstr.toLowerCase().contains(keyterms.get(i).toLowerCase())) {
				termcounter++;
				continue;
			}
		}

		if (termcounter >= (keyterms.size() / 2)) {
			String[] identiferitems = tempstr.split(" ");

			for (int i = 0; i < identiferitems.length; i++) {
				if (identiferitems[i] != null && !identiferitems[i].equals("")) {
					String itemlowercase = identiferitems[i].toLowerCase();
					if (!StopWords.contains(itemlowercase)) {
						if (!answercandidates.contains(itemlowercase)
								&& (!keyterms.contains(itemlowercase))) {
							if (!tabooCandidateSet.contains(itemlowercase)) {
								if (taggedlist.contains(itemlowercase)) {
									answercandidates.add(identiferitems[i]);
								}
							}
						}
					}
				}
			}
		}

		return answercandidates;
	}

}
