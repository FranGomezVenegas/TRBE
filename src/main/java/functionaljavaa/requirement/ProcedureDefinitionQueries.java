/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement;

import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsCnfg;
import databases.TblsProcedure;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.samplestructure.DataSampleStages.SampleStageBusinessRules;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lbplanet.utilities.LPArray;
import static lbplanet.utilities.LPJson.convertArrayRowToJSONObject;
import lbplanet.utilities.LPKPIs;
import lbplanet.utilities.LPPlatform;
import static lbplanet.utilities.LPPlatform.CONFIG_PROC_CONFIG_FILE_NAME;
import static lbplanet.utilities.LPPlatform.CONFIG_PROC_DATA_FILE_NAME;
import static lbplanet.utilities.LPPlatform.CONFIG_PROC_FILE_NAME;
import lbplanet.utilities.LPPlatform.LpPlatformBusinessRules;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class ProcedureDefinitionQueries {
    public enum ProcBusinessRulesQueries{
        PROCEDURE_MAIN_INFO(true, "PROCEDURE_MAIN_INFO", new String[]{"procedureName", "procedureVersion", "procedureRevision", "fileDeployment", "app"}, new String[]{CONFIG_PROC_FILE_NAME}, "", new Class[]{}, ""),
        PROCEDURE_ACTIONS_AND_ROLES(false, "PROCEDURE_ACTIONS_AND_ROLES", new String[]{}, new String[]{}, "procedureActionsAndRoles", new Class[]{String.class, ProcBusinessRulesQueries.class, JSONObject.class}, "procedure_events"),
        PROCEDURE_SAMPLE_AUDIT_LEVEL(true, "PROCEDURE_SAMPLE_AUDIT_LEVEL", new String[]{"sampleAuditRevisionMode", "sampleAuditChildRevisionRequired", "sampleAuditAuthorCanBeReviewerToo"}, new String[]{CONFIG_PROC_FILE_NAME}, "", new Class[]{}, ""),
        PROCEDURE_USER_SOP_CERTIFICATION_LEVEL(true, "PROCEDURE_USER_SOP_CERTIFICATION_LEVEL", new String[]{"actionEnabledUserSopCertification", "windowOpenableUserSopCertification"}, new String[]{CONFIG_PROC_FILE_NAME}, "allProcSops", new Class[]{String.class, ProcBusinessRulesQueries.class, JSONObject.class}, "procedure_all_sops"),
        PROGRAM_CORRECTIVE_ACTION(true, "PROGRAM_CORRECTIVE_ACTION", new String[]{"programCorrectiveActionMode", "sampleActionWhenUponControlMode", "sampleActionWhenOOSMode"}, new String[]{CONFIG_PROC_FILE_NAME}, "", new Class[]{}, ""),
        CHANGE_OF_CUSTODY(true, "CHANGE_OF_CUSTODY", new String[]{"changeOfCustodyObjects"}, new String[]{CONFIG_PROC_FILE_NAME}, "", new Class[]{}, ""),
        SAMPLE_STAGES_TIMING_CAPTURE(true, "SAMPLE_STAGES_TIMING_CAPTURE", new String[]{"sampleStagesTimingCaptureMode", "sampleStagesTimingCaptureStages"}, new String[]{CONFIG_PROC_FILE_NAME}, "", new Class[]{}, ""),
        SAMPLE_INCUBATION(true, "SAMPLE_INCUBATION", new String[]{"sampleIncubationMode", "incubationBatch_startMultipleInParallelPerIncubator"}, new String[]{CONFIG_PROC_FILE_NAME}, "sampleIncubation", new Class[]{String.class, ProcBusinessRulesQueries.class, JSONObject.class}, "incubation_rules"),
        PROCEDURE_ALL_PROC_USERS_ROLES(false, "PROCEDURE_ALL_PROC_USERS_ROLES", new String[]{}, new String[]{}, "allProcUsersRoles", new Class[]{String.class, ProcBusinessRulesQueries.class, JSONObject.class}, ""),
        PROCEDURE_SAMPLE_STAGES(true, "PROCEDURE_SAMPLE_STAGES", new String[]{"sampleStagesMode", "sampleStagesLogicType", "sampleStagesFirst"}, new String[]{CONFIG_PROC_FILE_NAME, CONFIG_PROC_DATA_FILE_NAME}, "sampleStages", new Class[]{String.class, ProcBusinessRulesQueries.class, JSONObject.class}, "stages_detail"),
        PROCEDURE_ENCRYPTION_TABLES_AND_FIELDS(false, "PROCEDURE_ENCRYPTION_TABLES_AND_FIELDS", new String[]{}, new String[]{}, "encryption", new Class[]{String.class, ProcBusinessRulesQueries.class, JSONObject.class}, "detail"),
    ;        
    private ProcBusinessRulesQueries(Boolean includeRunAttributesToJsObj, String propSectionN, String[] propertiesLst, String[] fileNameSuf, String methodN, Class[] methodParamClss, String methodSectionN){
        this.includeRunAttributesToJsonObj=includeRunAttributesToJsObj;
        this.propertiesSectionName=propSectionN;
        this.propertiesList=propertiesLst;
        this.fileNameSuffix=fileNameSuf;        
        this.methodName=methodN;
        this.methodParamClass=methodParamClss;
        this.methodSectionName=methodSectionN;
    }       
    public Boolean getIncludeRunAttributesToJsonObj(){return this.includeRunAttributesToJsonObj;}
    public String getPropertiesSectionName(){return this.propertiesSectionName;}
    public String[] getPropertiesList(){return this.propertiesList;}
    public String[] getFileNameSuffix(){return this.fileNameSuffix;}
    public Class[] getMethodParamClass(){return this.methodParamClass;}
    public String getMethodName(){return this.methodName;}
    public String getMethodSectionName(){return this.methodSectionName;}

    private final Boolean includeRunAttributesToJsonObj;
    private final String propertiesSectionName;
    private final String[] propertiesList;    
    private final String[] fileNameSuffix;
    private final Class[] methodParamClass;    
    private final String methodName;
    private final String methodSectionName;
    }

    private static final String     PROC_DISP_PROC_INSTC_SOPS_FLD_NAME="sop_id|sop_name";
    private static final String     PROC_DISP_PROC_INSTC_SOPS_SORT="sop_id";
    
    public static JSONObject getProcBusinessRulesQueriesInfo(String procInstanceName, String sectionName){
        JSONObject mainObj = new JSONObject();
        ProcBusinessRulesQueries bsnRuleQry=null;
        try{
            bsnRuleQry = ProcBusinessRulesQueries.valueOf(sectionName.toUpperCase());
        }catch(Exception e){
            return mainObj;                   
        }  
        mainObj=attributesToJsonObj(procInstanceName, bsnRuleQry, mainObj);
        mainObj=particularMethod(procInstanceName, bsnRuleQry, mainObj);
        JSONObject rObj=new JSONObject();
        rObj.put(bsnRuleQry.getPropertiesSectionName().toLowerCase(), mainObj);        
        return rObj;        
        
    }
    
    private static JSONObject attributesToJsonObj(String procInstanceName, ProcBusinessRulesQueries bsnRuleQry, JSONObject mainObj){ 
        if (bsnRuleQry.getIncludeRunAttributesToJsonObj()){        
            for (String curProp: bsnRuleQry.getPropertiesList()){
                for (String currFileNameSuffix: bsnRuleQry.getFileNameSuffix()){
                    String propValue = Parameter.getBusinessRuleProcedureFile(procInstanceName, currFileNameSuffix, curProp);
                if (propValue.length()>0)
                    mainObj.put(curProp, propValue);  
                }
            }
        }
        return mainObj;
    }
    private static JSONObject particularMethod(String procInstanceName, ProcBusinessRulesQueries bsnRuleQry, JSONObject mainObj){ 
        if (bsnRuleQry.getMethodName().length()>0){
            Method method = null;
            try {
                Class<?>[] paramTypes = bsnRuleQry.getMethodParamClass();
                method = ProcedureDefinitionQueries.class.getDeclaredMethod(bsnRuleQry.getMethodName(), paramTypes);
            } catch (NoSuchMethodException | SecurityException ex) {
                    return mainObj;
            }      
            try { 
                if (method!=null){ return (JSONObject) method.invoke(bsnRuleQry.getMethodName(), procInstanceName, bsnRuleQry, mainObj);}
            } catch (IllegalAccessException | NullPointerException | IllegalArgumentException | InvocationTargetException ex) {
                return mainObj;
            }
        }
        return mainObj;
    }
    
    public static JSONObject procedureActionsAndRoles(String procInstanceName, ProcBusinessRulesQueries bsnRuleQry, JSONObject mainObj){
        String[] procedureActions = Parameter.getBusinessRuleProcedureFile(procInstanceName, LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getAreaName(), LPPlatform.LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName()).toString().split("\\|");
        String[] verifyUserRequired = Parameter.getBusinessRuleProcedureFile(procInstanceName, LPPlatform.LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getAreaName(), LPPlatform.LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getTagName()).toString().split("\\|");
        String[] eSignRequired = Parameter.getBusinessRuleProcedureFile(procInstanceName, LpPlatformBusinessRules.ESIGN_REQUIRED.getAreaName(), LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName()).toString().split("\\|");
        String[] sampleStagesActionAutoMoveToNext = Parameter.getBusinessRuleProcedureFile(procInstanceName, SampleStageBusinessRules.ACTION_AUTOMOVETONEXT.getAreaName(), SampleStageBusinessRules.ACTION_AUTOMOVETONEXT.getTagName()).toString().split("\\|");
        JSONArray procActionsArr = new JSONArray();
        for (Object curProcAction: procedureActions){                                                      
            JSONObject procedureActionsObj = convertArrayRowToJSONObject(new String[]{"action_name"}, new Object[]{curProcAction});                
            JSONArray procActionAndRolesArr = new JSONArray();
            if (LPArray.valueInArray(eSignRequired, curProcAction))
                procedureActionsObj.put("e_sign/firma_electronica", "ON");
            if (LPArray.valueInArray(verifyUserRequired, curProcAction))
                procedureActionsObj.put("verify user/verificar usuario", "ON");            
            if (LPArray.valueInArray(sampleStagesActionAutoMoveToNext, curProcAction))
                procedureActionsObj.put("auto_move_to_next", "YES");
            else
                procedureActionsObj.put("auto_move_to_next", "NO");           
            
            procActionAndRolesArr.add(procedureActionsObj);
            String[] curActionRoles = Parameter.getMessageCodeValue(procInstanceName.replace("\"", "")+CONFIG_PROC_FILE_NAME, "actionEnabled"+curProcAction).split("\\|");
            for (Object curActionRole: curActionRoles){ 
                JSONObject currActionRolObj = convertArrayRowToJSONObject(new String[]{"rol"}, new Object[]{curActionRole});                
                procedureActionsObj.put("rol", currActionRolObj);
            }                     
            procActionsArr.add(procActionAndRolesArr);
        }
        mainObj.put(bsnRuleQry.getMethodSectionName(), procActionsArr);
        return mainObj;
    }
    public static JSONObject allProcSops(String procInstanceName, ProcBusinessRulesQueries bsnRuleQry, JSONObject mainObj){
        Object[][] procSopInMetaData = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.SopMetaData.TBL.getName(),
                new String[]{TblsCnfg.SopMetaData.FLD_SOP_ID.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, null, PROC_DISP_PROC_INSTC_SOPS_FLD_NAME.split("\\|"),
                PROC_DISP_PROC_INSTC_SOPS_SORT.split("\\|"), true );
        JSONArray sopArr=new JSONArray();
        for (Object[] curProcSop: procSopInMetaData){ 
            JSONObject currActionRolObj = convertArrayRowToJSONObject(PROC_DISP_PROC_INSTC_SOPS_FLD_NAME.split("\\|"), curProcSop);
            sopArr.add(currActionRolObj);            
        }                             
        mainObj.put(bsnRuleQry.getMethodSectionName().toLowerCase(), sopArr);
        return mainObj;
    }
    public static JSONObject allProcUsersRoles(ProcBusinessRulesQueries bsnRuleQry, JSONObject mainObj){
        JSONObject programkpIsObj = LPKPIs.getKPIs( 
                new String[]{"procedure_roles_by_user", "procedure_users_counter_by_role"}, 
                new String[]{"procedure", "procedure"},
                new String[]{TblsProcedure.ViewProcUserAndRoles.TBL.getName(), TblsProcedure.ViewProcUserAndRoles.TBL.getName()},
                new String[]{TblsProcedure.ViewProcUserAndRoles.FLD_ROLE_NAME.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause(), TblsProcedure.ViewProcUserAndRoles.FLD_ROLE_NAME.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new String[]{null, null},
                new String[]{TblsProcedure.ViewProcUserAndRoles.FLD_USER_NAME.getName()+"|"+TblsProcedure.ViewProcUserAndRoles.FLD_ROLE_NAME.getName(), TblsProcedure.ViewProcUserAndRoles.FLD_ROLE_NAME.getName()},
                new String[]{"false", "true"}
                );
        mainObj.put(bsnRuleQry.getMethodSectionName().toLowerCase(), programkpIsObj);
        return mainObj;
    }
    public static JSONObject sampleStages(String procInstanceName, ProcBusinessRulesQueries bsnRuleQry, JSONObject mainObj){
        String[] sampleStagesTimingCaptureStages = Parameter.getMessageCodeValue(procInstanceName.replace("\"", "")+CONFIG_PROC_FILE_NAME, "sampleStagesTimingCaptureStages").split("\\|");
        String[] sampleStagesListEn = Parameter.getMessageCodeValue(procInstanceName.replace("\"", "")+CONFIG_PROC_DATA_FILE_NAME, "sampleStagesList_en").split("\\|");
        JSONArray sampleStagesDataArr=new JSONArray();
        for (String curSampleStage: sampleStagesListEn){
            JSONObject stageDetailObj = new JSONObject();
            stageDetailObj.put("stage_name", curSampleStage);
            String[] directionNames=new String[]{"Previous", "Next"};
            for (String curDirection: directionNames){
                String[] propValuePrevious = Parameter.getMessageCodeValue(procInstanceName.replace("\"", "")+CONFIG_PROC_DATA_FILE_NAME, "sampleStage"+curSampleStage+curDirection).split("\\|");
                if (propValuePrevious[0].length()==0)
                    stageDetailObj.put(curDirection.toLowerCase()+"_stages_total", 0);
                else{
                    stageDetailObj.put(curDirection.toLowerCase()+"_stages_total", propValuePrevious.length);
                    JSONArray curStagePreviousStages=new JSONArray();
                    for (String curPrevStage: propValuePrevious){
                       JSONObject curPrevStageObj = new JSONObject();
                       curPrevStageObj.put("stage", curPrevStage);
                       curStagePreviousStages.add(curPrevStageObj);
                    } 
                    stageDetailObj.put(curDirection.toLowerCase()+"_stages", curStagePreviousStages);
                }
                if (LPArray.valueInArray(sampleStagesTimingCaptureStages, curSampleStage) || LPArray.valueInArray(sampleStagesTimingCaptureStages, "ALL"))
                    stageDetailObj.put("timing_capture", "YES");                
            }
            sampleStagesDataArr.add(stageDetailObj);
        }
        mainObj.put(bsnRuleQry.getMethodSectionName(), sampleStagesDataArr);
        return mainObj;
    }

    public static JSONObject sampleIncubation(String procInstanceName, ProcBusinessRulesQueries bsnRuleQry, JSONObject mainObj){
        String[] sampleIncubationTempReadingBusinessRule = Parameter.getMessageCodeValue(procInstanceName.replace("\"", "")+CONFIG_PROC_FILE_NAME, "sampleIncubationTempReadingBusinessRule").split("\\|");
        JSONArray incubRulesArr = new JSONArray();
        for (String curIncubRule: sampleIncubationTempReadingBusinessRule){
            JSONObject incubRulesObj = new JSONObject();
            incubRulesObj.put("rule", curIncubRule);  
            incubRulesArr.add(incubRulesObj);
        }    
        mainObj.put(bsnRuleQry.getMethodSectionName(), incubRulesArr); 
        return mainObj;
    }
    
    public static JSONObject encryption(String procInstanceName, ProcBusinessRulesQueries bsnRuleQry, JSONObject mainObj){
        String totalTables="total_tables";
        String totalFields="total_fields";
        JSONArray encrypTableFldsObjArr=new JSONArray();
        JSONArray schemasDataArr=new JSONArray();
        String[] schemasArr=new String[]{CONFIG_PROC_DATA_FILE_NAME, CONFIG_PROC_CONFIG_FILE_NAME};
        for (String curSchema: schemasArr){
            JSONObject curSchemaMainObj = new JSONObject();
            JSONObject curSchemaObj=new JSONObject();
            String[] encryptedTables = Parameter.getMessageCodeValue(procInstanceName.replace("\"", "")+curSchema, "encrypted_tables").split("\\|");
            if (encryptedTables[0].length()==0)
                curSchemaMainObj.put(totalTables, 0);
            else
                curSchemaMainObj.put(totalTables, encryptedTables.length);
            for (String currEncrypTable: encryptedTables){
                if (currEncrypTable.length()==0){
                    encrypTableFldsObjArr=new JSONArray();
                    encrypTableFldsObjArr.add("Nothing");
                }else{
                    encrypTableFldsObjArr=new JSONArray();
                    String[] encryptedTableFlds = Parameter.getMessageCodeValue(procInstanceName.replace("\"", "")+curSchema, "encrypted_"+currEncrypTable).split("\\|");
                    JSONObject encrypTableFldsObj = new JSONObject();
                    if (encryptedTables[0].length()==0)
                        encrypTableFldsObj.put(totalFields, 0);
                    else
                        encrypTableFldsObj.put(totalFields, encryptedTableFlds.length);
                    encrypTableFldsObjArr.add(encrypTableFldsObj);
                    for (String curFld: encryptedTableFlds){
                        encrypTableFldsObj = new JSONObject(); 
                        encrypTableFldsObj.put("field", curFld);
                        encrypTableFldsObjArr.add(encrypTableFldsObj);
                    }
                }
                curSchemaMainObj.put(currEncrypTable, encrypTableFldsObjArr);  
            } 
            curSchemaObj.put(curSchema.replace("-", ""), curSchemaMainObj);
            schemasDataArr.add(curSchemaObj);
        }
        mainObj.put(bsnRuleQry.getMethodSectionName(), schemasDataArr);
        return mainObj;
    }
}