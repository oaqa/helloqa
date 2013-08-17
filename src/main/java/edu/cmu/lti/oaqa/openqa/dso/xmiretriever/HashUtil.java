package edu.cmu.lti.oaqa.openqa.dso.xmiretriever;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
	public static byte[] createHash(String text, String method) {
		try {
			byte[] b = text.getBytes();
			MessageDigest algorithm = MessageDigest.getInstance(method);
			algorithm.reset();
			algorithm.update(b);
			byte messageDigest[] = algorithm.digest();
			return messageDigest;
		} catch (NoSuchAlgorithmException nsae) {
			return null;
		}
		// Do nothing for it.

	}

	public static String getHash(String text) {
		try {
			byte[] b = createHash(text, "SHA-1");
			return asHex(b);
		} catch (Exception e) {
			return null;
			// Don't do anything else.
		}
	}

	public static String asHex(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

}
