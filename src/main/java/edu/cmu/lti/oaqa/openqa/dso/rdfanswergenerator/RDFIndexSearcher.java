package edu.cmu.lti.oaqa.openqa.dso.rdfanswergenerator;

import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

public class RDFIndexSearcher {

	private static final double mu = 1000;

	QueryEnvironment env;

	public RDFIndexSearcher(boolean isServer, String index) {
		env = new QueryEnvironment();

		try {
			// env.addIndex("res" + File.separator + "experimental" +
			// File.separator + "events" + File.separator + "gtd_rand_corpus");
//			env.addServer("122.248.234.111:3259");
			if(isServer){
				env.addServer(index);
			}else{
				env.addIndex(index);
			}

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

	public String[] doSearch(String question) {
		try {
			ScoredExtentResult[] results = env.runQuery(question, 5);

			ParsedDocument[] documents = env.documents(results);

			if (documents.length == 0) {
				results = env.runQuery(question, 5);
			}

			String[] docno = env.documentMetadata(results, "docno");
			return docno;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void close() {
		try {
			env.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		RDFIndexSearcher is = new RDFIndexSearcher(true, "127.0.0.1:3259");
		try {
			String[] asdf = is.doSearch("USS Cole bombings");
			for (String s : asdf) {
				System.out.println(s);

			}

			is.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
