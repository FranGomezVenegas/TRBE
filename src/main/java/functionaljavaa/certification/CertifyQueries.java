/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.certification;

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
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntTables;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class CertifyQueries {
    
    public enum CertifObjects{
        ANALYSIS_METHOD(CertificationAnalysisMethodBusinessRules.CERTIFICATION_ANALYSIS_METHOD_MODE.getTagName(), 
                TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD,
                new String[]{TblsData.CertifUserAnalysisMethod.ID.getName(), TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), 
                    TblsData.CertifUserAnalysisMethod.USER_NAME.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), 
                    TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()},
                getAllFieldNames(TblsDataAudit.TablesDataAudit.CERTIF_USER_ANALYSIS_METHOD.getTableFields())),
        USER_SOP(ProcBusinessRulesQueries.PROCEDURE_USER_SOP_CERTIFICATION_LEVEL.getPropertiesSectionName(), 
                TblsData.TablesData.USER_SOP,
                new String[]{TblsData.UserSop.SOP_ID.getName(), TblsData.UserSop.SOP_NAME.getName(), 
                    TblsData.CertifUserAnalysisMethod.USER_NAME.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), 
                    TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()},
                getAllFieldNames(TblsDataAudit.TablesDataAudit.CERTIF_USER_ANALYSIS_METHOD.getTableFields()))
        ;
        private CertifObjects(String propName, EnumIntTables tblObj, String[] fieldsToGet, String[] auditFieldsToGet){
            this.propertyName=propName;
            this.table=tblObj;
            this.fieldsToGet=fieldsToGet;
            this.auditFieldsToGet=auditFieldsToGet;
        }     
        private final String propertyName;
        private final EnumIntTables table; 
        private final String[] fieldsToGet; 
        private final String[] auditFieldsToGet; 
        public String getPropertyName(){return this.propertyName;}
        public EnumIntTables getTable(){return this.table;}
        public String[] getFieldsToGet(){return this.fieldsToGet;}
        public String[] getAuditFieldsToGet(){return this.auditFieldsToGet;}
    };
    
    public static JSONArray objectsUponCertificationProcedure(Boolean includeOnlyEnabled){
        JSONArray jGlobalArr=new JSONArray();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();  
        for (CertifObjects curCertifObj: CertifObjects.values()){                        
            String tagValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName().toLowerCase(), curCertifObj.getPropertyName());
            Boolean tagValueOneOfEnableOnes = Parameter.isTagValueOneOfEnableOnes(tagValue);
            if (Boolean.FALSE.equals(includeOnlyEnabled) || Boolean.TRUE.equals(tagValueOneOfEnableOnes)){
                JSONObject jObj=new JSONObject();
                jObj.put("table", curCertifObj.getTable().getTableName());
                jObj.put("business_rule_to_enable_id", curCertifObj.getPropertyName());
                jObj.put("business_rule_value", tagValue);
                jGlobalArr.add(jObj);
            }
        }
        return jGlobalArr;
    }
    
    public static JSONArray certificationsInProgress(String areasToInclude, Boolean includeAuditHistory){
        String[] fldsName=new String[]{TblsData.CertifUserAnalysisMethod.LIGHT.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_STARTED.getName(), TblsData.CertifUserAnalysisMethod.CERTIF_COMPLETED.getName()};
        Object[] fldsValue=new Object[]{CertifGlobalVariables.CertifLight.RED.toString(), true, false};
        return certificationsHistory(areasToInclude, fldsName, fldsValue, includeAuditHistory);
    }
    
    public static JSONArray certificationsHistory(String areasToInclude, String[] fldsName, Object[] fldsValue, Boolean includeAuditHistory){
        JSONArray jGlobalArr=new JSONArray();
        String[] areasToIncludeArr=areasToInclude.split("\\|");
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();  
        for (CertifObjects curCertifObj: CertifObjects.values()){
            String[] fieldsToGet=curCertifObj.getFieldsToGet();
            if (includeAuditHistory!=null && includeAuditHistory){
                if (Boolean.FALSE.equals(LPArray.valueInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.ID.getName()))){
                    fieldsToGet=LPArray.addValueToArray1D(fieldsToGet, TblsData.CertifUserAnalysisMethod.ID.getName());
                }    
            }
            if ("ALL".equalsIgnoreCase(areasToInclude) || LPArray.valueInArray(areasToIncludeArr, curCertifObj.toString())){
                String tagValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, 
                        GlobalVariables.Schemas.PROCEDURE.getName().toLowerCase(), curCertifObj.getPropertyName());
                if (Parameter.isTagValueOneOfEnableOnes(tagValue)){
                    Object[][] certifRowExpDateInfo=QueryUtilitiesEnums.getTableData(curCertifObj.getTable(),
                        EnumIntTableFields.getTableFieldsFromString(curCertifObj.getTable(), fieldsToGet),
                        fldsName, fldsValue, new String[]{TblsData.CertifUserAnalysisMethod.CERTIF_EXPIRY_DATE.getName()});
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(certifRowExpDateInfo[0][0].toString())){
                        JSONArray jCertifObjArr=new JSONArray();
                        for (Object[] curRow: certifRowExpDateInfo){
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(curCertifObj.getFieldsToGet(), curRow);
                            if (includeAuditHistory!=null && includeAuditHistory){
                                Object[][] certifRowAuditInfo = QueryUtilitiesEnums.getTableData(curCertifObj.getTable(), 
                                    EnumIntTableFields.getTableFieldsFromString(curCertifObj.getTable(), curCertifObj.getAuditFieldsToGet()),
                                    new String[]{TblsDataAudit.CertifUserAnalysisMethod.CERTIF_ID.getName()},
                                    new Object[]{curRow[LPArray.valuePosicInArray(fieldsToGet, TblsData.CertifUserAnalysisMethod.ID.getName())]},
                                    new String[]{TblsDataAudit.CertifUserAnalysisMethod.AUDIT_ID.getName()});
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
