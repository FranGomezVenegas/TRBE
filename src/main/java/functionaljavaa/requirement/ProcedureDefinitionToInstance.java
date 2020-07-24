/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsDataAudit;
import databases.TblsProcedure;
import static functionaljavaa.requirement.RequirementLogFile.requirementsLogEntry;
import java.util.Arrays;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Administrator
 */
public class ProcedureDefinitionToInstance {
    private ProcedureDefinitionToInstance(){    throw new IllegalStateException("Utility class");}
    
    /**
     *
     */
    public static final String JSON_LABEL_FOR_NO = "No";

    /**
     *
     */
    public static final String JSON_LABEL_FOR_YES = "Yes";

    /**
     *
     */
    public static final String JSON_LABEL_FOR_ERROR = "Error";

    /**
     *
     */
    public static final String JSON_LABEL_FOR_NUM_RECORDS_IN_DEFINITION = "Num Records in definition";
    
    /**
     *
     */
    public static final String TABLE_NAME_APP_USERS = "users";

    /**
     *
     */
    public static final String FLD_NAME_APP_USERS_USER_NAME="user_name";

    /**
     *
     */
    public static final String FLD_NAME_APP_USERS_PERSON_NAME="person_name";

    /**
     *
     */
    public static final String TABLE_NAME_APP_USER_PROCESS = "user_process";

    /**
     *
     */
    public static final String FLD_NAME_APP_USER_PROCESS_USER_NAME="user_name";

    /**
     *
     */
    public static final String FLD_NAME_APP_USER_PROCESS_PROC_NAME="proc_name";

    /**
     *
     */
    public static final String FLD_NAME_APP_USER_PROCESS_ACTIVE="active";

    /**
     *
     */
    public static final String FLD_NAME_PROCEDURE_SCHEMA_PREFIX="schema_prefix";

    /**
     *
     */
    public static final String TABLE_NAME_PROCEDURE_USER_ROLE_SOURCE = "procedure_user_role";    

    /**
     *
     */
    public static final String FLD_NAME_PROCEDURE_USER_ROLE_NAME="procedure_name";

    /**
     *
     */
    public static final String FLD_NAME_PROCEDURE_USER_ROLE_VERSION="procedure_version";

    /**
     *
     */
    public static final String FLD_NAME_PROCEDURE_USER_ROLE_SCHEMA_PREFIX="schema_prefix";

    /**
     *
     */
    public static final String FLD_NAME_PROCEDURE_USER_ROLE_USER_NAME="user_name";

    /**
     *
     */
    public static final String FLD_NAME_PROCEDURE_USER_ROLE_ROLE_NAME="role_name";

    /**
     *
     */
    public static final String TABLE_NAME_PROCEDURE_SOP_META_DATA = "procedure_sop_meta_data";  

    /**
     *
     */
    public static final String FLD_NAME_PROCEDURE_SOP_META_DATA_SOP_ID="sop_id";

    /**
     *
     */
    public static final String FLD_NAME_PROCEDURE_SOP_META_DATA_SOP_NAME="sop_name";

    /**
     *
     */
    public static final String SCHEMA_AUTHORIZATION_ROLE = "labplanet";
    
    /**
     *
     */
    public static final String FIELDS_TO_RETRIEVE_PROCEDURE_INFO_SOURCE=TblsProcedure.ProcedureInfo.FLD_NAME.getName()+"|"+TblsProcedure.ProcedureInfo.FLD_VERSION.getName()+"|"+TblsProcedure.ProcedureInfo.FLD_LABEL_EN.getName()+"|"+TblsProcedure.ProcedureInfo.FLD_LABEL_ES.getName();

    /**
     *
     */
    public static final String FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SOURCE="user_name|role_name";

    /**
     *
     */
    public static final String FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SORT="user_name";

    /**
     *
     */
    public static final String FIELDS_TO_INSERT_APP_USER_PROCESS=FLD_NAME_APP_USER_PROCESS_USER_NAME+"|"+FLD_NAME_APP_USER_PROCESS_PROC_NAME+"|"+FLD_NAME_APP_USER_PROCESS_ACTIVE;

    /**
     *
     */
    public static final String FIELDS_TO_INSERT_PROCEDURE_USER_ROLE_DESTINATION="person_name|role_name|active";
    //public static final String FIELDS_TO_INSERT_PROCEDURE_INFO_DESTINATION="name|version|label_en|label_es";

    /**
     *
     */
    public static final String FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE="sop_id|sop_name|sop_version|sop_revision|current_status|expires|has_child|file_link|brief_summary";

    /**
     *
     */
    public static final String FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SORT="sop_id";

    /**
     *
     */
    public static final String FIELDS_TO_INSERT_PROCEDURE_SOP_META_DATA_DESTINATION="person_name|role_name|active";

    /**
     *
     */
    public static final String FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION="name|role_name|sop";

    /**
     *
     * @param procedure
     * @param procVersion
     * @param schemaPrefix
     * @return
     */
    public static final JSONObject createDBProcedureInfo(String procedure,  Integer procVersion, String schemaPrefix){
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestinationProc=LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_PROCEDURE);
         Object[][] procInfoRecordsSource = Rdbms.getRecordFieldsByFilter(LPPlatform.SCHEMA_REQUIREMENTS, TblsProcedure.ProcedureInfo.TBL.getName(), 
                new String[]{TblsProcedure.ProcedureInfo.FLD_NAME.getName(), TblsProcedure.ProcedureInfo.FLD_VERSION.getName(),FLD_NAME_PROCEDURE_SCHEMA_PREFIX}, new Object[]{procedure, procVersion, schemaPrefix}, 
                FIELDS_TO_RETRIEVE_PROCEDURE_INFO_SOURCE.split("\\|"), null);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfoRecordsSource[0][0].toString())){
          jsonObj.put(JSON_LABEL_FOR_ERROR, LPJson.convertToJSON(procInfoRecordsSource));
        }else{
            jsonObj.put(JSON_LABEL_FOR_NUM_RECORDS_IN_DEFINITION, procInfoRecordsSource.length);
            for (Object[] curRow: procInfoRecordsSource){
                Object[][] procInfoRecordsDestination = Rdbms.getRecordFieldsByFilter(schemaNameDestinationProc, TblsProcedure.ProcedureInfo.TBL.getName(), 
                       new String[]{TblsProcedure.ProcedureInfo.FLD_NAME.getName(), TblsProcedure.ProcedureInfo.FLD_VERSION.getName()}, new Object[]{procedure, procVersion}, 
                       FIELDS_TO_RETRIEVE_PROCEDURE_INFO_SOURCE.split("\\|"), null);
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfoRecordsDestination[0][0].toString())){
                    jsonObj.put("Record in the instance", "Already exists");
                }else{
                    jsonObj.put("Record in instance", "Not exists");
                    String[] fldName=FIELDS_TO_RETRIEVE_PROCEDURE_INFO_SOURCE.split("\\|");
                    Object[] fldValue=curRow;
                    if (!LPArray.valueInArray(fldName, TblsProcedure.ProcedureInfo.FLD_SCHEMA_PREFIX.getName())){
                        fldName=LPArray.addValueToArray1D(fldName, TblsProcedure.ProcedureInfo.FLD_SCHEMA_PREFIX.getName());
                        fldValue=LPArray.addValueToArray1D(fldValue, schemaPrefix);                        
                    }
                    Object[] insertRecordInTable = Rdbms.insertRecordInTable(schemaNameDestinationProc, TblsProcedure.ProcedureInfo.TBL.getName(), fldName, fldValue);
                    jsonObj.put("Record in the instance inserted?", insertRecordInTable[0].toString());
                    //if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTable[0].toString())){}
                }
            }
        }
        return jsonObj;
    }     

    /**
     *
     * @param procedure
     * @param procVersion
     * @param schemaPrefix
     * @return
     */
    public static final  JSONObject createDBProcedureEvents(String procedure,  Integer procVersion, String schemaPrefix){
        return new JSONObject();
/*        String schemaNameDestination=LPPlatform.buildSchemaName(LPPlatform.SCHEMA_APP, LPPlatform.SCHEMA_CONFIG);
         Object[][] procInfoRecordsSource = Rdbms.getRecordFieldsByFilter(LPPlatform.SCHEMA_REQUIREMENTS, TABLE_NAME_PROCEDURE, 
                new String[]{FLD_NAME_PROCEDURE_NAME, FLD_NAME_PROCEDURE_VERSION,FLD_NAME_PROCEDURE_SCHEMA_PREFIX}, new Object[]{procedure, procVersion, schemaPrefix}, 
                FIELDS_TO_RETRIEVE_PROCEDURE_INFO_SOURCE.split("\\|"), null);
            */            
    }

    /**
     *
     * @param procedure
     * @param procVersion
     * @param schemaPrefix
     * @return
     */
    public static final  JSONObject createDBPersonProfiles(String procedure,  Integer procVersion, String schemaPrefix){
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestinationProcedure=LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_PROCEDURE);
         Object[][] procUserRolesRecordsSource = Rdbms.getRecordFieldsByFilter(LPPlatform.SCHEMA_REQUIREMENTS, TABLE_NAME_PROCEDURE_USER_ROLE_SOURCE, 
                new String[]{FLD_NAME_PROCEDURE_USER_ROLE_NAME, FLD_NAME_PROCEDURE_USER_ROLE_VERSION,FLD_NAME_PROCEDURE_USER_ROLE_SCHEMA_PREFIX}, new Object[]{procedure, procVersion, schemaPrefix}, 
                FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SOURCE.split("\\|"), FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SORT.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserRolesRecordsSource[0][0].toString())){
          jsonObj.put(JSON_LABEL_FOR_ERROR, LPJson.convertToJSON(procUserRolesRecordsSource));
          return jsonObj;
        }
        jsonObj.put(JSON_LABEL_FOR_NUM_RECORDS_IN_DEFINITION, procUserRolesRecordsSource.length);    
        for (Object[] curRow: procUserRolesRecordsSource){
            Object curUserName = curRow[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SOURCE.split("\\|"), FLD_NAME_PROCEDURE_USER_ROLE_USER_NAME)];
            Object curRoleName = curRow[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SOURCE.split("\\|"), FLD_NAME_PROCEDURE_USER_ROLE_ROLE_NAME)];
            JSONArray jsArr = new JSONArray(); 
            JSONObject jsUserRoleObj = new JSONObject();
            jsUserRoleObj.put("User", curUserName); jsUserRoleObj.put("Role", curRoleName);

            Object[][] existsAppUser = Rdbms.getRecordFieldsByFilter(LPPlatform.SCHEMA_APP, TABLE_NAME_APP_USERS, 
                    new String[]{FLD_NAME_APP_USERS_USER_NAME}, new Object[]{curUserName.toString()}, new String[]{FLD_NAME_APP_USERS_PERSON_NAME}, null);
            String diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())) ? JSON_LABEL_FOR_NO : JSON_LABEL_FOR_YES;
            jsUserRoleObj.put("User exists in the app?", diagnosesForLog); 
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())){
                // Place to create the user
            }                
            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(existsAppUser[0]))){
                Object[] existsAppUserProcess = Rdbms.existsRecord(LPPlatform.SCHEMA_APP, TABLE_NAME_APP_USER_PROCESS, 
                        new String[]{FLD_NAME_APP_USER_PROCESS_USER_NAME,FLD_NAME_APP_USER_PROCESS_PROC_NAME}, new Object[]{curUserName.toString(), schemaPrefix});
                jsonObj.put("User was added to the Process at the App level?", existsAppUserProcess[0].toString());  
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUserProcess[0].toString())){
                    Object[] insertRecordInTable = Rdbms.insertRecordInTable(LPPlatform.SCHEMA_APP, TABLE_NAME_APP_USER_PROCESS, 
                            FIELDS_TO_INSERT_APP_USER_PROCESS.split("\\|"), new Object[]{curUserName.toString(), schemaPrefix, true});
                    jsonObj.put("Added the User to the Process at the App level by running this utility?", insertRecordInTable[0].toString());                                                                
                }
            }
            Object curPersonName = existsAppUser[0][0];                
            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())){
                Object[] insertRecordInTable = Rdbms.insertRecordInTable(schemaNameDestinationProcedure, TblsProcedure.PersonProfile.TBL.getName(), 
                        FIELDS_TO_INSERT_PROCEDURE_USER_ROLE_DESTINATION.split("\\|"), new Object[]{curPersonName.toString(), curRoleName.toString(), true});
                jsonObj.put("User Role inserted in the instance?", insertRecordInTable[0].toString());                    
            }
            jsArr.add(jsUserRoleObj);
            jsonObj.put("User "+curUserName+ " & Role "+curRoleName, jsArr);
        }                            
        return jsonObj;
    }

    /**
     *
     * @param procedure
     * @param procVersion
     * @param schemaPrefix
     * @return
     */
    public static final  JSONObject createDBSopMetaDataAndUserSop(String procedure,  Integer procVersion, String schemaPrefix){
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestination=LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG);
        
         Object[][] procSopMetaDataRecordsSource = Rdbms.getRecordFieldsByFilter(LPPlatform.SCHEMA_REQUIREMENTS, TABLE_NAME_PROCEDURE_SOP_META_DATA, 
                new String[]{FLD_NAME_PROCEDURE_USER_ROLE_NAME, FLD_NAME_PROCEDURE_USER_ROLE_VERSION,FLD_NAME_PROCEDURE_USER_ROLE_SCHEMA_PREFIX}, new Object[]{procedure, procVersion, schemaPrefix}, 
                FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE.split("\\|"), FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SORT.split("\\|"));
                //new String[]{"*"}, new String[]{"sop_id"});
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procSopMetaDataRecordsSource[0][0].toString())){
          jsonObj.put(JSON_LABEL_FOR_ERROR, LPJson.convertToJSON(procSopMetaDataRecordsSource));
          return jsonObj;
        }
        jsonObj.put(JSON_LABEL_FOR_NUM_RECORDS_IN_DEFINITION, procSopMetaDataRecordsSource.length);        
        for (Object[] curSopMetaData: procSopMetaDataRecordsSource){
            Object curSopId = curSopMetaData[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE.split("\\|"), TblsCnfg.SopMetaData.FLD_SOP_ID.getName())];
            Object curSopName = curSopMetaData[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE.split("\\|"), TblsCnfg.SopMetaData.FLD_SOP_NAME.getName())];
            JSONArray jsArr = new JSONArray(); 
            JSONObject jsUserRoleObj = new JSONObject();
            jsUserRoleObj.put("SOP Id", curSopId); jsUserRoleObj.put("SOP Name", curSopName);

            Object[][] existsAppUser = Rdbms.getRecordFieldsByFilter(schemaNameDestination, TblsCnfg.SopMetaData.TBL.getName(), 
                    new String[]{TblsCnfg.SopMetaData.FLD_SOP_NAME.getName()}, new Object[]{curSopName.toString()}, new String[]{TblsCnfg.SopMetaData.FLD_SOP_NAME.getName()}, null);
            String diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())) ? JSON_LABEL_FOR_NO : JSON_LABEL_FOR_YES;
            jsUserRoleObj.put("SOP exists in the procedure?", diagnosesForLog); 
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())){
                Object[] insertRecordInTable = Rdbms.insertRecordInTable(schemaNameDestination, TblsCnfg.SopMetaData.TBL.getName(), 
                        FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE.split("\\|"), curSopMetaData);
                diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTable[0].toString())) ? JSON_LABEL_FOR_NO : JSON_LABEL_FOR_YES;
                jsonObj.put("SOP inserted in the instance?", diagnosesForLog);
                //if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTable[0].toString())){}
            }                         
            jsArr.add(jsUserRoleObj);
            jsonObj.put("SOP Id "+curSopId+ " & SOP Name "+curSopName, jsArr);            
        }        
        return jsonObj;
    }

    /**
     *
     * @param procedure
     * @param procVersion
     * @param schemaPrefix
     * @return
     */
    public static final  JSONObject addProcedureSOPtoUsers(String procedure,  Integer procVersion, String schemaPrefix){
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestinationProc=LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_PROCEDURE);
        Object[][] procEventSopsRecordsSource = Rdbms.getRecordFieldsByFilter(schemaNameDestinationProc, TblsProcedure.ProcedureEvents.TBL.getName(), 
                new String[]{TblsProcedure.ProcedureEvents.FLD_SOP.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{""}, 
                FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), new String[]{"sop"});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventSopsRecordsSource[0][0].toString())){
          jsonObj.put(JSON_LABEL_FOR_ERROR, LPJson.convertToJSON(procEventSopsRecordsSource));
          return jsonObj;
        }
        jsonObj.put(JSON_LABEL_FOR_NUM_RECORDS_IN_DEFINITION, procEventSopsRecordsSource.length);  
        
        String[] existingSopRole = new String[0];
        for (Object[] curProcEventSops: procEventSopsRecordsSource){
            Object curProcEventName = curProcEventSops[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), TblsProcedure.ProcedureEvents.FLD_NAME.getName())];
            Object curSops = curProcEventSops[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), TblsProcedure.ProcedureEvents.FLD_SOP.getName())];
            Object curRoleName = curProcEventSops[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), TblsProcedure.ProcedureEvents.FLD_ROLE_NAME.getName())];
            JSONArray jsArr = new JSONArray(); 
            JSONObject jsUserRoleObj = new JSONObject();
            jsUserRoleObj.put("Procedure Event", curProcEventName); jsUserRoleObj.put("SOP Name", curSops); jsUserRoleObj.put("Role Name", curRoleName);
            
            String[] curSopsArr = curSops.toString().split("\\|"); 
            String[] curRoleNameArr = curRoleName.toString().split("\\|"); 
            JSONArray jsEventArr = new JSONArray();
            for (String sopFromArr: curSopsArr){         
                JSONArray jsSopRoleArr = new JSONArray();
                for (String roleFromArr: curRoleNameArr){
                    
                    JSONObject jsSopRoleObj = new JSONObject();
                    
                    String sopRoleValue=sopFromArr+"*"+roleFromArr;
                    Integer sopRolePosic = LPArray.valuePosicInArray(existingSopRole, sopRoleValue);
                    String diagnosesForLog = (sopRolePosic==-1) ? JSON_LABEL_FOR_NO : JSON_LABEL_FOR_YES;
                    jsSopRoleObj.put("SOP "+sopFromArr+" exists for role "+roleFromArr+" ?", diagnosesForLog);
                    if (sopRolePosic==-1){
                        ProcedureDefinitionToInstanceUtility.procedureAddSopToUsersByRole(procedure, procVersion, schemaPrefix, 
                                roleFromArr, sopFromArr, null, null);                        
                    }
                    jsSopRoleArr.add(jsSopRoleObj);
                    existingSopRole=LPArray.addValueToArray1D(existingSopRole, sopRoleValue);
                }
                jsEventArr.add(jsSopRoleArr);
                jsUserRoleObj.put("Event SOPs Log", jsEventArr);
            }
            jsArr.add(jsUserRoleObj); 
            jsonObj.put("Procedure Event "+curProcEventName+ " & SOP Name "+curSops+ " & Role Name "+curRoleName, jsArr);   
        }       
        return jsonObj;
    }
    
    /**
     *
     * @param schemaNamePrefix
     * @return
     */
    public static final  JSONObject createDBProcessSchemas(String schemaNamePrefix){
        JSONObject jsonObj = new JSONObject();
        
        String methodName = "createDataBaseSchemas";       
        String[] schemaNames = new String[]{LPPlatform.SCHEMA_CONFIG, LPPlatform.SCHEMA_DATA, LPPlatform.SCHEMA_DATA_AUDIT};
         jsonObj.put(JSON_LABEL_FOR_NUM_RECORDS_IN_DEFINITION, schemaNames.length);     
        for (String fn:schemaNames){
            JSONArray jsSchemaArr = new JSONArray();
            String configSchemaName = schemaNamePrefix+"-"+fn;
            jsSchemaArr.add(configSchemaName);
            requirementsLogEntry("", methodName, configSchemaName,2);
            
            configSchemaName = LPPlatform.buildSchemaName(configSchemaName, fn);
            String configSchemaScript = "CREATE SCHEMA "+configSchemaName+"  AUTHORIZATION "+SCHEMA_AUTHORIZATION_ROLE+";"+
                    " GRANT ALL ON SCHEMA "+configSchemaName+" TO "+SCHEMA_AUTHORIZATION_ROLE+ ";";     
            Rdbms.prepRdQuery(configSchemaScript, new Object[]{});
            
            // La idea es no permitir ejecutar prepUpQuery directamente, por eso es privada y no publica.            
                //Integer prepUpQuery = Rdbms.prepUpQuery(configSchemaScript, new Object[0]);
                //String diagnosesForLog = (prepUpQuery==-1) ? JSON_LABEL_FOR_NO : JSON_LABEL_FOR_YES;
                //jsonObj.put("Schema Created?", diagnosesForLog);
            
            jsonObj.put(configSchemaName, jsSchemaArr);
        }
        return jsonObj;
     }    
    
    /**
     *
     * @param schemaNamePrefix
     * @param tableName
     * @param fieldsName
     * @return
     */
    public static final  JSONObject createDBProcessTables(String schemaNamePrefix, String tableName, String[] fieldsName){
        JSONObject jsonObj = new JSONObject();        

        String tblCreateScript=TblsCnfg.Analysis.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("Analysis", tblCreateScript);

        tblCreateScript=TblsCnfg.AnalysisMethod.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("AnalysisMethod", tblCreateScript);

        tblCreateScript=TblsCnfg.AnalysisMethodParams.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("AnalysisMethodParams", tblCreateScript);
        
        tblCreateScript=TblsProcedure.PersonProfile.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("PersonProfile", tblCreateScript);        
        
        tblCreateScript=TblsProcedure.ProcedureEvents.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("ProcedureEvents", tblCreateScript);
        
        tblCreateScript=TblsProcedure.ProcedureInfo.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("ProcedureInfo", tblCreateScript);
        
        tblCreateScript=TblsCnfg.Sample.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("Sample", tblCreateScript);

        tblCreateScript=TblsCnfg.SampleRules.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SampleRules", tblCreateScript);

        tblCreateScript=TblsCnfg.SopMetaData.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SopMetaData", tblCreateScript);
        
        tblCreateScript=TblsCnfg.Spec.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("Spec", tblCreateScript);

        tblCreateScript=TblsCnfg.SpecLimits.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SpecLimits", tblCreateScript);

        tblCreateScript=TblsCnfg.SpecRules.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SpecRules", tblCreateScript);

        tblCreateScript=TblsCnfg.UnitsOfMeasurement.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("UnitsOfMeasurement", tblCreateScript);        

        tblCreateScript=TblsCnfg.ViewAnalysisMethodsView.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("ViewAnalysisMethodsView", tblCreateScript);        

        tblCreateScript=TblsCnfg.zzzDbErrorLog.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsCnfg.DbErrorLog", tblCreateScript);        
        
                
        tblCreateScript=TblsData.Sample.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("Sample", tblCreateScript);        

        tblCreateScript=TblsData.SampleAnalysis.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SampleAnalysis", tblCreateScript);        
        
        tblCreateScript=TblsData.SampleAnalysisResult.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SampleAnalysisResult", tblCreateScript);        
        
        tblCreateScript=TblsData.SampleAliq.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SampleAliq", tblCreateScript);        
        
        tblCreateScript=TblsData.SampleAliqSub.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SampleAliqSub", tblCreateScript);        
        
        tblCreateScript=TblsData.SampleCoc.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SampleCoc", tblCreateScript);                
        
        tblCreateScript=TblsData.UserAnalysisMethod.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("UserAnalysisMethod", tblCreateScript);               
        
        tblCreateScript=TblsData.UserSop.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("UserSop", tblCreateScript);               

        tblCreateScript=TblsData.ViewSampleCocNames.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("ViewSampleCocNames", tblCreateScript);               

        tblCreateScript=TblsData.ViewUserAndMetaDataSopView.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("ViewUserAndMetaDataSopView", tblCreateScript);               
        
        tblCreateScript=TblsData.ViewSampleAnalysisResultWithSpecLimits.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("ViewSampleAnalysisResultWithSpecLimits", tblCreateScript);                                 
                
        tblCreateScript=TblsDataAudit.Session.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("Session", tblCreateScript);          
        
        tblCreateScript=TblsDataAudit.Sample.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("Sample", tblCreateScript);   
        
        return jsonObj;
     }        
}
