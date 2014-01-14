package edu.cmu.lti.oaqa.openqa.dso.extractor;


import java.util.ArrayList;

import java.util.List;

import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import edu.cmu.lti.oaqa.openqa.dso.scorer.AnswerCandidateScorer;

public class LeafErrAnalysis extends CandidateExtractorBase {
	public LeafErrAnalysis(SupportingEvidenceArg arg) {
		super(arg);
	}

	@Override
	public List<AnswerCandidate> getAnswerCandidates(SupportingEvidenceArg arg) {
		List<AnswerCandidate> candidates = new ArrayList<AnswerCandidate>();

		int rank = 1;
		for (RetrievalResult document : arg.getPassages()) {
			// split search result into sentences and tokenize sentences
			String documentText = document.getText().replace(" ... ", " ! ");

			// get refined sentence
			String[] sentences = detectSentences(documentText);
			
			arg.setPsg(document.getDocID(), sentences);
			
			nes=new String[sentences.length][];
			for(int i=0;i<sentences.length;i++){
				nes[i]=extractCandidateByGoldStandard(arg.getGsCandidates(),sentences[i].toLowerCase());
			}

			List<AnswerCandidate> currentCandidates = AnswerCandidateScorer.getAnswerCandidates(arg,
					getTypeName(), nes, rank);
			for (AnswerCandidate candidate : currentCandidates) {
				candidate.addRetrievalResult(document);
			}
			
			candidates.addAll(currentCandidates);
			rank++;
		}

		return candidates;
	}
	
	private String[] extractCandidateByGoldStandard(String[] gsCandidates, String sentence){
		ArrayList<String> resultLs=new ArrayList<String>();
		for(String gsCandidate:gsCandidates){
			while(true){
				int index=sentence.indexOf(gsCandidate.toLowerCase());
				if(index<0){
					break;
				}else{
					resultLs.add(gsCandidate);
					sentence=sentence.substring(index+gsCandidate.length());
				}
			}
		}
		
		String[] candidates=new String[resultLs.size()];
		for(int i=0;i<candidates.length;i++){
			candidates[i]=resultLs.get(i);
		}
		
		return candidates;
	}

	@Override
	public String getTypeName() {
		return "ErrorAnalysis";
	}

}
