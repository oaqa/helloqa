package edu.cmu.lti.oaqa.openqa.dso.util;

public class LogUtil {
  
  public static String getInvokingClassName() {
    return new Throwable().getStackTrace()[1].getClassName();
  }
  
}
