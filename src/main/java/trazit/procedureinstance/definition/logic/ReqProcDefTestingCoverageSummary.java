/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.definition.logic;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import databases.Rdbms;
import databases.TblsTesting;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.LpPlatformBusinessRules;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import static trazit.procedureinstance.definition.apis.ReqProcedureDefinitionQueries.getScriptWithSteps;
import trazit.queries.QueryUtilities;

public class ReqProcDefTestingCoverageSummary {

    public static JSONObject procInstanceTestingInfo(String procInstanceName) {
        JSONObject jMainObj = new JSONObject();
        JSONArray dbRowsToJsonArr2 = new JSONArray();
        String repositoryName=LPPlatform.buildSchemaName(procInstanceName, TblsTesting.TablesTesting.SCRIPT.getRepositoryName());
        Object[] schemaExists=Rdbms.dbSchemaExists(repositoryName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(schemaExists[0].toString())){
            return jMainObj;
        }
        JSONArray dbRowsToJsonArr = QueryUtilities.dbRowsToJsonArr(procInstanceName, repositoryName, TblsTesting.TablesTesting.SCRIPT.getTableName(), getAllFieldNames(TblsTesting.TablesTesting.SCRIPT.getTableFields()), new String[]{TblsTesting.Script.ACTIVE.getName()}, new Object[]{true}, null, new String[]{}, true, true);

        for (int i = 0; i < dbRowsToJsonArr.length(); i++) {
            JSONObject jsonObject = (JSONObject) dbRowsToJsonArr.get(i);
            String scriptId = LPNulls.replaceNull(jsonObject.get(TblsTesting.Script.SCRIPT_ID.getName())).toString();
            if (scriptId.length() > 0) {
                JSONObject curTestObj = getScriptWithSteps(Integer.valueOf(scriptId), procInstanceName, null, null);
                dbRowsToJsonArr2.put(curTestObj);
            }
        }
        jMainObj.put("scripts", dbRowsToJsonArr2);
        dbRowsToJsonArr2 = new JSONArray();
        dbRowsToJsonArr = QueryUtilities.dbRowsToJsonArr(procInstanceName,LPPlatform.buildSchemaName(procInstanceName, TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getRepositoryName()), 
                TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableName(), getAllFieldNames(TblsTesting.TablesTesting.SCRIPTS_COVERAGE.getTableFields()), 
                new String[]{TblsTesting.ScriptsCoverage.ACTIVE.getName()}, new Object[]{true}, null, new String[]{}, true, true);
        for (int j = 0; j < dbRowsToJsonArr.length(); j++) {
            JSONObject jsonObject = (JSONObject) dbRowsToJsonArr.get(j);
            String coverageDetail = LPNulls.replaceNull(jsonObject.get("endpoints_coverage_detail")).toString();
            String endpointsExcludeList = LPNulls.replaceNull(jsonObject.get("endpoints_exclude_list")).toString();
            Integer coverageId=Integer.valueOf(jsonObject.get("coverage_id").toString());
            jsonObject.put("endpoints_summary_json", covSectionDetailEndpoints(coverageId, coverageDetail, endpointsExcludeList));
            coverageDetail = LPNulls.replaceNull(jsonObject.get("bus_rule_coverage_detail")).toString();
            jsonObject.put("business_rules_summary_json", covSectionDetailBusinessRules(coverageId, coverageDetail));
            coverageDetail = LPNulls.replaceNull(jsonObject.get("msg_coverage_detail")).toString();
            jsonObject.put("notifications_summary_json", covSectionDetailNotifications(coverageId, coverageDetail));
            dbRowsToJsonArr2.put(jsonObject);
        }
        jMainObj.put("coverage", dbRowsToJsonArr2);
        return jMainObj;
    }

    static JsonObject covSectionDetailEndpoints(Integer coverageId, String coverageDetail, String endpointsExcludeList) {
        String procedureArrInfo = null;
        JsonArray procedureObjects = null;
        JsonObject endpCovDetObj = null;
        JsonObject coverageDetailObj = null;
        if (coverageDetail == null || coverageDetail.length() == 0) {
            return endpCovDetObj;
        }
        if (coverageDetail != null) {
            endpCovDetObj = LPJson.convertToJsonObjectStringedValue(coverageDetail);
        }
        procedureArrInfo = "procedure_endpoints";
        procedureObjects = LPJson.convertToJsonArrayStringedObject(LPNulls.replaceNull(endpCovDetObj.get(procedureArrInfo)).toString());

        String summary = null;
        JsonArray endpointsDiagnostic = new JsonArray();
        JsonArray endpointsVisitedDiagnostic = new JsonArray();
        JsonArray endpointsExcludedDiagnostic = new JsonArray();
        JsonArray endpointsUncoveredDiagnostic = new JsonArray();
        if (endpCovDetObj != null) {
            summary = endpCovDetObj.get("summary").toString();
            JsonArray visitedOnes = LPJson.convertToJsonArrayStringedObject(LPNulls.replaceNull(
                    endpCovDetObj.get("visited")).toString());
            JsonArray uncoverageOnes = LPJson.convertToJsonArrayStringedObject(LPNulls.replaceNull(
                    endpCovDetObj.getAsJsonObject("uncoverage_summary").get("uncoverage_list")).toString());
            if (procedureObjects == null) {
                return endpCovDetObj;
            }
            for (int i = 0; i < procedureObjects.size(); i++) {
                JsonElement curRow = procedureObjects.get(i);
                String val = LPNulls.replaceNull(curRow.getAsString());
                JsonObject curEndDiagn = new JsonObject();
                curEndDiagn.addProperty("name", val);
                StringBuilder evaluation = new StringBuilder(0);
                if (LPJson.ValueInJsonArray(visitedOnes, val)) {
                    evaluation.append("visited");
                    endpointsVisitedDiagnostic.add(curEndDiagn);
                    curEndDiagn.addProperty("evaluation", evaluation.toString());
                    curEndDiagn.addProperty("coverage_id", coverageId);
                    endpointsDiagnostic.add(curEndDiagn);
                    continue;
                }
                if (LPArray.valueInArray(LPNulls.replaceNull(endpointsExcludeList).toString().split("\\|"), val)) {
                    evaluation.append("excluded");
                    endpointsExcludedDiagnostic.add(curEndDiagn);
                    curEndDiagn.addProperty("evaluation", evaluation.toString());
                    curEndDiagn.addProperty("coverage_id", coverageId);
                    endpointsDiagnostic.add(curEndDiagn);
                    continue;
                }
                if (LPJson.ValueInJsonArray(uncoverageOnes, val)) {
                    evaluation.append("uncoverage");
                    endpointsUncoveredDiagnostic.add(curEndDiagn);
                    curEndDiagn.addProperty("evaluation", evaluation.toString());
                    curEndDiagn.addProperty("coverage_id", coverageId);
                    endpointsDiagnostic.add(curEndDiagn);
                    continue;                
                }
            }
        }
        endpCovDetObj.add("evaluation_uncovered_only", endpointsUncoveredDiagnostic);
        endpCovDetObj.add("evaluation_excluded_only", endpointsExcludedDiagnostic);
        endpCovDetObj.add("evaluation_visited_only", endpointsVisitedDiagnostic);
        endpCovDetObj.add("evaluation", endpointsDiagnostic);
        //endpCovDetObj.put("evaluation", endpointsDiagnostic);
        return endpCovDetObj;
    }

    static Boolean XaddBusinessRule(String name) {
        if (name.toUpperCase().contains(LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName().toUpperCase())) {
            return false;
        }
        if (name.toUpperCase().contains(LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName().toUpperCase())) {
            return false;
        }
        if (name.toUpperCase().contains(LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getTagName().toUpperCase())) {
            return false;
        }
        if (name.toUpperCase().contains(LpPlatformBusinessRules.ACTIONCONFIRM_REQUIRED.getTagName().toUpperCase())) {
            return false;
        }
        if (name.toUpperCase().contains(LpPlatformBusinessRules.AUDITREASON_PHRASE.getTagName().toUpperCase())) {
            return false;
        }
        if (name.toUpperCase().contains(LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName().toUpperCase())) {
            return false;
        }
        if (name.toUpperCase().contains(LpPlatformBusinessRules.AUDIT_JUSTIF_REASON_REQUIRED.getTagName().toUpperCase())) {
            return false;
        }
        return true;
    }

    static JsonObject covSectionDetailBusinessRules(Integer coverageId, String coverageDetail) {

        String procedureArrInfo = null;
        JsonObject procedureObjects = null;
        JsonObject endpCovDetObj = null;
        JsonObject coverageDetailObj = null;
        if (coverageDetail == null || coverageDetail.length() == 0) {
            return procedureObjects;
        }
        if (coverageDetail != null) {
            endpCovDetObj = LPJson.convertToJsonObjectStringedValue(coverageDetail);
        }
        String summary = null;
        JsonArray endpointsDiagnostic = new JsonArray();
        JsonArray endpointsDiagnosticSectionOnly = new JsonArray();
        if (endpCovDetObj != null) {
            summary = endpCovDetObj.get("summary").toString();
            JsonArray uncoverageOnes = LPJson.convertToJsonArrayStringedObject(LPNulls.replaceNull(
                    endpCovDetObj.getAsJsonObject("uncoverage_summary").get("uncoverage_list")).toString());
            for (int i = 0; i < uncoverageOnes.size(); i++) {
                JsonElement curRow = uncoverageOnes.get(i);
                String val = LPNulls.replaceNull(curRow.getAsString());
                JsonObject curEndDiagn = new JsonObject();
                String[] valArr = val.split("_");
                String rName = val;
                if (valArr.length > 1) {
                    curEndDiagn.addProperty("area", valArr[0]);
                    rName = valArr[1];

                } else {
                    curEndDiagn.addProperty("area", "");
                    rName = val;
                }
                curEndDiagn.addProperty("name", rName);
                curEndDiagn.addProperty("evaluation", "not visited / not covered");
                curEndDiagn.addProperty("coverage_id", coverageId);
/*                if (addBusinessRule(rName)) {
                    endpointsDiagnostic.add(curEndDiagn);
                }*/
                endpointsDiagnosticSectionOnly.add(curEndDiagn);
            }
            endpCovDetObj.add("evaluation_uncovered_only", endpointsDiagnosticSectionOnly);
            JsonArray excludedOnes = LPJson.convertToJsonArrayStringedObject(LPNulls.replaceNull(
                    endpCovDetObj.getAsJsonObject("uncoverage_summary").get("excluded_list")).toString());
            endpointsDiagnosticSectionOnly = new JsonArray();
            for (int i = 0; i < excludedOnes.size(); i++) {
                JsonElement curRow = excludedOnes.get(i);
                String val = LPNulls.replaceNull(curRow.getAsString());
                JsonObject curEndDiagn = new JsonObject();
                String[] valArr = val.split("_");
                String rName = val;
                if (valArr.length > 1) {
                    curEndDiagn.addProperty("area", valArr[0]);
                    rName = valArr[1];

                } else {
                    curEndDiagn.addProperty("area", "");
                    rName = val;
                }
                curEndDiagn.addProperty("name", rName);
                curEndDiagn.addProperty("evaluation", "excluded");
/*                if (addBusinessRule(rName)) {
                    endpointsDiagnostic.add(curEndDiagn);
                    endpointsDiagnosticSectionOnly.add(curEndDiagn);
                }*/
            }
            endpCovDetObj.add("evaluation_excluded_only", endpointsDiagnosticSectionOnly);
/*
            JsonArray excludedByExclEndpoint = LPJson.convertToJsonArrayStringedObject(LPNulls.replaceNull(
                    endpCovDetObj.getAsJsonObject("uncoverage_summary").get("business_rules_excluded_by_exclude_the_endpoint")).toString());
            endpointsDiagnosticSectionOnly = new JsonArray();
            for (int i = 0; i < excludedByExclEndpoint.size(); i++) {
                JsonElement curRow = excludedByExclEndpoint.get(i);
                String val = LPNulls.replaceNull(curRow.getAsString());
                JsonObject curEndDiagn = new JsonObject();
                String[] valArr = val.split("_");
                String rName = val;
                if (valArr.length > 1) {
                    curEndDiagn.addProperty("area", valArr[0]);
                    rName = valArr[1];

                } else {
                    curEndDiagn.addProperty("area", "");
                    rName = val;
                }
                curEndDiagn.addProperty("name", rName);
                curEndDiagn.addProperty("evaluation", "excluded due to the endpoint was excluded");
//                if (addBusinessRule(rName)) {
//                    endpointsDiagnostic.add(curEndDiagn);
//                    endpointsDiagnosticSectionOnly.add(curEndDiagn);
//                } 
            }
            endpCovDetObj.add("evaluation_excluded_by_exclude_endpoint_only", endpointsDiagnosticSectionOnly);
*/
            JsonArray visitedOnes = LPJson.convertToJsonArrayStringedObject(LPNulls.replaceNull(
                    endpCovDetObj.get("visited")).toString());
            for (int i = 0; i < visitedOnes.size(); i++) {
                JsonObject curRow = visitedOnes.get(i).getAsJsonObject();
                String area = LPNulls.replaceNull(curRow.get("area").toString());
                String ruleN = LPNulls.replaceNull(curRow.get("rule_name").toString());
                JsonObject curEndDiagn = new JsonObject();
                //curEndDiagn.addProperty("name", area + "_" + ruleN);
                //String[] valArr=val.split("_");
                curEndDiagn.addProperty("area", area.replace("\"", ""));
                curEndDiagn.addProperty("name", ruleN.replace("\"", ""));
                curEndDiagn.addProperty("evaluation", "Visited / Covered");
/*                if (addBusinessRule(ruleN.replace("\"", ""))) {
                    endpointsDiagnostic.add(curEndDiagn);
                }*/
            }
            endpCovDetObj.add("evaluation_visited_only", endpointsDiagnosticSectionOnly);
        }
        endpCovDetObj.add("evaluation_all", endpointsDiagnostic);
        return endpCovDetObj;
    }

    static JsonObject covSectionDetailNotifications(Integer coverageId, String coverageDetail) {
        String[] internalTrazitClasses = new String[]{"LpPlatformSuccess", "RdbmsErrorTrapping", "RdbmsSuccess",
            "LpPlatformErrorTrapping"};
        String procedureArrInfo = null;
        JsonObject procedureObjects = null;
        JsonObject endpCovDetObj = null;
        JsonObject coverageDetailObj = null;
        if (coverageDetail == null || coverageDetail.length() == 0) {
            return procedureObjects;
        }
        endpCovDetObj = LPJson.convertToJsonObjectStringedValue(coverageDetail);

        String summary = null;
        JsonArray endpointsDiagnostic = new JsonArray();
        if (endpCovDetObj != null) {
            summary = endpCovDetObj.get("summary").toString();
            JsonObject summaryObj = LPJson.convertToJsonObjectStringedValue(summary);
            if (summaryObj.isEmpty()) {
                return endpCovDetObj;
            }
            JsonArray allMsgInfo = LPJson.convertToJsonArrayStringedObject(LPNulls.replaceNull(
                    summaryObj.getAsJsonArray("message_collections_visited")).toString());
            if (allMsgInfo.isEmpty()) {
                return endpCovDetObj;
            }
            for (int i = 0; i < allMsgInfo.size(); i++) {
                try {
                    JsonObject curRow = allMsgInfo.get(i).getAsJsonObject();
                    if (Boolean.FALSE.equals(curRow.isEmpty())) {
                        String clName = LPNulls.replaceNull(curRow.get("className")).toString();
                        clName = clName.replace("\"", "");
                        JsonArray clAllMsgs = curRow.get("messages").getAsJsonArray();
                        for (int iMsg = 0; iMsg < clAllMsgs.size(); iMsg++) {
                            JsonObject curEndDiagn = new JsonObject();
                            String curMsg = LPNulls.replaceNull(clAllMsgs.get(iMsg)).toString();
                            curMsg = curMsg.replace("\"", "");
                            StringBuilder eval = new StringBuilder(0);
                            String clSource = "";
                            try {
                                if (curMsg.contains(" visited")) {
                                    curMsg = curMsg.replace(" visited", "");
                                    eval.append("Visited / Covered");
                                } else {
                                    eval.append("not visited / not covered");
                                }
                                if (LPArray.valueInArray(internalTrazitClasses, clName)) {
                                    eval.append(" *** This collection is internal, not directly from the module");
                                    clSource = "TRAZIT";
                                } else {
                                    clSource = "MODULE";
                                }
                                curEndDiagn.addProperty("collection_source", clSource);
                                curEndDiagn.addProperty("collection_name", clName);
                                curEndDiagn.addProperty("notification_name", curMsg);
                                curEndDiagn.addProperty("evaluation", eval.toString());
                                curEndDiagn.addProperty("coverage_id", coverageId);
                                endpointsDiagnostic.add(curEndDiagn);
                            } catch (Exception e) {
                                String logErr = e.getMessage();
                            }
                        }
                    }
                } catch (Exception e) {
                    String logErr = e.getMessage();
                }
            }
            /*            JsonArray excludedOnes = LPJson.convertToJsonArrayStringedObject(LPNulls.replaceNull(
                    endpCovDetObj.getAsJsonObject("uncoverage_summary").get("excluded_list")).toString());
            for (int i = 0; i < excludedOnes.size(); i++) {
                JsonElement curRow = excludedOnes.get(i);
                String val = LPNulls.replaceNull(curRow.getAsString());
                JsonObject curEndDiagn = new JsonObject();
                curEndDiagn.addProperty("name", val);
                curEndDiagn.addProperty("evaluation", "excluded");
                endpointsDiagnostic.add(curEndDiagn);
            }
            JsonArray excludedByExclEndpoint = LPJson.convertToJsonArrayStringedObject(LPNulls.replaceNull(
                    endpCovDetObj.getAsJsonObject("uncoverage_summary").get("business_rules_excluded_by_exclude_the_endpoint")).toString());
            for (int i = 0; i < excludedByExclEndpoint.size(); i++) {
                JsonElement curRow = excludedByExclEndpoint.get(i);
                String val = LPNulls.replaceNull(curRow.getAsString());
                JsonObject curEndDiagn = new JsonObject();
                curEndDiagn.addProperty("name", val);
                curEndDiagn.addProperty("evaluation", "excluded due to the endpoint was excluded");
                endpointsDiagnostic.add(curEndDiagn);
            }

            JsonArray visitedOnes = LPJson.convertToJsonArrayStringedObject(LPNulls.replaceNull(
                    endpCovDetObj.get("visited")).toString());
            for (int i = 0; i < visitedOnes.size(); i++) {
                JsonObject curRow = visitedOnes.get(i).getAsJsonObject();
                String area = LPNulls.replaceNull(curRow.get("area").toString());
                String ruleN = LPNulls.replaceNull(curRow.get("rule_name").toString());
                JsonObject curEndDiagn = new JsonObject();
                curEndDiagn.addProperty("name", area + "_" + ruleN);
                curEndDiagn.addProperty("evaluation", "Visited / Covered");
                endpointsDiagnostic.add(curEndDiagn);
            }*/
        }
        endpCovDetObj.add("evaluation", endpointsDiagnostic);
        return endpCovDetObj;
    }

}
