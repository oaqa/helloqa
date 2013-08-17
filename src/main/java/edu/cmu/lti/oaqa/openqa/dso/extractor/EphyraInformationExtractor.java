package edu.cmu.lti.oaqa.openqa.dso.extractor;

import info.ephyra.nlp.OpenNLP;
import info.ephyra.querygeneration.Query;
import info.ephyra.questionanalysis.TermExtractor;
import info.ephyra.search.Result;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.openqa.dso.answer.AnswerScorer;
import edu.cmu.lti.oaqa.openqa.dso.answer.StructuredRanker;
import edu.cmu.lti.oaqa.openqa.dso.data.AnswerCandidate;
import edu.cmu.lti.oaqa.openqa.dso.data.RetrievalResult;
import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.framework.base.InformationExtractor_ImplBase;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.GTDExtractor;
import edu.cmu.lti.oaqa.openqa.dso.structuredsources.RANDExtractor;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;


public class EphyraInformationExtractor extends InformationExtractor_ImplBase
		implements IComponent {

	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());

	private static final int MIN_SENTENCE_LENGTH = 3;

	private static final double CANDIDATE_EXTENSION_WEIGHT = 0.5;

	private static final double CANDIDATE_SUB_WEIGHT = 3;

	private static final String[] CANDIDATE_EXTRACTOR_TYPE = { "MAIN", "SUB",
			"EXTENSION" };

	private Map<String, String> structuredMap = null;

	private static final String EVENT_MAP = "res" + File.separator + "experimental" + File.separator + "events" + File.separator + "event-map.txt";

	private GTDExtractor gtdExtractor = null;
	private RANDExtractor randExtractor = null;
	
	private List<RetrievalResult> structuredAnswers = new ArrayList<RetrievalResult>();

	private JSONObject config;

	public EphyraInformationExtractor(JSONObject config) {
		this.config = config;
	}
	
	@Override
	public void initialize() {

		structuredMap = new HashMap<String, String>();

		Scanner scanner = null;

		try {
			scanner = new Scanner(new File(EVENT_MAP));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (scanner.hasNextLine()) {

			String t_line = scanner.nextLine();
			String[] splits = t_line.split(",");
			structuredMap.put(splits[0], splits[1]);

		}

		gtdExtractor = new GTDExtractor();
		randExtractor = new RANDExtractor();

	}

	@Override
	/**
	 * Extracts candidates of the expected answer type.
	 * 
	 * @param answerType
	 *            answer type
	 * @param documents
	 *            list of search results
	 * @param candidates
	 *            list of answer candidates
	 */
	public List<AnswerCandidate> extractAnswerCandidates(String questionText,
			String answerType, List<String> keyterms, List<String> keyphrases,
			List<RetrievalResult> documents) {
		
		structuredAnswers = new ArrayList<RetrievalResult>();

		List<AnswerCandidate> candidates = new ArrayList<AnswerCandidate>();
		String[][] questionNEs = TermExtractor.getNes(questionText);
		List<String> questionNELs = new ArrayList<String>();

		for (int i = 0; i < questionNEs.length; i++) {
			for (int k = 0; k < questionNEs[i].length; k++) {
				if (questionNEs[i][k] != null
						&& (!questionNEs[i][k].equals(""))
						&& (!questionNELs.contains(questionNEs[i][k]))) {
					questionNELs.add(questionNEs[i][k]);
				}
			}
		}
		
		int rank = 1;

		List<String> docNos = new ArrayList<String>();

		for (RetrievalResult document : documents) {
			docNos.add(document.getDocID());
		}

		LOGGER.info(docNos);

		for (RetrievalResult document : documents) {

			// split search result into sentences and tokenize sentences
			String documentText = document.getText().replace(" ... ", " ! ");

			// get refined sentence
			String[] sentences = detectSentences(documentText);
			int sentenceLength = sentences.length;

			// No filtering/merging of candidates being done in this function,
			// so each candidate will have a list with only one retrieval result
			List<RetrievalResult> documentList = new ArrayList<RetrievalResult>();
			documentList.add(document);

			CandidateExtractorBase extractor = new CandidateExtractorByMainAnswerType(
					answerType, sentences);
			String[][] nesByMainAnswerType = extractor.getAnswerCandidates();

			CandidateExtractorBase extractorSub = new CandidateExtractorBySubAnswerType(
					answerType, sentences);
			String[][] nesBySubAnswerType = extractorSub.getAnswerCandidates();

			CandidateExtractorBase extractorExtension = new CandidateExtractorExtension(
					answerType, sentences, keyterms, documentText);
			String[][] nesExtension = extractorExtension.getAnswerCandidates();

			if (answerType.toLowerCase().contains("NEYES".toLowerCase())) {
				AnswerCandidate candidate = new AnswerCandidate("Yes",
						documentList);
				candidate.setScore(1);// evidenceScores[neCounter]);//
				candidates.add(candidate);
			} else if (nesByMainAnswerType != null) {
				generateAnswerCandidates(CANDIDATE_EXTRACTOR_TYPE[0],
						candidates, nesByMainAnswerType, sentenceLength,
						sentences, questionText, answerType, keyterms,
						keyphrases, documentText, documentList, rank);
			}

			/*
			 * if (nesBySubAnswerType != null) { nesBySubAnswerType =
			 * refineNEs(sentenceLength, nesByMainAnswerType,
			 * nesBySubAnswerType);
			 * generateAnswerCandidates(CANDIDATE_EXTRACTOR_TYPE[1], candidates,
			 * nesBySubAnswerType, sentenceLength, sentences, questionText,
			 * answerType, keyterms, keyphrases, documentText, documentList,
			 * rank); }
			 */

			/*
			 * if (nesExtension != null) { nesExtension =
			 * refineNEs(sentenceLength, nesByMainAnswerType, nesExtension);
			 * nesExtension = refineNEs(sentenceLength, nesBySubAnswerType,
			 * nesExtension);
			 * generateAnswerCandidates(CANDIDATE_EXTRACTOR_TYPE[2], candidates,
			 * nesExtension, sentenceLength, sentences, questionText,
			 * answerType, keyterms, keyphrases, documentText, documentList,
			 * rank); }
			 */

//			CandidateExtractorByXmi extractorByXmi = new CandidateExtractorByXmi(answerType, document);
//			String currentXmiCandidates[][] = extractorByXmi.getAnswerCandidates();
//			String[] xmiSentences = extractorByXmi.getSentences();
//			String xmiDocumentText = extractorByXmi.getDocumentText();
//			
//			if(currentXmiCandidates != null ){
//				generateAnswerCandidates(CANDIDATE_EXTRACTOR_TYPE[2], candidates, currentXmiCandidates, 
//					xmiSentences.length, xmiSentences, questionText, answerType, keyterms, keyphrases, xmiDocumentText, documentList, rank);
//			}
			
			rank++;

			// CandidateExtractorByXmi extractorByXmi = new
			// CandidateExtractorByXmi(answerType, document);
			// xmiCandidates.addAll(extractorByXmi.getCandidates());

			
		}

		List<AnswerCandidate> structuredCandidates = new ArrayList<AnswerCandidate>();
		
		CandidateExtractorByInfoBox extractorbyInfoBox = new CandidateExtractorByInfoBox();
		List<String> infoBoxCandidates = extractorbyInfoBox
				.getAnswerCandidates(keyterms, answerType);

		for (String ne : infoBoxCandidates) {
			
			if(ne == null || ne.isEmpty() || ne.equals("")) {
				continue;
			}
			
			AnswerCandidate candidate = new AnswerCandidate(ne.trim(),
					new ArrayList<RetrievalResult>());
			candidate.setScore(10);
			LOGGER.info("Adding Infobox candidate: " + candidate.getText());
			structuredCandidates.add(candidate);
		}
		
		
		/**
		 * Retrieval from RDF
		 */
		
		CandidateExtractorByRDF rdf = new CandidateExtractorByRDF(config);
		String rdfAns = rdf.getAnswerForQuestion(questionText);
		
		if(rdfAns != null && !rdfAns.isEmpty()) {
			AnswerCandidate candidate = new AnswerCandidate(rdfAns.trim(),
					new ArrayList<RetrievalResult>());
			candidate.setScore(10);
			LOGGER.info("Adding RDF candidate: " + candidate.getText());
			structuredCandidates.add(candidate);
		}
		
		/**
		 * Structured retrieval from GTD and Rand
		 */

		String datasetEvent = structuredMap.get(icEvent);

		CandidateExtractorByStructuredSources extractorbyStructured = new CandidateExtractorByStructuredSources();
		List<String> gtdrandCandidates = extractorbyStructured
				.getAnswerCandidates(questionText, answerType, datasetEvent,
						gtdExtractor, randExtractor);

		for (String ne : gtdrandCandidates) {

			if(ne == null || ne.isEmpty() || ne.equals("")) {
				continue;
			}
			
			AnswerCandidate candidate = new AnswerCandidate(ne.trim(),
					new ArrayList<RetrievalResult>());
			candidate.setScore(10);
			LOGGER.info("Adding GTD/Rand candidate: " + candidate.getText());
			structuredCandidates.add(candidate);

		}
		
		//Check through structured candidates for reranking
		List<AnswerCandidate> structuredRerank = StructuredRanker.rerankStructured(structuredCandidates);
		
		//Add structured candidates to all answer candidates
		candidates.addAll(structuredRerank);
		Random generator = new Random();

		for(AnswerCandidate ac : structuredRerank) {
			
			Result result = new Result(ac.getText(), new Query("STUCTURED" + ac.getText()),"" + generator.nextInt(1000000), 0); // Document Rank
			structuredAnswers.add(new RetrievalResult("INFO"+result.getDocID(),-1, ac.getText(), 1,null));
		}
		
		LOGGER.info("  Extracted " + candidates.size() + " candidates.");

		return candidates;
	}
	
	@Override
	public List<RetrievalResult> extractStructuredCandidates() {
		return structuredAnswers;
	}

	private void generateAnswerCandidates(String candidateType,
			List<AnswerCandidate> candidates, String[][] nes,
			int sentenceLength, String[] sentences, String questionText,
			String answerType, List<String> keyterms, List<String> keyphrases,
			String documentText, List<RetrievalResult> documentList, int rank) {
		for (int i = 0; i < sentenceLength; i++) {
			if (nes[i] != null && nes[i].length > 0) {
				// build arg
				SupportingEvidenceArg arg = buildSupportingEvidenceArg(i,
						sentences, questionText, answerType, nes, keyterms,
						keyphrases, documentText);

				// score nes
				double[] evidenceScores = AnswerScorer
						.getFusionUnigramProximityScore(arg);

				// distinct candidate of answer type, and candidate extension
				if (candidateType.equals(CANDIDATE_EXTRACTOR_TYPE[1])) {
					for (int k = 0; k < evidenceScores.length; k++) {
						if (evidenceScores[k] >= CANDIDATE_SUB_WEIGHT) {
							evidenceScores[k] = CANDIDATE_SUB_WEIGHT;
						}
					}
				}

				if (candidateType.equals(CANDIDATE_EXTRACTOR_TYPE[2])
						&& !answerType.equals("NEnone")) {
					for (int k = 0; k < evidenceScores.length; k++) {
						if (evidenceScores[k] >= CANDIDATE_EXTENSION_WEIGHT) {
							evidenceScores[k] = CANDIDATE_EXTENSION_WEIGHT;
						}
					}
				}

				// add NEs to candidates
				int neCounter = 0;
				for (String ne : nes[i]) {
					ne = candidateNormalization(ne, answerType);
					AnswerCandidate candidate = new AnswerCandidate(ne,
							documentList);
					candidate.setScore(AnswerScorer.normalization(
							evidenceScores[neCounter], keyterms.size(), rank));// evidenceScores[neCounter]);//
					candidates.add(candidate);
					neCounter++;
				}
			}
		}
	}

	private String candidateNormalization(String ne, String answerType) {
		String[] segment = ne.split(",");
		String normalizedStr = "";
		for (int i = 0; i < segment.length; i++) {
			if (!segment[i].equals("") && i != segment.length - 1) {
				normalizedStr += segment[i].trim() + ", ";
			} else {
				normalizedStr += segment[i].trim();
			}
		}

		CandidateExtractorBase.getNEMatchOntology(ne, answerType);

		return normalizedStr.trim();
	}

	private String[][] refineNEs(int sentenceLength,
			String[][] nesByAnswerType, String[][] nesExtension) {
		String[][] nes = new String[sentenceLength][];
		for (int i = 0; i < sentenceLength; i++) {
			HashSet<String> duplicate = new HashSet<String>();
			if (nesByAnswerType != null && nesByAnswerType[i] != null) {
				for (int j = 0; j < nesByAnswerType[i].length; j++) {
					if (!duplicate
							.contains(nesByAnswerType[i][j].toLowerCase())) {
						duplicate.add(nesByAnswerType[i][j].toLowerCase());
					}
				}
			}

			List<String> neList = new ArrayList<String>();
			if (nesExtension[i] != null) {
				for (int j = 0; j < nesExtension[i].length; j++) {
					if (!duplicate.contains(nesExtension[i][j].toLowerCase())) {
						neList.add(nesExtension[i][j].toLowerCase());
					}
				}
			}
			nes[i] = neList.toArray(new String[neList.size()]);
		}

		return nes;
	}

	private SupportingEvidenceArg buildSupportingEvidenceArg(int index,
			String[] sentences, String questionText, String answerType,
			String[][] nes, List<String> keyterms, List<String> keyphrases,
			String documentText) {
		// window -1, +1
		String previousSentence = "", currentSentence = "", nextSentence = "";
		if (index - 1 >= 0) {
			previousSentence = sentences[index - 1];
		}
		currentSentence = sentences[index];
		if (index + 1 < sentences.length) {
			nextSentence = sentences[index + 1];
		}

		SupportingEvidenceArg arg = new SupportingEvidenceArg(questionText,
				answerType, nes[index], keyterms, keyphrases, documentText,
				previousSentence, currentSentence, nextSentence);

		return arg;
	}

	private String[] detectSentences(String documentText) {
		String[] sentences = OpenNLP.sentDetect(documentText);
		List<String> refinedSentences = new ArrayList<String>();
		for (String sentence : sentences) {
			if (sentence.length() > MIN_SENTENCE_LENGTH) {
				refinedSentences.add(sentence);
			}
		}
		return refinedSentences.toArray(new String[refinedSentences.size()]);
	}

	@Override
	public String getComponentId() {
		return "Ephyra Information Extractor by Answer Type";
	}
}
