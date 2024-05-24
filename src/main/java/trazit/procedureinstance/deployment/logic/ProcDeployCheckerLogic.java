/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.deployment.logic;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.app.TestingRegressionUAT;
import trazit.procedureinstance.deployment.definition.ProcDeployEnums;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsCnfg;
import databases.TblsProcedure;
import trazit.procedureinstance.definition.definition.TblsReqs;
import static trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections.FLDSTORETR_PROCEDURE_INFO_SOURCE;
import static trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections.FLDSTORETR_REQS_PROCINFOSRC;
import functionaljavaa.requirement.masterdata.ClassMasterData;
import functionaljavaa.user.UserAndRolesViews;
import java.util.Map;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.JSONArray;
import trazit.globalvariables.GlobalVariables;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntTables;
import trazit.session.InternalMessage;

public class ProcDeployCheckerLogic {
    private ProcDeployCheckerLogic() {throw new IllegalStateException("Utility class");}
    private static JSONObject publishJson(Boolean anyMismatch, JSONObject mismatchesObj, JSONObject detailsObj){
        JSONObject mainObj=new JSONObject();
        if (anyMismatch==null)
            mainObj.put("status", "under development");
        else{
            if (Boolean.FALSE.equals(anyMismatch)){
                mainObj.put("pass", "yes");
                mainObj.put("pass_icon", "/images/Pass.jpg");
            }else{
                mainObj.put("pass", "no");
                mainObj.put("pass_icon", "/images/NotPass.png");
            }
            if (Boolean.FALSE.equals(detailsObj.isEmpty()))
                mainObj.put("detail", detailsObj);
            if (Boolean.FALSE.equals(mismatchesObj.isEmpty()))
                mainObj.put("mismatches_detail", mismatchesObj);
        }
        return mainObj;        
    }
    
    public static JSONObject createModuleSchemasAndBaseTables(String procInstanceName){
        Boolean anyMismatch=false;
        JSONObject detailsObj=new JSONObject();
        JSONObject mismatchesObj=new JSONObject();
        String[] schemaNames=ProcDeployEnums.moduleBaseSchemas(procInstanceName);
        schemaNames=LPArray.getUniquesArray(schemaNames);
        for (int i=0;i<schemaNames.length;i++)
            schemaNames[i]=schemaNames[i].replace("\"", "");
        detailsObj.put("expected_and_checked_repositories", LPJson.convertToJSON(schemaNames));
        Object[] dbSchemasList = Rdbms.dbSchemasList(procInstanceName);
        dbSchemasList=LPArray.getUniquesArray(dbSchemasList);
        Map<String, Object[]> evaluateValuesAreInArray = LPArray.evaluateValuesAreInArray(dbSchemasList, schemaNames);
        String evaluation= evaluateValuesAreInArray.keySet().iterator().next();        
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(evaluation))){
            anyMismatch=true;
            Object[] missingObjects = evaluateValuesAreInArray.get(evaluation);
            mismatchesObj.put("missing_schemas", LPJson.convertToJSONArray(missingObjects));
        }
        EnumIntTables[] moduleBaseTables = ProcDeployEnums.moduleBaseTables();
        String[] moduleBaseTablesArr=new String[]{};
        for (EnumIntTables curTbl: moduleBaseTables)
            moduleBaseTablesArr=LPArray.addValueToArray1D(moduleBaseTablesArr, 
                LPPlatform.buildSchemaName(procInstanceName, curTbl.getRepositoryName()).replace("\"", "")+"."+curTbl.getTableName());
        detailsObj.put("expected_and_checked_tables", LPJson.convertToJSON(moduleBaseTablesArr));
        Object[] dbSchemasTablesList = Rdbms.dbSchemaAndTableList(procInstanceName);
        dbSchemasTablesList=LPArray.getUniquesArray(dbSchemasTablesList);
        evaluateValuesAreInArray = LPArray.evaluateValuesAreInArray(dbSchemasTablesList, moduleBaseTablesArr);
        evaluation= evaluateValuesAreInArray.keySet().iterator().next();        
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(evaluation))){
            anyMismatch=true;
            Object[] missingObjects = evaluateValuesAreInArray.get(evaluation);
            mismatchesObj.put("missing_tables", LPJson.convertToJSONArray(missingObjects));
        }
        return publishJson(anyMismatch, mismatchesObj, detailsObj);
    }    
    
    public static final JSONObject createDBProcedureInfo(String procedure,  Integer procVersion, String procInstanceName){
        Boolean anyMismatch=false;
        JSONObject detailsObj=new JSONObject();
        JSONObject mismatchesObj=new JSONObject();        
        Object[][] procInfoRecordsDestination = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsProcedure.TablesProcedure.PROCEDURE_INFO.getRepositoryName()), TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(), 
               new String[]{TblsProcedure.ProcedureInfo.NAME.getName(), TblsProcedure.ProcedureInfo.VERSION.getName()}, new Object[]{procedure, procVersion}, 
               FLDSTORETR_PROCEDURE_INFO_SOURCE.split("\\|"));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfoRecordsDestination[0][0].toString())){
            anyMismatch=true;
            String errMsg="Not Deployed yet, there is no record in table "+TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName();
            Object[][] procInfoRecordsSource = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(), 
               new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(),TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName}, 
               FLDSTORETR_REQS_PROCINFOSRC.split("\\|"));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procInfoRecordsSource[0][0].toString()))
                errMsg=errMsg+". Not found the info in the requirements definition neither";
            else{
                errMsg=errMsg+". Found the info in the requirements definition neither";
                detailsObj.put("In requirements", 
                    LPJson.convertArrayRowToJSONObject(FLDSTORETR_REQS_PROCINFOSRC.split("\\|"), procInfoRecordsSource[0]));                
            }
            mismatchesObj.put(GlobalAPIsParams.LBL_ERROR, errMsg);
        }else
            detailsObj.put(TblsProcedure.TablesProcedure.PROCEDURE_INFO.getTableName(), 
                LPJson.convertArrayRowToJSONObject(FLDSTORETR_PROCEDURE_INFO_SOURCE.split("\\|"), procInfoRecordsDestination[0]));
        return publishJson(anyMismatch, mismatchesObj, detailsObj);
    }     

    public static final  JSONObject createDBPersonProfiles(String procedure,  Integer procVersion, String procInstanceName){
        Boolean anyMismatch=false;
        JSONObject detailsObj=new JSONObject();
        JSONObject mismatchesObj=new JSONObject();   
        JSONArray personProfilesDest = new JSONArray();
        JSONArray procUserRolesSource = new JSONArray();
        String[] personProfilesDestFlds = EnumIntTableFields.getAllFieldNames(TblsProcedure.TablesProcedure.PERSON_PROFILE);

        Object[][] personProfileRecordsDestination = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsProcedure.TablesProcedure.PERSON_PROFILE.getRepositoryName()), TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), 
               new String[]{TblsProcedure.PersonProfile.PERSON_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{}, 
               personProfilesDestFlds);
        Integer personNameFldPosic=LPArray.valuePosicInArray(personProfilesDestFlds, TblsProcedure.PersonProfile.PERSON_NAME.getName());
        Integer roleNameFldPosic=LPArray.valuePosicInArray(personProfilesDestFlds, TblsProcedure.PersonProfile.ROLE_NAME.getName());
        for (Object[] personProfileRecordsDestination1 : personProfileRecordsDestination) {
            personProfileRecordsDestination1[personNameFldPosic] = UserAndRolesViews.getUserByPerson(personProfileRecordsDestination1[personNameFldPosic].toString());
        }
        for (Object[] curRow: personProfileRecordsDestination)
            personProfilesDest.put(LPJson.convertArrayRowToJSONObject(personProfilesDestFlds, curRow));
        detailsObj.put(GlobalAPIsParams.LBL_DATA_DEPLOYED_TABLE+TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), personProfilesDest);

        String[] procUserRolesSourceFlds = new String[]{TblsReqs.ProcedureUserRoles.USER_NAME.getName() , TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()};
        Object[][] procUserAndRolesRecordsSource = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USER_ROLES.getTableName(), 
           new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(),TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName}, 
           procUserRolesSourceFlds);
        for (Object[] curRow: procUserAndRolesRecordsSource)
            procUserRolesSource.put(LPJson.convertArrayRowToJSONObject(procUserRolesSourceFlds, curRow));
        detailsObj.put(GlobalAPIsParams.LBL_DATA_IN_DEFINITION_TABLE+TblsReqs.TablesReqs.PROC_USER_ROLES.getTableName(), procUserRolesSource);

        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personProfileRecordsDestination[0][0].toString())){
            anyMismatch=true;
            String errMsg="Not Deployed yet, there is no record in table "+TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserAndRolesRecordsSource[0][0].toString()))
                errMsg=errMsg+". Not found the info in the requirements definition neither";
            else{
                errMsg=errMsg+". Found the info in the requirements definition neither";
            }
            mismatchesObj.put(GlobalAPIsParams.LBL_ERROR, errMsg);
        }else{
            if (personProfileRecordsDestination.length!=procUserAndRolesRecordsSource.length){
                anyMismatch=true;
                mismatchesObj.put(GlobalAPIsParams.LBL_ERROR, "Not the same record number found in both places");
            }
            String[] sourceInfo=new String[procUserAndRolesRecordsSource.length];
            String[] destInfo=new String[personProfileRecordsDestination.length];
            for (int i=0;i<procUserAndRolesRecordsSource.length;i++){
                sourceInfo[i]=procUserAndRolesRecordsSource[i][personNameFldPosic]+"-"+procUserAndRolesRecordsSource[i][roleNameFldPosic];
                destInfo[i]=personProfileRecordsDestination[i][0]+"-"+personProfileRecordsDestination[i][1];                
            }
            Map<String, Object[]> evaluateValuesAreInArray = LPArray.evaluateValuesAreInArray(sourceInfo, destInfo);
            String evaluation= evaluateValuesAreInArray.keySet().iterator().next();        
            if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(evaluation))){
                anyMismatch=true;
                Object[] missingObjects = evaluateValuesAreInArray.get(evaluation);
                mismatchesObj.put("missing_user-role pairs", LPJson.convertToJSONArray(missingObjects));
            }
            
        }
        return publishJson(anyMismatch, mismatchesObj, detailsObj);
    }
    
    public static final  JSONObject createDBProcedureEvents(String procedure,  Integer procVersion, String procInstanceName){        
        Boolean anyMismatch=false;
        JSONObject detailsObj=new JSONObject();
        JSONObject mismatchesObj=new JSONObject();   
        JSONArray personProfilesDest = new JSONArray();
        JSONArray procUserRolesSource = new JSONArray();

        String[] procUserRolesSourceFlds = getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableFields());
        Object[][] procUserAndRolesRecordsSource = Rdbms.getRecordFieldsByFilter(null, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, 
            new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(),TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName}),
            TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableFields(), null, false);
        for (Object[] curRow: procUserAndRolesRecordsSource)
            procUserRolesSource.put(LPJson.convertArrayRowToJSONObject(procUserRolesSourceFlds, curRow));
        detailsObj.put(GlobalAPIsParams.LBL_DATA_IN_DEFINITION_TABLE+TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(), procUserRolesSource);
        
        String[] personProfilesDestFlds = EnumIntTableFields.getAllFieldNames(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS);
        Object[][] personProfileRecordsDestination = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getRepositoryName()), TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, 
            new SqlWhere(TblsProcedure.TablesProcedure.PROCEDURE_VIEWS, new String[]{TblsProcedure.ProcedureViews.NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{}), 
               TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableFields(), null, false);
        for (Object[] curRow: personProfileRecordsDestination)
            personProfilesDest.put(LPJson.convertArrayRowToJSONObject(personProfilesDestFlds, curRow));
        detailsObj.put(GlobalAPIsParams.LBL_DATA_DEPLOYED_TABLE+TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(), personProfilesDest);


        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personProfileRecordsDestination[0][0].toString())){
            anyMismatch=true;
            String errMsg="Not Deployed yet, there is no record in table "+TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserAndRolesRecordsSource[0][0].toString()))
                errMsg=errMsg+". Not found the info in the requirements definition neither";
            else{
                errMsg=errMsg+". Found the info in the requirements definition neither";
            }
            mismatchesObj.put(GlobalAPIsParams.LBL_ERROR, errMsg);
        }else{
            if (personProfileRecordsDestination.length!=procUserAndRolesRecordsSource.length){
                anyMismatch=true;
                mismatchesObj.put(GlobalAPIsParams.LBL_ERROR, "Not the same record number found in both places");
            }
/*            
            Integer personNameFldPosic=LPArray.valuePosicInArray(personProfilesDestFlds, TblsProcedure.PersonProfile.PERSON_NAME.getName());
            Integer roleNameFldPosic=LPArray.valuePosicInArray(personProfilesDestFlds, TblsProcedure.PersonProfile.ROLE_NAME.getName());
            
            String[] sourceInfo=new String[procUserAndRolesRecordsSource.length];
            String[] destInfo=new String[personProfileRecordsDestination.length];
            for (int i=0;i<procUserAndRolesRecordsSource.length;i++){
                sourceInfo[i]=procUserAndRolesRecordsSource[i][personNameFldPosic]+"-"+procUserAndRolesRecordsSource[i][roleNameFldPosic];
                destInfo[i]=personProfileRecordsDestination[i][0]+"-"+personProfileRecordsDestination[i][1];                
            }
            HashMap<String, Object[]> evaluateValuesAreInArray = LPArray.evaluateValuesAreInArray(sourceInfo, destInfo);
            String evaluation= evaluateValuesAreInArray.keySet().iterator().next();        
            if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(evaluation))){
                anyMismatch=true;
                Object[] missingObjects = evaluateValuesAreInArray.get(evaluation);
                mismatchesObj.put("missing_user-role pairs", LPJson.convertToJSONArray(missingObjects));
            }
*/            
        }
        return publishJson(anyMismatch, mismatchesObj, detailsObj);
    }        

    public static final  JSONObject createBusinessRules(String procedure,  Integer procVersion, String procInstanceName){        
        Boolean anyMismatch=false;
        JSONObject detailsObj=new JSONObject();
        JSONObject mismatchesObj=new JSONObject();   
        JSONArray personProfilesDest = new JSONArray();
        JSONArray procUserRolesSource = new JSONArray();

        String[] procUserRolesSourceFlds = getAllFieldNames(TblsReqs.TablesReqs.PROC_BUS_RULES.getTableFields());
        Object[][] procUserAndRolesRecordsSource = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_BUS_RULES.getTableName(), 
                new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(),TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName}, 
                procUserRolesSourceFlds);
        for (Object[] curRow: procUserAndRolesRecordsSource)
            procUserRolesSource.put(LPJson.convertArrayRowToJSONObject(procUserRolesSourceFlds, curRow));
        detailsObj.put(GlobalAPIsParams.LBL_DATA_IN_DEFINITION_TABLE+TblsReqs.TablesReqs.PROC_BUS_RULES.getTableName(), procUserRolesSource);
        
        String[] personProfilesDestFlds = EnumIntTableFields.getAllFieldNames(TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE);
        Object[][] personProfileRecordsDestination = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getRepositoryName()), TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), 
               new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{}, 
               personProfilesDestFlds);
        for (Object[] curRow: personProfileRecordsDestination)
            personProfilesDest.put(LPJson.convertArrayRowToJSONObject(personProfilesDestFlds, curRow));
        detailsObj.put(GlobalAPIsParams.LBL_DATA_DEPLOYED_TABLE+TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE.getTableName(), personProfilesDest);


        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personProfileRecordsDestination[0][0].toString())){
            anyMismatch=true;
            String errMsg="Not Deployed yet, there is no record in table "+TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserAndRolesRecordsSource[0][0].toString()))
                errMsg=errMsg+". Not found the info in the requirements definition neither";
            else{
                errMsg=errMsg+". Found the info in the requirements definition neither";
            }
            mismatchesObj.put(GlobalAPIsParams.LBL_ERROR, errMsg);
        }else{
            Integer srcAreaFldPosic=LPArray.valuePosicInArray(procUserRolesSourceFlds, TblsReqs.ProcedureBusinessRules.FILE_SUFFIX.getName());
            Integer srcRuleNameFldPosic=LPArray.valuePosicInArray(procUserRolesSourceFlds, TblsReqs.ProcedureBusinessRules.RULE_NAME.getName());
            Integer srcRuleValueFldPosic=LPArray.valuePosicInArray(procUserRolesSourceFlds, TblsReqs.ProcedureBusinessRules.RULE_VALUE.getName());
            Integer destAreaFldPosic=LPArray.valuePosicInArray(personProfilesDestFlds, TblsProcedure.ProcedureBusinessRules.AREA.getName());
            Integer destRuleNameFldPosic=LPArray.valuePosicInArray(personProfilesDestFlds, TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName());
            Integer destRuleValueFldPosic=LPArray.valuePosicInArray(personProfilesDestFlds, TblsProcedure.ProcedureBusinessRules.RULE_VALUE.getName());
            
            String[] sourceInfo=new String[procUserAndRolesRecordsSource.length];
            String[] destInfo=new String[personProfileRecordsDestination.length];
            for (int i=0;i<procUserAndRolesRecordsSource.length;i++){
                sourceInfo[i]=procUserAndRolesRecordsSource[i][srcAreaFldPosic]+"-"+procUserAndRolesRecordsSource[i][srcRuleNameFldPosic]+"-"+procUserAndRolesRecordsSource[i][srcRuleValueFldPosic];
                destInfo[i]=personProfileRecordsDestination[i][destAreaFldPosic]+"-"+personProfileRecordsDestination[i][destRuleNameFldPosic]+"-"+personProfileRecordsDestination[i][destRuleValueFldPosic];
            }
            Map<String, Object[]> evaluateValuesAreInArray = LPArray.evaluateValuesAreInArray(destInfo, sourceInfo);
            String evaluation= evaluateValuesAreInArray.keySet().iterator().next();        
            if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(evaluation))){
                anyMismatch=true;
                Object[] missingObjects = evaluateValuesAreInArray.get(evaluation);
                mismatchesObj.put("missing_pairs", LPJson.convertToJSONArray(missingObjects));
            }
            
        }
        return publishJson(anyMismatch, mismatchesObj, detailsObj);
    }        

    public static final  JSONObject createDBSopMetaDataAndUserSop(String procedure,  Integer procVersion, String procInstanceName){        
        Boolean anyMismatch=false;
        JSONObject detailsObj=new JSONObject();
        JSONObject mismatchesObj=new JSONObject();   
        JSONArray personProfilesDest = new JSONArray();
        JSONArray procUserRolesSource = new JSONArray();

        String[] procUserRolesSourceFlds = getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableFields());
        Object[][] procUserAndRolesRecordsSource = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(), 
                new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(),TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName}, 
                procUserRolesSourceFlds);
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserAndRolesRecordsSource[0][0].toString()))){
            for (Object[] curRow: procUserAndRolesRecordsSource)
                procUserRolesSource.put(LPJson.convertArrayRowToJSONObject(procUserRolesSourceFlds, curRow));
            detailsObj.put(GlobalAPIsParams.LBL_DATA_IN_DEFINITION_TABLE+TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(), procUserRolesSource);
        }
        String[] personProfilesDestFlds = EnumIntTableFields.getAllFieldNames(TblsCnfg.TablesConfig.SOP_META_DATA.getTableFields());
        Object[][] personProfileRecordsDestination = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsCnfg.TablesConfig.SOP_META_DATA.getRepositoryName()), TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(), 
               new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{}, 
               personProfilesDestFlds);
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(personProfileRecordsDestination[0][0].toString()))){
            for (Object[] curRow: personProfileRecordsDestination)
                personProfilesDest.put(LPJson.convertArrayRowToJSONObject(personProfilesDestFlds, curRow));
            detailsObj.put(GlobalAPIsParams.LBL_DATA_DEPLOYED_TABLE+TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(), personProfilesDest);
        }
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personProfileRecordsDestination[0][0].toString())){
            anyMismatch=true;
            String errMsg="Not Deployed yet, there is no record in table "+TblsCnfg.TablesConfig.SOP_META_DATA.getTableName();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserAndRolesRecordsSource[0][0].toString()))
                errMsg=errMsg+". Not found the info in the requirements definition neither";
            else{
                errMsg=errMsg+". Found the info in the requirements definition neither";
            }
            mismatchesObj.put(GlobalAPIsParams.LBL_ERROR, errMsg);
        }else{
            Integer srcSopNameFldPosic=LPArray.valuePosicInArray(procUserRolesSourceFlds, TblsReqs.ProcedureSopMetaData.SOP_NAME.getName());
            Integer destSopNameFldPosic=LPArray.valuePosicInArray(personProfilesDestFlds, TblsCnfg.SopMetaData.SOP_NAME.getName());
            
            String[] sourceInfo=new String[procUserAndRolesRecordsSource.length];
            String[] destInfo=new String[personProfileRecordsDestination.length];
            for (int i=0;i<procUserAndRolesRecordsSource.length;i++){
                sourceInfo[i]=procUserAndRolesRecordsSource[i][srcSopNameFldPosic].toString();
                destInfo[i]=personProfileRecordsDestination[i][destSopNameFldPosic].toString();
            }
            Map<String, Object[]> evaluateValuesAreInArray = LPArray.evaluateValuesAreInArray(destInfo, sourceInfo);
            String evaluation= evaluateValuesAreInArray.keySet().iterator().next();        
            if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(evaluation))){
                anyMismatch=true;
                Object[] missingObjects = evaluateValuesAreInArray.get(evaluation);
                mismatchesObj.put("missing_pairs", LPJson.convertToJSONArray(missingObjects));
            }
            
        }
        return publishJson(anyMismatch, mismatchesObj, detailsObj);
    }        

    public static final  JSONObject addProcedureSOPtoUsers(String procedure,  Integer procVersion, String procInstanceName){        
        if (1==1)
            publishJson(null, null, null);
        Boolean anyMismatch=false;
        JSONObject detailsObj=new JSONObject();
        JSONObject mismatchesObj=new JSONObject();   
        JSONArray personProfilesDest = new JSONArray();
        JSONArray procUserRolesSource = new JSONArray();

        String[] procUserRolesSourceFlds = getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableFields());
        Object[][] procUserAndRolesRecordsSource = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(), 
                new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(),TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName}, 
                procUserRolesSourceFlds);
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserAndRolesRecordsSource[0][0].toString()))){
            for (Object[] curRow: procUserAndRolesRecordsSource)
                procUserRolesSource.put(LPJson.convertArrayRowToJSONObject(procUserRolesSourceFlds, curRow));
            detailsObj.put(GlobalAPIsParams.LBL_DATA_IN_DEFINITION_TABLE+TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(), procUserRolesSource);
        }
        String[] personProfilesDestFlds = EnumIntTableFields.getAllFieldNames(TblsCnfg.TablesConfig.SOP_META_DATA.getTableFields());
        Object[][] personProfileRecordsDestination = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsCnfg.TablesConfig.SOP_META_DATA.getRepositoryName()), TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(), 
               new String[]{TblsProcedure.ProcedureBusinessRules.RULE_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()}, new Object[]{}, 
               personProfilesDestFlds);
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(personProfileRecordsDestination[0][0].toString()))){
            for (Object[] curRow: personProfileRecordsDestination)
                personProfilesDest.put(LPJson.convertArrayRowToJSONObject(personProfilesDestFlds, curRow));
            detailsObj.put(GlobalAPIsParams.LBL_DATA_DEPLOYED_TABLE+TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(), personProfilesDest);
        }
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personProfileRecordsDestination[0][0].toString())){
            anyMismatch=true;
            String errMsg="Not Deployed yet, there is no record in table "+TblsCnfg.TablesConfig.SOP_META_DATA.getTableName();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserAndRolesRecordsSource[0][0].toString()))
                errMsg=errMsg+". Not found the info in the requirements definition neither";
            else{
                errMsg=errMsg+". Found the info in the requirements definition neither";
            }
            mismatchesObj.put(GlobalAPIsParams.LBL_ERROR, errMsg);
        }else{
            Integer srcSopNameFldPosic=LPArray.valuePosicInArray(procUserRolesSourceFlds, TblsReqs.ProcedureSopMetaData.SOP_NAME.getName());
            Integer destSopNameFldPosic=LPArray.valuePosicInArray(personProfilesDestFlds, TblsCnfg.SopMetaData.SOP_NAME.getName());
            
            String[] sourceInfo=new String[procUserAndRolesRecordsSource.length];
            String[] destInfo=new String[personProfileRecordsDestination.length];
            for (int i=0;i<procUserAndRolesRecordsSource.length;i++){
                sourceInfo[i]=procUserAndRolesRecordsSource[i][srcSopNameFldPosic].toString();
                destInfo[i]=personProfileRecordsDestination[i][destSopNameFldPosic].toString();
            }
            Map<String, Object[]> evaluateValuesAreInArray = LPArray.evaluateValuesAreInArray(destInfo, sourceInfo);
            String evaluation= evaluateValuesAreInArray.keySet().iterator().next();        
            if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(evaluation))){
                anyMismatch=true;
                Object[] missingObjects = evaluateValuesAreInArray.get(evaluation);
                mismatchesObj.put("missing_pairs", LPJson.convertToJSONArray(missingObjects));
            }
            
        }
        return publishJson(anyMismatch, mismatchesObj, detailsObj);
    }        

    public static JSONObject createDBModuleTablesAndFields(String procedure,  Integer procVersion, String procInstanceName, String moduleName){
        Boolean anyMismatch=false;
        JSONObject detailsObj=new JSONObject();
        JSONObject mismatchesObj=new JSONObject();
        JSONArray procUserRolesSource = new JSONArray();
                
        String[] procUserRolesSourceFlds = getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields());
        Object[][] procUserAndRolesRecordsSource = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(), 
                new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(),TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procedure, procVersion, procInstanceName}, 
                procUserRolesSourceFlds);
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(procUserAndRolesRecordsSource[0][0].toString()))){
            for (Object[] curRow: procUserAndRolesRecordsSource)
                procUserRolesSource.put(LPJson.convertArrayRowToJSONObject(procUserRolesSourceFlds, curRow));
            detailsObj.put(GlobalAPIsParams.LBL_DATA_IN_DEFINITION_TABLE+TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(), procUserRolesSource);
        }        
        
        Integer srcRepositoryNameFldPosic=LPArray.valuePosicInArray(procUserRolesSourceFlds, TblsReqs.ProcedureModuleTables.SCHEMA_NAME.getName());
        Integer srcTableNameFldPosic=LPArray.valuePosicInArray(procUserRolesSourceFlds, TblsReqs.ProcedureModuleTables.TABLE_NAME.getName());

        String[] moduleBaseTablesArr=new String[procUserAndRolesRecordsSource.length];
        for (int i=0;i<procUserAndRolesRecordsSource.length;i++)            
            moduleBaseTablesArr[i]=procUserAndRolesRecordsSource[i][srcRepositoryNameFldPosic]+"-"+procUserAndRolesRecordsSource[i][srcTableNameFldPosic];
        detailsObj.put("expected_and_checked_tables", LPJson.convertToJSON(moduleBaseTablesArr));
        Object[] dbSchemasTablesList = Rdbms.dbSchemaAndTableList(procInstanceName);
        dbSchemasTablesList=LPArray.getUniquesArray(dbSchemasTablesList);
        Map<String, Object[]> evaluateValuesAreInArray = LPArray.evaluateValuesAreInArray(dbSchemasTablesList, moduleBaseTablesArr);
        String evaluation = evaluateValuesAreInArray.keySet().iterator().next();        
        if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(evaluation))){
            anyMismatch=true;
            Object[] missingObjects = evaluateValuesAreInArray.get(evaluation);
            mismatchesObj.put("missing_tables", LPJson.convertToJSONArray(missingObjects));
        }
        return publishJson(anyMismatch, mismatchesObj, detailsObj);
    }    
    
    public static final  JSONObject deployMasterData(String procedure,  Integer procVersion, String instanceName, String moduleName){
        if (1==1)
            return publishJson(null, null, null);        
        try{
            JSONArray jsonArr=new JSONArray();
            JSONObject jsonObj = new JSONObject();
             Object[][] procMasterDataObjs = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MASTER_DATA.getTableName(), 
                new String[]{TblsReqs.ProcedureMasterData.PROCEDURE_NAME.getName(), TblsReqs.ProcedureMasterData.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureMasterData.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureMasterData.ACTIVE.getName()}, 
                    new Object[]{procedure, procVersion, instanceName, true}, 
                new String[]{TblsReqs.ProcedureMasterData.OBJECT_TYPE.getName(), TblsReqs.ProcedureMasterData.JSON_OBJ.getName()},
                new String[]{TblsReqs.ProcedureMasterData.ORDER_NUMBER.getName()});
            JSONArray jsonRowArr=new JSONArray();
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procMasterDataObjs[0][0].toString())){
              jsonObj.put(trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections.JsonTags.ERROR.getTagValueEn(), LPJson.convertToJSON(procMasterDataObjs[0]));
              jsonArr.put(jsonObj);
            }else{
                jsonArr.put(jsonObj);
                for (Object[] curRow: procMasterDataObjs){
                    ClassMasterData clssMD= new ClassMasterData(instanceName, curRow[0].toString(), curRow[1].toString(), moduleName);
                    JSONObject jsonRowObj = new JSONObject();
                    jsonRowObj.put(curRow[0], clssMD.getDiagnostic().getNewObjectId());
                    jsonRowArr.put(jsonRowObj);
                }            
            }
            return publishJson(null, null, null);
        }catch(Exception e){
            return publishJson(null, null, null);
        }
    }

    public static JSONObject dataRepositoriesAreMirror(String procInstanceName, String dbName){
        Boolean anyMismatch=false;
        JSONObject detailsObj=new JSONObject();
        JSONObject mismatchesObj=new JSONObject();        
        InternalMessage allMismatchesDiagnAll = TestingRegressionUAT.procedureRepositoryMirrors(procInstanceName);        
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(allMismatchesDiagnAll.getDiagnostic())) {
            Object[][] allMismatches = (Object[][]) allMismatchesDiagnAll.getNewObjectId();
            JSONArray jArr=new JSONArray();
            for (int i=1;i<allMismatches.length;i++){
                    jArr.put(LPJson.convertArrayRowToJSONObject(LPArray.convertObjectArrayToStringArray(allMismatches[0]), allMismatches[i]));
            }
            anyMismatch=true;
            mismatchesObj.put("error_not_mirror_tables",jArr);
        } 
        return publishJson(anyMismatch, mismatchesObj, detailsObj);
    }
}
