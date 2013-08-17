package edu.cmu.lti.oaqa.openqa.dso.xmiretriever.infobox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.CacheRequest;
import java.util.List;
import java.util.Map;

class MyCacheRequest extends CacheRequest {
	FileOutputStream fos;
	private File file;
	private boolean isError = false;

	public MyCacheRequest(String filename, Map<String, List<String>> rspHeaders) {
		try {
			file = new File(filename);
			fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(rspHeaders);
		} catch (Exception ex) {
			ex.printStackTrace();
			isError = true;
		}
	}
	
	

	public boolean isError() {
		return isError;
	}



	public OutputStream getBody() throws IOException {
		return fos;
	}

	public void abort() {
		// we abandon the cache by close the stream,
		// and delete the file
		try {
			fos.close();
			file.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}