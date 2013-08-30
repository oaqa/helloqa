

/* First created by JCasGen Tue Aug 27 18:06:45 EDT 2013 */
package org.oaqa.dso.model;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Aug 27 18:06:53 EDT 2013
 * XML source: /home/ruil/workspace/git/helloqa/src/main/resources/edu/cmu/lti/oaqa/OAQATypes.xml
 * @generated */
public class AnswerGSList extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AnswerGSList.class);
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
  protected AnswerGSList() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public AnswerGSList(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public AnswerGSList(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public AnswerGSList(JCas jcas, int begin, int end) {
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
  //* Feature: gslist

  /** getter for gslist - gets 
   * @generated */
  public FSArray getGslist() {
    if (AnswerGSList_Type.featOkTst && ((AnswerGSList_Type)jcasType).casFeat_gslist == null)
      jcasType.jcas.throwFeatMissing("gslist", "org.oaqa.dso.model.AnswerGSList");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AnswerGSList_Type)jcasType).casFeatCode_gslist)));}
    
  /** setter for gslist - sets  
   * @generated */
  public void setGslist(FSArray v) {
    if (AnswerGSList_Type.featOkTst && ((AnswerGSList_Type)jcasType).casFeat_gslist == null)
      jcasType.jcas.throwFeatMissing("gslist", "org.oaqa.dso.model.AnswerGSList");
    jcasType.ll_cas.ll_setRefValue(addr, ((AnswerGSList_Type)jcasType).casFeatCode_gslist, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for gslist - gets an indexed value - 
   * @generated */
  public AnswerGS getGslist(int i) {
    if (AnswerGSList_Type.featOkTst && ((AnswerGSList_Type)jcasType).casFeat_gslist == null)
      jcasType.jcas.throwFeatMissing("gslist", "org.oaqa.dso.model.AnswerGSList");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AnswerGSList_Type)jcasType).casFeatCode_gslist), i);
    return (AnswerGS)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AnswerGSList_Type)jcasType).casFeatCode_gslist), i)));}

  /** indexed setter for gslist - sets an indexed value - 
   * @generated */
  public void setGslist(int i, AnswerGS v) { 
    if (AnswerGSList_Type.featOkTst && ((AnswerGSList_Type)jcasType).casFeat_gslist == null)
      jcasType.jcas.throwFeatMissing("gslist", "org.oaqa.dso.model.AnswerGSList");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AnswerGSList_Type)jcasType).casFeatCode_gslist), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AnswerGSList_Type)jcasType).casFeatCode_gslist), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    