package functionaljavaa.certification;

import static databases.Rdbms.dbTableGetFieldDefinition;
import databases.TblsData;
import databases.TblsData.CertifUserAnalysisMethod;
import java.util.HashMap;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDatabase;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntTableFields;
/**
 *
 * @author User
 */
public class CertifGlobalVariables {
    public enum CertifLight{GREEN, RED};
    public enum CertifStatuses{UNCERTIFIED, CERTIFIED, NOT_CERTIFIED, EXPIRED, REVOKED};
    
    public enum CertifEventUpdateFieldsAndValues{
        NEW_RECORD(new String[]{TblsData.CertifUserAnalysisMethod.LIGHT.getName(), TblsData.CertifUserAnalysisMethod.STATUS.getName(), 
                TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()}, 
            new Object[]{CertifLight.RED.toString(), CertifStatuses.UNCERTIFIED.toString(), false, false}),
        CERTIF_STARTED(new String[]{TblsData.CertifUserAnalysisMethod.LIGHT.getName(), TblsData.CertifUserAnalysisMethod.STATUS.getName(), 
                TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()}, 
            new Object[]{CertifLight.RED.toString(), CertifStatuses.UNCERTIFIED.toString(), true, false}),
        NOT_CERTIFIED(new String[]{TblsData.CertifUserAnalysisMethod.LIGHT.getName(), TblsData.CertifUserAnalysisMethod.STATUS.getName(), 
                TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()}, 
            new Object[]{CertifLight.GREEN.toString(), CertifStatuses.NOT_CERTIFIED.toString(), true, true}),
        CERTIFIED(new String[]{TblsData.CertifUserAnalysisMethod.LIGHT.getName(), TblsData.CertifUserAnalysisMethod.STATUS.getName(), 
                TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName(),
                TblsData.CertifUserAnalysisMethod.CERTIFICATION_DATE.getName()}, 
            new Object[]{CertifLight.GREEN.toString(), CertifStatuses.CERTIFIED.toString(), true, true, LPDate.getCurrentTimeStamp()}),
        EXPIRED(new String[]{TblsData.CertifUserAnalysisMethod.LIGHT.getName(), TblsData.CertifUserAnalysisMethod.STATUS.getName(), 
                TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()}, 
            new Object[]{CertifLight.RED.toString(), CertifStatuses.EXPIRED.toString(), false, false}),
        REVOKED(new String[]{TblsData.CertifUserAnalysisMethod.LIGHT.getName(), TblsData.CertifUserAnalysisMethod.STATUS.getName(), 
                TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()}, 
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
    
    public enum UserCertifTrackAuditEvents implements EnumIntAuditEvents{ 
        ASSIGNED_BY_USERROLE_ASSIGNMENT, 
    }    
    
    public static String getScriptToAddCertifToAnyObject(String procInstanceName, String schemaGroupName, String tableName){
        return getScriptToAddCertifToAnyObjectPostgres(procInstanceName, schemaGroupName, tableName);
    }
    private static String getScriptToAddCertifToAnyObjectPostgres(String procInstanceName, String schemaGroupName, String tableName){            
        String[] fields=new String[]{//TblsData.CertifUserAnalysisMethod.USER_ID.getName(),
        //TblsData.CertifUserAnalysisMethod.ASSIGNED_ON.getName(), TblsData.CertifUserAnalysisMethod.ASSIGNED_BY.getName(),
        //TblsData.CertifUserAnalysisMethod.STATUS.getName(), 
        TblsData.CertifUserAnalysisMethod.CERTIFICATION_DATE.getName(),
        TblsData.CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(),
        TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName(),// TblsData.CertifUserAnalysisMethod.SOP_NAME.getName(),
        //TblsData.CertifUserAnalysisMethod.USER_NAME.getName(), TblsData.CertifUserAnalysisMethod.LIGHT.getName(),
        TblsData.CertifUserAnalysisMethod.TRAINING_ID.getName()};

        HashMap<String[], Object[][]> dbTableGetFieldDefinition = dbTableGetFieldDefinition(LPPlatform.buildSchemaName(procInstanceName, schemaGroupName), tableName);

        String[] fldDefinitionColName= dbTableGetFieldDefinition.keySet().iterator().next();    
        Object[][] tableFldsInfo = dbTableGetFieldDefinition.get(fldDefinitionColName);
        //if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tableFldsInfo[0][0].toString())) return "";
        Object[] tableFldsInfoColumns = LPArray.getColumnFromArray2D(tableFldsInfo, LPArray.valuePosicInArray(fldDefinitionColName, "column_name"));
        StringBuilder tblAlterScript=new StringBuilder();
        
        for (EnumIntTableFields curFld: CertifUserAnalysisMethod.values()){
            if (!LPArray.valueInArray(tableFldsInfoColumns, curFld)){
                if (tblAlterScript.length()>0)tblAlterScript.append(", ");
                tblAlterScript.append(LPDatabase.addColumn()).append(" ").append(curFld.getName()).append(" ").append(curFld.getFieldType());
            }
        }
        if (tblAlterScript.toString().length()>0) return LPDatabase.alterTable()+" "+LPPlatform.buildSchemaName(procInstanceName, schemaGroupName)+"."+tableName+" "
                +tblAlterScript.toString()+";";
        return "";
    }
    
}
