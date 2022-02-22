/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.Rdbms;
import static databases.Rdbms.dbGetIndexLastNumberInUse;
import databases.TblsTesting;
import functionaljavaa.businessrules.BusinessRules;
import lbplanet.utilities.LPHashMap;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPArray;
import functionaljavaa.parameter.Parameter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform.ApiErrorTraping;
import static lbplanet.utilities.LPPlatform.TRAP_MESSAGE_CODE_POSIC;
import static lbplanet.utilities.LPPlatform.TRAP_MESSAGE_EVALUATION_POSIC;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;
/*
 *
 * @author Administrator
 */
public class LPTestingOutFormat {

    /**
     * @return the csvHeaderTags
     */
    public HashMap<String, Object> getCsvHeaderTags() {
        return csvHeaderTags;
    }

    public enum InputModes{FILE, DATABASE}
    private String inputMode="";
    private Object[][] testingContent=new Object[0][0];
    private String filePathName="";
    private String fileName="";
    private HashMap<String, Object> csvHeaderTags=null;
    private StringBuilder htmlStyleHeader = new StringBuilder(0);
    private Integer numEvaluationArguments = -1;
    private Integer actionNamePosic = -1;
    private Integer auditReasonPosic = -1;
    private Integer stepIdPosic = -1;
    private Integer stopSyntaxisUnmatchPosic = -1;
    private Integer stopSyntaxisFalsePosic = -1;
    private Integer alternativeTokenFldPosic = -1;

    public LPTestingOutFormat(HttpServletRequest request, String testerName, String testerFileName){
        String csvPathName ="";
        String csvFileName ="";
        Object[][] csvFileContent = new Object[0][0];
        Object testingSource=request.getAttribute(LPTestingParams.TESTING_SOURCE);
        Integer numEvalArgs=0;
        numEvalArgs = Integer.valueOf(LPNulls.replaceNull(request.getAttribute(LPTestingParams.NUM_EVAL_ARGS).toString()));
        StringBuilder htmlStyleHdr = new StringBuilder(0);
        HashMap<String, Object> headerTags = new HashMap();
        Integer actionNmePosic=numEvalArgs+1;
        String[] fieldsName=new String[]{TblsTesting.ScriptSteps.EXPECTED_SYNTAXIS.getName(), TblsTesting.ScriptSteps.EXPECTED_CODE.getName(), TblsTesting.ScriptSteps.ESIGN_TO_CHECK.getName(),
            TblsTesting.ScriptSteps.CONFIRMUSER_USER_TO_CHECK.getName(), TblsTesting.ScriptSteps.CONFIRMUSER_PW_TO_CHECK.getName(),
            TblsTesting.ScriptSteps.ARGUMENT_01.getName(), TblsTesting.ScriptSteps.ARGUMENT_02.getName(),
            TblsTesting.ScriptSteps.ARGUMENT_03.getName(), TblsTesting.ScriptSteps.ARGUMENT_04.getName(),
            TblsTesting.ScriptSteps.ARGUMENT_05.getName(), TblsTesting.ScriptSteps.ARGUMENT_06.getName(),
            TblsTesting.ScriptSteps.ARGUMENT_07.getName(), TblsTesting.ScriptSteps.ARGUMENT_08.getName(),
            TblsTesting.ScriptSteps.ARGUMENT_09.getName(), TblsTesting.ScriptSteps.ARGUMENT_10.getName(), TblsTesting.ScriptSteps.STEP_ID.getName(),
            TblsTesting.ScriptSteps.AUDIT_REASON.getName(),
            TblsTesting.ScriptSteps.STOP_WHEN_SYNTAXIS_UNMATCH.getName(), TblsTesting.ScriptSteps.STOP_WHEN_SYNTAXIS_FALSE.getName(),
            TblsTesting.ScriptSteps.ALTERNATIVE_TOKEN.getName()
        };
        Integer scriptId = null;
        String procInstanceName=null;
        if (testingSource!=null && testingSource=="DB"){
            csvPathName ="";
            csvFileName ="";
            if (!LPFrontEnd.servletStablishDBConection(request, null)){return;}
            scriptId = Integer.valueOf(LPNulls.replaceNull(request.getAttribute(LPTestingParams.SCRIPT_ID).toString()));
            procInstanceName=LPNulls.replaceNull(request.getAttribute(LPTestingParams.SCHEMA_PREFIX)).toString();
            String repositoryName=LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_TESTING.getName(), "");
            if (procInstanceName!=null && procInstanceName.length()>0)
                repositoryName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
            
            csvFileContent = Rdbms.getRecordFieldsByFilter(repositoryName, TblsTesting.TablesTesting.SCRIPT_STEPS.getTableName(),
                    new String[]{TblsTesting.ScriptSteps.SCRIPT_ID.getName(), TblsTesting.ScriptSteps.ACTIVE.getName()}, new Object[]{scriptId, true},
                    fieldsName,
                    new String[]{TblsTesting.ScriptSteps.STEP_ID.getName()});
            headerTags.put(FileHeaderTags.NUM_HEADER_LINES.getTagValue().toString(), 0);
            headerTags.put(FileHeaderTags.NUM_TABLES.getTagValue().toString(), "-");
            headerTags.put(FileHeaderTags.NUM_EVALUATION_ARGUMENTS.getTagValue().toString(), numEvalArgs);
            actionNmePosic=5;
        }else{
            csvPathName =(String) request.getAttribute(LPTestingParams.UPLOAD_FILE_PARAM_FILE_PATH);
            csvFileName =(String) request.getAttribute(LPTestingParams.UPLOAD_FILE_PARAM_FILE_NAME);
            if ("".equals(csvPathName) || csvPathName==null){
                csvFileName = testerFileName;
                csvPathName = LPTestingOutFormat.TESTING_FILES_PATH; }
            csvPathName = csvPathName+csvFileName;
            String csvFileSeparator=LPTestingOutFormat.TESTING_FILES_FIELD_SEPARATOR;

            csvFileContent = LPArray.convertCSVinArray(csvPathName, csvFileSeparator);

            String[][] headerInfo = LPArray.convertCSVinArray(csvPathName, "=");
            headerTags = LPTestingOutFormat.getCSVHeader(headerInfo);
            numEvalArgs = Integer.valueOf(headerTags.get(LPTestingOutFormat.FileHeaderTags.NUM_EVALUATION_ARGUMENTS.getTagValue().toString()).toString());
        }
        htmlStyleHdr = new StringBuilder(0);
        htmlStyleHdr.append(LPTestingOutFormat.getHtmlStyleHeader(testerName, csvFileName, scriptId, procInstanceName));

        this.testingContent=csvFileContent;
        this.inputMode=LPNulls.replaceNull(testingSource).toString();
        this.filePathName=csvPathName;
        this.fileName=csvFileName;
        this.csvHeaderTags=headerTags;
        this.htmlStyleHeader=htmlStyleHdr;
        this.numEvaluationArguments=numEvalArgs;
        this.actionNamePosic=actionNmePosic;
        this.auditReasonPosic=LPArray.valuePosicInArray(fieldsName, TblsTesting.ScriptSteps.AUDIT_REASON.getName());
        this.stepIdPosic=LPArray.valuePosicInArray(fieldsName, TblsTesting.ScriptSteps.STEP_ID.getName());
        this.stopSyntaxisUnmatchPosic=LPArray.valuePosicInArray(fieldsName, TblsTesting.ScriptSteps.STOP_WHEN_SYNTAXIS_UNMATCH.getName());
        this.stopSyntaxisFalsePosic=LPArray.valuePosicInArray(fieldsName, TblsTesting.ScriptSteps.STOP_WHEN_SYNTAXIS_FALSE.getName());
        this.alternativeTokenFldPosic=LPArray.valuePosicInArray(fieldsName, TblsTesting.ScriptSteps.ALTERNATIVE_TOKEN.getName());
        
    }
    public StringBuilder publishEvalStep(HttpServletRequest request, Integer stepId, Object[] evaluate, JSONArray functionRelatedObjects, TestingAssert tstAssert){
        return publishEvalStep(request, stepId, evaluate, functionRelatedObjects, tstAssert, null);
    }

    public StringBuilder publishEvalStep(HttpServletRequest request, Integer stepId, Object[] evaluate, JSONArray functionRelatedObjects, TestingAssert tstAssert, LocalDateTime timeStarted){
        StringBuilder fileContentBuilder = new StringBuilder(0);
        LocalDateTime timeCompleted=LPDate.getCurrentTimeStamp();
        String[] updFldNames=new String[]{TblsTesting.ScriptSteps.DATE_EXECUTION.getName(), TblsTesting.ScriptSteps.TIME_COMPLETED.getName()};
        Object[] updFldValues=new Object[]{timeCompleted, timeCompleted};
        if (timeStarted!=null){
            updFldNames=LPArray.addValueToArray1D(updFldNames, TblsTesting.ScriptSteps.TIME_STARTED.getName());
            updFldValues=LPArray.addValueToArray1D(updFldValues, timeStarted);
            BigDecimal SecondsInDateRange = LPDate.SecondsInDateRange(timeStarted, timeCompleted, true);
            updFldNames=LPArray.addValueToArray1D(updFldNames, TblsTesting.ScriptSteps.TIME_CONSUME.getName());
            updFldValues=LPArray.addValueToArray1D(updFldValues, SecondsInDateRange);            
        }        
        if (numEvaluationArguments>0 && ("DB".equals(this.inputMode)) ){
            Integer scriptId = Integer.valueOf(LPNulls.replaceNull(request.getAttribute(LPTestingParams.SCRIPT_ID).toString()));
            String procInstanceName=LPNulls.replaceNull(request.getAttribute(LPTestingParams.SCHEMA_PREFIX)).toString();
            String repositoryName=LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_TESTING.getName(), "");
            if (procInstanceName!=null && procInstanceName.length()>0)
                repositoryName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
            if (evaluate==null || evaluate.length==0){
                updFldNames=LPArray.addValueToArray1D(updFldNames, new String[]{TblsTesting.ScriptSteps.FUNCTION_SYNTAXIS.getName(), TblsTesting.ScriptSteps.EVAL_SYNTAXIS.getName()});
                updFldValues=LPArray.addValueToArray1D(updFldValues, new Object[]{"EvaluateEmpty", tstAssert.getEvalSyntaxisDiagnostic()});                
            }else{
                updFldNames=LPArray.addValueToArray1D(updFldNames, new String[]{TblsTesting.ScriptSteps.FUNCTION_SYNTAXIS.getName(), TblsTesting.ScriptSteps.EVAL_SYNTAXIS.getName()});
                updFldValues=LPArray.addValueToArray1D(updFldValues, new Object[]{evaluate[TRAP_MESSAGE_EVALUATION_POSIC], tstAssert.getEvalSyntaxisDiagnostic()});
                if (numEvaluationArguments>1){
                    updFldNames=LPArray.addValueToArray1D(updFldNames, new String[]{TblsTesting.ScriptSteps.FUNCTION_CODE.getName(), TblsTesting.ScriptSteps.EVAL_CODE.getName(),
                        TblsTesting.ScriptSteps.DYNAMIC_DATA.getName()});
                    updFldValues=LPArray.addValueToArray1D(updFldValues, new Object[]{evaluate[TRAP_MESSAGE_CODE_POSIC], tstAssert.getEvalCodeDiagnostic(),
                        functionRelatedObjects.toJSONString()});
                }
            }
            Rdbms.updateRecordFieldsByFilter(repositoryName, TblsTesting.TablesTesting.SCRIPT_STEPS.getTableName(),
                    updFldNames, updFldValues,
                    new String[]{TblsTesting.ScriptSteps.SCRIPT_ID.getName(), TblsTesting.ScriptSteps.STEP_ID.getName()}, new Object[]{scriptId, stepId});
        }
        return fileContentBuilder;
    }

    public StringBuilder publishEvalSummary(HttpServletRequest request, TestingAssertSummary tstAssertSummary){
        return publishEvalSummary(request, tstAssertSummary, null);
    }
    public StringBuilder publishEvalSummary(HttpServletRequest request, TestingAssertSummary tstAssertSummary, String summaryPhrase){
        return publishEvalSummary(request, tstAssertSummary, summaryPhrase, null);
    }

    public StringBuilder publishEvalSummary(HttpServletRequest request, TestingAssertSummary tstAssertSummary, String summaryPhrase, LocalDateTime timeStarted){
        
        StringBuilder fileContentBuilder = new StringBuilder(0);
        tstAssertSummary.notifyResults();
        LocalDateTime timeCompleted=LPDate.getCurrentTimeStamp();
        String[] updFldNames=new String[]{TblsTesting.Script.DATE_EXECUTION.getName(), TblsTesting.Script.TIME_COMPLETED.getName(), TblsTesting.Script.EVAL_TOTAL_TESTS.getName()};
        Object[] updFldValues=new Object[]{timeCompleted, timeCompleted, tstAssertSummary.getTotalTests()};        
        BigDecimal secondsInDateRange=null;        
        if (timeStarted!=null){
            updFldNames=LPArray.addValueToArray1D(updFldNames, TblsTesting.Script.TIME_STARTED.getName());
            updFldValues=LPArray.addValueToArray1D(updFldValues, timeStarted);
            secondsInDateRange = LPDate.SecondsInDateRange(timeStarted, timeCompleted, true);
            updFldNames=LPArray.addValueToArray1D(updFldNames, TblsTesting.Script.TIME_CONSUME.getName());
            updFldValues=LPArray.addValueToArray1D(updFldValues, secondsInDateRange);            
        }
        if (numEvaluationArguments>0){
            if ("DB".equals(this.inputMode)){
                if (summaryPhrase==null){
                    if (numEvaluationArguments==0) summaryPhrase="COMPLETED ALL STEPS";
                    else{
                        if (tstAssertSummary.getTotalSyntaxisMatch()==testingContent.length)
                            summaryPhrase="COMPLETED SUCCESSFULLY";
                        else{
                            summaryPhrase="COMPLETED WITH UNEXPECTED RESULTS. ";
                            if (tstAssertSummary.getTotalSyntaxisUnMatch()>0) summaryPhrase=summaryPhrase+"Unmatched="+tstAssertSummary.getTotalSyntaxisUnMatch()+". ";
                            if (tstAssertSummary.getTotalSyntaxisUndefined()>0) summaryPhrase=summaryPhrase+"Undefined="+tstAssertSummary.getTotalSyntaxisUndefined()+". ";
                        }
                    }
                }
                String fileContentSummary = LPTestingOutFormat.createSummaryTable(tstAssertSummary, numEvaluationArguments, summaryPhrase, secondsInDateRange);
                fileContentBuilder.append(fileContentSummary);
                
                if (!LPFrontEnd.servletStablishDBConection(request, null)){return fileContentBuilder;}
                Integer scriptId = Integer.valueOf(LPNulls.replaceNull(request.getAttribute(LPTestingParams.SCRIPT_ID).toString()));
                String procInstanceName=LPNulls.replaceNull(request.getAttribute(LPTestingParams.SCHEMA_PREFIX)).toString();
                String repositoryName=LPPlatform.buildSchemaName(GlobalVariables.Schemas.APP_TESTING.getName(), "");
                if (procInstanceName!=null && procInstanceName.length()>0)
                    repositoryName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName());
                updFldNames=LPArray.addValueToArray1D(updFldNames, new String[]{TblsTesting.Script.EVAL_SYNTAXIS_MATCH.getName(), TblsTesting.Script.EVAL_SYNTAXIS_UNDEFINED.getName(),
                            TblsTesting.Script.EVAL_SYNTAXIS_UNMATCH.getName()});
                updFldValues=LPArray.addValueToArray1D(updFldValues, new Object[]{tstAssertSummary.getTotalSyntaxisMatch(), tstAssertSummary.getTotalSyntaxisUndefined(), tstAssertSummary.getTotalSyntaxisUnMatch()});
                if (numEvaluationArguments>1){
                    updFldNames=LPArray.addValueToArray1D(updFldNames, new String[]{TblsTesting.Script.EVAL_CODE_MATCH.getName(), TblsTesting.Script.EVAL_CODE_UNDEFINED.getName(),
                                TblsTesting.Script.EVAL_CODE_UNMATCH.getName()});
                    updFldValues=LPArray.addValueToArray1D(updFldValues, new Object[]{tstAssertSummary.getTotalCodeMatch(), tstAssertSummary.getTotalCodeUndefined(), tstAssertSummary.getTotalCodeUnMatch()});
                }
                updFldNames=LPArray.addValueToArray1D(updFldNames,TblsTesting.Script.RUN_SUMMARY.getName());
                updFldValues=LPArray.addValueToArray1D(updFldValues, summaryPhrase);
                

                ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(request, null, true);

/*                TestingAuditIds testingAuditObj = procReqInstance.getTestingAuditObj();
                if (testingAuditObj!=null){
                JSONArray jsonContent = testingAuditObj.getJsonContent();
                updFldNames=LPArray.addValueToArray1D(updFldNames, TblsTesting.Script.AUDIT_IDS_VALUES.getName());
                updFldValues=LPArray.addValueToArray1D(updFldValues, jsonContent.toJSONString());
                }
                 */
                Object[] fieldsForSessionObjects = getFieldsForSessionObjects();
                if (fieldsForSessionObjects!=null && fieldsForSessionObjects.length>0)
                    updFldNames=LPArray.addValueToArray1D(updFldNames, (String[]) fieldsForSessionObjects[0]);
                if (fieldsForSessionObjects!=null && fieldsForSessionObjects.length>1)
                    updFldValues=LPArray.addValueToArray1D(updFldValues, (Object[]) fieldsForSessionObjects[1]);
                Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(repositoryName, TblsTesting.TablesTesting.SCRIPT.getTableName(),
                        updFldNames, updFldValues,
                        new String[]{TblsTesting.ScriptSteps.SCRIPT_ID.getName()}, new Object[]{scriptId});
                procReqInstance.killIt();
            }
        }
        return fileContentBuilder;
    }
    public Object[] getFieldsForSessionObjects(){
        String[] updFldNames=new String[]{};
        Object[] updFldValues=new Object[]{};
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForActions(null, null, true);
        TestingAuditIds testingAuditObj = procReqInstance.getTestingAuditObj();
        if (testingAuditObj!=null){
            JSONArray jsonContent = testingAuditObj.getJsonContent();
            updFldNames=LPArray.addValueToArray1D(updFldNames, TblsTesting.Script.AUDIT_IDS_VALUES.getName());
            updFldValues=LPArray.addValueToArray1D(updFldValues, jsonContent.toJSONString());
        }
        TestingBusinessRulesVisited testingBusinessRulesVisitedObj = procReqInstance.getTestingBusinessRulesVisitedObj();
        if (testingBusinessRulesVisitedObj!=null){
            JSONArray jsonContent = testingBusinessRulesVisitedObj.getJsonContent();
            updFldNames=LPArray.addValueToArray1D(updFldNames, TblsTesting.Script.BUSINESS_RULES_VISITED.getName());
            updFldValues=LPArray.addValueToArray1D(updFldValues, jsonContent.toJSONString());
        }
        TestingMessageCodeVisited testingMessageCodeVisitedObj = procReqInstance.getTestingMessageCodeVisitedObj();
        if (testingMessageCodeVisitedObj!=null){
            JSONArray jsonContent = testingMessageCodeVisitedObj.getJsonContent();
            updFldNames=LPArray.addValueToArray1D(updFldNames, TblsTesting.Script.MESSAGES_VISITED.getName());
            updFldValues=LPArray.addValueToArray1D(updFldValues, jsonContent.toJSONString());
        }
        return new Object[]{updFldNames, updFldValues};
    }
    
    /**
     *
     */
    public static final String TESTING_FILES_PATH = "http://51.75.202.142:8888/testingRepository-20200203/";

    /**
     *
     */
    public static final String TESTING_FILES_PATH_NAS = "\\\\FRANCLOUD\\fran\\LabPlanet\\testingRepository\\";

    /**
     *
     */
    public static final String TESTING_FILES_PATH_CHEMOS = "C:\\Chemos\\";

    /**
     *
     */
    public static final String TESTING_FILES_FIELD_SEPARATOR=";";

    /**
     *
     */
    public static final String TESTING_USER="labplanet";

    /**
     *
     */
    public static final String TESTING_PW="avecesllegaelmomento";

    /**
     *
     */
    public static final String MSG_DB_CON_ERROR="<th>Error connecting to the database</th>";

    public enum FileHeaderTags{NUM_HEADER_LINES("NUMHEADERLINES"), MAX_NUM_HEADER_LINES(25), SEPARATOR("="),
        NUM_TABLES("NUMTABLES"), TESTER_NAME("TESTERNAME"), NUM_EVALUATION_ARGUMENTS("NUMEVALUATIONARGUMENTS"),
        NUM_ARGUMENTS("NUMARGUMENTS"), EVALUATION_POSITION("EVALUATIONPOSITION"),
        TABLE_NAME("TABLE"), TOKEN("TOKEN")
        ;
        private FileHeaderTags(Object value){
            this.tagValue=value;
        }
        public Object getTagValue(){return this.tagValue;}
        private Object tagValue;
    }

    /**
     *
     */
    public static final String ERROR_TRAPPING_FILEHEADER_MISSING_TAGS="There are missing tags in the file header: ";

    /**
     *
     */
    public static final String BUNDLE_FILE_NAME="parameter.config.testing-html-settings";

    /**
     *
     */
    public static final String TST_ICON_MATCH =ResourceBundle.getBundle(BUNDLE_FILE_NAME).getString("labPLANET_iconMatch");

    /**
     *
     */
    public static final String TST_ICON_UNMATCH =ResourceBundle.getBundle(BUNDLE_FILE_NAME).getString("labPLANET_iconUnMatch");

    /**
     *
     */
    public static final String TST_ICON_UNDEFINED=ResourceBundle.getBundle(BUNDLE_FILE_NAME).getString("labPLANET_iconUndefined");

    /**
     *
     */
    public static final String TST_BOOLEANMATCH =ResourceBundle.getBundle(BUNDLE_FILE_NAME).getString("labPLANET_booleanMatch");

    /**
     *
     */
    public static final String TST_BOOLEANUNMATCH =ResourceBundle.getBundle(BUNDLE_FILE_NAME).getString("labPLANET_booleanUnMatch");

    /**
     *
     */
    public static final String TST_BOOLEANUNDEFINED=ResourceBundle.getBundle(BUNDLE_FILE_NAME).getString("labPLANET_booleanUndefined");

    /**
     *
     */
    public static final String TST_ERRORCODEMATCH =ResourceBundle.getBundle(BUNDLE_FILE_NAME).getString("labPLANET_errorCodeMatch");

    /**
     *
     */
    public static final String TST_ERRORCODEUNMATCH =ResourceBundle.getBundle(BUNDLE_FILE_NAME).getString("labPLANET_errorCodeUnMatch");

    /**
     *
     */
    public static final String TST_ERRORCODEUNDEFINED=ResourceBundle.getBundle(BUNDLE_FILE_NAME).getString("labPLANET_errorCodeUndefined");

    /**
     *
     * @param response
     * @return
     */
    public static HttpServletResponse responsePreparation(HttpServletResponse response){
        response.setCharacterEncoding(LPPlatform.LAB_ENCODER_UTF8);

        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        String frontendUrl = prop.getString("frontend_url");

        response.setHeader("Access-Control-Allow-Origin", frontendUrl);
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setContentType("text/html;charset=UTF-8");
        return response;
    }

    /**
     *
     * @return
     */
    public static String htmlStart(){        return "<html>";    }

    /**
     *
     * @return
     */
    public static String htmlEnd(){        return "</html>";    }

    /**
     *
     * @return
     */
    public static String bodyStart(){        return "<body>";    }

    /**
     *
     * @return
     */
    public static String bodyEnd(){        return "</body>";    }

    /**
     *
     * @param cssClassName
     * @return
     */
    public static String tableStart(String cssClassName){        return "<table class=\""+ cssClassName+"\">";    }

    /**
     *
     * @return
     */
    public static String tableEnd(){        return "</table>";    }

    /**
     *
     * @return
     */
    public static String headerStart(){        return "<th>";    }

    /**
     *
     * @return
     */
    public static String headerEnd(){        return "</th>";    }

    /**
     *
     * @return
     */
    public static String rowStart(){        return "<tr>";    }

    /**
     *
     * @return
     */
    public static String rowEnd(){        return "</tr>";    }

    /**
     *
     * @return
     */
    public static String fieldStart(){        return "<td>";    }

    /**
     *
     * @return
     */
    public static String fieldEnd(){        return "</td>";    }

    /**
     *
     * @param field
     * @return
     */
    public static String headerAddField(String field){
        String content="";
        content = content+headerStart()+LPNulls.replaceNull((String) field)+headerEnd();
        return content;
    }

    /**
     *
     * @param fields
     * @return
     */
    public static String headerAddFields(Object[] fields){
        StringBuilder content=new StringBuilder(0);
        for (Object fld: fields){
            content.append(headerStart()).append(LPNulls.replaceNull(fld).toString()).append(headerEnd());
        }
        return content.toString();
    }

    /**
     *
     * @param fields
     * @return
     */
    public static String headerAddFields(String[] fields){
        StringBuilder content=new StringBuilder(0);
        for (Object fld: fields){
            content.append(headerStart()).append(LPNulls.replaceNull(fld).toString()).append(headerEnd());
        }
        return content.toString();
    }

    /**
     *
     * @param fields
     * @param numEvaluationArguments
     * @return
     */
    public static String[] addUATColumns(String[] fields, Integer numEvaluationArguments){
        String[] newFields = new String[]{"Test #"};
        newFields=LPArray.addValueToArray1D(newFields, fields);
        newFields = LPArray.addValueToArray1D(newFields, "Time(s)");
//        newFields=LPArray.addValueToArray1D(newFields, LPNulls.replaceNull(timeConsume).toString());
        if (numEvaluationArguments>0){
            newFields=LPArray.addValueToArray1D(newFields, "Syntaxis");
            if (numEvaluationArguments>1) newFields=LPArray.addValueToArray1D(newFields, "Code");
            newFields=LPArray.addValueToArray1D(newFields, "Evaluation");
        }
        return newFields;
    }

    /**
     *
     * @param field
     * @return
     */
    public static String rowAddField(String field){
        StringBuilder content=new StringBuilder(0);
        content.append(headerStart()).append(LPNulls.replaceNull(field)).append(headerEnd());
        return content.toString();
    }

    /**
     *
     * @param fields
     * @return
     */
    public static String rowAddFields(Object[] fields){
        StringBuilder content=new StringBuilder(0);
        for (Object field: fields){
            if (field==null){
                content.append(fieldStart()).append("").append(fieldEnd());
            }else{
                content.append(fieldStart()).append(LPNulls.replaceNull(field).toString()).append(fieldEnd());
            }
        }
        return content.toString();
    }

    /**
     *
     * @param csvPathName
     * @param fileContent
     */
    public static void createLogFile(String csvPathName, String fileContent){
        csvPathName = csvPathName.replace(".txt", ".html");
        File file = new File(csvPathName);
            try (FileWriter fileWriter = new FileWriter(file)) {
                if (file.exists()) {
                  if (!file.delete()){return;}
//                  file.delete();
                }
                if (!file.createNewFile()){return;}
//                file.createNewFile();
                if (!file.exists()){
                    return;
                }
                fileWriter.write(fileContent);
                fileWriter.flush();
            } catch (IOException ex) {Logger.getLogger(LPTestingOutFormat.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    /**
     *
     * @param servletName
     * @param fileName
     * @return
     */
    public static String getHtmlStyleHeader(String servletName, String fileName, Integer scriptId, String procInstanceName) {
        String fileContent = "";
        fileContent = fileContent + "<!DOCTYPE html>" + "";
        fileContent = fileContent + "<html>" + "";
        fileContent = fileContent + "<head>" + "";
        fileContent = fileContent + "<style>";
        ResourceBundle prop = ResourceBundle.getBundle(BUNDLE_FILE_NAME);
        fileContent = fileContent + prop.getString("testingTableStyleSummary1");
        fileContent = fileContent + prop.getString("testingTableStyleSummary2");
        fileContent = fileContent + prop.getString("testingTableStyleSummary3");
        fileContent = fileContent + prop.getString("testingTableStyleSummary4");
        fileContent = fileContent + prop.getString("testingTableStyleSummary5");
        fileContent = fileContent + prop.getString("testingTableStyle1");
        fileContent = fileContent + prop.getString("testingTableStyle2");
        fileContent = fileContent + prop.getString("testingTableStyle3");
        fileContent = fileContent + prop.getString("testingTableStyle4");
        fileContent = fileContent + prop.getString("testingTableStyle5");
        fileContent = fileContent + "</style>";
        fileContent = fileContent + "<title>Servlet " + servletName + "</title>" + "";
        fileContent = fileContent + "</head>" + "";
        fileContent = fileContent + "<body>" + "\n";
        if (scriptId==null)
            fileContent = fileContent + "<h2>File "+fileName+" being tested on "+LPDate.getCurrentTimeStamp().toString()+"</h2>" + "";
        else
            fileContent = fileContent + "<h2>Script "+scriptId+" being tested on "+LPDate.getCurrentTimeStamp().toString()+"</h2>" + "";
        if (procInstanceName!=null)
            fileContent = fileContent + "<h2>Procedure Instance Name: " + procInstanceName+"</h2>" + "";
        fileContent = fileContent + "<h2>Tester " + servletName + "</h2>" + "";

        fileContent = fileContent + "<table id=\"scriptTable\">";
        return fileContent;
    }

    /**
     *
     * @param csvContent
     * @return
     */

    public static HashMap<String, Object>  getCSVHeaderTester(String[][] csvContent){
        HashMap<String, Object> fieldsRequired = new HashMap();
        fieldsRequired.put(FileHeaderTags.MAX_NUM_HEADER_LINES.toString(), "");   
        fieldsRequired.put(FileHeaderTags.NUM_TABLES.toString(), "");
        fieldsRequired.put(FileHeaderTags.TESTER_NAME.toString(), "");
        fieldsRequired.put(FileHeaderTags.NUM_EVALUATION_ARGUMENTS.toString(), "");
        fieldsRequired.put(FileHeaderTags.TOKEN.toString(), "");
        return getCSVHeaderManager(fieldsRequired, csvContent);
    }

    public static HashMap<String, Object>  getCSVHeader(String[][] csvContent){
        HashMap<String, Object> fieldsRequired = new HashMap();
        fieldsRequired.put(FileHeaderTags.MAX_NUM_HEADER_LINES.toString(), "");   
        fieldsRequired.put(FileHeaderTags.NUM_TABLES.toString(), "");
        fieldsRequired.put(FileHeaderTags.NUM_EVALUATION_ARGUMENTS.toString(), "");
        return getCSVHeaderManager(fieldsRequired, csvContent);
    }

    private static HashMap<String, Object>  getCSVHeaderManager(HashMap<String, Object> fieldsRequired, String[][] csvContent){
        HashMap<String, Object> hm = new HashMap();
        Integer maxHeaderLines=Integer.valueOf(FileHeaderTags.MAX_NUM_HEADER_LINES.getTagValue().toString());
        if (csvContent.length<maxHeaderLines){maxHeaderLines=csvContent.length-1;}
        Integer iLineParsed = 0;
        Boolean continueParsing=true;
        while (continueParsing){
            String getLineKey = LPNulls.replaceNull(csvContent[iLineParsed][0]).toUpperCase();
            String getLineValue = LPNulls.replaceNull(csvContent[iLineParsed][1]);

            FileHeaderTags lineKeyTag = FileHeaderTags.valueOf(getLineKey.toUpperCase());


            if (fieldsRequired.containsKey(getLineKey)){
                switch (lineKeyTag){
                    case NUM_HEADER_LINES:
                        maxHeaderLines=Integer.parseInt(getLineValue);
                        break;
                    case NUM_TABLES:
                        Integer numTbls=Integer.parseInt(getLineValue);
                        for (int iNumTbls=1; iNumTbls<=numTbls; iNumTbls++){
                            fieldsRequired.put(FileHeaderTags.TABLE_NAME.getTagValue().toString()+String.valueOf(iNumTbls), "");
                        }
                        break;
                    default:
                        break;
                }
                hm.put(getLineKey, getLineValue);
                fieldsRequired.remove(getLineKey);
            }
            if (iLineParsed>=maxHeaderLines){continueParsing=false;}
            iLineParsed++;
        }
        if (!fieldsRequired.isEmpty()){
            hm.clear();
            hm.put(LPPlatform.LAB_FALSE, LPHashMap.hashMapToStringKeys(fieldsRequired, ", "));
        }
        return hm;
    }

    /**
     *
     * @param tstAssert
     * @param numArguments
     * @return
     */
    public static String createSummaryTable(TestingAssertSummary tstAssert, Integer numArguments, String scriptSummaryPhrase, BigDecimal timeConsume){
        String fileContentHeaderSummary = LPTestingOutFormat.tableStart("summary")+rowStart();
        String fileContentSummary =rowStart();
            fileContentHeaderSummary=fileContentHeaderSummary+headerAddField("Execution Summary");
            fileContentSummary = fileContentSummary +rowAddField(LPNulls.replaceNull(scriptSummaryPhrase));
        if (numArguments>0){
            fileContentHeaderSummary=fileContentHeaderSummary+headerAddField("Time Consume (s)");
            fileContentSummary = fileContentSummary +rowAddField(LPNulls.replaceNull(timeConsume).toString());
            fileContentHeaderSummary=fileContentHeaderSummary+headerAddField("Total Tests");
            fileContentSummary = fileContentSummary +rowAddField(tstAssert.getTotalTests().toString());
            fileContentHeaderSummary=fileContentHeaderSummary+headerAddField("Syntaxis Match "+LPTestingOutFormat.TST_ICON_MATCH);
            fileContentSummary = fileContentSummary +LPTestingOutFormat.rowAddField(tstAssert.getTotalSyntaxisMatch().toString());
            fileContentHeaderSummary=fileContentHeaderSummary+headerAddField("Syntaxis Undefined "+LPTestingOutFormat.TST_ICON_UNDEFINED);
            fileContentSummary = fileContentSummary +LPTestingOutFormat.rowAddField(tstAssert.getTotalSyntaxisUndefined().toString());
            fileContentHeaderSummary=fileContentHeaderSummary+headerAddField("Syntaxis Unmatch "+LPTestingOutFormat.TST_ICON_UNMATCH);
            fileContentSummary = fileContentSummary +LPTestingOutFormat.rowAddField(tstAssert.getTotalSyntaxisUnMatch().toString());
        }
        if (numArguments>1){
            fileContentHeaderSummary=fileContentHeaderSummary+headerAddField("Code Match "+LPTestingOutFormat.TST_ICON_MATCH);
            fileContentSummary = fileContentSummary +LPTestingOutFormat.rowAddField(tstAssert.getTotalCodeMatch().toString());
            fileContentHeaderSummary=fileContentHeaderSummary+headerAddField("Code Undefined "+LPTestingOutFormat.TST_ICON_UNDEFINED);
            fileContentSummary = fileContentSummary +LPTestingOutFormat.rowAddField(tstAssert.getTotalCodeUndefined().toString());
            fileContentHeaderSummary=fileContentHeaderSummary+headerAddField("Total ErrorCode Unmatch "+LPTestingOutFormat.TST_ICON_UNMATCH);
            fileContentSummary = fileContentSummary +LPTestingOutFormat.rowAddField(tstAssert.getTotalCodeUnMatch().toString());
        }
        fileContentSummary = fileContentHeaderSummary+fileContentSummary +rowEnd();
        fileContentSummary = fileContentSummary +tableEnd();
        return fileContentSummary;
    }

    /**
     *
     * @param content
     * @return
     */
    public static String convertArrayInHtmlTable(Object[][] content){
        StringBuilder fileContentTable = new StringBuilder(0);
        fileContentTable.append(LPTestingOutFormat.tableStart(""));
        fileContentTable.append(headerAddFields(content[0])).append(headerEnd());
        for (int iRows=1; iRows< content.length; iRows++){
            fileContentTable.append(rowStart()).append(rowAddFields(content[iRows])).append(rowEnd());
        }
        fileContentTable.append(LPTestingOutFormat.tableEnd());
        return fileContentTable.toString();
    }

    /**
     *
     * @param table1Header
     * @param numEvaluationArguments
     * @return
     */
    public static String createTableWithHeader(String table1Header, Integer numEvaluationArguments){
        String fileContentTable = LPTestingOutFormat.tableStart("");
        if (numEvaluationArguments==0)
            fileContentTable=fileContentTable+rowStart()+headerAddFields(table1Header.split(TESTING_FILES_FIELD_SEPARATOR))+rowEnd();
        else
            fileContentTable=fileContentTable+headerAddFields(addUATColumns(table1Header.split(TESTING_FILES_FIELD_SEPARATOR), numEvaluationArguments));
        fileContentTable=fileContentTable+rowStart();
        return fileContentTable;
    }

    /**
     *
     * @param value
     * @return
     */
    public static BigDecimal csvExtractFieldValueBigDecimal(Object value){
        if (value==null) return null;
        try{
            return new BigDecimal(value.toString());
        }catch(Exception e){return null;}
    }

    /**
     *
     * @param value
     * @return
     */
    public static Boolean csvExtractFieldValueBoolean(Object value){
        if (value==null) return false;
        if (value.toString().length()==0){return false;}
        try{
            return Boolean.valueOf(value.toString());
        }catch(Exception e){return false;}
    }

    /**
     *
     * @param value
     * @return
     */
    public static String csvExtractFieldValueString(Object value){
        if (value==null) return null;
        try{
            return value.toString();
        }catch(Exception e){return null;}
    }

    /**
     *
     * @param value
     * @return
     */
    public static String[] csvExtractFieldValueStringArr(Object value){
        if (value==null) return new String[0];
        try{
            return value.toString().split("\\|");
        }catch(Exception e){return new String[0];}
    }

    /**
     *
     * @param value
     * @return
     */
    public static Float csvExtractFieldValueFloat(Object value){
        if (value==null) return null;
        try{
            return Float.valueOf(value.toString());
        }catch(NumberFormatException e){return null;}
    }

    /**
     *
     * @param value
     * @return
     */
    public static Integer csvExtractFieldValueInteger(Object value){
        if (value==null) return null;
        try{
            return Integer.valueOf(value.toString());
        }catch(NumberFormatException e){return null;}
    }

    /**
     *
     * @param value
     * @return
     */
    public static Date csvExtractFieldValueDate(Object value){
        if (value==null) return null;
        try{
            return Date.valueOf(value.toString());
        }catch(NumberFormatException e){return null;}
    }

    public static LocalDateTime csvExtractFieldValueDateTime(Object value){
        if (value==null) return null;
        try{
            return LocalDateTime.parse(value.toString());
            //return LocalDateTime.valueOf(value.toString());
        }catch(Exception e){return null;}
    }

    /**
     *
     * @param csvFileName
     * @return
     */
    public static Object[][] getCSVFileContent(String csvFileName) {
        String csvPathName = LPTestingOutFormat.TESTING_FILES_PATH+csvFileName;
        String csvFileSeparator=LPTestingOutFormat.TESTING_FILES_FIELD_SEPARATOR;
        return LPArray.convertCSVinArray(csvPathName, csvFileSeparator);
    }

    private static int getStepObjectPosic(JsonObject jsonObject){
            int stepObjectPosic = 1;
            try{
                stepObjectPosic = jsonObject.get("object_posic").getAsInt();
            }catch(Exception ex){stepObjectPosic = 1;}
            return stepObjectPosic;
    }

    public static String getAttributeValue(Object value, Object[][] scriptSteps){
        try{
            if (!value.toString().contains("step")) return specialTagFilter(LPNulls.replaceNull(value.toString()));

            Object[] objToJsonObj = LPJson.convertToJsonObjectStringedObject(value.toString());            
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(objToJsonObj[0].toString()))
               return LPPlatform.LAB_FALSE;
            if (objToJsonObj.length==3){
                String h="2";
            }
            JsonObject jsonObject=(JsonObject) objToJsonObj[1];

            int stepNumber = jsonObject.get("step").getAsInt();
            String stepObjectType = jsonObject.get("object_type").getAsString();
            int stepObjectPosic=getStepObjectPosic(jsonObject);
            Integer stepPosic=LPArray.valuePosicInArray(LPArray.getColumnFromArray2D(scriptSteps, scriptSteps[0].length-6), stepNumber);
            if (stepPosic==-1) return "";

            JsonArray jsonArr=LPJson.convertToJsonArrayStringedObject(scriptSteps[stepPosic][scriptSteps[0].length-1].toString());
            Integer numObjectsFound=0;
            for (int i = 0; i < jsonArr.size(); i++) {
                JsonObject object = (JsonObject) jsonArr.get(i);
                String objType=object.get("object_type").getAsString();
                if (objType.equalsIgnoreCase(stepObjectType)){
                    numObjectsFound++;
                    if (numObjectsFound.equals(stepObjectPosic)){
                       String valueReplaced= object.get("object_name").getAsString();
                       if (objToJsonObj.length==3)valueReplaced=valueReplaced+"*"+objToJsonObj[2].toString();
                       return valueReplaced;
                   }
                }
            }
            return "";
        }catch(Exception ex){ return "";}
    }
    private static String specialTagFilter(String value){
        String tagName="{TZ_DATE}";
        if (value.contains(tagName)) value=value.replace(tagName, LPDate.getCurrentTimeStamp().toString());
        return value;
    }


    /**
     * @return the inputMode
     */
    public String getInputMode() {
        return inputMode;
    }

    /**
     * @return the testingContent
     */
    public Object[][] getTestingContent() {
        return testingContent;
    }

    /**
     * @return the filePathName
     */
    public String getFilePathName() {
        return filePathName;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }
    public Integer getActionNamePosic() {
        return actionNamePosic;
    }
    public Integer getAuditReasonPosic() {
        return auditReasonPosic;
    }
    public Integer getStepIdPosic() {
        return stepIdPosic;
    }
    public Integer getStopSyntaxisUnmatchPosic() {
        return stopSyntaxisUnmatchPosic;
    }
    public Integer getStopSyntaxisFalsePosic() {
        return stopSyntaxisFalsePosic;
    }
    public Integer getAlternativeTokenFldPosic() {
        return alternativeTokenFldPosic;
    }
    

    /**
     * @return the htmlStyleHeader
     */
    public StringBuilder getHtmlStyleHeader() {
        return htmlStyleHeader;
    }

    /**
     * @return the numEvaluationArguments
     */
    public Integer getNumEvaluationArguments() {
        return numEvaluationArguments;
    }

    public Object[] checkMissingMandatoryParamValuesByCall(LPAPIArguments[] args, Object[] testingRowValues){
        String missingValues="";
        for (int i=0;i<args.length;i++){
            String curArgValue = LPTestingOutFormat.csvExtractFieldValueString(testingRowValues[getActionNamePosic()+i]);
            if (LPNulls.replaceNull(curArgValue).length()==0  && args[i].getMandatory()){
                if (missingValues.length()>0) missingValues=missingValues+", ";
                missingValues=missingValues+args[i].getName();
            }
        }
        if (missingValues.length()==0)
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, "allFine", null, true);
        else
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, ApiErrorTraping.MANDATORY_PARAMS_MISSING, new Object[]{missingValues});
    }
    public static void cleanLastRun(String procInstanceName, Integer scriptId){
        String[] scriptFieldName=new String[]{TblsTesting.Script.RUN_SUMMARY.getName(), TblsTesting.Script.EVAL_TOTAL_TESTS.getName(), 
            TblsTesting.Script.EVAL_SYNTAXIS_MATCH.getName(), TblsTesting.Script.EVAL_SYNTAXIS_UNMATCH.getName(), 
            TblsTesting.Script.EVAL_SYNTAXIS_UNDEFINED.getName(), TblsTesting.Script.EVAL_CODE_MATCH.getName(), 
            TblsTesting.Script.EVAL_CODE_UNMATCH.getName(), TblsTesting.Script.EVAL_CODE_UNDEFINED.getName(), 
            TblsTesting.Script.DATE_EXECUTION.getName(), TblsTesting.Script.DB_ERRORS_IDS_VALUES.getName(), TblsTesting.Script.MSG_ERRORS_IDS_VALUES.getName()};
        Object[] scriptFieldValue=new Object[]{"NULL>>>STRING","NULL>>>INTEGER", "NULL>>>INTEGER", "NULL>>>INTEGER", "NULL>>>INTEGER", "NULL>>>INTEGER", "NULL>>>INTEGER", "NULL>>>INTEGER","NULL>>>DATETIME", "NULL>>>STRING", "NULL>>>STRING"};

        Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT.getTableName(),
            scriptFieldName, scriptFieldValue,
            new String[]{TblsTesting.Script.SCRIPT_ID.getName()}, new Object[]{scriptId});
        Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT_STEPS.getTableName(),
            new String[]{TblsTesting.ScriptSteps.FUNCTION_SYNTAXIS.getName(), TblsTesting.ScriptSteps.FUNCTION_CODE.getName(), TblsTesting.ScriptSteps.EVAL_SYNTAXIS.getName(), TblsTesting.ScriptSteps.EVAL_CODE.getName(), TblsTesting.ScriptSteps.DATE_EXECUTION.getName()},
            new Object[]{"NULL>>>STRING", "NULL>>>STRING", "NULL>>>STRING", "NULL>>>STRING","NULL>>>DATETIME"},
            new String[]{TblsTesting.ScriptSteps.SCRIPT_ID.getName()}, new Object[]{scriptId});
    }
    public static void getIdsBefore(String procInstanceName, Integer scriptId, Object[] scriptTblInfo){
        if (scriptTblInfo[2]!=null && scriptTblInfo[2].toString().length()>0)
            LPTestingOutFormat.setAuditIndexValues(procInstanceName, scriptId, scriptTblInfo[2].toString(), "before");

        if (scriptTblInfo[3]!=null && Boolean.valueOf(scriptTblInfo[3].toString()))
            LPTestingOutFormat.setDbErrorIndexValues(procInstanceName, scriptId, "before");

        if (scriptTblInfo[4]!=null && Boolean.valueOf(scriptTblInfo[4].toString()))
            LPTestingOutFormat.setMessagesErrorIndexValues(procInstanceName, scriptId, "before");
    }
    
    public static void setDbErrorIndexValues(String procInstanceName, Integer scriptId, String moment){
        JSONArray auditIndexInfo=new JSONArray();       
        auditIndexInfo.add(getScriptCurrentFldValue(procInstanceName, scriptId, TblsTesting.Script.DB_ERRORS_IDS_VALUES.getName()));
        auditIndexInfo.add(getScriptDbErrorIncrements(procInstanceName, scriptId, moment));
        if (auditIndexInfo!=null){
            Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT.getTableName(),
                new String[]{TblsTesting.Script.DB_ERRORS_IDS_VALUES.getName()},
                new Object[]{auditIndexInfo.toJSONString()},
                new String[]{TblsTesting.Script.SCRIPT_ID.getName()}, new Object[]{scriptId});
        }
    }

    public static void setMessagesErrorIndexValues(String procInstanceName, Integer scriptId, String moment){
        JSONArray auditIndexInfo=new JSONArray();       
        auditIndexInfo.add(getScriptCurrentFldValue(procInstanceName, scriptId, TblsTesting.Script.MSG_ERRORS_IDS_VALUES.getName()));
        auditIndexInfo.add(getScriptPropertiesErrorIncrements(procInstanceName, scriptId, moment));
        if (auditIndexInfo!=null){
            Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT.getTableName(),
                new String[]{TblsTesting.Script.MSG_ERRORS_IDS_VALUES.getName()},
                new Object[]{auditIndexInfo.toJSONString()},
                new String[]{TblsTesting.Script.SCRIPT_ID.getName()}, new Object[]{scriptId});
        }
    }
    
    public static void setAuditIndexValues(String procInstanceName, Integer scriptId, String scriptAuditIds, String moment){
        JSONArray auditIndexInfo=getScriptAuditIncrements(procInstanceName, scriptId, scriptAuditIds, moment);
        if (auditIndexInfo!=null){
            Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT.getTableName(),
                new String[]{TblsTesting.Script.AUDIT_IDS_VALUES.getName()},
                new Object[]{auditIndexInfo.toJSONString()},
                new String[]{TblsTesting.Script.SCRIPT_ID.getName()}, new Object[]{scriptId});
        }
    }
    private static JSONArray getScriptAuditIncrements(String procInstanceName, Integer scriptId, String scriptAuditIds, String moment){
        if (scriptAuditIds==null) return null;
        if (moment==null) return null;
        String[] auditIds=scriptAuditIds.split("\\|");
        JSONArray indxInfo=new JSONArray();
        for (String curAuditId: auditIds){
            String[] auditIdInfo=curAuditId.split("\\*");
            if (auditIdInfo.length==2 && auditIdInfo[0].length()>0 && auditIdInfo[1].length()>0){
                Object[] dbGetIndexLastNumberInUse = dbGetIndexLastNumberInUse(procInstanceName, auditIdInfo[0], auditIdInfo[1], null);
                JSONObject currIndxInfo=new JSONObject();
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbGetIndexLastNumberInUse[0].toString()))
                    currIndxInfo.put(Arrays.toString(auditIdInfo).replace("\\*", "_")+"_"+moment, "error getting the value");
                else
                    currIndxInfo.put(Arrays.toString(auditIdInfo).replace("\\*", "_")+"_"+moment, dbGetIndexLastNumberInUse[dbGetIndexLastNumberInUse.length-1]);
                indxInfo.add(currIndxInfo);
            }
        }
        return indxInfo;
    }
    private static JSONArray getScriptDbErrorIncrements(String procInstanceName, Integer scriptId, String moment){
        if (moment==null) return null;
        JSONArray indxInfo=new JSONArray();
        String indexName="zzz_db_error_log_id_seq";
        Object[] dbGetIndexLastNumberInUse = dbGetIndexLastNumberInUse(GlobalVariables.Schemas.CONFIG.getName(), "", null, indexName);
        JSONObject currIndxInfo=new JSONObject();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbGetIndexLastNumberInUse[0].toString()))
            currIndxInfo.put(indexName.replace("\\*", "_")+"_"+moment, "error getting the value");
        else
            currIndxInfo.put(indexName.replace("\\*", "_")+"_"+moment, dbGetIndexLastNumberInUse[dbGetIndexLastNumberInUse.length-1]);
        indxInfo.add(currIndxInfo);
        return indxInfo;
    }
    private static JSONArray getScriptPropertiesErrorIncrements(String procInstanceName, Integer scriptId, String moment){
        if (moment==null) return null;
        JSONArray indxInfo=new JSONArray();
        String indexName="zzz_properties_error_id_seq";
        Object[] dbGetIndexLastNumberInUse = dbGetIndexLastNumberInUse(GlobalVariables.Schemas.CONFIG.getName(), "", null, indexName);
        JSONObject currIndxInfo=new JSONObject();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbGetIndexLastNumberInUse[0].toString()))
            currIndxInfo.put(indexName.replace("\\*", "_")+"_"+moment, "error getting the value");
        else
            currIndxInfo.put(indexName.replace("\\*", "_")+"_"+moment, dbGetIndexLastNumberInUse[dbGetIndexLastNumberInUse.length-1]);
        indxInfo.add(currIndxInfo);
        return indxInfo;
    }
    private static String getScriptCurrentFldValue(String procInstanceName, Integer scriptId, String fieldName){
        String fldInfo="";
        Object[][] recordFieldsByFilter = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.TESTING.getName()), TblsTesting.TablesTesting.SCRIPT.getTableName(), 
            new String[]{TblsTesting.Script.SCRIPT_ID.getName()}, new Object[]{scriptId}, 
            new String[]{fieldName});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(recordFieldsByFilter[0][0].toString())){
            return recordFieldsByFilter[0][0].toString();
        }
        return fldInfo;
    }
    public static StringBuilder businessRulesTable(){
        StringBuilder fileContentTable1Builder=new StringBuilder(0);
        fileContentTable1Builder.append(LPTestingOutFormat.tableStart(""));            
        fileContentTable1Builder.append(LPTestingOutFormat.headerStart()).append("Rule Name").append(LPTestingOutFormat.headerEnd());
        fileContentTable1Builder.append(LPTestingOutFormat.headerStart()).append("Rule Value").append(LPTestingOutFormat.headerEnd());
        Object[][] SessionBusinessRulesList = BusinessRules.SessionBusinessRulesList(); 
        for (Object[] curRl: SessionBusinessRulesList){
            fileContentTable1Builder.append(LPTestingOutFormat.rowStart()).append(LPTestingOutFormat.fieldStart())
                .append(curRl[0]).append(LPTestingOutFormat.fieldEnd());
            fileContentTable1Builder.append(LPTestingOutFormat.fieldStart()).append(curRl[1]).append(LPTestingOutFormat.fieldEnd())
                .append(LPTestingOutFormat.rowEnd());
        }
        fileContentTable1Builder.append(LPTestingOutFormat.tableEnd());
        return fileContentTable1Builder;
    }
}
