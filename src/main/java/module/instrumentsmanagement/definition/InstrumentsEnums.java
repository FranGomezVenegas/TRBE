/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.definition;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_LAST_N_POINTS;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_NUM_DAYS;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME;
import com.labplanet.servicios.app.InvestigationAPI;
import module.instrumentsmanagement.definition.TblsInstrumentsData.TablesInstrumentsData;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.ArrayList;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;

/**
 *
 * @author User
 */
public class InstrumentsEnums {

    public enum AppConfigInstrumentsAuditEvents implements EnumIntAuditEvents {
        INSTRUMENT_FAMILY_UPDATED
    }

    public enum AppInstrumentsAuditEvents implements EnumIntAuditEvents {
        CREATION, TURN_ON_LINE, TURN_OFF_LINE,
        RESPONSIBLE_ASSIGNED, RESPONSIBLE_CHANGED, RESPONSIBLE_BACKUP_ASSIGNED, RESPONSIBLE_BACKUP_CHANGED,
        PREVIOUS_USAGE_PERF_CHECK, CALIBRATION, START_CALIBRATION, COMPLETE_CALIBRATION,
        PREVENTIVE_MAINTENANCE, START_PREVENTIVE_MAINTENANCE, COMPLETE_PREVENTIVE_MAINTENANCE,
        VERIFICATION, START_VERIFICATION, COMPLETE_VERIFICATION,
        SERVICE, START_SERVICE, COMPLETE_SERVICE,
        NON_ROUTINE_EVENT, DECOMMISSION, UNDECOMMISSION, UPDATE_INSTRUMENT,
        VALUE_ENTERED, VALUE_REENTERED, REOPEN_EVENT,
        ADDED_ATTACHMENT, REMOVED_ATTACHMENT, REACTIVATED_ATTACHMENT        
    }

    public enum InstrumentsAPIactionsEndpoints implements EnumIntEndpoints {
        NEW_INSTRUMENT("NEW_INSTRUMENT", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentNewInstrumentCreated_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FAMILY_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                    new LPAPIArguments("modelNumber", LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                    new LPAPIArguments("serialNumber", LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                    new LPAPIArguments("supplierName", LPAPIArguments.ArgumentType.STRING.toString(), false, 10),
                    new LPAPIArguments("manufacturerName", LPAPIArguments.ArgumentType.STRING.toString(), false, 11),
                    new LPAPIArguments("poDate", LPAPIArguments.ArgumentType.STRING.toString(), false, 12),
                    new LPAPIArguments("installationDate", LPAPIArguments.ArgumentType.STRING.toString(), false, 13),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 14),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 15),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        CHANGE_INSTRUMENT_FAMILY("CHANGE_INSTRUMENT_FAMILY", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentFamilyChanged_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("newFamilyName", LPAPIArguments.ArgumentType.STRING.toString(), true, 7)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        UPDATE_INSTRUMENT("UPDATE_INSTRUMENT", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentUpdated_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("modelNumber", LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                    new LPAPIArguments("serialNumber", LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                    new LPAPIArguments("supplierName", LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
                    new LPAPIArguments("manufacturerName", LPAPIArguments.ArgumentType.STRING.toString(), false, 10),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 11),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        ASSIGN_RESPONSIBLE("ASSIGN_RESPONSIBLE", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentResponsibleAssigned_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("userName", LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        CHANGE_RESPONSIBLE("CHANGE_RESPONSIBLE", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentResponsibleChanged_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("userName", LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        ASSIGN_RESPONSIBLE_BACKUP("ASSIGN_RESPONSIBLE_BACKUP", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentResponsibleBackupAssigned_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("userName", LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        CHANGE_RESPONSIBLE_BACKUP("CHANGE_RESPONSIBLE_BACKUP", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentResponsibleBackupChanged_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("userName", LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        DECOMMISSION_INSTRUMENT("DECOMMISSION_INSTRUMENT", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentDecommissioned_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        UNDECOMMISSION_INSTRUMENT("UNDECOMMISSION_INSTRUMENT", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentUndecommissioned_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        TURN_ON_LINE("TURN_ON_LINE", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentTurnedONLine_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        TURN_OFF_LINE("TURN_OFF_LINE", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentTurnedOFFLine_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        START_CALIBRATION("START_CALIBRATION", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentCalibrationStarted_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        COMPLETE_CALIBRATION("COMPLETE_CALIBRATION", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentCalibrationCompleted_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),
                    new LPAPIArguments("decision", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        START_PREVENTIVE_MAINTENANCE("START_PREVENTIVE_MAINTENANCE", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentPrevMaintStarted_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        COMPLETE_PREVENTIVE_MAINTENANCE("COMPLETE_PREVENTIVE_MAINTENANCE", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentPrevMaintCompleted_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),
                    new LPAPIArguments("decision", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        START_VERIFICATION("START_VERIFICATION", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentVerificationStarted_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        COMPLETE_VERIFICATION("COMPLETE_VERIFICATION", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentVerificationCompleted_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),
                    new LPAPIArguments("decision", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        START_SERVICE("START_SERVICE", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentServiceStarted_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        COMPLETE_SERVICE("COMPLETE_SERVICE", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentServiceCompleted_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),
                    new LPAPIArguments("decision", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        REOPEN_EVENT("REOPEN_EVENT", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentEventReopened_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        ENTER_EVENT_RESULT("ENTER_EVENT_RESULT", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "eventValueEntered_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments("variableName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("newValue", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments("containsAttachment", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        REENTER_EVENT_RESULT("REENTER_EVENT_RESULT", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "eventValueReentered_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7),
                    new LPAPIArguments("variableName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("newValue", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        INSTRUMENTAUDIT_SET_AUDIT_ID_REVIEWED("INSTRUMENTAUDIT_SET_AUDIT_ID_REVIEWED", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentAuditIdReviewed_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("auditId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),
        CONFIG_NEW_INSTRUMENT_FAMILY("CONFIG_NEW_INSTRUMENT_FAMILY", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "configInstrumentFamilyCreated_success",
                new LPAPIArguments[]{new LPAPIArguments("instrFamilyName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 8),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),  
        CONFIG_UPDATE_INSTRUMENT_FAMILY("CONFIG_UPDATE_INSTRUMENT_FAMILY", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "instrumentServiceStarted_success",
                new LPAPIArguments[]{new LPAPIArguments("instrFamilyName", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                    new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                null, null),  
        ADD_ATTACHMENT("ADD_ATTACHMENT", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "attachmentAdded_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
                new LPAPIArguments("fileUrl", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments("briefSummary", LPAPIArguments.ArgumentType.STRING.toString(), false, 9)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                "Provides the ability for adding attachment for a given instrument or even for a given event if the event id (optional) is added as part of the request", null),        
        REMOVE_ATTACHMENT("REMOVE_ATTACHMENT", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "attachmentRemoved_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
                new LPAPIArguments("attachmentId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                "Provides the ability for removing attachment for a given instrument or even for a given event if the event id (optional) is added as part of the request", null),        
        REACTIVATE_ATTACHMENT("REACTIVATE_ATTACHMENT", GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, "", "attachmentReactivated_success",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7),
                new LPAPIArguments("attachmentId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 8)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TablesInstrumentsData.INSTRUMENTS.getTableName()).build()).build(),
                "Provides the ability for reactivate one previously removed attachment for a given instrument or even for a given event if the event id (optional) is added as part of the request", null),        
        ;
        private InstrumentsAPIactionsEndpoints(String name, String mandatoryParams, String optionalParams, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.mandatoryParams = mandatoryParams;
            this.optionalParams = optionalParams;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.outputObjectTypes = outputObjectTypes;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);

        }

        @Override
        public String getName() {
            return this.name;
        }

        public String getMandatoryParams() {
            return this.mandatoryParams;
        }

        @Override
        public String getSuccessMessageCode() {
            return this.successMessageCode;
        }

        @Override
        public JsonArray getOutputObjectTypes() {
            return outputObjectTypes;
        }

        @Override
        public String getApiUrl() {
            return ApiUrls.INSTRUMENTS_ACTIONS.getUrl();
        }

        public HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex) {
            HashMap<HttpServletRequest, Object[]> hm = new HashMap<>();
            Object[] argValues = new Object[0];
            for (LPAPIArguments curArg : this.arguments) {
                argValues = LPArray.addValueToArray1D(argValues, curArg.getName() + ":" + getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }
            hm.put(request, argValues);
            return hm;
        }

        @Override
        public LPAPIArguments[] getArguments() {
            return arguments;
        }
        private final String name;
        private final String mandatoryParams;
        private final String optionalParams;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;

        @Override
        public String getDeveloperComment() {
            return this.devComment;
        }

        @Override
        public String getDeveloperCommentTag() {
            return this.devCommentTag;
        }
        private final String devComment;
        private final String devCommentTag;
    }
    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME + "|" + GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN + "|" + GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

    public enum InstrumentsAPIqueriesEndpoints implements EnumIntEndpoints {
        ACTIVE_INSTRUMENTS_LIST("ACTIVE_INSTRUMENTS_LIST", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FAMILY_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
            new LPAPIArguments("excludeIfUserIsNotResponsibleOrBackUp", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 8)}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        DECOMISSIONED_INSTRUMENTS_LAST_N_DAYS("DECOMISSIONED_INSTRUMENTS_LAST_N_DAYS", "",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 6),
                    new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FAMILY_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7)},
                EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        GET_INSTRUMENT_FAMILY_LIST("GET_INSTRUMENT_FAMILY_LIST", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        INSTRUMENT_AUDIT_FOR_GIVEN_INSTRUMENT("INSTRUMENT_AUDIT_FOR_GIVEN_INSTRUMENT", "", new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        INSTRUMENT_EVENTS_FOR_GIVEN_INSTRUMENT("INSTRUMENT_EVENTS_FOR_GIVEN_INSTRUMENT", "", new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        INSTRUMENT_EVENTS_INPROGRESS("INSTRUMENT_EVENTS_INPROGRESS", "", new LPAPIArguments[]{
            new LPAPIArguments("excludeIfUserIsNotResponsibleOrBackUp", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FAMILY_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
            new LPAPIArguments(REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9)}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        INSTRUMENT_EVENT_VARIABLES("INSTRUMENT_EVENT_VARIABLES", "", new LPAPIArguments[]{
            new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        COMPLETED_EVENTS_LAST_N_DAYS("COMPLETED_EVENTS_LAST_N_DAYS", "",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 6)},
                EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        EVENTS_ABOUT_OR_EXPIRED("EVENTS_ABOUT_OR_EXPIRED", "",
                new LPAPIArguments[]{new LPAPIArguments(REQUEST_PARAM_PROCINSTANCENAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6)},
                EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        OPEN_INVESTIGATIONS("OPEN_INVESTIGATIONS", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        INVESTIGATION_EVENTS_PENDING_DECISION("INVESTIGATION_EVENTS_PENDING_DECISION", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION("INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION", "", new LPAPIArguments[]{new LPAPIArguments(InvestigationAPI.ParamsList.INVESTIGATION_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        INVESTIGATION_RESULTS_PENDING_DECISION("INVESTIGATION_RESULTS_PENDING_DECISION", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),
        GET_INSTRUMENT_REPORT("GET_INSTRUMENT_REPORT", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_START, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_END, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
            new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 9),
            new LPAPIArguments(REQUEST_PARAM_LAST_N_POINTS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 10)}, EndPointsToRequirements.endpointWithNoOutputObjects,
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_RPT_INFO, "Name: INCUBATOR REPORT v1.0").build()).build(),
                null, null),
        INSTRUMENT_EVENTS_CALENDAR("INSTRUMENT_EVENTS_CALENDAR", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 6),            
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FAMILY_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_START, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_DATE_END, LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
            new LPAPIArguments("includeOnlyScheduledOne", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 10),            
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 12)}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                null, null),        
        GET_INSTR_ATTACHMENTS("GET_INSTR_ATTACHMENTS", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_INSTRUMENT_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments("eventId", LPAPIArguments.ArgumentType.INTEGER.toString(), false, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects, null,
                "Provides the ability to get all attachments for a given instrument or even for a given event if the event id (optional) is added as part of the request", null),                
        ;
        private InstrumentsAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, JsonArray reportInfo, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.outputObjectTypes = outputObjectTypes;
            this.reportInfo = reportInfo;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
        }

        public HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex) {
            HashMap<HttpServletRequest, Object[]> hm = new HashMap<>();
            Object[] argValues = new Object[0];
            for (LPAPIArguments curArg : this.arguments) {
                argValues = LPArray.addValueToArray1D(argValues, curArg.getName() + ":" + getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }
            hm.put(request, argValues);
            return hm;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getSuccessMessageCode() {
            return this.successMessageCode;
        }

        @Override
        public JsonArray getOutputObjectTypes() {
            return outputObjectTypes;
        }

        @Override
        public LPAPIArguments[] getArguments() {
            return arguments;
        }

        public JsonArray getReportInfo() {
            return reportInfo;
        }

        @Override
        public String getApiUrl() {
            return ApiUrls.INSTRUMENTS_QUERIES.getUrl();
        }
        private final String name;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
        private final JsonArray reportInfo;

        @Override
        public String getDeveloperComment() {
            return this.devComment;
        }

        @Override
        public String getDeveloperCommentTag() {
            return this.devCommentTag;
        }
        private final String devComment;
        private final String devCommentTag;
    }

    public enum ParamsList {
        INCIDENT_ID("incidentId"), INCIDENT_TITLE("incidentTitle"), INCIDENT_DETAIL("incidentDetail"),
        NOTE("note"), NEW_STATUS("newStatus"),;

        private ParamsList(String requestName) {
            this.requestName = requestName;
        }

        public String getParamName() {
            return this.requestName;
        }
        private final String requestName;
    }

    public enum InstrumentsBusinessRules implements EnumIntBusinessRules {
        REVISION_MODE("instrumentAuditRevisionMode", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        AUTHOR_CAN_REVIEW_AUDIT_TOO("instrumentAuditAuthorCanBeReviewerToo", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        CHILD_REVISION_REQUIRED("instrumentAuditChildRevisionRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        CORRECTIVE_ACTION_FOR_REJECTED_EVENT("instrumentEventCreateCorrectiveActionForRejectedEvent", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),;

        private InstrumentsBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator,
                Boolean isOpt, ArrayList<String[]> preReqs) {
            this.tagName = tgName;
            this.areaName = areaNm;
            this.valuesList = valuesList;
            this.allowMultiValue = allowMulti;
            this.multiValueSeparator = separator;
            this.isOptional = isOpt;
            this.preReqs = preReqs;
        }

        @Override
        public String getTagName() {
            return this.tagName;
        }

        @Override
        public String getAreaName() {
            return this.areaName;
        }

        @Override
        public JSONArray getValuesList() {
            return this.valuesList;
        }

        @Override
        public Boolean getAllowMultiValue() {
            return this.allowMultiValue;
        }

        @Override
        public char getMultiValueSeparator() {
            return this.multiValueSeparator;
        }

        @Override
        public Boolean getIsOptional() {
            return isOptional;
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            return this.preReqs;
        }

        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;
        private final Boolean isOptional;
        private final ArrayList<String[]> preReqs;
    }

    public enum InstrumentsErrorTrapping implements EnumIntMessages {
        ALREADY_EXISTS("instrumentAlreadyExists", "", ""),
        NOT_FOUND("instrumentNotFound", "The instrument <*1*> is not found in procedure <*2*>", "El instrumento <*1*> no se ha encontrado para el proceso <*2*>"),
        FAMILY_NOT_FOUND("instrumentFamilyNotFound", "The instrument family <*1*> is not found in procedure <*2*>", "La familia de instrumento <*1*> no se ha encontrado para el proceso <*2*>"),
        NOT_ONLINE("instrumentNotOnline", "The instrument <*1*> is not currently on line", "El instrumento <*1*> no está actualmente en línea"),
        NOT_DECOMMISSIONED("instrumentNotDecommissioned", "The instrument <*1*> is not currently decommissioned", "El instrumento <*1*> no está actualmente retirado"),
        ALREADY_ONLINE("instrumentAlreadyOnline", "The instrument <*1*> is currently on line", "El instrumento <*1*> está actualmente en línea"),
        NO_PENDING_CALIBRATION("instrumentHasNoPendingCalibration", "The instrument <*1*> has no pending calibration in progress in this moment", "El instrumento <*1*> no tiene ninguna calibración en curso en este momento"),
        NO_PENDING_VERIFICATION("instrumentHasNoPendingVerification", "The instrument <*1*> has no pending verification in progress in this moment", "El instrumento <*1*> no tiene ninguna verificación en curso en este momento"),
        NO_PENDING_SERVICE("instrumentHasNoPendingService", "The instrument <*1*> has no pending service in progress in this moment", "El instrumento <*1*> no tiene ningún servicio en curso en este momento"),
        NO_PENDING_PREV_MAINT("instrumentHasNoPendingPrevMaint", "The instrument <*1*> has no pending preventive maintenance in progress in this moment", "El instrumento <*1*> no tiene ningun mantenimiento preventivo en curso en este momento"),
        ALREADY_HAS_PENDING_CALIBRATION("instrumentAlreadyHasPendingCalibration", "The instrument <*1*> already has one pending calibration in progress in this moment", "El instrumento <*1*> tiene actualmente una calibración en curso en este momento"),
        ALREADY_HAS_PENDING_VERIFICATION("instrumentAlreadyHasPendingVerification", "The instrument <*1*> already has one pending verification in progress in this moment", "El instrumento <*1*> tiene actualmente una verificación en curso en este momento"),
        ALREADY_HAS_PENDING_PREV_MAINT("instrumentAlreadyHasPendingPrevMaint", "The instrument <*1*> already has one pending preventive maintenance in progress in this moment", "El instrumento <*1*> tiene actualmente un mantenimiento preventivo en curso en este momento"),
        ALREADY_HAS_PENDING_SERVICE("instrumentAlreadyHasPendingService", "The instrument <*1*> already has one pending service in progress in this moment", "El instrumento <*1*> tiene actualmente un servicio en curso en este momento"),
        ALREADY_INPROGRESS("instrumentEventAlreadyInprogress", "The instrument event <*1*> is currently in progress", "El evento de instrumento <*1*> está actualmente en progreso"),
        IS_LOCKED("instrumentIsLocked", "The instrument <*1*> is locked, the reason is <*2*>", "El instrumento <*1*> está actualmente bloqueado, la razón es <*2*>"),
        TRYINGUPDATE_RESERVED_FIELD("instrumentTryingToUpdateReservedField", "Not allowed to update the reserved field <*1*>", "No permitido modificar el campo reservado <*1*>"),
        ALREADY_DECOMMISSIONED("instrumentAlreadyDecommissioned", "Instrument <*1*> already decommissioned", "Instrumento <*1*> ya fue retirado"),
        WRONG_DECISION("instrumentWrongDecision", "wrongDecision <*1*> is not one of the accepted values(<*2*>)", "wrongDecision <*1*> is not one of the accepted values(<*2*>)"),
        AUDIT_RECORDS_PENDING_REVISION("instrumentAuditRecordsPendingRevision", "The sample <*1*> has pending sign audit records.", "La muestra <*1*> tiene registros de auditoría sin firmar"),
        AUDIT_RECORD_NOT_FOUND("AuditRecordNotFound", "The audit record <*1*> for sample does not exist", "No encontrado un registro de audit para muestra con id <*1*>"),
        AUDIT_RECORD_ALREADY_REVIEWED("AuditRecordAlreadyReviewed", "The audit record <*1*> was reviewed therefore cannot be reviewed twice.", "El registro de audit para muestra con id <*1*> ya fue revisado, no se puede volver a revisar."),
        AUTHOR_CANNOT_BE_REVIEWER("AuditSamePersonCannotBeAuthorAndReviewer", "Same person cannot review its own actions", "La misma persona no puede revisar sus propias acciones"),
        PARAMETER_MISSING("sampleAuditRevisionMode_ParameterMissing", "", ""),
        DISABLED("instrumentAuditRevisionMode_Disable", "", ""),
        VARIABLE_TYPE_NOT_RECOGNIZED("variableTypeNotRecognized", "", ""),
        INSTR_ALREADY_HAS_RESPONSIBLE("instrumentAlreadyHasResponsible", "", ""),
        INSTR_ALREADY_HAS_RESPONSIBLE_BACKUP("instrumentAlreadyHasResponsibleBackup", "", ""),
        USER_NOT_FOUND_TOBE_RESPONSIBLE("instruments_userNotFoundToBeResponsible", "The user <*1*> is not part of this procedure therefore cannot be assigned as responsible", "No encontrado el usuario <*1*> asignado a este proceso, por lo tanto no puede ser el responsable de un instrumento"),
        USER_NOT_FOUND_TOBE_RESPONSIBLE_BACKUP("instruments_userNotFoundToBeResponsibleBackup", "The user <*1*> is not part of this procedure therefore cannot be assigned as responsible backup", "No encontrado el usuario <*1*> asignado a este proceso, por lo tanto no puede ser el segundo responsable de un instrumento"),
        ONLY_RESPONSIBLE_OR_BACKUP("instruments_onlyResponsibleOrBackup", "", "");

        private InstrumentsErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
            this.errorCode = errCode;
            this.defaultTextWhenNotInPropertiesFileEn = defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs = defaultTextEs;
        }

        @Override
        public String getErrorCode() {
            return this.errorCode;
        }

        @Override
        public String getDefaultTextEn() {
            return this.defaultTextWhenNotInPropertiesFileEn;
        }

        @Override
        public String getDefaultTextEs() {
            return this.defaultTextWhenNotInPropertiesFileEs;
        }

        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }

    public enum InstrEventsErrorTrapping implements EnumIntMessages {
        EVENT_NOT_FOUND("instrEvent_EventNotFound", "The instrument event <*1*> is already complete in procedure <*2*>", "The instrument event <*1*> is already complete in procedure <*2*>"),
        EVENT_NOT_OPEN_FOR_CHANGES("instrEvent_NotOpenedForChanges", "The event is not open for changes", "Evento no abierto a cambios"),
        VARIABLE_NOT_EXISTS_EVENT_WITHNOVARIABLES("instEvent_variableNotExists_eventWithNoVariables", "This event has no this variable and the event has no variables", "Este evento no contiene esta variable y el evento no tiene variables"),
        VARIABLE_NOT_EXISTS("instEvent_variableNotExists", "The parameter <*1*> is not one of the event parameters <*2*>", "El parámetro <*1*> no es uno de los que tiene el evento, <*2*>"),
        MORE_THAN_ONE_VARIABLE("instEvent_moreThanOneVariable", "Found more than one record, <*1*> for the query <*2*> on <*3*>", "Found more than one record, <*1*> for the query <*2*> on <*3*>"),
        EVENT_HAS_PENDING_RESULTS("instEvent_eventWithPendingResults", "The event has <*1*> pending results", "El evento tiene <*1*> resultado(s) pendiente(s)"),
        EVENT_NOTHING_PENDING("instEvent_eventHasMothingPending", "The event has nothing pending", "El evento no tiene nada pendiente"),
        VARIABLE_VALUE_NOTONEOFTHEEXPECTED("instEvent_valueNotOneOfExpected", "The value <*1*> is not one of the accepted values <*2*> for variable <*3*> in procedure <*4*>", "The value <*1*> is not one of the accepted values <*2*> for variable <*3*> in procedure <*4*>"),
        NOT_NUMERIC_VALUE("DataSampleAnalysisResult_ValueNotNumericForQuantitativeParam", "", ""),
        USE_REENTER_WHEN_PARAM_ALREADY_HAS_VALUE("instEvent_whenParamAlreadyHasValueRequiresToUseReenterAction", "", ""),
        USE_ENTER_WHEN_PARAM_HAS_NO_VALUE("instEvent_whenParamHasNoValueRequiresToUseEnterResultAction", "", ""),
        SAME_RESULT_VALUE("DataSampleAnalysisResult_SampleAnalysisResultSameValue", "", ""),
        ATTACHMENT_NOT_FOUND("DataSampleAnalysisResult_attachmentNotFound", "", ""),;

        private InstrEventsErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs) {
            this.errorCode = errCode;
            this.defaultTextWhenNotInPropertiesFileEn = defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs = defaultTextEs;
        }

        @Override
        public String getErrorCode() {
            return this.errorCode;
        }

        @Override
        public String getDefaultTextEn() {
            return this.defaultTextWhenNotInPropertiesFileEn;
        }

        @Override
        public String getDefaultTextEs() {
            return this.defaultTextWhenNotInPropertiesFileEs;
        }

        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }

    public enum InstrLockingReasons {
        UNDER_CALIBRATION_EVENT("instrLockedByCalibrationInProgress"),
        UNDER_MAINTENANCE_EVENT("instrLockedByMaintenanceInProgress"),
        UNDER_SERVICE_EVENT("instrLockedByServiceInProgress"),
        UNDER_DAILY_VERIF_EVENT("instrLockedByDailyVerificationInProgress"),;

        InstrLockingReasons(String propName) {
            this.propName = propName;
        }

        public String getPropertyName() {
            return this.propName;
        }
        private final String propName;
    }
}
