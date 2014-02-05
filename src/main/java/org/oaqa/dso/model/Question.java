

/* First created by JCasGen Thu Aug 15 13:53:20 EDT 2013 */
package org.oaqa.dso.model;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



import org.oaqa.dso.model.gerp.GerpAnnotation;


/** The Question and any associated meta-data.
 * Updated by JCasGen Tue Feb 04 21:53:59 EST 2014
 * XML source: /home/ruil/workspace/git/helloqa/src/main/resources/edu/cmu/lti/oaqa/OAQATypes.xml
 * @generated */
public class Question extends GerpAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Question.class);
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
  protected Question() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Question(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Question(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Question(JCas jcas, int begin, int end) {
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
  //* Feature: id

  /** getter for id - gets A unique id for the question.
   * @generated */
  public String getId() {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "org.oaqa.dso.model.Question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Question_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets A unique id for the question. 
   * @generated */
  public void setId(String v) {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "org.oaqa.dso.model.Question");
    jcasType.ll_cas.ll_setStringValue(addr, ((Question_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: source

  /** getter for source - gets The source of the question, e.g., TREC11, Jeopardy, etc.
   * @generated */
  public String getSource() {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "org.oaqa.dso.model.Question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Question_Type)jcasType).casFeatCode_source);}
    
  /** setter for source - sets The source of the question, e.g., TREC11, Jeopardy, etc. 
   * @generated */
  public void setSource(String v) {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "org.oaqa.dso.model.Question");
    jcasType.ll_cas.ll_setStringValue(addr, ((Question_Type)jcasType).casFeatCode_source, v);}    
   
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets 
   * @generated */
  public String getText() {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "org.oaqa.dso.model.Question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Question_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets  
   * @generated */
  public void setText(String v) {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "org.oaqa.dso.model.Question");
    jcasType.ll_cas.ll_setStringValue(addr, ((Question_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: questionType

  /** getter for questionType - gets 
   * @generated */
  public String getQuestionType() {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_questionType == null)
      jcasType.jcas.throwFeatMissing("questionType", "org.oaqa.dso.model.Question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Question_Type)jcasType).casFeatCode_questionType);}
    
  /** setter for questionType - sets  
   * @generated */
  public void setQuestionType(String v) {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_questionType == null)
      jcasType.jcas.throwFeatMissing("questionType", "org.oaqa.dso.model.Question");
    jcasType.ll_cas.ll_setStringValue(addr, ((Question_Type)jcasType).casFeatCode_questionType, v);}    
  }

    