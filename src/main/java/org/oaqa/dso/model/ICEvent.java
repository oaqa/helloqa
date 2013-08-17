

/* First created by JCasGen Thu Aug 15 13:53:20 EDT 2013 */
package org.oaqa.dso.model;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Thu Aug 15 13:53:20 EDT 2013
 * XML source: /home/ruil/workspace/git/helloqa/descriptors/OAQATypes.xml
 * @generated */
public class ICEvent extends OAQAAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ICEvent.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ICEvent() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ICEvent(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ICEvent(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ICEvent(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: label

  /** getter for label - gets 
   * @generated */
  public String getLabel() {
    if (ICEvent_Type.featOkTst && ((ICEvent_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "org.oaqa.dso.model.ICEvent");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ICEvent_Type)jcasType).casFeatCode_label);}
    
  /** setter for label - sets  
   * @generated */
  public void setLabel(String v) {
    if (ICEvent_Type.featOkTst && ((ICEvent_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "org.oaqa.dso.model.ICEvent");
    jcasType.ll_cas.ll_setStringValue(addr, ((ICEvent_Type)jcasType).casFeatCode_label, v);}    
  }

    