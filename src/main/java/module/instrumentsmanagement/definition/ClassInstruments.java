/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.definition;

import modules.masterdata.analysis.ConfigAnalysisStructure;
import module.instrumentsmanagement.definition.TblsInstrumentsData.TablesInstrumentsData;
import functionaljavaa.audit.SampleAudit;
import module.instrumentsmanagement.logic.DataInstruments;
import static module.instrumentsmanagement.logic.DataInstrumentsEvents.instrumentAuditSetAuditRecordAsReviewed;
import static module.instrumentsmanagement.logic.DataInstrumentsEvents.objectVariableChangeValue;
import static module.instrumentsmanagement.logic.DataInstrumentsEvents.objectVariableSetValue;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrumentsAPIactionsEndpoints;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import functionaljavaa.user.UserAndRolesViews;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import static lbplanet.utilities.LPDate.isIntervalTypeOneRecognized;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPMath;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrEventsErrorTrapping;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrumentsErrorTrapping;
import module.instrumentsmanagement.logic.ConfigInstrumentsFamily;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import trazit.enums.ActionsClass;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;
import trazit.enums.EnumIntEndpoints;
/**
 *
 * @author User
 */
public class ClassInstruments implements ActionsClass{

    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    InternalMessage actionDiagnosesObj = null;
    private Boolean functionFound = false;
    private EnumIntEndpoints enumConstantByName;
    public ClassInstruments(HttpServletRequest request, InstrumentsAPIactionsEndpoints endPoint) {

        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        RelatedObjects rObj = RelatedObjects.getInstanceForActions();
        InternalMessage actionDiagnoses = null;
        Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
        this.enumConstantByName=endPoint;
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
            ResponseMessages mainMessage = procReqSession.getMessages();
            mainMessage.addMainForError(ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, ConfigAnalysisStructure.ConfigAnalysisErrorTrapping.MISSING_MANDATORY_FIELDS, new Object[]{argValues[2].toString()});
            this.messageDynamicData = new Object[]{argValues[2].toString()};
            return;
        }
        DataInstruments instr = null;
        String instrName = argValues[0].toString();
        if (Boolean.FALSE.equals("NEW_INSTRUMENT".equalsIgnoreCase(endPoint.getName()))
                && Boolean.FALSE.equals(endPoint.getName().toUpperCase().startsWith("CONFIG"))) {
            instr = new DataInstruments(instrName);
            if (Boolean.TRUE.equals(instr.getHasError())) {
                this.actionDiagnosesObj = instr.getErrorDetail();
                this.relatedObj = rObj;
                rObj.killInstance();
                return;
            }
            String[] responsiblesArr = new String[]{};
            if (LPNulls.replaceNull(instr.getResponsible()).length() > 0) {
                responsiblesArr = LPArray.addValueToArray1D(responsiblesArr, instr.getResponsible());
            }
            if (LPNulls.replaceNull(instr.getResponsibleBackup()).length() > 0) {
                responsiblesArr = LPArray.addValueToArray1D(responsiblesArr, instr.getResponsibleBackup());
            }
            if (responsiblesArr.length > 0 && Boolean.FALSE.equals(LPArray.valueInArray(responsiblesArr, procReqSession.getToken().getPersonName()))) {
                this.actionDiagnosesObj = instr.getErrorDetail();
                this.actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.ONLY_RESPONSIBLE_OR_BACKUP, new Object[]{});
                this.relatedObj = rObj;
                rObj.killInstance();
                return;
            }
        }
        this.functionFound = true;
        switch (endPoint) {
            case NEW_INSTRUMENT:
                String familyName = argValues[1].toString();
                String modelNumber = argValues[2].toString();
                String serialNumber = argValues[3].toString();
                String supplierName = argValues[4].toString();
                String manufacturerName = argValues[5].toString();
                String poDateStr = argValues[6].toString();
                String installationDateStr = argValues[7].toString();
                String fieldName = argValues[8].toString();
                String fieldValue = argValues[9].toString();
                String[] fieldNames = null;
                Object[] fieldValues = null;
                if (fieldName != null && fieldName.length() > 0) {
                    fieldNames = fieldName.split("\\|");
                }
                if (fieldValue != null && fieldValue.length() > 0) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"),
                            TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS, fieldNames);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                        this.actionDiagnosesObj = (InternalMessage) fieldValues[1];
                        this.messageDynamicData = this.actionDiagnosesObj.getMessageCodeVariables();
                        return;
                    }
                }
                if (fieldValues != null && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else if (LPNulls.replaceNull(modelNumber).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.MODEL_NUMBER.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, modelNumber);
                }
                if (LPNulls.replaceNull(serialNumber).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.SERIAL_NUMBER.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, serialNumber);
                }
                if (LPNulls.replaceNull(supplierName).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.SUPPLIER.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, supplierName);
                }
                if (LPNulls.replaceNull(manufacturerName).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.MANUFACTURER.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, manufacturerName);
                }
                if (LPNulls.replaceNull(poDateStr).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.PO_DATE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, LPDate.stringFormatToDate(poDateStr));
                }
                if (LPNulls.replaceNull(installationDateStr).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.INSTALLATION_DATE.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, LPDate.stringFormatToDate(installationDateStr));
                }
                Integer fldPosic = LPArray.valuePosicInArray(fieldNames, TblsInstrumentsData.Instruments.RESPONSIBLE.getName());
                if (fldPosic > -1) {
                    Object[] personByUser = UserAndRolesViews.getPersonByUser(fieldValues[fldPosic].toString());
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUser[0].toString())) {
                        this.actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.USER_NOT_FOUND_TOBE_RESPONSIBLE, new Object[]{fieldValues[fldPosic].toString()});
                        this.messageDynamicData = new Object[]{fieldValues[fldPosic].toString()};
                        return;
                    } else {
                        fieldValues[fldPosic] = personByUser[0];
                    }
                }
                fldPosic = LPArray.valuePosicInArray(fieldNames, TblsInstrumentsData.Instruments.RESPONSIBLE_BACKUP.getName());
                if (fldPosic > -1) {
                    Object[] personByUser = UserAndRolesViews.getPersonByUser(fieldValues[fldPosic].toString());
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUser[0].toString())) {
                        this.actionDiagnosesObj = new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.USER_NOT_FOUND_TOBE_RESPONSIBLE_BACKUP, new Object[]{fieldValues[fldPosic].toString()});
                        this.messageDynamicData = new Object[]{fieldValues[fldPosic].toString()};
                        return;
                    } else {
                        fieldValues[fldPosic] = personByUser[0];
                    }
                }
                actionDiagnoses = DataInstruments.createNewInstrument(instrName, familyName, fieldNames, fieldValues);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case CHANGE_INSTRUMENT_FAMILY:
                this.relatedObj = rObj;
                rObj.killInstance();
                return;
            case UPDATE_INSTRUMENT:
                modelNumber = argValues[1].toString();
                serialNumber = argValues[2].toString();
                supplierName = argValues[3].toString();
                manufacturerName = argValues[4].toString();
                fieldName = argValues[5].toString();
                fieldValue = argValues[6].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldName != null) {
                    fieldNames = fieldName.split("\\|");
                }
                if (fieldValue != null) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else if (LPNulls.replaceNull(modelNumber).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.MODEL_NUMBER.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, modelNumber);
                }
                if (LPNulls.replaceNull(serialNumber).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.SERIAL_NUMBER.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, serialNumber);
                }
                if (LPNulls.replaceNull(supplierName).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.SUPPLIER.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, supplierName);
                }
                if (LPNulls.replaceNull(manufacturerName).length() > 0) {
                    fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.MANUFACTURER.getName());
                    fieldValues = LPArray.addValueToArray1D(fieldValues, manufacturerName);
                }
                actionDiagnoses = instr.updateInstrument(fieldNames, fieldValues);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case ASSIGN_RESPONSIBLE:
                if (instr.getResponsible() != null) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.INSTR_ALREADY_HAS_RESPONSIBLE, new Object[]{}, null);
                    break;
                }
                String userName = argValues[1].toString();
                Object[] personByUserDaign = UserAndRolesViews.getPersonByUser(userName);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUserDaign[0].toString())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_FALSE, personByUserDaign[personByUserDaign.length - 1].toString(), new Object[]{}, null);
                    break;
                }
                String personName = personByUserDaign[0].toString();
                fieldName = argValues[2].toString();
                fieldValue = argValues[3].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldName != null) {
                    fieldNames = fieldName.split("\\|");
                }
                if (fieldValue != null) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                }
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.RESPONSIBLE.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, personName);
                actionDiagnoses = instr.assignResponsible(fieldNames, fieldValues);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case CHANGE_RESPONSIBLE:
                userName = argValues[1].toString();
                personByUserDaign = UserAndRolesViews.getPersonByUser(userName);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUserDaign[0].toString())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_FALSE, personByUserDaign[personByUserDaign.length - 1].toString(), new Object[]{}, null);
                    break;
                }
                personName = personByUserDaign[0].toString();
                fieldName = argValues[2].toString();
                fieldValue = argValues[3].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldName != null) {
                    fieldNames = fieldName.split("\\|");
                }
                if (fieldValue != null) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                }
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.RESPONSIBLE.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, personName);
                actionDiagnoses = instr.changeResponsible(fieldNames, fieldValues);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case ASSIGN_RESPONSIBLE_BACKUP:
                if (instr.getResponsibleBackup() != null) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.INSTR_ALREADY_HAS_RESPONSIBLE_BACKUP, new Object[]{}, null);
                    break;
                }

                userName = argValues[1].toString();
                personByUserDaign = UserAndRolesViews.getPersonByUser(userName);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUserDaign[0].toString())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_FALSE, personByUserDaign[personByUserDaign.length - 1].toString(), new Object[]{}, null);
                    break;
                }
                personName = personByUserDaign[0].toString();
                fieldName = argValues[2].toString();
                fieldValue = argValues[3].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldName != null) {
                    fieldNames = fieldName.split("\\|");
                }
                if (fieldValue != null) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                }
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.RESPONSIBLE.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, personName);
                actionDiagnoses = instr.assignResponsibleBackup(fieldNames, fieldValues);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case CHANGE_RESPONSIBLE_BACKUP:
                userName = argValues[1].toString();
                personByUserDaign = UserAndRolesViews.getPersonByUser(userName);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(personByUserDaign[0].toString())) {
                    actionDiagnoses = new InternalMessage(LPPlatform.LAB_FALSE, personByUserDaign[personByUserDaign.length - 1].toString(), new Object[]{}, null);
                    break;
                }
                personName = personByUserDaign[0].toString();
                fieldName = argValues[2].toString();
                fieldValue = argValues[3].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldName != null) {
                    fieldNames = fieldName.split("\\|");
                }
                if (fieldValue != null) {
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                }
                fieldNames = LPArray.addValueToArray1D(fieldNames, TblsInstrumentsData.Instruments.RESPONSIBLE.getName());
                fieldValues = LPArray.addValueToArray1D(fieldValues, personName);
                actionDiagnoses = instr.changeResponsibleBackup(fieldNames, fieldValues);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case DECOMMISSION_INSTRUMENT:
                fieldName = argValues[1].toString();
                fieldValue = argValues[2].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldValue != null && fieldValue.length() > 0) {
                    if (fieldName != null) {
                        fieldNames = fieldName.split("\\|");
                    }
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else {
                    actionDiagnoses = instr.decommissionInstrument(fieldNames, fieldValues);
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case UNDECOMMISSION_INSTRUMENT:
                fieldName = argValues[1].toString();
                fieldValue = argValues[2].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldValue != null && fieldValue.length() > 0) {
                    if (fieldName != null) {
                        fieldNames = fieldName.split("\\|");
                    }
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else {
                    actionDiagnoses = instr.unDecommissionInstrument(fieldNames, fieldValues);
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case TURN_ON_LINE:
                fieldName = argValues[1].toString();
                fieldValue = argValues[2].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldValue != null && fieldValue.length() > 0) {
                    if (fieldName != null) {
                        fieldNames = fieldName.split("\\|");
                    }
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else {
                    actionDiagnoses = instr.turnOnLine(fieldNames, fieldValues);
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case TURN_OFF_LINE:
                fieldName = argValues[1].toString();
                fieldValue = argValues[2].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldValue != null && fieldValue.length() > 0) {
                    if (fieldName != null) {
                        fieldNames = fieldName.split("\\|");
                    }
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else {
                    instr = new DataInstruments(instrName);
                }
                actionDiagnoses = instr.turnOffLine(fieldNames, fieldValues);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case START_CALIBRATION:
                instr = new DataInstruments(instrName);
                actionDiagnoses = instr.startCalibration(false);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), actionDiagnoses.getNewObjectId());
                }
                break;
            case COMPLETE_CALIBRATION:
                instr = new DataInstruments(instrName);
                String decision = argValues[3].toString();
                actionDiagnoses = instr.completeCalibration(decision);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case START_PREVENTIVE_MAINTENANCE:
                instr = new DataInstruments(instrName);
                actionDiagnoses = instr.startPrevMaint(false);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), actionDiagnoses.getNewObjectId());
                }
                break;
            case COMPLETE_PREVENTIVE_MAINTENANCE:
                instr = new DataInstruments(instrName);
                decision = argValues[3].toString();
                actionDiagnoses = instr.completePrevMaint(decision);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case START_VERIFICATION:
                instr = new DataInstruments(instrName);
                actionDiagnoses = instr.startVerification();
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), actionDiagnoses.getNewObjectId());
                }
                break;
            case COMPLETE_VERIFICATION:
                instr = new DataInstruments(instrName);
                decision = argValues[3].toString();
                actionDiagnoses = instr.completeVerification(decision);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case START_SERVICE:
                instr = new DataInstruments(instrName);
                actionDiagnoses = instr.startSevice();
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), actionDiagnoses.getNewObjectId());
                }
                break;
            case COMPLETE_SERVICE:
                instr = new DataInstruments(instrName);
                decision = argValues[3].toString();
                actionDiagnoses = instr.completeService(decision);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case REOPEN_EVENT:
                instr = new DataInstruments(instrName);
                Integer instrEventId = (Integer) argValues[1];
                actionDiagnoses = instr.reopenEvent(instrEventId);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case ENTER_EVENT_RESULT:
                instrEventId = (Integer) argValues[1];
                String variableName = argValues[2].toString();
                String newValue = argValues[3].toString();
                InputStream requestBody = null;
                byte[] textInBytes = null;
                if (Boolean.valueOf(LPNulls.replaceNull(argValues[4]).toString())) {

                    try {
                        //requestBody = LPAPIArguments.getRequestBody(request);

                        String text = "";
                        String pdfPath = "D:/LP/Interfaces/HPLC_VALIDACIONES_FRAN_382.pdf";
                        File pdfFile = new File(pdfPath);

                        PDDocument document = PDDocument.load(pdfFile);

                        PDFTextStripper textStripper = new PDFTextStripper();
                        text = textStripper.getText(document);
                        textInBytes = text.getBytes(StandardCharsets.UTF_8);
                        requestBody = new ByteArrayInputStream(textInBytes);
                    } catch (IOException ex) {
                        Logger.getLogger(ClassInstruments.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                actionDiagnoses = objectVariableSetValue(instrName, instrEventId, variableName, newValue, textInBytes);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;

            case REENTER_EVENT_RESULT:
                instrEventId = (Integer) argValues[1];
                variableName = argValues[2].toString();
                newValue = argValues[3].toString();
                actionDiagnoses = objectVariableChangeValue(instrName, instrEventId, variableName, newValue);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                }
                break;
            case INSTRUMENTAUDIT_SET_AUDIT_ID_REVIEWED:
                String instrumentName = LPNulls.replaceNull(argValues[0]).toString();
                Integer auditId = Integer.valueOf(LPNulls.replaceNull(argValues[1]).toString());
                Object[][] auditInfo = QueryUtilitiesEnums.getTableData(TblsInstrumentsDataAudit.TablesInstrumentsDataAudit.INSTRUMENTS,
                        EnumIntTableFields.getTableFieldsFromString(TblsInstrumentsDataAudit.TablesInstrumentsDataAudit.INSTRUMENTS, new String[]{TblsInstrumentsDataAudit.Instruments.INSTRUMENT_NAME.getName()}),
                        new String[]{TblsInstrumentsDataAudit.Instruments.AUDIT_ID.getName()}, new Object[]{auditId},
                        new String[]{TblsInstrumentsDataAudit.Instruments.AUDIT_ID.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditInfo[0][0].toString())) {
                    actionDiagnoses = new InternalMessage(auditInfo[0][0].toString(), SampleAudit.SampleAuditErrorTrapping.AUDIT_RECORD_NOT_FOUND, new Object[]{auditId});
                    instrumentName = null;
                } else {
                    actionDiagnoses = instrumentAuditSetAuditRecordAsReviewed(auditId, ProcedureRequestSession.getInstanceForActions(null, null, null).getToken().getPersonName());
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TblsInstrumentsDataAudit.TablesInstrumentsDataAudit.INSTRUMENTS.getTableName(), auditId);
                    this.messageDynamicData = new Object[]{auditId, instrumentName};
                    break;
                }
                break;
            case CONFIG_NEW_INSTRUMENT_FAMILY:
                instrName = argValues[0].toString();
                fieldName = argValues[1].toString();
                fieldValue = argValues[2].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldValue != null && fieldValue.length() > 0) {
                    if (fieldName != null) {
                        fieldNames = fieldName.split("\\|");
                    }
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                }
                if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                    actionDiagnoses = (InternalMessage) fieldValues[1];
                } else {
                    actionDiagnoses = ConfigInstrumentsFamily.configNewInstrumentFamily(instrName, fieldNames, fieldValues);
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), actionDiagnoses.getNewObjectId());
                }
                break;                
            case CONFIG_UPDATE_INSTRUMENT_FAMILY:
                instrName = argValues[0].toString();
                fieldName = argValues[1].toString();
                fieldValue = argValues[2].toString();
                String eventName = argValues[3].toString();
                String intervalType = argValues[4].toString();
                String intervalNumber = argValues[5].toString();
                fieldNames = null;
                fieldValues = null;
                if (fieldValue != null && fieldValue.length() > 0) {
                    if (fieldName != null) {
                        fieldNames = fieldName.split("\\|");
                    }
                    fieldValues = LPArray.convertStringWithDataTypeToObjectArrayInternalMessage(fieldValue.split("\\|"));
                }
                actionDiagnoses=null;
                if (eventName.length()>0&&(intervalType.length()>0||intervalNumber.length()>0)){
                    switch (eventName){
                        case "calib":
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsInstrumentsConfig.InstrumentsFamily.CALIB_INTERVAL.getName());
                            break;
                        case "pm":
                            fieldNames=LPArray.addValueToArray1D(fieldNames, TblsInstrumentsConfig.InstrumentsFamily.PM_INTERVAL.getName());
                            break;
                        default: 
                            actionDiagnoses=new InternalMessage(LPPlatform.LAB_FALSE, InstrEventsErrorTrapping.CONFIG_EVENT_NAME_NOT_RECOGNIZED, new Object[]{eventName, "calib, pm"});
                            break;
                    }                    
                    if (intervalType.length()==0||intervalNumber.length()==0){
                        actionDiagnoses=new InternalMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING, new Object[]{"intervalType, eventName"});                        
                    }else{                            
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(LPMath.isNumeric(intervalNumber, false).getDiagnostic())){
                            actionDiagnoses=LPMath.isNumeric(intervalNumber, false);
                        }
                        InternalMessage intervalTypeOneRecognized = isIntervalTypeOneRecognized(intervalType.toUpperCase());
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(intervalTypeOneRecognized.getDiagnostic())){ 
                            actionDiagnoses=intervalTypeOneRecognized;
                        }                        
                        fieldValues=LPArray.addValueToArray1D(fieldValues, intervalType+"*"+intervalNumber);
                    }                            
                }
                if (actionDiagnoses==null){
                    if (fieldValues != null && fieldValues.length>0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(fieldValues[0].toString())) {
                        actionDiagnoses = (InternalMessage) fieldValues[1];
                    } else {
                        actionDiagnoses = ConfigInstrumentsFamily.configUpdateInstrumentFamily(instrName, fieldNames, fieldValues);
                    }                
                }
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                    rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), actionDiagnoses.getNewObjectId());
                }
                break;
            case ADD_ATTACHMENT:
                instrName = argValues[0].toString();
                instrEventId = LPNulls.replaceNull(argValues[1]).toString().length() > 0 ? (Integer) argValues[1] : null;
                String attachUrl = argValues[2].toString();
                String briefSummary = argValues[3].toString();
                if (instr != null) {
                    actionDiagnoses = instr.addAttachment(instrEventId, attachUrl, briefSummary);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                        if (instrEventId != null) {
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), instrEventId);
                        }
                    }
                }
                break;
            case REMOVE_ATTACHMENT:
                instrName = argValues[0].toString();
                instrEventId = LPNulls.replaceNull(argValues[1]).toString().length() > 0 ? (Integer) argValues[1] : null;
                Integer attachmentId = LPNulls.replaceNull(argValues[2]).toString().length() > 0 ? (Integer) argValues[2] : null;
                if (instr != null) {
                    actionDiagnoses = instr.removeAttachment(instrEventId, attachmentId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                        if (instrEventId != null) {
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), instrEventId);
                        }
                    }
                }
                break;
            case REACTIVATE_ATTACHMENT:
                instrName = argValues[0].toString();
                instrEventId = LPNulls.replaceNull(argValues[1]).toString().length() > 0 ? (Integer) argValues[1] : null;
                attachmentId = LPNulls.replaceNull(argValues[2]).toString().length() > 0 ? (Integer) argValues[2] : null;
                if (instr != null) {
                    actionDiagnoses = instr.reactivateAttachment(instrEventId, attachmentId);
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(actionDiagnoses.getDiagnostic())) {
                        rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENTS.getTableName(), instrName);
                        if (instrEventId != null) {
                            rObj.addSimpleNode(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName()), TablesInstrumentsData.INSTRUMENT_EVENT.getTableName(), instrEventId);
                        }
                    }
                }
                break;
            default:
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnosticBilingue(request, null, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND, null);
                return;
        }
        if (actionDiagnoses != null) {
            this.actionDiagnosesObj = actionDiagnoses;
            this.relatedObj = rObj;
            rObj.killInstance();
        }
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
