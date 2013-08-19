package edu.cmu.lti.oaqa.openqa.dso.phase.keyterm;

import info.ephyra.nlp.NETagger;
import info.ephyra.nlp.OpenNLP;
import info.ephyra.nlp.SnowballStemmer;
import info.ephyra.nlp.StanfordNeTagger;
import info.ephyra.nlp.StanfordParser;
import info.ephyra.nlp.indices.FunctionWords;
import info.ephyra.nlp.indices.IrregularVerbs;
import info.ephyra.nlp.semantics.ontologies.WordNet;
import info.ephyra.questionanalysis.KeywordExtractor;
import info.ephyra.questionanalysis.QuestionNormalizer;
import info.ephyra.questionanalysis.Term;
import info.ephyra.questionanalysis.TermExtractor;
import info.ephyra.util.Dictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.ecd.log.AbstractLoggedComponent;
import edu.cmu.lti.oaqa.framework.BaseJCasHelper;
import edu.cmu.lti.oaqa.framework.types.InputElement;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.JCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.KeytermJCasManipulator;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewManager;
import edu.cmu.lti.oaqa.openqa.dso.framework.jcas.ViewType;
import edu.cmu.lti.oaqa.openqa.dso.question.QuestionParser;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;

public class KeytermExtractor extends AbstractLoggedComponent {
	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		initialize();
	}

	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());

	public static final String TOKENIZER_PATH = "res/ephyra/nlp/tokenizer/opennlp/EnglishTok.bin.gz";

	public static final String SENT_DETECTOR_PATH = "res/ephyra/nlp/sentencedetector/opennlp/EnglishSD.bin.gz";

	public static final String TAGGER_PATH = "res/ephyra/nlp/postagger/opennlp/tag.bin.gz";
	public static final String TAGGER_DICT_PATH = "res/ephyra/nlp/postagger/opennlp/tagdict";

	public static final String CHUNKER_PATH = "res/ephyra/nlp/phrasechunker/opennlp/EnglishChunk.bin.gz";

	public static final String NER_LIST_PATH = "res/ephyra/nlp/netagger/lists/";
	public static final String NER_REGEX_PATH = "res/ephyra/nlp/netagger/patterns.lst";
	public static final String NER_STANFORD_PATH = "res/ephyra/nlp/netagger/stanford/ner-eng-ie.crf-3-all2006-distsim.ser.gz";

	public void initialize() {
		// tokenizer
		if (!OpenNLP.createTokenizer(TOKENIZER_PATH))
			LOGGER.fatal("Could not initialize tokenizer.");

		// sentence segmenter
		if (!OpenNLP.createSentenceDetector(SENT_DETECTOR_PATH))
			LOGGER.fatal("Could not initialize sentence segmenter.");

		// stemmer
		SnowballStemmer.create();

		// part of speech tagger
		if (!OpenNLP.createPosTagger(TAGGER_PATH, TAGGER_DICT_PATH))
			LOGGER.fatal("Could not initialize POS tagger.");

		// phrase chunker
		if (!OpenNLP.createChunker(CHUNKER_PATH))
			LOGGER.fatal("Could not initialize phrase chunker.");

		// syntactic parser
		try {
			StanfordParser.initialize();
		} catch (Exception e) {
			LOGGER.fatal("Could not initialize syntactic parser.");
		}

		// named entity recognizers
		NETagger.loadListTaggers(NER_LIST_PATH);
		NETagger.loadRegExTaggers(NER_REGEX_PATH);
		if (!StanfordNeTagger.isInitialized()
				&& !StanfordNeTagger.init(NER_STANFORD_PATH))
			LOGGER.fatal("Could not initialize NE tagger.");

		// function words
		if (!FunctionWords
				.loadIndex("res/ephyra/indices/functionwords_nonumbers"))
			System.err.println("Could not load function words.");

		// irregular verbs
		if (!IrregularVerbs.loadVerbs("res/ephyra/indices/irregularverbs"))
			LOGGER.fatal("Could not load irregular verbs.");
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			InputElement input = ((InputElement) BaseJCasHelper.getAnnotation(
					jcas, InputElement.type));
			String questionText = input.getQuestion();

			// normalize question
			String qn = QuestionNormalizer.normalize(questionText);

			// resolve verb constructions with auxiliaries
			String verbMod = (QuestionNormalizer.handleAuxiliaries(qn))[0];

			// extract keywords
			String[] kws = KeywordExtractor.getKeywords(verbMod);
			List<String> keyterms = new ArrayList<String>();
			for (String kw : kws) {
				keyterms.add(kw);
			}
			LOGGER.info("  Keywords: " + Arrays.toString(kws));

			// extract bigrams
			List<String> keyphrases = new ArrayList<String>();
			QuestionParser questionParse = new QuestionParser();
			questionParse.initialize(questionText);
			keyphrases = questionParse.getBigrams();

			// extract phrases based on WordNet and named entities
			String[][] nes = TermExtractor.getNes(questionText);
			Dictionary wordNet = new WordNet();
			Term[] terms = TermExtractor.getTerms(verbMod, qn, nes,
					new Dictionary[] { wordNet });

			List<String> keyNERs = new ArrayList<String>(terms.length);
			for (int i = 0; i < terms.length; i++) {
				keyNERs.add(terms[i].getText().replace("\"", ""));
			}
			LOGGER.info("  Phrases:  " + keyNERs);

			// keyphrases=keyNERs;
			// Save result into a view
			KeytermJCasManipulator.storeKeyTermsAndPhrases(ViewManager.getView(jcas, ViewType.KEYTERM), keyterms,
					keyphrases);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}
