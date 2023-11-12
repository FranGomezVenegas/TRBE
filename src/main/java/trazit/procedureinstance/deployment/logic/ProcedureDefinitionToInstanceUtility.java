/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.deployment.logic;

import lbplanet.utilities.LPPlatform;
import databases.Rdbms;
import databases.TblsCnfg;
import databases.TblsProcedure;
import trazit.procedureinstance.definition.definition.TblsReqs;
import functionaljavaa.sop.UserSop;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.definition.ReqProcedureEnums.ReqProcedureDefinitionErrorTraping;
import trazit.procedureinstance.definition.logic.ClassReqProcedureQueries;

/**
 *
 * @author Administrator
 */
public class ProcedureDefinitionToInstanceUtility {
    private ProcedureDefinitionToInstanceUtility(){    throw new IllegalStateException("Utility class");}


    public static final Object[] isModuleBusinessRulesAvailable(String procInstanceName, String busRuleArea, String busRuleName, String busRuleValue){

        Object[] diagnoses = Rdbms.existsRecord("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableName(),
            new String[]{TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(),TblsReqs.ProcedureReqSolution.BUSINESS_RULE_AREA.getName(), TblsReqs.ProcedureReqSolution.BUSINESS_RULE.getName()}, 
            new Object[]{procInstanceName, busRuleArea, busRuleName});
        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString()))
            return new Object[]{LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.BUSINESS_RULE_ALREADY_PART_OF_PROCEDURE, new Object[]{busRuleName, procInstanceName}};
        JSONObject dbSingleRowToJsonObj = ClassReqProcedureQueries.dbSingleRowToJsonObj(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableFields()), new String[]{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName});

        String moduleName=dbSingleRowToJsonObj.get("module_name").toString();
        Integer moduleVersion=dbSingleRowToJsonObj.get("module_version").toString().length()>0?Integer.valueOf(dbSingleRowToJsonObj.get("module_version").toString()):-1;        

        Object[][] moduleBusinessRules = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.BUSINESS_RULES_IN_SOLUTION.getViewName(), 
            new String[]{TblsReqs.viewBusinessRulesInSolution.PROC_INSTANCE_NAME.getName(),
                TblsReqs.viewBusinessRulesInSolution.MODULE_NAME.getName(), TblsReqs.viewBusinessRulesInSolution.MODULE_VERSION.getName(),
                TblsReqs.viewBusinessRulesInSolution.AREA.getName(), TblsReqs.viewBusinessRulesInSolution.RULE_NAME.getName()}, 
            new Object[]{procInstanceName, moduleName, moduleVersion, busRuleArea, busRuleName}, 
            new String[]{TblsReqs.viewBusinessRulesInSolution.PRESENT.getName(), TblsReqs.viewBusinessRulesInSolution.VALUES_LIST.getName()}, new String[]{});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(moduleBusinessRules[0][0].toString()))
            return new Object[]{LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.MODULE_BUSINESS_RULE_NOT_FOUND, new Object[]{busRuleArea, busRuleName}};
        if (Boolean.FALSE.equals("0".equalsIgnoreCase(moduleBusinessRules[0][0].toString())))
            return new Object[]{LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.MODULE_BUSINESS_ALREADY_PRESENT, new Object[]{busRuleArea, busRuleName}};
        
        if (LPNulls.replaceNull(moduleBusinessRules[0][1]).toString().length()>0){
            String valueToCheck="\"keyName\":\""+busRuleValue+"\"";
            if (Boolean.FALSE.equals(moduleBusinessRules[0][1].toString().contains(valueToCheck)))
                return new Object[]{LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.MODULE_BUSINESS_VALUE_NOT_ALLOWED, new Object[]{busRuleName, busRuleValue, moduleBusinessRules[0][1].toString()}};
        }
        
        return new Object[]{LPPlatform.LAB_TRUE};
    }

    public static final Object[] isModuleWindowAvailable(String procInstanceName, String viewQuery){
        JSONObject dbSingleRowToJsonObj = ClassReqProcedureQueries.dbSingleRowToJsonObj(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableFields()), new String[]{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName});
        String moduleName=dbSingleRowToJsonObj.get("module_name").toString();
        Integer moduleVersion=dbSingleRowToJsonObj.get("module_version").toString().length()>0?Integer.valueOf(dbSingleRowToJsonObj.get("module_version").toString()):-1;        

        Object[][] moduleViewQuery = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES.getTableName(), 
            new String[]{TblsReqs.ModuleActionsAndQueries.MODULE_NAME.getName(), TblsReqs.ModuleActionsAndQueries.MODULE_VERSION.getName(),
                TblsReqs.ModuleActionsAndQueries.ENDPOINT_NAME.getName()}, 
            new Object[]{moduleName, moduleVersion, viewQuery}, 
            new String[]{TblsReqs.ModuleActionsAndQueries.API_NAME.getName(), TblsReqs.ModuleActionsAndQueries.ENDPOINT_NAME.getName()}, new String[]{});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(moduleViewQuery[0][0].toString())||
           (Boolean.FALSE.equals(moduleViewQuery[0][0].toString().toUpperCase().contains("QUER"))&&Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(moduleViewQuery[0][0].toString()))) )
            return new Object[]{LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.MODULE_VIEW_QUERY_NOT_FOUND, new Object[]{viewQuery, moduleName}};
        
        return new Object[]{LPPlatform.LAB_TRUE};
    }
    public static final Object[] isModuleWindowActionAvailable(String procInstanceName, String wAction){
        JSONObject dbSingleRowToJsonObj = ClassReqProcedureQueries.dbSingleRowToJsonObj(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableFields()), new String[]{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName});
        String moduleName=dbSingleRowToJsonObj.get("module_name").toString();
        Integer moduleVersion=dbSingleRowToJsonObj.get("module_version").toString().length()>0?Integer.valueOf(dbSingleRowToJsonObj.get("module_version").toString()):-1;        

        Object[][] moduleWindowActions = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ViewsReqs.ACTIONS_IN_SOLUTION.getViewName(), 
            new String[]{TblsReqs.viewActionsInSolution.MODULE_NAME.getName(), TblsReqs.viewActionsInSolution.MODULE_VERSION.getName(),
                TblsReqs.viewActionsInSolution.ENDPOINT_NAME.getName()}, 
            new Object[]{moduleName, moduleVersion, wAction}, 
            new String[]{TblsReqs.viewActionsInSolution.API_NAME.getName(), TblsReqs.viewActionsInSolution.ENDPOINT_NAME.getName(), TblsReqs.viewActionsInSolution.QUERY_FOR_BUTTON.getName(), TblsReqs.viewActionsInSolution.EXTRA_ACTIONS.getName()}, new String[]{});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(moduleWindowActions[0][0].toString()) )
            return new Object[]{LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.MODULE_WINDOW_ACTION_NOT_FOUND, new Object[]{wAction, moduleName}};
        
        return moduleWindowActions[0];
    }    
    public static final Object[] isModuleSpecialWindowAvailable(String procInstanceName, String viewQuery){
        JSONObject dbSingleRowToJsonObj = ClassReqProcedureQueries.dbSingleRowToJsonObj(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableName(),
                getAllFieldNames(TblsReqs.TablesReqs.PROCEDURE_INFO.getTableFields()), new String[]{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()}, new Object[]{procInstanceName});
        String moduleName=dbSingleRowToJsonObj.get("module_name").toString();
        Integer moduleVersion=dbSingleRowToJsonObj.get("module_version").toString().length()>0?Integer.valueOf(dbSingleRowToJsonObj.get("module_version").toString()):-1;        

        Object[][] moduleSpecialViewQuery = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.MODULE_SPECIAL_VIEWS.getTableName(), 
            new String[]{TblsReqs.ModuleSpecialViews.MODULE_NAME.getName(), TblsReqs.ModuleSpecialViews.MODULE_VERSION.getName(),
                TblsReqs.ModuleSpecialViews.VIEW_NAME.getName()}, 
            new Object[]{moduleName, moduleVersion, viewQuery}, 
            new String[]{TblsReqs.ModuleSpecialViews.VIEW_NAME.getName(), TblsReqs.ModuleSpecialViews.JSON_MODEL.getName(),
                TblsReqs.ModuleSpecialViews.JSON_REQUIREMENTS.getName(), TblsReqs.ModuleSpecialViews.WINDOW_TYPE.getName()}, new String[]{});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(moduleSpecialViewQuery[0][0].toString()))
            return new Object[]{LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.MODULE_VIEW_QUERY_NOT_FOUND, new Object[]{viewQuery, moduleName}};
        
        return moduleSpecialViewQuery[0];
    }
    public static final Object[] procedureUsersList(String procInstanceName, Integer procVersion){
        Object[][] procedureRolesListArr = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_USERS.getTableName(), 
                new String[]{TblsReqs.ProcedureUsers.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUsers.PROCEDURE_VERSION.getName()}, new Object[]{procInstanceName, procVersion}, 
                new String[]{TblsReqs.ProcedureUsers.USER_NAME.getName()}, new String[]{TblsReqs.ProcedureUsers.USER_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureRolesListArr[0][0].toString()))
            return new Object[]{};
        return LPArray.getColumnFromArray2D(procedureRolesListArr, 0);
    }

    public static final Object[][] procedureParentAndUserRequirementsList(String procInstanceName, Integer procVersion, EnumIntTableFields fldObj){
        Object[][] procedureRolesListArr = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS.getTableName(), 
                new String[]{TblsReqs.ProcedureUserRequirements.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirements.PROCEDURE_VERSION.getName()}, 
                new Object[]{procInstanceName, procVersion}, 
                new String[]{fldObj.getName(), TblsReqs.ProcedureUserRequirements.CODE.getName()}, new String[]{TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureRolesListArr[0][0].toString()))
            return new Object[][]{{}};
        return procedureRolesListArr; //LPArray.getColumnFromArray2D(procedureRolesListArr, 0);
    }
    
    public static final Object[] procedureRolesList(String procInstanceName, Integer procVersion){
        Object[][] procedureRolesListArr = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_ROLES.getTableName(), 
                new String[]{TblsReqs.ProcedureRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.PROCEDURE_VERSION.getName()}, new Object[]{procInstanceName, procVersion}, 
                new String[]{TblsReqs.ProcedureRoles.ROLE_NAME.getName()}, new String[]{TblsReqs.ProcedureRoles.ROLE_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureRolesListArr[0][0].toString()))
            return new Object[]{};
        return LPArray.getColumnFromArray2D(procedureRolesListArr, 0);
    }
    /**
     *
     * @param procInstanceName
     * @param procVersion
     * @param schemaName
     * @param roleName
     * @param sopName
     * @param sopVersion
     * @param sopRevision
     * @return
     */  
    public static final Object[] procedureSops(String procInstanceName, Integer procVersion){
        Object[][] procedureRolesListArr = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA.getTableName(), 
                new String[]{TblsReqs.ProcedureSopMetaData.PROCEDURE_NAME.getName(), TblsReqs.ProcedureSopMetaData.PROCEDURE_VERSION.getName()}, new Object[]{procInstanceName, procVersion}, 
                new String[]{TblsReqs.ProcedureSopMetaData.SOP_NAME.getName()}, new String[]{TblsReqs.ProcedureSopMetaData.SOP_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procedureRolesListArr[0][0].toString()))
            return new Object[]{};
        return LPArray.getColumnFromArray2D(procedureRolesListArr, 0);
    }    
    public static final Object[][] procedureAddSopToUsersByRole(String procInstanceName, Integer procVersion, String schemaName, String roleName, String sopName, Integer sopVersion, Integer sopRevision){
        String schemaNameDestinationProcedure=LPPlatform.buildSchemaName(schemaName, GlobalVariables.Schemas.PROCEDURE.getName());
        UserSop usSop = new UserSop();
        Object[][] diagnoses = new Object[0][0];
        Object[][] personPerRole = Rdbms.getRecordFieldsByFilter(schemaName, schemaNameDestinationProcedure, TblsProcedure.TablesProcedure.PERSON_PROFILE.getTableName(),
        new String[]{TblsProcedure.PersonProfile.ROLE_NAME.getName()}, new Object[]{roleName}, new String[]{TblsProcedure.PersonProfile.PERSON_NAME.getName()});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(personPerRole[0][0].toString())){
            for (Object[] curPersRole: personPerRole){
                String curPersonName=curPersRole[0].toString();
                Object[] addSopToUserByName = usSop.addSopToUserByName(schemaName, curPersonName, sopName);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(addSopToUserByName[0].toString())) return LPArray.array1dTo2d(addSopToUserByName,addSopToUserByName.length-1);
                //diagnoses = LPArray.joinTwo2DArrays(diagnoses, new Object[][]{{curPersonName, addSopToUserByName}});
            }
        }
        return diagnoses;
        /*usSop.addSopToUserInternalLogic();
        Object[][] existsAppUser = Rdbms.getRecordFieldsByFilter(schemaNameDestination, TABLE_NAME_SOP_META_DATA_DESTINATION,
        new String[]{FLD_NAME_PROCEDURE_SOP_META_DATA_SOP_NAME}, new Object[]{SopName}, new String[]{FLD_NAME_PROCEDURE_SOP_META_DATA_SOP_NAME}, null);
        String diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())) ? JSON_LABEL_FOR_NO : JSON_LABEL_FOR_YES;
        //jsUserRoleObj.put("SOP exists in the procedure?", diagnosesForLog); 
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsAppUser[0][0].toString())){
        Object[] insertRecordInTable = Rdbms.insertRecordInTable(schemaNameDestination, TABLE_NAME_SOP_META_DATA_DESTINATION,
        FIELDS_TO_RETRIEVE_PROCEDURE_SOP_META_DATA_SOURCE.split("\\|"), curSopMetaData);
        diagnosesForLog = (LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTable[0].toString())) ? JSON_LABEL_FOR_NO : JSON_LABEL_FOR_YES;
        jsonObj.put("SOP inserted in the instance?", diagnosesForLog);
        //if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTable[0].toString())){}
        }
         */
    }

    /**
     *
     * @param procInstanceName
     * @param procVersion
     * @param schemaName
     */
    public static final void procedureAddUserSops( String procInstanceName, Integer procVersion, String schemaName){
        String tableName = "procedure";  
        String newEntry = "";
        StringBuilder sopListBuilder = new StringBuilder(0);
        
        Object[][] procUserReqInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaName, tableName, 
                        new String[]{"procedure", "version", "code is not null", "active", "in_scope", "in_system"}, 
                        new Object[]{procInstanceName, procVersion, "", true, true, true}, 
                        new String[]{"code", "name", "sop_name", "sop_section", "roles", "schema_name schema_name"}, 
                        new String[]{"order_number", "id"});

        Integer contProcUserReqInfo = procUserReqInfo.length;       

        newEntry = " query returns " + contProcUserReqInfo++ + " records.";
        contProcUserReqInfo--;

        for (Integer icontProcUserReqInfo=0; icontProcUserReqInfo<contProcUserReqInfo;icontProcUserReqInfo++){            
            String sopName = (String) procUserReqInfo[icontProcUserReqInfo][2]; 
            String sopSectionName = (String) procUserReqInfo[icontProcUserReqInfo][3];
            String role = (String) procUserReqInfo[icontProcUserReqInfo][4];
            
            newEntry = " Parsing record " + (icontProcUserReqInfo+1) + "/" + contProcUserReqInfo + ": Sop=" + sopName + " Section=" + sopSectionName + " Role=" + role;

            if (sopName!=null){                
                String[] sopNames = sopName.split(",");
                for (String sp: sopNames){
                    if (sopSectionName!=null){sp = sp+"-"+sopSectionName;}  
                    Object[] diagnoses = Rdbms.existsRecord(procInstanceName, schemaName+"-config", TblsCnfg.TablesConfig.SOP_META_DATA.getTableName(), 
                            new String[]{TblsCnfg.SopMetaData.SOP_NAME.getName()}, new Object[]{sp});
                    if ( (LPPlatform.LAB_TRUE.equalsIgnoreCase(diagnoses[0].toString())) && (role!=null) ){                  
                        String[] roles = role.split(",");
                        for (String r: roles){         
                            Object[][] userProfileInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaName, tableName, 
                                            new String[]{"role_id"}, 
                                            new Object[]{procInstanceName+"_"+r}, 
                                            new String[]{"user_info_id"});

                            Integer contUser = userProfileInfo.length;     

                            newEntry = "Found " + contUser + " users having assigned the role "+procInstanceName+"_"+r;

                            for (Integer icontUser=0;icontUser<contUser;icontUser++){
                                UserSop usSop=new UserSop();
                                String userInfoId = (String) userProfileInfo[icontUser][0];

                                Object[] newSopUser = usSop.addSopToUserByName(schemaName+"-data", userInfoId, sopName);

                                newEntry = icontUser+"/"+contUser+"  "+newSopUser[newSopUser.length-1].toString();

                                sopListBuilder.append(sp).append("|");            
                            }    
                        }    
                    }                                                                    
                }
            }
        }
    }         
    
}
