package edu.cmu.lti.oaqa.openqa.dso.framework.jcas;

/*
 *  Copyright 2012 Carnegie Mellon University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

//Responsible for creating and accessing JCAS views safely... in HelloQA, only candidateView and finalAnswerView are used. 

/**
 * 
 * @author ruil
 * 
 */
public class ViewManager {

	public static JCas getView(JCas jcas, ViewType type) throws CASException {
		return getOrCreateView(jcas, type);
	}

	public static JCas getOrCreateView(JCas jcas, ViewType type)
			throws CASException {
		String viewName = type.toString();
		try {
			return jcas.getView(viewName);
		} catch (Exception e) {
			jcas.createView(viewName);
			return jcas.getView(viewName);
		}
	}

}
