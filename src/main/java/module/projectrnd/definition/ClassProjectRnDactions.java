/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.projectrnd.definition;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.ApiErrorTraping;
import module.methodvalidation.logic.DataMethValSample;
import module.monitoring.definition.TblsEnvMonitData;
import module.projectrnd.definition.ProjectsRnDEnums.ProjectRnDAPIactionsEndpoints;
import module.projectrnd.definition.TblsProjectRnDData.TablesProjectRnDData;
import module.projectrnd.logic.DataProjectRnD;
import modules.masterdata.analysis.ConfigAnalysisStructure;
import trazit.enums.ActionsClass;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.enums.EnumIntEndpoints;

public class ClassProjectRnDactions implements ActionsClass{

    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    //private Object[] diagnostic = new Object[0];
    InternalMessage actionDiagnosesObj = null;
    private Boolean functionFound = false;
    private Boolean isSuccess;
    private EnumIntEndpoints enumConstantByName;
    private HttpServletResponse response;
    public ClassProjectRnDactions(HttpServletRequest request, ProjectRnDAPIactionsEndpoints endPoint) {
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
        DataProjectRnD projectRnDObj = null;
        DataMethodDevSeq medDevSequenceObj = null;

        String projectName = "";
        String sequenceName = "";
        String analyticalParameter = "";
        if ( ("ADD_SAMPLE_TO_SEQUENCE".equalsIgnoreCase(endPoint.getName())) ||
             ("LOG_SAMPLE_TO_SEQUENCE".equalsIgnoreCase(endPoint.getName())) ){   
            sequenceName = argValues[0].toString();
            analyticalParameter = argValues[1].toString();
            projectName = argValues[2].toString();      
            medDevSequenceObj = new DataMethodDevSeq(sequenceName, analyticalParameter, projectName);
            if (Boolean.TRUE.equals(medDevSequenceObj.getHasError())) {
                this.actionDiagnosesObj = medDevSequenceObj.getErrorDetail();
                this.relatedObj = rObj;
                rObj.killInstance();
                medDevSequenceObj = null;
                return;
            }          
        }else{
            if (Boolean.FALSE.equals("PROJECTAUDIT_SET_AUDIT_ID_REVIEWED".equalsIgnoreCase(endPoint.getName()))) {
                projectName = argValues[0].toString();
            }
            if (Boolean.FALSE.equals("NEW_PROJECT".equalsIgnoreCase(endPoint.getName())) 
                && Boolean.FALSE.equals(endPoint.getName().toUpperCase().startsWith("CONFIG_"))
                && Boolean.FALSE.equals("PROJECT_NOTE_REMOVE".equalsIgnoreCase(endPoint.getName()))                 
                && Boolean.FALSE.equals("PROJECTAUDIT_SET_AUDIT_ID_REVIEWED".equalsIgnoreCase(endPoint.getName()))                
                ) {
                projectRnDObj = new DataProjectRnD(projectName);
                if (Boolean.TRUE.equals(projectRnDObj.getHasError())) {
                    this.actionDiagnosesObj = projectRnDObj.getErrorDetail();
                    this.relatedObj = rObj;
                    rObj.killInstance();
                    projectRnDObj = null;
                    return;
                }
            }
}
        this.enumConstantByName=endPoint;
        this.functionFound = true;
        switch (endPoint) {
            case NEW_PROJECT:
                String projectType = LPNulls.replaceNull(argValues[1]).toString();
                String purpose = LPNulls.replaceNull(argValues[2]).toString();                
                String responsible = LPNulls.replaceNull(argValues[3]).toString();
                String fldNamesStr = argValues[4].toString();
                String fldValuesStr = argValues[5].toString();
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
                if (projectType.length()>0){
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProjectRnDData.Project.TYPE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, projectType);                    
                }
                if (purpose.length()>0){
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProjectRnDData.Project.PURPOSE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, purpose);                    
                }
                if (responsible.length()>0){
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProjectRnDData.Project.RESPONSIBLE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, responsible);                    
                }
                actionDiagnoses = DataProjectRnD.createNewProject(projectName, fieldNames, fieldValues);
                break;
            case NEW_ANALYTICAL_SEQUENCE:
                String analyticalSequenceName = LPNulls.replaceNull(argValues[1]).toString();
                String analyticalPurpose = LPNulls.replaceNull(argValues[2]).toString();
                analyticalParameter = LPNulls.replaceNull(argValues[3]).toString();
                purpose = LPNulls.replaceNull(argValues[4]).toString();                
                responsible = LPNulls.replaceNull(argValues[5]).toString();
                String numSamplesStr = LPNulls.replaceNull(argValues[6]).toString();
                fldNamesStr = argValues[7].toString();
                fldValuesStr = argValues[8].toString();
                //? 1 : Integer.valueOf(LPNulls.replaceNull(argValues[15].toString()));
                fieldNames = null;
                fieldValues = null;
                if (fldValuesStr != null && fldValuesStr.length() > 0) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fldValuesStr.split("\\|"));
                    fieldNames = fldNamesStr.split("\\|");
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];                
                }
                if (analyticalPurpose.length()>0){
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProjectRnDData.MethodDevelopmentSequence.ANALYTICAL_PURPOSE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, analyticalPurpose);                    
                }
                if (analyticalParameter.length()>0){
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProjectRnDData.MethodDevelopmentSequence.ANALYTICAL_PARAMETER.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, analyticalParameter);                    
                }

                if (purpose.length()>0){
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProjectRnDData.MethodDevelopmentSequence.PURPOSE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, purpose);                    
                }
                if (responsible.length()>0){
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsProjectRnDData.MethodDevelopmentSequence.RESPONSIBLE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, responsible);                    
                }
                actionDiagnoses = DataProjectRnD.createNewAnalyticalSequence(analyticalSequenceName, analyticalParameter, projectName, fieldNames, fieldValues, numSamplesStr.length()==0?null:Integer.valueOf(numSamplesStr));
                break;
            case ADD_SAMPLE_TO_SEQUENCE:
                String numSamplesToLogStr=LPNulls.replaceNull(argValues[3]).toString();
                fieldNames = null;
                fieldValues = null;
                if (LPNulls.replaceNull(argValues[4]).toString().length() > 0) {
                    fieldNames = argValues[6].toString().split("\\|");                        
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(argValues[5].toString().split("\\|"),
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
                    actionDiagnoses = MethSmp.logAnalyticalParameterSamplelogParameterSample(projectName, sequenceName, analyticalParameter, fieldNames, fieldValues, 
                        numSamplesToLogStr.length()==0?1:Integer.valueOf(numSamplesToLogStr));
                }
                break;
            case LOG_SAMPLE_TO_SEQUENCE:
                String analysisList=LPNulls.replaceNull(argValues[3]).toString();
                numSamplesToLogStr=LPNulls.replaceNull(argValues[4]).toString();
                fieldNames = null;
                fieldValues = null;
                if (LPNulls.replaceNull(argValues[5]).toString().length() > 0) {
                    fieldNames = argValues[7].toString().split("\\|");                        
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(argValues[6].toString().split("\\|"),
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
                    actionDiagnoses = MethSmp.logAnalyticalParameterSamplelogParameterSample(projectName, sequenceName, analyticalParameter, fieldNames, fieldValues, 
                        numSamplesToLogStr.length()==0?1:Integer.valueOf(numSamplesToLogStr));
                }       
                break;
            case PROJECT_NOTE_REMOVE:
                Integer noteId =Integer.valueOf(argValues[0].toString());
                Object[] exist = Rdbms.existsRecord(TblsProjectRnDData.TablesProjectRnDData.PROJECT_NOTES, 
                        new String[]{TblsProjectRnDData.ProjectNotes.ID.getName()}, new Object[]{noteId}, procReqSession.getProcedureInstance());
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(exist[0].toString())) {
                    actionDiagnoses=new InternalMessage(LPPlatform.LAB_FALSE, ProjectsRnDEnums.ProjectRnDErrorTrapping.NOTE_NOT_FOUND, new Object[]{noteId}, null);
                }
                SqlWhere whereObj=new SqlWhere();
                whereObj.addConstraint(TblsProjectRnDData.ProjectNotes.ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{noteId}, null);
                RdbmsObject removeRecordInTable = Rdbms.removeRecordInTable(TblsProjectRnDData.TablesProjectRnDData.PROJECT_NOTES, whereObj, procReqSession.getProcedureInstance());
                actionDiagnoses=new InternalMessage(removeRecordInTable.getRunSuccess()?LPPlatform.LAB_TRUE:LPPlatform.LAB_FALSE, 
                        removeRecordInTable.getErrorMessageCode(), removeRecordInTable.getErrorMessageVariables(), null);
                break;
            case UPDATE_TEST_ATTRIBUTE:
                String testIdStr = LPNulls.replaceNull(argValues[1]).toString();
                String attrName = argValues[2].toString();
                String attrValue = argValues[3].toString();                
                actionDiagnoses = DataProjectRnD.updateTestAttribute(Integer.valueOf(testIdStr), attrName, attrValue);
                break;
                
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
                projectRnDObj = null;
                return;
        }

        this.actionDiagnosesObj = actionDiagnoses;
        this.messageDynamicData=actionDiagnoses.getMessageCodeVariables();
        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesProjectRnDData.PROJECT.getTableName(), projectName);
        this.relatedObj = rObj;
        projectRnDObj = null;

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
