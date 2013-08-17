package edu.cmu.lti.oaqa.openqa.dso.framework.jcas;

import java.util.Iterator;

import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.oaqa.dso.model.AnswerType;

public class AnswerTypeJCasManipulator {
	/**
	 * Convert UIMA data model
	 * 
	 * @param questionView
	 * @return answerType
	 */
	public static String loadAnswerType(JCas jcas) {
		String result = null;
		AnnotationIndex<?> index = jcas.getAnnotationIndex(AnswerType.type);
		Iterator<?> it = index.iterator();

		if (it.hasNext()) {
			AnswerType atype = (AnswerType) it.next();
			result = atype.getLabel();
		}
		return result;
	}

	/**
	 * Stores (overwrite) answer type in a view
	 * 
	 * @param questionView
	 * @param type
	 */
	public static void storeAnswerType(JCas jcas, String type) {
		// Remove old content first! (otherwise, it would work only once)
		Iterator<?> it = jcas.getJFSIndexRepository().getAllIndexedFS(
				AnswerType.type);
		while (it.hasNext()) {
			AnswerType oaqaType = (AnswerType) it.next();
			oaqaType.removeFromIndexes();
		}

		AnswerType oaqaType = new AnswerType(jcas);
		oaqaType.setLabel(type);
		oaqaType.addToIndexes();
	}
}
