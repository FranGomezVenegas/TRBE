/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules.masterdata.spec;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import databases.TblsCnfg;
import databases.TblsData;
import functionaljavaa.materialspec.ConfigSpecStructure;
import trazit.session.ResponseMessages;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import modules.masterdata.spec.MasterDataSpecEnums.MasterDataSpecActionsEndpoints;
import trazit.enums.ActionsClass;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ClassSpec implements ActionsClass{

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

    public ClassSpec(HttpServletRequest request, HttpServletResponse response, MasterDataSpecActionsEndpoints endPoint) {
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        try {
            ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, false, false);
            ResponseMessages messages = procReqInstance.getMessages();
            String actionName = procReqInstance.getActionName();
            String language = procReqInstance.getLanguage();
            
            Object[] actionDiagnoses = null;
            this.diagnosticObj = null;
            Object[] dynamicDataObjects = new Object[]{};
            this.functionFound = true;
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
                this.diagnostic = (Object[]) argValues[1];
                this.messageDynamicData = new Object[]{argValues[2].toString()};
                return;
            }
            String sopName = "";
            Integer trainingId = null;
            switch (endPoint) {
                case SPEC_NEW:
                    ConfigSpecStructure cSpec = new ConfigSpecStructure();
                    String code = argValues[0].toString();
                    String anaFieldName = argValues[1].toString();
                    String  anaFieldValue = argValues[2].toString();
                    String[] anaFieldNameArr = new String[]{};
                    Object[] anaFieldValueArr = new Object[]{};
                    if (anaFieldName != null && anaFieldName.length() > 0) {
                        anaFieldNameArr = anaFieldName.split("\\|");
                    }
                    if (anaFieldValue != null && anaFieldValue.length() > 0) {
                        anaFieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(anaFieldValue.split("\\|"));
                    }
                    if (anaFieldValueArr != null && anaFieldValueArr.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(anaFieldValueArr[0].toString())) {
                        Object[] diagn = anaFieldValueArr;
                        this.diagnosticObj=new InternalMessage(diagn[0].toString(), diagn[diagn.length-1].toString(), null, null);
                    } else {
                        this.diagnosticObj = cSpec.specNew(code, 1, anaFieldNameArr, anaFieldValueArr, null, null);
                    }
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{anaFieldName, anaFieldValue, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{anaFieldName};
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS.getTableName(), this.diagnosticObj.getNewObjectId());
                    }
                    break;
                case UPDATE_SPEC_RULES:
                    cSpec = new ConfigSpecStructure();
                    code = argValues[0].toString();
                    Integer codeVersion = (Integer) argValues[1];
                    String methodName = argValues[2].toString();
                    String[] fldNames=new String[]{};
                    Object[] fldValues=new Object[]{};
                    this.diagnosticObj = cSpec.updateSpecRules(code, codeVersion, fldNames, fldValues);
                    
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{code, methodName, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{code};
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS.getTableName(), this.diagnosticObj.getNewObjectId());
                    }
                    break;


                case SPEC_LIMIT_NEW:
                    cSpec = new ConfigSpecStructure();
                    code = argValues[0].toString();
                    codeVersion = (Integer) argValues[1];
                    methodName = argValues[2].toString();
                    fldNames=new String[]{};
                    fldValues=new Object[]{};
                    int iFld=3;
                    if (LPNulls.replaceNull(argValues[iFld]).toString().length()>0){
                        fldNames = LPArray.addValueToArray1D(fldNames, TblsCnfg.AnalysisMethodParams.ANALYSIS.getName());
                        fldValues = LPArray.addValueToArray1D(fldValues, argValues[iFld].toString());                        
                    }
                    iFld++;
                    if (LPNulls.replaceNull(argValues[iFld]).toString().length()>0){
                        fldNames = LPArray.addValueToArray1D(fldNames, TblsCnfg.AnalysisMethodParams.PARAM_NAME.getName());
                        fldValues = LPArray.addValueToArray1D(fldValues, argValues[iFld].toString());                        
                    }
                    iFld++;
                    if (LPNulls.replaceNull(argValues[iFld]).toString().length()>0){
                        fldNames = LPArray.addValueToArray1D(fldNames, TblsCnfg.AnalysisMethodParams.PARAM_TYPE.getName());
                        fldValues = LPArray.addValueToArray1D(fldValues, argValues[iFld].toString());                        
                    }
                    iFld++;
                    Integer numReplicas=1;
                    if (LPNulls.replaceNull(argValues[iFld]).toString().length()>0){
                        numReplicas=Integer.valueOf(argValues[iFld].toString());
                    }
                    fldNames = LPArray.addValueToArray1D(fldNames, TblsCnfg.AnalysisMethodParams.NUM_REPLICAS.getName());
                    fldValues = LPArray.addValueToArray1D(fldValues, numReplicas);                        
                    iFld++;
                    if (LPNulls.replaceNull(argValues[iFld]).toString().length()>0){
                        fldNames = LPArray.addValueToArray1D(fldNames, TblsCnfg.AnalysisMethodParams.UOM.getName());
                        fldValues = LPArray.addValueToArray1D(fldValues, argValues[iFld].toString());                        
                    }
                    if (LPNulls.replaceNull(argValues[iFld]).toString().length()>0){
                        fldNames = LPArray.addValueToArray1D(fldNames, TblsCnfg.AnalysisMethodParams.UOM_CONVERSION_MODE.getName());
                        fldValues = LPArray.addValueToArray1D(fldValues, argValues[iFld].toString());                        
                    }
                    if (LPNulls.replaceNull(argValues[iFld]).toString().length()>0){
                        fldNames = LPArray.addValueToArray1D(fldNames, TblsCnfg.AnalysisMethodParams.CALC_LINKED.getName());
                        fldValues = LPArray.addValueToArray1D(fldValues, argValues[iFld].toString());                        
                    }
                    if (LPNulls.replaceNull(argValues[iFld]).toString().length()>0){
                        fldNames = LPArray.addValueToArray1D(fldNames, TblsCnfg.AnalysisMethodParams.LIST_ENTRY.getName());
                        fldValues = LPArray.addValueToArray1D(fldValues, argValues[iFld].toString());                        
                    }
                    this.diagnosticObj = cSpec.specLimitNew(code, codeVersion, fldNames, fldValues);
                    
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{code, methodName, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{code};
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS.getTableName(), this.diagnosticObj.getNewObjectId());
                    }
                    break;

                
                case SPEC_UPDATE:
                    cSpec = new ConfigSpecStructure();
                    code = argValues[0].toString();
                    codeVersion = (Integer) argValues[1];
                    anaFieldName = argValues[2].toString();
                    anaFieldValue = argValues[3].toString();
                    anaFieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(anaFieldValue.split("\\|"));
                    if (anaFieldValueArr != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(anaFieldValueArr[0].toString())) {
                        Object[] diagn = anaFieldValueArr;
                        this.diagnosticObj=new InternalMessage(diagn[0].toString(), diagn[diagn.length-1].toString(), null, null);
                    } else {
                        this.diagnosticObj = cSpec.specUpdate(code, codeVersion, anaFieldName.split("\\|"), anaFieldValueArr);
                    }
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{anaFieldName, anaFieldValue, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{anaFieldName};
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS.getTableName(), this.diagnosticObj.getNewObjectId());
                    }
                    break;  
                default:
                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getClass().getSimpleName()}, language, this.getClass().getSimpleName());
                    return;
            }
            if (actionDiagnoses != null && LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses[0].toString())) {
                actionDiagnoses = ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, endPoint, dynamicDataObjects);
            }

            if (actionDiagnoses != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses[0].toString())) {

            } else {
                rObj = RelatedObjects.getInstanceForActions();
                rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), sopName);
                rObj.killInstance();
            }
            this.diagnostic = actionDiagnoses;
            this.relatedObj = rObj;
            if (this.diagnosticObj!=null)
                this.messageDynamicData = this.diagnosticObj.getMessageCodeVariables();
            else
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

}
