package edu.cmu.lti.oaqa.openqa.dso.data;

import java.util.List;

public class SupportingEvidenceArg {

	private String questionText;
	private String answerType;
	private String[] nes;
	private List<String> keywords;
	private List<String> keyphrases;

	private String[] sentences;
	private String passages;
	private String previousSentence;
	private String currentSentence;
	private String nextSentence;

	private double combinedScore;
	private int rank;
	private String pID;
	
	private String classNames;

	public SupportingEvidenceArg(String questionText, String answerType,
			List<String> keywords, List<String> keyphrases, String pID, String passages, String[] sentences, int rank, String classNames) {
		this.questionText = questionText;
		this.answerType = answerType;

		this.keywords = keywords;
		this.keyphrases = keyphrases;
		
		this.passages = passages;
		this.sentences=sentences;
		
		this.pID=pID;
		this.rank=rank;
		
		this.classNames=classNames;
	}

	public void setSentences(String previousSentence, String currentSentence,
			String nextSentence){
		this.previousSentence = previousSentence;
		this.currentSentence = currentSentence;
		this.nextSentence = nextSentence;
	}
	
	public void updateSupportingEvidenceArg(int index, String[][] nes) {
		// window -1, +1
		String previousSentence = "", currentSentence = "", nextSentence = "";
		if (index - 1 >= 0) {
			previousSentence = sentences[index - 1];
		}
		currentSentence = sentences[index];
		if (index + 1 < sentences.length) {
			nextSentence = sentences[index + 1];
		}
		
		setSentences(previousSentence, currentSentence, nextSentence);
		setNEs(nes[index]);
	}
	
	public String[] getSentences(){
		return sentences;
	}

	public String getAnswerType() {
		return answerType;
	}

	public void setAnsType(String answerType) {
		this.answerType = answerType;
	}

	public String[] getNEs() {
		return nes;
	}

	public void setNEs(String[] nes) {
		this.nes = nes;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setQuestionText(List<String> keywords) {
		this.keywords = keywords;
	}

	public List<String> getKeyphrases() {
		return keyphrases;
	}

	public void setKeyphrases(List<String> keyphrases) {
		this.keyphrases = keyphrases;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getPassages() {
		return passages;
	}

	public String getPreviousSentence() {
		return previousSentence;
	}

	public String getCurrentSentence() {
		return currentSentence;
	}

	public String getNextSentence() {
		return nextSentence;
	}

	public double getCombinedScore() {
		return combinedScore;
	}

	public void setCombinedScore(double score) {
		combinedScore = score;
	}
	
	public String getPID(){
		return pID;
	}
	
	public int getRank(){
		return rank;
	}
	
	public String getClassNames(){
		return this.classNames;
	}
}
