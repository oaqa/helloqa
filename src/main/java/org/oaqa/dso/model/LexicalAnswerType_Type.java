
/* First created by JCasGen Thu Aug 15 13:53:20 EDT 2013 */
package org.oaqa.dso.model;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** The lexical answer type found in the question.
 * Updated by JCasGen Tue Aug 27 18:06:53 EDT 2013
 * @generated */
public class LexicalAnswerType_Type extends OAQAAnnotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (LexicalAnswerType_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = LexicalAnswerType_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new LexicalAnswerType(addr, LexicalAnswerType_Type.this);
  			   LexicalAnswerType_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new LexicalAnswerType(addr, LexicalAnswerType_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = LexicalAnswerType.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.oaqa.dso.model.LexicalAnswerType");
 
  /** @generated */
  final Feature casFeat_predicate;
  /** @generated */
  final int     casFeatCode_predicate;
  /** @generated */ 
  public int getPredicate(int addr) {
        if (featOkTst && casFeat_predicate == null)
      jcas.throwFeatMissing("predicate", "org.oaqa.dso.model.LexicalAnswerType");
    return ll_cas.ll_getRefValue(addr, casFeatCode_predicate);
  }
  /** @generated */    
  public void setPredicate(int addr, int v) {
        if (featOkTst && casFeat_predicate == null)
      jcas.throwFeatMissing("predicate", "org.oaqa.dso.model.LexicalAnswerType");
    ll_cas.ll_setRefValue(addr, casFeatCode_predicate, v);}
    
  
 
  /** @generated */
  final Feature casFeat_label;
  /** @generated */
  final int     casFeatCode_label;
  /** @generated */ 
  public String getLabel(int addr) {
        if (featOkTst && casFeat_label == null)
      jcas.throwFeatMissing("label", "org.oaqa.dso.model.LexicalAnswerType");
    return ll_cas.ll_getStringValue(addr, casFeatCode_label);
  }
  /** @generated */    
  public void setLabel(int addr, String v) {
        if (featOkTst && casFeat_label == null)
      jcas.throwFeatMissing("label", "org.oaqa.dso.model.LexicalAnswerType");
    ll_cas.ll_setStringValue(addr, casFeatCode_label, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public LexicalAnswerType_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_predicate = jcas.getRequiredFeatureDE(casType, "predicate", "org.oaqa.dso.model.Predicate", featOkTst);
    casFeatCode_predicate  = (null == casFeat_predicate) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_predicate).getCode();

 
    casFeat_label = jcas.getRequiredFeatureDE(casType, "label", "uima.cas.String", featOkTst);
    casFeatCode_label  = (null == casFeat_label) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_label).getCode();

  }
}



    