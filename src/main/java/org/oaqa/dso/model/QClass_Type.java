
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

/** The class of the question, determined by an automatic question classification process.
 * Updated by JCasGen Thu Jan 16 23:10:26 EST 2014
 * @generated */
public class QClass_Type extends OAQAAnnotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (QClass_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = QClass_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new QClass(addr, QClass_Type.this);
  			   QClass_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new QClass(addr, QClass_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = QClass.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.oaqa.dso.model.QClass");
 
  /** @generated */
  final Feature casFeat_qClass;
  /** @generated */
  final int     casFeatCode_qClass;
  /** @generated */ 
  public String getQClass(int addr) {
        if (featOkTst && casFeat_qClass == null)
      jcas.throwFeatMissing("qClass", "org.oaqa.dso.model.QClass");
    return ll_cas.ll_getStringValue(addr, casFeatCode_qClass);
  }
  /** @generated */    
  public void setQClass(int addr, String v) {
        if (featOkTst && casFeat_qClass == null)
      jcas.throwFeatMissing("qClass", "org.oaqa.dso.model.QClass");
    ll_cas.ll_setStringValue(addr, casFeatCode_qClass, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public QClass_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_qClass = jcas.getRequiredFeatureDE(casType, "qClass", "org.oaqa.dso.model.QuestionClass", featOkTst);
    casFeatCode_qClass  = (null == casFeat_qClass) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_qClass).getCode();

  }
}



    