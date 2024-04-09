/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.definition;

import databases.Rdbms;
import databases.TblsData;
import modules.masterdata.analysis.ConfigAnalysisStructure;
import static functionaljavaa.inventory.DataInventoryRetain.*;
import functionaljavaa.modulesample.DataModuleSampleAnalysisResult;
import module.inspectionlot.rawmaterial.logic.DataInspectionLot;
import module.inspectionlot.rawmaterial.logic.DataInspectionLotDecision;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.samplestructure.DataSampleAnalysisResult;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.inspectionlot.rawmaterial.logic.DataBulk;
import trazit.enums.ActionsClass;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ApiMessageReturn;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassInspLotRMactions implements ActionsClass{

    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    private Object[] diagnostic = null;
    InternalMessage actionDiagnosesObj = null;
    private Boolean functionFound = false;
    private EnumIntEndpoints enumConstantByName;
    
    public ClassInspLotRMactions(HttpServletRequest request, InspLotRMEnums.InspLotRMAPIactionsEndpoints endPoint) {
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        DataInspectionLot insplot = new DataInspectionLot();
        DataInspectionLotDecision insplotDecision = new DataInspectionLotDecision();

        this.actionDiagnosesObj = null;
        this.enumConstantByName=endPoint;
        InternalMessage actionDiagnoses = null;
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        this.functionFound = true;
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
            this.diagnostic = ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.messageDynamicData = new Object[]{argValues[2].toString()};
            this.relatedObj = rObj;
            rObj.killInstance();
            return;
        }
        switch (endPoint) {
            case NEW_LOT:
                String lotName = argValues[0].toString();
                String materialName = argValues[1].toString();
                String template = argValues[2].toString();
                Integer templateVersion = (Integer) argValues[3];

                String q = argValues[4].toString();
                String qUomStr = argValues[5].toString();
                String nContStr = LPNulls.replaceNull(argValues[6]).toString();
                String numBulksStr = LPNulls.replaceNull(argValues[7]).toString();

                String fieldName = LPNulls.replaceNull(argValues[8]).toString();
                String fieldValue = LPNulls.replaceNull(argValues[9]).toString();
                String[] fieldNameArr = new String[]{};
                Object[] fieldValueArr = new Object[]{};
                if (fieldName.length() > 0) {
                    fieldNameArr = fieldName.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                if (q.length() > 0) {
                    fieldNameArr = LPArray.addValueToArray1D(fieldNameArr, TblsInspLotRMData.Lot.QUANTITY.getName());
                    fieldValueArr = LPArray.addValueToArray1D(fieldValueArr, Integer.valueOf(q));
                }
                if (qUomStr.length() > 0) {
                    fieldNameArr = LPArray.addValueToArray1D(fieldNameArr, TblsInspLotRMData.Lot.QUANTITY_UOM.getName());
                    fieldValueArr = LPArray.addValueToArray1D(fieldValueArr, qUomStr);
                }
                if (nContStr.length() > 0) {
                    fieldNameArr = LPArray.addValueToArray1D(fieldNameArr, TblsInspLotRMData.Lot.NUM_CONTAINERS.getName());
                    fieldValueArr = LPArray.addValueToArray1D(fieldValueArr, Integer.valueOf(nContStr));
                }
                Integer numBulks = null;
                if (numBulksStr.length() > 0) {
                    numBulks = Integer.valueOf(numBulksStr);
                    fieldNameArr = LPArray.addValueToArray1D(fieldNameArr, TblsInspLotRMData.Lot.NUM_BULKS.getName());
                    fieldValueArr = LPArray.addValueToArray1D(fieldValueArr, numBulks);
                }
                Integer numLotsToCreate = 1;
                if (fieldValueArr != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValueArr[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValueArr[1];
                    break;
                }
                actionDiagnoses = insplot.createLot(lotName, numBulks, materialName, template, templateVersion, fieldNameArr, fieldValueArr, numLotsToCreate);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.NEW_LOT, new Object[]{lotName, template, templateVersion, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{lotName};
                break;
            case LOT_ADD_ADHOC_BULKS:
                lotName = argValues[0].toString();
                String numAdhocBulksStr = argValues[1].toString();
                Integer numAdhocBulks = 1;
                if (numAdhocBulksStr.length() > 0) {
                    numAdhocBulks = Integer.valueOf(numAdhocBulksStr);
                }
                actionDiagnoses = DataBulk.addAdhocBulk(lotName, numAdhocBulks);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_BULK_ADJUST_QUANTITY, new Object[]{lotName, numAdhocBulks});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{numAdhocBulks, lotName};
                break;
            case LOT_BULK_ADJUST_QUANTITY:
                lotName = argValues[0].toString();
                Integer bulkId = Integer.valueOf(argValues[1].toString());
                String quantityStr = LPNulls.replaceNull(argValues[2]).toString();
                String quantityUomStr = LPNulls.replaceNull(argValues[3]).toString();
                fieldName = LPNulls.replaceNull(argValues[4]).toString();
                fieldValue = LPNulls.replaceNull(argValues[5]).toString();
                fieldNameArr = new String[]{};
                fieldValueArr = new Object[]{};
                if (fieldName.length() > 0) {
                    fieldNameArr = fieldName.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                actionDiagnoses = DataBulk.lotBulkAdjustQuantity(lotName, bulkId, BigDecimal.valueOf(Double.valueOf(quantityStr)), quantityUomStr, fieldNameArr, fieldValueArr);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_BULK_ADJUST_QUANTITY, new Object[]{lotName, quantityStr, fieldNameArr, fieldValueArr, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{quantityStr, lotName};
                break;
            case LOT_BULK_ADJUST_SAMPLE_QUANTITY:
                lotName = argValues[0].toString();
                bulkId = Integer.valueOf(argValues[1].toString());
                quantityStr = LPNulls.replaceNull(argValues[2]).toString();
                quantityUomStr = LPNulls.replaceNull(argValues[3]).toString();
                fieldName = LPNulls.replaceNull(argValues[4]).toString();
                fieldValue = LPNulls.replaceNull(argValues[5]).toString();
                fieldNameArr = new String[]{};
                fieldValueArr = new Object[]{};
                if (fieldName.length() > 0) {
                    fieldNameArr = fieldName.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                actionDiagnoses = DataBulk.lotBulkSampleQuantity(lotName, bulkId, BigDecimal.valueOf(Double.parseDouble(quantityStr)), quantityUomStr, fieldNameArr, fieldValueArr);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_BULK_ADJUST_SAMPLE_QUANTITY, new Object[]{lotName, quantityStr, fieldNameArr, fieldValueArr, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{quantityStr, lotName};
                break;
            case LOT_BULK_TAKE_DECISION:
                lotName = argValues[0].toString();
                bulkId = Integer.valueOf(argValues[1].toString());
                String decision = argValues[2].toString();
                fieldName = LPNulls.replaceNull(argValues[3]).toString();
                fieldValue = LPNulls.replaceNull(argValues[4]).toString();
                fieldNameArr = new String[]{};
                fieldValueArr = new Object[]{};
                if (fieldName.length() > 0) {
                    fieldNameArr = fieldName.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                actionDiagnoses = DataBulk.lotBulkTakeDecision(lotName, bulkId, decision, fieldNameArr, fieldValueArr);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    DataInspectionLot.lotQuantityReduce(lotName, bulkId, decision, fieldNameArr, fieldValueArr);
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_TAKE_USAGE_DECISION, new Object[]{lotName, decision, fieldNameArr, fieldValueArr, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                } else {
                    this.actionDiagnosesObj = actionDiagnoses;
                }
                this.messageDynamicData = new Object[]{lotName, decision};
                break;

            case LOT_ALL_BULKS_TAKE_DECISION:
                lotName = argValues[0].toString();
                decision = argValues[1].toString();
                fieldName = LPNulls.replaceNull(argValues[2]).toString();
                fieldValue = LPNulls.replaceNull(argValues[3]).toString();
                fieldNameArr = new String[]{};
                fieldValueArr = new Object[]{};
                if (fieldName.length() > 0) {
                    fieldNameArr = fieldName.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                actionDiagnoses = insplotDecision.lotAllBulksTakeDecision(insplot, lotName, decision, fieldNameArr, fieldValueArr);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_TAKE_USAGE_DECISION, new Object[]{lotName, decision, fieldNameArr, fieldValueArr, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                    this.messageDynamicData = new Object[]{lotName, decision};
                }else{
                    this.actionDiagnosesObj=actionDiagnoses;
                    this.messageDynamicData = actionDiagnoses.getMessageCodeVariables();
                }                
                break;
            case LOT_RETAIN_RECEPTION:
            case LOT_RETAIN_UNLOCK:
            case LOT_RETAIN_LOCK:
                lotName = argValues[0].toString();
                Integer retainId = (Integer) argValues[1];
                if ("LOT_RETAIN_RECEPTION".equalsIgnoreCase(endPoint.getName())) {
                    actionDiagnoses = retainReception(lotName, retainId);
                }
                if ("LOT_RETAIN_UNLOCK".equalsIgnoreCase(endPoint.getName())) {
                    actionDiagnoses = retainUnlock(lotName, retainId);
                }
                if ("LOT_RETAIN_LOCK".equalsIgnoreCase(endPoint.getName())) {
                    actionDiagnoses = retainLock(lotName, retainId);
                }
                if (actionDiagnoses!=null&&LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_RETAIN_LOCK, new Object[]{lotName, LPNulls.replaceNull(retainId), ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{lotName};
                break;
            case LOT_RETAIN_MOVEMENT:
                lotName = argValues[0].toString();
                retainId = (Integer) argValues[1];
                String newLocation = argValues[2].toString();
                Integer newLocationId = (Integer) argValues[3];
                if (newLocation != null) {
                    actionDiagnoses = retainMovement(lotName, retainId, newLocation);
                }
                if (newLocationId != null) {
                    actionDiagnoses = retainMovement(lotName, retainId, newLocationId);
                }
                if (actionDiagnoses!=null&&LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_RETAIN_MOVEMENT, new Object[]{lotName, LPNulls.replaceNull(retainId), ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{lotName};
                break;
            case LOT_RETAIN_EXTRACT:
                lotName = argValues[0].toString();
                retainId = (Integer) argValues[1];
                BigDecimal quantity = (BigDecimal) argValues[2];
                String quantityUom = argValues[3].toString();
                actionDiagnoses = retainExtract(lotName, retainId, quantity, quantityUom);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_RETAIN_EXTRACT, new Object[]{lotName, LPNulls.replaceNull(retainId), ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{lotName};
                break;
            case REVIEWSAMPLE:
                Integer sampleId = (Integer) argValues[0];
                EnumIntTableFields[] fieldsToRetrieveObj = TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableFields();
                Object[][] lotInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION,
                        fieldsToRetrieveObj,
                        new String[]{TblsInspLotRMData.Sample.SAMPLE_ID.getName()},
                        new Object[]{sampleId},
                        new String[]{TblsInspLotRMData.Sample.SAMPLE_ID.getName()});
                if (LPPlatform.LAB_FALSE.equals(lotInfo[0][0].toString())) {
                    this.actionDiagnosesObj=new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{sampleId, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())}, sampleId);
                }
                Integer sampleLotFldPosic = EnumIntTableFields.getFldPosicInArray(fieldsToRetrieveObj, TblsInspLotRMData.Sample.LOT_NAME.getName());
                if (sampleLotFldPosic == -1) {
                    this.actionDiagnosesObj=new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{sampleId, TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), LPPlatform.buildSchemaName(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())}, sampleId);
                }
                String sampleLot = lotInfo[0][sampleLotFldPosic].toString();
                DataModuleSampleAnalysisResult moduleSmpAnaRes = new DataModuleSampleAnalysisResult();
                DataSampleAnalysisResult smpAnaRes = new DataSampleAnalysisResult(moduleSmpAnaRes);
                this.actionDiagnosesObj = smpAnaRes.sampleAnalysisResultReview(sampleId, null, null);

                if (LPPlatform.LAB_TRUE.equals(actionDiagnoses.getDiagnostic())) {
                    DataInspectionLotDecision.setLotReadyForRevision(sampleLot);
                }
                rObj.addSimpleNode(LPPlatform.buildSchemaName(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                this.messageDynamicData = new Object[]{sampleId};
                if (this.actionDiagnosesObj != null && LPPlatform.LAB_TRUE.equalsIgnoreCase(this.actionDiagnosesObj.getDiagnostic())) {
                    this.actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_TRUE, endPoint, new Object[]{argValues[0], ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                }
                break;

            case LOT_TAKE_USAGE_DECISION:
                lotName = argValues[0].toString();
                decision = argValues[1].toString();
                fieldName = LPNulls.replaceNull(argValues[2]).toString();
                fieldValue = LPNulls.replaceNull(argValues[3]).toString();
                fieldNameArr = new String[]{};
                fieldValueArr = new Object[]{};
                if (fieldName.length() > 0) {
                    fieldNameArr = fieldName.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                actionDiagnoses = insplotDecision.lotTakeDecision(lotName, decision, fieldNameArr, fieldValueArr);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_TAKE_USAGE_DECISION, new Object[]{lotName, decision, fieldNameArr, fieldValueArr, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                }
                this.actionDiagnosesObj = actionDiagnoses;
                this.messageDynamicData = new Object[]{lotName, decision};
                break;
            case LOT_ADD_NOTANALYZED_PARAM:
                lotName = argValues[0].toString();
                String analysis = argValues[1].toString();
                String value = LPNulls.replaceNull(argValues[2]).toString();
                String reason = LPNulls.replaceNull(argValues[3]).toString();                
                actionDiagnoses = DataInspectionLot.addLotNotAnalyzedValue(lotName, analysis, value, reason);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_ADD_NOTANALYZED_PARAM, new Object[]{lotName, LPNulls.replaceNull(analysis), ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT_NOT_ANALYZED_RESULT.getTableName(), analysis);
                }
                this.messageDynamicData = new Object[]{lotName, analysis};                        
                break;
            case LOT_REMOVE_NOTANALYZED_PARAM:
                lotName = argValues[0].toString();
                analysis = argValues[1].toString();
                actionDiagnoses = DataInspectionLot.removedLotNotAnalyzedValue(lotName, analysis);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_REMOVE_NOTANALYZED_PARAM, new Object[]{lotName, LPNulls.replaceNull(analysis), ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT_NOT_ANALYZED_RESULT.getTableName(), analysis);
                }
                this.messageDynamicData = new Object[]{lotName, analysis};                        
                break;
            case LOT_CREATE_COA:
                lotName = argValues[0].toString();
                String htmlText = argValues[1].toString();
                fieldName = LPNulls.replaceNull(argValues[2]).toString();
                fieldValue = LPNulls.replaceNull(argValues[3]).toString();
                fieldNameArr = new String[]{};
                fieldValueArr = new Object[]{};
                if (fieldName.length() > 0) {
                    fieldNameArr = fieldName.split("\\|");
                    fieldValueArr = LPArray.convertStringWithDataTypeToObjectArray(fieldValue.split("\\|"));
                }
                actionDiagnoses = insplot.createLotCoa(lotName, htmlText);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_TRUE, InspLotRMEnums.InspLotRMAPIactionsEndpoints.LOT_CREATE_COA, new Object[]{lotName, ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance()});
                    rObj.addSimpleNode(ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance(), TblsInspLotRMData.TablesInspLotRMData.LOT.getTableName(), lotName);
                }
                this.messageDynamicData = new Object[]{lotName};                
                break;
            default:
                this.functionFound = false;
                break;
        }
        this.actionDiagnosesObj = actionDiagnoses;
        if (actionDiagnoses!=null&&LPPlatform.LAB_FALSE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
            this.diagnostic = ApiMessageReturn.trapMessage(actionDiagnoses.getDiagnostic(), actionDiagnoses.getMessageCodeObj(), actionDiagnoses.getMessageCodeVariables());
        }
        this.relatedObj = rObj;
        rObj.killInstance();
    }

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
        return actionDiagnosesObj;
    }

    /**
     * @return the functionFound
     */
    public Boolean getFunctionFound() {
        return functionFound;
    }
    @Override    public StringBuilder getRowArgsRows() {        return null;    }
    @Override    public EnumIntEndpoints getEndpointObj(){        return enumConstantByName;    }
}
