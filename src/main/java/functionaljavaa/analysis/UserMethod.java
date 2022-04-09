/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.analysis;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.TblsData;
import databases.Token;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.sop.UserSop.userSopStatuses;
import functionaljavaa.user.UserProfile;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import lbplanet.utilities.LPPlatform.LpPlatformErrorTrapping;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
/**
 * Class for anything related to analysis user method
 * @author Fran Gomez
 */
public class UserMethod {
   String classVersion = "0.1";

    public enum UserMethodBusinessRules  {     
        CERTIFICATE_NOTASSIGNED ("userMethodCertificate_notAssigned", GlobalVariables.Schemas.CONFIG.getName()),
        CERTIFICATE_INACTIVE("userMethodCertificate_inactive", GlobalVariables.Schemas.CONFIG.getName()),
        CERTIFICATE_CERTIFIED("userMethodCertificate_certified", GlobalVariables.Schemas.CONFIG.getName()),
        ;
        private UserMethodBusinessRules(String tgName, String areaNm){
            this.tagName=tgName;
            this.areaName=areaNm;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        
        private final String tagName;
        private final String areaName;

    }
   
    /**
     *
     */
    public static final String TABLENAME_DATA_USER_METHOD="user_method";   

    /**
     *
     */
    public static final String FIELDNAME_DATA_USER_METHOD_ACTIVE="active";

    /**
     *
     */
    public static final String FIELDNAME_DATA_USER_METHOD_ANALYSIS="analysis";

    /**
     *
     */
    public static final String FIELDNAME_DATA_USER_METHOD_LAST_ANALYSIS_ON="last_training_on";
        
    /**
     *
     */
    public static final String FIELDNAME_DATA_USER_METHOD_LAST_TRAINING_ON="last_analysis_on";

    /**
     *
     */
    public static final String FIELDNAME_DATA_USER_METHOD_LAST_SAMPLE="last_sample";

    /**
     *
     */
    public static final String FIELDNAME_DATA_USER_METHOD_LAST_SAMPLE_ANALYSIS="last_sample_analysis";
    public static final String FIELDNAME_DATA_USER_METHOD_METHOD_NAME="method_name";

    /**
     *
     */
    public static final String FIELDNAME_DATA_USER_METHOD_METHOD_VERSION="method_version";

    /**
     *
     */
    public static final String FIELDNAME_DATA_USER_METHOD_TRAIN_INTERVAL="train_interval";

    /**
     *
     */
    public static final String FIELDNAME_DATA_USER_METHOD_USER_ID="user_id";

    /**
     *
     */
    public static final String FIELDNAME_DATA_USER_METHOD_USER_METHOD_ID="user_method_id";
        
        
 
/**
 * This function evaluate and return which is the current certification level for a given user and for one particular user method and version.
 *  It is considered: "Not assigned" when no records in table user_method found. // Inactive when found but expired // Certified when found and not expired.
 *  The specific values for all 3 values are configured in the parameter field for the procedure, the entries names are: 
 *  userMethodCertificate_notAssigned, userMethodCertificate_inactive, userMethodCertificate_certified.
 * Parameter Bundle: 
 *      config-userMethodCertificate_notAssigned, userMethodCertificate_inactive, userMethodCertificate_certified
 * @param procInstanceName String - Procedure name
 * @param analysis String - Analysis name
 * @param methodName String - Method Name
 * @param methodVersion Integer - Method version
 * @param userName String User name
 * @return String - The certification level
 */    
    public String userMethodCertificationLevel( String procInstanceName, String analysis, String methodName, Integer methodVersion, String userName){
        String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());  
        String schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());   
        
        String userMethodNotAssigned = Parameter.getBusinessRuleProcedureFile(procInstanceName, UserMethodBusinessRules.CERTIFICATE_NOTASSIGNED.getAreaName(), UserMethodBusinessRules.CERTIFICATE_NOTASSIGNED.getTagName());
        String userMethodInactive = Parameter.getBusinessRuleProcedureFile(procInstanceName, UserMethodBusinessRules.CERTIFICATE_INACTIVE.getAreaName(), UserMethodBusinessRules.CERTIFICATE_INACTIVE.getTagName());
        String userMethodCertified = Parameter.getBusinessRuleProcedureFile(procInstanceName, UserMethodBusinessRules.CERTIFICATE_CERTIFIED.getAreaName(), UserMethodBusinessRules.CERTIFICATE_CERTIFIED.getTagName());
        
        String[] whereFieldName = new String[]{FIELDNAME_DATA_USER_METHOD_USER_ID, FIELDNAME_DATA_USER_METHOD_ANALYSIS,
                FIELDNAME_DATA_USER_METHOD_METHOD_NAME, FIELDNAME_DATA_USER_METHOD_METHOD_VERSION};
        Object[] whereFieldValue = new Object[]{userName, analysis, methodName, methodVersion};
        String[] getFieldName = new String[]{FIELDNAME_DATA_USER_METHOD_ACTIVE, FIELDNAME_DATA_USER_METHOD_TRAIN_INTERVAL,
                FIELDNAME_DATA_USER_METHOD_LAST_TRAINING_ON, FIELDNAME_DATA_USER_METHOD_LAST_ANALYSIS_ON};
                
        Object[][] userMethodData = Rdbms.getRecordFieldsByFilter(schemaDataName, TABLENAME_DATA_USER_METHOD, whereFieldName, whereFieldValue, getFieldName);
        if (LPPlatform.LAB_FALSE.equals(userMethodData[0][0].toString())){return userMethodNotAssigned;}    
        
        Boolean userMethodActive = (Boolean) userMethodData[0][0];
        if (!userMethodActive){return userMethodInactive;}
        else{return userMethodCertified;}                
    }    

    /**
     *
     * @param analysis
     * @param methodName
     * @param methodVersion
     * @param sampleId
     * @param testId
     * @param preAuditId
     * @return
     */
    public static Object[] newUserMethodEntry(String analysis, String methodName, Integer methodVersion, Integer sampleId, Integer testId, Integer preAuditId){
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        String schemaDataName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());
        Object[] diagnoses = new Object[]{LPPlatform.LAB_FALSE};
        String[] whereFields = new String[]{UserMethod.FIELDNAME_DATA_USER_METHOD_USER_ID, FIELDNAME_DATA_USER_METHOD_ANALYSIS, 
            FIELDNAME_DATA_USER_METHOD_METHOD_NAME, FIELDNAME_DATA_USER_METHOD_METHOD_VERSION};
        Object[] whereFieldsValue = new Object[]{token.getUserName(), analysis, methodName, methodVersion};
        String[] updFields = new String[]{UserMethod.FIELDNAME_DATA_USER_METHOD_LAST_TRAINING_ON, UserMethod.FIELDNAME_DATA_USER_METHOD_LAST_SAMPLE, FIELDNAME_DATA_USER_METHOD_LAST_SAMPLE_ANALYSIS};
        Object[] updFieldsValue = new Object[]{Rdbms.getLocalDate(), sampleId, testId};
        Object[][] userMethodInfo;
        userMethodInfo = Rdbms.getRecordFieldsByFilter(schemaDataName, UserMethod.TABLENAME_DATA_USER_METHOD, whereFields, whereFieldsValue, 
                new String[]{FIELDNAME_DATA_USER_METHOD_USER_METHOD_ID, UserMethod.FIELDNAME_DATA_USER_METHOD_USER_ID, FIELDNAME_DATA_USER_METHOD_ANALYSIS, 
                    FIELDNAME_DATA_USER_METHOD_METHOD_NAME, FIELDNAME_DATA_USER_METHOD_METHOD_VERSION});
        if (!(LPPlatform.LAB_FALSE.equalsIgnoreCase(userMethodInfo[0][0].toString()))) {
            diagnoses = Rdbms.updateRecordFieldsByFilter(schemaDataName, UserMethod.TABLENAME_DATA_USER_METHOD, updFields, updFieldsValue, whereFields, whereFieldsValue);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) {
                updFields = LPArray.addValueToArray1D(updFields, whereFields);
                updFieldsValue = LPArray.addValueToArray1D(updFieldsValue, whereFieldsValue);
                SampleAudit smpAudit = new SampleAudit();
                smpAudit.sampleAuditAdd(SampleAudit.DataSampleAuditEvents.UPDATE_LAST_ANALYSIS_USER_METHOD, UserMethod.TABLENAME_DATA_USER_METHOD, 
                    testId, sampleId, testId, null, updFields, updFieldsValue);
            }
        }
        return diagnoses;        
    }
    
    public static final Object[][] getUserAnalysisMethodCerttifByProcess(String[] filterFieldName, Object[] filterFieldValue, String[] fieldsToReturn, String[] procInstanceName){                
        String viewName = TblsData.ViewsData.USER_AND_ANALYSISMETHOD_CERTIF_VIEW.getViewName();
       String DIAGNOSES_ERROR_CODE = "DIAGNOSES_ERROR_CODE";
        
        if (fieldsToReturn.length<=0){
            String[][] getUserProfileNEW = new String[1][2];
            getUserProfileNEW[0][0]=DIAGNOSES_ERROR_CODE;
            getUserProfileNEW[0][1]="No fields specified for fieldsToReturn";
            return getUserProfileNEW;}
                    
        if ((filterFieldName==null) || (filterFieldValue==null) || (procInstanceName==null)){
            String[][] getUserProfileNEW = new String[1][4];
            getUserProfileNEW[0][0]=DIAGNOSES_ERROR_CODE;
            getUserProfileNEW[0][1]="filterFieldName and/or filterFieldValue and/or procInstanceName are null and this is not expected";
            if (filterFieldName==null){getUserProfileNEW[0][2]="filterFieldName is null";}else{getUserProfileNEW[0][2]="filterFieldName="+Arrays.toString(filterFieldName);}
            if (filterFieldValue==null){getUserProfileNEW[0][3]="filterFieldValue is null";}else{getUserProfileNEW[0][3]="filterFieldValue="+Arrays.toString(filterFieldValue);}
            return getUserProfileNEW;}       
        int correctProcess=0;
        StringBuilder query = new StringBuilder(0);
        for(String currProcInstanceName: procInstanceName){ 
            Object[] viewExistInSchema= Rdbms.dbViewExists(currProcInstanceName, GlobalVariables.Schemas.DATA.getName(), viewName);
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(viewExistInSchema[0].toString())){
                correctProcess++;
                query.append("(select ");
                for(String fRet: fieldsToReturn){
                    if (fRet!=null && fRet.length()>0){
                        if ("procedure_name".equalsIgnoreCase(fRet))
                            query.append("'"+currProcInstanceName+"'").append(",");
                        else
                            query.append(fRet).append(",");
                    }
                }
                query.deleteCharAt(query.length() - 1);

                if (currProcInstanceName.contains(GlobalVariables.Schemas.DATA.getName())){
                    query.append(" from \"").append(currProcInstanceName).append("\".").append(viewName).append(" where 1=1");}
                else{query.append(" from \"").append(currProcInstanceName).append("-data\".").append(viewName).append(" where 1=1");}
                for(String fFN: filterFieldName){
                    query.append(" and ").append(fFN); 
                    if (!fFN.contains("null")){query.append("= ?");}
                }
                query.append(") union ");
            }else
                if (!"APP".equalsIgnoreCase(currProcInstanceName))
                    LPPlatform.saveMessageInDbErrorLog("", new Object[]{currProcInstanceName, GlobalVariables.Schemas.DATA.getName(), viewName}, 
                            new Object[]{viewName, viewName, "getUserAnalysisMethodCerttifByProcess", 290}, "view not exist in this given schema", new Object[0], currProcInstanceName);
        }       
        for (int i=0;i<6;i++){query.deleteCharAt(query.length() - 1);}
        
        
        Object[] filterFieldValueAllSchemas = new Object[filterFieldValue.length*correctProcess];
        Integer iFldValue=0;
        for(String sPref: procInstanceName){
            for(Object fVal: filterFieldValue){
                filterFieldValueAllSchemas[iFldValue]=fVal;    
                iFldValue++;
            }
        }               
        try{
            ResultSet res = Rdbms.prepRdQuery(query.toString(), filterFieldValueAllSchemas);         
            res.last();
            Integer numLines=res.getRow();
            if (numLines==0)return null;
                
            
            Integer numColumns=fieldsToReturn.length;
            res.first();
            Object[][] getUserProfileNEW=new Object[numLines][numColumns];
            for (Integer inumLines=0;inumLines<numLines;inumLines++){
                for (Integer inumColumns=0;inumColumns<numColumns;inumColumns++)
                    getUserProfileNEW[inumLines][inumColumns]=res.getObject(inumColumns+1);                
                res.next();
            }
            return getUserProfileNEW;                
        }catch(SQLException ex){
            Object[] trpErr=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LpPlatformErrorTrapping.SPECIALFUNCTION_CAUSEDEXCEPTION, new String[]{ex.getMessage()});
            return LPArray.array1dTo2d(trpErr, trpErr.length);            
        }
    }
    public static Object[][] getNotCertifAnaMethCertif( String userInfoId, String procInstanceNameName, String[] fieldsToRetrieve) {
        Object[] userSchemas = null;
        if (procInstanceNameName.contains("ALL")){
            UserProfile usProf = new UserProfile();
            userSchemas = usProf.getAllUserProcedurePrefix(userInfoId);
        }
        else{
            userSchemas = new String[1];
            userSchemas[0]=procInstanceNameName;
        }

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userSchemas[0].toString())){
            return LPArray.array1dTo2d(userSchemas, userSchemas.length);
        }
        String[] filterFieldName = new String[2];
        Object[] filterFieldValue = new Object[2];
        String[] fieldsToReturn = new String[0];

        filterFieldName[0]=TblsData.CertifUserAnalysisMethod.USER_NAME.getName();
        filterFieldValue[0]=userInfoId;
        filterFieldName[1]=TblsData.CertifUserAnalysisMethod.LIGHT.getName();
        filterFieldValue[1]=userSopStatuses.NOTPASS.getLightCode();
        if (fieldsToRetrieve!=null){            
            for (String fv: fieldsToRetrieve){
                if (!LPArray.valueInArray(fieldsToReturn, fv)){
                    fieldsToReturn = LPArray.addValueToArray1D(fieldsToReturn, fv);
                }
            }
        }else{
            fieldsToReturn = LPArray.addValueToArray1D(fieldsToReturn, TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName());
            fieldsToReturn = LPArray.addValueToArray1D(fieldsToReturn, TblsData.CertifUserAnalysisMethod.METHOD_VERSION.getName());
        }
        return getUserAnalysisMethodCerttifByProcess(filterFieldName, filterFieldValue, fieldsToReturn, (String[]) userSchemas);     
    }
    
    
}



