package edu.cmu.lti.oaqa.openqa.dso.xmiretriever;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.xml.sax.SAXException;

public class XmiCASRetriever {
	private String xmiServer = "http://dawn.isri.cmu.edu:8080/xmi/";
	private static String docPatternString = "^(.+)-([0-9_a-z]+?)$"; // DSO-1000
	private static String localDir = "xmirepo";
	private static String localDirFull = null;
	static {
		try {
			localDirFull = new File(localDir).getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private static Pattern docPattern = null;

	public XmiCASRetriever() {
		docPattern = Pattern.compile(docPatternString);
		try {
			localDirFull = new File(localDir).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean hasXmiResource(String setName, String docNo){
		try {
			URL xmiResource = getXmiResource(setName, docNo);
			File f = new File(xmiResource.toURI());
			return f.exists();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private static URL getXmiResource(String setName, String docNo)
			throws MalformedURLException {
		// local only
		return new URL("file:///" + URLEncoder.encode(localDirFull + File.separator + setName
				+ File.separator + "xmi" + File.separator + setName + "-" + docNo + ".xmi"));

	}

	private URL getTypeResource(String setName) throws MalformedURLException {
		
//		System.err.println("file:///" + localDirFull + File.separator + setName
//				+ File.separator + "type" + File.separator + "types.xml");
//		
		return new URL("file:///" + URLEncoder.encode(localDirFull + File.separator + setName
				+ File.separator + "type" + File.separator + "types.xml"));
		
			
	}

	public CAS retrieveCAS(String docID) {
		Matcher matcher = docPattern.matcher(docID);
		if (!matcher.matches())
			return null;
		String setName = matcher.group(1).toLowerCase();

		String docNo = matcher.group(2).toLowerCase();

		try {
			URL xmiURL = getXmiResource(setName, docNo);
			URL typeURL = getTypeResource(setName);
			XMLInputSource typeInput = new XMLInputSource(typeURL);
			
			TypeSystemDescription typeSystemDescription = UIMAFramework.getXMLParser().parseTypeSystemDescription(typeInput);
			
			CAS cas = CasCreationUtils.createCas(typeSystemDescription, null, null);
			
			XmiCasDeserializer.deserialize(xmiURL.openStream(), cas);
			
			return cas;
		} catch (FileNotFoundException e){
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		} catch (InvalidXMLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
