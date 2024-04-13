/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules.masterdata.analysis;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import databases.TblsCnfg;
import databases.TblsData;
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
import modules.masterdata.analysis.MasterDataAnalysisEnums.MasterDataAnalysisActionsEndpoints;
import trazit.enums.ActionsClass;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassAnalysis implements ActionsClass{

    /**
     * @return the messageDynamicData
     */
    @Override
    public Object[] getMessageDynamicData() {
        return this.messageDynamicData;
    }

    /**
     * @return the rObj
     */
    @Override
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
        return null;
    }

    public InternalMessage getDiagnosticObj() {
        return this.diagnosticObj;
    }
    private InternalMessage diagnosticObj;
    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    private Boolean functionFound = false;
    private EnumIntEndpoints enumConstantByName;
    
    
    public ClassAnalysis(HttpServletRequest request, MasterDataAnalysisActionsEndpoints endPoint) {
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        try {
            ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, false, false);
            ResponseMessages messages = procReqInstance.getMessages();
            String actionName = procReqInstance.getActionName();
            String language = procReqInstance.getLanguage();
            
            this.diagnosticObj = null;
            Object[] dynamicDataObjects = new Object[]{};
            this.functionFound = true;
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
                this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
                this.messageDynamicData = new Object[]{argValues[2].toString()};
                this.relatedObj = rObj;
                rObj.killInstance();
                return;
            }
            String sopName = "";
            Integer trainingId = null;
            switch (endPoint) {
                case ANALYSIS_NEW:
                    String code = argValues[0].toString();
                    String anaFieldName = argValues[1].toString();
                    ConfigAnalysisStructure anaStr = new ConfigAnalysisStructure(code, null);
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
                        this.diagnosticObj = anaStr.analysisNew(code, 1, anaFieldNameArr, anaFieldValueArr, procReqInstance.getProcedureInstance());
                    }
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{anaFieldName, anaFieldValue, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{anaFieldName};
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS.getTableName(), this.diagnosticObj.getNewObjectId());
                    }
                    break;
                case ANALYSIS_DEACTIVATE:
                    code = argValues[0].toString();
                    Integer codeVersion = Integer.valueOf(argValues[1].toString());
                    anaStr = new ConfigAnalysisStructure(code, codeVersion);
/*                    String anaFieldName = argValues[1].toString();
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
                    } else {*/
                        this.diagnosticObj = anaStr.analysisDeactivate();
                    //}
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{code, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{code};
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS.getTableName(), code);
                    }
                    break;
                case ANALYSIS_REACTIVATE:
                    code = argValues[0].toString();
                    codeVersion = Integer.valueOf(argValues[1].toString());
                    anaStr = new ConfigAnalysisStructure(code, codeVersion);
/*                    String anaFieldName = argValues[1].toString();
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
                    } else {*/
                        this.diagnosticObj = anaStr.analysisReactivate();
                    //}
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{code, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{code};
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS.getTableName(), code);
                    }
                    break;
                case ANALYSIS_APPROVAL_FOR_USE:
                    code = argValues[0].toString();
                    codeVersion = Integer.valueOf(argValues[1].toString());
                    anaStr = new ConfigAnalysisStructure(code, codeVersion);
/*                    String anaFieldName = argValues[1].toString();
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
                    } else {*/
                        this.diagnosticObj = anaStr.analysisApproveForUse();
                    //}
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{code, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{code};
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS.getTableName(), code);
                    }
                    break;
                case ANALYSIS_ADD_METHOD:
                    code = argValues[0].toString();
                    codeVersion = Integer.valueOf(argValues[1].toString());
                    anaStr = new ConfigAnalysisStructure(code, codeVersion);
                    String methodName = argValues[2].toString();
                    String expiryIntervalInfo = argValues[3].toString();

                    this.diagnosticObj = anaStr.analysisAddMethod(code, codeVersion, methodName, expiryIntervalInfo);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{code, methodName, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{methodName, code};
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS.getTableName(), this.diagnosticObj.getNewObjectId());
                    }
                    break;
                case ANALYSIS_REMOVE_METHOD:
                    code = argValues[0].toString();
                    codeVersion = (Integer) argValues[1];
                    anaStr = new ConfigAnalysisStructure(code, codeVersion);
                    methodName = argValues[2].toString();
                    this.diagnosticObj = anaStr.analysisRemoveMethod(code, codeVersion, methodName);
                    
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{code, methodName, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{methodName, code};
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS.getTableName(), this.diagnosticObj.getNewObjectId());
                    }
                    break;
                case ANALYSIS_ADD_PARAM:
                    code = argValues[0].toString();
                    codeVersion = (Integer) argValues[1];
                    anaStr = new ConfigAnalysisStructure(code, codeVersion);
                    methodName = argValues[2].toString();
                    String[] fldNames=new String[]{};
                    Object[] fldValues=new Object[]{};
                    int iFld=3;
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
                    this.diagnosticObj = anaStr.analysisMethodParamsNew(code, codeVersion, methodName, fldNames, fldValues, false);
                    
/*                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{code, methodName, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{code};
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS.getTableName(), this.diagnosticObj.getNewObjectId());
                    }*/
                    break;

                
                case ANALYSIS_UPDATE:
                    code = argValues[0].toString();
                    codeVersion = (Integer) argValues[1];
                    anaStr = new ConfigAnalysisStructure(code, codeVersion);
                    anaFieldName = argValues[2].toString();
                    anaFieldValue = argValues[3].toString();
                    anaFieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(anaFieldValue.split("\\|"));
                    if (anaFieldValueArr != null && anaFieldValueArr.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(anaFieldValueArr[0].toString())) {
                        Object[] diagn = anaFieldValueArr;
                        this.diagnosticObj=new InternalMessage(diagn[0].toString(), diagn[diagn.length-1].toString(), null, null);
                    } else {
                        this.diagnosticObj = anaStr.analysisUpdate(code, codeVersion, anaFieldName.split("\\|"), anaFieldValueArr);
                    }
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.diagnosticObj.getDiagnostic())) {
                        messageDynamicData = new Object[]{anaFieldName, anaFieldValue, procReqInstance.getProcedureInstance()};
                    } else {
                        messageDynamicData = new Object[]{anaFieldName};
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ANALYSIS.getTableName(), this.diagnosticObj.getNewObjectId());
                    }
                    break;  
                default:
                    LPFrontEnd.servletReturnResponseError(request, null, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getClass().getSimpleName()}, language, this.getClass().getSimpleName());
                    return;
            }

            rObj = RelatedObjects.getInstanceForActions();
            rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(), sopName);
            rObj.killInstance();
            this.enumConstantByName=endPoint;
            this.relatedObj = rObj;
            if (this.diagnosticObj!=null)
                this.messageDynamicData = this.diagnosticObj.getMessageCodeVariables();
            else
                this.messageDynamicData = dynamicDataObjects;
            rObj.killInstance();
        } catch (Exception e) {
            this.relatedObj = rObj;
            this.diagnosticObj = new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.UNHANDLED_EXCEPTION, new Object[]{endPoint.getName() + ", Error:" + e.getMessage()}, null);
            this.messageDynamicData = new Object[]{e.getMessage()};
            rObj.killInstance();

        }
    }
    @Override    public StringBuilder getRowArgsRows() {        return null;    }
    @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }

    @Override    public void initializeEndpoint(String actionName) {        throw new UnsupportedOperationException("Not supported yet.");}
    @Override    public void createClassEnvMonAndHandleExceptions(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {        throw new UnsupportedOperationException("Not supported yet.");}

    @Override
    public HttpServletResponse getHttpResponse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

