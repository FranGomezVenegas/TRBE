/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig.TablesEnvMonitConfig;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfigAudit.TablesEnvMonitConfigAudit;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData.TablesEnvMonitData;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData.ViewsEnvMonData;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitDataAudit.TablesEnvMonitDataAudit;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitProcedure.TablesEnvMonitProcedure;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import static databases.Rdbms.insertRecordInTableFromTable;
import databases.SqlStatement.WHERECLAUSE_TYPES;
import databases.TblsApp;
import databases.TblsAppConfig;
import databases.TblsCnfg;
import databases.TblsCnfg.TablesConfig;
import databases.TblsCnfgAudit.TablesCfgAudit;
import databases.TblsData.TablesData;
import databases.TblsData.ViewsData;
import databases.TblsDataAudit.TablesDataAudit;
import databases.TblsProcedure;
import databases.TblsProcedure.TablesProcedure;
import databases.TblsProcedureAudit;
import databases.TblsReqs;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.requirement.masterdata.ClassMasterData;
import java.util.Arrays;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform.LpPlatformBusinessRules;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import static trazit.enums.deployrepository.DeployTables.createTableScript;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author Administrator
 */
public class ProcedureDefinitionToInstance {
    
    public static final String[] ProcedureSchema_TablesWithNoTestingClone=new String[]{TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(),
        TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(), TblsProcedure.ViewProcUserAndRoles.TBL.getName(), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName()};
    
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
    public static final String FIELDS_TO_RETRIEVE_REQS_PROCEDURE_INFO_SOURCE=TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName()+"|"+TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName()+"|"+TblsReqs.ProcedureInfo.LABEL_EN.getName()+"|"+TblsReqs.ProcedureInfo.LABEL_ES.getName();
    public static final String FIELDS_TO_RETRIEVE_PROCEDURE_INFO_SOURCE=TblsProcedure.ProcedureInfo.NAME.getName()+"|"+TblsProcedure.ProcedureInfo.VERSION.getName()+"|"+TblsProcedure.ProcedureInfo.LABEL_EN.getName()+"|"+TblsProcedure.ProcedureInfo.LABEL_ES.getName();

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
    public static final String FIELDS_TO_INSERT_APP_USER_PROCESS=TblsApp.UserProcess.USER_NAME.getName()+"|"+TblsApp.UserProcess.PROC_NAME.getName()+"|"+TblsApp.UserProcess.ACTIVE.getName();

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
        JSONObject jsonErrorObj = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        String schemaNameDestinationProc=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName());
         Object[][] procInfoRecordsSource = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(), 
                new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(),TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName}, 
                FIELDS_TO_RETRIEVE_REQS_PROCEDURE_INFO_SOURCE.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfoRecordsSource[0][0].toString())){
          jsonErrorObj.put("Record in requirements", "Not exists");  
          jsonErrorObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procInfoRecordsSource[0]));
        }else{
            jsonErrorObj.put("Record in requirements", "Found");  
            for (Object[] curRow: procInfoRecordsSource){
                Object[][] procInfoRecordsDestination = Rdbms.getRecordFieldsByFilter(schemaNameDestinationProc, TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(), 
                       new String[]{TblsProcedure.ProcedureInfo.NAME.getName(), TblsProcedure.ProcedureInfo.VERSION.getName()}, new Object[]{procedure, procVersion}, 
                       FIELDS_TO_RETRIEVE_PROCEDURE_INFO_SOURCE.split("\\|"));
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfoRecordsDestination[0][0].toString())){
                    jsonErrorObj.put("Record in the new instance", "Already exists");
                }else{
                    jsonErrorObj.put("Record in new instance", "Not exists");
                    String[] fldName=FIELDS_TO_RETRIEVE_PROCEDURE_INFO_SOURCE.split("\\|");
                    Object[] fldValue=curRow;
                    if (!LPArray.valueInArray(fldName, TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName())){
                        fldName=LPArray.addValueToArray1D(fldName, TblsProcedure.ProcedureInfo.PROC_INSTANCE_NAME.getName());
                        fldValue=LPArray.addValueToArray1D(fldValue, procInstanceName);                        
                    }
                    Object[] insertRecordInTable = Rdbms.insertRecordInTable(schemaNameDestinationProc, TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(), fldName, fldValue);
                    jsonObj = new JSONObject();
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(insertRecordInTable[0].toString())){
                        jsonObj.put("Record inserted in the new instance?", true);
                        return jsonObj;
                    }else{
                        jsonObj.put("Record inserted in the new instance?", false);
                        jsonObj.put("error_detail", jsonErrorObj);
                        return jsonObj;
                    }
                }
            }
        }
        jsonObj.put("Record inserted in the instance?", false);
        jsonObj.put("error_detail", jsonErrorObj);
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
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_USER_REQS_EVENTS.getTableFields()),
                    GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS_EVENTS.getTableName(), 
                new String[]{TblsReqs.ProcedureUserRequirementsEvents.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirementsEvents.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRequirementsEvents.PROC_INSTANCE_NAME.getName()},
                new Object[]{procedure, procVersion, procInstanceName},
                LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), 
                    TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(), getAllFieldNames(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableFields()));

        JSONObject jsonObj = new JSONObject();
//        jsonObj.put("Diagnostic from createDBProcedureEvents", insertRecordInTableFromTable[0].toString());
        String[] procEventFldNamesToGet=getAllFieldNames(TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableFields());
        Object[][] procEventRows = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(), 
                new String[]{TblsProcedure.ProcedureEvents.ROLE_NAME.getName(), WHERECLAUSE_TYPES.OR.getSqlClause()+" "+TblsProcedure.ProcedureEvents.ROLE_NAME.getName()+" "+WHERECLAUSE_TYPES.LIKE}, 
                new Object[]{"ALL", "%|%"}, 
                procEventFldNamesToGet);
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventRows[0][0].toString())){
            Object[][] procRoles = new Object[][]{{}};
                Object[][] procRolesAllRoles = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_ROLES.getTableName(), 
                    new String[]{TblsReqs.ProcedureRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName()},
                    new Object[]{procedure, procVersion, procInstanceName},
                    new String[]{TblsReqs.ProcedureRoles.ROLE_NAME.getName()});
            for (Object[] curProcEvent: procEventRows){
                if ("ALL".equalsIgnoreCase(curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ROLE_NAME.getName())].toString()))
                    procRoles=procRolesAllRoles;
                else
                    procRoles=LPArray.array1dTo2d(curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ROLE_NAME.getName())].toString().split("\\|"), 1);

                for (int i=0;i<procRoles.length;i++){
                    if (i==0){
                        Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(),
                            new String[]{TblsProcedure.ProcedureEvents.ROLE_NAME.getName()}, new Object[]{procRoles[0][0].toString()},
                            new String[]{TblsProcedure.ProcedureEvents.ROLE_NAME.getName(), TblsProcedure.ProcedureEvents.NAME.getName()}, new Object[]{curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ROLE_NAME.getName())].toString(), curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.NAME.getName())]});
                updateRecordFieldsByFilter=new Object[]{};
                    }else{
                    
                        curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.ROLE_NAME.getName())]=procRoles[i][0].toString();
                        Object[] insertRecordInTable = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(), 
                                procEventFldNamesToGet, curProcEvent);
                    }
                }
/*                Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(),
                        new String[]{TblsProcedure.ProcedureEvents.ROLE_NAME.getName()}, new Object[]{procRoles[0][0].toString()},
                        new String[]{TblsProcedure.ProcedureEvents.PROCEDURE_NAME.getName()}, new Object[]{curProcEvent[LPArray.valuePosicInArray(procEventFldNamesToGet, TblsProcedure.ProcedureEvents.PROCEDURE_NAME.getName())]});
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
         Object[][] procUserRolesRecordsSource = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USER_ROLES.getTableName(), 
                new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(),TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName}, 
                FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SOURCE.split("\\|"), FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SORT.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserRolesRecordsSource[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procUserRolesRecordsSource[0]));
          return jsonObj;
        }
        for (Object[] curRow: procUserRolesRecordsSource){
            Object curUserName = curRow[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SOURCE.split("\\|"), TblsReqs.ProcedureUserRoles.USER_NAME.getName())];
            Object curRoleName = curRow[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROCEDURE_USER_ROLE_SOURCE.split("\\|"), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName())];
            JSONArray jsArr = new JSONArray(); 
            JSONObject jsUserRoleObj = new JSONObject();
            jsUserRoleObj.put("User", curUserName); jsUserRoleObj.put("Role", curRoleName);

            Object[][] existsAppUser = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                    new String[]{TblsApp.Users.USER_NAME.getName()}, new Object[]{curUserName.toString()}, new String[]{TblsApp.Users.PERSON_NAME.getName()});
            String diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())) ? JsonTags.NO.getTagValue() : JsonTags.YES.getTagValue();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())){
                String personId=curUserName+"z";
                
                Object[] insertRecordInTable=Rdbms.insertRecordInTable(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), 
                    new String[]{TblsAppConfig.Person.PERSON_ID.getName(), TblsAppConfig.Person.FIRST_NAME.getName(), 
                        TblsAppConfig.Person.LAST_NAME.getName(), TblsAppConfig.Person.PHOTO.getName()}, 
                    new Object[]{personId, "I'm a user demo", "for demos ", "https://hasta-pronto.ru/wp-content/uploads/2014/09/chibcha.jpg"});
                insertRecordInTable = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), 
                        new String[]{TblsApp.Users.USER_NAME.getName(), TblsApp.Users.EMAIL.getName(), TblsApp.Users.ESIGN.getName(),
                            TblsApp.Users.PASSWORD.getName(), TblsApp.Users.PERSON_NAME.getName()},
                        new Object[]{curUserName, "trazit.info@gmail.com", "firmademo", "1234", personId});
                existsAppUser=LPArray.array1dTo2d(insertRecordInTable,1);
                diagnosesForLog=diagnosesForLog+" trying to create, log for creation="+insertRecordInTable[0].toString();
//                insertRecordInTable=Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USER_PROCESS.getTableName(), 
//                    new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName(), TblsApp.UserProcess.ACTIVE.getName()}, 
//                    new Object[]{curUserName, fakeProcName, true});
                
                // Place to create the user
            }                
            jsUserRoleObj.put("User exists in the app after running this logic?", LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnosesForLog)); 
            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(Arrays.toString(existsAppUser[0]))){
                Object[] existsAppUserProcess = Rdbms.existsRecord(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USER_PROCESS.getTableName(), 
                        new String[]{TblsApp.UserProcess.USER_NAME.getName(), TblsApp.UserProcess.PROC_NAME.getName()}, new Object[]{curUserName.toString(), procInstanceName});
                jsonObj.put("User was added to the Process at the App level?",  LPPlatform.LAB_TRUE.equalsIgnoreCase(existsAppUserProcess[0].toString()));  
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUserProcess[0].toString())){
                    Object[] insertRecordInTable = Rdbms.insertRecordInTable(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USER_PROCESS.getTableName(), 
                            FIELDS_TO_INSERT_APP_USER_PROCESS.split("\\|"), new Object[]{curUserName.toString(), procInstanceName, true});
                    jsonObj.put("Added the User to the Process at the App level by running this utility?", insertRecordInTable[0].toString());                                                                
                }
            }
            Object curPersonName = existsAppUser[0][0];                
            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())){
                Object[] insertRecordInTable = Rdbms.insertRecordInTable(schemaNameDestinationProcedure, TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), 
                        FIELDS_TO_INSERT_PROCEDURE_USER_ROLE_DESTINATION.split("\\|"), new Object[]{curPersonName.toString(), curRoleName.toString(), true});
                jsonObj.put("User Role inserted in the instance?", insertRecordInTable[0].toString());                    
            }
            jsArr.add(jsUserRoleObj);
            jsonObj.put("User "+curUserName+ " & Role "+curRoleName, jsArr);
        }                            
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procUserRolesRecordsSource.length);    
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
        
         Object[][] procSopMetaDataRecordsSource = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(), 
                new String[]{TblsReqs.ProcedureSopMetaData.PROCEDURE_NAME.getName(), TblsReqs.ProcedureSopMetaData.PROCEDURE_VERSION.getName(),TblsReqs.ProcedureSopMetaData.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName}, 
                FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE.split("\\|"), FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SORT.split("\\|"));
        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procSopMetaDataRecordsSource[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procSopMetaDataRecordsSource));
          return jsonObj;
        }
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procSopMetaDataRecordsSource.length);        
        for (Object[] curSopMetaData: procSopMetaDataRecordsSource){
            Object curSopId = curSopMetaData[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE.split("\\|"), TblsCnfg.SopMetaData.SOP_ID.getName())];
            Object curSopName = curSopMetaData[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE.split("\\|"), TblsCnfg.SopMetaData.SOP_NAME.getName())];
            JSONArray jsArr = new JSONArray(); 
            JSONObject jsUserRoleObj = new JSONObject();
            jsUserRoleObj.put("SOP Id", curSopId); jsUserRoleObj.put("SOP Name", curSopName);

            Object[][] existsAppUser = Rdbms.getRecordFieldsByFilter(schemaNameDestination, TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(), 
                    new String[]{TblsCnfg.SopMetaData.SOP_NAME.getName()}, new Object[]{curSopName.toString()}, new String[]{TblsCnfg.SopMetaData.SOP_NAME.getName()});
            String diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())) ? JsonTags.NO.getTagValue() : JsonTags.YES.getTagValue();
            jsUserRoleObj.put("SOP exists in the procedure?", diagnosesForLog); 
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())){
                Object[] insertRecordInTable = Rdbms.insertRecordInTable(schemaNameDestination, TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(), 
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
        
         Object[][] procModuleTablesAndFieldsSource = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(), 
                new String[]{TblsReqs.ProcedureModuleTables.ACTIVE.getName(), TblsReqs.ProcedureModuleTables.PROCEDURE_NAME.getName(), TblsReqs.ProcedureModuleTables.PROCEDURE_VERSION.getName(),TblsReqs.ProcedureModuleTables.PROC_INSTANCE_NAME.getName()}, 
                new Object[]{true, procedure, procVersion, procInstanceName}, 
                getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), 
                new String[]{TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName(), TblsReqs.ProcedureModuleTables.ORDER_NUMBER.getName()});        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procModuleTablesAndFieldsSource[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procModuleTablesAndFieldsSource[0]));
          return jsonObj;
        }
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procModuleTablesAndFieldsSource.length);  
        JSONObject errorsOnlyObj=new JSONObject();
        for (Object[] curModuleTablesAndFields: procModuleTablesAndFieldsSource){
            JSONObject curTblJsonObj=new JSONObject();
            String tableCreationScriptTable = "";
            String curSchemaName = LPNulls.replaceNull(curModuleTablesAndFields[LPArray.valuePosicInArray(getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName())]).toString();
            String curTableName = LPNulls.replaceNull(curModuleTablesAndFields[LPArray.valuePosicInArray(getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), TblsReqs.ProcedureModuleTables.TABLE_NAME.getName())]).toString();
            String curFieldName = LPNulls.replaceNull(curModuleTablesAndFields[LPArray.valuePosicInArray(getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), TblsReqs.ProcedureModuleTables.FIELD_NAME.getName())]).toString();
            String curIsView = LPNulls.replaceNull(curModuleTablesAndFields[LPArray.valuePosicInArray(getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields()), TblsReqs.ProcedureModuleTables.IS_VIEW.getName())]).toString();
            curTblJsonObj.put("table_name", curTableName);
            curTblJsonObj.put("repository_name", curSchemaName);
            curTblJsonObj.put("fields_name", curFieldName);
            Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
            String diagn="";
            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(dbTableExists[0].toString()))
                curTblJsonObj.put("diagnostic", "table already exists in this repository");
            else{
                diagn ="table NOT exists in this repository";
                String tblCreateScript =null;
                switch (moduleName.toUpperCase()){
                    case "ENVIRONMENTAL_MONITORING":       
                        Boolean cont=true;
                        try{
                            switch (curSchemaName.toString().toLowerCase()){                            
                                case "data":
                                    if (curIsView==null || !Boolean.valueOf(curIsView)){
                                        try{
                                            tblCreateScript = createTableScript(TablesEnvMonitData.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName));
                                        }catch(Exception e){
                                            tblCreateScript = createTableScript(TablesData.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName));
                                        }
                                    }else{
                                        try{
                                            tblCreateScript = ViewsEnvMonData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                        }catch(Exception e){
                                            tblCreateScript = ViewsData.valueOf(curTableName.toUpperCase()).getViewCreatecript();
                                        }                                        
                                    }
                                    break;
                                case "config":
                                    try{
                                        tblCreateScript = createTableScript(TablesEnvMonitConfig.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName));
                                    }catch(Exception e){                                        
                                        tblCreateScript = createTableScript(TablesConfig.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName));
                                    }
                                    break;
                                case "data-audit":
                                    try{
                                        tblCreateScript = createTableScript(TablesEnvMonitDataAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName));
                                    }catch(Exception e){
                                        tblCreateScript = createTableScript(TablesDataAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName));
                                    }
                                    break;
                                case "config-audit":
                                    try{
                                        tblCreateScript = createTableScript(TablesEnvMonitConfigAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName));
                                    }catch(Exception e){
                                        tblCreateScript = createTableScript(TablesCfgAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName));                                        
                                    }
                                    break;
                                case "procedure":
                                    try{
                                        tblCreateScript = createTableScript(TablesEnvMonitProcedure.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName));
                                    }catch(Exception e){
                                        tblCreateScript = createTableScript(TablesProcedure.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName));                                        
                                    }
                                    break;
                                case "procedure-audit":
                                    tblCreateScript = createTableScript(TblsProcedureAudit.TablesProcedureAudit.valueOf(curTableName.toUpperCase()), LPPlatform.buildSchemaName(procInstanceName, curSchemaName));
                                    break;
                                default:
                                    cont=false;
                                    curTblJsonObj.put("unexpected_error", "repository "+curSchemaName+" not recognized");
                            }                    
                        }catch(Exception e){
                            cont=false;
                            curTblJsonObj.put("unexpected_error", e.getMessage());
                        }
                        if (cont){
                            Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(tblCreateScript, new Object[]{});
                            if ("-999".equalsIgnoreCase(prepUpQuery[0].toString()))
                                diagn=diagn+" and not created, "+prepUpQuery[prepUpQuery.length-1];                                
                            else
                                diagn=diagn+" and created";
                            curTblJsonObj.put("diagnostic", diagn);
                            JSONObject scriptLog=new JSONObject();
                            scriptLog.put("1) creator_diagn", prepUpQuery[prepUpQuery.length-1]);
                            scriptLog.put("1) script", tblCreateScript);                            
                            String schemaForTesting = Rdbms.suffixForTesting(LPPlatform.buildSchemaName(procInstanceName, curSchemaName), curTableName);
                            if (!schemaForTesting.equalsIgnoreCase(LPPlatform.buildSchemaName(procInstanceName, curSchemaName))){
                                curTblJsonObj.put("requires_testing_clone", true);
                                tblCreateScript = createTableScript(TablesEnvMonitData.valueOf(curTableName.toUpperCase()), schemaForTesting);
                                prepUpQuery = Rdbms.prepUpQueryWithDiagn(tblCreateScript, new Object[]{});
                                scriptLog.put("2) script_testing", tblCreateScript);
                                scriptLog.put("2) creator_diagn_testing", prepUpQuery[prepUpQuery.length-1]);
                            }else
                                curTblJsonObj.put("requires_testing_clone", false);

                            if (prepUpQuery[prepUpQuery.length-1].toString().toLowerCase().contains("error"))
                                errorsOnlyObj.put(curSchemaName+"."+curTableName, scriptLog);
                            curTblJsonObj.put("scripts_detail", scriptLog);                                
                        }
                        /*                    if (GlobalVariables.Schemas.CONFIG.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitConfig.getTableCreationScriptFromConfigTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.CONFIG_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitConfigAudit.getTableCreationScriptFromConfigAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitData.getTableCreationScriptFromDataTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.DATA_AUDIT.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitDataAudit.getTableCreationScriptFromDataAuditTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
                        if (GlobalVariables.Schemas.PROCEDURE.getName().equalsIgnoreCase(curSchemaName.toString()))
                            tableCreationScriptTable = TblsEnvMonitProcedure.getTableCreationScriptFromDataProcedureTableEnvMonit(curTableName.toString(), procInstanceName, curFieldName.toString().split("\\|"));
    */                    break;


                    case "INSPECTION_LOT_RAW_MATERIAL":
    /*                    if (GlobalVariables.Schemas.CONFIG.getName().equalsIgnoreCase(curSchemaName.toString())){
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
    */                    break;
                    case "GENOME":
                        break;
                    default: 
                        tableCreationScriptTable="";
                        break;
                }
            }
        jsonObj.put(curSchemaName+"-"+curTableName, curTblJsonObj);
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
        Object[][] procEventSopsRecordsSource = Rdbms.getRecordFieldsByFilter(schemaNameDestinationProc, TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(), 
                new String[]{TblsProcedure.ProcedureEvents.SOP.getName()+WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{""}, 
                FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), new String[]{"sop"});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procEventSopsRecordsSource[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), LPJson.convertToJSON(procEventSopsRecordsSource));
          return jsonObj;
        }
        jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procEventSopsRecordsSource.length);  
        
        String[] existingSopRole = new String[0];
        for (Object[] curProcEventSops: procEventSopsRecordsSource){
            Object curProcEventName = curProcEventSops[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), TblsProcedure.ProcedureEvents.NAME.getName())];
            Object curSops = curProcEventSops[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), TblsProcedure.ProcedureEvents.SOP.getName())];
            Object curRoleName = curProcEventSops[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), TblsProcedure.ProcedureEvents.ROLE_NAME.getName())];
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

        String[] schemaNames = new String[]{GlobalVariables.Schemas.CONFIG.getName(), GlobalVariables.Schemas.CONFIG_AUDIT.getName(), GlobalVariables.Schemas.DATA.getName(), GlobalVariables.Schemas.DATA_AUDIT.getName(), GlobalVariables.Schemas.PROCEDURE_AUDIT.getName(), GlobalVariables.Schemas.PROCEDURE.getName()};
         jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), schemaNames.length);     
        for (String fn:schemaNames){
            JSONArray jsSchemaArr = new JSONArray();
            String configSchemaName = schemaNamePrefix+"-"+fn;
            jsSchemaArr.add(configSchemaName);
            
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
        return jsonObj;
     }        
    public static final  JSONArray createBusinessRules(String procedure,  Integer procVersion, String instanceName){
        String[] fieldsRequired=new String[]{TblsReqs.ProcedureBusinessRules.FILE_SUFFIX.getName(), TblsReqs.ProcedureBusinessRules.RULE_NAME.getName(), TblsReqs.ProcedureBusinessRules.RULE_VALUE.getName()};
        String diagnObjName="diagnostic";
        String[] fildsToGet=new String[]{TblsReqs.ProcedureBusinessRules.FILE_SUFFIX.getName(), TblsReqs.ProcedureBusinessRules.RULE_NAME.getName(),
        TblsReqs.ProcedureBusinessRules.RULE_VALUE.getName()};
        for (String curFldReq: fieldsRequired){
            if (!LPArray.valueInArray(fildsToGet, curFldReq)) LPArray.addValueToArray1D(fildsToGet, curFldReq);
        }
        JSONArray jsonArr = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        Object[][] procBusRules = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_BUS_RULES.getTableName(), 
                new String[]{TblsReqs.ProcedureBusinessRules.PROCEDURE_NAME.getName(), TblsReqs.ProcedureBusinessRules.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureBusinessRules.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureBusinessRules.ACTIVE.getName()}, 
                new Object[]{procedure, procVersion, instanceName, true}, 
                fildsToGet, new String[]{});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procBusRules[0][0].toString())){
          jsonObj.put(JsonTags.ERROR.getTagValue(), RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND.getErrorCode());
          jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), 0); 
          jsonArr.add(jsonObj);
          return jsonArr;
        }else{
            jsonObj.put(JsonTags.NUM_RECORDS_IN_DEFINITION.getTagValue(), procBusRules.length); 
        }
        jsonArr.add(jsonObj);
        Parameter parm=new Parameter();
        Object[] procBusRulesFiles = LPArray.getColumnFromArray2D(procBusRules, LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureBusinessRules.FILE_SUFFIX.getName()));
        String[] filesNames=LPArray.getUniquesArray(procBusRulesFiles);
/*        for (String curFile: filesNames){
            parm.createPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  
                    instanceName+"-"+curFile);  
        }        */
        for (Object[] curprocBusRules: procBusRules){
            Object[] diagn = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), 
                    new String[]{TblsProcedure.ProcedureBusinessRules.AREA.getName(), TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(), TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()}, 
                    curprocBusRules);
            curprocBusRules=LPArray.addValueToArray1D(curprocBusRules, diagn);
            JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fildsToGet, curprocBusRules);
            //Object curProcEventName = curProcEventSops[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), TblsProcedure.ProcedureEvents.PROCEDURE_NAME.getName())];
            jsonArr.add(convertArrayRowToJSONObject);
        }
        //Build procedureActions and actionEnabled properties
        fildsToGet=new String[]{TblsReqs.ProcedureUserRequirements.WIDGET_ACTION.getName(), TblsReqs.ProcedureUserRequirements.ROLES.getName(), TblsReqs.ProcedureUserRequirements.ESIGN_REQ.getName(), TblsReqs.ProcedureUserRequirements.USERCONFIRM_REQ.getName()};
        Object[][] procActionsEnabledBusRules = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName(), 
                new String[]{TblsReqs.ProcedureUserRequirements.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirements.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUserRequirements.ACTIVE.getName(), TblsReqs.ProcedureUserRequirements.IN_SYSTEM.getName(), TblsReqs.ProcedureUserRequirements.IN_SCOPE.getName()}, 
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
            String curAction=LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.WIDGET_ACTION.getName())]).toString();
            if (curAction.length()>0){
                String esigReq=LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.ESIGN_REQ.getName())]).toString();
                String userConfirmReq=LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.USERCONFIRM_REQ.getName())]).toString();
                if ("true".equalsIgnoreCase(esigReq)) allEsigReq=allEsigReq+"|"+curAction;
                if ("true".equalsIgnoreCase(userConfirmReq)) allUserConfirmReq=allUserConfirmReq+"|"+curAction;

                Object[] diagn = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(instanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), 
                        new String[]{TblsProcedure.ProcedureBusinessRules.AREA.getName(), TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName(), TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName()}, 
                        new Object[]{GlobalVariables.Schemas.PROCEDURE.getName(), LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName()+curAction, LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.ROLES.getName())]).toString()});
/*                
                String diagn=parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),  instanceName+"-"+GlobalVariables.Schemas.PROCEDURE.getName(),  
                        LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName()+curAction,  
                        LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.ROLES.getName())]).toString());
                if (!LPArray.valueInArray(fildsToGet, diagnObjName))//{
                    fildsToGet=LPArray.addValueToArray1D(fildsToGet, diagnObjName);
*/                
    //                curProcEventSops=LPArray.addValueToArray1D(curProcEventSops, diagn);
    //            }else
    //                curProcEventSops[LPArray.valuePosicInArray(fildsToGet, diagnObjName)]=diagn;
                curProcActionEnabled=LPArray.addValueToArray1D(curProcActionEnabled, diagn);
                JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fildsToGet, curProcActionEnabled);
                //Object curProcEventName = curProcEventSops[LPArray.valuePosicInArray(FIELDS_TO_RETRIEVE_PROC_EVENT_DESTINATION.split("\\|"), TblsProcedure.ProcedureEvents.PROCEDURE_NAME.getName())];
                jsonArr.add(convertArrayRowToJSONObject);
                if (allEnabledActions.length()>0)allEnabledActions=allEnabledActions+"|";
                allEnabledActions=allEnabledActions+LPNulls.replaceNull(curProcActionEnabled[LPArray.valuePosicInArray(fildsToGet, TblsReqs.ProcedureUserRequirements.WIDGET_ACTION.getName())]).toString();
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
        try{
            JSONArray jsonArr=new JSONArray();
            JSONObject jsonObj = new JSONObject();
             Object[][] procMasterDataObjs = Rdbms.getRecordFieldsByFilter(GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MASTER_DATA.getTableName(), 
                new String[]{TblsReqs.ProcedureMasterData.PROCEDURE_NAME.getName(), TblsReqs.ProcedureMasterData.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureMasterData.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureMasterData.ACTIVE.getName()}, 
                    new Object[]{procedure, procVersion, instanceName, true}, 
                new String[]{TblsReqs.ProcedureMasterData.OBJECT_TYPE.getName(), TblsReqs.ProcedureMasterData.JSON_OBJ.getName()},
                new String[]{TblsReqs.ProcedureMasterData.ORDER_NUMBER.getName()});
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
        }catch(Exception e){
            return (JSONObject) jsonObjSummary.put("error", e.getMessage());
        }
    }

}
