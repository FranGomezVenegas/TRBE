/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.apis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labplanet.servicios.app.GlobalAPIsParams;
import static platform.app.apis.IncidentAPIactions.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlStatementEnums;
import databases.SqlWhere;
import databases.SqlWhereEntry;
import module.instrumentsmanagement.definition.TblsInstrumentsConfig;
import module.instrumentsmanagement.definition.TblsInstrumentsData;
import module.instrumentsmanagement.definition.TblsInstrumentsData.TablesInstrumentsData;
import module.instrumentsmanagement.definition.TblsInstrumentsData.ViewsInstrumentsData;
import module.instrumentsmanagement.definition.TblsInstrumentsDataAudit;
import module.instrumentsmanagement.definition.TblsInstrumentsDataAudit.TablesInstrumentsDataAudit;
import databases.TblsDataAudit;
import databases.TblsProcedure;
import databases.features.Token;
import functionaljavaa.moduleenvironmentalmonitoring.DataProgramCorrectiveAction;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrumentsAPIqueriesEndpoints;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.isTagValueOneOfEnableOnes;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.instrumentsmanagement.definition.InstrumentsEnums;
import module.instrumentsmanagement.definition.TblsInstrumentsProcedure;
import module.instrumentsmanagement.logic.DataInstrumentsCorrectiveAction;
import static module.instrumentsmanagement.logic.SchedInstruments.logNextEventWhenExpiredOrClose;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViewFields;
import static trazit.globalvariables.GlobalVariables.DEFAULTLANGUAGE;
import static trazit.queries.QueryUtilities.getNdaysArray;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class InstrumentsAPIqueries extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request = LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);

        String language = LPFrontEnd.setLanguage(request);
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        try {
            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }
            String actionName = procReqInstance.getActionName();
            String finalToken = procReqInstance.getTokenString();

            Token token = new Token(finalToken);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())) {
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode(), null, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }
            InstrumentsAPIqueriesEndpoints endPoint = null;
            try {
                endPoint = InstrumentsAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
            } catch (Exception e) {
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            if (argValues.length > 0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(argValues[0].toString())) {
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, response,
                        LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{argValues[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;
            }

            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                return;
            }

            switch (endPoint) {
                case ACTIVE_INSTRUMENTS_LIST:
                    Boolean filterByResponsible = Boolean.valueOf(LPNulls.replaceNull(argValues[1]).toString());
                    String familyName = LPNulls.replaceNull(argValues[0]).toString();
                    SqlWhere sW = new SqlWhere();
                    if (familyName.length() > 0) {
                        sW.addConstraint(TblsInstrumentsData.Instruments.FAMILY, SqlStatement.WHERECLAUSE_TYPES.IN, familyName.split("\\|"), "|");
                    }
                    sW.addConstraint(TblsInstrumentsData.Instruments.DECOMMISSIONED, SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL, new Object[]{true}, null);
                    if (Boolean.TRUE.equals(filterByResponsible)) {
                        SqlWhereEntry[] orClauses = new SqlWhereEntry[]{
                            new SqlWhereEntry(TblsInstrumentsData.ViewNotDecommInstrumentAndEventData.RESPONSIBLE,
                            SqlStatement.WHERECLAUSE_TYPES.IS_NULL, new Object[]{""}, null),
                            new SqlWhereEntry(TblsInstrumentsData.ViewNotDecommInstrumentAndEventData.RESPONSIBLE,
                            SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{token.getUserName()}, null),
                            new SqlWhereEntry(TblsInstrumentsData.ViewNotDecommInstrumentAndEventData.RESPONSIBLE_BACKUP,
                            SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{token.getUserName()}, null)
                        };
                        sW.addOrClauseConstraint(orClauses);
                    }
                    EnumIntTableFields[] allFieldNamesFromDatabase = EnumIntTableFields.getAllFieldNamesFromDatabase(TablesInstrumentsData.INSTRUMENTS);
                    String[] fieldsToRetrieve = getAllFieldNames(allFieldNamesFromDatabase);
                    Object[][] instrumentsInfo = QueryUtilitiesEnums.getTableData(TablesInstrumentsData.INSTRUMENTS,
                            allFieldNamesFromDatabase, sW, new String[]{TblsInstrumentsData.Instruments.NAME.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    JSONArray jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentsInfo[0][0].toString()))) {
                        for (Object[] currInstr : instrumentsInfo) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstr);
                            JSONObject instLockingDetail = instrumentLockingInfo(fieldsToRetrieve, currInstr);
                            if (Boolean.FALSE.equals(instLockingDetail.isEmpty())) {
                                jObj.put("locking_reason", instLockingDetail);
                            }
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;

                case INSTRUMENT_AUDIT_FOR_GIVEN_INSTRUMENT:
                    String instrName = LPNulls.replaceNull(argValues[0]).toString();
                    fieldsToRetrieve = getAllFieldNames(TblsInstrumentsDataAudit.TablesInstrumentsDataAudit.INSTRUMENTS);
                    if (Boolean.FALSE.equals(LPArray.valueInArray(fieldsToRetrieve, TblsInstrumentsDataAudit.Instruments.AUDIT_ID.getName()))) {
                        fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, TblsInstrumentsDataAudit.Instruments.AUDIT_ID.getName());
                    }
                    instrumentsInfo = QueryUtilitiesEnums.getTableData(TablesInstrumentsDataAudit.INSTRUMENTS,
                            EnumIntTableFields.getAllFieldNamesFromDatabase(TablesInstrumentsDataAudit.INSTRUMENTS),
                            new String[]{TblsInstrumentsDataAudit.Instruments.INSTRUMENT_NAME.getName(), TblsDataAudit.Sample.PARENT_AUDIT_ID.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                            new Object[]{instrName, ""},
                            new String[]{TblsInstrumentsDataAudit.Instruments.INSTRUMENT_NAME.getName(), TblsInstrumentsDataAudit.Instruments.DATE.getName() + " asc"});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentsInfo[0][0].toString()))) {
                        for (Object[] currInstrAudit : instrumentsInfo) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrAudit);

                            Object[] convertToJsonObjectStringedObject = LPJson.convertToJsonObjectStringedObject(currInstrAudit[LPArray.valuePosicInArray(fieldsToRetrieve, TblsDataAudit.Sample.FIELDS_UPDATED.getName())].toString());
                            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObject[0].toString())) {
                                jObj.put(TblsDataAudit.Sample.FIELDS_UPDATED.getName(), convertToJsonObjectStringedObject[1]);
                            }

                            Integer curAuditId = Integer.valueOf(currInstrAudit[LPArray.valuePosicInArray(fieldsToRetrieve, TblsInstrumentsDataAudit.Instruments.AUDIT_ID.getName())].toString());
                            Object[][] sampleAuditInfoLvl2 = QueryUtilitiesEnums.getTableData(TblsInstrumentsDataAudit.TablesInstrumentsDataAudit.INSTRUMENTS,
                                    EnumIntTableFields.getTableFieldsFromString(TablesInstrumentsDataAudit.INSTRUMENTS, "ALL"),
                                    new String[]{TblsDataAudit.Sample.PARENT_AUDIT_ID.getName()}, new Object[]{curAuditId},
                                    new String[]{TblsDataAudit.Sample.AUDIT_ID.getName()});
                            JSONArray jArrLvl2 = new JSONArray();
                            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditInfoLvl2[0][0].toString())) {
                                Object[] childJObj = new Object[]{null, null, "No child", "", "", "", null, "", "", null, null};
                                for (int iChild = childJObj.length; iChild < fieldsToRetrieve.length; iChild++) {
                                    childJObj = LPArray.addValueToArray1D(childJObj, "");
                                }
                                JSONObject jObjLvl2 = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, childJObj);
                                jArrLvl2.add(jObjLvl2);
                            } else {
                                for (Object[] curRowLvl2 : sampleAuditInfoLvl2) {
                                    JSONObject jObjLvl2 = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRowLvl2,
                                            new String[]{TblsDataAudit.Sample.FIELDS_UPDATED.getName()});
                                    Object[] convertToJsonObjectStringedObjectLvl2 = LPJson.convertToJsonObjectStringedObject(curRowLvl2[LPArray.valuePosicInArray(fieldsToRetrieve, TblsDataAudit.Sample.FIELDS_UPDATED.getName())].toString());
                                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObjectLvl2[0].toString())) {
                                        jObjLvl2.put(TblsDataAudit.Sample.FIELDS_UPDATED.getName(), convertToJsonObjectStringedObjectLvl2[1]);
                                    }
                                    jArrLvl2.add(jObjLvl2);
                                }
                            }
                            jObj.put("sublevel", jArrLvl2);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                case INSTRUMENT_EVENTS_FOR_GIVEN_INSTRUMENT:
                    instrName = LPNulls.replaceNull(argValues[0]).toString();
                    fieldsToRetrieve = getAllFieldNames(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENT_EVENT);
                    Object[][] appInstrumentsAuditEvents = QueryUtilitiesEnums.getTableData(TablesInstrumentsData.INSTRUMENT_EVENT,
                            EnumIntTableFields.getAllFieldNamesFromDatabase(TablesInstrumentsData.INSTRUMENT_EVENT),
                            new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName()},
                            new Object[]{instrName},
                            new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName(), TblsInstrumentsData.InstrumentEvent.CREATED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(appInstrumentsAuditEvents[0][0].toString()))) {
                        for (Object[] currInstrEv : appInstrumentsAuditEvents) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrEv);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                case INSTRUMENT_EVENTS_INPROGRESS:
                    filterByResponsible = Boolean.valueOf(LPNulls.replaceNull(argValues[0]).toString());
                    familyName = LPNulls.replaceNull(argValues[1]).toString();
                    String[] whereFldName = new String[]{TblsInstrumentsData.ViewNotDecommInstrumentAndEventData.COMPLETED_BY.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()};
                    Object[] whereFldValue = new Object[]{};
                    sW = new SqlWhere(ViewsInstrumentsData.NOT_DECOM_INSTR_EVENT_DATA_VW, whereFldName, whereFldValue);
                    if (familyName.length() > 0) {
                        sW.addConstraint(TblsInstrumentsData.ViewNotDecommInstrumentAndEventData.INSTRUMENT_FAMILY, SqlStatement.WHERECLAUSE_TYPES.IN, familyName.split("\\|"), "|");
                    }
                    if (Boolean.TRUE.equals(filterByResponsible)) {
                        SqlWhereEntry[] orClauses = new SqlWhereEntry[]{
                            new SqlWhereEntry(TblsInstrumentsData.ViewNotDecommInstrumentAndEventData.RESPONSIBLE,
                            SqlStatement.WHERECLAUSE_TYPES.IS_NULL, new Object[]{""}, null),
                            new SqlWhereEntry(TblsInstrumentsData.ViewNotDecommInstrumentAndEventData.RESPONSIBLE,
                            SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{token.getPersonName()}, null),
                            new SqlWhereEntry(TblsInstrumentsData.ViewNotDecommInstrumentAndEventData.RESPONSIBLE_BACKUP,
                            SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{token.getPersonName()}, null)
                        };
                        sW.addOrClauseConstraint(orClauses);
                    }
                    EnumIntViewFields[] fieldsToRetrieveVwObj = EnumIntViewFields.getViewFieldsFromString(ViewsInstrumentsData.NOT_DECOM_INSTR_EVENT_DATA_VW, "ALL");
                    fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(fieldsToRetrieveVwObj);
                    appInstrumentsAuditEvents = QueryUtilitiesEnums.getViewData(ViewsInstrumentsData.NOT_DECOM_INSTR_EVENT_DATA_VW,
                            fieldsToRetrieveVwObj, sW, new String[]{TblsInstrumentsData.ViewNotDecommInstrumentAndEventData.INSTRUMENT.getName(), TblsInstrumentsData.ViewNotDecommInstrumentAndEventData.CREATED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(appInstrumentsAuditEvents[0][0].toString()))) {
                        for (Object[] currInstrEv : appInstrumentsAuditEvents) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrEv);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;

                case INSTRUMENT_EVENT_VARIABLES:
                    Integer instrEventId = (Integer) argValues[0];
                    EnumIntTableFields[] tblFieldsToRetrieveObj = EnumIntTableFields.getAllFieldNamesFromDatabase(TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES);
                    String[] tblFieldsToRetrieveString = EnumIntTableFields.getAllFieldNames(tblFieldsToRetrieveObj);
                    appInstrumentsAuditEvents = QueryUtilitiesEnums.getTableData(TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES,
                            tblFieldsToRetrieveObj,
                            new String[]{TblsInstrumentsData.InstrEventVariableValues.EVENT_ID.getName()},
                            new Object[]{instrEventId},
                            new String[]{TblsInstrumentsData.InstrEventVariableValues.ID.getName(), TblsInstrumentsData.InstrEventVariableValues.CREATED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    Integer attachFldPosic = LPArray.valuePosicInArray(tblFieldsToRetrieveString, TblsInstrumentsData.InstrEventVariableValues.ATTACHMENT.getName());
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(appInstrumentsAuditEvents[0][0].toString()))) {
                        for (Object[] currInstrEv : appInstrumentsAuditEvents) {

                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(tblFieldsToRetrieveString, currInstrEv);

                            if (LPNulls.replaceNull(currInstrEv[attachFldPosic]).toString().length() > 0) {
                                String text = "";
                                String pdfPath = "D:/LP/Interfaces/HPLC_VALIDACIONES_FRAN_382.pdf";
                                File pdfFile = new File(pdfPath);

                                /*                                PDDocument document = PDDocument.load(pdfFile);

                                PDFTextStripper textStripper = new PDFTextStripper();
                                text = textStripper.getText(document);
                                byte[] textInBytes = text.getBytes(StandardCharsets.UTF_8);
                                ObjectMapper objectMapper = new ObjectMapper();
                                JsonNode jsonNode = objectMapper.convertValue(textInBytes, JsonNode.class);
                                String jsonString = objectMapper.writeValueAsString(jsonNode);
                                document.close();*/
                                byte[] buffer = new byte[1024];
                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                FileInputStream fis = new FileInputStream(pdfFile);
                                int read;
                                while ((read = fis.read(buffer)) != -1) {
                                    os.write(buffer, 0, read);
                                }
                                fis.close();
                                ObjectMapper objectMapper = new ObjectMapper();
                                JsonNode jsonNode = null;
                                try {
                                    jsonNode = objectMapper.convertValue(os, JsonNode.class);
                                } catch (Exception e) {
                                    String s = e.getMessage();
                                }
                                String jsonString = objectMapper.writeValueAsString(jsonNode);

                                os.close();

                                jObj.put("attachment_text", text);
                                jObj.put("attachment_jsonNode", jsonNode);
                                jObj.put("attachment_jsonstring", jsonString);
                            }
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                case DECOMISSIONED_INSTRUMENTS_LAST_N_DAYS:
                    String numDays = LPNulls.replaceNull(argValues[0]).toString();
                    familyName = LPNulls.replaceNull(argValues[1]).toString();

                    if (numDays.length() == 0) {
                        numDays = String.valueOf(7);
                    }
                    int numDaysInt = 0 - Integer.valueOf(numDays);
                    sW = new SqlWhere();
                    sW.addConstraint(TblsInstrumentsData.Instruments.DECOMMISSIONED,
                            SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{true}, null);
                    sW.addConstraint(TblsInstrumentsData.Instruments.DECOMMISSIONED_ON,
                            SqlStatement.WHERECLAUSE_TYPES.GREATER_THAN, new Object[]{LPDate.addDays(LPDate.getCurrentDateWithNoTime(), numDaysInt)}, null);
                    if (familyName.length() > 0) {
                        sW.addConstraint(TblsInstrumentsData.Instruments.FAMILY, SqlStatement.WHERECLAUSE_TYPES.IN, familyName.split("\\|"), "|");
                    }

                    fieldsToRetrieve = getAllFieldNames(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS);
                    Object[][] instrDecommissionedClosedLastDays = QueryUtilitiesEnums.getTableData(TablesInstrumentsData.INSTRUMENTS,
                            EnumIntTableFields.getAllFieldNamesFromDatabase(TablesInstrumentsData.INSTRUMENTS),
                            sW, new String[]{TblsInstrumentsData.Instruments.DECOMMISSIONED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrDecommissionedClosedLastDays[0][0].toString()))) {
                        for (Object[] currIncident : instrDecommissionedClosedLastDays) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                case COMPLETED_EVENTS_LAST_N_DAYS:
                    numDays = LPNulls.replaceNull(argValues[0]).toString();
                    if (numDays.length() == 0) {
                        numDays = String.valueOf(7);
                    }
                    numDaysInt = 0 - Integer.valueOf(numDays);
                    fieldsToRetrieve = getAllFieldNames(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENT_EVENT);
                    Object[][] instrEventsCompletedLastDays = QueryUtilitiesEnums.getTableData(TablesInstrumentsData.INSTRUMENT_EVENT,
                            EnumIntTableFields.getAllFieldNamesFromDatabase(TablesInstrumentsData.INSTRUMENT_EVENT),
                            new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName() + SqlStatement.WHERECLAUSE_TYPES.GREATER_THAN.getSqlClause(), TblsInstrumentsData.InstrumentEvent.COMPLETED_DECISION.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                            new Object[]{LPDate.addDays(LPDate.getCurrentDateWithNoTime(), numDaysInt)},
                            new String[]{TblsInstrumentsData.InstrumentEvent.COMPLETED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrEventsCompletedLastDays[0][0].toString()))) {
                        for (Object[] currIncident : instrEventsCompletedLastDays) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                case GET_INSTRUMENT_REPORT:
                    instrName = argValues[0].toString();

                    String startDateStr = argValues[1].toString();
                    String endDateStr = argValues[2].toString();
                    String lastNdaysStr = argValues[3].toString();
                    String numPointsStr = argValues[4].toString();

                    EnumIntTableFields[] fieldsToRetrieveObj = TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS.getTableFields();
                    Object[][] instrumentInfo = QueryUtilitiesEnums.getTableData(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS,
                            fieldsToRetrieveObj,
                            new String[]{TblsInstrumentsData.Instruments.NAME.getName()},
                            new String[]{instrName},
                            new String[]{TblsInstrumentsData.Instruments.NAME.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentInfo[0][0].toString())) {
                        LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());
                    }
                    JSONObject jObjMainObject = LPJson.convertArrayRowToJSONObject(getAllFieldNames(fieldsToRetrieveObj), instrumentInfo[0]);
                    if (LPNulls.replaceNull(lastNdaysStr).toString().length() > 0) {
                        JSONArray jArr2 = new JSONArray();
                        jArr = getNdaysArray(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENT_EVENT, lastNdaysStr, TblsInstrumentsData.InstrumentEvent.CREATED_ON,
                                new String[]{TblsInstrumentsData.InstrumentEvent.INSTRUMENT.getName()},
                                new Object[]{instrName},
                                new String[]{TblsInstrumentsData.InstrumentEvent.CREATED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                        for (int i = 0; i < jArr.size(); i++) {

                            JSONObject jsonObject = (JSONObject) jArr.get(i);
                            Object curEvId = jsonObject.get(TblsInstrumentsData.InstrumentEvent.ID.getName());
                            if (LPNulls.replaceNull(curEvId).toString().length() > 0) {
                                sW = new SqlWhere();
                                sW.addConstraint(TblsInstrumentsData.InstrEventVariableValues.EVENT_ID, SqlStatement.WHERECLAUSE_TYPES.EQUAL,
                                        new Object[]{curEvId}, null);
                                EnumIntTableFields[] variablesValuesFieldsToRetrieveObj = TblsInstrumentsData.TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES.getTableFields();
                                Object[][] VariableValuesInfo = QueryUtilitiesEnums.getTableData(TblsInstrumentsData.TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES,
                                        variablesValuesFieldsToRetrieveObj, sW, new String[]{TblsInstrumentsData.InstrEventVariableValues.EVENT_ID.getName()});
                                if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(VariableValuesInfo[0][0].toString()))) {
                                    JSONArray varValuesArr = new JSONArray();
                                    for (Object[] curRow2 : VariableValuesInfo) {
                                        varValuesArr.add(LPJson.convertArrayRowToJSONObject(getAllFieldNames(variablesValuesFieldsToRetrieveObj), curRow2));
                                    }
                                    jsonObject.put(TblsInstrumentsData.TablesInstrumentsData.INSTR_EVENT_VARIABLE_VALUES.getTableName(), varValuesArr);
                                    jArr2.add(jsonObject);
                                }
                            }
                        }
                        jObjMainObject.put("last_n_days_events", jArr2);
                    }
                    Integer familyFldPosic = EnumIntTableFields.getFldPosicInArray(fieldsToRetrieveObj, TblsInstrumentsData.Instruments.FAMILY.getName());
                    if (familyFldPosic > -1 && LPNulls.replaceNull(instrumentInfo[0][familyFldPosic]).toString().length() > 0) {
                        JSONArray jArrPieceOfInfo = new JSONArray();
                        fieldsToRetrieveObj = TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableFields();
                        Object[][] instrumentFamilyInfo = QueryUtilitiesEnums.getTableData(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY,
                                fieldsToRetrieveObj,
                                new String[]{TblsInstrumentsConfig.InstrumentsFamily.NAME.getName()},
                                new String[]{LPNulls.replaceNull(instrumentInfo[0][familyFldPosic]).toString()},
                                new String[]{TblsInstrumentsConfig.InstrumentsFamily.NAME.getName()});
                        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentFamilyInfo[0][0].toString()))) {
                            for (Object[] curRow : instrumentFamilyInfo) {
                                JSONObject insFamJson = LPJson.convertArrayRowToJSONObject(getAllFieldNames(fieldsToRetrieveObj), curRow);
                                EnumIntTableFields[] vSetFlds = new EnumIntTableFields[]{TblsInstrumentsConfig.InstrumentsFamily.CALIB_VARIABLES_SET, TblsInstrumentsConfig.InstrumentsFamily.PM_VARIABLES_SET,
                                    TblsInstrumentsConfig.InstrumentsFamily.SERVICE_VARIABLES_SET, TblsInstrumentsConfig.InstrumentsFamily.VERIF_SAME_DAY_VARIABLES_SET};
                                for (EnumIntTableFields curFld : vSetFlds) {
                                    Integer evVarSetFldPosic = EnumIntTableFields.getFldPosicInArray(fieldsToRetrieveObj, curFld.getName());
                                    if (evVarSetFldPosic > -1 && LPNulls.replaceNull(curRow[evVarSetFldPosic]).toString().length() > 0) {
                                        Object[][] evVarSetInfo = QueryUtilitiesEnums.getTableData(TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES_SET,
                                                new EnumIntTableFields[]{TblsInstrumentsConfig.VariablesSet.VARIABLES_LIST},
                                                new String[]{TblsInstrumentsConfig.VariablesSet.NAME.getName()},
                                                new String[]{LPNulls.replaceNull(curRow[evVarSetFldPosic]).toString()},
                                                new String[]{TblsInstrumentsConfig.VariablesSet.NAME.getName()});
                                        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(evVarSetInfo[0][0].toString())) && LPNulls.replaceNull(evVarSetInfo[0][0]).toString().length() > 0) {
                                            sW = new SqlWhere();
                                            sW.addConstraint(TblsInstrumentsConfig.Variables.PARAM_NAME, SqlStatement.WHERECLAUSE_TYPES.IN,
                                                    new Object[]{LPNulls.replaceNull(evVarSetInfo[0][0]).toString().split("\\|")}, "|");
                                            EnumIntTableFields[] variablesFieldsToRetrieveObj = TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES.getTableFields();
                                            Object[][] VariablesInfo = QueryUtilitiesEnums.getTableData(TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES,
                                                    variablesFieldsToRetrieveObj, sW, new String[]{TblsInstrumentsConfig.Variables.PARAM_NAME.getName()});
                                            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(VariablesInfo[0][0].toString()))) {
                                                JSONArray variablesArr = new JSONArray();
                                                for (Object[] curRow2 : VariablesInfo) {
                                                    variablesArr.add(LPJson.convertArrayRowToJSONObject(getAllFieldNames(variablesFieldsToRetrieveObj), curRow2));
                                                }
                                                insFamJson.put(curFld.getName() + "_detail", variablesArr);
                                            }
                                        }
                                    }
                                }
                                jObjMainObject.put(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableName(), insFamJson);
                            }

                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jObjMainObject);
                    return;
                /*                    

                    String[] prodLotfieldToRetrieveArr = new String[0];
                    if ((tblFieldsToRetrieveStr != null) && (tblFieldsToRetrieveStr.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(tblFieldsToRetrieveStr)) {
                            prodLotfieldToRetrieveArr = EnumIntTableFields.getAllFieldNames(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS.getTableFields());
                        } else {
                            prodLotfieldToRetrieveArr = tblFieldsToRetrieveStr.split("\\|");
                        }
                    }
                    prodLotfieldToRetrieveArr = LPArray.addValueToArray1D(prodLotfieldToRetrieveArr, TblsInstrumentsData.Instruments.NAME.getName());
                    String[] tblFieldToDisplayArr = new String[0];
                    if ((tblFieldsToDisplayStr != null) && (tblFieldsToDisplayStr.length() > 0)) {
                        if ("ALL".equalsIgnoreCase(tblFieldsToDisplayStr)) {
                            tblFieldToDisplayArr = EnumIntTableFields.getAllFieldNames(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS.getTableFields());
                        } else {
                            tblFieldToDisplayArr = tblFieldsToDisplayStr.split("\\|");
                        }
                    }
                    String[] instrumentsTblAllFields = EnumIntTableFields.getAllFieldNames(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS.getTableFields());
                    Object[][] instrumentInfo = QueryUtilitiesEnums.getTableData(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS,
                            EnumIntTableFields.getTableFieldsFromString(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS, instrumentsTblAllFields),
                            new String[]{TblsInstrumentsData.Instruments.NAME.getName()}, new Object[]{instrName}, null);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentInfo[0][0].toString())) {
                        return;
                    }
                    JSONObject jObjInstrumentInfo = new JSONObject();
                    JSONObject jObjMainObject = new JSONObject();
                    JSONObject jObjPieceOfInfo = new JSONObject();
                    JSONArray jArrPieceOfInfo = new JSONArray();
                    for (int iFlds = 0; iFlds < instrumentInfo[0].length; iFlds++) {
                        if (LPArray.valueInArray(prodLotfieldToRetrieveArr, instrumentsTblAllFields[iFlds])) {
                            jObjInstrumentInfo.put(instrumentsTblAllFields[iFlds], instrumentInfo[0][iFlds].toString());
                        }
                    }
                    String[] instrumentsTblAllFields = EnumIntTableFields.getAllFieldNames(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY.getTableFields());
                    Object[][] instrumentInfo = QueryUtilitiesEnums.getTableData(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS,
                            EnumIntTableFields.getTableFieldsFromString(TblsInstrumentsData.TablesInstrumentsData.INSTRUMENTS, instrumentsTblAllFields),
                            new String[]{TblsInstrumentsData.Instruments.NAME.getName()}, new Object[]{instrName}, null);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentInfo[0][0].toString())) {
                        return;
                    }
                    
                    
                    for (String fieldToDisplayArr1 : tblFieldToDisplayArr) {
                        if (LPArray.valueInArray(instrumentsTblAllFields, fieldToDisplayArr1)) {
                            jObjPieceOfInfo = new JSONObject();
                            jObjPieceOfInfo.put(GlobalAPIsParams.LBL_FIELD_NAME, fieldToDisplayArr1);
                            jObjPieceOfInfo.put(GlobalAPIsParams.LBL_FIELD_VALUE, instrumentInfo[0][LPArray.valuePosicInArray(instrumentsTblAllFields, fieldToDisplayArr1)].toString());
                            jArrPieceOfInfo.add(jObjPieceOfInfo);
                        }
                    }
                    jObjMainObject.put(GlobalAPIsParams.INCUBATION_REPORT_JSON_TAG_NAME_FIELD_TO_RETRIEVE, jObjInstrumentInfo);
                    jObjMainObject.put(GlobalAPIsParams.INCUBATION_REPORT_JSON_TAG_NAME_FIELD_TO_DISPLAY, jArrPieceOfInfo);

                    String numPoints = request.getParameter(EnvMonitAPIParams.REQUEST_PARAM_INCUBATOR_NUM_POINTS);
                    Integer numPointsInt = null;
                    fieldsToRetrieve = new String[]{TblsEnvMonitData.InstrIncubatorNoteBook.ID.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.EVENT_TYPE.getName(),
                        TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_ON.getName(), TblsEnvMonitData.InstrIncubatorNoteBook.CREATED_BY.getName(),
                        TblsEnvMonitData.InstrIncubatorNoteBook.TEMPERATURE.getName()};
                    if (numPoints != null) {
                        numPointsInt = Integer.valueOf(numPoints);
                    } else {
                        numPointsInt = 20;
                    }
                    Object[][] instrReadings = new Object[0][0];
                    if (startDateStr == null && endDateStr == null) {
                        instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(instrName, numPointsInt);
                    }
                    if (startDateStr != null && endDateStr == null) {

                        startDateStr = startDateStr.replace(" ", "T");
                        instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(instrName, numPointsInt, dateStringFormatToLocalDateTime(startDateStr));
                    }
                    if (startDateStr != null && endDateStr != null) {
                        startDateStr = startDateStr.replace(" ", "T");
                        endDateStr = endDateStr.replace(" ", "T");
                        instrReadings = DataIncubatorNoteBook.getLastTemperatureReading(instrName, numPointsInt, dateStringFormatToLocalDateTime(startDateStr), dateStringFormatToLocalDateTime(endDateStr), true);
                    }
                    jArrLastTempReadings = new JSONArray();
                    for (Object[] currReading : instrReadings) {
                        jObj = new JSONObject();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(currReading[0].toString())) {
                            jObj.put(GlobalAPIsParams.LBL_ERROR, "No temperature readings found");
                        } else {
                            jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currReading);
                        }

                        jArrLastTempReadings.add(jObj);
                    }
                    jObjMainObject.put(GlobalAPIsParams.INCUBATION_REPORT_JSON_TAG_NAME_LAST_N_TEMP_READINGS, jArrLastTempReadings);
                    jObjMainObject.put(reportInfoTagNAme, endPoint.getReportInfo());
                    this.isSuccess = true;
                    this.responseSuccessJObj = jObjMainObject;
                    break;
                 */

                case GET_INSTRUMENT_FAMILY_LIST:
                    jArr = instrumentFamiliesList(null);
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    break;
                case EVENTS_ABOUT_OR_EXPIRED:
                    String procInstanceName = LPNulls.replaceNull(argValues[0]).toString();
                    if (procInstanceName.length() == 0) {
                        procInstanceName = procReqInstance.getProcedureInstance();
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, logNextEventWhenExpiredOrClose(procInstanceName, true));
                    break;
                case OPEN_INVESTIGATIONS:
                    fieldsToRetrieveObj = TblsProcedure.TablesProcedure.INVESTIGATION.getTableFields();
                    Object[][] incidentsNotClosed = QueryUtilitiesEnums.getTableData(TblsProcedure.TablesProcedure.INVESTIGATION,
                            fieldsToRetrieveObj,
                            new String[]{TblsProcedure.Investigation.CLOSED.getName() + "<>"},
                            new Object[]{true},
                            new String[]{TblsProcedure.Investigation.ID.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    JSONArray investigationJArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsNotClosed[0][0].toString()))) {
                        for (Object[] currInvestigation : incidentsNotClosed) {
                            JSONObject investigationJObj = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(fieldsToRetrieveObj), currInvestigation);
                            Integer investFldPosic = LPArray.valuePosicInArray(EnumIntTableFields.getAllFieldNames(fieldsToRetrieveObj), TblsProcedure.Investigation.ID.getName());
                            if (investFldPosic > -1) {
                                Integer investigationId = Integer.valueOf(currInvestigation[investFldPosic].toString());
                                EnumIntTableFields[] fieldsToRetrieveInvestObj = TblsProcedure.TablesProcedure.INVEST_OBJECTS.getTableFields();
                                incidentsNotClosed = QueryUtilitiesEnums.getTableData(TblsProcedure.TablesProcedure.INVEST_OBJECTS,
                                        fieldsToRetrieveInvestObj,
                                        new String[]{TblsProcedure.InvestObjects.INVEST_ID.getName()},
                                        new Object[]{investigationId},
                                        new String[]{TblsProcedure.InvestObjects.ID.getName()});
                                JSONArray investObjectsJArr = new JSONArray();
                                if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsNotClosed[0][0].toString()))) {
                                    for (Object[] currInvestObject : incidentsNotClosed) {
                                        JSONObject investObjectsJObj = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(fieldsToRetrieveInvestObj), currInvestObject);
                                        investObjectsJArr.add(investObjectsJObj);
                                    }
                                }
                                investigationJObj.put(TblsProcedure.TablesProcedure.INVEST_OBJECTS.getTableName(), investObjectsJArr);
                            }
                            investigationJArr.add(investigationJObj);
                        }
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, investigationJArr);
                    break;

                case INVESTIGATION_EVENTS_PENDING_DECISION:
                    JSONArray jArray = new JSONArray();
                    String statusClosed = DataInstrumentsCorrectiveAction.InstrumentsCorrectiveActionStatuses.STATUS_CLOSED.getStatusCode();
                    String createInvCorrectiveAction = Parameter.getBusinessRuleProcedureFile(procReqInstance.getProcedureInstance(), InstrumentsEnums.InstrumentsBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_EVENT.getAreaName(), InstrumentsEnums.InstrumentsBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_EVENT.getTagName());
                    if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(createInvCorrectiveAction))) {
                        JSONObject jObj = new JSONObject();
                        jObj.put(TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION.getTableName(), "corrective action not active!");
                        jArray.add(jObj);
                    } else {
                        fieldsToRetrieveObj = TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION.getTableFields();
                        Object[][] investigationResultsPendingDecision = QueryUtilitiesEnums.getTableData(TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION,
                                fieldsToRetrieveObj,
                                new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.STATUS.getName() + "<>"},
                                new String[]{statusClosed},
                                new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.INSTRUMENT.getName()});
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationResultsPendingDecision[0][0].toString())) {
                            LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());
                        }

                        for (Object[] curRow : investigationResultsPendingDecision) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(getAllFieldNames(fieldsToRetrieveObj), curRow);
                            jArray.add(jObj);
                        }
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, jArray);
                    break;

                case INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION:
                    jArray = new JSONArray();
                    createInvCorrectiveAction = Parameter.getBusinessRuleProcedureFile(procReqInstance.getProcedureInstance(), InstrumentsEnums.InstrumentsBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_EVENT.getAreaName(), InstrumentsEnums.InstrumentsBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_EVENT.getTagName());
                    if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(createInvCorrectiveAction))) {
                        JSONObject jObj = new JSONObject();
                        jObj.put(TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION.getTableName(), "corrective action not active!");
                        jArray.add(jObj);
                        LPFrontEnd.servletReturnSuccess(request, response, jArray);
                    }
                    Integer investigationId = null;
                    String investigationIdStr = LPNulls.replaceNull(argValues[0]).toString();
                    if (investigationIdStr != null && investigationIdStr.length() > 0) {
                        investigationId = Integer.valueOf(investigationIdStr);
                    }

                    fieldsToRetrieveObj = TblsProcedure.TablesProcedure.INVESTIGATION.getTableFields();
                    incidentsNotClosed = QueryUtilitiesEnums.getTableData(TblsProcedure.TablesProcedure.INVESTIGATION,
                            fieldsToRetrieveObj,
                            new String[]{TblsProcedure.Investigation.ID.getName()},
                            new Object[]{investigationId},
                            new String[]{TblsProcedure.Investigation.ID.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    investigationJArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsNotClosed[0][0].toString()))) {
                        for (Object[] currInvestigation : incidentsNotClosed) {
                            JSONObject investigationJObj = LPJson.convertArrayRowToJSONObject(getAllFieldNames(fieldsToRetrieveObj), currInvestigation);
                            fieldsToRetrieveObj = TblsProcedure.TablesProcedure.INVEST_OBJECTS.getTableFields();
                            incidentsNotClosed = QueryUtilitiesEnums.getTableData(TblsProcedure.TablesProcedure.INVEST_OBJECTS,
                                    fieldsToRetrieveObj,
                                    new String[]{TblsProcedure.InvestObjects.INVEST_ID.getName()},
                                    new Object[]{investigationId},
                                    new String[]{TblsProcedure.InvestObjects.ID.getName()});
                            JSONArray investObjectsJArr = new JSONArray();
                            if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(incidentsNotClosed[0][0].toString()))) {
                                for (Object[] currInvestObject : incidentsNotClosed) {
                                    JSONObject investObjectsJObj = LPJson.convertArrayRowToJSONObject(getAllFieldNames(fieldsToRetrieveObj), currInvestObject);
                                    investObjectsJArr.add(investObjectsJObj);
                                }
                            }
                            investigationJObj.put(TblsProcedure.TablesProcedure.INVEST_OBJECTS.getTableName(), investObjectsJArr);
                            investigationJArr.add(investigationJObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, investigationJArr);
                    break;
                case INVESTIGATION_RESULTS_PENDING_DECISION:
                    jArray = new JSONArray();
                    statusClosed = DataProgramCorrectiveAction.ProgramCorrectiveActionStatuses.STATUS_CLOSED.getStatusCode();
                    createInvCorrectiveAction = Parameter.getBusinessRuleProcedureFile(procReqInstance.getProcedureInstance(), InstrumentsEnums.InstrumentsBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_EVENT.getAreaName(), InstrumentsEnums.InstrumentsBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_EVENT.getTagName());
                    if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(createInvCorrectiveAction))) {
                        JSONObject jObj = new JSONObject();
                        jObj.put(TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION.getTableName(), "corrective action not active!");
                        jArray.add(jObj);
                        LPFrontEnd.servletReturnSuccess(request, response, jArray);
                    }
                    fieldsToRetrieveObj = TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION.getTableFields();
                    Object[][] investigationResultsPendingDecision = QueryUtilitiesEnums.getTableData(TblsInstrumentsProcedure.TablesInstrumentsProcedure.INSTRUMENT_CORRECTIVE_ACTION,
                            fieldsToRetrieveObj,
                            new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.STATUS.getName() + "<>"},
                            new Object[]{statusClosed},
                            new String[]{TblsInstrumentsProcedure.InstrumentsCorrectiveAction.CREATED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationResultsPendingDecision[0][0].toString())) {
                        LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());
                    }
                    for (Object[] curRow : investigationResultsPendingDecision) {
                        JSONObject jObj = LPJson.convertArrayRowToJSONObject(getAllFieldNames(fieldsToRetrieveObj), curRow);
                        jArray.add(jObj);
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArray);
                    break;

                default:
            }
        } finally {
            // release database resources
            try {
                procReqInstance.killIt();

            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private JSONObject instrumentLockingInfo(String[] fieldsToRetrieve, Object[] currInstr) {
        JSONObject jObj = new JSONObject();

        Integer fldPosic = LPArray.valuePosicInArray(fieldsToRetrieve, TblsInstrumentsData.Instruments.IS_LOCKED.getName());
        if (fldPosic == -1) {
            return jObj;
        }
        if (Boolean.FALSE.equals(Boolean.TRUE.equals(Boolean.valueOf(LPNulls.replaceNull(currInstr[fldPosic]).toString())))) {
            return jObj;
        }
        fldPosic = LPArray.valuePosicInArray(fieldsToRetrieve, TblsInstrumentsData.Instruments.LOCKED_REASON.getName());
        if (fldPosic == -1) {
            jObj.put(GlobalAPIsParams.LBL_MESSAGE_EN, "Locked");
            jObj.put(GlobalAPIsParams.LBL_MESSAGE_ES, "Bloqueado");
            return jObj;
        }
        String errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_API_SUCCESSMESSAGE + "InstrumentsAPIactionsEndpoints", null, LPNulls.replaceNull(currInstr[fldPosic]).toString(), DEFAULTLANGUAGE, null, true, "InstrumentsAPIactionsEndpoints");
        if (errorTextEn.length() == 0) {
            errorTextEn = LPNulls.replaceNull(currInstr[fldPosic]).toString();
        }
        String errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_API_SUCCESSMESSAGE + "InstrumentsAPIactionsEndpoints", null, LPNulls.replaceNull(currInstr[fldPosic]).toString(), "es", null, false, "InstrumentsAPIactionsEndpoints");
        if (errorTextEs.length() == 0) {
            errorTextEs = LPNulls.replaceNull(currInstr[fldPosic]).toString();
        }
        jObj.put(GlobalAPIsParams.LBL_MESSAGE_EN, errorTextEn);
        jObj.put(GlobalAPIsParams.LBL_MESSAGE_ES, errorTextEs);
        return jObj;
    }

    public static JSONArray instrumentFamiliesList(String alternativeProcInstanceName) {
        String[] fieldsToRetrieve = getAllFieldNames(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY, alternativeProcInstanceName);
        Object[][] instrumentFamily = QueryUtilitiesEnums.getTableData(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY,
                EnumIntTableFields.getAllFieldNamesFromDatabase(TblsInstrumentsConfig.TablesInstrumentsConfig.INSTRUMENTS_FAMILY, alternativeProcInstanceName),
                new String[]{TblsInstrumentsConfig.InstrumentsFamily.NAME.getName() + "<>"},
                new Object[]{">>>"},
                new String[]{TblsInstrumentsConfig.InstrumentsFamily.NAME.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()}, alternativeProcInstanceName);
        JSONArray jArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentFamily[0][0].toString()))) {
            for (Object[] currInstr : instrumentFamily) {
                JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstr);
                jArr.add(jObj);
            }
        }
        return jArr;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
