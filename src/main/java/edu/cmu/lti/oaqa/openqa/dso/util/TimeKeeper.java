package edu.cmu.lti.oaqa.openqa.dso.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for storing time took for a process.
 * 
 * @author hideki
 *
 */
public class TimeKeeper {

  private Set<String> qids = new LinkedHashSet<String>();
  // { componentId: { qid: process_time } }
  private Map<String,Map<String,Long>> startTime = new LinkedHashMap<String,Map<String,Long>>();
  private Map<String,Map<String,Integer>> processTime = new LinkedHashMap<String,Map<String,Integer>>();
  
  private static volatile TimeKeeper instance;
  
  private String beginDate;
  
  public static TimeKeeper getInstance() {
    
    synchronized (TimeKeeper.class) {
      if (instance == null)
        instance = new TimeKeeper();
    }
    return instance;
  }
  
  private TimeKeeper() {
    beginDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
  }
  
  public static StopWatch createStopWatch( String qid, String componentId ) {
    return new StopWatch( TimeKeeper.getInstance(), qid, componentId );
  }
  
  void start( String qid, String componentId ) {
    qids.add(qid);
    Map<String,Long> qidStartTime = startTime.get(componentId);
    if (qidStartTime==null) qidStartTime = new LinkedHashMap<String,Long>();
    qidStartTime.put(qid, System.currentTimeMillis());
    startTime.put(componentId, qidStartTime);
  }
  
  void end( String qid, String componentId ) {
    Map<String,Integer> qidProcessTime = processTime.get(componentId);
    if (qidProcessTime==null) qidProcessTime = new LinkedHashMap<String,Integer>();
    long start = startTime.get(componentId).get(qid);
    qidProcessTime.put(qid, (int)(System.currentTimeMillis()-start));
    processTime.put(componentId, qidProcessTime);
  }

  /**
   * @return the processTime
   */
  public Map<String, Map<String, Integer>> getProcessTimeMap() {
    return processTime;
  }
  
  public int getProcessTime( String qid, String componentId ) {
    return processTime.get(componentId).get(qid);
  }

  /**
   * @return the beginDate
   */
  public String getBeginDate() {
    return beginDate;
  }

  /**
   * @return the qids
   */
  public Set<String> getQids() {
    return qids;
  }
  
  public static class StopWatch {
    private TimeKeeper timeKeeper;
    private String qid;
    private String componentId;
    
    public StopWatch( TimeKeeper timeKeeper,
            String qid, String componentId ) {
      this.timeKeeper = timeKeeper;
      this.qid = qid;
      this.componentId = componentId;
    }
    
    public void start() {
      timeKeeper.start(qid, componentId);
    }
    
    public void stop() {
      timeKeeper.end(qid, componentId);
    }
  }

}
