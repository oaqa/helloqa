

/* First created by JCasGen Thu Aug 15 13:53:20 EDT 2013 */
package org.oaqa.dso.model;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** The class of the question, determined by an automatic question classification process.
 * Updated by JCasGen Tue Feb 04 22:31:41 EST 2014
 * XML source: /home/ruil/workspace/git/helloqa/src/main/resources/edu/cmu/lti/oaqa/OAQATypes.xml
 * @generated */
public class QClass extends OAQAAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(QClass.class);
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
  protected QClass() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public QClass(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public QClass(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public QClass(JCas jcas, int begin, int end) {
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
  //* Feature: qClass

  /** getter for qClass - gets The kind (class) of the question.
   * @generated */
  public String getQClass() {
    if (QClass_Type.featOkTst && ((QClass_Type)jcasType).casFeat_qClass == null)
      jcasType.jcas.throwFeatMissing("qClass", "org.oaqa.dso.model.QClass");
    return jcasType.ll_cas.ll_getStringValue(addr, ((QClass_Type)jcasType).casFeatCode_qClass);}
    
  /** setter for qClass - sets The kind (class) of the question. 
   * @generated */
  public void setQClass(String v) {
    if (QClass_Type.featOkTst && ((QClass_Type)jcasType).casFeat_qClass == null)
      jcasType.jcas.throwFeatMissing("qClass", "org.oaqa.dso.model.QClass");
    jcasType.ll_cas.ll_setStringValue(addr, ((QClass_Type)jcasType).casFeatCode_qClass, v);}    
  }

    