package edu.cmu.lti.oaqa.openqa.dso.framework.jcas;

import java.util.Iterator;

import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.oaqa.dso.model.ICEvent;

public class ICEventJCasManipulator {
	public static String loadIcEvent(JCas questionView) {

		String result = null;
		AnnotationIndex<?> index = questionView
				.getAnnotationIndex(ICEvent.type);

		Iterator<?> it = index.iterator();

		if (it.hasNext()) {
			ICEvent ic = (ICEvent) it.next();
			result = ic.getLabel();
		}

		return result;
	}

	public static void storeICEvent(JCas questionView, String type) {

		Iterator<?> it = questionView.getJFSIndexRepository().getAllIndexedFS(
				ICEvent.type);

		while (it.hasNext()) {
			ICEvent icEvent = (ICEvent) it.next();
			icEvent.removeFromIndexes();
		}

		ICEvent icEvent = new ICEvent(questionView);
		icEvent.setLabel(type);
		icEvent.addToIndexes();

	}
}
