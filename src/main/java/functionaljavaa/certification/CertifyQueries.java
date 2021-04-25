/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.certification;

import databases.Rdbms;
import databases.TblsData;
import databases.TblsDataAudit;
import functionaljavaa.certification.AnalysisMethodCertif.CertificationAnalysisMethodBusinessRules;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.requirement.ProcedureDefinitionQueries.ProcBusinessRulesQueries;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class CertifyQueries {
    
    public enum CertifObjects{
        ANALYSIS_METHOD(CertificationAnalysisMethodBusinessRules.CERTIFICATION_ANALYSIS_METHOD_MODE.getTagName(), 
                TblsData.CertifUserAnalysisMethod.TBL.getName(),
                new String[]{TblsData.CertifUserAnalysisMethod.FLD_ID.getName(), TblsData.CertifUserAnalysisMethod.FLD_METHOD_NAME.getName(), 
                    TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), 
                    TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()},
                TblsDataAudit.CertifUserAnalysisMethod.getAllFieldNames()),
        USER_SOP(ProcBusinessRulesQueries.PROCEDURE_USER_SOP_CERTIFICATION_LEVEL.getPropertiesSectionName(), 
                TblsData.UserSop.TBL.getName(),
                new String[]{TblsData.UserSop.FLD_SOP_ID.getName(), TblsData.UserSop.FLD_SOP_NAME.getName(), 
                    TblsData.CertifUserAnalysisMethod.FLD_USER_NAME.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), 
                    TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()},
                TblsDataAudit.CertifUserAnalysisMethod.getAllFieldNames())
        ;
        private CertifObjects(String propName, String tblName, String[] fieldsToGet, String[] auditFieldsToGet){
            this.propertyName=propName;
            this.tableName=tblName;
            this.fieldsToGet=fieldsToGet;
            this.auditFieldsToGet=auditFieldsToGet;
        }     
        private final String propertyName;
        private final String tableName; 
        private final String[] fieldsToGet; 
        private final String[] auditFieldsToGet; 
        public String getPropertyName(){return this.propertyName;}
        public String getTableName(){return this.tableName;}
        public String[] getFieldsToGet(){return this.fieldsToGet;}
        public String[] getAuditFieldsToGet(){return this.auditFieldsToGet;}
    };
    
    public static JSONArray objectsUponCertificationProcedure(Boolean includeOnlyEnabled){
        JSONArray jGlobalArr=new JSONArray();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();  
        for (CertifObjects curCertifObj: CertifObjects.values()){                        
            Boolean tagValueOneOfEnableOnes = false;
            String tagValue = Parameter.getMessageCodeValue("config", procInstanceName, 
                GlobalVariables.Schemas.PROCEDURE.getName().toLowerCase(), curCertifObj.getPropertyName(), null);
            tagValueOneOfEnableOnes = Parameter.isTagValueOneOfEnableOnes(tagValue);
            if (!includeOnlyEnabled || tagValueOneOfEnableOnes){
                JSONObject jObj=new JSONObject();
                jObj.put("table", curCertifObj.getTableName());
                jObj.put("business_rule_to_enable_id", curCertifObj.getPropertyName());
                jObj.put("business_rule_value", tagValue);
                jGlobalArr.add(jObj);
            }
        }
        return jGlobalArr;
    }
    
    public static JSONArray CertificationsInProgress(String areasToInclude, Boolean includeAuditHistory){
        String[] fldsName=new String[]{TblsData.CertifUserAnalysisMethod.FLD_LIGHT.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.FLD_CERTIF_COMPLETED.getName()};
        Object[] fldsValue=new Object[]{CertifGlobalVariables.CertifLight.RED.toString(), true, false};
        return CertificationsHistory(areasToInclude, fldsName, fldsValue, includeAuditHistory);
    }
    
    public static JSONArray CertificationsHistory(String areasToInclude, String[] fldsName, Object[] fldsValue, Boolean includeAuditHistory){
        JSONArray jGlobalArr=new JSONArray();
        String[] areasToIncludeArr=areasToInclude.split("\\|");
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();  
        for (CertifObjects curCertifObj: CertifObjects.values()){
            String[] fieldsToGet=curCertifObj.getFieldsToGet();
            if (includeAuditHistory!=null && includeAuditHistory){
                if (!LPArray.valueInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.FLD_ID.getName()))
                    fieldsToGet=LPArray.addValueToArray1D(fieldsToGet, TblsData.CertifUserAnalysisMethod.FLD_ID.getName());
            }
            if ("ALL".equalsIgnoreCase(areasToInclude) || LPArray.valueInArray(areasToIncludeArr, curCertifObj.toString())){
                String tagValue = Parameter.getMessageCodeValue("config", procInstanceName, 
                        GlobalVariables.Schemas.PROCEDURE.getName().toLowerCase(), curCertifObj.getPropertyName(), null);
                if (Parameter.isTagValueOneOfEnableOnes(tagValue)){
                    Object[][] certifRowExpDateInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), curCertifObj.getTableName(), 
                        fldsName, fldsValue, fieldsToGet,
                        new String[]{TblsData.CertifUserAnalysisMethod.FLD_CERTIF_EXPIRY_DATE.getName()});
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())){
                        JSONArray jCertifObjArr=new JSONArray();
                        for (Object[] curRow: certifRowExpDateInfo){
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(curCertifObj.getFieldsToGet(), curRow);
                            if (includeAuditHistory!=null && includeAuditHistory){
                                Object[][] certifRowAuditInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA_AUDIT.getName()), curCertifObj.getTableName(), 
                                    new String[]{TblsDataAudit.CertifUserAnalysisMethod.FLD_CERTIF_ID.getName()},
                                    new Object[]{curRow[LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.FLD_ID.getName())]},
                                    curCertifObj.getAuditFieldsToGet(), 
                                    new String[]{TblsDataAudit.CertifUserAnalysisMethod.FLD_AUDIT_ID.getName()});
                                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowAuditInfo[0][0].toString())){
                                    JSONArray jCertifAuditObjArr=new JSONArray();
                                    for (Object[] curAuditRow: certifRowAuditInfo)
                                        jCertifAuditObjArr.add(LPJson.convertArrayRowToJSONObject(curCertifObj.getAuditFieldsToGet(), curAuditRow));
                                    jObj.put("audit", jCertifAuditObjArr);
                                }else
                                    jObj.put("audit", "nothing");
                            }
                            jCertifObjArr.add(jObj);
                        }
                        JSONObject jCertifObjObj=new JSONObject();
                        jCertifObjObj.put(curCertifObj.toString().toLowerCase(), jCertifObjArr);
                        jGlobalArr.add(jCertifObjObj);
                    }
                }
            }
        }
        return jGlobalArr;
    }
}
