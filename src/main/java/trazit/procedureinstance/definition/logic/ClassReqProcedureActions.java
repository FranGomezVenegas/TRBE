package trazit.procedureinstance.definition.logic;

import trazit.procedureinstance.definition.definition.ReqProcedureEnums.ProcedureDefinitionAPIActionsEndpoints;
import trazit.procedureinstance.definition.definition.ReqProcedureEnums.ReqProcedureDefinitionErrorTraping;
import databases.Rdbms;
import databases.Rdbms.RdbmsErrorTrapping;
import static databases.Rdbms.insertRecordInTableFromTable;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsTesting;
import functionaljavaa.materialspec.DataSpec;
import trazit.procedureinstance.definition.definition.TblsReqs;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.TestingScript;
import static functionaljavaa.unitsofmeasurement.UnitsOfMeasurement.getUomFromConfig;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import static lbplanet.utilities.LPArray.convertStringWithDataTypeToObjectArrayInternalMessage;
import static lbplanet.utilities.LPArray.valuePosicArray2D;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.LpPlatformSuccess;
import lbplanet.utilities.TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntMessages;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntTables;
import trazit.globalvariables.GlobalVariables;
import static trazit.globalvariables.GlobalVariables.DEFAULTLANGUAGE;
import static trazit.procedureinstance.definition.definition.ReqProcedureEnums.ProcedureDefinitionAPIActionsEndpoints.ADD_ROLE;
import static trazit.procedureinstance.definition.definition.ReqProcedureEnums.ProcedureDefinitionAPIActionsEndpoints.ADD_ROLE_TO_USER;
import static trazit.procedureinstance.definition.definition.ReqProcedureEnums.ProcedureDefinitionAPIActionsEndpoints.ADD_USER;
import trazit.procedureinstance.definition.definition.TblsReqs.TablesReqs;
import static trazit.procedureinstance.definition.logic.CoverageTestingAnalysis.newCoverageTest;
import static trazit.procedureinstance.definition.logic.ReqProcedureFrontendMasterData.getActiveModules;
import trazit.procedureinstance.deployment.apis.ProcDefinitionChecker;
import trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections;
import trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceUtility;
import static trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceUtility.procedureParentAndUserRequirementsList;
import static trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceUtility.procedureRolesList;
import static trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceUtility.procedureSops;
import static trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceUtility.procedureUsersList;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ClassReqProcedureActions {

    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    private Object[] diagnostic = new Object[0];
    private Boolean functionFound = false;
    EnumIntMessages diagnosticObjIntMsg;
    InternalMessage diagnosticObj;

    public static Boolean isProcInstLocked(String procName, Integer procVersion, String instanceName) {
        EnumIntTableFields[] fieldsToRetrieve = new EnumIntTableFields[]{TblsReqs.ProcedureInfo.LOCKED_FOR_ACTIONS};
        Object[][] procAndInstanceArr = Rdbms.getRecordFieldsByFilter(null, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_INFO,
            new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_INFO, new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()},
                new Object[]{procName, procVersion, instanceName}), fieldsToRetrieve, null, false);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(procAndInstanceArr[0][0].toString())) {
            return true;
        }
        return Boolean.TRUE.equals(Boolean.valueOf(procAndInstanceArr[0][0].toString()));
    }

    public ClassReqProcedureActions(HttpServletRequest request, HttpServletResponse response, ProcedureDefinitionAPIActionsEndpoints endPoint) {
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForProcManagement(request, response, false);
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        String[] mandatArgs = new String[]{};
        for (LPAPIArguments curArg : endPoint.getArguments()) {
            if (Boolean.TRUE.equals(curArg.getMandatory())) {
                mandatArgs = LPArray.addValueToArray1D(mandatArgs, curArg.getName());
            }
        }
        if (mandatArgs.length > 0) {
            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, mandatArgs);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, DEFAULTLANGUAGE, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }
        }
        Object[] actionDiagnoses = null;
        this.functionFound = true;
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
            this.diagnostic = (Object[]) argValues[1];
            this.messageDynamicData = new Object[]{argValues[2].toString()};
            return;
        }
        String procedureName = null;
        Integer procedureVersion = null;
        String procInstanceName = null;
        int i=0;

        if (Boolean.FALSE.equals("SUGGEST_SPEC_LIMITS_TESTING".equalsIgnoreCase(endPoint.getName()))&&
            Boolean.FALSE.equals("NEW_PROCEDURE".equalsIgnoreCase(endPoint.getName()))){
            procedureName = argValues[0].toString();
            procedureVersion = (Integer) argValues[1];
            procInstanceName = argValues[2].toString();
            if (Boolean.TRUE.equals(isProcInstLocked(procedureName, procedureVersion, procInstanceName))) {
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response,
                        ReqProcedureDefinitionErrorTraping.INSTANCE_LOCKED_FOR_ACTIONS.getErrorCode(), new Object[]{procedureName, procedureVersion, procInstanceName}, DEFAULTLANGUAGE, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }     
            procReqInstance.setProcInstanceName(procInstanceName);
            procedureName = argValues[i].toString();
            i++;
            procedureVersion = (Integer) argValues[i];
            i++;
            procInstanceName = argValues[i].toString();
            i++;            
        }
        this.functionFound = true;
        switch (endPoint) {
            case SET_PROCEDURE_BUSINESS_RULES:
                procedureName = argValues[0].toString();
                procedureVersion = (Integer) argValues[1];
                procInstanceName = argValues[2].toString();
                String suffixName = argValues[3].toString();
                String propName = argValues[4].toString();
                String propValue = argValues[5].toString();
                Parameter parm = new Parameter();
                parm.addTagInPropertiesFile(Parameter.PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.name(),
                        procInstanceName + "-" + suffixName, propName, propValue);

                break;
            case NEW_PROCEDURE:
                procedureName = argValues[0].toString();
                procedureVersion = (Integer) argValues[1];
                procInstanceName = argValues[2].toString();
                String moduleName = argValues[3].toString();
                Integer moduleVersion = (Integer) argValues[4];
                String lblEn = argValues[5].toString();
                String lblEs = argValues[6].toString();
                Object[][] modulesList = getActiveModules(procInstanceName, new String[]{TblsReqs.Modules.MODULE_NAME.getName(), TblsReqs.Modules.PICTURE.getName(), TblsReqs.Modules.MODULE_SETTINGS.getName()});
                Integer rowIndex=LPArray.valuePosicInArray2D(modulesList, moduleName, 0);
                if (rowIndex==-1) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.MODULE_NOT_FOUND, new Object[]{moduleName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.MODULE_NOT_FOUND, new Object[]{moduleName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.MODULE_NOT_FOUND;
                    this.messageDynamicData = new Object[]{moduleName, procedureName, procedureVersion};
                    break;
                }else{        
                    Object[] existsRecord = Rdbms.existsRecord(TblsReqs.TablesReqs.PROCEDURE_INFO, 
                        new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(),
                            TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()},
                        new Object[]{procedureName, procedureVersion, procInstanceName}, null);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())){
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.PROCEDURE_INSTANCE_ALREADY_EXISTS, new Object[]{procedureName, procInstanceName});
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.PROCEDURE_INSTANCE_ALREADY_EXISTS, new Object[]{procedureName, procInstanceName});
                        this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.PROCEDURE_INSTANCE_ALREADY_EXISTS;
                        this.messageDynamicData = new Object[]{procedureName, procInstanceName};
                        break;                        
                    }else{                        
                        RdbmsObject insertDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROCEDURE_INFO,
                                new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(),
                                    TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureInfo.MODULE_NAME.getName(), TblsReqs.ProcedureInfo.MODULE_VERSION.getName(),
                                    TblsReqs.ProcedureInfo.LABEL_EN.getName(), TblsReqs.ProcedureInfo.LABEL_ES.getName(), 
                                    TblsReqs.ProcedureInfo.PROCEDURE_HASH_CODE.getName(), TblsReqs.ProcedureInfo.NAVIGATION_ICON_NAME.getName(),
                                    TblsReqs.ProcedureInfo.MODULE_SETTINGS.getName()},
                                new Object[]{procedureName, procedureVersion, procInstanceName, moduleName, moduleVersion, lblEn, lblEs, 
                                    LPDate.getCurrentTimeStamp().hashCode(), modulesList[rowIndex][1].toString(), modulesList[rowIndex][2]});
                        if (Boolean.TRUE.equals(insertDiagn.getRunSuccess())) {

                            Object[] insertRecordInTableFromTable = insertRecordInTableFromTable(true, 
                                getAllFieldNames(TblsReqs.TablesReqs.MODULE_MANUALS.getTableFields()),
                                GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.MODULE_MANUALS.getTableName(), 
                                new String[]{TblsReqs.ModuleManuals.MODULE_NAME.getName(), TblsReqs.ModuleManuals.MODULE_VERSION.getName()},
                                new Object[]{moduleName, moduleVersion},
                                GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MANUALS.getTableName(), 
                                getAllFieldNames(TblsReqs.TablesReqs.PROC_MANUALS.getTableFields())
                                ,new String[]{TblsReqs.ProcedureManuals.PROCEDURE_NAME.getName(), TblsReqs.ProcedureManuals.PROCEDURE_VERSION.getName(),
                                    TblsReqs.ProcedureManuals.PROC_INSTANCE_NAME.getName()}, 
                                new Object[]{procedureName, procedureVersion, procInstanceName}, null);            

                            insertRecordInTableFromTable = insertRecordInTableFromTable(true, 
                                getAllFieldNames(TblsReqs.TablesReqs.MODULE_TABLES_AND_VIEWS.getTableFields()),
                                GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.MODULE_TABLES_AND_VIEWS.getTableName(), 
                                new String[]{TblsReqs.ModuleTablesAndViews.MODULE_NAME.getName(), TblsReqs.ModuleTablesAndViews.MODULE_VERSION.getName(), TblsReqs.ModuleTablesAndViews.IS_MANDATORY.getName()},
                                new Object[]{moduleName, moduleVersion, true},
                                GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableName(), 
                                getAllFieldNames(TblsReqs.TablesReqs.PROC_MODULE_TABLES.getTableFields())
                                ,new String[]{TblsReqs.ProcedureModuleTables.PROCEDURE_NAME.getName(), TblsReqs.ProcedureModuleTables.PROCEDURE_VERSION.getName(),
                                    TblsReqs.ProcedureModuleTables.PROC_INSTANCE_NAME.getName()}, 
                                new Object[]{procedureName, procedureVersion, procInstanceName}, null);            

                            actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, insertDiagn.getErrorMessageCode(), new Object[]{moduleName});
                            this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, insertDiagn.getErrorMessageCode(), new Object[]{moduleName});
                            this.messageDynamicData = new Object[]{moduleName, procedureName, procedureVersion};
                        } else {
                            actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
                            this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
                            this.diagnosticObjIntMsg = insertDiagn.getErrorMessageCode();
                            this.messageDynamicData = insertDiagn.getErrorMessageVariables();
                        }                        
                    }
                }
                break;

            case ADD_USER:
                String userName = argValues[3].toString();
                Object[] procedureSopsList = procedureUsersList(procedureName, procedureVersion);
                if (Boolean.TRUE.equals(LPArray.valueInArray(procedureSopsList, userName, true))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_ALREADY_EXISTS, new Object[]{userName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_ALREADY_EXISTS, new Object[]{userName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_ALREADY_EXISTS;
                    this.messageDynamicData = new Object[]{userName, procedureName, procedureVersion};
                    break;
                }
                RdbmsObject removeDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROC_USERS,
                    new String[]{TblsReqs.ProcedureUsers.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUsers.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureUsers.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUsers.USER_NAME.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, userName});
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{userName});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{userName});
                    this.messageDynamicData = new Object[]{userName, procedureName, procedureVersion};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                break;
            case ADD_ROLE:
                String roleName = argValues[3].toString();
                Object[] procedureRolesList = procedureRolesList(procedureName, procedureVersion);
                if (Boolean.TRUE.equals(LPArray.valueInArray(procedureRolesList, roleName, true))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_ALREADY_EXISTS, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_ALREADY_EXISTS, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.ROLE_ALREADY_EXISTS;
                    this.messageDynamicData = new Object[]{roleName, procedureName, procedureVersion};
                    break;
                }
                removeDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROCEDURE_ROLES,
                        new String[]{TblsReqs.ProcedureRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.PROCEDURE_VERSION.getName(),
                            TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureRoles.ROLE_NAME.getName()},
                        new Object[]{procedureName, procedureVersion, procInstanceName, roleName});
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.messageDynamicData = new Object[]{roleName};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections.createDBPersonProfiles(procedureName, procedureVersion, procInstanceName);
                break;
            case ADD_ROLE_TO_USER:
                userName = argValues[3].toString();
                roleName = argValues[4].toString();
                procedureSopsList = procedureUsersList(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureSopsList, userName, false))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND;
                    this.messageDynamicData = new Object[]{userName, procedureName, procedureVersion};
                    break;
                }
                procedureRolesList = procedureRolesList(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureRolesList, roleName, false))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND;
                    this.messageDynamicData = new Object[]{roleName, procedureName, procedureVersion};
                    break;
                }
                removeDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROC_USER_ROLES,
                        new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(),
                            TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()},
                        new Object[]{procedureName, procedureVersion, procInstanceName, userName, roleName});
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.messageDynamicData = new Object[]{roleName, userName};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                break;
            case REMOVE_USER:
                userName = argValues[3].toString();
                procedureSopsList = procedureUsersList(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureSopsList, userName, true))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND, new Object[]{userName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND, new Object[]{userName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND;
                    this.messageDynamicData = new Object[]{userName, procedureName, procedureVersion};
                    break;
                }
                removeDiagn = Rdbms.removeRecordInTable(TblsReqs.TablesReqs.PROC_USER_ROLES,
                    new SqlWhere(TblsReqs.TablesReqs.PROC_USER_ROLES,new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUserRoles.USER_NAME.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, userName}), null);
                removeDiagn = Rdbms.removeRecordInTable(TblsReqs.TablesReqs.PROC_USERS,
                    new SqlWhere(TblsReqs.TablesReqs.PROC_USERS,new String[]{TblsReqs.ProcedureUsers.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUsers.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureUsers.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUsers.USER_NAME.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, userName}), null);
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{userName});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{userName});
                    this.messageDynamicData = new Object[]{userName, procedureName, procedureVersion};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                break;
            case REMOVE_ROLE:
                roleName = argValues[3].toString();
                procedureRolesList = procedureRolesList(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureRolesList, roleName, true))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND;
                    this.messageDynamicData = new Object[]{roleName, procedureName, procedureVersion};
                    break;
                }
                removeDiagn = Rdbms.removeRecordInTable(TblsReqs.TablesReqs.PROC_USER_ROLES,
                    new SqlWhere(TblsReqs.TablesReqs.PROC_USER_ROLES,new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, roleName}), null);                
                removeDiagn = Rdbms.removeRecordInTable(TblsReqs.TablesReqs.PROCEDURE_ROLES,
                        new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_ROLES, new String[]{TblsReqs.ProcedureRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.PROCEDURE_VERSION.getName(),
                            TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureRoles.ROLE_NAME.getName()},
                        new Object[]{procedureName, procedureVersion, procInstanceName, roleName}), null);
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.messageDynamicData = new Object[]{roleName};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections.createDBPersonProfiles(procedureName, procedureVersion, procInstanceName);
                break;
            case REMOVE_ROLE_TO_USER:
                userName = argValues[3].toString();
                roleName = argValues[4].toString();
                procedureSopsList = procedureUsersList(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureSopsList, userName, false))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND;
                    this.messageDynamicData = new Object[]{userName, procedureName, procedureVersion};
                    break;
                }
                procedureRolesList = procedureRolesList(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureRolesList, roleName, false))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND;
                    this.messageDynamicData = new Object[]{roleName, procedureName, procedureVersion};
                    break;
                }
                removeDiagn = Rdbms.removeRecordInTable(TblsReqs.TablesReqs.PROC_USER_ROLES,
                        new SqlWhere(TblsReqs.TablesReqs.PROC_USER_ROLES, new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(),
                            TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUserRoles.USER_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()},
                        new Object[]{procedureName, procedureVersion, procInstanceName, userName, roleName}), null);
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.messageDynamicData = new Object[]{roleName, userName};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                break;
            case RENAME_USER:
                userName = argValues[3].toString();
                String newuserName = argValues[4].toString();
                procedureSopsList = procedureUsersList(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureSopsList, userName, true))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND, new Object[]{userName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND, new Object[]{userName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_NOT_FOUND;
                    this.messageDynamicData = new Object[]{userName, procedureName, procedureVersion};
                    break;
                }
                removeDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROC_USER_ROLES,
                    new EnumIntTableFields[]{TblsReqs.ProcedureUserRoles.USER_NAME}, new Object[]{newuserName},
                    new SqlWhere(TblsReqs.TablesReqs.PROC_USER_ROLES,new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUserRoles.USER_NAME.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, userName}), null);
                removeDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROC_USERS,
                        new EnumIntTableFields[]{TblsReqs.ProcedureUsers.USER_NAME}, new Object[]{newuserName},
                    new SqlWhere(TblsReqs.TablesReqs.PROC_USERS,new String[]{TblsReqs.ProcedureUsers.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUsers.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureUsers.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUsers.USER_NAME.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, userName}), null);
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{userName});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{userName});
                    this.messageDynamicData = new Object[]{userName, procedureName, procedureVersion};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                break;
            case RENAME_ROLE:
                roleName = argValues[3].toString();
                String newroleName = argValues[4].toString();
                procedureRolesList = procedureRolesList(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureRolesList, roleName, true))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND;
                    this.messageDynamicData = new Object[]{roleName, procedureName, procedureVersion};
                    break;
                }
                removeDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROC_USER_ROLES,
                    new EnumIntTableFields[]{TblsReqs.ProcedureUserRoles.ROLE_NAME}, new Object[]{newroleName},
                    new SqlWhere(TblsReqs.TablesReqs.PROC_USER_ROLES,new String[]{TblsReqs.ProcedureUserRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRoles.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureUserRoles.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, roleName}), null);                
                removeDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROCEDURE_ROLES,
                    new EnumIntTableFields[]{TblsReqs.ProcedureRoles.ROLE_NAME}, new Object[]{newroleName},
                    new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_ROLES, new String[]{TblsReqs.ProcedureRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureRoles.ROLE_NAME.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, roleName}), null);
                removeDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION,
                    new EnumIntTableFields[]{TblsReqs.ProcedureReqSolution.ROLES}, new Object[]{newroleName},
                    new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, new String[]{TblsReqs.ProcedureReqSolution.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolution.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.ROLES.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, roleName}), null);
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.messageDynamicData = new Object[]{roleName};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections.createDBPersonProfiles(procedureName, procedureVersion, procInstanceName);
                break;
            case CLONE_ROLE:
                roleName = argValues[3].toString();
                newroleName = argValues[4].toString();
                procedureRolesList = procedureRolesList(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureRolesList, roleName, true))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND;
                    this.messageDynamicData = new Object[]{roleName, procedureName, procedureVersion};
                    break;
                }
                String[] wFldN=new String[]{TblsReqs.ProcedureRoles.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRoles.PROCEDURE_VERSION.getName(),
                            TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureRoles.ROLE_NAME.getName()};
                Object[] wFldV=new Object[]{procedureName, procedureVersion, procInstanceName, newroleName};
                removeDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROCEDURE_ROLES,wFldN, wFldV);

                EnumIntTableFields[] fldsToGet = TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableFields();
                Integer roleNameFldPosic=EnumIntTableFields.getFldPosicInArray(fldsToGet, TblsReqs.ProcedureReqSolution.ROLES.getName());
                Object[][] recordFieldsByFilter = Rdbms.getRecordFieldsByFilter(null, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION,
                new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, wFldN, wFldV), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION.getTableFields(), null, false);
                if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(recordFieldsByFilter[0][0].toString()))){
                    for (Object[] curRow: recordFieldsByFilter){
                        curRow[roleNameFldPosic]=newroleName;
                        removeDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION,EnumIntTableFields.getAllFieldNames(fldsToGet), curRow);
                    }
                }
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.messageDynamicData = new Object[]{newroleName, roleName};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections.createDBPersonProfiles(procedureName, procedureVersion, procInstanceName);
                break;                
                
            case ADD_SOP:
                String sopName = argValues[3].toString();
                Integer sopVersion = Integer.valueOf(argValues[4].toString());
                String fileLink = argValues[5].toString();
                String fieldName=argValues[6].toString();
                String fieldValue=argValues[7].toString();                
                procedureSopsList = procedureSops(procedureName, procedureVersion);
                if (Boolean.TRUE.equals(LPArray.valueInArray(procedureSopsList, sopName, false))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.SOP_ALREADY_EXISTS, new Object[]{sopName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.SOP_ALREADY_EXISTS, new Object[]{sopName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.SOP_ALREADY_EXISTS;
                    this.messageDynamicData = new Object[]{sopName, procedureName, procedureVersion};
                    break;
                }
                String[] fieldNames=new String[0];
                Object[] fieldValues=new Object[0];
                if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                if (fieldValue!=null && fieldValue.length()>0) fieldValues = convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"), TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA, fieldName.split("\\|"));
                if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                    InternalMessage errMsg=(InternalMessage)fieldValues[1];
                    actionDiagnoses=null;                         
                    this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, errMsg.getMessageCodeObj(), errMsg.getMessageCodeVariables());
                    this.diagnosticObjIntMsg=errMsg.getMessageCodeObj();
                    break;
                }else{
                    String[] insFldN=new String[]{TblsReqs.ProcedureSopMetaData.PROCEDURE_NAME.getName(), TblsReqs.ProcedureSopMetaData.PROCEDURE_VERSION.getName(),
                                TblsReqs.ProcedureSopMetaData.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureSopMetaData.SOP_NAME.getName(), TblsReqs.ProcedureSopMetaData.SOP_VERSION.getName(), TblsReqs.ProcedureSopMetaData.FILE_LINK.getName()};
                    Object[] insFldV=new Object[]{procedureName, procedureVersion, procInstanceName, sopName, sopVersion, fileLink};
                    insFldN=LPArray.addValueToArray1D(insFldN, fieldNames);
                    insFldV=LPArray.addValueToArray1D(insFldV, fieldValues);
                    RdbmsObject insertDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA,insFldN, insFldV);
                    if (Boolean.TRUE.equals(insertDiagn.getRunSuccess())) {
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
                        this.messageDynamicData = new Object[]{sopName, sopName};
                    } else {
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, insertDiagn.getErrorMessageCode(), insertDiagn.getErrorMessageVariables());
                        this.diagnosticObjIntMsg = insertDiagn.getErrorMessageCode();
                        this.messageDynamicData = insertDiagn.getErrorMessageVariables();
                    }
                }
                break;
            case REMOVE_SOP:
                sopName = argValues[3].toString();
                procedureSopsList = procedureSops(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureSopsList, sopName, false))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.SOP_NOT_FOUND, new Object[]{sopName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.SOP_NOT_FOUND, new Object[]{sopName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.SOP_NOT_FOUND;
                    this.messageDynamicData = new Object[]{sopName, procedureName, procedureVersion};
                    break;
                }
                removeDiagn = Rdbms.removeRecordInTable(TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA,
                    new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA,new String[]{TblsReqs.ProcedureSopMetaData.PROCEDURE_NAME.getName(), TblsReqs.ProcedureSopMetaData.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureSopMetaData.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureSopMetaData.SOP_NAME.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, sopName}), null);
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{sopName});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{sopName});
                    this.messageDynamicData = new Object[]{sopName, procedureName, procedureVersion};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                break;
            case RENAME_SOP:
                sopName = argValues[3].toString();
                String newsopName = argValues[4].toString();
                procedureSopsList = procedureSops(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureSopsList, sopName, false))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.SOP_NOT_FOUND, new Object[]{sopName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.SOP_NOT_FOUND, new Object[]{sopName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.SOP_NOT_FOUND;
                    this.messageDynamicData = new Object[]{sopName, procedureName, procedureVersion};
                    break;
                }
                removeDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA,
                    new EnumIntTableFields[]{TblsReqs.ProcedureSopMetaData.SOP_NAME}, new Object[]{newsopName},
                    new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA,new String[]{TblsReqs.ProcedureSopMetaData.PROCEDURE_NAME.getName(), TblsReqs.ProcedureSopMetaData.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureSopMetaData.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureSopMetaData.SOP_NAME.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, sopName}), null);                
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.messageDynamicData = new Object[]{sopName};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections.createDBPersonProfiles(procedureName, procedureVersion, procInstanceName);
                break;
            case UPDATE_INFO_USER_REQUIREMENT:
            case UPDATE_INFO_PARENT_USER_REQUIREMENT:
            case NEW_PARENT_USER_REQUIREMENT:
            case NEW_USER_REQUIREMENT:
                String parentCode = argValues[i].toString();
                i++;
                String code ="";
                if (("NEW_USER_REQUIREMENT".equalsIgnoreCase(endPoint.getName()))||
                        ("UPDATE_INFO_USER_REQUIREMENT".equalsIgnoreCase(endPoint.getName()))){
                    code = argValues[i].toString();
                    i++;
                }
                String description = argValues[i].toString();
                i++;
                String orderNumber = argValues[i].toString();
                i++;
                String active = argValues[i].toString();
                i++;
                String in_scope = argValues[i].toString();
                i++;
                String in_system = argValues[i].toString();
                i++;
                Object[][] procedureParentUserAndReqList = procedureParentAndUserRequirementsList(procedureName, procedureVersion, TblsReqs.ProcedureUserRequirements.PARENT_CODE);
                int[] valuePosicArray2D=null;
                if (code.length()==0){
                    valuePosicArray2D = valuePosicArray2D(procedureParentUserAndReqList, new Object[][]{{0, parentCode}});
                }else{
                    valuePosicArray2D = valuePosicArray2D(procedureParentUserAndReqList, new Object[][]{{0, parentCode},{1, code}});                        
                }
                if (endPoint.getName().toUpperCase().contains("UPDATE")){
                    if (valuePosicArray2D.length==0) {
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{parentCode, procedureName, procedureVersion});
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{parentCode, procedureName, procedureVersion});
                        this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND;

                        this.messageDynamicData = new Object[]{code.length()==0?parentCode:parentCode+"-"+code, procedureName, procedureVersion};
                        break;
                    }
                }else if ("NEW_USER_REQUIREMENT".equalsIgnoreCase(endPoint.getName())){
                    if (valuePosicArray2D(procedureParentUserAndReqList, new Object[][]{{0, parentCode}}).length==0){
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{parentCode, procedureName, procedureVersion});
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{parentCode, procedureName, procedureVersion});
                        this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.PARENT_USER_REQUIREMENT_NOT_FOUND;
                        this.messageDynamicData = new Object[]{parentCode, procedureName, procedureVersion};
                        break;                        
                    }
                    if (valuePosicArray2D.length>0) {
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_ALREADY_EXISTS, new Object[]{parentCode, procedureName, procedureVersion});
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_ALREADY_EXISTS, new Object[]{parentCode, procedureName, procedureVersion});
                        this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_ALREADY_EXISTS;
                        this.messageDynamicData = new Object[]{parentCode, procedureName, procedureVersion};
                        break;                                    
                    }
                }else{                    
                    if (valuePosicArray2D.length>0) {
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_ALREADY_EXISTS, new Object[]{parentCode, procedureName, procedureVersion});
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_ALREADY_EXISTS, new Object[]{parentCode, procedureName, procedureVersion});
                        this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_ALREADY_EXISTS;

                        this.messageDynamicData = new Object[]{parentCode, procedureName, procedureVersion};
                        break;
                    }                    
                }                
                String[] fldN=new String[]{};
                Object[] fldV=new Object[]{};
                if (description.length()>0){
                    fldN=LPArray.addValueToArray1D(fldN, TblsReqs.ProcedureUserRequirements.DESCRIPTION.getName());
                    fldV=LPArray.addValueToArray1D(fldV, description);
                }
                if (orderNumber.length()>0){
                    fldN=LPArray.addValueToArray1D(fldN, TblsReqs.ProcedureUserRequirements.ORDER_NUMBER.getName());
                    fldV=LPArray.addValueToArray1D(fldV, new BigDecimal(orderNumber));
                }
                if (active.length()>0){
                    fldN=LPArray.addValueToArray1D(fldN, TblsReqs.ProcedureUserRequirements.ACTIVE.getName());
                    fldV=LPArray.addValueToArray1D(fldV,Boolean.valueOf(active));
                }
                if (in_scope.length()>0){
                    fldN=LPArray.addValueToArray1D(fldN, TblsReqs.ProcedureUserRequirements.IN_SCOPE.getName());
                    fldV=LPArray.addValueToArray1D(fldV, Boolean.valueOf(in_scope));
                }
                if (in_system.length()>0){
                    fldN=LPArray.addValueToArray1D(fldN, TblsReqs.ProcedureUserRequirements.IN_SYSTEM.getName());
                    fldV=LPArray.addValueToArray1D(fldV, Boolean.valueOf(in_system));
                }
                removeDiagn = null;
                if (endPoint.getName().toUpperCase().contains("NEW_")){
                    fldN=LPArray.addValueToArray1D(fldN,new String[]{TblsReqs.ProcedureUserRequirements.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirements.PROCEDURE_VERSION.getName(),
                            TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName()});
                    fldV=LPArray.addValueToArray1D(fldV, new Object[]{procedureName, procedureVersion, procInstanceName, parentCode});
                    if (code.length()>0){
                        fldN=LPArray.addValueToArray1D(fldN, TblsReqs.ProcedureUserRequirements.CODE.getName());
                        fldV=LPArray.addValueToArray1D(fldV, code);
                    }
                    removeDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROCEDURE_USER_REQS,
                        fldN, fldV);                                        
                }else{
                    String[] whereFldN=new String[]{TblsReqs.ProcedureUserRequirements.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirements.PROCEDURE_VERSION.getName(),
                            TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName()};
                    Object[] whereFldV=new Object[]{procedureName, procedureVersion, procInstanceName, parentCode};
                    if (code.length()>0){
                        whereFldN=LPArray.addValueToArray1D(whereFldN, TblsReqs.ProcedureUserRequirements.CODE.getName());
                        whereFldV=LPArray.addValueToArray1D(whereFldV, code);
                    }
                    removeDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROCEDURE_USER_REQS,
                        EnumIntTableFields.getTableFieldsFromString(TblsReqs.TablesReqs.PROCEDURE_USER_REQS, fldN), fldV,
                        new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_USER_REQS,whereFldN, whereFldV), null);                    
                }
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{parentCode});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{parentCode});
                    if (code.length()>0){
                        this.messageDynamicData = new Object[]{code, procedureName, procedureVersion};
                    }else{
                        this.messageDynamicData = new Object[]{parentCode, procedureName, procedureVersion};
                    }
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                break;
            case REMOVE_PARENT_USER_REQUIREMENT:
            case REMOVE_USER_REQUIREMENT:
                parentCode = argValues[i].toString();
                i++;
/*                procedureSopsList = procedureSops(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureSopsList, sopName, false))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.SOP_NOT_FOUND, new Object[]{sopName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.SOP_NOT_FOUND, new Object[]{sopName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.SOP_NOT_FOUND;
                    this.messageDynamicData = new Object[]{sopName, procedureName, procedureVersion};
                    break;
                }*/
                code ="";
                String[] rmvFldN=new String[]{TblsReqs.ProcedureUserRequirements.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirements.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName()};
                Object[] rmvFldV=new Object[]{procedureName, procedureVersion, procInstanceName, parentCode};
                if ("REMOVE_USER_REQUIREMENT".equalsIgnoreCase(endPoint.getName())){
                    code = argValues[i].toString();
                    i++;
                    rmvFldN=LPArray.addValueToArray1D(rmvFldN, TblsReqs.ProcedureUserRequirements.CODE.getName());
                    rmvFldV=LPArray.addValueToArray1D(rmvFldV, code);
                }else{
                    rmvFldN=LPArray.addValueToArray1D(rmvFldN, TblsReqs.ProcedureUserRequirements.CODE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause());
                    rmvFldV=LPArray.addValueToArray1D(rmvFldV, "");
                    recordFieldsByFilter = Rdbms.getRecordFieldsByFilter(null, GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS,
                    new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_USER_REQS, rmvFldN, rmvFldV), new EnumIntTableFields[]{TblsReqs.ProcedureUserRequirements.CODE}, null, false);
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(recordFieldsByFilter[0][0].toString()))){
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.PARENT_USER_REQUIREMENT_HAS_CHILD, new Object[]{parentCode, recordFieldsByFilter.length});
                        this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.PARENT_USER_REQUIREMENT_HAS_CHILD;
                        this.messageDynamicData = new Object[]{parentCode, recordFieldsByFilter.length};
                     break;
                    }
                    rmvFldN[rmvFldN.length-1]=TblsReqs.ProcedureUserRequirements.CODE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause();
                }
                removeDiagn = Rdbms.removeRecordInTable(TblsReqs.TablesReqs.PROCEDURE_USER_REQS,
                    new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_USER_REQS, rmvFldN, rmvFldV), null);
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{parentCode, code});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{parentCode, code});
                    if (code.length()>0){
                        this.messageDynamicData = new Object[]{code, procedureName, procedureVersion};
                    }else{
                        this.messageDynamicData = new Object[]{parentCode, procedureName, procedureVersion};
                    }
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                break;
            case MOVE_USER_REQUIREMENT:
                parentCode = argValues[i].toString();
                i++;
                code = argValues[i].toString();
                i++;
                String newParentCode = argValues[i].toString();
                i++;
                String newCode = argValues[i].toString();
                procedureParentUserAndReqList = procedureParentAndUserRequirementsList(procedureName, procedureVersion, TblsReqs.ProcedureUserRequirements.PARENT_CODE);
                valuePosicArray2D = valuePosicArray2D(procedureParentUserAndReqList, new Object[][]{{0, parentCode},{1, code}});                        
                if (valuePosicArray2D.length==0) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{parentCode+"-"+code, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{parentCode+"-"+code, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND;

                    this.messageDynamicData = new Object[]{parentCode+"-"+code, procedureName, procedureVersion};
                    break;
                }
                valuePosicArray2D = valuePosicArray2D(procedureParentUserAndReqList, new Object[][]{{0, newParentCode},{1, newCode}});                        
                if (valuePosicArray2D.length>0) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_ALREADY_EXISTS, new Object[]{parentCode, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_ALREADY_EXISTS, new Object[]{newCode, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_ALREADY_EXISTS;
                    this.messageDynamicData = new Object[]{newCode, procedureName, procedureVersion};
                    break;
                }                    
                valuePosicArray2D = valuePosicArray2D(procedureParentUserAndReqList, new Object[][]{{0, newParentCode}});                        
                if (valuePosicArray2D.length==0) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{parentCode, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{newParentCode, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.PARENT_USER_REQUIREMENT_NOT_FOUND;

                    this.messageDynamicData = new Object[]{newParentCode, procedureName, procedureVersion};
                    break;
                }
                String[] whereFldN=new String[]{TblsReqs.ProcedureUserRequirements.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirements.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName(), TblsReqs.ProcedureUserRequirements.CODE.getName()};
                Object[] whereFldV=new Object[]{procedureName, procedureVersion, procInstanceName, parentCode, code};
                fldN=new String[]{TblsReqs.ProcedureUserRequirements.PARENT_CODE.getName(), TblsReqs.ProcedureUserRequirements.CODE.getName()};
                fldV=new Object[]{newParentCode, newCode};
                removeDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROCEDURE_USER_REQS,
                    EnumIntTableFields.getTableFieldsFromString(TblsReqs.TablesReqs.PROCEDURE_USER_REQS, fldN), fldV,
                    new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_USER_REQS,whereFldN, whereFldV), null);                    
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{parentCode});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{parentCode});
                    if (code.length()>0){
                        this.messageDynamicData = new Object[]{code, procedureName, procedureVersion};
                    }else{
                        this.messageDynamicData = new Object[]{parentCode, procedureName, procedureVersion};
                    }
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                break;            
            case UPDATE_RISK:
            case NEW_RISK:
                String reqId = argValues[i].toString();
                i++;
                String riskId="";
                if ("UPDATE_RISK".equalsIgnoreCase(endPoint.getName())){
                    riskId = argValues[i].toString();
                    i++;
                }
                String level = argValues[i].toString();
                i++;
                String comment = argValues[i].toString();
                i++;
                String partOfTesting = argValues[i].toString();
                i++;
                String expectedTestNames = argValues[i].toString();
/*                i++;
                fieldName = argValues[i].toString();
                i++;
                fieldValue = argValues[i].toString();
                i++;*/
                procedureParentUserAndReqList = procedureParentAndUserRequirementsList(procedureName, procedureVersion, TblsReqs.ProcedureUserRequirements.REQ_ID);                
                valuePosicArray2D = valuePosicArray2D(procedureParentUserAndReqList, new Object[][]{{0, Integer.valueOf(reqId)}});
                if (valuePosicArray2D.length==0){
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.RISK_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.RISK_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.RISK_REQUIREMENT_NOT_FOUND;

                    this.messageDynamicData = new Object[]{reqId, procedureName, procedureVersion};
                    break;
                }

                fieldNames=new String[0];
                fieldValues=new Object[0];
/*                if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                if (fieldValue!=null && fieldValue.length()>0) fieldValues = convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"), TblsReqs.TablesReqs.PROCEDURE_SOP_META_DATA, fieldName.split("\\|"));
                if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                    InternalMessage errMsg=(InternalMessage)fieldValues[1];
                    actionDiagnoses=null;                         
                    this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, errMsg.getMessageCodeObj(), errMsg.getMessageCodeVariables());
                    this.diagnosticObjIntMsg=errMsg.getMessageCodeObj();
                    break;
                }else{                
*/                
                fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{TblsReqs.ProcedureRiskAssessment.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRiskAssessment.PROCEDURE_VERSION.getName(),
                    TblsReqs.ProcedureRiskAssessment.PROC_INSTANCE_NAME.getName()});
                fieldValues=LPArray.addValueToArray1D(fieldValues,new Object[]{procedureName, procedureVersion, procInstanceName});
                if (level.length()>0){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsReqs.ProcedureRiskAssessment.LEVEL.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldValues, level);
                }
                if (comment.length()>0){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsReqs.ProcedureRiskAssessment.COMMENTS.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldValues, comment);
                }
                if (partOfTesting.length()>0){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsReqs.ProcedureRiskAssessment.HASTOBE_PART_OF_TESTING.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldValues, Boolean.valueOf(partOfTesting));
                }
                if (expectedTestNames.length()>0){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsReqs.ProcedureRiskAssessment.EXPECTED_TESTS.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldValues, expectedTestNames);
                }
                if ("NEW_RISK".equalsIgnoreCase(endPoint.getName())){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsReqs.ProcedureRiskAssessment.REQ_ID.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldValues, Integer.valueOf(reqId));
                    removeDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROCEDURE_RISK_ASSESSMENT,
                        fieldNames, fieldValues);
                }else{
                    removeDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROCEDURE_RISK_ASSESSMENT,
                        EnumIntTableFields.getTableFieldsFromString(TblsReqs.TablesReqs.PROCEDURE_RISK_ASSESSMENT, fieldNames), fieldValues,
                        new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_RISK_ASSESSMENT,new String[]{TblsReqs.ProcedureRiskAssessment.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRiskAssessment.PROCEDURE_VERSION.getName(),
                            TblsReqs.ProcedureRiskAssessment.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureRiskAssessment.REQ_ID.getName(), TblsReqs.ProcedureRiskAssessment.RISK_ID.getName()},
                        new Object[]{procedureName, procedureVersion, procInstanceName, Integer.valueOf(reqId), Integer.valueOf(riskId)}), null);
                }
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{reqId});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{reqId});
                    this.messageDynamicData = new Object[]{reqId, procedureName, procedureVersion};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
//                }
                break;
            case SET_RISK_READY_FOR_REVISION:
            case SIGN_RISK:
                reqId = argValues[i].toString();
                i++;                
                riskId = argValues[i].toString();
                String[] updfieldNames= new String[]{};
                Object[] updfieldValues= new Object[]{};
                if ("SET_RISK_READY_FOR_REVISION".equalsIgnoreCase(endPoint.getName())){
                    updfieldNames=LPArray.addValueToArray1D(updfieldNames, TblsReqs.ProcedureRiskAssessment.READY_FOR_REVISION.getName());
                    updfieldValues=LPArray.addValueToArray1D(updfieldValues, new Object[]{true});
                }else{
                    updfieldNames=LPArray.addValueToArray1D(updfieldNames, TblsReqs.ProcedureRiskAssessment.READY_FOR_REVISION.getName());
                    updfieldNames=LPArray.addValueToArray1D(updfieldNames, TblsReqs.ProcedureRiskAssessment.SIGNED.getName());
                    updfieldValues=LPArray.addValueToArray1D(updfieldValues, new Object[]{false, true});
                }
                RdbmsObject updateDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROCEDURE_RISK_ASSESSMENT,
                    EnumIntTableFields.getTableFieldsFromString(TblsReqs.TablesReqs.PROCEDURE_RISK_ASSESSMENT, updfieldNames), 
                    updfieldValues,
                    new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_RISK_ASSESSMENT,new String[]{TblsReqs.ProcedureRiskAssessment.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRiskAssessment.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureRiskAssessment.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureRiskAssessment.REQ_ID.getName(), TblsReqs.ProcedureRiskAssessment.RISK_ID.getName()},
                            new Object[]{procedureName, procedureVersion, procInstanceName, Integer.valueOf(reqId), Integer.valueOf(riskId)}), null);
                if (Boolean.TRUE.equals(updateDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, updateDiagn.getErrorMessageCode(), new Object[]{reqId});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, updateDiagn.getErrorMessageCode(), new Object[]{reqId});
                    this.messageDynamicData = new Object[]{reqId, procedureName, procedureVersion};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, updateDiagn.getErrorMessageCode(), updateDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, updateDiagn.getErrorMessageCode(), updateDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = updateDiagn.getErrorMessageCode();
                    this.messageDynamicData = updateDiagn.getErrorMessageVariables();
                }
                break;
            case REMOVE_RISK:
                reqId = argValues[3].toString();
                riskId = argValues[4].toString();
                procedureParentUserAndReqList = procedureParentAndUserRequirementsList(procedureName, procedureVersion, TblsReqs.ProcedureUserRequirements.REQ_ID);                
                valuePosicArray2D = valuePosicArray2D(procedureParentUserAndReqList, new Object[][]{{0, Integer.valueOf(reqId)}});
                if (valuePosicArray2D.length==0){
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND;

                    this.messageDynamicData = new Object[]{reqId, procedureName, procedureVersion};
                    break;
                }                
                removeDiagn = Rdbms.removeRecordInTable(TblsReqs.TablesReqs.PROCEDURE_RISK_ASSESSMENT,
                    new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_RISK_ASSESSMENT,new String[]{TblsReqs.ProcedureRiskAssessment.PROCEDURE_NAME.getName(), TblsReqs.ProcedureRiskAssessment.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureRiskAssessment.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureRiskAssessment.REQ_ID.getName(), TblsReqs.ProcedureRiskAssessment.RISK_ID.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, Integer.valueOf(reqId), Integer.valueOf(riskId)}), null);
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{riskId});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{riskId});
                    this.messageDynamicData = new Object[]{riskId, procedureName, procedureVersion};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                break;
            case ADD_BUSINESS_RULE_REQ_SOLUTION:
                reqId = argValues[i].toString();
                i++;
                String bRuleArea = argValues[i].toString();
                i++;
                String bRuleName = argValues[i].toString();
                i++;
                String bRuleValue= argValues[i].toString();
                i++;
                fieldName = argValues[i].toString();
                i++;
                fieldValue = argValues[i].toString();
                i++;
                procedureParentUserAndReqList = procedureParentAndUserRequirementsList(procedureName, procedureVersion, TblsReqs.ProcedureUserRequirements.REQ_ID);                
                valuePosicArray2D = valuePosicArray2D(procedureParentUserAndReqList, new Object[][]{{0, Integer.valueOf(reqId)}});
                if (valuePosicArray2D.length==0){
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND;

                    this.messageDynamicData = new Object[]{reqId, procedureName, procedureVersion};
                    break;
                }                
                Object[] isModuleBusinessRulesAvailableDiagn=ProcedureDefinitionToInstanceUtility.isModuleBusinessRulesAvailable(procInstanceName, bRuleArea, bRuleName, bRuleValue);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isModuleBusinessRulesAvailableDiagn[0].toString())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, (EnumIntMessages) isModuleBusinessRulesAvailableDiagn[1], (Object[]) isModuleBusinessRulesAvailableDiagn[2]);
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, (EnumIntMessages) isModuleBusinessRulesAvailableDiagn[1], (Object[]) isModuleBusinessRulesAvailableDiagn[2]);
                    this.diagnosticObjIntMsg = (EnumIntMessages) isModuleBusinessRulesAvailableDiagn[1];
                    this.messageDynamicData = (Object[])isModuleBusinessRulesAvailableDiagn[2];
                    break;
                }

                fieldNames=new String[0];
                fieldValues=new Object[0];
                if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                if (fieldValue!=null && fieldValue.length()>0) fieldValues = convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, fieldName.split("\\|"));
                if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                    InternalMessage errMsg=(InternalMessage)fieldValues[1];
                    actionDiagnoses=null;                         
                    this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, errMsg.getMessageCodeObj(), errMsg.getMessageCodeVariables());
                    this.diagnosticObjIntMsg=errMsg.getMessageCodeObj();
                    break;
                }else{                
                    fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{TblsReqs.ProcedureReqSolution.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolution.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.REQ_ID.getName(), TblsReqs.ProcedureReqSolution.TYPE.getName(),
                        TblsReqs.ProcedureReqSolution.BUSINESS_RULE_AREA.getName(), TblsReqs.ProcedureReqSolution.BUSINESS_RULE.getName(), TblsReqs.ProcedureReqSolution.BUSINESS_RULE_VALUE.getName(),                
                    });                        
                    
                    fieldValues=LPArray.addValueToArray1D(fieldValues,new Object[]{procedureName, procedureVersion, procInstanceName, 
                        Integer.valueOf(reqId), ProcedureDefinitionToInstanceSections.ReqSolutionTypes.BUSINESS_RULE.getTagValue(), bRuleArea, bRuleName, bRuleValue});
                    removeDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION,
                        fieldNames, fieldValues);
                    if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{reqId});
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{reqId});
                        this.messageDynamicData = new Object[]{bRuleName, procedureName, procedureVersion};
                    } else {
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                        this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                        this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                    }
                }
                break;                
            case ADD_WINDOW_REQ_SOLUTION:
                reqId = argValues[i].toString();
                i++;
                String windowName = argValues[i].toString();
                i++;
                String windowQuery = argValues[i].toString();
                i++;
                String windowType= argValues[i].toString(); 
                i++;
                String windowMode= argValues[i].toString(); 
                i++;
                roleName= argValues[i].toString();
                i++;
                sopName= argValues[i].toString();
                i++;
                fieldName = argValues[i].toString();
                i++;
                fieldValue = argValues[i].toString();
                i++;
                procedureRolesList = procedureRolesList(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureRolesList, roleName, false))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND;
                    this.messageDynamicData = new Object[]{roleName, procedureName, procedureVersion};
                    break;
                }
                procedureParentUserAndReqList = procedureParentAndUserRequirementsList(procedureName, procedureVersion, TblsReqs.ProcedureUserRequirements.REQ_ID);                
                valuePosicArray2D = valuePosicArray2D(procedureParentUserAndReqList, new Object[][]{{0, Integer.valueOf(reqId)}});
                if (valuePosicArray2D.length==0){
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND;

                    this.messageDynamicData = new Object[]{reqId, procedureName, procedureVersion};
                    break;
                }                
                Object[] isModuleWindowAvailableDiagn=ProcedureDefinitionToInstanceUtility.isModuleWindowAvailable(procInstanceName, windowQuery);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isModuleWindowAvailableDiagn[0].toString())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, (EnumIntMessages) isModuleWindowAvailableDiagn[1], (Object[]) isModuleWindowAvailableDiagn[2]);
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, (EnumIntMessages) isModuleWindowAvailableDiagn[1], (Object[]) isModuleWindowAvailableDiagn[2]);
                    this.diagnosticObjIntMsg = (EnumIntMessages) isModuleWindowAvailableDiagn[1];
                    this.messageDynamicData = (Object[])isModuleWindowAvailableDiagn[2];
                    break;
                }

                fieldNames=new String[0];
                fieldValues=new Object[0];
                if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                if (fieldValue!=null && fieldValue.length()>0) fieldValues = convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, fieldName.split("\\|"));
                if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                    InternalMessage errMsg=(InternalMessage)fieldValues[1];
                    actionDiagnoses=null;                         
                    this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, errMsg.getMessageCodeObj(), errMsg.getMessageCodeVariables());
                    this.diagnosticObjIntMsg=errMsg.getMessageCodeObj();
                    break;
                }else{                
                    fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{TblsReqs.ProcedureReqSolution.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolution.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.REQ_ID.getName(), TblsReqs.ProcedureReqSolution.TYPE.getName(),
                        TblsReqs.ProcedureReqSolution.WINDOW_NAME.getName(), TblsReqs.ProcedureReqSolution.ROLES.getName(),
                        TblsReqs.ProcedureReqSolution.WINDOW_QUERY.getName(), TblsReqs.ProcedureReqSolution.WINDOW_TYPE.getName(), 
                        TblsReqs.ProcedureReqSolution.WINDOW_MODE.getName(), });                        
                    
                    fieldValues=LPArray.addValueToArray1D(fieldValues,new Object[]{procedureName, procedureVersion, procInstanceName, 
                        Integer.valueOf(reqId), ProcedureDefinitionToInstanceSections.ReqSolutionTypes.WINDOW.getTagValue(), windowName, roleName,
                        windowQuery, windowType, windowMode});
                    if (LPNulls.replaceNull(sopName).toString().length()>0){
                        fieldNames=LPArray.addValueToArray1D(fieldNames, TblsReqs.ProcedureReqSolution.SOP_NAME.getName());
                        fieldValues=LPArray.addValueToArray1D(fieldValues, sopName);
                    }
                    removeDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION,
                        fieldNames, fieldValues);
                    if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{reqId});
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{reqId});
                        this.messageDynamicData = new Object[]{reqId, procedureName, procedureVersion};
                    } else {
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                        this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                        this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                    }
                }
                break;
            case ADD_WINDOW_BUTTON_REQ_SOLUTION:
                reqId = argValues[i].toString();
                i++;
                String actionType = argValues[i].toString();
                i++;
                String windowActionName = argValues[i].toString();
                i++;
                String confirmDialog = argValues[i].toString();
                i++;
                String confirmDialogDetail= argValues[i].toString(); 
                i++;
                roleName= argValues[i].toString();
                i++;
                fieldName = argValues[i].toString();
                i++;
                fieldValue = argValues[i].toString();
                i++;
                procedureRolesList = procedureRolesList(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureRolesList, roleName, false))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND;
                    this.messageDynamicData = new Object[]{roleName, procedureName, procedureVersion};
                    break;
                }
                procedureParentUserAndReqList = procedureParentAndUserRequirementsList(procedureName, procedureVersion, TblsReqs.ProcedureUserRequirements.REQ_ID);                
                valuePosicArray2D = valuePosicArray2D(procedureParentUserAndReqList, new Object[][]{{0, Integer.valueOf(reqId)}});
                if (valuePosicArray2D.length==0){
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND;

                    this.messageDynamicData = new Object[]{reqId, procedureName, procedureVersion};
                    break;
                }                
                Object[] isModuleWindowActionAvailableDiagn=ProcedureDefinitionToInstanceUtility.isModuleWindowActionAvailable(procInstanceName, windowActionName);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isModuleWindowActionAvailableDiagn[0].toString())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, (EnumIntMessages) isModuleWindowActionAvailableDiagn[1], (Object[]) isModuleWindowActionAvailableDiagn[2]);
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, (EnumIntMessages) isModuleWindowActionAvailableDiagn[1], (Object[]) isModuleWindowActionAvailableDiagn[2]);
                    this.diagnosticObjIntMsg = (EnumIntMessages) isModuleWindowActionAvailableDiagn[1];
                    this.messageDynamicData = (Object[])isModuleWindowActionAvailableDiagn[2];
                    break;
                }
                if (Boolean.FALSE.equals((ProcedureDefinitionToInstanceSections.ReqSolutionTypes.WINDOW_BUTTON.getTagValue().equalsIgnoreCase(actionType))
                    || (ProcedureDefinitionToInstanceSections.ReqSolutionTypes.TABLE_ROW_BUTTON.getTagValue().equalsIgnoreCase(actionType))) ){
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.MODULE_WINDOW_BUTTON_NOT_FOUND, new Object[]{actionType, ProcedureDefinitionToInstanceSections.ReqSolutionTypes.WINDOW_BUTTON.getTagValue()+", "+ProcedureDefinitionToInstanceSections.ReqSolutionTypes.TABLE_ROW_BUTTON.getTagValue()});
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.MODULE_WINDOW_BUTTON_NOT_FOUND, new Object[]{actionType, ProcedureDefinitionToInstanceSections.ReqSolutionTypes.WINDOW_BUTTON.getTagValue()+", "+ProcedureDefinitionToInstanceSections.ReqSolutionTypes.TABLE_ROW_BUTTON.getTagValue()});
                        this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.MODULE_WINDOW_BUTTON_NOT_FOUND;
                        this.messageDynamicData =new Object[]{actionType, ProcedureDefinitionToInstanceSections.ReqSolutionTypes.WINDOW_BUTTON.getTagValue()+", "+ProcedureDefinitionToInstanceSections.ReqSolutionTypes.TABLE_ROW_BUTTON.getTagValue()};
                        break;
                }
                            
                fieldNames=new String[0];
                fieldValues=new Object[0];
                if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                if (fieldValue!=null && fieldValue.length()>0) fieldValues = convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, fieldName.split("\\|"));
                if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                    InternalMessage errMsg=(InternalMessage)fieldValues[1];
                    actionDiagnoses=null;                         
                    this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, errMsg.getMessageCodeObj(), errMsg.getMessageCodeVariables());
                    this.diagnosticObjIntMsg=errMsg.getMessageCodeObj();
                    break;
                }else{                
                    fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{TblsReqs.ProcedureReqSolution.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolution.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.REQ_ID.getName(), TblsReqs.ProcedureReqSolution.TYPE.getName(),
                        TblsReqs.ProcedureReqSolution.WINDOW_ACTION.getName(), TblsReqs.ProcedureReqSolution.ROLES.getName()});                        
                    
                    fieldValues=LPArray.addValueToArray1D(fieldValues,new Object[]{procedureName, procedureVersion, procInstanceName, 
                        Integer.valueOf(reqId), actionType, windowActionName, roleName});
                    if (confirmDialog.length()>0){
                        fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{TblsReqs.ProcedureReqSolution.CONFIRM_DIALOG.getName(), TblsReqs.ProcedureReqSolution.CONFIRM_DIALOG_DETAIL.getName()});
                        fieldValues=LPArray.addValueToArray1D(fieldValues,new Object[]{confirmDialog, confirmDialogDetail});
                    }
                    fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{TblsReqs.ProcedureReqSolution.QUERY_FOR_BUTTON.getName(), TblsReqs.ProcedureReqSolution.EXTRA_ACTIONS.getName()});
                    fieldValues=LPArray.addValueToArray1D(fieldValues,new Object[]{Boolean.valueOf(LPNulls.replaceNull(isModuleWindowActionAvailableDiagn[2]).toString()), isModuleWindowActionAvailableDiagn[3]});
                    removeDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, fieldNames, fieldValues);
                    if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{reqId});
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{reqId});
                        this.messageDynamicData = new Object[]{reqId, procedureName, procedureVersion};
                    } else {
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                        this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                        this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                    }
                }
                break;
            case ADD_SPECIAL_WINDOW_REQ_SOLUTION:
                reqId = argValues[i].toString();
                i++;
                String specialWindowName = argValues[i].toString();
                i++;
                windowMode= argValues[i].toString(); 
                i++;
                roleName= argValues[i].toString();
                i++;
                fieldName = argValues[i].toString();
                i++;
                fieldValue = argValues[i].toString();
                i++;
                procedureRolesList = procedureRolesList(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureRolesList, roleName, false))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND, new Object[]{roleName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.ROLE_NOT_FOUND;
                    this.messageDynamicData = new Object[]{roleName, procedureName, procedureVersion};
                    break;
                }

                procedureParentUserAndReqList = procedureParentAndUserRequirementsList(procedureName, procedureVersion, TblsReqs.ProcedureUserRequirements.REQ_ID);                
                valuePosicArray2D = valuePosicArray2D(procedureParentUserAndReqList, new Object[][]{{0, Integer.valueOf(reqId)}});
                if (valuePosicArray2D.length==0){
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND;

                    this.messageDynamicData = new Object[]{reqId, procedureName, procedureVersion};
                    break;
                }                
                Object[] isModuleSpecialWindowAvailableDiagn=ProcedureDefinitionToInstanceUtility.isModuleSpecialWindowAvailable(procInstanceName, specialWindowName);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(isModuleSpecialWindowAvailableDiagn[0].toString())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, (EnumIntMessages) isModuleSpecialWindowAvailableDiagn[1], (Object[]) isModuleSpecialWindowAvailableDiagn[2]);
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, (EnumIntMessages) isModuleSpecialWindowAvailableDiagn[1], (Object[]) isModuleSpecialWindowAvailableDiagn[2]);
                    this.diagnosticObjIntMsg = (EnumIntMessages) isModuleSpecialWindowAvailableDiagn[1];
                    this.messageDynamicData = (Object[])isModuleSpecialWindowAvailableDiagn[2];
                    break;
                }
                fieldNames=new String[0];
                fieldValues=new Object[0];
                if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                if (fieldValue!=null && fieldValue.length()>0) fieldValues = convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, fieldName.split("\\|"));
                if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                    InternalMessage errMsg=(InternalMessage)fieldValues[1];
                    actionDiagnoses=null;                         
                    this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, errMsg.getMessageCodeObj(), errMsg.getMessageCodeVariables());
                    this.diagnosticObjIntMsg=errMsg.getMessageCodeObj();
                    break;
                }else{                
                    fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{TblsReqs.ProcedureReqSolution.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolution.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.REQ_ID.getName(), TblsReqs.ProcedureReqSolution.TYPE.getName(),
                        TblsReqs.ProcedureReqSolution.WINDOW_NAME.getName(), TblsReqs.ProcedureReqSolution.SPECIAL_VIEW_NAME.getName(), TblsReqs.ProcedureReqSolution.ROLES.getName(), 
                        TblsReqs.ProcedureReqSolution.WINDOW_TYPE.getName(), TblsReqs.ProcedureReqSolution.WINDOW_MODE.getName(), TblsReqs.ProcedureReqSolution.SPECIAL_VIEW_JSON_MODEL.getName()});                        
                    
                    fieldValues=LPArray.addValueToArray1D(fieldValues,new Object[]{procedureName, procedureVersion, procInstanceName, 
                        Integer.valueOf(reqId), ProcedureDefinitionToInstanceSections.ReqSolutionTypes.SPECIAL_VIEW.getTagValue(), specialWindowName, specialWindowName, roleName,
                        isModuleSpecialWindowAvailableDiagn[3].toString(), windowMode, isModuleSpecialWindowAvailableDiagn[1]});
                    removeDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION,
                        fieldNames, fieldValues);
                    if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                        
                        
                        JSONObject speViewDefinition=new JSONObject(isModuleSpecialWindowAvailableDiagn[2].toString());
                        //speViewDefinition=(JSONObject) speViewDefinition.get("requirementsInfo");
                        if (Boolean.TRUE.equals(speViewDefinition.has("window_actions"))){
                            org.json.JSONArray speViewDefinitionActions=speViewDefinition.getJSONArray("window_actions");                       
                            for (i=0;i<speViewDefinitionActions.length();i++){
                                JSONObject curAction = (JSONObject) speViewDefinitionActions.get(i);                            
                                windowActionName = curAction.get("actionName").toString();

                                fieldNames=new String[]{TblsReqs.ProcedureReqSolution.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolution.PROCEDURE_VERSION.getName(),
                                    TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.REQ_ID.getName(), TblsReqs.ProcedureReqSolution.TYPE.getName(),
                                    TblsReqs.ProcedureReqSolution.WINDOW_ACTION.getName(), TblsReqs.ProcedureReqSolution.ROLES.getName()};                        

                                fieldValues=new Object[]{procedureName, procedureVersion, procInstanceName, 
                                    Integer.valueOf(reqId), ProcedureDefinitionToInstanceSections.ReqSolutionTypes.WINDOW_BUTTON.getTagValue(), windowActionName, roleName};
                                /*if (confirmDialog.length()>0){
                                    fieldNames=LPArray.addValueToArray1D(fieldNames, new String[]{TblsReqs.ProcedureReqSolution.CONFIRM_DIALOG.getName(), TblsReqs.ProcedureReqSolution.CONFIRM_DIALOG_DETAIL.getName()});
                                    fieldValues=LPArray.addValueToArray1D(fieldValues,new Object[]{confirmDialog, confirmDialogDetail});
                                }*/
                                removeDiagn = Rdbms.insertRecordInTable(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, fieldNames, fieldValues);
                            }
                        }
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{reqId});
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{reqId});
                        this.messageDynamicData = new Object[]{reqId, procedureName, procedureVersion};
                    } else {
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                        this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                        this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                    }
                }
                break;
            case UPDATE_REQUIREMENT_SOLUTION:
                reqId = argValues[i].toString();
                i++;
                String solId = argValues[i].toString();
                i++;
                sopName = argValues[i].toString();
                i++;
                fieldName = argValues[i].toString();
                i++;
                fieldValue = argValues[i].toString();
                i++;
                
                fieldNames=new String[0];
                fieldValues=new Object[0];
                if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                if (fieldValue!=null && fieldValue.length()>0) fieldValues = convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, fieldName.split("\\|"));
                if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                    InternalMessage errMsg=(InternalMessage)fieldValues[1];
                    actionDiagnoses=null;                         
                    this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, errMsg.getMessageCodeObj(), errMsg.getMessageCodeVariables());
                    this.diagnosticObjIntMsg=errMsg.getMessageCodeObj();
                    break;
                }                
                procedureParentUserAndReqList = procedureParentAndUserRequirementsList(procedureName, procedureVersion, TblsReqs.ProcedureUserRequirements.REQ_ID);                
                valuePosicArray2D = valuePosicArray2D(procedureParentUserAndReqList, new Object[][]{{0, Integer.valueOf(reqId)}});
                if (valuePosicArray2D.length==0){
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND, new Object[]{reqId, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.USER_REQUIREMENT_NOT_FOUND;

                    this.messageDynamicData = new Object[]{reqId, procedureName, procedureVersion};
                    break;
                }         
                if (LPNulls.replaceNull(sopName).toString().length()>0){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsReqs.ProcedureReqSolution.SOP_NAME.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldValues, sopName);
                }
                removeDiagn = Rdbms.updateTableRecordFieldsByFilter(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION,
                    EnumIntTableFields.getTableFieldsFromString(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, fieldNames), fieldValues,
                    new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION,new String[]{TblsReqs.ProcedureReqSolution.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolution.PROCEDURE_VERSION.getName(),
                            TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.REQ_ID.getName(), TblsReqs.ProcedureReqSolution.SOLUTION_ID.getName()},
                        new Object[]{procedureName, procedureVersion, procInstanceName, Integer.valueOf(reqId), Integer.valueOf(solId)}), null);                    
                
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{solId});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{solId});
                    this.messageDynamicData = new Object[]{solId, procedureName, procedureVersion};                    
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                break;

            case REMOVE_REQ_SOLUTION:
                String solutionId = argValues[3].toString();
/*                procedureSopsList = procedureSops(procedureName, procedureVersion);
                if (Boolean.FALSE.equals(LPArray.valueInArray(procedureSopsList, sopName, false))) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.SOLUTION_REQUIREMENT_NOT_FOUND, new Object[]{sopName, procedureName, procedureVersion});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ReqProcedureDefinitionErrorTraping.SOLUTION_REQUIREMENT_NOT_FOUND, new Object[]{sopName, procedureName, procedureVersion});
                    this.diagnosticObjIntMsg = ReqProcedureDefinitionErrorTraping.SOLUTION_REQUIREMENT_NOT_FOUND;
                    this.messageDynamicData = new Object[]{sopName, procedureName, procedureVersion};
                    break;
                }*/
                removeDiagn = Rdbms.removeRecordInTable(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION,
                    new SqlWhere(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION,new String[]{TblsReqs.ProcedureReqSolution.PROCEDURE_NAME.getName(), TblsReqs.ProcedureReqSolution.PROCEDURE_VERSION.getName(),
                        TblsReqs.ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), TblsReqs.ProcedureReqSolution.REQ_ID.getName()},
                    new Object[]{procedureName, procedureVersion, procInstanceName, Integer.valueOf(solutionId)}), null);
                if (Boolean.TRUE.equals(removeDiagn.getRunSuccess())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{solutionId});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, removeDiagn.getErrorMessageCode(), new Object[]{solutionId});
                    this.messageDynamicData = new Object[]{solutionId, procedureName, procedureVersion};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, removeDiagn.getErrorMessageCode(), removeDiagn.getErrorMessageVariables());
                    this.diagnosticObjIntMsg = removeDiagn.getErrorMessageCode();
                    this.messageDynamicData = removeDiagn.getErrorMessageVariables();
                }
                break;
            case GET_UOM:
                String uomName = argValues[3].toString();
                String importType = argValues[4].toString();
                actionDiagnoses = getUomFromConfig(uomName, importType);
                break;
            case PROC_DEPLOY_CHECKER:
                request.setAttribute("run_as_checker", true);
            /*                    JSONObject jMainObj = new JSONObject();
                    String mainObjectName = "proc_deploy_check_summary"; 
                    procedureName=argValues[0].toString();
                    procedureVersion = (Integer) argValues[1];  
                    procInstanceName=argValues[2].toString();       
                    JSONObject jObj=new JSONObject();                

                    
                    jObj.put("Status", "Under Development");
                    jMainObj.put(mainObjectName, jObj);
                    LPFrontEnd.servletReturnSuccess(request, response, jMainObj);                    
                    return;                    */
            case DEPLOY_REQUIREMENTS:
                request.setAttribute("procedureName", procedureName);
                request.setAttribute("procInstanceName", procInstanceName);
                request.setAttribute("endPointName", endPoint.getName());
                //RequestDispatcher rd = request.getRequestDispatcher("/testing/platform/ProcedureDeployment");
                RequestDispatcher rd = request.getRequestDispatcher("/ProcedureDefinitionToInstance");

                try {
                    rd.forward(request, response);
                } catch (ServletException | IOException ex) {
                    Logger.getLogger(LPFrontEnd.class.getName()).log(Level.SEVERE, null, ex);
                }
                actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "Completed", null);
                this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, LPPlatform.LpPlatformSuccess.ALL_FINE, new Object[]{});
                break;
            case DEFINITION_CHECKER:
                procedureName = argValues[0].toString();
                procedureVersion = (Integer) argValues[1];
                procInstanceName = argValues[2].toString();
                
                ProcDefinitionChecker defChk=new ProcDefinitionChecker(procedureName, procedureVersion, procInstanceName);
                
                Rdbms.closeRdbms();
                LPFrontEnd.servletReturnSuccess(request, response, defChk.publishReport());                
                break;
            case DEPLOY_REQUIREMENTS_CLONE_SPRINT:
                String newProcInstanceName = argValues[3].toString();
                Boolean continueIfNewExists = Boolean.valueOf(LPNulls.replaceNull(argValues[4]).toString());
                String[] clonableProcInstanceFldName = new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()};
                Object[] clonableProcInstanceFldValue = new Object[]{procedureName, procedureVersion, procInstanceName};
                Object[] existsRecord = Rdbms.existsRecord("", TablesReqs.PROCEDURE_INFO.getRepositoryName(), TablesReqs.PROCEDURE_INFO.getTableName(),
                        clonableProcInstanceFldName, clonableProcInstanceFldValue);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(existsRecord[0].toString())) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, null);
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, null);
                    this.diagnosticObjIntMsg = RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND;
                    this.messageDynamicData = null;
                    break;
                }
                if (Boolean.FALSE.equals(continueIfNewExists)) {
                    existsRecord = Rdbms.existsRecord("", TablesReqs.PROCEDURE_INFO.getRepositoryName(), TablesReqs.PROCEDURE_INFO.getTableName(),
                            new String[]{TblsReqs.ProcedureInfo.PROCEDURE_NAME.getName(), TblsReqs.ProcedureInfo.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName()},
                            new Object[]{procedureName, procedureVersion, newProcInstanceName});
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(existsRecord[0].toString())) {
                        actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.RECORD_ALREADY_EXISTS, null);
                        this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtilitiesErrorTrapping.RECORD_ALREADY_EXISTS, null);
                        this.diagnosticObjIntMsg = TrazitUtilitiesErrorTrapping.RECORD_ALREADY_EXISTS;
                        this.messageDynamicData = null;
                        break;
                    }
                }
                String[] tblsArr = null;
                String[] tblsWithErrorArr = null;
                for (EnumIntTables curTbl : TablesReqs.values()) {
                    Integer valuePosicInArray = null;
                    tblsArr = LPArray.addValueToArray1D(tblsArr, curTbl.getTableName());
                    if ("fe_proc_model".equalsIgnoreCase(curTbl.getTableName())) {
                        valuePosicInArray = -1;
                    }
                    String[] curTblAllFields = EnumIntTableFields.getAllFieldNames(curTbl.getTableFields());
                    Object[][] curTblInfo = QueryUtilitiesEnums.getTableData(curTbl,
                            EnumIntTableFields.getTableFieldsFromString(curTbl, "ALL"),
                            clonableProcInstanceFldName, clonableProcInstanceFldValue, clonableProcInstanceFldName);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(curTblInfo[0][0].toString())) {
                        tblsWithErrorArr = LPArray.addValueToArray1D(tblsWithErrorArr, curTbl.getTableName());
                    } else {
                        valuePosicInArray = LPArray.valuePosicInArray(curTblAllFields, TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME.getName());
                        if (valuePosicInArray == -1) {
                            tblsWithErrorArr = LPArray.addValueToArray1D(tblsWithErrorArr, curTbl.getTableName());
                        } else {
                            curTblInfo = LPArray.setColumnValueToArray2D(curTblInfo, valuePosicInArray, newProcInstanceName);
                            for (Object[] curTblRec : curTblInfo) {
                                RdbmsObject insertRecordInTable = Rdbms.insertRecordInTable(curTbl,
                                        curTblAllFields, curTblRec);
                                if (Boolean.FALSE.equals(insertRecordInTable.getRunSuccess())) {
                                    tblsWithErrorArr = LPArray.addValueToArray1D(tblsWithErrorArr, curTbl.getTableName());
                                }
                            }
                        }
                    }
                }
                if (tblsWithErrorArr != null && tblsWithErrorArr.length > 0) {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.DB_ERROR, new Object[]{Arrays.toString(tblsWithErrorArr)});
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, RdbmsErrorTrapping.DB_ERROR, new Object[]{Arrays.toString(tblsWithErrorArr)});
                    this.diagnosticObjIntMsg = RdbmsErrorTrapping.DB_ERROR;
                    this.messageDynamicData = new Object[]{Arrays.toString(tblsWithErrorArr)};
                } else {
                    actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.ALL_FINE, null);
                    this.diagnosticObj = new InternalMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.ALL_FINE, null);
                }
                break;
            case SUGGEST_SPEC_LIMITS_TESTING:
                procInstanceName = argValues[2].toString();
                ProcedureRequestSession reqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
                reqSession.setProcInstanceName(procInstanceName);
                
                String spec = argValues[3].toString();
                Integer specVersion = null;
                if (LPNulls.replaceNull(argValues[4]).toString().length() > 0) {
                    specVersion = (Integer) argValues[4];
                }
                Object[] testing = DataSpec.suggestTestingForSpec(spec, specVersion);
                Boolean saveScript=Boolean.valueOf(LPNulls.replaceNull(argValues[5]).toString());
                Object[][] testingData=(Object[][]) testing[1];
                
                if (saveScript && testingData.length>0){
                    TestingScript.newSpecScript(LPTestingParams.TestingServletsConfig.DB_SCHEMACONFIG_SPEC_RESULTCHECK.name(), true, 
                            spec, specVersion, (String[]) testing[0], (Object[][]) testing[1]);
                }
                JSONArray jArr = new JSONArray();
                for (Object[] curRow : testingData) {
                    jArr.add(LPJson.convertArrayRowToJSONObject((String[]) testing[0], curRow));
                }
                LPFrontEnd.servletReturnSuccess(request, response, jArr);
                break;
            case NEW_SCRIPT_TESTING:
                String purpose = argValues[3].toString();                
                this.diagnosticObj = TestingScriptRecords.newScriptRecord(procInstanceName, purpose);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())){
                    this.messageDynamicData=new Object[]{this.diagnosticObj.getNewObjectId(), procInstanceName};
                }
                break;
            case DELETE_SCRIPT_TESTING:
                String scriptId = argValues[3].toString();
                TestingScriptRecords tstScript=new TestingScriptRecords(procInstanceName, Integer.valueOf(scriptId));
                this.diagnosticObj = tstScript.deleteScriptRecord();
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())){
                    this.messageDynamicData=new Object[]{scriptId};
                }
                break;
            case SCRIPT_SAVE_POINT:
                scriptId = argValues[3].toString();
                fieldNames=new String[0];
                fieldValues=new Object[0];
                String tester = argValues[4].toString();
                if (argValues[4].toString().length()>0){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTesting.ScriptSavePoint.TESTER.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldNames, argValues[4].toString());
                }
                if (argValues[5].toString().length()>0){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTesting.ScriptSavePoint.PURPOSE.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldNames, argValues[5].toString());
                }
                if (argValues[6].toString().length()>0){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTesting.ScriptSavePoint.REVIEWER.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldNames, argValues[6].toString());
                }
                if (argValues[7].toString().length()>0){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTesting.ScriptSavePoint.CONCLUSION.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldNames, argValues[7].toString());
                }
                if (argValues[8].toString().length()>0){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTesting.ScriptSavePoint.SIGNED.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldNames, Boolean.valueOf(argValues[8].toString()));
                }
                tstScript=new TestingScriptRecords(procInstanceName, Integer.valueOf(scriptId));
                this.diagnosticObj = tstScript.scriptTestSavePoint(Integer.valueOf(scriptId), fieldNames, fieldValues);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())){
                    this.messageDynamicData=new Object[]{scriptId};
                }
                break;
            case SCRIPT_ADD_STEP:
                scriptId = argValues[3].toString();
                String action = argValues[4].toString();

                fieldName = argValues[5].toString();
                fieldValue = argValues[6].toString();
                fieldNames=new String[]{TblsTesting.ScriptSteps.SCRIPT_ID.getName(), TblsTesting.ScriptSteps.ACTION_NAME.getName(), TblsTesting.ScriptSteps.ACTIVE.getName()};
                fieldValues=new Object[]{Integer.valueOf(scriptId), action, true};
                /*if (fieldName!=null && fieldName.length()>0) fieldNames = fieldName.split("\\|");                                            
                if (fieldValue!=null && fieldValue.length()>0) fieldValues = convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"), TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, fieldName.split("\\|"));
                if (fieldValue!=null && fieldName.length()>0) fieldValues = fieldValue.split("\\|");                                            
                if (fieldValues!=null && fieldValues.length>0 && fieldValues[0].toString().length()>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())){
                    InternalMessage errMsg=(InternalMessage)fieldValues[1];
                    actionDiagnoses=null;                         
                    this.diagnosticObj=new InternalMessage(LPPlatform.LAB_FALSE, errMsg.getMessageCodeObj(), errMsg.getMessageCodeVariables());
                    this.diagnosticObjIntMsg=errMsg.getMessageCodeObj();
                    break;
                }                                
                fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTesting.ScriptSteps.ACTION_NAME.getName());
                fieldValues=LPArray.addValueToArray1D(fieldNames, action);*/
                int iArg=1;
                for (String curArgValu: fieldValue.split("\\|")){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, "argument_0"+iArg++);                    
                    fieldValues=LPArray.addValueToArray1D(fieldValues, curArgValu.toString().split("\\*")[0]);                   
                }
                String expectedSyntaxis = argValues[7].toString();
                if (expectedSyntaxis.length()>0){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTesting.ScriptSteps.EXPECTED_SYNTAXIS.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldValues, expectedSyntaxis);
                }
                String expectedNotification = argValues[8].toString();
                if (expectedSyntaxis.length()>0){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTesting.ScriptSteps.EXPECTED_CODE.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldValues, expectedNotification);
                }
                String alternativeToken = argValues[9].toString();
                if (alternativeToken.length()>0){
                    fieldNames=LPArray.addValueToArray1D(fieldNames, TblsTesting.ScriptSteps.ALTERNATIVE_TOKEN.getName());
                    fieldValues=LPArray.addValueToArray1D(fieldValues, alternativeToken);
                }
                tstScript=new TestingScriptRecords(procInstanceName, Integer.valueOf(scriptId));
                this.diagnosticObj = tstScript.scriptTestAddStep(Integer.valueOf(scriptId), fieldNames, fieldValues);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())){
                    this.messageDynamicData=new Object[]{scriptId, scriptId};
                }
                break;
            case SCRIPT_REMOVE_STEP:
                scriptId = argValues[3].toString();
                String stepId = argValues[4].toString();
                tstScript=new TestingScriptRecords(procInstanceName, Integer.valueOf(scriptId));
                this.diagnosticObj = tstScript.scriptTestRemoveStep(Integer.valueOf(scriptId), Integer.valueOf(stepId));
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())){
                    this.messageDynamicData=new Object[]{stepId, scriptId};
                }
                break;
            case NEW_COVERAGE_TESTING:
                String scriptIdsList = argValues[3].toString();
                purpose = argValues[4].toString();                
                this.diagnosticObj = newCoverageTest(scriptIdsList, purpose);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())){
                    this.messageDynamicData=new Object[]{this.diagnosticObj.getNewObjectId(), scriptIdsList};
                }
                break;
            case DELETE_COVERAGE_TESTING:
                String coverageId = argValues[3].toString();
                CoverageTestingAnalysis cov=new CoverageTestingAnalysis(procInstanceName, Integer.valueOf(coverageId));
                this.diagnosticObj = cov.deleteCoverageTest();
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())){
                    this.messageDynamicData=new Object[]{coverageId};
                }
                break;
            case COVERAGE_ADD_SCRIPT:
                coverageId = argValues[3].toString();
                scriptId = argValues[4].toString();
                cov=new CoverageTestingAnalysis(procInstanceName, Integer.valueOf(coverageId));
                this.diagnosticObj = cov.coverageTestAddScript(Integer.valueOf(scriptId));
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())){
                    this.messageDynamicData=new Object[]{scriptId, coverageId};
                }
                break;
            case COVERAGE_REMOVE_SCRIPT:
                coverageId = argValues[3].toString();
                scriptId = argValues[4].toString();
                cov=new CoverageTestingAnalysis(procInstanceName, Integer.valueOf(coverageId));
                this.diagnosticObj = cov.coverageTestRemoveScript(Integer.valueOf(scriptId));
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())){
                    this.messageDynamicData=new Object[]{scriptId, coverageId};
                }
                break;
            case COVERAGE_EXCLUDE_ACTION:
                coverageId = argValues[3].toString();
                action = argValues[4].toString();
                cov=new CoverageTestingAnalysis(procInstanceName, Integer.valueOf(coverageId));
                this.diagnosticObj = cov.excludeCoverageAction(action);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())){
                    this.messageDynamicData=new Object[]{action, coverageId};
                }
                break;
            case COVERAGE_UNEXCLUDE_ACTION:
                coverageId = argValues[3].toString();
                action = argValues[4].toString();
                cov=new CoverageTestingAnalysis(procInstanceName, Integer.valueOf(coverageId));
                this.diagnosticObj = cov.unExcludeCoverageAction(action);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())){
                    this.messageDynamicData=new Object[]{action, coverageId};
                }
                break;
        }
        this.diagnostic = actionDiagnoses;        
        this.relatedObj = rObj;
        rObj.killInstance();
    }

    /**
     * @return the messageDynamicData
     */
    public Object[] getMessageDynamicData() {
        return messageDynamicData;
    }

    /**
     * @return the relatedObj
     */
    public RelatedObjects getRelatedObj() {
        return relatedObj;
    }

    /**
     * @return the endpointExists
     */
    public Boolean getEndpointExists() {
        return endpointExists;
    }

    /**
     * @return the diagnostic
     */
    public Object[] getDiagnostic() {
        return diagnostic;
    }
    public InternalMessage getDiagnosticObj() {
        return diagnosticObj;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }

}
