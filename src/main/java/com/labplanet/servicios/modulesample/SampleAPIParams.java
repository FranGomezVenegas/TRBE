/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.modulesample;

import lbplanet.utilities.LPFrontEnd;
import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.TblsData;
import functionaljavaa.audit.SampleAudit.DataSampleAnalysisAuditEvents;
import functionaljavaa.audit.SampleAudit.DataSampleAnalysisResultAuditEvents;
import functionaljavaa.audit.SampleAudit.DataSampleAuditEvents;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import trazit.enums.EnumIntAuditEvents;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;

/**
 *
 * @author Administrator
 */
public class SampleAPIParams extends HttpServlet {
  
    /**
     *
     */
    public static final String SERVLET_API_URL="/modulesample/SampleAPI";  

    /**
     *
     */
    public static final String SERVLET_FRONTEND_URL="/frontend/SampleAPIfrontEnd";
    
    public enum SampleAPIactionsEndpoints implements EnumIntEndpoints{
        REVIEWSAMPLE_TESTINGGROUP("REVIEWSAMPLE_TESTINGGROUP", "reviewSampleTestingGroup_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TESTING_GROUP, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).
            add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_REVISION_TESTING_GROUP.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_TESTINGGROUP_REVIEWED),
        REVIEWRESULT("REVIEWRESULT", "reviewResult_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build(), DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_REVIEWED ),
        UNREVIEWRESULT("UNREVIEWRESULT", "unreviewResult_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build(), DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_UNREVIEWED ),
        CANCELSAMPLE("CANCELSAMPLE", "cancelSample_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_CANCELED),
        UNCANCELSAMPLE("UNCANCELSAMPLE", "uncancelSample_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_UNCANCELED ),
        CANCELTEST("CANCELTEST", "cancelTest_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ANALYSIS.getTableName()).build()).build(), DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_CANCELED ),
        UNREVIEWTEST("UNREVIEWTEST", "unreviewTest_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ANALYSIS.getTableName()).build()).build(), DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_UNREVIEWED ),
        UNCANCELTEST("UNCANCELTEST", "uncancelTest_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ANALYSIS.getTableName()).build()).build(), DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_UNCANCELED ),
        CANCELRESULT("CANCELRESULT", "cancelResult_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build(), DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_CANCELED ),
        UNCANCELRESULT("UNCANCELRESULT", "uncancelResult_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build(), DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_UNCANCELED ),
        COC_ABORTCHANGE("COC_ABORTCHANGE", "cocAbortChange_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_CANCEL_CHANGE_COMMENT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)},             
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.CHAIN_OF_CUSTODY_ABORTED),
        COC_STARTCHANGE("COC_STARTCHANGE", "cocStartChange_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_CUSTODIAN_CANDIDATE, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, 
                Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.CHAIN_OF_CUSTODY_STARTED),
        COC_CONFIRMCHANGE("COC_CONFIRMCHANGE", "cocConfirmChange_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_CONFIRM_CHANGE_COMMENT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, 
                Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.CHAIN_OF_CUSTODY_COMPLETED),
        GETSAMPLEINFO("GETSAMPLEINFO", "getSampleInfo_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7)}, 
                Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), null),
        TOKEN_VALIDATE_ESIGN_PHRASE("TOKEN_VALIDATE_ESIGN_PHRASE", "tokenValidateEsignPhrase_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7)}, EndPointsToRequirements.endpointWithNoOutputObjects, null),
        LOGALIQUOT("LOGALIQUOT", "logAliquot_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
                Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ALIQ.getTableName()).build()).build(), DataSampleAuditEvents.LOG_SAMPLE_ALIQUOT),
        
        LOGSUBALIQUOT("LOGSUBALIQUOT", "logSubAliquot_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ALIQUOT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
                Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ALIQ.getTableName()).build()).add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ALIQ_SUB.getTableName()).build()).build(), DataSampleAuditEvents.LOG_SAMPLE_SUBALIQUOT),
        SAMPLESTAGE_MOVETONEXT("SAMPLESTAGE_MOVETONEXT", "sampleStage_moveToNext_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
                Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLESTAGE_MOVETONEXT),
        SAMPLESTAGE_MOVETOPREVIOUS("SAMPLESTAGE_MOVETOPREVIOUS", "sampleStage_moveToPrevious_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
                Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLESTAGE_MOVETOPREVIOUS),
        SAMPLEAUDIT_SET_AUDIT_ID_REVIEWED("SAMPLEAUDIT_SET_AUDIT_ID_REVIEWED", "sampleAudit_setAuditIdReviewed_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_AUDIT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
                Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.REVIEWED_AUDIT_ID),
        LOGSAMPLE("LOGSAMPLE", "sampleLogged_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_TEMPLATE_VERSION, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 7), 
                new LPAPIArguments("specName", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                new LPAPIArguments("specVersion", LPAPIArguments.ArgumentType.INTEGER.toString(), true, 9),
                new LPAPIArguments("variationName", LPAPIArguments.ArgumentType.STRING.toString(), true, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NUM_SAMPLES_TO_LOG, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 13)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_LOGGED ),
        RECEIVESAMPLE("RECEIVESAMPLE", "sampleReceived_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_RECEIVED ),                
        SETSAMPLINGDATE("SETSAMPLINGDATE", "setSamplingDate_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SET_SAMPLING_DATE ),                
        CHANGESAMPLINGDATE("CHANGESAMPLINGDATE", "changeSamplingDate_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NEW_DATETIME, LPAPIArguments.ArgumentType.DATETIME.toString(), true, 7)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_CHANGE_SAMPLING_DATE ),
        SETSAMPLINGDATEEND("SETSAMPLINGDATEEND", "setSamplingDateEnd_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SET_SAMPLING_DATE_END ),                
        CHANGESAMPLINGDATEEND("CHANGESAMPLINGDATEEND", "changeSamplingDate_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NEW_DATETIME, LPAPIArguments.ArgumentType.DATETIME.toString(), true, 7)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_CHANGE_SAMPLING_DATE_END ),
        SAMPLINGCOMMENTADD("SAMPLINGCOMMENTADD", "samplingCommentAdd_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_COMMENT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_RECEPTION_COMMENT_ADD ),
        SAMPLINGCOMMENTREMOVE("SAMPLINGCOMMENTREMOVE", "samplingCommentRemove_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_RECEPTION_COMMENT_REMOVE ),
        INCUBATIONSTART("INCUBATIONSTART", "incubationStart_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_SET_INCUBATION_1_STARTED ),                
        INCUBATIONEND("INCUBATIONEND", "incubationEnd_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_SET_INCUBATION_1_ENDED ),
        INCUBATION2START("INCUBATION2START", "incubation2Start_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_SET_INCUBATION_2_STARTED ),
        INCUBATION2END("INCUBATION2END", "incubation2End_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_SET_INCUBATION_2_ENDED ),
        SAMPLEANALYSISADD("SAMPLEANALYSISADD", "sampleAnalysisAdd_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), true, 8)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_ADDED ),
        TESTASSIGNMENT("TESTASSIGNMENT", "testAssignment_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_NEW_ANALYST, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_ANALYST_ASSIGNMENT ),
        ENTERRESULT("ENTERRESULT", "enterResult_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build(), DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED),
        REENTERRESULT("REENTERRESULT", "reEnterResult_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build())
                .add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build(), DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_REENTERED),
        ENTERRESULT_BY_ANALYSIS_NAME("ENTERRESULT_BY_ANALYSIS_NAME", "enterResult_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ANALYSIS_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 8)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(),DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED ),
        ENTERRESULT_LOD("ENTERRESULT_LOD", "enterResultLOD_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RAW_VALUE_RESULT, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ANALYSIS_RESULT.getTableName()).build()).build(), DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED ),
        RESULT_CHANGE_UOM("RESULT_CHANGE_UOM", "resultChangeUOM_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_NEW_UOM, LPAPIArguments.ArgumentType.STRING.toString(), true, 7)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAnalysisResultAuditEvents.SAMPLE_ANALYSIS_RESULT_ENTERED ),
        REVIEWSAMPLE("REVIEWSAMPLE", "reviewSample_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE.getTableName()).build()).build(), DataSampleAuditEvents.SAMPLE_REVIEWED ),
        UNREVIEWSAMPLE("UNREVIEWSAMPLE", "unreviewSample_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ANALYSIS.getTableName()).build()).build(), DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_UNREVIEWED ),
        REVIEWTEST("REVIEWTEST", "reviewTest_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ANALYSIS.getTableName()).build()).build(), DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_REVIEWED ),
        REVIEWTEST_BY_SAMPLE_ID_AND_ANALYSIS_NAME("REVIEWTEST_BY_SAMPLE_ID_AND_ANALYSIS_NAME", "reviewTestBySampleIdAndAnalysisName_success",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ANALYSIS_NAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),}, 
            Json.createArrayBuilder().add(Json.createObjectBuilder().add("repository", GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsData.TablesData.SAMPLE_ANALYSIS.getTableName()).build()).build(), DataSampleAnalysisAuditEvents.SAMPLE_ANALYSIS_REVIEWED),
        ;      
        private SampleAPIactionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, EnumIntAuditEvents actionEventObj){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums; 
            this.outputObjectTypes=outputObjectTypes;            
            this.actionEventObj=actionEventObj;
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){                
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            hm.put(request, argValues);            
            return hm;
        }
        @Override        public String getName(){return this.name;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override        public LPAPIArguments[] getArguments() {return arguments;}
        @Override        public String getApiUrl(){return ApiUrls.SAMPLES_ACTIONS.getUrl();}
        public EnumIntAuditEvents getAuditEventObj() {return actionEventObj;}
        
        private final String name;
        private final String successMessageCode;    
        private final  LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;   
        private final EnumIntAuditEvents actionEventObj;
    }
    
    public enum SampleAPIqueriesEndpoints implements EnumIntEndpoints{
        /**
         *
         */
        GET_SAMPLETEMPLATES("GET_SAMPLETEMPLATES", "",
            new LPAPIArguments[]{}, null),
        UNRECEIVESAMPLES_LIST("UNRECEIVESAMPLES_LIST", "",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 9),}, null),
        SAMPLES_PENDING_TESTINGGROUP_REVISION("SAMPLES_PENDING_TESTINGGROUP_REVISION", "",
            new LPAPIArguments[]{new LPAPIArguments("testingGroup", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),}, null),
        SAMPLES_PENDING_SAMPLE_REVISION("SAMPLES_PENDING_SAMPLE_REVISION", "",
            new LPAPIArguments[]{                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 6),}, null),                
        SAMPLES_INPROGRESS_LIST2("SAMPLES_INPROGRESS_LIST", "",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 14),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 15),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 16),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 17),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 18),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 19),}, null),
        SAMPLEANALYSIS_PENDING_REVISION("SAMPLEANALYSIS_PENDING_REVISION", "",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),}, null),
/*                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 15),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 16),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 17),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 18),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 19),}),*/
        SAMPLES_AND_RESULTS_VIEW("SAMPLE_AND_RESULTS_VIEW", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),}, null),
        SAMPLES_BY_STAGE("SAMPLES_BY_STAGE", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 14),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 15),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 16),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 17),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 18),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 19),}, null),
        ANALYSIS_ALL_LIST("ANALYSIS_ALL_LIST", "",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7)}, null),
        GET_SAMPLE_ANALYSIS_LIST("GET_SAMPLE_ANALYSIS_LIST", "",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8)}, null),
        GET_SAMPLE_ANALYSIS_RESULT_LIST("GET_SAMPLE_ANALYSIS_RESULT_LIST", "", new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGOFOBJECTS.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                //new LPAPIArguments(EnvMonitAPIParams., LPAPIArguments.ArgumentType.STRING.toString(), false, 7)
                }, null),        
        SAMPLES_VIEW("SAMPLES_VIEW", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TEST_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 14),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 15),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 16),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 17),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 18),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 19),
                new LPAPIArguments("includeOnlyIfResultsInProgress", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 20),
                }, null),        
        SAMPLES_ANALYSIS_VIEW("SAMPLES_ANALYSIS_VIEW", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_LAST_LEVEL, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ADD_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 14),
                new LPAPIArguments("includeOnlyIfResultsInProgress", LPAPIArguments.ArgumentType.BOOLEAN.toString(), false, 15),
                }, null),        
        SAMPLES_ANALYSIS_RESULTS_VIEW("SAMPLES_ANALYSIS_RESULTS_VIEW", "",
                new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),                
                }, null),        
        CHANGEOFCUSTODY_SAMPLE_HISTORY("CHANGEOFCUSTODY_SAMPLE_HISTORY", "",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9)}, null),
        CHANGEOFCUSTODY_USERS_LIST("CHANGEOFCUSTODY_USERS_LIST", "",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SORT_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8)}, null),
        GET_SAMPLE_ANALYSIS_RESULT_SPEC("GET_SAMPLE_ANALYSIS_RESULT_SPEC", "",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_RESULT_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6)}, null),
        SAMPLE_ENTIRE_STRUCTURE("SAMPLE_ENTIRE_STRUCTURE", "",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_FIELD_TO_SORT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 10),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ANALYSIS_RESULT_FIELD_TO_SORT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 11),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_AUDIT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_AUDIT_FIELD_TO_SORT, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 13)}, null),
        GET_SAMPLE_AUDIT("GET_SAMPLE_AUDIT", "",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_AUDIT_FIELD_TO_RETRIEVE, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7)}, null),        
        GET_SAMPLE_STAGES_SUMMARY_REPORT("GET_SAMPLE_STAGES_SUMMARY_REPORT", "",new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            }, null),
        KPIS("KPIS", "", new LPAPIArguments[]{
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_OBJ_GROUP_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABLE_CATEGORY, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_TABLE_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 8),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 9),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE, LPAPIArguments.ArgumentType.STRINGARR.toString(), true, 10),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FIELDS_TO_RETRIEVE_OR_GROUPING, LPAPIArguments.ArgumentType.STRINGARR.toString(), false, 12),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_GROUPED, LPAPIArguments.ArgumentType.BOOLEANARR.toString(), true, 11),
        }, null),            
        GET_METHOD_CERTIFIED_USERS_LIST("GET_METHOD_CERTIFIED_USERS_LIST", "",
            new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_METHOD_NAME, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),            
        }, null),
        ;      
        private SampleAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;            
            this.outputObjectTypes=outputObjectTypes;            
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){                
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            hm.put(request, argValues);            
            return hm;
        }
        @Override        public String getName(){return this.name;}
        @Override        public String getSuccessMessageCode(){return this.successMessageCode;}           
        @Override        public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override        public LPAPIArguments[] getArguments() {return arguments;}
        @Override        public String getApiUrl(){return ApiUrls.SAMPLES_QUERIES.getUrl();}
        private final String name;
        private final String successMessageCode;    
        private final  LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;
    }
    

    public static final String MANDATORY_PARAMS_MAIN_SERVLET =GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME+"|"+GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN+"|"+GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME+"|"+GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

    public static final String MANDATPARMS_FE_UNRECSMPS_LIST="sortFieldsName|sampleFieldToRetrieve"; 

    public static final String MPARAMS_FE_UNRECSMPS_SORTFLDSNAME_DEFAULTV="";

    public static final String MANDATORY_PARAMS_FRONTEND_SAMPLES_INPROGRESS_LIST_SAMPLE_ANALYSIS_FIELD_RETRIEVE_DEFAULT_VALUE="test_id|status|analysis|method_name|method_version";

    public static final String MANDATORY_PARAMS_FRONTEND_SAMPLES_INPROGRESS_LIST_SAMPLE_ANALYSIS_RESULT_FIELD_RETRIEVE_DEFAULT_VALUE="result_id|status|param_name|raw_value|pretty_value";

    public static final String MANDATPARMS_FE_UNRECSMPS_LIST_SAMPLE_FIELD_RETRIEVE_DEFAULT_VALUE="sample_id";

    public static final String MANDATPARMS_FE_GETSMPANA_LIST=GlobalAPIsParams.REQUEST_PARAM_SAMPLE_ID; 

    public static final String MANDATORY_PARAMS_FRONTEND_GET_SAMPLE_ANALYSIS_RESULT_LIST="sampleId"; 

    public static final String MANDATORY_PARAMS_FRONTEND_CHANGEOFCUSTODY_SAMPLE_HISTORY="sampleId"; 

    public static final String MANDATORY_PARAMS_FRONTEND_GET_SAMPLE_ANALYSIS_RESULT_SPEC="resultId"; 
    
    public static final String MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_ANALYSIS_ALL_LIST="code|method_name|method_version"; 

    public static final String MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_GET_SAMPLE_ANALYSIS_LIST="sample_id|test_id"; 

    public static final String MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_GET_SAMPLE_ANALYSIS_RESULT_LIST="sample_id|test_id|result_id|param_name|limit_id|param_type|spec_eval|spec_eval_detail|method_name|method_version|is_locked|warning_reason"; 

    public static final String MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_CHANGEOFCUSTODY_SAMPLE_HISTORY="sample_id|custodian|custodian_name|custodian_candidate|candidate_name|coc_started_on|status|coc_confirmed_on"; 

    public static final String MANDATORY_FIELDS_FRONTEND_TO_RETRIEVE_CHANGEOFCUSTODY_USERS_LIST="user_name|person_name"; 
    
    public static final String MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_LIST="sample_id|test_id"; 

    public static final String MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_GET_SAMPLE_ANALYSIS_RESULT_LIST="sample_id|test_id|result_id"; 

    public static final String MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_CHANGEOFCUSTODY_SAMPLE_HISTORY="sample_id|coc_started_on"; 

    public static final String MANDATORY_FIELDS_FRONTEND_WHEN_SORT_NULL_CHANGEOFCUSTODY_USERS_LIST="user_name|person_name"; 

    
    
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet sampleAPIParams</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet sampleAPIParams at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
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
