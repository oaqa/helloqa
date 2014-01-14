package edu.cmu.lti.oaqa.openqa.dso.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import edu.cmu.lti.oaqa.openqa.dso.data.SupportingEvidenceArg;
import edu.cmu.lti.oaqa.openqa.dso.extractor.ICandidateExtractor;
import edu.cmu.lti.oaqa.openqa.dso.extractor.LeafEphyraMainType;
import edu.cmu.lti.oaqa.openqa.dso.scorer.IAnswerScorer;

public class ClassUtil {
	public static ICandidateExtractor extractorFactory(String className, SupportingEvidenceArg arg){
        try {
            Class<?> classDefinition = Class.forName(className.trim());
            Constructor<?> constructor = classDefinition.getConstructor(SupportingEvidenceArg.class);
            ICandidateExtractor object = (ICandidateExtractor) constructor.newInstance(arg);
            //System.out.println("String = " + object.getTypeName());
            return object;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public static IAnswerScorer scorerFactory(String className, SupportingEvidenceArg arg){
        try {
            Class<?> classDefinition = Class.forName(className.trim());
            Constructor<?> constructor = classDefinition.getConstructor(SupportingEvidenceArg.class);
            IAnswerScorer object = (IAnswerScorer) constructor.newInstance(arg);
            //System.out.println("String = " + object.getTypeName());
            return object;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public static void main(String[] args){
        String className="edu.cmu.lti.oaqa.openqa.dso.extractor.LeafEphyraMainType";
        
        try {
            Class<?> classDefinition = Class.forName(className);
            @SuppressWarnings("unchecked")
			Constructor<LeafEphyraMainType> constructor = (Constructor<LeafEphyraMainType>) classDefinition.getConstructor(String.class, String[].class);
 
            ICandidateExtractor object = (ICandidateExtractor) constructor.newInstance(new Object[] {"Hello World!", null});
            System.out.println("String = " + object.getTypeName());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
