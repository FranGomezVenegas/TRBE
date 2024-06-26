/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.app.logic;

import databases.TblsApp;
import databases.TblsApp.TablesApp;
import databases.TblsAppConfig;
import databases.TblsAppConfig.TablesAppConfig;
import platform.app.apis.AdminActions;
import platform.app.definition.PlatformAdminEnums.PlatformAdminAPIActionsEndpoints;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.user.UserAndRolesViews;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.ApiErrorTraping;
import modules.masterdata.analysis.ConfigAnalysisStructure;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ClassPlatformAdmin {

    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    InternalMessage actionDiagnosesObj = null;
    private Boolean functionFound = false;

    public ClassPlatformAdmin(HttpServletRequest request, PlatformAdminAPIActionsEndpoints endPoint) {
        ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);

        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        InternalMessage actionDiagnoses = null;
        this.functionFound = true;
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
            this.actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.messageDynamicData = new Object[]{argValues[2].toString()};
            this.relatedObj = rObj;
            rObj.killInstance();
            return;
        }
        switch (endPoint) {
            case ADD_WHITE_IP:
                String ipVal1 = LPNulls.replaceNull(argValues[0]).toString();
                String ipVal2 = LPNulls.replaceNull(argValues[1]).toString();
                String ipVal3 = LPNulls.replaceNull(argValues[2]).toString();
                String ipVal4 = LPNulls.replaceNull(argValues[3]).toString();
                String description = LPNulls.replaceNull(argValues[4]).toString();
                actionDiagnoses = AdminActions.addWhiteIp(ipVal1, ipVal2, ipVal3, ipVal4, description);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_WHITE_LIST.getTableName(), actionDiagnoses.getNewObjectId());
                }
                break;
            case ADD_BLACK_IP:
                ipVal1 = LPNulls.replaceNull(argValues[0]).toString();
                ipVal2 = LPNulls.replaceNull(argValues[1]).toString();
                ipVal3 = LPNulls.replaceNull(argValues[2]).toString();
                ipVal4 = LPNulls.replaceNull(argValues[3]).toString();
                description = LPNulls.replaceNull(argValues[4]).toString();
                actionDiagnoses = AdminActions.addBlackIp(ipVal1, ipVal2, ipVal3, ipVal4, description);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), actionDiagnoses.getNewObjectId());
                }
                break;


            case ACTIVATE_WHITE_IP:
                String id = LPNulls.replaceNull(argValues[0]).toString();
                actionDiagnoses = AdminActions.activateWhiteIp(Integer.valueOf(id));
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_WHITE_LIST.getTableName(), id);
                }
                break;
            case DEACTIVATE_WHITE_IP:
                id = LPNulls.replaceNull(argValues[0]).toString();
                actionDiagnoses = AdminActions.deActivateWhiteIp(Integer.valueOf(id));
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_WHITE_LIST.getTableName(), id);
                }
                break;
            case UPDATE_WHITE_IP:
                Integer idInt = Integer.valueOf(LPNulls.replaceNull(argValues[0]).toString());
                ipVal1 = LPNulls.replaceNull(argValues[1]).toString();
                ipVal2 = LPNulls.replaceNull(argValues[2]).toString();
                ipVal3 = LPNulls.replaceNull(argValues[3]).toString();
                ipVal4 = LPNulls.replaceNull(argValues[4]).toString();
                description = LPNulls.replaceNull(argValues[5]).toString();
                actionDiagnoses = AdminActions.updateWhiteIp(idInt, ipVal1, ipVal2, ipVal3, ipVal4, description);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), idInt);
                }
                break;
            case REMOVE_WHITE_IP:
                id = LPNulls.replaceNull(argValues[0]).toString();
                actionDiagnoses = AdminActions.removeWhiteIp(Integer.valueOf(id));
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_WHITE_LIST.getTableName(), id);
                }
                break;
            case ACTIVATE_BLACK_IP:
                id = LPNulls.replaceNull(argValues[0]).toString();
                actionDiagnoses = AdminActions.activateBlackIp(Integer.valueOf(id));
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), id);
                }
                break;
            case DEACTIVATE_BLACK_IP:
                id = LPNulls.replaceNull(argValues[0]).toString();
                actionDiagnoses = AdminActions.deActivateBlackIp(Integer.valueOf(id));
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), id);
                }
                break;
            case REMOVE_BLACK_IP:
                id = LPNulls.replaceNull(argValues[0]).toString();
                actionDiagnoses = AdminActions.removeBlackIp(Integer.valueOf(id));
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), id);
                }
                break;
            case UPDATE_BLACK_IP:
                idInt = Integer.valueOf(LPNulls.replaceNull(argValues[0]).toString());
                ipVal1 = LPNulls.replaceNull(argValues[1]).toString();
                ipVal2 = LPNulls.replaceNull(argValues[2]).toString();
                ipVal3 = LPNulls.replaceNull(argValues[3]).toString();
                ipVal4 = LPNulls.replaceNull(argValues[4]).toString();
                description = LPNulls.replaceNull(argValues[5]).toString();
                actionDiagnoses = AdminActions.updateBlackIp(idInt, ipVal1, ipVal2, ipVal3, ipVal4, description);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP_PROC_DATA.getName(), TblsApp.TablesApp.IP_BLACK_LIST.getTableName(), idInt);
                }
                break;
            case UPDATE_USER_SHIFT:
                String newShift = LPNulls.replaceNull(argValues[0]).toString();
                String userName = LPNulls.replaceNull(argValues[1]).toString();
                if (userName.length() == 0) {
                    userName = instanceForActions.getToken().getUserName();
                }
                EnumIntTableFields[] updFldsN = EnumIntTableFields.getTableFieldsFromString(TablesAppConfig.PERSON,
                        new String[]{TblsAppConfig.Person.SHIFT.getName()});
                actionDiagnoses = UserAndRolesViews.updateUserPersonFields(userName, updFldsN, new Object[]{newShift});
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()) && userName.length() > 0) {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), userName);
                    this.messageDynamicData = new Object[]{newShift, userName};
                }
                break;

            case UPDATE_USER_MAIL:
                String newMail = LPNulls.replaceNull(argValues[0]).toString();
                userName = LPNulls.replaceNull(argValues[1]).toString();
                if (userName.length() == 0) {
                    userName = instanceForActions.getToken().getUserName();
                }
                updFldsN = EnumIntTableFields.getTableFieldsFromString(TablesApp.USERS,
                        new String[]{TblsApp.Users.EMAIL.getName()});
                actionDiagnoses = UserAndRolesViews.updateUsersFields(userName, updFldsN, new Object[]{newMail});
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()) && userName.length() > 0) {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), userName);
                    this.messageDynamicData = new Object[]{newMail};
                }
                break;
            case UPDATE_USER_ALIAS:
                String newAlias = LPNulls.replaceNull(argValues[0]).toString();
                userName = LPNulls.replaceNull(argValues[1]).toString();
                if (userName.length() == 0) {
                    userName = instanceForActions.getToken().getUserName();
                }
                updFldsN = EnumIntTableFields.getTableFieldsFromString(TablesAppConfig.PERSON,
                        new String[]{TblsAppConfig.Person.ALIAS.getName()});
                actionDiagnoses = UserAndRolesViews.updateUserPersonFields(userName, updFldsN, new Object[]{newAlias});
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic()) && userName.length() > 0) {
                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.USERS.getTableName(), userName);
                    this.messageDynamicData = new Object[]{newAlias};
                }
                break;

            default:
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, null, ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND, null);
                return;
        }
        this.relatedObj = rObj;
        this.actionDiagnosesObj = actionDiagnoses;
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
        return null;
    }

    public InternalMessage getDiagnosticObj() {
        return this.actionDiagnosesObj;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }

}
