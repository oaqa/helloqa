
/* First created by JCasGen Tue Aug 27 18:06:45 EDT 2013 */
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
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Tue Aug 27 18:06:53 EDT 2013
 * @generated */
public class AnswerGSList_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (AnswerGSList_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = AnswerGSList_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new AnswerGSList(addr, AnswerGSList_Type.this);
  			   AnswerGSList_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new AnswerGSList(addr, AnswerGSList_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = AnswerGSList.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.oaqa.dso.model.AnswerGSList");
 
  /** @generated */
  final Feature casFeat_gslist;
  /** @generated */
  final int     casFeatCode_gslist;
  /** @generated */ 
  public int getGslist(int addr) {
        if (featOkTst && casFeat_gslist == null)
      jcas.throwFeatMissing("gslist", "org.oaqa.dso.model.AnswerGSList");
    return ll_cas.ll_getRefValue(addr, casFeatCode_gslist);
  }
  /** @generated */    
  public void setGslist(int addr, int v) {
        if (featOkTst && casFeat_gslist == null)
      jcas.throwFeatMissing("gslist", "org.oaqa.dso.model.AnswerGSList");
    ll_cas.ll_setRefValue(addr, casFeatCode_gslist, v);}
    
   /** @generated */
  public int getGslist(int addr, int i) {
        if (featOkTst && casFeat_gslist == null)
      jcas.throwFeatMissing("gslist", "org.oaqa.dso.model.AnswerGSList");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_gslist), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_gslist), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_gslist), i);
  }
   
  /** @generated */ 
  public void setGslist(int addr, int i, int v) {
        if (featOkTst && casFeat_gslist == null)
      jcas.throwFeatMissing("gslist", "org.oaqa.dso.model.AnswerGSList");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_gslist), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_gslist), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_gslist), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public AnswerGSList_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_gslist = jcas.getRequiredFeatureDE(casType, "gslist", "uima.cas.FSArray", featOkTst);
    casFeatCode_gslist  = (null == casFeat_gslist) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_gslist).getCode();

  }
}



    