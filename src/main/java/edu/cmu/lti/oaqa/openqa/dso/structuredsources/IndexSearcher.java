package edu.cmu.lti.oaqa.openqa.dso.structuredsources;
import java.io.File;
import java.util.List;
import java.util.Map;

import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

/**
 * Search the indri index
 * 
 * @author Junyang Ng
 * 
 */
public class IndexSearcher {

	private static final double mu = 1000;

	QueryEnvironment env;

	public IndexSearcher() {
		env = new QueryEnvironment();

		try {
			env.addIndex("res" + File.separator + "experimental" + File.separator + "events" + File.separator + "gtd_rand_corpus");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String[] rules = { "method:dirichlet", "mu:" + mu };
		try {
			env.setScoringRules(rules);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String[] doSearch(String question) throws Exception {

		ScoredExtentResult[] results = env.runQuery(question, 5);

		ParsedDocument[] documents = env.documents(results);
		
		if(documents.length == 0) {
			results = env.runQuery(question, 5);
		}
		
		String[] docno = env.documentMetadata(results, "docno");

		return docno;

	}

	public static void main(String[] args) {

		IndexSearcher is = new IndexSearcher();
		try {
			String[] asdf = is.doSearch("#equals(month 5) #equals(year 2010) lahore mosque");
			for(String s : asdf) {
				System.out.println(s);
				
			}
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
