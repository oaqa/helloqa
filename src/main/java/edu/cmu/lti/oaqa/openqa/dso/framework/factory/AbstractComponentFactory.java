package edu.cmu.lti.oaqa.openqa.dso.framework.factory;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;
import edu.cmu.lti.oaqa.openqa.dso.util.LogUtil;

public abstract class AbstractComponentFactory {
  protected final Logger LOGGER = Logger.getLogger(LogUtil.getInvokingClassName());    

  public abstract IComponent create( JSONObject param ) throws JSONException;

  protected static String[] convertJSONArray( JSONArray jarray ) throws JSONException {
    String[] array = new String[jarray.length()];
    for (int i=0; i<jarray.length(); i++) {
      array[i] = jarray.getString(i);
    }
    return array;
  }

}
