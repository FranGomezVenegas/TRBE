/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.inspectionlot.rawmaterial.definition;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.moduleenvmonit.EnvMonAPIqueries.JSON_TAG_SPEC_DEFINITION;
import com.labplanet.servicios.modulesample.ClassSampleQueries;
import com.labplanet.servicios.modulesample.SampleAPIParams;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlStatementEnums;
import databases.SqlWhere;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsProcedure;
import functionaljavaa.certification.AnalysisMethodCertifQueries;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.materialspec.SpecFrontEndUtilities;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.parameter.Parameter.isTagValueOneOfEnableOnes;
import functionaljavaa.responserelatedobjects.RelatedObjects;
import java.util.Arrays;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPParadigm;
import lbplanet.utilities.LPPlatform;
import static module.inspectionlot.rawmaterial.definition.InspLotQueries.configMaterialStructure;
import static module.inspectionlot.rawmaterial.definition.InspLotQueries.dataSampleStructure;
import module.inspectionlot.rawmaterial.definition.InspLotRMEnums.InspLotRMBusinessRules;
import module.inspectionlot.rawmaterial.logic.DataInsLotsCorrectiveAction;
import module.inspectionlot.rawmaterial.logic.DataInspLotRMCertificate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntQueriesObj;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViewFields;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.definition.apis.ReqProcedureDefinitionQueries;
import trazit.queries.QueryUtilities;
import trazit.session.ProcedureRequestSession;
import trazit.queries.QueryUtilitiesEnums;

/**
 *
 * @author User
 */
public class ClassInspLotRMQueries implements EnumIntQueriesObj {

    private Object[] messageDynamicData = new Object[]{};
    private RelatedObjects relatedObj = RelatedObjects.getInstanceForActions();
    private Boolean endpointExists = true;
    private Object[] diagnostic = new Object[0];
    private Boolean functionFound = false;

    private Boolean isSuccess = false;
    private JSONObject responseSuccessJObj = null;
    private JSONArray responseSuccessJArr = null;

    public ClassInspLotRMQueries(HttpServletRequest request, HttpServletResponse response, InspLotRMEnums.InspLotRMQueriesAPIEndpoints endPoint) {
        request = LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, false);
        String actionName = procReqInstance.getActionName();
        String language = procReqInstance.getLanguage();
        String procInstanceName = procReqInstance.getProcedureInstance();
        try {
            if (Boolean.TRUE.equals(procReqInstance.getHasErrors())) {
                procReqInstance.killIt();
                LPFrontEnd.servletReturnResponseError(request, null, procReqInstance.getErrorMessage(), new Object[]{procReqInstance.getErrorMessage(), ClassInspLotRMQueries.class.getSimpleName()}, procReqInstance.getLanguage(), null);
                return;
            }
            this.functionFound = true;
            Object[] argValues = LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());
            this.functionFound = true;
            if (argValues.length > 0 && LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(argValues[0]).toString())) {
                this.diagnostic = (Object[]) argValues[1];
                this.messageDynamicData = new Object[]{argValues[2].toString()};
                return;
            }
            switch (endPoint) {
                case GET_LOT_INFO:
                    String lotName = LPNulls.replaceNull(argValues[0]).toString();
                    String fieldsToRetrieveStr = LPNulls.replaceNull(argValues[1].toString());
                    Boolean includesSamplesInfo = Boolean.valueOf(LPNulls.replaceNull(argValues[2]).toString());
                    Boolean includesMaterialInfo = Boolean.valueOf(LPNulls.replaceNull(argValues[3]).toString());
                    if (Boolean.TRUE.equals(includesMaterialInfo) && fieldsToRetrieveStr.length() > 0 && Boolean.FALSE.equals(fieldsToRetrieveStr.contains(TblsInspLotRMData.Lot.MATERIAL_NAME.getName()))) {
                        fieldsToRetrieveStr = fieldsToRetrieveStr + "|" + TblsInspLotRMData.Lot.MATERIAL_NAME.getName();
                    }

                    JSONObject lotJsonObj = new JSONObject();
                    JSONArray lotsJsonArr = new JSONArray();
                    SqlWhere sW=new SqlWhere();
                    if ("ALL".equalsIgnoreCase(lotName))
                        sW=new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.LOT, new String[]{TblsInspLotRMData.Lot.ACTIVE.getName()}, new Object[]{true});
                    else 
                        sW=new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.LOT, new String[]{TblsInspLotRMData.Lot.NAME.getName()}, new Object[]{lotName});
                    EnumIntTableFields[] tableFieldsLot = TblsInspLotRMData.TablesInspLotRMData.LOT.getTableFields();
                    String[] fieldsToRetrieveLot = EnumIntTableFields.getAllFieldNames(tableFieldsLot);
                    Object[][] lotInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.LOT,
                            tableFieldsLot, sW,
                            new String[]{TblsInspLotRMData.Lot.NAME.getName()}, null);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) {
                        lotJsonObj.put(GlobalAPIsParams.LBL_ERROR, Arrays.toString(lotInfo[0]));
                        lotsJsonArr.add(lotJsonObj);
                        Rdbms.closeRdbms();
                        LPFrontEnd.servletReturnSuccess(request, response, lotsJsonArr);
                        return;
                    }
                    for (Object[] currLot : lotInfo) {

                        JSONObject jLotInfoObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveLot, currLot);
                        String currMaterial = "";
                        if (LPArray.valueInArray(fieldsToRetrieveLot, TblsInspLotRMData.Lot.MATERIAL_NAME.getName())) {
                            currMaterial = currLot[LPArray.valuePosicInArray(fieldsToRetrieveLot, TblsInspLotRMData.Lot.MATERIAL_NAME.getName())].toString();
                            if (Boolean.TRUE.equals(includesSamplesInfo) && currMaterial != null && currMaterial.length() > 0) {
                                jLotInfoObj.put(TblsData.TablesData.SAMPLE.getTableName(), dataSampleStructure(lotName, null, null, new String[]{TblsInspLotRMData.Sample.SAMPLE_ID.getName()}, true, true));
                            }
                            if (Boolean.TRUE.equals(includesMaterialInfo) && currMaterial != null && currMaterial.length() > 0) {
                                jLotInfoObj.put(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL.getTableName(), configMaterialStructure(currMaterial, null, new String[]{TblsInspLotRMConfig.Material.NAME.getName()}, true, true, true));
                            }
                        }
                        JSONArray materialUomInfo = configMaterialStructure(currMaterial, TblsInspLotRMConfig.Material.DEFAULT_UOM.getName() + "|" + TblsInspLotRMConfig.Material.ALTERNATIVE_UOMS.getName(), new String[]{TblsInspLotRMConfig.Material.NAME.getName()}, false, false, false);

                        lotJsonObj.put("lot_info", jLotInfoObj);
                        lotJsonObj.put("lot_name", jLotInfoObj.get(TblsInspLotRMData.Lot.NAME.getName()));

                        EnumIntTableFields[] tableFieldsBulk = TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableFields();
                        String[] fieldsToRetrieveBulk = EnumIntTableFields.getAllFieldNames(tableFieldsBulk);
                        lotInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK,
                                tableFieldsBulk, new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK, new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName()}, new Object[]{lotName}),
                                new String[]{TblsInspLotRMData.LotBulk.LOT_NAME.getName()}, null);

                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) {
                            JSONObject jLotSectionInfoObj = new JSONObject();
                            lotJsonObj.put(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(), jLotSectionInfoObj);
                        } else {
                            JSONArray lotInfoJsonArr = new JSONArray();
                            for (Object[] curRow : lotInfo) {
                                JSONObject jLotSectionInfoObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveBulk, curRow);
                                for (Iterator it = materialUomInfo.iterator(); it.hasNext();) {
                                    JSONObject matInfoObj = (JSONObject) it.next();

                                    jLotSectionInfoObj.put(TblsInspLotRMConfig.Material.DEFAULT_UOM.getName(), (String) matInfoObj.get(TblsInspLotRMConfig.Material.DEFAULT_UOM.getName()));
                                    jLotSectionInfoObj.put(TblsInspLotRMConfig.Material.ALTERNATIVE_UOMS.getName(), (String) matInfoObj.get(TblsInspLotRMConfig.Material.ALTERNATIVE_UOMS.getName()));
                                }
                                lotInfoJsonArr.add(jLotSectionInfoObj);
                            }
                            lotJsonObj.put(TblsInspLotRMData.TablesInspLotRMData.LOT_BULK.getTableName(), lotInfoJsonArr);
                        }
                        tableFieldsBulk = TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableFields();
                        fieldsToRetrieveBulk = EnumIntTableFields.getAllFieldNames(tableFieldsBulk);
                        lotInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN,
                                tableFieldsBulk, new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN, new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName()}, new Object[]{lotName}),
                                new String[]{TblsInspLotRMData.InventoryRetain.LOT_NAME.getName()}, null);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) {
                            JSONObject jLotSectionInfoObj = new JSONObject();
                            lotJsonObj.put(TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(), jLotSectionInfoObj);
                        } else {
                            JSONArray lotInfoJsonArr = new JSONArray();
                            for (Object[] curRow : lotInfo) {
                                JSONObject jLotSectionInfoObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveBulk, curRow);
                                lotInfoJsonArr.add(jLotSectionInfoObj);
                            }
                            lotJsonObj.put(TblsInspLotRMData.TablesInspLotRMData.INVENTORY_RETAIN.getTableName(), lotInfoJsonArr);
                        }

                        tableFieldsBulk = TblsInspLotRMData.TablesInspLotRMData.LOT_NOT_ANALYZED_RESULT.getTableFields();
                        fieldsToRetrieveBulk = EnumIntTableFields.getAllFieldNames(tableFieldsBulk);
                        lotInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.LOT_NOT_ANALYZED_RESULT,
                                tableFieldsBulk, new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.LOT_NOT_ANALYZED_RESULT, new String[]{TblsInspLotRMData.LotNotAnalyzedResult.LOT_NAME.getName()}, new Object[]{lotName}),
                                new String[]{TblsInspLotRMData.LotNotAnalyzedResult.LOT_NAME.getName()}, null);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotInfo[0][0].toString())) {
                            JSONObject jLotSectionInfoObj = new JSONObject();
                            lotJsonObj.put(TblsInspLotRMData.TablesInspLotRMData.LOT_NOT_ANALYZED_RESULT.getTableName(), jLotSectionInfoObj);
                        } else {
                            JSONArray lotInfoJsonArr = new JSONArray();
                            for (Object[] curRow : lotInfo) {
                                JSONObject jLotSectionInfoObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveBulk, curRow);
                                lotInfoJsonArr.add(jLotSectionInfoObj);
                            }
                            lotJsonObj.put(TblsInspLotRMData.TablesInspLotRMData.LOT_NOT_ANALYZED_RESULT.getTableName(), lotInfoJsonArr);
                        }

                        EnumIntTableFields[] tableFieldsSample = TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableFields();
                        String[] fieldsToRetrieveSample = EnumIntTableFields.getAllFieldNames(tableFieldsSample);
                        Object[][] lotSampleInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.SAMPLE,
                                tableFieldsSample, new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.SAMPLE, new String[]{TblsInspLotRMData.Sample.LOT_NAME.getName()}, new Object[]{lotName}),
                                new String[]{TblsInspLotRMData.Sample.SAMPLE_ID.getName()}, null);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotSampleInfo[0][0].toString())) {
                            JSONObject jLotSampleSectionInfoObj = new JSONObject();
                            lotJsonObj.put(TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableName(), jLotSampleSectionInfoObj);
                        } else {
                            JSONArray jLotSampleSectionInfoArr = new JSONArray();
                            for (Object[] curRow : lotSampleInfo) {
                                JSONObject jLotSampleSectionInfoObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveSample, curRow);
                                Object sampleId = jLotSampleSectionInfoObj.get(TblsInspLotRMData.Sample.SAMPLE_ID.getName());
                                EnumIntTableFields[] tableFieldsSmpAna = TblsData.TablesData.SAMPLE_ANALYSIS.getTableFields();
                                String[] fieldsToRetrieveSmpAna = EnumIntTableFields.getAllFieldNames(tableFieldsSmpAna);
                                Object[][] sampleAnaInfo = QueryUtilitiesEnums.getTableData(TblsData.TablesData.SAMPLE_ANALYSIS,
                                        tableFieldsSmpAna,
                                        new SqlWhere(TblsData.TablesData.SAMPLE_ANALYSIS, new String[]{TblsInspLotRMData.Sample.SAMPLE_ID.getName()}, new Object[]{Integer.valueOf(sampleId.toString())}),
                                        new String[]{TblsData.SampleAnalysis.SAMPLE_ID.getName()}, null);
                                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAnaInfo[0][0].toString())) {
                                    JSONObject sampleAnaSectionInfoObj = new JSONObject();
                                    jLotSampleSectionInfoObj.put(TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), sampleAnaSectionInfoObj);
                                } else {
                                    JSONArray sampleAnaJsonArr = new JSONArray();
                                    for (Object[] curRow2 : sampleAnaInfo) {
                                        JSONObject sampleAnaSectionInfoObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveSmpAna, curRow2);
                                        Object testId = sampleAnaSectionInfoObj.get(TblsData.SampleAnalysis.TEST_ID.getName());
                                        EnumIntTableFields[] tableFieldsSmpAnaRes = TblsInspLotRMData.TablesInspLotRMData.SAMPLE_ANALYSIS_RESULT.getTableFields();
                                        String[] fieldsToRetrieveSmpAnaRes = EnumIntTableFields.getAllFieldNames(tableFieldsSmpAnaRes);
                                        Object[][] sampleAnaResInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.SAMPLE_ANALYSIS_RESULT,
                                                tableFieldsSmpAnaRes,
                                                new SqlWhere(TblsInspLotRMData.TablesInspLotRMData.SAMPLE_ANALYSIS_RESULT,
                                                        new String[]{TblsInspLotRMData.SampleAnalysisResult.TEST_ID.getName()}, new Object[]{Integer.valueOf(testId.toString())}),
                                                new String[]{TblsInspLotRMData.SampleAnalysisResult.RESULT_ID.getName()}, null);
                                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAnaResInfo[0][0].toString())) {
                                            JSONObject sampleAnaResSectionInfoObj = new JSONObject();
                                            jLotSampleSectionInfoObj.put(TblsInspLotRMData.TablesInspLotRMData.SAMPLE_ANALYSIS_RESULT.getTableName(), sampleAnaResSectionInfoObj);
                                        } else {
                                            JSONArray sampleAnaResJsonArr = new JSONArray();
                                            for (Object[] curRow3 : sampleAnaResInfo) {
                                                JSONObject sampleAnaResSectionInfoObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieveSmpAnaRes, curRow3);
                                                sampleAnaResJsonArr.add(sampleAnaResSectionInfoObj);
                                            }
                                            sampleAnaSectionInfoObj.put(TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName(), sampleAnaResJsonArr);

                                            sampleAnaJsonArr.add(sampleAnaSectionInfoObj);
                                        }
                                        jLotSampleSectionInfoObj.put(TblsData.TablesData.SAMPLE_ANALYSIS.getTableName(), sampleAnaJsonArr);
                                    }
                                }

                                jLotSampleSectionInfoArr.add(jLotSampleSectionInfoObj);
                            }
                            lotJsonObj.put(TblsInspLotRMData.TablesInspLotRMData.SAMPLE.getTableName(), jLotSampleSectionInfoArr);
                        }
                        Object specCode = jLotInfoObj.get(TblsInspLotRMData.Lot.SPEC_CODE.getName());
                        Object specConfigVersion = jLotInfoObj.get(TblsInspLotRMData.Lot.SPEC_CODE_VERSION.getName());
                        Object specVariationName = jLotInfoObj.get(TblsInspLotRMData.Lot.SPEC_VARIATION_NAME.getName());

                        JSONObject specDefinition = new JSONObject();
                        String[] specFlds = null;
                        JSONArray specLimitsInfo = null;
                        if (Boolean.FALSE.equals(specCode == null || specCode == "" || specConfigVersion == null || "".equals(specConfigVersion.toString()))) {
                            JSONObject specInfo = SpecFrontEndUtilities.configSpecInfo(procReqInstance, TblsCnfg.TablesConfig.SPEC, (String) specCode, (Integer) specConfigVersion,
                                    null, null);
                            specDefinition.put(TblsCnfg.TablesConfig.SPEC.getTableName(), specInfo);
                            specFlds = new String[]{TblsCnfg.SpecLimits.VARIATION_NAME.getName(), TblsCnfg.SpecLimits.ANALYSIS.getName(),
                                TblsCnfg.SpecLimits.METHOD_NAME.getName(), TblsCnfg.SpecLimits.PARAMETER.getName(), TblsCnfg.SpecLimits.LIMIT_ID.getName(), TblsInspLotRMConfig.SpecLimits.ADD_IN_COA.getName(),
                                TblsCnfg.SpecLimits.SPEC_TEXT_EN.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_RED_AREA_EN.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_YELLOW_AREA_EN.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_GREEN_AREA_EN.getName(),
                                TblsCnfg.SpecLimits.SPEC_TEXT_ES.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_RED_AREA_ES.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_YELLOW_AREA_ES.getName(), TblsCnfg.SpecLimits.SPEC_TEXT_GREEN_AREA_ES.getName()};
                            specLimitsInfo = SpecFrontEndUtilities.configSpecLimitsInfo(procReqInstance, TblsInspLotRMConfig.TablesInspLotRMConfig.SPEC_LIMITS, specCode.toString(), (Integer) specConfigVersion,
                                    specVariationName.toString(), specFlds, new String[]{TblsInspLotRMConfig.SpecLimits.COA_ORDER.getName(), TblsInspLotRMConfig.SpecLimits.ANALYSIS.getName()});
                            specDefinition.put(TblsCnfg.TablesConfig.SPEC_LIMITS.getTableName(), specLimitsInfo);
                            lotJsonObj.put(JSON_TAG_SPEC_DEFINITION, specDefinition);
                        }
                        lotJsonObj.put("lot_coa", DataInspLotRMCertificate.getLotCoAInfo(lotName, "CC", jLotInfoObj, fieldsToRetrieveSample, lotSampleInfo, specFlds, specLimitsInfo));

                        lotsJsonArr.add(lotJsonObj);
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, lotsJsonArr);
                    break;
                case GET_LOTS_PENDING_USAGE_DECISION:
                    lotName = LPNulls.replaceNull(argValues[0]).toString();
                    fieldsToRetrieveStr = LPNulls.replaceNull(argValues[1]).toString();
                    /*                    if (LPNulls.replaceNull(fieldsToRetrieveStr).length() == 0) {
                        fieldsToRetrieve = EnumIntTableFields.getAllFieldNames(TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT.getTableFields());
                    } else {
                        fieldsToRetrieve = fieldsToRetrieveStr.split("\\|");
                    }
                     */
                    EnumIntTableFields[] fieldsToRetrieveObj = TblsInspLotRMData.TablesInspLotRMData.LOT.getTableFields();
                    Object[][] lotsPendingDecisionInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMData.TablesInspLotRMData.LOT,
                            fieldsToRetrieveObj,
                            new String[]{TblsInspLotRMData.Lot.READY_FOR_REVISION.getName()}, new Object[]{true},
                            new String[]{TblsInspLotRMData.Lot.CREATED_ON.getName()});
                    JSONArray jArr = new JSONArray();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(lotsPendingDecisionInfo[0][0].toString())) {
                        LPFrontEnd.servletReturnSuccess(request, response, jArr);
                        return;
                    }
                    for (Object[] curRow : lotsPendingDecisionInfo) {
                        JSONObject jObj = LPJson.convertArrayRowToJSONObject(getAllFieldNames(fieldsToRetrieveObj), curRow);
                        jArr.add(jObj);
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    break;

                case GET_LOT_SAMPLES_INFO:
                    lotName = LPNulls.replaceNull(argValues[0]).toString();
                    fieldsToRetrieveStr = LPNulls.replaceNull(argValues[1].toString());
                    Boolean includesSampleAnalysisInfo = Boolean.valueOf(LPNulls.replaceNull(argValues[2]).toString());
                    Boolean includesSampleAnalysisResultInfo = Boolean.valueOf(LPNulls.replaceNull(argValues[3]).toString());
                    jArr = new JSONArray();
                    jArr.add(dataSampleStructure(lotName, null, fieldsToRetrieveStr, new String[]{TblsInspLotRMData.Sample.SAMPLE_ID.getName()}, includesSampleAnalysisInfo, includesSampleAnalysisResultInfo));
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    break;
                case GET_LOT_AUDIT:
                    String[] fieldsToRetrieve = null;
                    lotName = LPNulls.replaceNull(argValues[0]).toString();
                    fieldsToRetrieveStr = LPNulls.replaceNull(argValues[1]).toString();
                    if (LPNulls.replaceNull(fieldsToRetrieveStr).length() == 0) {
                        fieldsToRetrieve = EnumIntTableFields.getAllFieldNames(TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT.getTableFields());
                    } else {
                        fieldsToRetrieve = fieldsToRetrieveStr.split("\\|");
                    }
                    fieldsToRetrieve = new String[]{TblsInspLotRMDataAudit.Lot.LOT_NAME.getName(), TblsInspLotRMDataAudit.Lot.AUDIT_ID.getName(), TblsInspLotRMDataAudit.Lot.ACTION_NAME.getName(), TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName(),
                        TblsInspLotRMDataAudit.Lot.REVIEWED.getName(), TblsInspLotRMDataAudit.Lot.REVIEWED_ON.getName(), TblsInspLotRMDataAudit.Lot.DATE.getName(), TblsInspLotRMDataAudit.Lot.PERSON.getName(), TblsInspLotRMDataAudit.Lot.REASON.getName(), TblsInspLotRMDataAudit.Lot.ACTION_PRETTY_EN.getName(), TblsInspLotRMDataAudit.Lot.ACTION_PRETTY_ES.getName()};
                    Object[][] sampleAuditInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT,
                            EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT, fieldsToRetrieve),
                            new String[]{TblsInspLotRMDataAudit.Lot.LOT_NAME.getName(), TblsInspLotRMDataAudit.Lot.PARENT_AUDIT_ID.getName() + SqlStatement.WHERECLAUSE_TYPES.IS_NULL.getSqlClause()}, new Object[]{lotName},
                            new String[]{TblsInspLotRMDataAudit.Lot.AUDIT_ID.getName()});
                    jArr = new JSONArray();
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditInfo[0][0].toString())) {
                        LPFrontEnd.servletReturnSuccess(request, response, jArr);
                        return;
                    }
                    for (Object[] curRow : sampleAuditInfo) {
                        JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRow,
                                new String[]{TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName()});
                        Object[] convertToJsonObjectStringedObject = LPJson.convertToJsonObjectStringedObject(curRow[LPArray.valuePosicInArray(fieldsToRetrieve, TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName())].toString());
                        if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObject[0].toString())) {
                            jObj.put(TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName(), convertToJsonObjectStringedObject[1]);
                        }

                        Integer curAuditId = Integer.valueOf(jObj.get(TblsInspLotRMDataAudit.Lot.AUDIT_ID.getName()).toString());
                        Object[][] sampleAuditInfoLvl2 = QueryUtilitiesEnums.getTableData(TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT,
                                EnumIntTableFields.getTableFieldsFromString(TblsInspLotRMDataAudit.TablesInspLotRMDataAudit.LOT, fieldsToRetrieve),
                                new String[]{TblsInspLotRMDataAudit.Lot.PARENT_AUDIT_ID.getName()}, new Object[]{curAuditId},
                                new String[]{TblsInspLotRMDataAudit.Lot.AUDIT_ID.getName()});
                        JSONArray jArrLvl2 = new JSONArray();
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAuditInfoLvl2[0][0].toString())) {
                            //Object[] childJObj=new Object[]{null, null, "No child", "", "", "", null, "", "", null, null};
                            //for (int iChild=childJObj.length;iChild<fieldsToRetrieve.length;iChild++)
                            //    childJObj=LPArray.addValueToArray1D(childJObj, null);      
                            Object[] childJObj = new Object[fieldsToRetrieve.length];
                            childJObj[2] = "No child";
                            JSONObject jObjLvl2 = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, childJObj);
                            jArrLvl2.add(jObjLvl2);
                        } else {
                            for (Object[] curRowLvl2 : sampleAuditInfoLvl2) {
                                JSONObject jObjLvl2 = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curRowLvl2,
                                        new String[]{TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName()});
                                Object[] convertToJsonObjectStringedObjectLvl2 = LPJson.convertToJsonObjectStringedObject(curRowLvl2[LPArray.valuePosicInArray(fieldsToRetrieve, TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName())].toString());
                                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(convertToJsonObjectStringedObjectLvl2[0].toString())) {
                                    jObjLvl2.put(TblsInspLotRMDataAudit.Lot.FIELDS_UPDATED.getName(), convertToJsonObjectStringedObjectLvl2[1]);
                                }
                                jArrLvl2.add(jObjLvl2);
                            }
                        }
                        jObj.put("sublevel", jArrLvl2);
                        jArr.add(jObj);
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    return;
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

                case INVESTIGATION_DEVIATION_PENDING_DECISION:
                    JSONArray jArray = new JSONArray();
                    String statusClosed = DataInsLotsCorrectiveAction.LotsCorrectiveActionStatuses.STATUS_CLOSED.getStatusCode();
                    String createInvCorrectiveAction = Parameter.getBusinessRuleProcedureFile(procReqInstance.getProcedureInstance(), InspLotRMBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_BULK.getAreaName(), InspLotRMBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_BULK.getTagName());
                    if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(createInvCorrectiveAction))) {
                        JSONObject jObj = new JSONObject();
                        jObj.put(TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION.getTableName(), "corrective action not active!");
                        jArray.add(jObj);
                    } else {
                        fieldsToRetrieveObj = TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION.getTableFields();
                        Object[][] investigationResultsPendingDecision = QueryUtilitiesEnums.getTableData(TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION,
                                fieldsToRetrieveObj,
                                new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.STATUS.getName() + "<>"},
                                new String[]{statusClosed},
                                new String[]{TblsInspLotRMProcedure.LotsCorrectiveAction.LOT_NAME.getName()});
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
                    createInvCorrectiveAction = Parameter.getBusinessRuleProcedureFile(procReqInstance.getProcedureInstance(), InspLotRMBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_BULK.getAreaName(), InspLotRMBusinessRules.CORRECTIVE_ACTION_FOR_REJECTED_BULK.getTagName());
                    if (Boolean.FALSE.equals(isTagValueOneOfEnableOnes(createInvCorrectiveAction))) {
                        JSONObject jObj = new JSONObject();
                        jObj.put(TblsInspLotRMProcedure.TablesInspLotRMProcedure.LOT_CORRECTIVE_ACTION.getTableName(), "corrective action not active!");
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
                case GET_MATERIALS:
                    jArr = new JSONArray();
                    SqlWhere whereObj = new SqlWhere();
                    whereObj.addConstraint(TblsInspLotRMConfig.Material.NAME, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    EnumIntTableFields[] flds = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL);
                    Object[][] materialInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL,
                            flds, whereObj,
                            new String[]{TblsInspLotRMConfig.Material.NAME.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInfo[0][0].toString())) {
                        LPFrontEnd.servletReturnSuccess(request, response, jArr);
                        return;
                    }
                    for (Object[] curRow : materialInfo) {
                        JSONObject jObj2 = new JSONObject();
                        jObj2 = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(flds), curRow);
                        jArr.add(jObj2);
                    }

                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    break;
                case GET_SPECS:
                    String specCode = LPNulls.replaceNull(argValues[0]).toString();

                    jArr = new JSONArray();
                    whereObj = new SqlWhere();
                    if ("ALL".equalsIgnoreCase(specCode)||LPNulls.replaceNull(specCode).toString().length() == 0) {
                        whereObj.addConstraint(TblsInspLotRMConfig.Spec.CODE, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    } else {
                        whereObj.addConstraint(TblsInspLotRMConfig.Spec.CODE, SqlStatement.WHERECLAUSE_TYPES.LIKE, new Object[]{specCode}, null);
                    }
                    flds = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsInspLotRMConfig.TablesInspLotRMConfig.SPEC);
                    Object[][] specInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMConfig.TablesInspLotRMConfig.SPEC,
                            flds, whereObj,
                            new String[]{TblsInspLotRMConfig.Spec.CODE.getName()});
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(specInfo[0][0].toString())) {
                        LPFrontEnd.servletReturnSuccess(request, response, jArr);
                        return;
                    }
                    for (Object[] curRow : specInfo) {
                        JSONObject jObj2 = new JSONObject();
                        jObj2 = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(flds), curRow);

                        Object curSpecCode = jObj2.get(TblsInspLotRMConfig.Spec.CODE.getName());
                        Object curSpecConfigVersion = jObj2.get(TblsInspLotRMConfig.Spec.CONFIG_VERSION.getName());

                        if (jObj2.containsKey(TblsInspLotRMConfig.Spec.ANALYSES.getName())){
                            Object analysisListStr = jObj2.get(TblsInspLotRMConfig.Spec.ANALYSES.getName());
                            if (analysisListStr.toString().length()==0){
                                jObj2.put("analysis_list", new JSONArray());
                            }else{
                                JSONArray analysisJArr=new JSONArray();
                                for (String curAna: analysisListStr.toString().split("\\|")){
                                    JSONObject jObj=new JSONObject();
                                    jObj.put("name", curAna);
                                    analysisJArr.add(jObj);
                                }
                                jObj2.put("analysis_list", analysisJArr);
                            }
                        }
                        if (jObj2.containsKey(TblsInspLotRMConfig.Spec.VARIATION_NAMES.getName())){
                            Object variationsListStr = jObj2.get(TblsInspLotRMConfig.Spec.VARIATION_NAMES.getName());
                            if (variationsListStr.toString().length()==0){
                                jObj2.put("variations_list", new JSONArray());
                            }else{
                                JSONArray variationsJArr=new JSONArray();
                                for (String curVar: variationsListStr.toString().split("\\|")){
                                    JSONObject jObj=new JSONObject();
                                    jObj.put("name", curVar);
                                    variationsJArr.add(jObj);
                                }
                                jObj2.put("variations_list", variationsJArr);
                            }
                        }                       
                        whereObj = new SqlWhere();
                        whereObj.addConstraint(TblsInspLotRMConfig.SpecLimits.CODE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curSpecCode}, null);
                        whereObj.addConstraint(TblsInspLotRMConfig.SpecLimits.CONFIG_VERSION, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{curSpecConfigVersion}, null);
                        
                        JSONArray specRulesInfo = QueryUtilities.dbRowsToJsonArrSimpleJson(procInstanceName, procInstanceName, TblsCnfg.TablesConfig.SPEC_RULES, 
                        EnumIntTableFields.getTableFieldsFromString(TblsCnfg.TablesConfig.SPEC_RULES, "ALL"),
                        whereObj, new String[]{}, null, true);
                        jObj2.put(TblsCnfg.TablesConfig.SPEC_RULES.getTableName(), specRulesInfo);
                        
                        JSONArray specRulesCardArr=new JSONArray();
                        if (specRulesInfo != null && specRulesInfo.size() > 0) {
                            JSONObject jsonObject = (JSONObject) specRulesInfo.get(0);

                            // Iterate through the keys of the JSONObject
                            for (Object key : jsonObject.keySet()) {
                                // Skip the key "hello"
                                if ( (!TblsInspLotRMConfig.SpecLimits.CODE.getName().equals(key.toString())) && (!TblsInspLotRMConfig.SpecLimits.CONFIG_VERSION.getName().equals(key.toString())) ) {
                                    JSONObject specRulesCardObj=new JSONObject();
                                    specRulesCardObj.put(key, jsonObject.get(key));
                                    specRulesCardObj.put("name", key);
                                    specRulesCardObj.put("value", jsonObject.get(key));
                                    specRulesCardArr.add(specRulesCardObj);
                                }
                            }
                        }                        
                        jObj2.put(TblsCnfg.TablesConfig.SPEC_RULES.getTableName()+"_for_card", specRulesCardArr);
                        EnumIntTableFields[] fldsSpecLimits = EnumIntTableFields.getAllFieldNamesFromDatabase(TblsInspLotRMConfig.TablesInspLotRMConfig.SPEC_LIMITS);
                        JSONArray jSpecLimitsArr = new JSONArray();
                        Object[][] specLimitsInfo = QueryUtilitiesEnums.getTableData(TblsInspLotRMConfig.TablesInspLotRMConfig.SPEC_LIMITS,
                                fldsSpecLimits, whereObj,
                                new String[]{TblsInspLotRMConfig.SpecLimits.CODE.getName()});
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(specInfo[0][0].toString())) {
                            LPFrontEnd.servletReturnSuccess(request, response, jArr);
                            return;
                        }
                        for (Object[] curRow2 : specLimitsInfo) {
                            JSONObject jObj3 = new JSONObject();
                            jObj3 = LPJson.convertArrayRowToJSONObject(EnumIntTableFields.getAllFieldNames(fldsSpecLimits), curRow2);
                            jSpecLimitsArr.add(jObj3);
                        }
                        jObj2.put(TblsInspLotRMConfig.TablesInspLotRMConfig.SPEC_LIMITS.getTableName(), jSpecLimitsArr);

                        String curSpecTestScripts = LPNulls.replaceNull(jObj2.get(TblsInspLotRMConfig.Spec.TESTING_SCRIPTS.getName())).toString();
                        JSONArray scriptDetail = new JSONArray();
                        if (curSpecTestScripts.length() > 0) {
                            for (String curId : curSpecTestScripts.split("\\|")) {
                                JSONObject curTestObj = ReqProcedureDefinitionQueries.getSpecScriptWithSteps(Integer.valueOf(curId),
                                        procReqInstance.getProcedureInstance(), null, null);
                                if (Boolean.FALSE.equals(curTestObj.isEmpty())) {
                                    scriptDetail.add(curTestObj);
                                }
                            }
                        }
                        jObj2.put("scripts_detail", scriptDetail);

                        jArr.add(jObj2);
                    }
                    Rdbms.closeRdbms();
                    LPFrontEnd.servletReturnSuccess(request, response, jArr);
                    break;
                case GET_ANALYSIS:
                    String code = LPNulls.replaceNull(argValues[0]).toString();
                    Boolean includeCertif = Boolean.valueOf(LPNulls.replaceNull(argValues[1]).toString());
                    jArr = new JSONArray();
                    whereObj = new SqlWhere();
                    String[] wFldN=new String[]{};
                    Object[] wFldV=new Object[]{};
                    if ("ALL".equalsIgnoreCase(code)){
                        wFldN=LPArray.addValueToArray1D(wFldN, TblsCnfg.Analysis.CODE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause());                        
                    }else{
                        wFldN=LPArray.addValueToArray1D(wFldN, TblsCnfg.Analysis.CODE.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.LIKE.getSqlClause());
                        wFldV=LPArray.addValueToArray1D(wFldV, code);
                    }
                    if ("ALL".equalsIgnoreCase(code)||LPNulls.replaceNull(code).toString().length() == 0) {
                        whereObj.addConstraint(TblsCnfg.Analysis.CODE, SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, null);
                    } else {
                        whereObj.addConstraint(TblsCnfg.Analysis.CODE, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{code}, null);
                    }
                    String repositoryName=LPPlatform.buildSchemaName(procReqInstance.getProcedureInstance(), TblsCnfg.TablesConfig.ANALYSIS.getRepositoryName());
                    JSONArray mainArr = QueryUtilities.dbRowsToJsonArrSimpleJson(procReqInstance.getProcedureInstance(), repositoryName, TblsCnfg.TablesConfig.ANALYSIS.getTableName(),
                        EnumIntTableFields.getAllFieldNames(EnumIntTableFields.getAllFieldNamesFromDatabase(TblsCnfg.TablesConfig.ANALYSIS)),
                        wFldN, wFldV,
                        new String[]{TblsCnfg.Analysis.CODE.getName()}, null, false, false);                    
                    JSONArray dbRowsToJsonArr2 = new JSONArray();                    
                    for (int i = 0; i < mainArr.size(); i++) {
                        JSONObject anajObj = (JSONObject) mainArr.get(i);
                        String curAnalysis = LPNulls.replaceNull(anajObj.get("code")).toString();
                        JSONArray anaMethod = QueryUtilities.dbRowsToJsonArrSimpleJson(procReqInstance.getProcedureInstance(), repositoryName, TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName(),
                            EnumIntTableFields.getAllFieldNames(EnumIntTableFields.getAllFieldNamesFromDatabase(TblsCnfg.TablesConfig.ANALYSIS_METHOD)),
                            new String[]{TblsCnfg.AnalysisMethod.ANALYSIS.getName()},new Object[]{curAnalysis},
                            new String[]{TblsCnfg.AnalysisMethod.ANALYSIS.getName()}, null, false, false);   
                        JSONArray anaMethodArr = new JSONArray();
                        for (int j = 0; j < anaMethod.size(); j++) {
                            JSONObject anaMethodjObj = (JSONObject) anaMethod.get(j);
                            String curAnaMethod = LPNulls.replaceNull(anaMethodjObj.get(TblsCnfg.AnalysisMethod.METHOD_NAME.getName())).toString();
                            JSONArray anaMethodParam = QueryUtilities.dbRowsToJsonArrSimpleJson(procReqInstance.getProcedureInstance(), repositoryName, TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS.getTableName(),
                                EnumIntTableFields.getAllFieldNames(EnumIntTableFields.getAllFieldNamesFromDatabase(TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS)),
                                new String[]{TblsCnfg.AnalysisMethodParams.ANALYSIS.getName(), TblsCnfg.AnalysisMethodParams.METHOD_NAME.getName()},
                                new Object[]{curAnalysis, curAnaMethod},
                                new String[]{TblsCnfg.AnalysisMethodParams.ANALYSIS.getName()}, null, false, false);   
                            anaMethodjObj.put(TblsCnfg.TablesConfig.ANALYSIS_METHOD_PARAMS.getTableName(), anaMethodParam);
                            anaMethodArr.add(anaMethodjObj);
                        }
                        anajObj.put(TblsCnfg.TablesConfig.ANALYSIS_METHOD.getTableName(), anaMethodArr);
                        dbRowsToJsonArr2.add(anajObj);
                    }                    
                    Rdbms.closeRdbms();
                    JSONObject jMainObj=new JSONObject();
                    jMainObj.put(TblsCnfg.TablesConfig.ANALYSIS.getTableName(), dbRowsToJsonArr2);
                    if (includeCertif){
                        jMainObj.put("certifications_info", AnalysisMethodCertifQueries.methodsByUser(procReqInstance.getProcedureInstance()));
                    }    
                    LPFrontEnd.servletReturnSuccess(request, response, jMainObj);
                    
                    break;                    
                    
                case GET_SAMPLE_ANALYSIS_RESULT_LIST:
                    RelatedObjects rObj = RelatedObjects.getInstanceForActions();
                        Integer sampleId = Integer.valueOf(LPNulls.replaceNull(argValues[0]).toString());
                        String[] resultFieldToRetrieveArr = EnumIntViewFields.getAllFieldNames(EnumIntViewFields.getViewFieldsFromString(TblsInspLotRMData.ViewsInspLotRMData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, "ALL"));
                        EnumIntViewFields[] fldsToGet = EnumIntViewFields.getAllFieldNamesFromDatabase(TblsInspLotRMData.ViewsInspLotRMData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, null);
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
                        Integer posicLimitIdFld = EnumIntViewFields.getFldPosicInArray(fldsToGet, TblsData.ViewSampleAnalysisResultWithSpecLimits.LIMIT_ID.getName());

                        Object[][] analysisResultList = QueryUtilitiesEnums.getViewData(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW,
                                fldsToGet,
                                new SqlWhere(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, sampleAnalysisWhereFieldsNameArr, sampleAnalysisWhereFieldsValueArr),
                                sortFieldsNameArr);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(analysisResultList[0][0].toString())) {

                            this.isSuccess = true;
                            this.responseSuccessJArr = new JSONArray();
                        } else {
                            rObj.addSimpleNode(GlobalVariables.Schemas.APP.getName(), TblsData.TablesData.SAMPLE.getTableName(), sampleId);
                            Object[] objectsIds = ClassSampleQueries.getObjectsId(EnumIntViewFields.getAllFieldNames(fldsToGet), analysisResultList, "-");
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

                                Object[] resultLockData = ClassSampleQueries.sampleAnalysisResultLockData(EnumIntViewFields.getAllFieldNames(fldsToGet), curRow);
                                if (resultLockData != null && resultLockData[0] != null) {
                                    if (resultLockData.length > 2) {
                                        row = LPJson.convertArrayRowToJSONObject(LPArray.addValueToArray1D(LPArray.addValueToArray1D(EnumIntViewFields.getAllFieldNames(fldsToGet), (String) resultLockData[2]), (String[]) resultLockData[0]),
                                                LPArray.addValueToArray1D(LPArray.addValueToArray1D(curRow, resultLockData[3]), (Object[]) resultLockData[1]));
                                    } else {
                                        row = LPJson.convertArrayRowToJSONObject(LPArray.addValueToArray1D(EnumIntViewFields.getAllFieldNames(fldsToGet), (String[]) resultLockData[0]), LPArray.addValueToArray1D(curRow, (Object[]) resultLockData[1]));
                                    }
                                } else {
                                    row = LPJson.convertArrayRowToJSONObject(EnumIntViewFields.getAllFieldNames(fldsToGet), curRow);
                                }
                                if ((currRowLimitId != null) && (currRowLimitId.length() > 0)) {
                                    specRule.specLimitsRule(Integer.valueOf(currRowLimitId), null);
                                    row.put(ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_DETAILED, LPNulls.replaceNull(specRule.getRuleRepresentation()).replace(("R"), "R (" + currRowRawValue + ")"));
                                    Object[][] specRuleDetail = specRule.getRuleData();
                                    JSONArray specRuleDetailjArr = new JSONArray();
                                    JSONObject specRuleDetailjObj = new JSONObject();
                                    for (Object[] curSpcRlDet : specRuleDetail) {
                                        specRuleDetailjObj.put(curSpcRlDet[0], curSpcRlDet[1]);
                                    }
                                    specRuleDetailjArr.add(specRuleDetailjObj);
                                    row.put(ConfigSpecRule.JSON_TAG_NAME_SPEC_RULE_INFO, specRuleDetailjArr);
                                }
                                jArr.add(row);
                            }
                            Rdbms.closeRdbms();
                            this.isSuccess = true;
                            this.responseSuccessJArr = jArr;
                        }
                        return;
                default:
                    procReqInstance.killIt();
                    LPFrontEnd.servletReturnResponseError(request, response,
                            LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(),
                            new Object[]{actionName, this.getClass().getSimpleName()}, language,
                            ClassInspLotRMQueries.class.getSimpleName());
            }
        } catch (Exception e) {
            LPFrontEnd.servletReturnResponseError(request, response,
                    LPParadigm.ParadigmErrorTrapping.UNHANDLED_EXCEPTION_IN_CODE.getErrorCode(),
                    new Object[]{e.getMessage()}, language,
                    ClassInspLotRMQueries.class.getSimpleName());

        } finally {
            procReqInstance.killIt();
        }
    }

    @Override
    public Object[] getMessageDynamicData() {
        return messageDynamicData;
    }

    @Override
    public RelatedObjects getRelatedObj() {
        return relatedObj;
    }

    @Override
    public Boolean getEndpointExists() {
        return endpointExists;
    }

    @Override
    public Object[] getDiagnostic() {
        return diagnostic;
    }

    @Override
    public Boolean getFunctionFound() {
        return this.functionFound;
    }

    @Override
    public Boolean getIsSuccess() {
        return this.isSuccess;
    }

    @Override
    public JSONObject getResponseSuccessJObj() {
        return this.responseSuccessJObj;
    }

    @Override
    public JSONArray getResponseSuccessJArr() {
        return this.responseSuccessJArr;
    }

    @Override
    public Object[] getResponseError() {
        return this.diagnostic;
    }
}
