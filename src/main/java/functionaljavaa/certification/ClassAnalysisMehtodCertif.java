/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.certification;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.labplanet.servicios.app.CertifyAnalysisMethodAPI;
import static com.labplanet.servicios.app.CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFUSER_READ_AND_UNDERSTOOD;
import static com.labplanet.servicios.app.CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFUSER_TRAINING_REQUIRED;
import static com.labplanet.servicios.app.CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFUSER_UNDERSTOOD_AND_SENDTOREVIEWER;
import static com.labplanet.servicios.app.CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_ASSIGN_METHOD_TO_USER;
import static com.labplanet.servicios.app.CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_COMPLETE_CERTIFIED_USER_METHOD;
import static com.labplanet.servicios.app.CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_COMPLETE_NOT_CERTIFIED_USER_METHOD;
import static com.labplanet.servicios.app.CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_REVOKE_USER_METHOD;
import static com.labplanet.servicios.app.CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_START_USER_METHOD;
import static com.labplanet.servicios.app.CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.USER_MARKIT_AS_COMPLETED;
import databases.TblsData;
import trazit.session.ResponseMessages;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import trazit.enums.ActionsClass;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassAnalysisMehtodCertif implements ActionsClass{

    /**
     * @return the messageDynamicData
     */
    public Object[] getMessageDynamicData() {
        return this.messageDynamicData;
    }

    /**
     * @return the rObj
     */
    public RelatedObjects getRelatedObj() {
        return this.relatedObj;
    }

    /**
     * @return the endpointExists
     */
    public Boolean getEndpointExists() {
        return this.endpointExists;
    }

    /**
     * @return the diagnostic
     */
    public Object[] getDiagnostic() {
        return this.diagnostic;
    }

    public InternalMessage getDiagnosticObj() {
        return this.diagnosticObj;
    }
    private InternalMessage diagnosticObj;
    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    private Object[] diagnostic = new Object[0];
    private Boolean functionFound = false;
    private EnumIntEndpoints enumConstantByName;
    public ClassAnalysisMehtodCertif(HttpServletRequest request, CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints endPoint) {
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        try {
            ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, null);
            ResponseMessages messages = procReqInstance.getMessages();
            String actionName = procReqInstance.getActionName();
            String language = procReqInstance.getLanguage();
            
            Object[] actionDiagnoses = null;
            this.diagnosticObj = null;
            Object[] dynamicDataObjects = new Object[]{};
            this.functionFound = true;
            this.enumConstantByName=endPoint;
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
                this.diagnostic = (Object[]) argValues[1];
                this.messageDynamicData = new Object[]{argValues[2].toString()};
                return;
            }
            String sopName = "";
            Integer trainingId = null;
            switch (endPoint) {
                case CERTIFY_ASSIGN_METHOD_TO_USER:
                    sopName = argValues[3].toString();
                    if (LPNulls.replaceNull(argValues[4]).toString().length()>0){
                        trainingId = (Integer) argValues[4];
                    }
                case CERTIFY_START_USER_METHOD:
                case CERTIFY_COMPLETE_CERTIFIED_USER_METHOD:
                case CERTIFY_COMPLETE_NOT_CERTIFIED_USER_METHOD:
                case CERTIFY_REVOKE_USER_METHOD:
                    String methodName = argValues[0].toString();
                    Integer methodVersion = (Integer) argValues[1];
                    String userName = argValues[2].toString();
                    if (actionName.equalsIgnoreCase(CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_ASSIGN_METHOD_TO_USER.getName())) {
                        diagnosticObj = AnalysisMethodCertif.newRecord(methodName, methodVersion, userName, sopName, trainingId);
                    }
                    if (actionName.equalsIgnoreCase(CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_START_USER_METHOD.getName())) {
                        diagnosticObj = AnalysisMethodCertif.startCertification(methodName, userName);
                    }
                    if (actionName.equalsIgnoreCase(CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_COMPLETE_CERTIFIED_USER_METHOD.getName())) {
                        diagnosticObj = AnalysisMethodCertif.completeCertificationCertified(methodName, userName);
                    }
                    if (actionName.equalsIgnoreCase(CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_COMPLETE_NOT_CERTIFIED_USER_METHOD.getName())) {
                        diagnosticObj = AnalysisMethodCertif.completeCertificationNotCertified(methodName, userName);
                    }
                    if (actionName.equalsIgnoreCase(CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_REVOKE_USER_METHOD.getName())) {
                        diagnosticObj = AnalysisMethodCertif.revokeCertification(methodName, userName);
                    }

                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{sopName, userName, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{methodName, userName, procReqInstance.getProcedureInstance()};
                    }
                    if (actionName.equalsIgnoreCase(CertifyAnalysisMethodAPI.CertifyAnalysisMethodAPIactionsEndpoints.CERTIFY_ASSIGN_METHOD_TO_USER.getName())) {
                        rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), diagnosticObj.getNewObjectId());
                    }

                    break;
                case CERTIFUSER_READ_AND_UNDERSTOOD:
                case USER_MARKIT_AS_COMPLETED:
                    methodName = argValues[0].toString();
                    userName = procReqInstance.getToken().getUserName();
                    diagnosticObj = AnalysisMethodCertif.userMarkItAsCompleted(methodName);
                    messageDynamicData = new Object[]{methodName, userName, procReqInstance.getProcedureInstance()};
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{sopName, userName, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{methodName, userName, procReqInstance.getProcedureInstance()};
                    }
                    break;
                case CERTIFUSER_UNDERSTOOD_AND_SENDTOREVIEWER:
                case CERTIFUSER_TRAINING_REQUIRED:
                    diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.NOT_IMPLEMENTED_YET, null);
                    break;
                default:
                    LPFrontEnd.servletReturnResponseError(request, null,LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getClass().getSimpleName()}, language, this.getClass().getSimpleName());
                    return;
            }
            if (actionDiagnoses != null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())) {
                actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, dynamicDataObjects);
            }

            if (actionDiagnoses != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses[0].toString())) {

            } else {
                rObj = RelatedObjects.getInstanceForActions();
                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), sopName);
//            JSONObject dataSampleJSONMsg = LPFrontEnd.responseJSONDiagnosticLPTrue(endPoint.getClass().getSimpleName(), endPoint, new Object[]{incId}, rObj.getRelatedObject());
                rObj.killInstance();
//            LPFrontEnd.servletReturnSuccess(request, response, dataSampleJSONMsg);
            }
            this.diagnostic = actionDiagnoses;
            this.relatedObj = rObj;
            this.messageDynamicData = dynamicDataObjects;
            rObj.killInstance();
        } catch (Exception e) {
            this.diagnostic = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.UNHANDLED_EXCEPTION, new Object[]{e.getMessage()});
            this.relatedObj = rObj;
            this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.UNHANDLED_EXCEPTION, new Object[]{endPoint.getName() + ", Error:" + e.getMessage()}, null);
            this.messageDynamicData = new Object[]{e.getMessage()};
            rObj.killInstance();

        }
    }
    @Override    public StringBuilder getRowArgsRows() {        return null;    }
    @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }
}
