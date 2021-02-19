package functionaljavaa.certification;

import databases.TblsData;
import lbplanet.utilities.LPDate;
/**
 *
 * @author User
 */
public class CertifGlobalVariables {
    public enum CertifLight{GREEN, RED};
    public enum CertifStatuses{UNCERTIFIED, CERTIFIED, NOT_CERTIFIED, EXPIRED, REVOKED};
    
    public enum CertifEventUpdateFieldsAndValues{
        NEW_RECORD(new String[]{TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_STATUS.getName(), 
                TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()}, 
            new Object[]{CertifLight.RED.toString(), CertifStatuses.UNCERTIFIED.toString(), false, false}),
        CERTIF_STARTED(new String[]{TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_STATUS.getName(), 
                TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()}, 
            new Object[]{CertifLight.RED.toString(), CertifStatuses.UNCERTIFIED.toString(), true, false}),
        NOT_CERTIFIED(new String[]{TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_STATUS.getName(), 
                TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()}, 
            new Object[]{CertifLight.GREEN.toString(), CertifStatuses.NOT_CERTIFIED.toString(), true, true}),
        CERTIFIED(new String[]{TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_STATUS.getName(), 
                TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName(),
                TblsData.CertifUserAnalysisMethod.FLD_CERTIFICATION_DATE.getName()}, 
            new Object[]{CertifLight.GREEN.toString(), CertifStatuses.CERTIFIED.toString(), true, true, LPDate.getCurrentTimeStamp()}),
        EXPIRED(new String[]{TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_STATUS.getName(), 
                TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()}, 
            new Object[]{CertifLight.RED.toString(), CertifStatuses.EXPIRED.toString(), false, false}),
        REVOKED(new String[]{TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_STATUS.getName(), 
                TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()}, 
            new Object[]{CertifLight.RED.toString(), CertifStatuses.REVOKED.toString(), false, false}),

        ;
        private CertifEventUpdateFieldsAndValues(String[] tgName, Object[] areaNm){
            this.fieldsName=tgName;
            this.fieldsValue=areaNm;
        }       
        public String[] getFieldsName(){return this.fieldsName;}
        public Object[] getFieldsValue(){return this.fieldsValue;}
        
        private final String[] fieldsName;
        private final Object[] fieldsValue;        
    }
}
