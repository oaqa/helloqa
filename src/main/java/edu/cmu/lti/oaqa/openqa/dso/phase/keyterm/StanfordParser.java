package edu.cmu.lti.oaqa.openqa.dso.phase.keyterm;

import info.ephyra.util.Properties;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.cmu.lti.javelin.util.DeltaRangeMap;
import edu.cmu.lti.javelin.util.RangeMap;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;

/**
 * Wrapper for the Stanford parser.
 * 
 * @author Justin Betteridge, Nico Schlaefer
 * @version 2007-10-30
 */
public class StanfordParser
{
    protected static final Logger log = Logger.getLogger(StanfordParser.class);
    protected static final Pattern whitespace_pattern = Pattern.compile("\\s+");
    protected static final Pattern escaped_char_pattern = Pattern.compile("\\\\/");
    protected static final Pattern double_quote_lable_pattern = Pattern.compile("[`'][`']");
    protected static final Pattern bracket_label_pattern = Pattern.compile("-...-");

    public static final String BEGIN_KEY = "begin";
    public static final String END_KEY = "end";
    
    protected static class MutableInteger {
        public int value;
        public MutableInteger() { value = 0; }
        public MutableInteger(int i) { value = i; }
        public String toString() { return Integer.toString(value); }
        public int getValue() { return value; }
        public void setValue(int i) { value = i; }
    }

    protected static TreebankLanguagePack tlp = null;
    protected static LexicalizedParser parser = null;

    /**
     * Hide default ctor.
     */
    protected StanfordParser() {}

    /**
     * Initializes static resources.
     * 
     * @throws Exception
     */
    public static void initialize() throws Exception
    {
        if (parser != null) return;
        Properties properties = Properties.loadFromClassName(StanfordParser.class.getName());
        tlp = new PennTreebankLanguagePack();
        String modelFile = properties.getProperty("modelFile");
        if (modelFile == null)
            throw new Exception("Required property '" 
                + "modelFile' is undefined");
        parser = LexicalizedParser.loadModel(modelFile);
    }

    /**
     * Unloads static resources.
     * 
     * @throws Exception
     */
    public static void destroy() throws Exception
    {
        tlp = null;
        parser = null;
    }
    
    /**
     * Parses a sentence and returns a string representation of the parse tree.
     * 
     * @param sentence a sentence
     * @return Tree whose Label is a MapLabel containing correct begin and end
     * character offsets in keys BEGIN_KEY and END_KEY
     */
	@SuppressWarnings("unchecked")
    public static String parse(String sentence)
    {
        if (tlp == null || parser == null)
            throw new RuntimeException("Parser has not been initialized");
        
        // parse the sentence to produce stanford Tree
        log.debug("Parsing sentence");
        Tree tree = null;
        synchronized (parser) {
            Tokenizer tokenizer = tlp.getTokenizerFactory().getTokenizer(new StringReader(sentence));
            List<Word> words = tokenizer.tokenize();
            log.debug("Tokenization: "+words);
    	    List<CoreLabel> rawWords = Sentence.toCoreLabelList(words);
    	    tree = parser.apply(rawWords);
    	    tree.pennPrint();
        }
        
        return tree.toString().replaceAll(" \\[[\\S]+\\]","");
    }
    
    /**
     * @param sentence
     * @return a list of RangeMap objects which define a mapping of character
     * offsets in a white-space depleted version of the input string back into
     * offsets in the input string.
     */
    protected static List<RangeMap> createMapping(String sentence)
    {
        List<RangeMap> mapping = new LinkedList<RangeMap>();
        Matcher whitespace_matcher = whitespace_pattern.matcher(sentence);
        DeltaRangeMap delta_rmap = null;

        // find all sequences of whitespace chars
        while (whitespace_matcher.find()) {
            int start = whitespace_matcher.start();
            int end = whitespace_matcher.end();
            int length = end - start;

            if (delta_rmap == null) {
                // create a new RangeMap object whose start begins at current
                // match start, and whose end is at the moment undefined. The
                // delta here is taken to be the length of the whitespace
                // sequence.
                delta_rmap = new DeltaRangeMap(start, 0, length);
            } else {
                // we've found the next sequence of whitespace chars, so we
                // finalize the end extent of the previous RangeMap, and make a
                // new RangeMap to describe the mapping from this point forward.
                delta_rmap.end = start - delta_rmap.delta;
                mapping.add(delta_rmap);
                delta_rmap = new DeltaRangeMap(delta_rmap.end, 0, delta_rmap.delta + length);
            }
        }

        // process trailing DeltaRangeMap if it exists
        if (delta_rmap != null) {
            delta_rmap.end = sentence.length() - delta_rmap.delta;
            mapping.add(delta_rmap);
        }

        return mapping;
    }
    
    public static void main(String[] args) throws Exception
    {
        if (args.length != 1) {
            System.out.println("USAGE: StanfordParser <inputSentencesFile>");
            System.out.println("Output stored in: <inputSentencesFile>.parses");
            System.exit(0);
        }
        StanfordParser.initialize();
        List<String> sentences = new ArrayList<String> ();
        BufferedReader in = new BufferedReader(new FileReader(args[0]));
        BufferedWriter out = new BufferedWriter(new FileWriter(args[0]+".parses"));
        String sentence;
        while ((sentence = in.readLine()) != null) {
            sentences.add(sentence);
        }
        for (String s : sentences) {
            out.append(StanfordParser.parse(s)+"\n");
        }
        out.close();
        in.close();
    }
}
