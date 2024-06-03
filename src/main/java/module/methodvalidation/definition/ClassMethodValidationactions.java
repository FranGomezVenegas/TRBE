/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.methodvalidation.definition;

import com.labplanet.servicios.app.GlobalAPIsParams;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.Rdbms;
import databases.TblsData;
import functionaljavaa.modulesample.DataModuleSampleAnalysisResult;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.samplestructure.DataSample;
import functionaljavaa.samplestructure.DataSampleAnalysisResult;
import functionaljavaa.samplestructure.DataSampleStructureEnums;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.ApiErrorTraping;
import module.methodvalidation.definition.MethodValidationEnums.MethodValidationAPIactionsEndpoints;
import static module.methodvalidation.definition.MethodValidationEnums.MethodValidationAPIactionsEndpoints.NEW_PARAMETER;
import module.methodvalidation.logic.DataMethValSample;
import module.methodvalidation.logic.DataMethodValidation;
import module.methodvalidation.logic.DataParameterSampleAnalysis;
import module.monitoring.definition.TblsEnvMonitData;
import module.projectrnd.definition.TblsProjectRnDData;
import module.projectrnd.definition.TblsProjectRnDData.TablesProjectRnDData;
import modules.masterdata.analysis.ConfigAnalysisStructure;
import trazit.enums.ActionsClass;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.enums.EnumIntEndpoints;

public class ClassMethodValidationactions implements ActionsClass{

    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    //private Object[] diagnostic = new Object[0];
    InternalMessage actionDiagnosesObj = null;
    private Boolean functionFound = false;
    private Boolean isSuccess;
    private EnumIntEndpoints enumConstantByName;
    private HttpServletResponse response;
    public ClassMethodValidationactions(HttpServletRequest request, MethodValidationAPIactionsEndpoints endPoint) {
        this.functionFound = true;

        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        InternalMessage actionDiagnoses = null;
/*        Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
            this.diagnostic=areMandatoryParamsInResponse;
            this.actionDiagnosesObj = new InternalMessage(areMandatoryParamsInResponse[0].toString(),
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()});
            //LPFrontEnd.servletReturnResponseError(request, response,
            //        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, "en", LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;
        }
*/
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
            this.actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.messageDynamicData = new Object[]{argValues[2].toString()};
            this.relatedObj = rObj;
            rObj.killInstance();
            return;
        }        
        DataMethodValidation methValidObj = null;
        String parameterName = "";
        String analyticalParameter ="";
        String projectName ="";
        if (Boolean.FALSE.equals("PARAMETERAUDIT_SET_AUDIT_ID_REVIEWED".equalsIgnoreCase(endPoint.getName()))
/*            && Boolean.FALSE.equals("ENTERRESULT".equalsIgnoreCase(endPoint.getName()))                
            && Boolean.FALSE.equals("REENTERRESULT".equalsIgnoreCase(endPoint.getName()))                
            && Boolean.FALSE.equals("ENTERRESULT_PARSING".equalsIgnoreCase(endPoint.getName()))                */
                ) {
            parameterName = argValues[0].toString();
            analyticalParameter = argValues[1].toString();
            projectName = argValues[2].toString();
        }
        if (Boolean.FALSE.equals("NEW_PARAMETER".equalsIgnoreCase(endPoint.getName()))
            && Boolean.FALSE.equals("LOGSAMPLE".equalsIgnoreCase(endPoint.getName()))

            && Boolean.FALSE.equals(endPoint.getName().toUpperCase().startsWith("CONFIG_"))
            && Boolean.FALSE.equals("PARAMETERAUDIT_SET_AUDIT_ID_REVIEWED".equalsIgnoreCase(endPoint.getName()))                
            ) {
            methValidObj = new DataMethodValidation(parameterName, analyticalParameter, projectName);
            if (Boolean.TRUE.equals(methValidObj.getHasError())) {
                this.actionDiagnosesObj = methValidObj.getErrorDetail();
                this.relatedObj = rObj;
                rObj.killInstance();
                methValidObj = null;
                return;
            }
        }
        DataParameterSampleAnalysis methValParamSmpAna = new DataParameterSampleAnalysis();
        DataModuleSampleAnalysisResult moduleSmpAnaRes = new DataModuleSampleAnalysisResult();
        DataSample smp = new DataSample(methValParamSmpAna);
        DataSampleAnalysisResult smpAnaRes = new DataSampleAnalysisResult(moduleSmpAnaRes);
        Integer incubationStage = null;
        Integer sampleId = null;
        
        
        this.enumConstantByName=endPoint;
        this.functionFound = true;
        switch (endPoint) {
            case NEW_PARAMETER:
                projectName = LPNulls.replaceNull(argValues[2]).toString();
                String purpose = LPNulls.replaceNull(argValues[3]).toString();                
                String responsible = LPNulls.replaceNull(argValues[4]).toString();
                String fldNamesStr = argValues[5].toString();
                String fldValuesStr = argValues[6].toString();
                //? 1 : Integer.valueOf(LPNulls.replaceNull(argValues[15].toString()));
                String[] fieldNames = null;
                Object[] fieldValues = null;
                if (fldValuesStr != null && fldValuesStr.length() > 0) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                    fieldNames = fldNamesStr.split("\\|");
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];                
                }
                if (purpose.length()>0){
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProjectRnDData.Project.PURPOSE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, purpose);                    
                }
                if (responsible.length()>0){
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProjectRnDData.Project.RESPONSIBLE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, responsible);                    
                }
                actionDiagnoses = methValidObj.createNewParameter(parameterName, analyticalParameter, projectName, fieldNames, fieldValues);
                break;
            case ADD_SAMPLE_TO_PARAMETER:
                fieldNames = null;
                fieldValues = null;
                if (LPNulls.replaceNull(argValues[1]).toString().length() > 0) {
                    fieldNames = argValues[2].toString().split("\\|");                        
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(argValues[2].toString().split("\\|"),
                    TblsEnvMonitData.TablesEnvMonitData.SAMPLE, fieldNames);
                    if (fieldValues != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                        actionDiagnoses = (InternalMessage) fieldValues[1];
                    }
                }
                if (fieldNames != null) {
                    Object[] checkTwoArraysSameLength = LPArray.checkTwoArraysSameLength(fieldNames, fieldValues);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkTwoArraysSameLength[0].toString())) {
                        actionDiagnoses=new InternalMessage(LPPlatform.LAB_FALSE, checkTwoArraysSameLength[checkTwoArraysSameLength.length - 1].toString(), null, null);
                    }
                }
                if (actionDiagnoses == null) {
                    DataMethValSample MethSmp= new DataMethValSample();
                    actionDiagnoses = MethSmp.logParameterSample(parameterName, fieldNames, fieldValues, 
                        (LPNulls.replaceNull(argValues[3]).toString().length() > 0?Integer.valueOf(argValues[3].toString()):null));
                }
                break;
            case REENTERRESULT: 
            case ENTERRESULT:
                Integer resultId = (Integer) argValues[3];
                String rawValueResult = argValues[4].toString();
                Object[][] resultData = Rdbms.getRecordFieldsByFilter(procReqSession.getProcedureInstance(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                        new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()}, new Object[]{resultId},
                        new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.ANALYSIS.getName(),
                            TblsData.SampleAnalysisResult.METHOD_NAME.getName(), TblsData.SampleAnalysisResult.METHOD_VERSION.getName(), TblsData.SampleAnalysisResult.PARAM_NAME.getName(),
                            TblsData.SampleAnalysisResult.STATUS.getName(), TblsData.SampleAnalysisResult.RAW_VALUE.getName(), TblsData.SampleAnalysisResult.UOM.getName(),
                            TblsData.SampleAnalysisResult.UOM_CONVERSION_MODE.getName()});
                if (LPPlatform.LAB_FALSE.equals(resultData[0][0].toString())) {
                    actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, DataSampleStructureEnums.DataSampleAnalysisResultErrorTrapping.NOT_FOUND, new Object[]{resultId.toString(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())});
                } else {
                    String currRawValue = (String) resultData[0][7];
                    if (currRawValue != null && currRawValue.length() > 0 && SampleAPIParams.SampleAPIactionsEndpoints.ENTERRESULT.getName().equalsIgnoreCase(endPoint.getName())) {
                        procReqSession.killIt();
                        request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, SampleAPIParams.SampleAPIactionsEndpoints.REENTERRESULT.getName());
                        procReqSession = ProcedureRequestSession.getInstanceForActions(request, null, procReqSession.getIsForTesting());
                        if (Boolean.TRUE.equals(procReqSession.getHasErrors())) {
                            procReqSession.killIt();
                            actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, 
                                    procReqSession.getErrorMessageCodeObj(), new Object[]{resultId.toString(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())});
                            break;
                        }
                    }
                }
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultEntry(resultId, rawValueResult, smp);                
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), resultId);
                this.messageDynamicData = new Object[]{resultId};
                break;
            case ENTERRESULT_PARSING:
                resultId = (Integer) argValues[3];
                byte[] fileInBytes = (byte[]) argValues[4];
                actionDiagnosesObj = smpAnaRes.sampleAnalysisResultEntryByParsing(resultId, fileInBytes, smp);
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{resultId};
                break;

/*                ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(null, null, null);
                String procInstanceName=instanceForActions.getProcedureInstance();                
                ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();                
                Object[] dynamicDataObjects = actionDiagnoses.getMessageCodeVariables();
                rObj.addSimpleNode(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.TablesEnvMonitData.SAMPLE.getTableName(), actionDiagnosesObj.getNewObjectId());
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    Integer sampleId = Integer.valueOf(actionDiagnoses.getNewObjectId().toString());
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{argValues[0], procInstanceName}, sampleId);
                    messages.addMainForSuccess(endPoint, new Object[]{sampleId, procInstanceName, (String) argValues[5]});
                }*/                
                
/*            case FORMULA_ADD_INGREDIENT:
                String ingredient = argValues[1].toString();
                String quantity = argValues[2].toString();
                String quantityUom = argValues[3].toString();
                fldNamesStr = argValues[4].toString();
                fldValuesStr = argValues[5].toString();
                fieldNames = null;
                fieldValues = null;
                if (fldValuesStr != null && fldValuesStr.length() > 0) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                    fieldNames = fldNamesStr.split("\\|");
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else {
                    if (formulaObj == null) {
                        return;
                    }
                    if (quantity.length()>0){
                        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProjectRnDData.FormulaIngredients.QUANTITY.getName());
                        fieldValues = LPArray.addValueToArray1D(fieldValues, quantity);                    
                    }
                    if (quantityUom.length()>0){
                        fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProjectRnDData.FormulaIngredients.QUANTITY_UOM.getName());
                        fieldValues = LPArray.addValueToArray1D(fieldValues, Integer.valueOf(quantityUom));
                    }                   
                    actionDiagnoses = formulaObj.addFormulaIngredient(ingredient, fieldNames, fieldValues);
                }
                break;
            case FORMULA_UPDATE_INGREDIENT:
                fldNamesStr = argValues[3].toString();
                fldValuesStr = argValues[4].toString();
                fieldNames = null;
                fieldValues = null;
                if (fldValuesStr != null && fldValuesStr.length() > 0) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                    fieldNames = fldNamesStr.split("\\|");
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else {
                    if (formulaObj == null) {
                        return;
                    }
                    actionDiagnoses = formulaObj.updateFormula(fieldNames, fieldValues);
                }
                break;
            case FORMULA_REMOVE_INGREDIENT:
                ingredient = argValues[1].toString();
                actionDiagnoses = formulaObj.removeFormulaIngredient(ingredient);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), formulaName);
                }
                break;
*/                
/*            case RETIRE_LOT:
                fldNamesStr = argValues[3].toString();
                fldValuesStr = argValues[4].toString();
                fieldNames = null;
                fieldValues = null;
                if (fldValuesStr != null && fldValuesStr.length() > 0) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                    fieldNames = fldNamesStr.split("\\|");
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else {
                    if (invLot == null) {
                        return;
                    }
                    actionDiagnoses = invLot.retireInventoryLot(fieldNames, fieldValues);
                }
                break;
            case UNRETIRE_LOT:
                fldNamesStr = argValues[3].toString();
                fldValuesStr = argValues[4].toString();
                fieldNames = null;
                fieldValues = null;
                if (fldValuesStr != null && fldValuesStr.length() > 0) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                    fieldNames = fldNamesStr.split("\\|");
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else {
                    if (invLot == null) {
                        return;
                    }
                    actionDiagnoses = invLot.closeFormula(fieldNames, fieldValues);
                }
                break;
            case CONSUME_INV_LOT_QUANTITY:
                volume = null;
                if (argValues[3] != null && argValues[3].toString().length() > 0) {
                    volume = BigDecimal.valueOf(Double.valueOf(argValues[3].toString()));
                }
                volumeUom = argValues[4].toString();
                if (invLot == null) {
                    return;
                }
                actionDiagnoses = invLot.consumeInvLotVolume(volume, volumeUom);
                break;
            case ADD_INV_LOT_QUANTITY:
                volume = null;
                if (argValues[3] != null && argValues[3].toString().length() > 0) {
                    volume = BigDecimal.valueOf(Double.valueOf(argValues[3].toString()));
                }
                volumeUom = argValues[4].toString();
                if (invLot == null) {
                    return;
                }
                actionDiagnoses = invLot.addInvLotVolume(volume, volumeUom);
                break;
            case ADJUST_INV_LOT_QUANTITY:
                volume = null;
                if (argValues[3] != null && argValues[3].toString().length() > 0) {
                    volume = BigDecimal.valueOf(Double.valueOf(argValues[3].toString()));
                }
                volumeUom = argValues[4].toString();
                if (invLot == null) {
                    return;
                }
                actionDiagnoses = invLot.adjustInvLotVolume(volume, volumeUom);
                break;
            case COMPLETE_QUALIFICATION:
                category = argValues[1].toString();
                reference = argValues[2].toString();
                String decision = argValues[3].toString();
                String turnAvailable = argValues[4].toString();
                if (invLot == null) {
                    return;
                }
                actionDiagnoses = invLot.completeQualification(decision, category, reference, Boolean.valueOf(turnAvailable));
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), referenceName);
                }
                break;
            case REOPEN_QUALIFICATION:
                if (invLot == null) {
                    return;
                }
                actionDiagnoses = invLot.removeFormulaIngredient();
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), referenceName);
                }
                break;
            case ENTER_EVENT_RESULT:
                category = argValues[1].toString();
                reference = argValues[2].toString();
                Integer lotQualifId = (Integer) argValues[3];
                String variableName = argValues[4].toString();
                String newValue = argValues[5].toString();
                actionDiagnoses = objectVariableSetValue(referenceName, category, reference, lotQualifId, variableName, newValue);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), referenceName);
                }
                break;

            case REENTER_EVENT_RESULT:
                category = argValues[1].toString();
                reference = argValues[2].toString();
                lotQualifId = (Integer) argValues[3];
                variableName = argValues[4].toString();
                newValue = argValues[5].toString();
                actionDiagnoses = objectVariableChangeValue(referenceName, category, reference, lotQualifId, variableName, newValue);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), referenceName);
                }
                break;
            case LOTAUDIT_SET_AUDIT_ID_REVIEWED:
                referenceName = LPNulls.replaceNull(argValues[0]).toString();
                Integer auditId = Integer.valueOf(LPNulls.replaceNull(argValues[1]).toString());
                Object[][] auditInfo = QueryUtilitiesEnums.getTableData(TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT,
                        EnumIntTableFields.getTableFieldsFromString(TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT, new String[]{TblsInvTrackingDataAudit.Lot.LOT_NAME.getName()}),
                        new String[]{TblsInvTrackingDataAudit.Lot.AUDIT_ID.getName()}, new Object[]{auditId},
                        new String[]{TblsInvTrackingDataAudit.Lot.AUDIT_ID.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditInfo[0][0].toString())) {
                    actionDiagnoses = new InternalMessage(auditInfo[0][0].toString(), SampleAudit.SampleAuditErrorTrapping.AUDIT_RECORD_NOT_FOUND, new Object[]{auditId});
                    referenceName = null;
                } else {
                    actionDiagnoses = invTrackingAuditSetAuditRecordAsReviewed(auditId, ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getPersonName());
                    this.messageDynamicData = new Object[]{auditId, referenceName};
                    break;
                }
                break;
            case CONFIG_ADD_REFERENCE: 
                String[] tblFields = new String[]{TblsInvTrackingConfig.Reference.NAME.getName(), TblsInvTrackingConfig.Reference.CATEGORY.getName(), TblsInvTrackingConfig.Reference.LOT_REQUIRES_QUALIF.getName(),
                    TblsInvTrackingConfig.Reference.MIN_STOCK.getName(), TblsInvTrackingConfig.Reference.MIN_STOCK_UOM.getName(), TblsInvTrackingConfig.Reference.ALLOWED_UOMS.getName(),
                    TblsInvTrackingConfig.Reference.MIN_STOCK_TYPE.getName(), TblsInvTrackingConfig.Reference.REQUIRES_AVAILABLES_FOR_USE.getName(), TblsInvTrackingConfig.Reference.MIN_AVAILABLES_FOR_USE.getName(),
                    TblsInvTrackingConfig.Reference.MIN_AVAILABLES_FOR_USE_TYPE.getName(), TblsInvTrackingConfig.Reference.ALLOW_OPENING_SOME_AT_A_TIME.getName(), TblsInvTrackingConfig.Reference.QUALIF_VARIABLES_SET.getName()};

                JSONObject jsonObj = new JSONObject();
                jsonObj.put("parsing_type", "SIMPLE_TABLE");
                jsonObj.put("object_type", TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableName());
                JSONArray jArr = new JSONArray();
                JSONObject jsonValuesObj = new JSONObject();
                Object[] checkTwoArraysSameLength=LPArray.checkTwoArraysSameLength(tblFields, argValues);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(checkTwoArraysSameLength[0].toString())) {
                    actionDiagnoses=new InternalMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ARRAYS_DIFFERENT_SIZE, new Object[]{checkTwoArraysSameLength[checkTwoArraysSameLength.length - 1].toString()}, null);
                }else{
                    for (int i = 0; i < argValues.length; i++) {
                        if (LPNulls.replaceNull(argValues[i]).toString().length() > 0) {
                            jsonValuesObj.put(tblFields[i], argValues[i]);
                        }
                    }
                    jArr.add(jsonValuesObj);
                    jsonObj.put("values", jArr);
                    ClassMasterData clss = new ClassMasterData(procReqSession.getProcedureInstance(), TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableName(), jsonObj.toJSONString(), TrazitModules.STOCKS.toString());
                    actionDiagnoses = clss.getDiagnostic();
                    this.messageDynamicData = new Object[]{argValues[0].toString()};
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(clss.getDiagnostic().getDiagnostic())) {
                        this.messageDynamicData = clss.getDiagnostic().getMessageCodeVariables();
                    }else{
                        AppInventoryLotAudit.inventoryLotConfigAuditAdd(InvTrackingEnums.AppConfigInventoryTrackingAuditEvents.REFERENCE_CREATED, TblsInvTrackingConfigAudit.TablesInvTrackingConfigAudit.REFERENCE, reference,
                            category, tblFields, argValues);

                    }
                }
                break;
            case CONFIG_UPDATE_REFERENCE:
                referenceName = argValues[0].toString();
                category = argValues[1].toString();
                String fieldName = argValues[2].toString();
                String fieldValue = argValues[3].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldValue != null && fieldValue.length() > 0) {
                    if (fieldName != null) {
                        fieldNames = fieldName.split("\\|");
                    }
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                }
                actionDiagnoses=null;
                if (actionDiagnoses==null){
                    if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                        actionDiagnoses = (InternalMessage) fieldValues[1];
                    } else {
                        actionDiagnoses = ConfigInvTracking.configUpdateReference(referenceName, category, fieldNames, fieldValues);
                    }                
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), referenceName);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT_QUALIFICATION.getTableName(), actionDiagnoses.getNewObjectId());
                }
                break;


            case ADD_ATTACHMENT:
                //referenceName = argValues[0].toString();
                lotQualifId = LPNulls.replaceNull(argValues[3]).toString().length() > 0 ? (Integer) argValues[3] : null;
                String attachUrl = argValues[4].toString();
                String briefSummary = argValues[5].toString();
                if (referenceName != null) {
                    actionDiagnoses = invLot.addAttachment(lotQualifId, attachUrl, briefSummary);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), referenceName);
                        if (lotQualifId != null) {
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT_QUALIFICATION.getTableName(), lotQualifId);
                        }
                    }
                }
                break;
            case REMOVE_ATTACHMENT:
                //referenceName = argValues[0].toString();
                lotQualifId = LPNulls.replaceNull(argValues[3]).toString().length() > 0 ? (Integer) argValues[3] : null;
                Integer attachmentId = LPNulls.replaceNull(argValues[4]).toString().length() > 0 ? (Integer) argValues[4] : null;
                if (referenceName != null) {
                    actionDiagnoses = invLot.removeAttachment(lotQualifId, attachmentId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), referenceName);
                        if (lotQualifId != null) {
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT_QUALIFICATION.getTableName(), lotQualifId);
                        }
                    }
                }
                break;
            case REACTIVATE_ATTACHMENT:
                referenceName = argValues[0].toString();
                lotQualifId = LPNulls.replaceNull(argValues[1]).toString().length() > 0 ? (Integer) argValues[1] : null;
                attachmentId = LPNulls.replaceNull(argValues[2]).toString().length() > 0 ? (Integer) argValues[2] : null;
                if (referenceName != null) {
                    actionDiagnoses = invLot.reactivateAttachment(lotQualifId, attachmentId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT.getTableName(), referenceName);
                        if (lotQualifId != null) {
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInvTrackingData.LOT_QUALIFICATION.getTableName(), lotQualifId);
                        }
                    }
                }
                break;
*/                
            default:
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, null, ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND, null);
                methValidObj = null;
                return;
        }

        this.actionDiagnosesObj = actionDiagnoses;

        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesProjectRnDData.PROJECT.getTableName(), parameterName);
        this.relatedObj = rObj;
        methValidObj = null;

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
    @Override    public StringBuilder getRowArgsRows() {        return null;    }
    @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }
    @Override    public void initializeEndpoint(String actionName) {        throw new UnsupportedOperationException("Not supported yet.");}
    @Override    public void createClassEnvMonAndHandleExceptions(HttpServletRequest request, String actionName, Object[][] testingContent, Integer iLines, Integer table1NumArgs, Integer auditReasonPosic) {        throw new UnsupportedOperationException("Not supported yet.");}

    @Override
    public HttpServletResponse getHttpResponse() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
