package edu.cmu.lti.oaqa.openqa.dso.xmiretriever.infobox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.CacheResponse;
import java.util.List;
import java.util.Map;

class MyCacheResponse extends CacheResponse {
	FileInputStream fis;
	Map<String, List<String>> headers;
	boolean isError = false;
	public MyCacheResponse(String filename) {
		try {
			fis = new FileInputStream(new File(filename));
			ObjectInputStream ois = new ObjectInputStream(fis);
			headers = (Map<String, List<String>>) ois.readObject();
		} catch (Exception ex) {
			isError = true;
		}
	}
	
	public boolean getIsError(){
		return isError;
	}

	public InputStream getBody() throws IOException {
		return fis;
	}

	public Map getHeaders() throws IOException {
		return headers;
	}
}