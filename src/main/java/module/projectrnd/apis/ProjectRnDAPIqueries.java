/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.projectrnd.apis;

import com.labplanet.servicios.modulesample.ClassSampleQueries;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import static platform.app.apis.IncidentAPIactions.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.TblsData;
import databases.features.Token;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import module.formulation.definition.TblsFormulationData;
import module.formulation.logic.ClssFormulationQueries;
import static module.formulation.logic.ClssFormulationQueries.getFormulas;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData;
import module.methodvalidation.definition.TblsMethodValidationData;
import module.projectrnd.definition.ProjectsRnDEnums.ProjectsRnDAPIqueriesEndpoints;
import org.json.JSONArray;
import org.json.JSONObject;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViewFields;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class ProjectRnDAPIqueries extends HttpServlet {

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
            ProjectsRnDAPIqueriesEndpoints endPoint = null;
            try {
                endPoint = ProjectsRnDAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
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
                case ALL_ACTIVE_PROJECTS:

/*            LPMailing newMail = new LPMailing();
             newMail.fakeMailForTesting(new String[]{"info.fran.gomez@gmail.com", "fgomez@trazit.net", "ibelmonte@trazit.net"}, 
                "prueba, al ejecutar la consulta ALL_ACTIVE_PROJECTS", "esto es una prueba desde Trazit ",
                    null, null);*/
                    String project = LPNulls.replaceNull(argValues[0]).toString();
                    String projectType = LPNulls.replaceNull(argValues[1]).toString();                    

                    SqlWhere sW = new SqlWhere();
                    sW.addConstraint(TblsFormulationData.Project.IS_OPEN, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{true}, null);
                    
                    if (project.length() > 0) {
                        if ("ALL".equalsIgnoreCase(project)){
                            sW.addConstraint(TblsFormulationData.Project.NAME, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, "|");
                        }else{
                            sW.addConstraint(TblsFormulationData.Project.NAME, SqlStatement.WHERECLAUSE_TYPES.IN, project.split("\\|"), "|");
                        }
                    }
                    if (projectType.length() > 0) {
                        sW.addConstraint(TblsFormulationData.Project.TYPE, SqlStatement.WHERECLAUSE_TYPES.IN, projectType.split("\\|"), "|");
                    }
                    String[] fieldsToRetrieve = getAllFieldNames(TblsFormulationData.TablesFormulationData.PROJECT);
                    Object[][] projectsInfo = QueryUtilitiesEnums.getTableData(TblsFormulationData.TablesFormulationData.PROJECT,
                            EnumIntTableFields.getAllFieldNamesFromDatabase(TblsFormulationData.TablesFormulationData.PROJECT),
                            sW, new String[]{TblsFormulationData.Project.NAME.getName()});
                    JSONArray jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(projectsInfo[0][0].toString()))) {
                        for (Object[] curProj : projectsInfo) {
                            org.json.simple.JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curProj);
                            String curProjName=curProj[LPArray.valuePosicInArray(fieldsToRetrieve, TblsFormulationData.Project.NAME.getName())].toString();
                            sW = new SqlWhere();
                            sW.addConstraint(TblsFormulationData.Formula.PROJECT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curProjName}, null);
                            jObj.put(TblsFormulationData.TablesFormulationData.FORMULA.getTableName(), ClssFormulationQueries.getFormulas(sW, true));
                            org.json.simple.JSONObject  instLockingDetail = ClssFormulationQueries.formulaLockingInfo(fieldsToRetrieve, curProj);
                            if (Boolean.FALSE.equals(instLockingDetail.isEmpty())) {
                                jObj.put("locking_reason", instLockingDetail);
                            }
                            //jObj.put("method_validation", fakeMethodValidationData());
                            JSONArray jMainArr=new JSONArray();
                            sW = new SqlWhere();
                            sW.addConstraint(TblsMethodValidationData.ValidationMethodParams.PROJECT, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curProjName}, null);                            
                            EnumIntTableFields[] fldToGet = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS);
                            Object[][] projectParamsInfo = QueryUtilitiesEnums.getTableData(TblsMethodValidationData.TablesMethodValidationData.VALIDATION_METHOD_PARAMS,
                                    fldToGet,sW, new String[]{TblsMethodValidationData.ValidationMethodParams.NAME.getName()});
                            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projectParamsInfo[0][0].toString())){
                                jObj.put("method_validation", jMainArr);
                            }else{
                                for (Object[] curParam: projectParamsInfo){                                    
                                    jMainArr.put(methodValidationData(curProjName, 
                                            curParam[EnumIntTableFields.getFldPosicInArray(fldToGet, TblsMethodValidationData.ValidationMethodParams.NAME.getName())].toString(),
                                    curParam[EnumIntTableFields.getFldPosicInArray(fldToGet, TblsMethodValidationData.ValidationMethodParams.ANALYTICAL_PARAMETER.getName())].toString()));
                                }
                            }                            
                            jObj.put("method_validation", jMainArr);
                            
                            jArr.put(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;                
                case ALL_ACTIVE_FORMULAS:
                    String formulaName = LPNulls.replaceNull(argValues[0]).toString();
                    project = LPNulls.replaceNull(argValues[1]).toString();

                    sW = new SqlWhere();
                    sW.addConstraint(TblsFormulationData.Formula.IS_OPEN, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{true}, null);
                    if (formulaName.length() > 0) {
                        if ("ALL".equalsIgnoreCase(formulaName)){
                            sW.addConstraint(TblsFormulationData.Formula.NAME, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, "|");
                        }else{
                            sW.addConstraint(TblsFormulationData.Formula.NAME, SqlStatement.WHERECLAUSE_TYPES.IN, formulaName.split("\\|"), "|");
                        }
                    }
                    if (project.length() > 0) {
                        sW.addConstraint(TblsFormulationData.Formula.PROJECT, SqlStatement.WHERECLAUSE_TYPES.IN, project.split("\\|"), "|");
                    }

                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, getFormulas(sW, true));
                    return;
                case GET_SAMPLE_ANALYSIS_RESULT_LIST:
                    RelatedObjects rObj = RelatedObjects.getInstanceForActions();
                        Integer sampleId = Integer.valueOf(LPNulls.replaceNull(argValues[0]).toString());
                        String[] resultFieldToRetrieveArr = EnumIntViewFields.getAllFieldNames(EnumIntViewFields.getViewFieldsFromString(TblsInspLotRMData.ViewsInspLotRMData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, "ALL"));
                        //fieldsToGet = EnumIntViewFields.(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, sampleAnalysisFieldToRetrieve.split("\\|"));
                        resultFieldToRetrieveArr = LPArray.getUniquesArray(LPArray.addValueToArray1D(resultFieldToRetrieveArr, SampleAPIParams.MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|")));

                        String[] sampleAnalysisWhereFieldsNameArr = new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName()};
                        Object[] sampleAnalysisWhereFieldsValueArr = new Object[]{sampleId};

                        String sampleAnalysisWhereFieldsName = LPNulls.replaceNull(argValues[2]).toString();
                        if ((sampleAnalysisWhereFieldsName != null) && (sampleAnalysisWhereFieldsName.length() > 0)) {
                            sampleAnalysisWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsName.split("\\|"));
                        }
                        String sampleAnalysisWhereFieldsValue = LPNulls.replaceNull(argValues[3]).toString();
                        if ((sampleAnalysisWhereFieldsValue != null) && (sampleAnalysisWhereFieldsValue.length() > 0)) {
                            sampleAnalysisWhereFieldsValueArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsValueArr, LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")));
                        }

                        String sarWhereFieldsName = LPNulls.replaceNull(argValues[4]).toString();
                        if ((sarWhereFieldsName != null) && (sarWhereFieldsName.length() > 0)) {
                            sampleAnalysisWhereFieldsNameArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsNameArr, sarWhereFieldsName.split("\\|"));
                        }
                        String sarWhereFieldsValue = LPNulls.replaceNull(argValues[5]).toString();
                        if ((sarWhereFieldsValue != null) && (sarWhereFieldsValue.length() > 0)) {
                            sampleAnalysisWhereFieldsValueArr = LPArray.addValueToArray1D(sampleAnalysisWhereFieldsValueArr, (sampleAnalysisWhereFieldsValue != null ? LPArray.convertStringWithDataTypeToObjectArray(sampleAnalysisWhereFieldsValue.split("\\|")) : new Object[]{}));
                        }

                        String[] sortFieldsNameArr = null;
                        String sortFieldsName = LPNulls.replaceNull(argValues[6]).toString();
                        if ((sortFieldsName != null) && (sortFieldsName.length() > 0)) {
                            sortFieldsNameArr = sortFieldsName.split("\\|");
                        } else {
                            sortFieldsNameArr = LPArray.getUniquesArray(SampleAPIParams.MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_RESULT_LIST.split("\\|"));
                        }

                        Integer posicRawValueFld = LPArray.valuePosicInArray(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE.getName());
                        if (posicRawValueFld == -1) {
                            resultFieldToRetrieveArr = LPArray.addValueToArray1D(resultFieldToRetrieveArr, TblsData.ViewSampleAnalysisResultWithSpecLimits.RAW_VALUE.getName());
                            posicRawValueFld = resultFieldToRetrieveArr.length;
                        }
    
                        Object[][] analysisResultList = null;
                        Object[] viewExists=Rdbms.dbViewExists(procReqInstance.getProcedureInstance(), procReqInstance.getProcedureInstance(), TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW.getRepositoryName(), TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW.getViewName());
                        Integer posicLimitIdFld = -1;
                        EnumIntViewFields[] fldsToGetView = EnumIntViewFields.getAllFieldNamesFromDatabase(TblsInspLotRMData.ViewsInspLotRMData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, null);
                        EnumIntTableFields[] fldsToGetTable = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, null);
                        String[] fldsToGet=null;
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(viewExists[0].toString())){
                            fldsToGet=EnumIntViewFields.getAllFieldNames(fldsToGetView);
                            posicLimitIdFld = EnumIntViewFields.getFldPosicInArray(fldsToGetView, TblsData.ViewSampleAnalysisResultWithSpecLimits.LIMIT_ID.getName());
                            analysisResultList = QueryUtilitiesEnums.getViewData(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW,
                                    fldsToGetView,
                                    new SqlWhere(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsValueArr),
                                    sortFieldsNameArr);
                            
                        }else{
                            fldsToGet=EnumIntTableFields.getAllFieldNames(fldsToGetTable);
                            posicLimitIdFld = EnumIntTableFields.getFldPosicInArray(fldsToGetTable, TblsData.ViewSampleAnalysisResultWithSpecLimits.LIMIT_ID.getName());
                            analysisResultList = QueryUtilitiesEnums.getTableData(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT,
                                    EnumIntTableFields.getAllFieldNamesFromDatabase(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT),
                                    new SqlWhere(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT, sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsValueArr),
                                    new String[]{TblsData.SampleAnalysisResult.RESULT_ID.getName()});                                    
                        }
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisResultList[0][0].toString())) {
                            Rdbms.closeRdbms();
                            LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());

                            //this.isSuccess = true;
                            //this.responseSuccessJArr = new JSONArray();
                        } else {
                            rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                            Object[] objectsIds = ClassSampleQueries.getObjectsId(fldsToGet, analysisResultList, "-");
                            for (Object curObj : objectsIds) {
                                String[] curObjDet = curObj.toString().split("-");
                                if (TblsData.SampleAnalysisResult.TEST_ID.getName().equalsIgnoreCase(curObjDet[0])) {
                                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), curObjDet[1]);
                                }
                                if (TblsData.SampleAnalysisResult.RESULT_ID.getName().equalsIgnoreCase(curObjDet[0])) {
                                    rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), curObjDet[1]);
                                }
                            }
                            jArr = new JSONArray();
                            for (Object[] curRow : analysisResultList) {
                                ConfigSpecRule specRule = new ConfigSpecRule();
                                String currRowRawValue = curRow[posicRawValueFld].toString();
                                String currRowLimitId = curRow[posicLimitIdFld].toString();
                                JSONObject row = new JSONObject();
                                Object[] resultLockData = null;
                                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(viewExists[0].toString())){
                                    resultLockData = ClassSampleQueries.sampleAnalysisResultLockData(fldsToGet, curRow);
                                }else{
                                    resultLockData = ClassSampleQueries.sampleAnalysisResultLockData(fldsToGet, curRow);
                                }
                                if (resultLockData != null && resultLockData[0] != null) {
                                    if (resultLockData.length > 2) {
                                        row = LPJson.convertArrayRowToJSONObjectNoJsonSimple(LPArray.addValueToArray1D(LPArray.addValueToArray1D(fldsToGet, (String) resultLockData[2]), (String[]) resultLockData[0]),
                                                LPArray.addValueToArray1D(LPArray.addValueToArray1D(curRow, resultLockData[3]), (Object[]) resultLockData[1]));
                                    } else {
                                        row = LPJson.convertArrayRowToJSONObjectNoJsonSimple(LPArray.addValueToArray1D(fldsToGet, (String[]) resultLockData[0]), LPArray.addValueToArray1D(curRow, (Object[]) resultLockData[1]));
                                    }
                                } else {
                                    row = LPJson.convertArrayRowToJSONObjectNoJsonSimple(fldsToGet, curRow);
                                }
                                if ((currRowLimitId != null) && (currRowLimitId.length() > 0)) {
                                    specRule.specLimitsRule(Integer.valueOf(currRowLimitId), null);
                                    row.put(ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_DETAILED, LPNulls.replaceNull(specRule.getRuleRepresentation()).replace(("R"), "R (" + currRowRawValue + ")"));
                                    Object[][] specRuleDetail = specRule.getRuleData();
                                    JSONArray specRuleDetailjArr = new JSONArray();
                                    JSONObject specRuleDetailjObj = new JSONObject();
                                    for (Object[] curSpcRlDet : specRuleDetail) {
                                        specRuleDetailjObj.put(curSpcRlDet[0].toString(), curSpcRlDet[1]);
                                    }
                                    specRuleDetailjArr.put(specRuleDetailjObj);
                                    row.put(ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_INFO, specRuleDetailjArr);
                                }
                                jArr.put(row);
                            }
                            Rdbms.closeRdbms();
                            LPFrontEnd.servletReturnSuccess(request, response, jArr);
                            
                            //this.isSuccess = true;
                            //this.responseSuccessJArr = jArr;
                        }
                        return;                    
/*                    
                case ALL_INVENTORY_REFERENCES:
                    category = LPNulls.replaceNull(argValues[0]).toString();

                    sW = new SqlWhere();
                    sW.addConstraint(TblsInvTrackingConfig.Reference.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, null);
                    if (category.length() > 0) {
                        sW.addConstraint(TblsFormulationData.Lot.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.IN, category.split("\\|"), "|");
                    }

                    EnumIntTableFields[] fieldsToRetrieveObj = TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE.getTableFields();
                    Object[][] configReferencesInfo = QueryUtilitiesEnums.getTableData(TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE,
                            fieldsToRetrieveObj,
                            sW, new String[]{TblsInvTrackingConfig.Reference.CATEGORY.getName()});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(configReferencesInfo[0][0].toString()))) {
                        for (Object[] currInstr : configReferencesInfo) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(fieldsToRetrieveObj), currInstr);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
                case AUDIT_FOR_GIVEN_INVENTORY_LOT:
                    String lotName = LPNulls.replaceNull(argValues[0]).toString();
                    fieldsToRetrieve = getAllFieldNames(TblsFormulationDataAudit.TablesInvTrackingDataAudit.LOT);
                    if (Boolean.FALSE.equals(LPArray.valueInArray(fieldsToRetrieve, TblsFormulationDataAudit.Lot.AUDIT_ID.getName()))) {
                        fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, TblsFormulationDataAudit.Lot.AUDIT_ID.getName());
                    }
                    configReferencesInfo = QueryUtilitiesEnums.getTableData(TblsFormulationDataAudit.TablesInvTrackingDataAudit.LOT,
                            EnumIntTableFields.getAllFieldNamesFromDatabase(TblsFormulationDataAudit.TablesInvTrackingDataAudit.LOT),
                            new String[]{TblsFormulationDataAudit.Lot.LOT_NAME.getName(), TblsFormulationDataAudit.Lot.PARENT_AUDIT_ID.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()},
                            new Object[]{lotName, ""},
                            new String[]{TblsFormulationDataAudit.Lot.LOT_NAME.getName(), TblsFormulationDataAudit.Lot.DATE.getName() + " asc"}, null, false);
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(configReferencesInfo[0][0].toString()))) {
                        for (Object[] currInstrAudit : configReferencesInfo) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrAudit);
                            Object[] convertToJsonObjectStringedObject = LPJson.convertToJsonObjectStringedObject(currInstrAudit[LPArray.valuePosicInArray(fieldsToRetrieve, TblsFormulationDataAudit.Lot.FIELDS_UPDATED.getName())].toString());
                            if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObject[0].toString())) {
                                jObj.put(TblsFormulationDataAudit.Lot.FIELDS_UPDATED.getName(), convertToJsonObjectStringedObject[1]);
                            }
                            Integer curAuditId = Integer.valueOf(currInstrAudit[LPArray.valuePosicInArray(fieldsToRetrieve, TblsFormulationDataAudit.Lot.AUDIT_ID.getName())].toString());
                            Object[][] auditInfoLvl2 = QueryUtilitiesEnums.getTableData(TblsFormulationDataAudit.TablesInvTrackingDataAudit.LOT,
                                    EnumIntTableFields.getTableFieldsFromString(TblsFormulationDataAudit.TablesInvTrackingDataAudit.LOT, "ALL"),
                                    new String[]{TblsFormulationDataAudit.Lot.PARENT_AUDIT_ID.getName()}, new Object[]{curAuditId},
                                    new String[]{TblsFormulationDataAudit.Lot.AUDIT_ID.getName()}, null, false);
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
                                            new String[]{TblsFormulationDataAudit.Lot.FIELDS_UPDATED.getName()});
                                    Object[] convertToJsonObjectStringedObjectLvl2 = LPJson.convertToJsonObjectStringedObject(curRowLvl2[LPArray.valuePosicInArray(fieldsToRetrieve, TblsFormulationDataAudit.Lot.FIELDS_UPDATED.getName())].toString());
                                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObjectLvl2[0].toString())) {
                                        jObjLvl2.put(TblsFormulationDataAudit.Lot.FIELDS_UPDATED.getName(), convertToJsonObjectStringedObjectLvl2[1]);
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
                    sW = new SqlWhere();
                    sW.addConstraint(TblsFormulationData.LotQualification.COMPLETED_BY, SqlStatement.WHERECLAUSE_TYPES.IS_NULL, null, null);
                    fieldsToRetrieveObj = EnumIntTableFields.getTableFieldsFromString(TblsFormulationData.TablesInvTrackingData.LOT_QUALIFICATION, "ALL");
                    fieldsToRetrieve = EnumIntTableFields.getAllFieldNames(fieldsToRetrieveObj);
                    Object[][] qualifsInProgress = QueryUtilitiesEnums.getTableData(TblsFormulationData.TablesInvTrackingData.LOT_QUALIFICATION,
                            fieldsToRetrieveObj, sW, new String[]{TblsFormulationData.LotQualification.CATEGORY.getName(), TblsFormulationData.LotQualification.CREATED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(qualifsInProgress[0][0].toString()))) {
                        for (Object[] currInstrEv : qualifsInProgress) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrEv);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return; 
                case QUALIFIFICATION_EVENT_VARIABLES:
                    String lotQualifIdStr = LPNulls.replaceNull(argValues[0]).toString();
                    category = LPNulls.replaceNull(argValues[1]).toString();
                    reference = LPNulls.replaceNull(argValues[2]).toString();
                    lotName = LPNulls.replaceNull(argValues[3]).toString();
                    jArr = new JSONArray();
                    if (lotQualifIdStr.length() == 0) {
                        DataInventory invLot = new DataInventory(lotName, reference, category, null);
                        if ((Boolean.TRUE.equals(invLot.getHasError())) || (Boolean.FALSE.equals(invLot.getRequiresQualification()))) {
                            Rdbms.closeRdbms();
                            LPFrontEnd.servletReturnSuccess(request, response, jArr);
                            return;
                        }
                        lotQualifIdStr = LPNulls.replaceNull(
                                invLot.getQualificationFieldValues()[LPArray.valuePosicInArray(
                                invLot.getQualificationFieldNames(), TblsFormulationData.LotQualificationVariableValues.QUALIF_ID.getName())]).toString();
                    }
                    String[] wFldNames = new String[]{TblsFormulationData.LotQualificationVariableValues.QUALIF_ID.getName()};
                    Object[] wFldValues = new Object[]{Integer.valueOf(lotQualifIdStr)};

                    EnumIntTableFields[] tblFieldsToRetrieveObj = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsFormulationData.TablesInvTrackingData.LOT_QUALIFICATION_VARIABLE_VALUES);
                    String[] tblFieldsToRetrieve = EnumIntTableFields.getAllFieldNames(tblFieldsToRetrieveObj);
                    qualifsInProgress = QueryUtilitiesEnums.getTableData(TblsFormulationData.TablesInvTrackingData.LOT_QUALIFICATION_VARIABLE_VALUES,
                            tblFieldsToRetrieveObj,
                            wFldNames, wFldValues,
                            new String[]{TblsFormulationData.LotQualificationVariableValues.ID.getName(), TblsFormulationData.LotQualificationVariableValues.CREATED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(qualifsInProgress[0][0].toString()))) {
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
                    category = LPNulls.replaceNull(argValues[1]).toString();
                    reference = LPNulls.replaceNull(argValues[2]).toString();
                    if (numDays.length() == 0) {
                        numDays = String.valueOf(7);
                    }
                    int numDaysInt = 0 - Integer.valueOf(numDays);
                    sW = new SqlWhere();
                    sW.addConstraint(TblsFormulationData.Lot.RETIRED, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{true}, null);
                    sW.addConstraint(TblsFormulationData.Lot.RETIRED_ON, SqlStatement.WHERECLAUSE_TYPES.GREATER_THAN, new Object[]{LPDate.addDays(LPDate.getCurrentDateWithNoTime(), numDaysInt)}, null);
                    if (category.length() > 0) {
                        sW.addConstraint(TblsFormulationData.Lot.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.IN, category.split("\\|"), "|");
                    }
                    if (reference.length() > 0) {
                        sW.addConstraint(TblsFormulationData.Lot.REFERENCE, SqlStatement.WHERECLAUSE_TYPES.IN, reference.split("\\|"), "|");
                    }

                    fieldsToRetrieve = getAllFieldNames(TblsFormulationData.TablesInvTrackingData.LOT);
                    Object[][] instrDecommissionedClosedLastDays = QueryUtilitiesEnums.getTableData(TblsFormulationData.TablesInvTrackingData.LOT,
                            EnumIntTableFields.getAllFieldNamesFromDatabase(TblsFormulationData.TablesInvTrackingData.LOT),
                            sW, new String[]{TblsFormulationData.Lot.RETIRED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
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
                case EXPIRED_LOTS:
                    SqlWhere sWhere = new SqlWhere();
                    category = LPNulls.replaceNull(argValues[0]).toString();
                    reference = LPNulls.replaceNull(argValues[1]).toString();
                    lotName = LPNulls.replaceNull(argValues[2]).toString();
                    String samplingDayStart = request.getParameter(TblsFormulationData.Lot.EXPIRY_DATE.getName().toLowerCase() + "_start");
                    String samplingDayEnd = request.getParameter(TblsFormulationData.Lot.EXPIRY_DATE.getName().toLowerCase() + "_end");

                    if (category.length() > 0) {
                        sWhere.addConstraint(TblsFormulationData.ViewExpiredLots.CATEGORY,
                                category.contains("%") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, category.split("\\|"), null);
                    }
                    if (reference.length() > 0) {
                        sWhere.addConstraint(TblsFormulationData.ViewExpiredLots.REFERENCE,
                                reference.contains("%") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, reference.split("\\|"), null);
                    }
                    if (lotName.length() > 0) {
                        sWhere.addConstraint(TblsFormulationData.ViewExpiredLots.LOT_NAME,
                                lotName.contains("%") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{lotName}, null);
                    }
                    sWhere.addConstraint(TblsFormulationData.Lot.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    Object[] buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsData.CertifUserAnalysisMethod.CERTIFICATION_DATE.getName().toLowerCase(), samplingDayStart, samplingDayEnd);
                    SqlWhereEntry[] orClauses = null;
                    if (LPPlatform.LAB_TRUE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString())) {
                        if (buildDateRangeFromStrings.length > 3) {
                            orClauses = new SqlWhereEntry[]{
                                new SqlWhereEntry(TblsFormulationData.Lot.EXPIRY_DATE, SqlStatement.WHERECLAUSE_TYPES.BETWEEN, new Object[]{buildDateRangeFromStrings[2], buildDateRangeFromStrings[3]}, "|"),
                                new SqlWhereEntry(TblsFormulationData.Lot.EXPIRY_DATE_IN_USE, SqlStatement.WHERECLAUSE_TYPES.BETWEEN, new Object[]{buildDateRangeFromStrings[2], buildDateRangeFromStrings[3]}, "|")
                            };
                        } else {
                            orClauses = new SqlWhereEntry[]{
                                new SqlWhereEntry(TblsFormulationData.Lot.EXPIRY_DATE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{buildDateRangeFromStrings[2]}, "|"),
                                new SqlWhereEntry(TblsFormulationData.Lot.EXPIRY_DATE_IN_USE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{buildDateRangeFromStrings[2]}, "|")
                            };
                        }
                        sWhere.addOrClauseConstraint(orClauses);                        
                    }
                    reference = LPNulls.replaceNull(argValues[0]).toString();
                    category = LPNulls.replaceNull(argValues[1]).toString();
                    fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsFormulationData.ViewsInvTrackingData.LOTS_EXPIRED.getViewFields());
                    Object[][] referenceWithControlIssues = QueryUtilitiesEnums.getViewData(TblsFormulationData.ViewsInvTrackingData.LOTS_EXPIRED,
                            TblsFormulationData.ViewsInvTrackingData.LOTS_EXPIRED.getViewFields(),
                            sWhere, new String[]{TblsFormulationData.ViewExpiredLots.EXPIRY_REASON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceWithControlIssues[0][0].toString()))) {
                        for (Object[] currIncident : referenceWithControlIssues) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    JSONObject jSummaryObj = new JSONObject();
                    jSummaryObj.put(RESPONSE_JSON_DATATABLE, jArr);
                    LPFrontEnd.servletReturnSuccess(request, response, jSummaryObj);
                    return;
                case REFERENCES_UNDER_MIN_STOCK:
                    sWhere = new SqlWhere();
                    category = LPNulls.replaceNull(argValues[0]).toString();
                    reference = LPNulls.replaceNull(argValues[1]).toString();
                    if (category.length() > 0) {
                        sWhere.addConstraint(TblsFormulationData.ViewReferencesStockUnderMin.CATEGORY,
                                category.contains("%") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, category.split("\\|"), "|");
                    }
                    if (reference.length() > 0) {
                        if (reference.contains("%")) {
                            sWhere.addConstraint(TblsFormulationData.ViewReferencesStockUnderMin.NAME,
                                    SqlStatement.WHERECLAUSE_TYPES.LIKE, new Object[]{reference}, null);
                        } else {
                            sWhere.addConstraint(TblsFormulationData.ViewReferencesStockUnderMin.NAME,
                                    SqlStatement.WHERECLAUSE_TYPES.IN, reference.split("\\|"), "|");
                        }
                    }
                    fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsFormulationData.ViewsInvTrackingData.REFERENCES_STOCK_UNDER_MIN.getViewFields());

                    sWhere.addConstraint(TblsFormulationData.ViewReferencesStockUnderMin.CURRENT_STOCK, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    Object[][] referenceWithStockAvailableForUseUponMin = QueryUtilitiesEnums.getViewData(TblsFormulationData.ViewsInvTrackingData.REFERENCES_STOCK_UNDER_MIN,
                            TblsFormulationData.ViewsInvTrackingData.REFERENCES_STOCK_UNDER_MIN.getViewFields(),
                            sWhere, new String[]{TblsFormulationData.ViewReferencesStockUnderMin.CURRENT_STOCK.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceWithStockAvailableForUseUponMin[0][0].toString()))) {
                        for (Object[] currIncident : referenceWithStockAvailableForUseUponMin) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    jSummaryObj = new JSONObject();
                    jSummaryObj.put(RESPONSE_JSON_DATATABLE, jArr);
                    LPFrontEnd.servletReturnSuccess(request, response, jSummaryObj);
                    return;
                case REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN_STOCK:
                    sWhere = new SqlWhere();
                    category = LPNulls.replaceNull(argValues[0]).toString();
                    reference = LPNulls.replaceNull(argValues[1]).toString();
                    if (category.length() > 0) {
                        sWhere.addConstraint(TblsFormulationData.ViewReferencesAvailableForUseUnderMin.CATEGORY,
                                category.contains("*") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{category.split("\\|")}, "|");
                    }
                    if (reference.length() > 0) {
                        sWhere.addConstraint(TblsFormulationData.ViewReferencesAvailableForUseUnderMin.NAME,
                                reference.contains("*") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{reference.split("\\|")}, "|");
                    }
                    fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsFormulationData.ViewsInvTrackingData.REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN.getViewFields());

                    sWhere.addConstraint(TblsFormulationData.ViewReferencesAvailableForUseUnderMin.CURRENT_STOCK_AVAILABLE_FOR_USE, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    referenceWithStockAvailableForUseUponMin = QueryUtilitiesEnums.getViewData(TblsFormulationData.ViewsInvTrackingData.REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN,
                            TblsFormulationData.ViewsInvTrackingData.REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN.getViewFields(),
                            sWhere, new String[]{TblsFormulationData.ViewReferencesAvailableForUseUnderMin.CURRENT_STOCK_AVAILABLE_FOR_USE.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceWithStockAvailableForUseUponMin[0][0].toString()))) {
                        for (Object[] currIncident : referenceWithStockAvailableForUseUponMin) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    jSummaryObj = new JSONObject();
                    jSummaryObj.put(RESPONSE_JSON_DATATABLE, jArr);
                    LPFrontEnd.servletReturnSuccess(request, response, jSummaryObj);
                    return;
                case REFERENCE_WITH_CONTROL_ISSUES:
                    jSummaryObj = new JSONObject();
                    sWhere = new SqlWhere();
                    sWhere.addConstraint(TblsFormulationData.Lot.LOT_NAME, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    category = LPNulls.replaceNull(argValues[0]).toString();
                    reference = LPNulls.replaceNull(argValues[1]).toString();
                    if (category.length() > 0) {
                        sWhere.addConstraint(TblsFormulationData.ViewExpiredLots.CATEGORY,
                                category.contains("*") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{category.split("\\|")}, "|");
                    }
                    if (reference.length() > 0) {
                        sWhere.addConstraint(TblsFormulationData.ViewExpiredLots.REFERENCE,
                                reference.contains("*") ? SqlStatement.WHERECLAUSE_TYPES.LIKE : SqlStatement.WHERECLAUSE_TYPES.IN, new Object[]{reference.split("\\|")}, "|");
                    }
                    fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsFormulationData.ViewsInvTrackingData.LOTS_EXPIRED.getViewFields());
                    referenceWithControlIssues = QueryUtilitiesEnums.getViewData(TblsFormulationData.ViewsInvTrackingData.LOTS_EXPIRED,
                            TblsFormulationData.ViewsInvTrackingData.LOTS_EXPIRED.getViewFields(),
                            sWhere, new String[]{TblsFormulationData.ViewExpiredLots.EXPIRY_REASON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceWithControlIssues[0][0].toString()))) {
                        for (Object[] currIncident : referenceWithControlIssues) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                        jSummaryObj.put("has_expired_lots", true);
                    } else {
                        jSummaryObj.put("has_expired_lots", false);
                    }
                    jSummaryObj.put("expired_lots_list", jArr);

                    fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsFormulationData.ViewsInvTrackingData.REFERENCES_STOCK_UNDER_MIN.getViewFields());
                    sWhere = new SqlWhere();
                    sWhere.addConstraint(TblsFormulationData.ViewReferencesStockUnderMin.CURRENT_STOCK, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    referenceWithStockAvailableForUseUponMin = QueryUtilitiesEnums.getViewData(TblsFormulationData.ViewsInvTrackingData.REFERENCES_STOCK_UNDER_MIN,
                            TblsFormulationData.ViewsInvTrackingData.REFERENCES_STOCK_UNDER_MIN.getViewFields(),
                            sWhere, new String[]{TblsFormulationData.ViewReferencesStockUnderMin.CURRENT_STOCK.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceWithStockAvailableForUseUponMin[0][0].toString()))) {
                        for (Object[] currIncident : referenceWithStockAvailableForUseUponMin) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                        jSummaryObj.put("has_references_with_stock_upon_min", true);
                    } else {
                        jSummaryObj.put("has_references_with_stock_upon_min", false);
                    }
                    jSummaryObj.put("references_with_stock_upon_min_list", jArr);

                    fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsFormulationData.ViewsInvTrackingData.REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN.getViewFields());
                    sWhere = new SqlWhere();
                    sWhere.addConstraint(TblsFormulationData.ViewReferencesAvailableForUseUnderMin.CURRENT_STOCK_AVAILABLE_FOR_USE, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    referenceWithStockAvailableForUseUponMin = QueryUtilitiesEnums.getViewData(TblsFormulationData.ViewsInvTrackingData.REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN,
                            TblsFormulationData.ViewsInvTrackingData.REFERENCES_AVAILABLE_FOR_USE_UNDER_MIN.getViewFields(),
                            sWhere, new String[]{TblsFormulationData.ViewReferencesAvailableForUseUnderMin.CURRENT_STOCK_AVAILABLE_FOR_USE.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(referenceWithStockAvailableForUseUponMin[0][0].toString()))) {
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
                case COMPLETED_EVENTS_LAST_N_DAYS:
                    numDays = LPNulls.replaceNull(argValues[0]).toString();
                    category = LPNulls.replaceNull(argValues[1]).toString();
                    reference = LPNulls.replaceNull(argValues[2]).toString();
                    if (numDays.length() == 0) {
                        numDays = String.valueOf(7);
                    }
                    numDaysInt = 0 - Integer.valueOf(numDays);

                    sW = new SqlWhere();
                    sW.addConstraint(TblsFormulationData.LotQualification.COMPLETED_DECISION, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, new Object[]{}, null);
                    sW.addConstraint(TblsFormulationData.LotQualification.COMPLETED_ON, SqlStatement.WHERECLAUSE_TYPES.GREATER_THAN, new Object[]{LPDate.addDays(LPDate.getCurrentDateWithNoTime(), numDaysInt)}, null);
                    if (category.length() > 0) {
                        sW.addConstraint(TblsFormulationData.LotQualification.CATEGORY, SqlStatement.WHERECLAUSE_TYPES.IN, category.split("\\|"), "|");
                    }
                    if (reference.length() > 0) {
                        sW.addConstraint(TblsFormulationData.LotQualification.REFERENCE, SqlStatement.WHERECLAUSE_TYPES.IN, reference.split("\\|"), "|");
                    }

                    fieldsToRetrieve = getAllFieldNames(TblsFormulationData.TablesInvTrackingData.LOT_QUALIFICATION);
                    Object[][] invEventsCompletedLastDays = QueryUtilitiesEnums.getTableData(TblsFormulationData.TablesInvTrackingData.LOT_QUALIFICATION,
                            EnumIntTableFields.getAllFieldNamesFromDatabase(TblsFormulationData.TablesInvTrackingData.LOT_QUALIFICATION),
                            sW, new String[]{TblsFormulationData.LotQualification.COMPLETED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    jArr = new JSONArray();
                    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(invEventsCompletedLastDays[0][0].toString()))) {
                        for (Object[] currIncident : invEventsCompletedLastDays) {
                            JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currIncident);
                            jArr.add(jObj);
                        }
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
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
                case INVESTIGATION_QUALIFICATIONS_PENDING_DECISION:
                    JSONArray jArray = new JSONArray();
                    String statusClosed = DataProgramCorrectiveAction.ProgramCorrectiveActionStatuses.STATUS_CLOSED.getStatusCode();
                    String createInvCorrectiveAction = Parameter.getBusinessRuleProcedureFile(procReqInstance.getProcedureInstance(), InvTrackingEnums.InventoryTrackBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_QUALIFICATION.getAreaName(), InvTrackingEnums.InventoryTrackBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_QUALIFICATION.getTagName());
                    if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(createInvCorrectiveAction))) {
                        jObj = new JSONObject();
                        jObj.put(TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION.getTableName(), "corrective action not active!");
                        jArray.add(jObj);
                    } else {
                        fieldsToRetrieveObj = TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION.getTableFields();
                        Object[][] investigationResultsPendingDecision = QueryUtilitiesEnums.getTableData(TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION,
                                fieldsToRetrieveObj,
                                new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.STATUS.getName() + "<>"},
                                new String[]{statusClosed},
                                new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.LOT_NAME.getName()});
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationResultsPendingDecision[0][0].toString())) {
                            LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());
                        }

                        for (Object[] curRow : investigationResultsPendingDecision) {
                            jObj = LPJson.convertArrayRowToJSONObject(getAllFieldNames(fieldsToRetrieveObj), curRow);
                            jArray.add(jObj);
                        }
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, jArray);
                    break;
                case INVESTIGATION_DETAIL_FOR_GIVEN_INVESTIGATION:
                    jArray = new JSONArray();
                    createInvCorrectiveAction = Parameter.getBusinessRuleProcedureFile(procReqInstance.getProcedureInstance(), InvTrackingEnums.InventoryTrackBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_QUALIFICATION.getAreaName(), InvTrackingEnums.InventoryTrackBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_QUALIFICATION.getTagName());
                    if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(createInvCorrectiveAction))) {
                        jObj = new JSONObject();
                        jObj.put(TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION.getTableName(), "corrective action not active!");
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
                    if (Boolean.FALSE.equals(isProgramCorrectiveActionEnable(procReqInstance.getProcedureInstance()))){
                        jObj = new JSONObject();
                        jObj.put("corrective_action_not_active", "please check the business rule, "+
                            Parameter.getBusinessRuleProcedureFile(procReqInstance.getProcedureInstance(), DataProgramCorrectiveAction.DataProgramCorrectiveActionBusinessRules.ACTION_MODE.getAreaName(), DataProgramCorrectiveAction.DataProgramCorrectiveActionBusinessRules.ACTION_MODE.getTagName()));
                        jArray.add(jObj);
                        LPFrontEnd.servletReturnSuccess(request, response, jArray);
                    }
                    fieldsToRetrieveObj = TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION.getTableFields();
                    Object[][] investigationResultsPendingDecision = QueryUtilitiesEnums.getTableData(TblsInvTrackingProcedure.TablesInvTrackingProcedure.INVENTORY_CORRECTIVE_ACTION,
                            fieldsToRetrieveObj,
                            new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.STATUS.getName() + "<>"},
                            new Object[]{statusClosed},
                            new String[]{TblsInvTrackingProcedure.InventoryCorrectiveAction.CREATED_ON.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationResultsPendingDecision[0][0].toString())) {
                        LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());
                    }
                    for (Object[] curRow : investigationResultsPendingDecision) {
                        jObj = LPJson.convertArrayRowToJSONObject(getAllFieldNames(fieldsToRetrieveObj), curRow);
                        jArray.add(jObj);
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArray);
                    break;
                case GET_LOT_ATTACHMENTS:
                    lotName = argValues[0].toString();
                    Integer lotQualifId = LPNulls.replaceNull(argValues[1]).toString().length() > 0 ? (Integer) argValues[1] : null;
                    jArr = lotAttachment(lotName, lotQualifId, null);
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return; */
                default:
            }
        } catch (NumberFormatException e2) {
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
/*    public static JSONArray lotAttachment(String lotName, Integer qualifId, String alternativeProcInstanceName) {
        String[] fieldsToRetrieve = getAllFieldNames(TblsFormulationData.TablesInvTrackingData.FORMULA_ATTACHMENT, alternativeProcInstanceName);
        String[] wFldN = new String[]{TblsFormulationData.LotAttachments.LOT_NAME.getName(), TblsFormulationData.LotAttachments.REMOVED.getName()};
        Object[] wFldV = new Object[]{lotName, false};
        if (qualifId != null && qualifId.toString().length() > 0) {
            wFldN = LPArray.addValueToArray1D(wFldN, TblsFormulationData.LotAttachments.QUALIF_ID.getName());
            wFldV = LPArray.addValueToArray1D(wFldV, qualifId);
        }
        Object[][] instrumentFamily = QueryUtilitiesEnums.getTableData(TblsFormulationData.TablesInvTrackingData.FORMULA_ATTACHMENT,
                EnumIntTableFields.getAllFieldNamesFromDatabase(TblsFormulationData.TablesInvTrackingData.FORMULA_ATTACHMENT, alternativeProcInstanceName),
                wFldN, wFldV, new String[]{TblsFormulationData.LotAttachments.LOT_NAME.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()}, alternativeProcInstanceName);
        JSONArray jArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(instrumentFamily[0][0].toString()))) {
            for (Object[] currInstr : instrumentFamily) {
                JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstr);
                jArr.add(jObj);
            }
        }
        return jArr;
    }
*/
    static JSONObject methodValidationData(String curProjName, String paramName, String analyticalParameter){
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        String procInstanceName=procReqInstance.getProcedureInstance();
        JSONObject jObj=new JSONObject();
        switch (paramName.toLowerCase()){
            case "lineality":
                jObj.put("title_en", "Lineality Assay");
                jObj.put("title_es", "Ensayo linealidad");
                break;
            case "repeteability":
                jObj.put("title_en", "Repeatibility Assay");
                jObj.put("title_es", "Ensayo repetibilidad");
                break;
        }     
        Object[][] samplesInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsData.TablesData.SAMPLE.getRepositoryName()), TblsData.TablesData.SAMPLE.getTableName(),
                new String[]{TblsMethodValidationData.Sample.PROJECT.getName(), TblsMethodValidationData.Sample.PARAMETER_NAME.getName()}, new Object[]{curProjName, paramName}, 
                new String[]{TblsData.Sample.SAMPLE_ID.getName()},
                new String[]{TblsData.Sample.SAMPLE_ID.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(samplesInfo[0][0].toString()))
            return jObj;
        int i=0;
        JSONArray resultsJArr=new JSONArray();
        String finalResult="";
        for (Object[] curSmp: samplesInfo){
            JSONObject sampleJObj=new JSONObject();
            Object[][] testsInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsData.TablesData.SAMPLE_ANALYSIS.getRepositoryName()), TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(),
                    new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName()}, new Object[]{Integer.valueOf(curSmp[0].toString())}, 
                    new String[]{TblsData.SampleAnalysis.TEST_ID.getName()},
                    new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName()});
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(testsInfo[0][0].toString()))
                return jObj;            
            i++;
            for (Object[] curTst: testsInfo){
                int iInj=0;
                String[] fldsToGetSmpRslt = new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(),                 
                    TblsData.SampleAnalysisResult.TEST_ID.getName(), TblsData.SampleAnalysisResult.RESULT_ID.getName(),
                    TblsData.SampleAnalysisResult.PARAM_NAME.getName(), TblsData.SampleAnalysisResult.PARAM_TYPE.getName(),
                    TblsData.SampleAnalysisResult.PRETTY_VALUE.getName()};
                Object[][] resultsInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getRepositoryName()), TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(),
                        new String[]{TblsData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{Integer.valueOf(curTst[0].toString())}, 
                        fldsToGetSmpRslt,
                        new String[]{TblsData.SampleAnalysisResult.SAMPLE_ID.getName(), TblsData.SampleAnalysisResult.TEST_ID.getName()});
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(resultsInfo[0][0].toString()))
                    return jObj;
                jObj.put("total_samples", samplesInfo.length);                
                //jObj.put("final_results", finalResultsJObj);
                
                for (Object[] curRslt: resultsInfo){
                    if ("CALC".equalsIgnoreCase(curRslt[4].toString())){
                        finalResult=curRslt[5].toString();
                    }else{
                        iInj++;
                        sampleJObj=LPJson.convertArrayRowToJSONObjectNoJsonSimple(fldsToGetSmpRslt, curRslt);
                        sampleJObj.put("order_number", i);
                        sampleJObj.put("name", "sample "+i);
                        sampleJObj.put("injection", "Inj " + iInj);
                        sampleJObj.put("result", curRslt[5]);
                        sampleJObj.put("final_result", "");
                        resultsJArr.put(sampleJObj);
                    }                    
                }
            }
            JSONObject objToModify = resultsJArr.getJSONObject(resultsJArr.length()- 1); // Last added object
            objToModify.put("final_result", finalResult);  // Update the final result
            resultsJArr.put(resultsJArr.length()- 1, objToModify);
            jObj.put("results", resultsJArr);  
        }
        return jObj;        
    }
    static JSONArray fakeMethodValidationData(){
        JSONArray jMainArr=new JSONArray();
        
        JSONObject jObj=new JSONObject();
        jObj.put("title_en", "Repeatibility Assay");
        jObj.put("title_es", "Ensayo repetibilidad");
        JSONObject finalResultsJObj=new JSONObject();
        finalResultsJObj.put("average", 98.667);
        finalResultsJObj.put("standard_deviation", 0.776);
        finalResultsJObj.put("c_v", 0.8);
        finalResultsJObj.put("total_samples", 6);
        jObj.put("final_results", finalResultsJObj);
        JSONArray resultsJArr=new JSONArray();
        
        JSONObject sampleJObj=new JSONObject();
        int i=0;
        sampleJObj.put("order_number", i++);
        sampleJObj.put("name", "sample 1");
        sampleJObj.put("injection", "Inj 1");
        sampleJObj.put("result", 99.6);
        sampleJObj.put("final_result", "");
        resultsJArr.put(sampleJObj);
        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("name", "");
        sampleJObj.put("injection", "Inj 2");
        sampleJObj.put("result", 99.7);
        sampleJObj.put("final_result", "99.65");
        resultsJArr.put(sampleJObj);
        
        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("name", "sample 2");
        sampleJObj.put("injection", "Inj 1");
        sampleJObj.put("result", 98.2);
        sampleJObj.put("final_result", "");
        resultsJArr.put(sampleJObj);
        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("name", "");
        sampleJObj.put("injection", "Inj 2");
        sampleJObj.put("result", 98.4);
        sampleJObj.put("final_result", "98.30");
        resultsJArr.put(sampleJObj);
        
        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("name", "sample 3");
        sampleJObj.put("injection", "Inj 1");
        sampleJObj.put("result", 97.9);
        sampleJObj.put("final_result", "");
        resultsJArr.put(sampleJObj);
        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("name", "");
        sampleJObj.put("injection", "Inj 2");
        sampleJObj.put("result", 98.0);
        sampleJObj.put("final_result", "97.95");
        resultsJArr.put(sampleJObj);

        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("name", "sample 4");
        sampleJObj.put("injection", "Inj 1");
        sampleJObj.put("result", 99.9);
        sampleJObj.put("final_result", "");
        resultsJArr.put(sampleJObj);
        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("name", "");
        sampleJObj.put("injection", "Inj 2");
        sampleJObj.put("result", 99.0);
        sampleJObj.put("final_result", "99.45");
        resultsJArr.put(sampleJObj);

        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("name", "sample 5");
        sampleJObj.put("injection", "Inj 1");
        sampleJObj.put("result", 97.7);
        sampleJObj.put("final_result", "");
        resultsJArr.put(sampleJObj);
        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("name", "");
        sampleJObj.put("injection", "Inj 2");
        sampleJObj.put("result", 97.9);
        sampleJObj.put("final_result", "97.80");
        resultsJArr.put(sampleJObj);

        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("name", "sample 6");
        sampleJObj.put("injection", "Inj 1");
        sampleJObj.put("result", 98.9);
        sampleJObj.put("final_result", "");
        resultsJArr.put(sampleJObj);
        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("name", "");
        sampleJObj.put("injection", "Inj 2");
        sampleJObj.put("result", 98.8);
        sampleJObj.put("final_result", "98.85");
        resultsJArr.put(sampleJObj);        
        jObj.put("results", resultsJArr);        
        jMainArr.put(jObj);
        
        jObj=new JSONObject();
        jObj.put("title_en", "Lineality Assay");
        jObj.put("title_es", "Ensayo linealidad");
        
        resultsJArr=new JSONArray();
        JSONArray chartResultsJArr=new JSONArray();
        
        i=0;
        sampleJObj=new JSONObject();
        JSONObject chartJObj=new JSONObject();
        sampleJObj.put("order_number", i++);

        sampleJObj.put("sample_id", 25);
        sampleJObj.put("test_id", 2);
        sampleJObj.put("result_id", 1);

        sampleJObj.put("theoretical_value", "10");
        sampleJObj.put("name", "sample 1");
        sampleJObj.put("injection", "Inj 1");
        sampleJObj.put("result", 9.9);
        sampleJObj.put("final_result", "");
        resultsJArr.put(sampleJObj);
        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);

        sampleJObj.put("sample_id", 25);
        sampleJObj.put("test_id", 2);
        sampleJObj.put("result_id", 2);

        sampleJObj.put("theoretical_value", "");
        sampleJObj.put("name", "");
        sampleJObj.put("injection", "Inj 2");
        sampleJObj.put("result", 9.8);
        sampleJObj.put("final_result", "9.85");
        chartJObj.put("theoretical_value", "10");
        chartJObj.put("value", "9.85");
        chartResultsJArr.put(chartJObj);
        resultsJArr.put(sampleJObj);
        
        sampleJObj=new JSONObject();
        chartJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("theoretical_value", "25");
        sampleJObj.put("name", "sample 2");
        sampleJObj.put("injection", "Inj 1");
        sampleJObj.put("result", 24.9);
        sampleJObj.put("final_result", "");
        resultsJArr.put(sampleJObj);
        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("theoretical_value", "");
        sampleJObj.put("name", "");
        sampleJObj.put("injection", "Inj 2");
        sampleJObj.put("result", 25.0);
        sampleJObj.put("final_result", "24.95");
        chartJObj.put("theoretical_value", "25");
        chartJObj.put("value", "24.95");
        chartResultsJArr.put(chartJObj);
        resultsJArr.put(sampleJObj);
        
        sampleJObj=new JSONObject();
        chartJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("theoretical_value", "50");
        sampleJObj.put("name", "sample 3");
        sampleJObj.put("injection", "Inj 1");
        sampleJObj.put("result", 49.7);
        sampleJObj.put("final_result", "");
        resultsJArr.put(sampleJObj);
        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("theoretical_value", "");
        sampleJObj.put("name", "");
        sampleJObj.put("injection", "Inj 2");
        sampleJObj.put("result", 50.2);
        sampleJObj.put("final_result", "49.95");
        chartJObj.put("theoretical_value", "50");
        chartJObj.put("value", "49.95");
        chartResultsJArr.put(chartJObj);
        resultsJArr.put(sampleJObj);

        sampleJObj=new JSONObject();
        chartJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("theoretical_value", "75");
        sampleJObj.put("name", "sample 4");
        sampleJObj.put("injection", "Inj 1");
        sampleJObj.put("result", 76.0);
        sampleJObj.put("final_result", "");
        resultsJArr.put(sampleJObj);
        sampleJObj=new JSONObject();        
        sampleJObj.put("order_number", i++);
        sampleJObj.put("theoretical_value", "");
        sampleJObj.put("name", "");
        sampleJObj.put("injection", "Inj 2");
        sampleJObj.put("result", 75.8);
        sampleJObj.put("final_result", "75.90");
        chartJObj.put("theoretical_value", "75");
        chartJObj.put("value", "75.90");
        chartResultsJArr.put(chartJObj);
        resultsJArr.put(sampleJObj);

        sampleJObj=new JSONObject();
        chartJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("theoretical_value", "100");
        sampleJObj.put("name", "sample 5");
        sampleJObj.put("injection", "Inj 1");
        sampleJObj.put("result", 99.8);
        sampleJObj.put("final_result", "");
        resultsJArr.put(sampleJObj);
        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("theoretical_value", "");
        sampleJObj.put("name", "");
        sampleJObj.put("injection", "Inj 2");
        sampleJObj.put("result", 98.9);
        sampleJObj.put("final_result", "99.35");
        chartJObj.put("theoretical_value", "100");
        chartJObj.put("value", "99.35");
        chartResultsJArr.put(chartJObj);

        sampleJObj=new JSONObject();
        chartJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("theoretical_value", "120");        
        sampleJObj.put("name", "sample 6");
        sampleJObj.put("injection", "Inj 1");
        sampleJObj.put("result", 119.7);
        sampleJObj.put("final_result", "");
        resultsJArr.put(sampleJObj);
        sampleJObj=new JSONObject();
        sampleJObj.put("order_number", i++);
        sampleJObj.put("theoretical_value", "");
        sampleJObj.put("name", "");
        sampleJObj.put("injection", "Inj 2");
        sampleJObj.put("result", 119.0);
        sampleJObj.put("final_result", "119.35");
        chartJObj.put("theoretical_value", "120");
        chartJObj.put("value", "119.35");
        chartResultsJArr.put(chartJObj);
        finalResultsJObj=new JSONObject();
        finalResultsJObj.put("total_samples", 6);
        jObj.put("final_results", finalResultsJObj);        
        resultsJArr.put(sampleJObj);        
        jObj.put("results", resultsJArr);  
        jObj.put("chart_results", chartResultsJArr);  
        
        jMainArr.put(jObj);
        
        return jMainArr;
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
