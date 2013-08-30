package edu.cmu.lti.oaqa.openqa.dso.framework.jcas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.oaqa.dso.model.AnswerGS;
import org.oaqa.dso.model.AnswerGSList;

public class AnsGSJCasManipulator {
	/**
	 * Convert UIMA data model
	 * 
	 * @param questionView
	 * @return answerType
	 */
	public static ArrayList<String> loadAnsGS(JCas jcas) {
		ArrayList<String> results = new ArrayList<String>();
		AnnotationIndex<?> index = jcas.getAnnotationIndex(AnswerGSList.type);
		Iterator<?> it = index.iterator();

		if (it.hasNext()) {
			AnswerGSList gs = (AnswerGSList) it.next();
			FSArray list=gs.getGslist();
			for(int i=0;i<list.size();i++){
				AnswerGS label=(AnswerGS)list.get(i);
				results.add(label.getLabel());
			}
		}
		return results;
	}

	/**
	 * Stores (overwrite) answer type in a view
	 * 
	 * @param questionView
	 * @param type
	 */
	public static void storeAnsGS(JCas jcas, List<String> labels) {
		// Remove old content first! (otherwise, it would work only once)
		Iterator<?> it = jcas.getJFSIndexRepository().getAllIndexedFS(
				AnswerGSList.type);
		while (it.hasNext()) {
			AnswerGSList ansgs = (AnswerGSList) it.next();
			ansgs.removeFromIndexes();
		}

		AnswerGSList ansgs = new AnswerGSList(jcas);
		
		FSArray list=new FSArray(jcas, labels.size());
		for(int i=0;i<labels.size();i++){
			AnswerGS label=new AnswerGS(jcas);
			label.addToIndexes();
			label.setLabel(labels.get(i));
			list.set(i, label);
		}
		
		ansgs.setGslist(list);
		ansgs.addToIndexes();
	}
}
