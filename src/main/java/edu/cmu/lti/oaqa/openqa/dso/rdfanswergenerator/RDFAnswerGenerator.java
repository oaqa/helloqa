package edu.cmu.lti.oaqa.openqa.dso.rdfanswergenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;


public class RDFAnswerGenerator {
	private static String url = "jdbc:virtuoso://127.0.0.1:1111";
	private static final String cacheDir = "res/cache/rdf/".replace('/', File.separatorChar);
	private static final String ext = ".ttl";
	private Model model;
	private static final Logger LOGGER = Logger.getLogger(LogUtil
			.getInvokingClassName());
	
	public static Map<String, String[]> semanticTargetMap = new HashMap<String, String[]>();
	
	static {
		/**
		 * Create the cache dirs 
		 */
		new File(cacheDir).mkdirs();
		
		
		/**
		 * Mapping between semantic targets and rdf predicates
		 */
		semanticTargetMap.put("when-happened-date", new String[]{
				"http://dbpedia.org/property/date", 
				"http://dbpedia.org/property/Date", 
				"http://www.mpii.de/yago/resource/happenedOnDate", 
				"http://www.mpii.de/yago/resource/startedOnDate", 
				"http://www.mpii.de/yago/resource/endedOnDate"});
		
		semanticTargetMap.put("perpetrators", new String[]{
			"http://dbpedia.org/property/perp",
			"http://dbpedia.org/property/Perps"
		});
		
		semanticTargetMap.put("fatalities", new String[]{
				"http://dbpedia.org/property/fatalities",
				"http://dbpedia.org/property/Fatalities"
			});
		
		semanticTargetMap.put("injuries", new String[]{
				"http://dbpedia.org/property/injuries",
				"http://dbpedia.org/property/Injuries"
			});
		
		semanticTargetMap.put("target", new String[]{
				"http://dbpedia.org/property/target",
				"http://dbpedia.org/property/Target",
			});
		
		semanticTargetMap.put("location", new String[]{
				"http://dbpedia.org/property/location",
				"http://dbpedia.org/property/Location",
				"http://www.mpii.de/yago/resource/isLocatedIn"
			});
		
		semanticTargetMap.put("type", new String[]{
				"http://dbpedia.org/property/type",
				"http://dbpedia.org/property/Type"
			});
		
		semanticTargetMap.put("weapons", new String[]{
				"http://dbpedia.org/property/weapons",
				"http://dbpedia.org/property/Weapons"
			});
	}
	
	private String getCachedModelFilename(String eventName){
		return cacheDir + eventName + ext;
	}
	
	public boolean hasCachedModel(String eventName){
		File f = new File(getCachedModelFilename(eventName));
		return f.exists();
	}
	
	private Model loadCachedModel(String eventName){
		Model model = ModelFactory.createDefaultModel();
		InputStream in = FileManager.get().open(getCachedModelFilename(eventName));
		model.getReader("TURTLE").read(model, in, null);
		return model;
	}
	
	private void saveCachedModel(String eventName, Model model){
		try {
			model.getWriter("TURTLE").write(model, new FileOutputStream(getCachedModelFilename(eventName)), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public RDFAnswerGenerator(String sparqlServer, boolean isServer, String index, String eventName){
		url = sparqlServer;
		RDFIndexSearcher is = new RDFIndexSearcher(isServer, index);
		String[] resources = is.doSearch("#2(" + eventName + ")");
		
		if(resources == null || resources.length == 0)
			return;
		is.close();
		
		LOGGER.info("Identified resources: " + Arrays.toString(resources));
		if(eventName.trim().equals(""))
			return;
		if(hasCachedModel(eventName)){
			model = loadCachedModel(eventName);
			return;
		}
		String queryString = 
		"CONSTRUCT { ?o ?p ?s }" +
		"WHERE {" +
		"?o ?p ?s ." +
		"FILTER( ?o = <" + resources[0] + ">" +
		")" +
		"}";
		Query query = QueryFactory.create(queryString);
		QueryExecution execution = QueryExecutionFactory.sparqlService(sparqlServer, query);
		try {
			model = execution.execConstruct();
			printModel();
			saveCachedModel(eventName, model);
		}
		catch (RuntimeException e) {
			LOGGER.info("DBpedia parse error.");
		}
	}
	
	
	
	public List<String> getAnswers(String answerType){
		return null;
	}
	
	public List<String> getProperty(String property){
		List<String> properties = new ArrayList<String>();
		if(model == null) return properties;
		Property p = model.getProperty(property);
		NodeIterator objects = model.listObjectsOfProperty(p);
		while(objects.hasNext()){
			RDFNode object = objects.next();
			if(object.isLiteral())
				properties.add(object.asLiteral().getLexicalForm());
		}
		return properties;
	}
	
	public List<String> getPropertiesForTarget(String semanticTarget){
		List<String> properties = new ArrayList<String>();
		for(String property : semanticTargetMap.get(semanticTarget)){
			properties.addAll(getProperty(property));
		}
		return properties;
	}
	
	public void printSemanticTargets(){
		for(String target : semanticTargetMap.keySet()){
			System.out.println(target);
			
			for(String prop: getPropertiesForTarget(target)){
				System.out.println("\t" + prop);
			}
		}
	}
	
	public void printModel(){
		if(model == null) return;
		StmtIterator statements = model.listStatements();
		while(statements.hasNext()){
			Statement s = statements.next();
			System.out.println(s);
		}
	}
	
	public static void main(String args[]){
		RDFAnswerGenerator a = new RDFAnswerGenerator("http://dbpedia.org/sparql", false, "res/rdf_dbpedia_labels_indri_index", "Sbarro suicide bombing");
		a.printSemanticTargets();
		a.printModel();
	}
}
