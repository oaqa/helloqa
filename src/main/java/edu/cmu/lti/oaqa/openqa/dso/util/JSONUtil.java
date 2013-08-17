package edu.cmu.lti.oaqa.openqa.dso.util;

import org.json.JSONArray;
import org.json.JSONException;

public class JSONUtil {
	public static String[] convertJSONArray( JSONArray jarray ) throws JSONException {
	    String[] array = new String[jarray.length()];
	    for (int i=0; i<jarray.length(); i++) {
	      array[i] = jarray.getString(i);
	    }
	    return array;
	  }
}
