/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inventorytrack.apis;

import static platform.app.apis.IncidentAPIactions.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlStatementEnums;
import databases.SqlWhere;
import databases.features.Token;
import functionaljavaa.parameter.Parameter;
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
import module.inventorytrack.definition.TblsInvTrackingDataAudit;
import module.inventorytrack.definition.InvTrackingEnums.InvLotStatuses;
import module.inventorytrack.definition.InvTrackingEnums.InventoryTrackAPIqueriesEndpoints;
import module.inventorytrack.definition.TblsInvTrackingData;
import module.inventorytrack.logic.DataInventory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViewFields;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class InvTrackingAPIqueries extends HttpServlet {

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
            InventoryTrackAPIqueriesEndpoints endPoint = null;
            try {
                endPoint = InventoryTrackAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
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
                case ALL_INVENTORY_LOTS:
                    String category = LPNulls.replaceNull(argValues[0]).toString();
                    String reference = LPNulls.replaceNull(argValues[1]).toString();

                    SqlWhere sW = new SqlWhere();
                    sW.addConstraint(TblsInvTrackingData.Lot.STATUS, SqlStatement.WHERECLAUSE_TYPES.NOT_EQUAL, new Object[]{InvLotStatuses.RETIRED.toString().toLowerCase()}, null);
                    if (category.length() > 0) {
                        sW.addConstraint(TblsInvTrackingData.Lot.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.IN, category.split("\\|"), "|");
                    }
                    if (reference.length() > 0) {
                        sW.addConstraint(TblsInvTrackingData.Lot.REFERENCE, SqlStatement.WHERECLAUSE_TYPES.IN, reference.split("\\|"), "|");
                    }

                    String[] fieldsToRetrieve = getAllFieldNames(TblsInvTrackingData.TablesInvTrackingData.LOT);
                    Object[][] instrumentsInfo = QueryUtilitiesEnums.getTableData(TblsInvTrackingData.TablesInvTrackingData.LOT,
                            EnumIntTableFields.getAllFieldNamesFromDatabase(TblsInvTrackingData.TablesInvTrackingData.LOT),
                            sW, new String[]{TblsInvTrackingData.Lot.LOT_NAME.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    JSONArray jArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentsInfo[0][0].toString())) {
                        for (Object[] currInstr : instrumentsInfo) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstr);
                            JSONObject instLockingDetail = instrumentLockingInfo(fieldsToRetrieve, currInstr);
                            if (!instLockingDetail.isEmpty()) {
                                jObj.put("locking_reason", instLockingDetail);
                            }
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                case AUDIT_FOR_GIVEN_INVENTORY_LOT:
                    String lotName = LPNulls.replaceNull(argValues[0]).toString();
                    fieldsToRetrieve = getAllFieldNames(TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT);
                    if (!LPArray.valueInArray(fieldsToRetrieve, TblsInvTrackingDataAudit.Lot.AUDIT_ID.getName())) {
                        fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, TblsInvTrackingDataAudit.Lot.AUDIT_ID.getName());
                    }
                    instrumentsInfo = QueryUtilitiesEnums.getTableData(TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT,
                            EnumIntTableFields.getAllFieldNamesFromDatabase(TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT),
                            new String[]{TblsInvTrackingDataAudit.Lot.LOT_NAME.getName(), TblsInvTrackingDataAudit.Lot.PARENT_AUDIT_ID.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                            new Object[]{lotName, ""},
                            new String[]{TblsInvTrackingDataAudit.Lot.LOT_NAME.getName(), TblsInvTrackingDataAudit.Lot.DATE.getName() + " asc"}, null, false);
                    jArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentsInfo[0][0].toString())) {
                        for (Object[] currInstrAudit : instrumentsInfo) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrAudit);

                            Object[] convertToJsonObjectStringedObject = LPJson.convertToJsonObjectStringedObject(currInstrAudit[LPArray.valuePosicInArray(fieldsToRetrieve, TblsInvTrackingDataAudit.Lot.FIELDS_UPDATED.getName())].toString());
                            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObject[0].toString())) {
                                jObj.put(TblsInvTrackingDataAudit.Lot.FIELDS_UPDATED.getName(), convertToJsonObjectStringedObject[1]);
                            }

                            Integer curAuditId = Integer.valueOf(currInstrAudit[LPArray.valuePosicInArray(fieldsToRetrieve, TblsInvTrackingDataAudit.Lot.AUDIT_ID.getName())].toString());
                            Object[][] auditInfoLvl2 = QueryUtilitiesEnums.getTableData(TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT,
                                    EnumIntTableFields.getTableFieldsFromString(TblsInvTrackingDataAudit.TablesInvTrackingDataAudit.LOT, "ALL"),
                                    new String[]{TblsInvTrackingDataAudit.Lot.PARENT_AUDIT_ID.getName()}, new Object[]{curAuditId},
                                    new String[]{TblsInvTrackingDataAudit.Lot.AUDIT_ID.getName()}, null, false);
                            JSONArray jArrLvl2 = new JSONArray();
                            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditInfoLvl2[0][0].toString())) {
                                Object[] childJObj = new Object[]{null, null, "No child", "", "", "", null, "", "", null, null};
                                for (int iChild = childJObj.length; iChild < fieldsToRetrieve.length; iChild++) {
                                    childJObj = LPArray.addValueToArray1D(childJObj, "");
                                }
                                JSONObject jObjLvl2 = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, childJObj);
                                jArrLvl2.add(jObjLvl2);
                            } else {
                                for (Object[] curRowLvl2 : auditInfoLvl2) {
                                    JSONObject jObjLvl2 = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRowLvl2,
                                            new String[]{TblsInvTrackingDataAudit.Lot.FIELDS_UPDATED.getName()});
                                    Object[] convertToJsonObjectStringedObjectLvl2 = LPJson.convertToJsonObjectStringedObject(curRowLvl2[LPArray.valuePosicInArray(fieldsToRetrieve, TblsInvTrackingDataAudit.Lot.FIELDS_UPDATED.getName())].toString());
                                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObjectLvl2[0].toString())) {
                                        jObjLvl2.put(TblsInvTrackingDataAudit.Lot.FIELDS_UPDATED.getName(), convertToJsonObjectStringedObjectLvl2[1]);
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
                case QUALIFICATIONS_INPROGRESS:
                    //String[] whereFldName = new String[]{TblsInvTrackingData.LotCertification.COMPLETED_BY.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()};
                    //Object[] whereFldValue = new Object[]{};
                    sW = new SqlWhere();
                    sW.addConstraint(TblsInvTrackingData.LotCertification.COMPLETED_BY, 
                            SqlStatement.WHERECLAUSE_TYPES.IS_NULL, null, null);
                    //TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION, whereFldName, whereFldValue);
                    EnumIntTableFields[] fieldsToRetrieveObj = EnumIntTableFields.getTableFieldsFromString(TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION, "ALL");
                    fieldsToRetrieve = EnumIntTableFields.getAllFieldNames(fieldsToRetrieveObj);
                    Object[][] qualifsInProgress = QueryUtilitiesEnums.getTableData(TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION,
                            fieldsToRetrieveObj, sW, new String[]{TblsInvTrackingData.LotCertification.CATEGORY.getName(), TblsInvTrackingData.LotCertification.CREATED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(qualifsInProgress[0][0].toString())) {
                        for (Object[] currInstrEv : qualifsInProgress) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrEv);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;

                case QUALIFIFICATION_EVENT_VARIABLES:
                    String lotCertifIdStr = LPNulls.replaceNull(argValues[0]).toString();
                    category = LPNulls.replaceNull(argValues[1]).toString();
                    reference = LPNulls.replaceNull(argValues[2]).toString();
                    lotName = LPNulls.replaceNull(argValues[3]).toString();
                    jArr = new JSONArray();
                    if (lotCertifIdStr.length() == 0) {
                        DataInventory invLot = new DataInventory(lotName, reference, category, null);
                        if ((Boolean.TRUE.equals(invLot.getHasError())) || (Boolean.FALSE.equals(invLot.getRequiresQualification()))) {
                            Rdbms.closeRdbms();
                            LPFrontEnd.servletReturnSuccess(request, response, jArr);
                            return;
                        }
                        lotCertifIdStr = LPNulls.replaceNull(
                                invLot.getQualificationFieldValues()[LPArray.valuePosicInArray(
                                invLot.getQualificationFieldNames(), TblsInvTrackingData.LotCertificationVariableValues.CERTIF_ID.getName())]).toString();
                    }
                    String[] wFldNames = new String[]{TblsInvTrackingData.LotCertificationVariableValues.CERTIF_ID.getName()};
                    Object[] wFldValues = new Object[]{Integer.valueOf(lotCertifIdStr)};

                    EnumIntTableFields[] tblFieldsToRetrieveObj = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION_VARIABLE_VALUES);
                    String[] tblFieldsToRetrieve = EnumIntTableFields.getAllFieldNames(tblFieldsToRetrieveObj);
                    qualifsInProgress = QueryUtilitiesEnums.getTableData(TblsInvTrackingData.TablesInvTrackingData.LOT_CERTIFICATION_VARIABLE_VALUES,
                            tblFieldsToRetrieveObj,
                            wFldNames, wFldValues,
                            new String[]{TblsInvTrackingData.LotCertificationVariableValues.ID.getName(), TblsInvTrackingData.LotCertificationVariableValues.CREATED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(qualifsInProgress[0][0].toString())) {
                        for (Object[] currInstrEv : qualifsInProgress) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(tblFieldsToRetrieve, currInstrEv);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                case RETIRED_INVENTORY_LOTS_LAST_N_DAYS:
                    String numDays = LPNulls.replaceNull(argValues[0]).toString();
                    category = LPNulls.replaceNull(argValues[0]).toString();
                    reference = LPNulls.replaceNull(argValues[1]).toString();
                    if (numDays.length() == 0) {
                        numDays = String.valueOf(7);
                    }
                    int numDaysInt = 0 - Integer.valueOf(numDays);

                    sW = new SqlWhere();
                    sW.addConstraint(TblsInvTrackingData.Lot.RETIRED, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{true}, null);
                    sW.addConstraint(TblsInvTrackingData.Lot.RETIRED_ON, SqlStatement.WHERECLAUSE_TYPES.GREATER_THAN, new Object[]{LPDate.addDays(LPDate.getCurrentDateWithNoTime(), numDaysInt)}, null);
                    if (category.length() > 0) {
                        sW.addConstraint(TblsInvTrackingData.Lot.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.IN, category.split("\\|"), "|");
                    }
                    if (reference.length() > 0) {
                        sW.addConstraint(TblsInvTrackingData.Lot.REFERENCE, SqlStatement.WHERECLAUSE_TYPES.IN, reference.split("\\|"), "|");
                    }

                    fieldsToRetrieve = getAllFieldNames(TblsInvTrackingData.TablesInvTrackingData.LOT);
                    Object[][] instrDecommissionedClosedLastDays = QueryUtilitiesEnums.getTableData(TblsInvTrackingData.TablesInvTrackingData.LOT,
                            EnumIntTableFields.getAllFieldNamesFromDatabase(TblsInvTrackingData.TablesInvTrackingData.LOT),
                            sW, new String[]{TblsInvTrackingData.Lot.RETIRED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(instrDecommissionedClosedLastDays[0][0].toString())) {
                        for (Object[] currIncident : instrDecommissionedClosedLastDays) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                case EXPIRED_LOTS:
                    SqlWhere sWhere = new SqlWhere();
                    category = LPNulls.replaceNull(argValues[0]).toString();
                    reference = LPNulls.replaceNull(argValues[1]).toString();
                    lotName = LPNulls.replaceNull(argValues[2]).toString();
                    if (category.length() > 0) {
                        sWhere.addConstraint(TblsInvTrackingData.ViewExpiredLots.CATEGORY,
                                category.contains("%") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{category}, null);
                    }
                    if (reference.length() > 0) {
                        sWhere.addConstraint(TblsInvTrackingData.ViewExpiredLots.REFERENCE,
                                reference.contains("%") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{reference}, null);
                    }
                    if (lotName.length() > 0) {
                        sWhere.addConstraint(TblsInvTrackingData.ViewExpiredLots.LOT_NAME,
                                lotName.contains("%") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{lotName}, null);
                    }

                    sWhere.addConstraint(TblsInvTrackingData.Lot.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    reference = LPNulls.replaceNull(argValues[0]).toString();
                    category = LPNulls.replaceNull(argValues[1]).toString();
                    fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsInvTrackingData.ViewsInvTrackingData.LOTS_EXPIRED.getViewFields());
                    Object[][] referenceWithControlIssues = QueryUtilitiesEnums.getViewData(TblsInvTrackingData.ViewsInvTrackingData.LOTS_EXPIRED,
                            TblsInvTrackingData.ViewsInvTrackingData.LOTS_EXPIRED.getViewFields(),
                            sWhere, new String[]{TblsInvTrackingData.ViewExpiredLots.EXPIRY_REASON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceWithControlIssues[0][0].toString())) {
                        for (Object[] currIncident : referenceWithControlIssues) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    JSONObject jSummaryObj = new JSONObject();
                    jSummaryObj.put("datatable", jArr);
                    LPFrontEnd.servletReturnSuccess(request, response, jSummaryObj);
                    return;
                case REFERENCES_UNDER_MIN_STOCK:
                    sWhere = new SqlWhere();
                    category = LPNulls.replaceNull(argValues[0]).toString();
                    reference = LPNulls.replaceNull(argValues[1]).toString();
                    if (category.length() > 0) {
                        sWhere.addConstraint(TblsInvTrackingData.ViewReferencesStockUnderMin.CATEGORY,
                                category.contains("%") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{category.split("\\|")}, "|");
                    }
                    if (reference.length() > 0) {
                        if (reference.contains("%")) {
                            sWhere.addConstraint(TblsInvTrackingData.ViewReferencesStockUnderMin.NAME,
                                    SqlStatement.WHERECLAUSE_TYPES.LIKE, new Object[]{reference}, null);
                        } else {
                            sWhere.addConstraint(TblsInvTrackingData.ViewReferencesStockUnderMin.NAME,
                                    SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{reference.split("\\|")}, "|");
                        }
                    }
                    fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsInvTrackingData.ViewsInvTrackingData.REFERENCES_STOCK_UNDER_MIN.getViewFields());

                    sWhere.addConstraint(TblsInvTrackingData.ViewReferencesStockUnderMin.CURRENT_STOCK, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    Object[][] referenceWithStockAvailableForUseUponMin = QueryUtilitiesEnums.getViewData(TblsInvTrackingData.ViewsInvTrackingData.REFERENCES_STOCK_UNDER_MIN,
                            TblsInvTrackingData.ViewsInvTrackingData.REFERENCES_STOCK_UNDER_MIN.getViewFields(),
                            sWhere, new String[]{TblsInvTrackingData.ViewReferencesStockUnderMin.CURRENT_STOCK.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceWithStockAvailableForUseUponMin[0][0].toString())) {
                        for (Object[] currIncident : referenceWithStockAvailableForUseUponMin) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    jSummaryObj = new JSONObject();
                    jSummaryObj.put("datatable", jArr);
                    LPFrontEnd.servletReturnSuccess(request, response, jSummaryObj);
                    return;
                case REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN_STOCK:
                    sWhere = new SqlWhere();
                    category = LPNulls.replaceNull(argValues[0]).toString();
                    reference = LPNulls.replaceNull(argValues[1]).toString();
                    if (category.length() > 0) {
                        sWhere.addConstraint(TblsInvTrackingData.ViewReferencesAvailableForUseUnderMin.CATEGORY,
                                category.contains("*") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{category.split("\\|")}, "|");
                    }
                    if (reference.length() > 0) {
                        sWhere.addConstraint(TblsInvTrackingData.ViewReferencesAvailableForUseUnderMin.NAME,
                                reference.contains("*") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{reference.split("\\|")}, "|");
                    }
                    fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsInvTrackingData.ViewsInvTrackingData.REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN.getViewFields());

                    sWhere.addConstraint(TblsInvTrackingData.ViewReferencesAvailableForUseUnderMin.CURRENT_STOCK_AVAILABLE_FOR_USE, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    referenceWithStockAvailableForUseUponMin = QueryUtilitiesEnums.getViewData(TblsInvTrackingData.ViewsInvTrackingData.REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN,
                            TblsInvTrackingData.ViewsInvTrackingData.REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN.getViewFields(),
                            sWhere, new String[]{TblsInvTrackingData.ViewReferencesAvailableForUseUnderMin.CURRENT_STOCK_AVAILABLE_FOR_USE.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceWithStockAvailableForUseUponMin[0][0].toString())) {
                        for (Object[] currIncident : referenceWithStockAvailableForUseUponMin) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    jSummaryObj = new JSONObject();
                    jSummaryObj.put("datatable", jArr);
                    LPFrontEnd.servletReturnSuccess(request, response, jSummaryObj);
                    return;
                case REFERENCE_WITH_CONTROL_ISSUES:
                    jSummaryObj = new JSONObject();
                    sWhere = new SqlWhere();
                    sWhere.addConstraint(TblsInvTrackingData.Lot.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    category = LPNulls.replaceNull(argValues[0]).toString();
                    reference = LPNulls.replaceNull(argValues[1]).toString();
                    if (category.length() > 0) {
                        sWhere.addConstraint(TblsInvTrackingData.ViewExpiredLots.CATEGORY,
                                category.contains("*") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{category.split("\\|")}, "|");
                    }
                    if (reference.length() > 0) {
                        sWhere.addConstraint(TblsInvTrackingData.ViewExpiredLots.REFERENCE,
                                reference.contains("*") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{reference.split("\\|")}, "|");
                    }
                    fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsInvTrackingData.ViewsInvTrackingData.LOTS_EXPIRED.getViewFields());
                    referenceWithControlIssues = QueryUtilitiesEnums.getViewData(TblsInvTrackingData.ViewsInvTrackingData.LOTS_EXPIRED,
                            TblsInvTrackingData.ViewsInvTrackingData.LOTS_EXPIRED.getViewFields(),
                            sWhere, new String[]{TblsInvTrackingData.ViewExpiredLots.EXPIRY_REASON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceWithControlIssues[0][0].toString())) {
                        for (Object[] currIncident : referenceWithControlIssues) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                        jSummaryObj.put("has_expired_lots", true);
                    } else {
                        jSummaryObj.put("has_expired_lots", false);
                    }
                    jSummaryObj.put("expired_lots_list", jArr);

                    fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsInvTrackingData.ViewsInvTrackingData.REFERENCES_STOCK_UNDER_MIN.getViewFields());
                    sWhere = new SqlWhere();
                    sWhere.addConstraint(TblsInvTrackingData.ViewReferencesStockUnderMin.CURRENT_STOCK, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    referenceWithStockAvailableForUseUponMin = QueryUtilitiesEnums.getViewData(TblsInvTrackingData.ViewsInvTrackingData.REFERENCES_STOCK_UNDER_MIN,
                            TblsInvTrackingData.ViewsInvTrackingData.REFERENCES_STOCK_UNDER_MIN.getViewFields(),
                            sWhere, new String[]{TblsInvTrackingData.ViewReferencesStockUnderMin.CURRENT_STOCK.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceWithStockAvailableForUseUponMin[0][0].toString())) {
                        for (Object[] currIncident : referenceWithStockAvailableForUseUponMin) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                        jSummaryObj.put("has_references_with_stock_upon_min", true);
                    } else {
                        jSummaryObj.put("has_references_with_stock_upon_min", false);
                    }
                    jSummaryObj.put("references_with_stock_upon_min_list", jArr);

                    fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsInvTrackingData.ViewsInvTrackingData.REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN.getViewFields());
                    sWhere = new SqlWhere();
                    sWhere.addConstraint(TblsInvTrackingData.ViewReferencesAvailableForUseUnderMin.CURRENT_STOCK_AVAILABLE_FOR_USE, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    referenceWithStockAvailableForUseUponMin = QueryUtilitiesEnums.getViewData(TblsInvTrackingData.ViewsInvTrackingData.REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN,
                            TblsInvTrackingData.ViewsInvTrackingData.REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN.getViewFields(),
                            sWhere, new String[]{TblsInvTrackingData.ViewReferencesAvailableForUseUnderMin.CURRENT_STOCK_AVAILABLE_FOR_USE.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceWithStockAvailableForUseUponMin[0][0].toString())) {
                        for (Object[] curRow : referenceWithStockAvailableForUseUponMin) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRow);
                            jArr.add(jObj);
                        }
                        jSummaryObj.put("has_references_with_stock_available_for_use_upon_min", true);
                    } else {
                        jSummaryObj.put("has_references_with_stock_available_for_use_upon_min", false);
                    }
                    jSummaryObj.put("references_with_stock_available_for_use_upon_min_list", jArr);

                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jSummaryObj);
                    break;
                case LOT_PRINT_LABEL:
                    lotName = LPNulls.replaceNull(argValues[0]).toString();
                    JSONObject jObj = new JSONObject();
                    String zplCode = "^XA\n"
                            + "\n"
                            + "^FX Top section with logo, name and address.\n"
                            + "^CF0,60\n"
                            + "^FO50,50^GB100,100,100^FS\n"
                            + "^FO75,75^FR^GB100,100,100^FS\n"
                            + "^FO93,93^GB40,40,40^FS\n"
                            + "^FO220,50^FDIntershipping, Inc.^FS\n"
                            + "^CF0,30\n"
                            + "^FO220,115^FD1000 Shipping Lane^FS\n"
                            + "^FO220,155^FDShelbyville TN 38102^FS\n"
                            + "^FO220,195^FDUnited States (USA)^FS\n"
                            + "^FO50,250^GB700,3,3^FS\n"
                            + "\n"
                            + "^FX Second section with recipient address and permit information.\n"
                            + "^CFA,30\n"
                            + "^FO50,300^FDJohn Doe^FS\n"
                            + "^FO50,340^FD100 Main Street^FS\n"
                            + "^FO50,380^FDSpringfield TN 39021^FS\n"
                            + "^FO50,420^FDUnited States (USA)^FS\n"
                            + "^CFA,15\n"
                            + "^FO600,300^GB150,150,3^FS\n"
                            + "^FO638,340^FDPermit^FS\n"
                            + "^FO638,390^FD123456^FS\n"
                            + "^FO50,500^GB700,3,3^FS\n"
                            + "\n"
                            + "^FX Third section with bar code.\n"
                            + "^BY5,2,270\n"
                            + "^FO100,550^BC^FD" + lotName + "^FS\n"
                            + "\n"
                            + "^FX Fourth section (the two boxes on the bottom).\n"
                            + "^FO50,900^GB700,250,3^FS\n"
                            + "^FO400,900^GB3,250,3^FS\n"
                            + "^CF0,40\n"
                            + "^FO100,960^FDCtr. X34B-1^FS\n"
                            + "^FO100,1010^FDREF1 F00B47^FS\n"
                            + "^FO100,1060^FDREF2 BL4H8^FS\n"
                            + "^CF0,190\n"
                            + "^FO470,955^FDCA^FS\n"
                            + "\n"
                            + "^XZ";
                    jObj.put("zpl_code", zplCode);
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jObj);
                    break;
                default:
            }
        } catch (Exception e2) {
            JSONArray jObj = new JSONArray();
            LPFrontEnd.servletReturnSuccess(request, response, jObj);
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

        Integer fldPosic = LPArray.valuePosicInArray(fieldsToRetrieve, TblsInvTrackingData.Lot.IS_LOCKED.getName());
        if (fldPosic == -1) {
            return jObj;
        }
        if (!Boolean.TRUE.equals(Boolean.valueOf(LPNulls.replaceNull(currInstr[fldPosic]).toString()))) {
            return jObj;
        }
        fldPosic = LPArray.valuePosicInArray(fieldsToRetrieve, TblsInvTrackingData.Lot.LOCKED_REASON.getName());
        if (fldPosic == -1) {
            jObj.put("message_en", "Locked");
            jObj.put("message_es", "Bloqueado");
            return jObj;
        }
        String errorTextEn = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_API_SUCCESSMESSAGE + "InstrumentsAPIactionsEndpoints", null, LPNulls.replaceNull(currInstr[fldPosic]).toString(), "en", null, true, "InstrumentsAPIactionsEndpoints");
        if (errorTextEn.length() == 0) {
            errorTextEn = LPNulls.replaceNull(currInstr[fldPosic]).toString();
        }
        String errorTextEs = Parameter.getMessageCodeValue(LPPlatform.CONFIG_FILES_FOLDER, LPPlatform.CONFIG_FILES_API_SUCCESSMESSAGE + "InstrumentsAPIactionsEndpoints", null, LPNulls.replaceNull(currInstr[fldPosic]).toString(), "es", null, false, "InstrumentsAPIactionsEndpoints");
        if (errorTextEs.length() == 0) {
            errorTextEs = LPNulls.replaceNull(currInstr[fldPosic]).toString();
        }
        jObj.put("message_en", errorTextEn);
        jObj.put("message_es", errorTextEs);
        return jObj;
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
