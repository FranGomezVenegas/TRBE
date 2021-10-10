/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfigAudit;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitDataAudit;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitProcedure;
import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMConfig;
import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMData;
import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMDataAudit;
import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMProcedure;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import static databases.Rdbms.dbTableExists;
import static databases.Rdbms.insertRecordInTableFromTable;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsApp;
import databases.TblsAppConfig;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsDataAudit;
import databases.TblsProcedure;
import databases.TblsReqs;
import databases.TblsTesting;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.requirement.RequirementLogFile.requirementsLogEntry;
import functionaljavaa.requirement.masterdata.ClassMasterData;
import java.util.Arrays;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform.LpPlatformBusinessRules;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class ProcedureDefinitionToInstance {
    
    public static final String[] ProcedureSchema_TablesWithNoTestingClone=new String[]{TblsProcedure.PersonProfile.TBL.getName(), TblsProcedure.ProcedureEvents.TBL.getName(),
        TblsProcedure.ProcedureInfo.TBL.getName(), TblsProcedure.ViewProcUserAndRoles.TBL.getName()};
    
    private ProcedureDefinitionToInstance(){    throw new IllegalStateException("Utility class");}
    
    public enum JsonTags{
        NO("No"), YES("Yes"), ERROR("Error"), USERS("Users"), NUM_RECORDS_IN_DEFINITION("Num Records in definition")
        ;
        private JsonTags(String tgVal){
            this.tagValue=tgVal;
        }       
        public String getTagValue(){return this.tagValue;}
        
        private final String tagValue;
    }
    

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
    public static final String FIELDS_TO_INSERT_APP_USER_PROCESS=TblsApp.UserProcess.FLD_USER_NAME.getName()+"|"+TblsApp.UserProcess.FLD_PROC_NAME.getName()+"|"+TblsApp.UserProcess.FLD_ACTIVE.getName();

    /**
     *
     */
    public static final String FIELDS_TO_INSERT_PROCEDURE_USER_ROLE_DESTINATION="person_name|role_name|active";

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
     * @param procInstanceName
     * @return
     */
    public static final JSONObject createDBProcedureInfo(String procedure,  Integer procVersion, String procInstanceName){
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestinationProc=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName());
         Object[][] procInfoRecordsSource = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsProcedure.ProcedureInfo.TBL.getName(), 
                new String[]{TblsProcedure.ProcedureInfo.FLD_NAME.getName(), TblsProcedure.ProcedureInfo.FLD_VERSION.getName(),TblsProcedure.ProcedureInfo.FLD_SCHEMA_PREFIX.getName()}, new Object[]{procedure, procVersion, procInstanceName}, 
                FIELDS_TO_RETRIEVE_PROCEDURE_INFO_SOURCE.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfoRecordsSource[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procInfoRecordsSource[0]));
        }else{
            jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procInfoRecordsSource.length);
            for (Object[] curRow: procInfoRecordsSource){
                Object[][] procInfoRecordsDestination = Rdbms.getRecordFieldsByFilter(schemaNameDestinationProc, TblsProcedure.ProcedureInfo.TBL.getName(), 
                       new String[]{TblsProcedure.ProcedureInfo.FLD_NAME.getName(), TblsProcedure.ProcedureInfo.FLD_VERSION.getName()}, new Object[]{procedure, procVersion}, 
                       FIELDS_TO_RETRIEVE_PROCEDURE_INFO_SOURCE.split("\\|"));
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfoRecordsDestination[0][0].toString())){
                    jsonObj.put("Record in the instance", "Already exists");
                }else{
                    jsonObj.put("Record in instance", "Not exists");
                    String[] fldName=FIELDS_TO_RETRIEVE_PROCEDURE_INFO_SOURCE.split("\\|");
                    Object[] fldValue=curRow;
                    if (!LPArray.valueInArray(fldName, TblsProcedure.ProcedureInfo.FLD_SCHEMA_PREFIX.getName())){
                        fldName=LPArray.addValueToArray1D(fldName, TblsProcedure.ProcedureInfo.FLD_SCHEMA_PREFIX.getName());
                        fldValue=LPArray.addValueToArray1D(fldValue, procInstanceName);                        
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
     * @param procInstanceName
     * @return
     */
    public static final  JSONObject createDBProcedureEvents(String procedure,  Integer procVersion, String procInstanceName){        
        Object[] insertRecordInTableFromTable = insertRecordInTableFromTable(true, 
                TblsReqs.ProcedureUserRequirementsEvents.getAllFieldNames(),
                    GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureUserRequirementsEvents.TBL.getName(), 
                new String[]{TblsReqs.ProcedureUserRequirementsEvents.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirementsEvents.FLD_PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRequirementsEvents.FLD_SCHEMA_PREFIX.getName()},
                new Object[]{procedure, procVersion, procInstanceName},
                LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), 
                    TblsProcedure.ProcedureEvents.TBL.getName(), TblsProcedure.ProcedureEvents.getAllFieldNames());

        JSONObject jsonObj = new JSONObject();
//        jsonObj.put("Diagnostic from createDBProcedureEvents", insertRecordInTableFromTable[0].toString());
        String[] procEventFldNamesToGet=TblsProcedure.ProcedureEvents.getAllFieldNames();
        Object[][] procEventRows = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.ProcedureEvents.TBL.getName(), 
                new String[]{TblsProcedure.ProcedureEvents.FLD_ROLE_NAME.getName(), WHERECLAUSE_TYPES.OR.getSqlClause()+" "+TblsProcedure.ProcedureEvents.FLD_ROLE_NAME.getName()+" "+WHERECLAUSE_TYPES.LIKE}, 
                new Object[]{"ALL", "%|%"}, 
                procEventFldNamesToGet);
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventRows[0][0].toString())){
            Object[][] procRoles = new Object[][]{{}};
                Object[][] procRolesAllRoles = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureRoles.TBL.getName(), 
                    new String[]{TblsReqs.ProcedureRoles.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.FLD_PROCEDURE_VERSION.getName(), TblsReqs.ProcedureRoles.FLD_SCHEMA_PREFIX.getName()},
                    new Object[]{procedure, procVersion, procInstanceName},
                    new String[]{TblsReqs.ProcedureRoles.FLD_ROLE_NAME.getName()});
            for (Object[] curProcEvent: procEventRows){
                if ("ALL".equalsIgnoreCase(curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.FLD_ROLE_NAME.getName())].toString()))
                    procRoles=procRolesAllRoles;
                else
                    procRoles=LPArray.array1dTo2d(curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.FLD_ROLE_NAME.getName())].toString().split("\\|"), 1);

                for (int i=0;i<procRoles.length;i++){
                    if (i==0){
                        Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.ProcedureEvents.TBL.getName(),
                            new String[]{TblsProcedure.ProcedureEvents.FLD_ROLE_NAME.getName()}, new Object[]{procRoles[0][0].toString()},
                            new String[]{TblsProcedure.ProcedureEvents.FLD_ROLE_NAME.getName(), TblsProcedure.ProcedureEvents.FLD_NAME.getName()}, new Object[]{curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.FLD_ROLE_NAME.getName())].toString(), curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.FLD_NAME.getName())]});
                updateRecordFieldsByFilter=new Object[]{};
                    }else{
                    
                        curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.FLD_ROLE_NAME.getName())]=procRoles[i][0].toString();
                        Object[] insertRecordInTable = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.ProcedureEvents.TBL.getName(), 
                                procEventFldNamesToGet, curProcEvent);
                    }
                }
/*                Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.ProcedureEvents.TBL.getName(),
                        new String[]{TblsProcedure.ProcedureEvents.FLD_ROLE_NAME.getName()}, new Object[]{procRoles[0][0].toString()},
                        new String[]{TblsProcedure.ProcedureEvents.FLD_NAME.getName()}, new Object[]{curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.FLD_NAME.getName())]});
                updateRecordFieldsByFilter=new Object[]{};
*/                
            }
        }
        return jsonObj;
    }

    /**
     *
     * @param procedure
     * @param procVersion
     * @param procInstanceName
     * @return
     */
    public static final  JSONObject createDBPersonProfiles(String procedure,  Integer procVersion, String procInstanceName){
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestinationProcedure=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName());
         Object[][] procUserRolesRecordsSource = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureUserRole.TBL.getName(), 
                new String[]{TblsReqs.ProcedureUserRole.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRole.FLD_PROCEDURE_VERSION.getName(),TblsReqs.ProcedureUserRole.FLD_SCHEMA_PREFIX.getName()}, new Object[]{procedure, procVersion, procInstanceName}, 
                FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SOURCE.split("\\|"), FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SORT.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserRolesRecordsSource[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procUserRolesRecordsSource[0]));
          return jsonObj;
        }
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procUserRolesRecordsSource.length);    
        for (Object[] curRow: procUserRolesRecordsSource){
            Object curUserName = curRow[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SOURCE.split("\\|"), TblsReqs.ProcedureUserRole.FLD_USER_NAME.getName())];
            Object curRoleName = curRow[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SOURCE.split("\\|"), TblsReqs.ProcedureUserRole.FLD_ROLE_NAME.getName())];
            JSONArray jsArr = new JSONArray(); 
            JSONObject jsUserRoleObj = new JSONObject();
            jsUserRoleObj.put("User", curUserName); jsUserRoleObj.put("Role", curRoleName);

            Object[][] existsAppUser = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.Users.TBL.getName(), 
                    new String[]{TblsApp.Users.FLD_USER_NAME.getName()}, new Object[]{curUserName.toString()}, new String[]{TblsApp.Users.FLD_PERSON_NAME.getName()});
            String diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())) ? JsonTags.NO.getTagValue() : JsonTags.YES.getTagValue();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())){
                String personId=curUserName+"z";
                
                Object[] insertRecordInTable=Rdbms.insertRecordInTable(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.Person.TBL.getName(), 
                    new String[]{TblsAppConfig.Person.FLD_PERSON_ID.getName(), TblsAppConfig.Person.FLD_FIRST_NAME.getName(), 
                        TblsAppConfig.Person.FLD_LAST_NAME.getName(), TblsAppConfig.Person.FLD_PHOTO.getName()}, 
                    new Object[]{personId, "I'm a user demo", "for demos ", "https://hasta-pronto.ru/wp-content/uploads/2014/09/chibcha.jpg"});
                insertRecordInTable = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.Users.TBL.getName(), 
                        new String[]{TblsApp.Users.FLD_USER_NAME.getName(), TblsApp.Users.FLD_EMAIL.getName(), TblsApp.Users.FLD_ESIGN.getName(),
                            TblsApp.Users.FLD_PASSWORD.getName(), TblsApp.Users.FLD_PERSON_NAME.getName()},
                        new Object[]{curUserName, "trazit.info@gmail.com", "firmademo", "1234", personId});
                existsAppUser=LPArray.array1dTo2d(insertRecordInTable,1);
                diagnosesForLog=diagnosesForLog+" trying to create, log for creation="+insertRecordInTable[0].toString();
//                insertRecordInTable=Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.UserProcess.TBL.getName(), 
//                    new String[]{TblsApp.UserProcess.FLD_USER_NAME.getName(), TblsApp.UserProcess.FLD_PROC_NAME.getName(), TblsApp.UserProcess.FLD_ACTIVE.getName()}, 
//                    new Object[]{curUserName, fakeProcName, true});
                
                // Place to create the user
            }                
            jsUserRoleObj.put("User exists in the app?", diagnosesForLog); 
            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(existsAppUser[0]))){
                Object[] existsAppUserProcess = Rdbms.existsRecord(GlobalVariables.Schemas.APP.getName(), TblsApp.UserProcess.TBL.getName(), 
                        new String[]{TblsApp.UserProcess.FLD_USER_NAME.getName(), TblsApp.UserProcess.FLD_PROC_NAME.getName()}, new Object[]{curUserName.toString(), procInstanceName});
                jsonObj.put("User was added to the Process at the App level?", existsAppUserProcess[0].toString());  
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUserProcess[0].toString())){
                    Object[] insertRecordInTable = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.UserProcess.TBL.getName(), 
                            FIELDS_TO_INSERT_APP_USER_PROCESS.split("\\|"), new Object[]{curUserName.toString(), procInstanceName, true});
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
     * @param procInstanceName
     * @return
     */
    public static final  JSONObject createDBSopMetaDataAndUserSop(String procedure,  Integer procVersion, String procInstanceName){
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestination=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
        
         Object[][] procSopMetaDataRecordsSource = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureSopMetaData.TBL.getName(), 
                new String[]{TblsReqs.ProcedureSopMetaData.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureSopMetaData.FLD_PROCEDURE_VERSION.getName(),TblsReqs.ProcedureSopMetaData.FLD_SCHEMA_PREFIX.getName()}, new Object[]{procedure, procVersion, procInstanceName}, 
                FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE.split("\\|"), FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SORT.split("\\|"));
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procSopMetaDataRecordsSource[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procSopMetaDataRecordsSource));
          return jsonObj;
        }
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procSopMetaDataRecordsSource.length);        
        for (Object[] curSopMetaData: procSopMetaDataRecordsSource){
            Object curSopId = curSopMetaData[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE.split("\\|"), TblsCnfg.SopMetaData.FLD_SOP_ID.getName())];
            Object curSopName = curSopMetaData[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE.split("\\|"), TblsCnfg.SopMetaData.FLD_SOP_NAME.getName())];
            JSONArray jsArr = new JSONArray(); 
            JSONObject jsUserRoleObj = new JSONObject();
            jsUserRoleObj.put("SOP Id", curSopId); jsUserRoleObj.put("SOP Name", curSopName);

            Object[][] existsAppUser = Rdbms.getRecordFieldsByFilter(schemaNameDestination, TblsCnfg.SopMetaData.TBL.getName(), 
                    new String[]{TblsCnfg.SopMetaData.FLD_SOP_NAME.getName()}, new Object[]{curSopName.toString()}, new String[]{TblsCnfg.SopMetaData.FLD_SOP_NAME.getName()});
            String diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())) ? JsonTags.NO.getTagValue() : JsonTags.YES.getTagValue();
            jsUserRoleObj.put("SOP exists in the procedure?", diagnosesForLog); 
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())){
                Object[] insertRecordInTable = Rdbms.insertRecordInTable(schemaNameDestination, TblsCnfg.SopMetaData.TBL.getName(), 
                        FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE.split("\\|"), curSopMetaData);
                diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTable[0].toString())) ? JsonTags.NO.getTagValue() : JsonTags.YES.getTagValue();
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
     * @param procInstanceName
     * @param moduleName
     * @return
     */
    public static final  JSONObject createDBModuleTablesAndFields(String procedure,  Integer procVersion, String procInstanceName, String moduleName){
        JSONObject jsonObj = new JSONObject();
        
         Object[][] procModuleTablesAndFieldsSource = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureModuleTablesAndFields.TBL.getName(), 
                new String[]{TblsReqs.ProcedureModuleTablesAndFields.FLD_ACTIVE.getName(), TblsReqs.ProcedureModuleTablesAndFields.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureModuleTablesAndFields.FLD_PROCEDURE_VERSION.getName(),TblsReqs.ProcedureModuleTablesAndFields.FLD_SCHEMA_PREFIX.getName()}, 
                new Object[]{true, procedure, procVersion, procInstanceName}, 
                TblsReqs.ProcedureModuleTablesAndFields.getAllFieldNames(), new String[]{TblsReqs.ProcedureModuleTablesAndFields.FLD_SCHEMA_NAME.getName()});        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procModuleTablesAndFieldsSource[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procModuleTablesAndFieldsSource));
          return jsonObj;
        }
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procModuleTablesAndFieldsSource.length);        
        for (Object[] curModuleTablesAndFields: procModuleTablesAndFieldsSource){
            String tableCreationScriptTable = "";
            Object curSchemaName = curModuleTablesAndFields[LPArray.valuePosicInArray(TblsReqs.ProcedureModuleTablesAndFields.getAllFieldNames(), TblsReqs.ProcedureModuleTablesAndFields.FLD_SCHEMA_NAME.getName())];
            Object curTableName = curModuleTablesAndFields[LPArray.valuePosicInArray(TblsReqs.ProcedureModuleTablesAndFields.getAllFieldNames(), TblsReqs.ProcedureModuleTablesAndFields.FLD_TABLE_NAME.getName())];
            Object curFieldName = curModuleTablesAndFields[LPArray.valuePosicInArray(TblsReqs.ProcedureModuleTablesAndFields.getAllFieldNames(), TblsReqs.ProcedureModuleTablesAndFields.FLD_FIELD_NAME.getName())];

            switch (moduleName.toUpperCase()){
                case "ENVIRONMENTAL_MONITORING":
                    if (GlobalVariables.Schemas.CONFIG.getName().equalsIgnoreCase(curSchemaName.toString()))
                        tableCreationScriptTable = TblsEnvMonitConfig.getTableCreationScriptFromConfigTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                    if (GlobalVariables.Schemas.CONFIG_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                        tableCreationScriptTable = TblsEnvMonitConfigAudit.getTableCreationScriptFromConfigAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                    if (GlobalVariables.Schemas.DATA.getName().equalsIgnoreCase(curSchemaName.toString()))
                        tableCreationScriptTable = TblsEnvMonitData.getTableCreationScriptFromDataTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                    if (GlobalVariables.Schemas.DATA_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                        tableCreationScriptTable = TblsEnvMonitDataAudit.getTableCreationScriptFromDataAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                    if (GlobalVariables.Schemas.PROCEDURE.getName().equalsIgnoreCase(curSchemaName.toString()))
                        tableCreationScriptTable = TblsEnvMonitProcedure.getTableCreationScriptFromDataProcedureTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                    break;
                case "INSPECTION_LOT_RAW_MATERIAL":
                    if (GlobalVariables.Schemas.CONFIG.getName().equalsIgnoreCase(curSchemaName.toString())){
                        Object[] tableExists=dbTableExists(procInstanceName+"-"+GlobalVariables.Schemas.CONFIG.getName(), curTableName.toString());
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(tableExists[0].toString()))
                            tableCreationScriptTable=TblsInspLotRMConfig.getTableUpdateScriptFromConfigTableInspLotRM(curTableName.toString(), procInstanceName+"-"+GlobalVariables.Schemas.CONFIG.getName(), curFieldName.toString().split("\\|"));
                        else
                            tableCreationScriptTable = TblsInspLotRMConfig.getTableCreationScriptFromConfigTableInspLotRM(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                    }
//                    if (GlobalVariables.Schemas.CONFIG_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
//                        tableCreationScriptFromCnfgTable = TblsInspLotRMCnfgAduit.getTableCreationScriptFromCnfgTable(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                    if (GlobalVariables.Schemas.DATA.getName().equalsIgnoreCase(curSchemaName.toString())){
                        tableCreationScriptTable = TblsInspLotRMData.getTableCreationScriptFromDataTableInspLotRM(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                    }
                    if (GlobalVariables.Schemas.DATA_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                        tableCreationScriptTable = TblsInspLotRMDataAudit.getTableCreationScriptFromDataAuditTableInspLotRM(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                    if (GlobalVariables.Schemas.PROCEDURE.getName().equalsIgnoreCase(curSchemaName.toString()))
                        tableCreationScriptTable = TblsInspLotRMProcedure.getTableCreationScriptFromDataProcedureTableInspLotRM(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));                  
                    break;
                case "GENOME":
                    break;
                default: 
                    tableCreationScriptTable="";
                    break;
            }
            if (tableCreationScriptTable.length()>0 && tableCreationScriptTable.contains(LPPlatform.LAB_FALSE)){
                if (GlobalVariables.Schemas.CONFIG.getName().equalsIgnoreCase(curSchemaName.toString()))
                    tableCreationScriptTable = TblsCnfg.getTableCreationScriptFromCnfgTable(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
    //            if (GlobalVariables.Schemas.CONFIG_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
    //                tableCreationScriptFromCnfgTable = TblsCnfg.getTableCreationScriptFromCnfgTable(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                if (GlobalVariables.Schemas.DATA.getName().equalsIgnoreCase(curSchemaName.toString()))
                    tableCreationScriptTable = TblsData.getTableCreationScriptFromDataTable(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                if (GlobalVariables.Schemas.DATA_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                    tableCreationScriptTable = TblsDataAudit.getTableCreationScriptFromDataAuditTable(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                if (GlobalVariables.Schemas.TESTING.getName().equalsIgnoreCase(curSchemaName.toString()))
                    tableCreationScriptTable = TblsTesting.getTableCreationScriptFromTestingTable(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
            } 
            if (tableCreationScriptTable.contains(LPPlatform.LAB_FALSE))
                LPPlatform.saveMessageInDbErrorLog(tableCreationScriptTable, null, new Object[]{"ProcedureDefinitionToInstance >> createDBModuleTablesAndFields"}, "table not declared in switch", new Object[]{tableCreationScriptTable});
            else     
                if (tableCreationScriptTable.length()>0){
                    Rdbms.prepUpQuery(tableCreationScriptTable, new Object[]{});
                    jsonObj.put(curSchemaName.toString()+"-"+curTableName.toString(), tableCreationScriptTable);
                    if (curSchemaName.toString().contains(GlobalVariables.Schemas.DATA.getName()) || curSchemaName.toString().contains(GlobalVariables.Schemas.PROCEDURE.getName())){                    
                        String newSchemaName=Rdbms.suffixForTesting(curSchemaName.toString(), tableCreationScriptTable); 
                        tableCreationScriptTable=tableCreationScriptTable.replace(LPPlatform.buildSchemaName(procInstanceName, curSchemaName.toString()), LPPlatform.buildSchemaName(procInstanceName, newSchemaName));
                        Rdbms.prepUpQuery(tableCreationScriptTable, new Object[]{});
                        jsonObj.put(curSchemaName.toString()+"-"+curTableName.toString(), tableCreationScriptTable);                    
                    }
                }
        }
        return jsonObj;
    }

    /**
     *
     * @param procedure
     * @param procVersion
     * @param procInstanceName
     * @return
     */
    public static final  JSONObject addProcedureSOPtoUsers(String procedure,  Integer procVersion, String procInstanceName){
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestinationProc=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName());
        Object[][] procEventSopsRecordsSource = Rdbms.getRecordFieldsByFilter(schemaNameDestinationProc, TblsProcedure.ProcedureEvents.TBL.getName(), 
                new String[]{TblsProcedure.ProcedureEvents.FLD_SOP.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{""}, 
                FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), new String[]{"sop"});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventSopsRecordsSource[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procEventSopsRecordsSource));
          return jsonObj;
        }
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procEventSopsRecordsSource.length);  
        
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
                    String diagnosesForLog = (sopRolePosic==-1) ? JsonTags.NO.getTagValue() : JsonTags.YES.getTagValue();
                    jsSopRoleObj.put("SOP "+sopFromArr+" exists for role "+roleFromArr+" ?", diagnosesForLog);
                    if (sopRolePosic==-1){
                        Object[][] procedureAddSopToUsersByRole = ProcedureDefinitionToInstanceUtility.procedureAddSopToUsersByRole(procedure, procVersion, procInstanceName, 
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
     * @param schemaNamePrefix - Procedure Instance where it applies
     * @return
     */
    public static final  JSONObject createDBProcessSchemas(String schemaNamePrefix){
        JSONObject jsonObj = new JSONObject();

        String methodName = "createDataBaseSchemas";       
        String[] schemaNames = new String[]{GlobalVariables.Schemas.CONFIG.getName(), GlobalVariables.Schemas.CONFIG_AUDIT.getName(), GlobalVariables.Schemas.DATA.getName(), GlobalVariables.Schemas.DATA_AUDIT.getName(), GlobalVariables.Schemas.PROCEDURE_AUDIT.getName(), GlobalVariables.Schemas.PROCEDURE.getName()};
         jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), schemaNames.length);     
        for (String fn:schemaNames){
            JSONArray jsSchemaArr = new JSONArray();
            String configSchemaName = schemaNamePrefix+"-"+fn;
            jsSchemaArr.add(configSchemaName);
            requirementsLogEntry("", methodName, configSchemaName,2);
            
            configSchemaName = LPPlatform.buildSchemaName(configSchemaName, fn);
            String configSchemaScript = "CREATE SCHEMA "+configSchemaName+"  AUTHORIZATION "+SCHEMA_AUTHORIZATION_ROLE+";"+
                    " GRANT ALL ON SCHEMA "+configSchemaName+" TO "+SCHEMA_AUTHORIZATION_ROLE+ ";";     
            Rdbms.prepUpQuery(configSchemaScript, new Object[]{});
            
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
     * @param schemaNamePrefix - Procedure Instance where it applies
     * @param tableName
     * @param fieldsName
     * @return
     */
    public static final  JSONObject createDBProcessTables(String schemaNamePrefix, String tableName, String[] fieldsName){
        JSONObject jsonObj = new JSONObject();        

        String tblCreateScript=TblsCnfg.Analysis.createTableScript(schemaNamePrefix, new String[]{""});
        //Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("Analysis", tblCreateScript);

        tblCreateScript=TblsCnfg.AnalysisMethod.createTableScript(schemaNamePrefix, new String[]{""});
        //Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("AnalysisMethod", tblCreateScript);

        tblCreateScript=TblsCnfg.AnalysisMethodParams.createTableScript(schemaNamePrefix, new String[]{""});
        //Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("AnalysisMethodParams", tblCreateScript);
        
        tblCreateScript=TblsProcedure.PersonProfile.createTableScript(schemaNamePrefix, new String[]{""});
//        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("PersonProfile", tblCreateScript);        
        
        tblCreateScript=TblsProcedure.ProcedureEvents.createTableScript(schemaNamePrefix, new String[]{""});
//        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("ProcedureEvents", tblCreateScript);
        
        tblCreateScript=TblsProcedure.ProcedureInfo.createTableScript(schemaNamePrefix, new String[]{""});
//        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("ProcedureInfo", tblCreateScript);

        tblCreateScript=TblsProcedure.ViewProcUserAndRoles.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("ViewProcUserAndRoles", tblCreateScript);
/*        
        tblCreateScript=TblsCnfg.Sample.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("Sample", tblCreateScript);

        tblCreateScript=TblsCnfg.SampleRules.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SampleRules", tblCreateScript);

        tblCreateScript=TblsCnfg.SopMetaData.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SopMetaData", tblCreateScript);
        
        tblCreateScript=TblsCnfg.Spec.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("Spec", tblCreateScript);

        tblCreateScript=TblsCnfg.SpecLimits.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SpecLimits", tblCreateScript);

        tblCreateScript=TblsCnfg.SpecRules.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SpecRules", tblCreateScript);

        tblCreateScript=TblsCnfg.UnitsOfMeasurement.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("UnitsOfMeasurement", tblCreateScript);        

        tblCreateScript=TblsCnfg.ViewAnalysisMethodsView.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("ViewAnalysisMethodsView", tblCreateScript);        

        tblCreateScript=TblsCnfg.zzzDbErrorLog.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsCnfg.DbErrorLog", tblCreateScript);        
        
                
        tblCreateScript=TblsData.Sample.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("Sample", tblCreateScript);        

        tblCreateScript=TblsData.SampleAnalysis.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SampleAnalysis", tblCreateScript);        
        
        tblCreateScript=TblsData.SampleAnalysisResult.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SampleAnalysisResult", tblCreateScript);        
        
        tblCreateScript=TblsData.SampleAliq.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SampleAliq", tblCreateScript);        
        
        tblCreateScript=TblsData.SampleAliqSub.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SampleAliqSub", tblCreateScript);        
        
        tblCreateScript=TblsData.SampleCoc.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("SampleCoc", tblCreateScript);                
        
        tblCreateScript=TblsData.UserAnalysisMethod.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("UserAnalysisMethod", tblCreateScript);               
        
        tblCreateScript=TblsData.UserSop.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("UserSop", tblCreateScript);               

        tblCreateScript=TblsData.ViewSampleCocNames.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("ViewSampleCocNames", tblCreateScript);               

        tblCreateScript=TblsData.ViewUserAndMetaDataSopView.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("ViewUserAndMetaDataSopView", tblCreateScript);               
        
        tblCreateScript=TblsData.ViewSampleAnalysisResultWithSpecLimits.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("ViewSampleAnalysisResultWithSpecLimits", tblCreateScript);                                 
                
        tblCreateScript=TblsDataAudit.Session.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("Session", tblCreateScript);          
        
        tblCreateScript=TblsDataAudit.Sample.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepUpQuery(tblCreateScript, new Object[]{});
        jsonObj.put("Sample", tblCreateScript);   
*/        
        return jsonObj;
     }        
    
    public static final  JSONArray createPropBusinessRules(String procedure,  Integer procVersion, String instanceName){
        String[] fieldsRequired=new String[]{TblsReqs.ProcedureBusinessRules.FLD_FILE_SUFFIX.getName(), TblsReqs.ProcedureBusinessRules.FLD_RULE_NAME.getName(), TblsReqs.ProcedureBusinessRules.FLD_RULE_VALUE.getName()};
        String diagnObjName="diagnostic";
        String[] fildsToGet=TblsReqs.ProcedureBusinessRules.getAllFieldNames();
        for (String curFldReq: fieldsRequired){
            if (!LPArray.valueInArray(fildsToGet, curFldReq)) LPArray.addValueToArray1D(fildsToGet, curFldReq);
        }
        JSONArray jsonArr = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        Object[][] procBusRules = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureBusinessRules.TBL.getName(), 
                new String[]{TblsReqs.ProcedureBusinessRules.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureBusinessRules.FLD_PROCEDURE_VERSION.getName(), TblsReqs.ProcedureBusinessRules.FLD_INSTANCE_NAME.getName(), TblsReqs.ProcedureBusinessRules.FLD_ACTIVE.getName()}, 
                new Object[]{procedure, procVersion, instanceName, true}, 
                fildsToGet, new String[]{});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procBusRules[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procBusRules[0]));
          jsonArr.add(jsonObj);
          return jsonArr;
        }
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procBusRules.length); 
        jsonArr.add(jsonObj);
        Parameter parm=new Parameter();
        Object[] procBusRulesFiles = LPArray.getColumnFromArray2D(procBusRules, LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureBusinessRules.FLD_FILE_SUFFIX.getName()));
        //String diagn=parm.addTagInPropertiesFile("PROCEDURE_BUSINESS_RULE",  "oil-pl1-config",  "hola",  "adios");
        String[] filesNames=LPArray.getUniquesArray(procBusRulesFiles);
        for (String curFile: filesNames){
            parm.createPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                    instanceName+"-"+curFile);  
        }        
        for (Object[] curProcEventSops: procBusRules){
            String diagn=parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  instanceName+"-"+LPNulls.replaceNull(curProcEventSops[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureBusinessRules.FLD_FILE_SUFFIX.getName())]).toString(),  
                    LPNulls.replaceNull(curProcEventSops[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureBusinessRules.FLD_RULE_NAME.getName())]).toString(),  
                    LPNulls.replaceNull(curProcEventSops[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureBusinessRules.FLD_RULE_VALUE.getName())]).toString());
            if (!LPArray.valueInArray(fildsToGet, diagnObjName))//{
                fildsToGet=LPArray.addValueToArray1D(fildsToGet, diagnObjName);
//                curProcEventSops=LPArray.addValueToArray1D(curProcEventSops, diagn);
//            }else
//                curProcEventSops[LPArray.valuePosicInArray(fildsToGet, diagnObjName)]=diagn;
            curProcEventSops=LPArray.addValueToArray1D(curProcEventSops, diagn);
            JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fildsToGet, curProcEventSops);
            //Object curProcEventName = curProcEventSops[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), TblsProcedure.ProcedureEvents.FLD_NAME.getName())];
            jsonArr.add(convertArrayRowToJSONObject);
        }

        //Build procedureActions and actionEnabled properties
        fildsToGet=new String[]{TblsReqs.ProcedureUserRequirements.FLD_WIDGET_ACTION.getName(), TblsReqs.ProcedureUserRequirements.FLD_ROLES.getName(), TblsReqs.ProcedureUserRequirements.FLD_ESIGN_REQ.getName(), TblsReqs.ProcedureUserRequirements.FLD_USERCONFIRM_REQ.getName()};
        Object[][] procActionsEnabledBusRules = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureUserRequirements.TBL.getName(), 
                new String[]{TblsReqs.ProcedureUserRequirements.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirements.FLD_PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRequirements.FLD_SCHEMA_PREFIX.getName(), TblsReqs.ProcedureUserRequirements.FLD_ACTIVE.getName(), TblsReqs.ProcedureUserRequirements.FLD_IN_SYSTEM.getName(), TblsReqs.ProcedureUserRequirements.FLD_IN_SCOPE.getName()}, 
                new Object[]{procedure, procVersion, instanceName, true, true, true}, 
                fildsToGet, new String[]{});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procActionsEnabledBusRules[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procActionsEnabledBusRules[0]));
          jsonArr.add(jsonObj);
          return jsonArr;
        }
        String allEnabledActions="";
        String allEsigReq="";
        String allUserConfirmReq="";
        for (Object[] curProcActionEnabled: procActionsEnabledBusRules){
            String curAction=LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.FLD_WIDGET_ACTION.getName())]).toString();
            if (curAction.length()>0){
                String esigReq=LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.FLD_ESIGN_REQ.getName())]).toString();
                String userConfirmReq=LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.FLD_USERCONFIRM_REQ.getName())]).toString();
                if ("true".equalsIgnoreCase(esigReq)) allEsigReq=allEsigReq+"|"+curAction;
                if ("true".equalsIgnoreCase(userConfirmReq)) allUserConfirmReq=allUserConfirmReq+"|"+curAction;

                String diagn=parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  instanceName+"-"+GlobalVariables.Schemas.PROCEDURE.getName(),  
                        LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName()+curAction,  
                        LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.FLD_ROLES.getName())]).toString());
                if (!LPArray.valueInArray(fildsToGet, diagnObjName))//{
                    fildsToGet=LPArray.addValueToArray1D(fildsToGet, diagnObjName);
    //                curProcEventSops=LPArray.addValueToArray1D(curProcEventSops, diagn);
    //            }else
    //                curProcEventSops[LPArray.valuePosicInArray(fildsToGet, diagnObjName)]=diagn;
                curProcActionEnabled=LPArray.addValueToArray1D(curProcActionEnabled, diagn);
                JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fildsToGet, curProcActionEnabled);
                //Object curProcEventName = curProcEventSops[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), TblsProcedure.ProcedureEvents.FLD_NAME.getName())];
                jsonArr.add(convertArrayRowToJSONObject);
                if (allEnabledActions.length()>0)allEnabledActions=allEnabledActions+"|";
                allEnabledActions=allEnabledActions+LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.FLD_WIDGET_ACTION.getName())]).toString();
            }
        }        
        String diagn=parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  instanceName+"-"+GlobalVariables.Schemas.PROCEDURE.getName(),  
                LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName(), allEnabledActions);
        diagn=parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  instanceName+"-"+GlobalVariables.Schemas.PROCEDURE.getName(),  
                LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName(), allEsigReq);
        diagn=parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  instanceName+"-"+GlobalVariables.Schemas.PROCEDURE.getName(),  
                LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getTagName(), allUserConfirmReq);
        
        return jsonArr;
    }

    public static final  JSONObject deployMasterData(String procedure,  Integer procVersion, String instanceName){
        JSONObject jsonObjSummary = new JSONObject();
        JSONArray jsonArr=new JSONArray();
        JSONObject jsonObj = new JSONObject();
         Object[][] procMasterDataObjs = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureMasterData.TBL.getName(), 
                new String[]{TblsReqs.ProcedureMasterData.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureMasterData.FLD_PROCEDURE_VERSION.getName(), TblsReqs.ProcedureMasterData.FLD_INSTANCE_NAME.getName(), TblsReqs.ProcedureMasterData.FLD_ACTIVE.getName()}, 
                        new Object[]{procedure, procVersion, instanceName, true}, 
                new String[]{TblsReqs.ProcedureMasterData.FLD_OBJECT_TYPE.getName(), TblsReqs.ProcedureMasterData.FLD_JSON_OBJ.getName()});
        JSONArray jsonRowArr=new JSONArray();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procMasterDataObjs[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procMasterDataObjs[0]));
          jsonArr.add(jsonObj);
        }else{
            jsonArr.add(jsonObj);
            for (Object[] curRow: procMasterDataObjs){
                ClassMasterData clssMD= new ClassMasterData(instanceName, curRow[0].toString(), curRow[1].toString());
                JSONObject jsonRowObj = new JSONObject();
                jsonRowObj.put(curRow[0], clssMD.getDiagnostic()[clssMD.getDiagnostic().length-1]);
                jsonRowArr.add(jsonRowObj);
            }            
        }
        jsonObjSummary.put("summary", jsonRowArr);
        return jsonObjSummary;
    }

}
