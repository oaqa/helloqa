package edu.cmu.lti.oaqa.openqa.dso.structuredsources;

import info.ephyra.nlp.semantics.ontologies.WordNet;
import info.ephyra.questionanalysis.atype.extractor.FeatureExtractor;
import info.ephyra.questionanalysis.atype.extractor.FeatureExtractorFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

import edu.cmu.lti.javelin.util.Language;
import edu.cmu.lti.util.Pair;
import edu.cmu.minorthird.classify.BasicDataset;
import edu.cmu.minorthird.classify.ClassLabel;
import edu.cmu.minorthird.classify.Classifier;
import edu.cmu.minorthird.classify.ClassifierLearner;
import edu.cmu.minorthird.classify.Dataset;
import edu.cmu.minorthird.classify.DatasetClassifierTeacher;
import edu.cmu.minorthird.classify.Example;
import edu.cmu.minorthird.classify.Instance;
import edu.cmu.minorthird.classify.algorithms.linear.MaxEntLearner;
import edu.cmu.minorthird.util.IOUtil;

public class FieldAnalyser {
	public static final String WORDNET_PATH = "res/ephyra/ontologies/wordnet/file_properties.xml";
	public static final String CLASSIFIER_PATH = "res/experimental/field_analyzer/maxent.classifier";
	
	private static FeatureExtractor extractor = null;
	private static Classifier classifier = null;
	
	static {
		try{
			classifier = (Classifier) IOUtil.loadSerialized(new File(CLASSIFIER_PATH));
			Pair<Language, Language> languagePair = new Pair<Language, Language>(
					Language.valueOf("en_US"), Language.valueOf("en_US"));
			WordNet.initialize(WORDNET_PATH);
			extractor = FeatureExtractorFactory.getInstance(languagePair.getFirst());
			extractor.initialize();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static String classify(String question){
		Instance instance = extractor.createInstance(question);
		ClassLabel classification = classifier.classification(instance);
		return classification.bestClassName();
	}
	
	
	public static void train(String trainFileName, String classifierFileName){
		Example[] loadFile = extractor.loadFile(trainFileName);
		
	    Dataset set = new BasicDataset();
	    
	    for(Example e : loadFile){
	    	set.add(e);
	    }
	    ClassifierLearner learner = new MaxEntLearner();
	    Classifier classifier = new DatasetClassifierTeacher(set).train(learner);
		
	    try {
			IOUtil.saveSerialized((Serializable) classifier, new File(classifierFileName));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void main(String args[]) throws Exception{
//		Classifier classifier = (Classifier) IOUtil.loadSerialized(new File("fieldClassifier"));
//		Pair<Language, Language> languagePair = new Pair<Language, Language>(
//				Language.valueOf("en_US"), Language.valueOf("en_US"));
//		
//		WordNet.initialize(WORDNET_PATH);
//			 
//		FeatureExtractor extractor = FeatureExtractorFactory.getInstance(languagePair.getFirst());
//		extractor.initialize();
//		Scanner sc = new Scanner(System.in);
//		System.out.print("> ");
//		while(sc.hasNextLine()){
//			String line = sc.nextLine();
//			if(line.trim().equals("quit")) break;
//			String classLabel = FieldAnalyser.classify(line);	
//			System.out.println(classLabel);
//			System.out.print("> ");
//		}
//		
//		train("res/experimental/field_analyzer/train.txt", "res/experimental/field_analyzer/maxent.classifier");
	}
}
