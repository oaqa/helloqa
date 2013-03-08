package edu.cmu.lti.oaqa.openqa.hello.util;

import org.apache.solr.client.solrj.SolrServerException;

import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;


public class SolrProvider {
	
	private static final String SERVER = "";
    private static final Integer PORT = 0; 
    private static final Boolean EMBEDDED = true;
    private static final String CORE = "data/guten";
	private static final SolrWrapper SOLR_WRAPPER = createSolrWrapper();
	
	
	public static String getDocText(String id){
		try {
			return SOLR_WRAPPER.getDocText(id);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private static SolrWrapper createSolrWrapper(){
		try {
			return new SolrWrapper(SERVER,PORT,EMBEDDED,CORE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	

}
