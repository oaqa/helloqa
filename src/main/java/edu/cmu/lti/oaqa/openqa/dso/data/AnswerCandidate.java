package edu.cmu.lti.oaqa.openqa.dso.data;

import java.util.ArrayList;
import java.util.List;

import org.oaqa.dso.model.Answer;


/**
 * Class that represents an Answer Candidates
 * 
 * @author Kevin Dela Rosa
 * @version 2010-03-02
 * 
 */
public class AnswerCandidate implements Comparable<AnswerCandidate> {
	private String text;
	private List<Double> featureVector;
	private String[] featureLabels;
	private double score;
	private List<RetrievalResult> retrievalResultList;

	/**
	 * Default Constructor
	 */
	public AnswerCandidate() {
		this.featureVector = new ArrayList<Double>(0);
		this.score = 0.0;
		this.retrievalResultList = new ArrayList<RetrievalResult>(0);
	}

	/**
	 * Constructor
	 * 
	 * @param answer
	 *            answer model object
	 */
	public AnswerCandidate(Answer answer) {
		this.featureVector = new ArrayList<Double>(0);
		this.text = answer.getText();
		this.score = answer.getScore();
		if (answer.getFeatureVector() != null) {
			for (int j = 0; j < answer.getFeatureVector().size(); j++) {
				this.addFeature(answer.getFeatureVector(j));
			}
		}
		this.retrievalResultList = new ArrayList<RetrievalResult>();
		if (answer.getSearchResultList() != null) {
			for (int j = 0; j < answer.getSearchResultList().size(); j++) {
				this.addRetrievalResult(new RetrievalResult(answer
						.getSearchResultList(j)));
			}
		}

	}

	/**
	 * Constructor
	 * 
	 * @param text
	 *            text value of candidate
	 * @param results
	 *            list of documents that contain candidate
	 */
	public AnswerCandidate(String text, List<RetrievalResult> results) {
		this.text = text;
		this.featureVector = new ArrayList<Double>(0);
		this.score = 0.0;
		this.retrievalResultList = new ArrayList<RetrievalResult>();
		for (RetrievalResult result : results) {
			this.addRetrievalResult(result);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param text
	 *            text value of candidate
	 * @param results
	 *            list of documents that contain candidate
	 * @param featureLabels
	 *            labels of features stored in object
	 */
	public AnswerCandidate(String text, List<RetrievalResult> results,
			String[] featureLabels) {
		this.text = text;
		this.retrievalResultList = new ArrayList<RetrievalResult>();
		for (RetrievalResult result : results) {
			this.addRetrievalResult(result);
		}
		this.featureLabels = featureLabels;
		this.featureVector = new ArrayList<Double>(0);
		this.score = 0.0;
	}

	@Override
	public String toString() {
		return this.text;
	}

	/**
	 * Sets list of documents
	 * 
	 * @param results
	 *            list of documents/results that contain candidate
	 */
	public void setRetrievalResultLIst(List<RetrievalResult> results) {
		this.retrievalResultList = new ArrayList<RetrievalResult>();
		for (RetrievalResult result : results) {
			this.addRetrievalResult(result);
		}
	}

	/**
	 * Get list of documents
	 * 
	 * @return list of documents/results that contain candidate
	 */
	public List<RetrievalResult> getRetrievalResults() {
		return this.retrievalResultList;
	}

	/**
	 * Add retrieval result to document/result list
	 * 
	 * @param result
	 *            document/result that contains candidate
	 */
	public void addRetrievalResult(RetrievalResult result) {
		this.retrievalResultList.add(result);
	}

	/**
	 * Set score
	 * 
	 * @param score
	 *            score for candidate
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * Get score
	 * 
	 * @return score for candidate
	 */
	public double getScore() {
		return this.score;
	}

	/**
	 * Set feature values
	 * 
	 * @param features
	 *            list of feature values
	 */
	public void setFeatures(List<Double> features) {
		for (double feature : features) {
			this.addFeature(feature);
		}
	}

	/**
	 * Set feature values
	 * 
	 * @param features
	 *            array of feature values
	 */
	public void setFeatures(double[] features) {
		for (double feature : features) {
			this.addFeature(feature);
		}
	}

	/**
	 * Get feature values
	 * 
	 * @return array of feature values
	 */
	public double[] getFeatures() {
		if (featureVector != null) {
			double[] features = new double[featureVector.size()];
			for (int i = 0; i < features.length; i++) {
				features[i] = featureVector.get(i);
			}
			return features;
		}
		return null;
	}

	/**
	 * Add feature value
	 * 
	 * @param feature
	 */
	public void addFeature(double feature) {
		this.featureVector.add(feature);
	}

	/**
	 * Get feature value
	 * 
	 * @param index
	 *            index of feature
	 * @return feature value
	 */
	public double getFeature(int index) {
		return this.featureVector.get(index);
	}

	/**
	 * Set candidate text
	 * 
	 * @param text
	 *            candidate text value
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Get candidate text
	 * 
	 * @return candidate text value
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Get feature labels
	 * 
	 * @return array of feature labels
	 */
	public String[] getFeatureLabels() {
		return this.featureLabels;
	}

	/**
	 * Set feature labels
	 * 
	 * @param featureLabels
	 *            array of feature labels
	 */
	public void setFeatureLables(String[] featureLabels) {
		this.featureLabels = featureLabels;
	}

	/**
	 * Necessary function to make this object comparable (used in sorting by
	 * score).
	 */
	public int compareTo(AnswerCandidate o) {
		// Used in sorting, compares only by answer score
		return (int) ((this.score - o.getScore()) * 100000000);
	}
}
