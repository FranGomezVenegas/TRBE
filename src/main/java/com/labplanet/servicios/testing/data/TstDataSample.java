/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.testing.data;

import static com.labplanet.servicios.app.TestingAPIActions.scriptExecutionEvidenceSave;
import lbplanet.utilities.LPPlatform;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPNulls;
import com.labplanet.servicios.modulesample.SampleAPIParams.SampleAPIactionsEndpoints;
import databases.Rdbms;
import databases.TblsData;
import functionaljavaa.businessrules.ActionsControl;
import functionaljavaa.businessrules.BusinessRules;
import functionaljavaa.changeofcustody.ChangeOfCustody;
import functionaljavaa.samplestructure.DataSample;
import functionaljavaa.samplestructure.DataSampleAnalysis;
import functionaljavaa.samplestructure.DataSampleAnalysisResult;
import functionaljavaa.modulesample.DataModuleSampleAnalysis;
import functionaljavaa.modulesample.DataModuleSampleAnalysisResult;
import functionaljavaa.samplestructure.DataSampleIncubation;
import functionaljavaa.testingscripts.LPTestingParams;
import functionaljavaa.testingscripts.TestingAssert;
import functionaljavaa.testingscripts.TestingAssertSummary;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPHttp;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
/**
 *
 * @author Administrator
 */
public class TstDataSample extends HttpServlet {

    /**
     *
     */
    public static final String OBJECT_LEVEL_SAMPLE="SAMPLE";

    /**
     *
     */
    public static final String OBJECT_LEVEL_TEST="TEST";

    /**
     *
     */
    public static final String OBJECT_LEVEL_RESULT="RESULT";

    /**
     *
     */
    public static final String LBL_OBJECT_ID="ObjectId";

    /**
     *
     */
    public static final String LBL_OBJECT_LEVEL="objectLevel";

    /**
     *
     */
    public static final String LBL_OBJECT_USER_NAME="userName";

    /**
     *
     */
    public static final String LBL_OBJECT_FIELD_NAMES="fieldNames";

    /**
     *
     */
    public static final String LOD_JAVASCRIPT_FORMULA="C:\\home\\myResult.js";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
        response = LPTestingOutFormat.responsePreparation(response);        
        String csvFileName = "dataSampleStructure.txt";      
        Object[][] dataSample2D = new Object[1][6];
        Object[] dataSample = new Object[6];
        DataModuleSampleAnalysis smpAna = new DataModuleSampleAnalysis();   
        DataModuleSampleAnalysisResult moduleSmpAnaRes = new DataModuleSampleAnalysisResult();   
        DataSample smp = new DataSample(smpAna);   
        DataSampleAnalysisResult smpAnaRes = new functionaljavaa.samplestructure.DataSampleAnalysisResult(moduleSmpAnaRes);   
        
        TestingAssertSummary tstAssertSummary = new TestingAssertSummary();

        String csvPathName = LPTestingOutFormat.TESTING_FILES_PATH+csvFileName; 
        String csvFileSeparator=LPTestingOutFormat.TESTING_FILES_FIELD_SEPARATOR;
        
        Object[][] csvFileContent = LPArray.convertCSVinArray(csvPathName, csvFileSeparator); 
        StringBuilder fileContentBuilder = new StringBuilder(0);
        fileContentBuilder.append(LPTestingOutFormat.getHtmlStyleHeader(this.getClass().getSimpleName(), csvFileName, null, null));
        try (PrintWriter out = response.getWriter()) {
            Map<String, Object> csvHeaderTags = LPTestingOutFormat.getCSVHeader(LPArray.convertCSVinArray(csvPathName, "="));
            if (csvHeaderTags.containsKey(LPPlatform.LAB_FALSE)){
                fileContentBuilder.append("There are missing tags in the file header: ").append(csvHeaderTags.get(LPPlatform.LAB_FALSE));
                out.println(fileContentBuilder.toString()); 
                return;
            }            
            if (Rdbms.getRdbms().startRdbms()==null){fileContentBuilder.append("Connection to the database not established");return;}
                
            Integer numEvaluationArguments = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.NUM_EVALUATION_ARGUMENTS.getTagValue().toString()).toString());   
            Integer numHeaderLines = Integer.valueOf(csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.NUM_HEADER_LINES.getTagValue().toString()).toString());   
            String table1Header = csvHeaderTags.get(LPTestingOutFormat.FileHeaderTags.TABLE_NAME.getTagValue().toString()+"1").toString();               
            StringBuilder fileContentTable1Builder = new StringBuilder(0);
            fileContentTable1Builder.append(LPTestingOutFormat.createTableWithHeader(table1Header, numEvaluationArguments));
          
            for (Integer iLines=numHeaderLines;iLines<csvFileContent.length;iLines++){
                tstAssertSummary.increaseTotalTests();
                TestingAssert tstAssert = new TestingAssert(csvFileContent[iLines], numEvaluationArguments, false);

                Integer lineNumCols = csvFileContent[0].length-1;                                
                String procInstanceName = null;
                if (lineNumCols>=numEvaluationArguments)
                    {procInstanceName=LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments]);}
                String userName = null;
                if (lineNumCols>=numEvaluationArguments+1)
                    userName = LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+1]);
                String userRole = null;
                if (lineNumCols>=numEvaluationArguments+2)
                    userRole = LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+2]);
                String actionName = null;
                if (lineNumCols>=numEvaluationArguments+3)                
                    actionName = LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+3]);
                
                fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{iLines-numHeaderLines+1, procInstanceName, userName, userRole, actionName}));
                SampleAPIactionsEndpoints endPoint = null;
                try{
                    endPoint = SampleAPIactionsEndpoints.valueOf(actionName.toUpperCase());
                }catch(Exception e){
                            dataSample[0] = "LABPLANET_FALSE";
                            dataSample[1] = "function "+actionName+" not recognized"; dataSample[2] = ""; dataSample[3] = ""; dataSample[4] = ""; dataSample[5] = "function "+actionName+" not recognized"; 
                }
                if (endPoint!=null){                
                    Object[] areMandatoryParamsInResponse = LPHttp.areEndPointMandatoryParamsInApiRequest(request, endPoint.getArguments());
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                            dataSample[0] = areMandatoryParamsInResponse[1].toString();
                            dataSample[1] = ""; dataSample[2] = ""; dataSample[3] = ""; dataSample[4] = ""; dataSample[5] = "";                         
                    }else{
                        BusinessRules bi=new BusinessRules(procInstanceName, null);
                        InternalMessage actionEnabledForRole = ActionsControl.procUserRoleActionEnabled(procInstanceName, userRole, actionName, bi);
                        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabledForRole.getDiagnostic())){
                            if (SampleAPIactionsEndpoints.GETSAMPLEINFO.getName().equalsIgnoreCase(actionName)){                
                                    dataSample2D[0][0] = actionEnabledForRole.getDiagnostic();
                                    dataSample2D[0][1] = actionEnabledForRole.getDiagnostic(); dataSample2D[0][2] = actionEnabledForRole.getDiagnostic(); 
                                    dataSample2D[0][3] = actionEnabledForRole.getDiagnostic(); dataSample2D[0][4] = Arrays.toString(actionEnabledForRole.getMessageCodeVariables()); 
                                    dataSample2D[0][5] = Arrays.toString(actionEnabledForRole.getMessageCodeVariables()); 
                            }else{        
                                    dataSample[0] = actionEnabledForRole.getDiagnostic(); dataSample[1] = actionEnabledForRole.getDiagnostic(); dataSample[2] = actionEnabledForRole.getDiagnostic();
                                    dataSample[3] = actionEnabledForRole.getDiagnostic(); dataSample[4] = Arrays.toString(actionEnabledForRole.getMessageCodeVariables()); dataSample[5] = Arrays.toString(actionEnabledForRole.getMessageCodeVariables()); 
                            }                      
                        }else{  
                            switch (endPoint){
                                case LOGSAMPLE:                            
                                    String sampleTemplate=null;
                                    Integer sampleTemplateVersion=null;
                                    String[] sampleTemplateInfo = new String[0];
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleTemplateInfo = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+4]);
                                    sampleTemplate = sampleTemplateInfo[0];
                                    sampleTemplateVersion = Integer.parseInt(sampleTemplateInfo[1]);
                                    String[] fieldName = null;
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        fieldName = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+5]);
                                    String[] fieldValue = null;
                                    if (lineNumCols>=numEvaluationArguments+6)                
                                        fieldValue = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+6]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"templateName, templateVersion, fieldNames, fieldValues", 
                                            sampleTemplate+", "+sampleTemplateVersion.toString()+", "+Arrays.toString(fieldName)+", "+Arrays.toString(fieldValue)}));                              
                                    dataSample = smp.logSample(sampleTemplate, sampleTemplateVersion, fieldName, fieldValue, null);
                                    break;
                                case RECEIVESAMPLE:  
                                    Integer sampleId = null;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleId = LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"sampleId, receiver", LPNulls.replaceNull(sampleId).toString()+", "+userName}));                              
                                    dataSample = smp.sampleReception(sampleId);
                                    break;       
                                case CHANGESAMPLINGDATE:
                                    sampleId = null;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleId = LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    LocalDateTime newDate = null;
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        newDate = LPTestingOutFormat.csvExtractFieldValueDateTime(csvFileContent[iLines][numEvaluationArguments+5]);                            
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"sampleId, userName, newDate", 
                                            LPNulls.replaceNull(sampleId).toString()+", "+userName+", "+LPNulls.replaceNull(newDate).toString()}));                              
                                    dataSample = smp.changeSamplingDate(sampleId, newDate);
                                    break;       
                                case SAMPLINGCOMMENTADD:
                                    sampleId = null;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleId = LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    String comment=null;
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        comment = LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+5]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"sampleId, userName, comment", LPNulls.replaceNull(sampleId).toString()+", "+userName+", "+comment}));                              
                                    dataSample = smp.sampleReceptionCommentAdd(sampleId, comment);
                                    break;       
                                case SAMPLINGCOMMENTREMOVE:
                                    sampleId = null;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleId = LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"sampleId, userName, comment", LPNulls.replaceNull(sampleId).toString()+", "+userName}));                              
                                    dataSample = smp.sampleReceptionCommentRemove(sampleId);
                                    break;       
                                case INCUBATIONSTART:
                                    sampleId = null;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleId = LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"sampleId, userName", LPNulls.replaceNull(sampleId).toString()+", "+userName}));                   
                                    String incubName=null;
                                    BigDecimal tempReading=null;
                                    dataSample = DataSampleIncubation.setSampleStartIncubationDateTime(sampleId, 1, incubName, tempReading);
                                    break;       
                                case INCUBATIONEND:
                                    sampleId = null;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleId = LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"sampleId, userName", LPNulls.replaceNull(sampleId).toString()+", "+userName})); 
                                    incubName=null;
                                    tempReading=null;
                                    dataSample = DataSampleIncubation.setSampleEndIncubationDateTime(sampleId, 1, incubName, tempReading, null);
                                    dataSample=(Object[])dataSample[0];
                                    break;       
                                case SAMPLEANALYSISADD:
                                    sampleId=null;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleId = LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    fieldName=null;
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        fieldName = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+5]);
                                    String[] fieldValueStrArr=null;
                                    if (lineNumCols>=numEvaluationArguments+6)
                                         fieldValueStrArr = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+6]);
                                    Object[] fieldValueObjArr=LPArray.convertStringWithDataTypeToObjectArray(fieldValueStrArr);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"sampleId, userName, fieldNames, fieldValues", 
                                            LPNulls.replaceNull(sampleId).toString()+", "+userName+", "+Arrays.toString(fieldName)+", "+Arrays.toString(fieldValueObjArr)}));                              
                                    dataSample = DataSampleAnalysis.sampleAnalysisAddtoSample(sampleId, fieldName, fieldValueObjArr);
                                    break;              
                                case ENTERRESULT:
                                    Integer resultId = 0;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleId=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    String rawValueResult=null;
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        rawValueResult=LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+5]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"resultId, userName, fieldNames, rawValueResult", resultId.toString()+", "+userName+", "+rawValueResult}));  
                                    Object[] actionDiagnoses = smpAnaRes.sampleAnalysisResultEntry(resultId, rawValueResult, smp);
                                    dataSample=(Object[]) actionDiagnoses[0]; 
                                    break;  
                                case REVIEWRESULT:
                                    Integer objectId = 0;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        objectId=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    String objectLevel="";
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        objectLevel=LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+5]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{LBL_OBJECT_USER_NAME+", "+LBL_OBJECT_FIELD_NAMES+", "+LBL_OBJECT_LEVEL+", "+LBL_OBJECT_ID, userName+", "+objectLevel+", "+objectId.toString()}));                              
                                    sampleId = null; Integer testId = null; resultId = null;
                                    if (objectLevel.equalsIgnoreCase(OBJECT_LEVEL_SAMPLE)){sampleId=objectId;}
                                    if (objectLevel.equalsIgnoreCase(OBJECT_LEVEL_TEST)){testId=objectId;}
                                    if (objectLevel.equalsIgnoreCase(OBJECT_LEVEL_RESULT)){resultId=objectId;}
                                    dataSample = smpAnaRes.sampleAnalysisResultReview(sampleId, testId, resultId);
                                    break;                                     
                                case CANCELRESULT:
                                    objectId = 0;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        objectId=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    objectLevel="";
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        objectLevel=LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+5]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{LBL_OBJECT_USER_NAME+", "+LBL_OBJECT_FIELD_NAMES+", "+LBL_OBJECT_LEVEL+", "+LBL_OBJECT_ID, userName+", "+objectLevel+", "+objectId.toString()}));                              
                                    sampleId = null; testId = null; resultId = null;
                                    if (objectLevel.equalsIgnoreCase(OBJECT_LEVEL_SAMPLE)){sampleId = objectId;}
                                    if (objectLevel.equalsIgnoreCase(OBJECT_LEVEL_TEST)){testId = objectId;}
                                    if (objectLevel.equalsIgnoreCase(OBJECT_LEVEL_RESULT)){resultId = objectId;}
                                    dataSample = smpAnaRes.sampleAnalysisResultCancel(sampleId, testId, resultId);
                                    break;                            
                                case UNCANCELRESULT: 
                                    objectId = 0;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        objectId=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    objectLevel="";
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        objectLevel=LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+5]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{LBL_OBJECT_USER_NAME+", "+LBL_OBJECT_FIELD_NAMES+", "+LBL_OBJECT_LEVEL+", "+LBL_OBJECT_ID, userName+", "+objectLevel+", "+objectId.toString()}));                              
                                    sampleId = null; testId = null; resultId = null;

                                    if (objectLevel.equalsIgnoreCase(OBJECT_LEVEL_SAMPLE)){sampleId = objectId;}
                                    if (objectLevel.equalsIgnoreCase(OBJECT_LEVEL_TEST)){testId = objectId;}
                                    if (objectLevel.equalsIgnoreCase(OBJECT_LEVEL_RESULT)){resultId = objectId;}
                                    dataSample = smpAnaRes.sampleAnalysisResultUnCancel(sampleId, testId, resultId);
                                    break;       
                                case TESTASSIGNMENT: 
                                    testId = 0;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        testId=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    String newAnalyst=null;
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        newAnalyst=LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+5]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"testId, userName, newAnalyst", testId.toString()+", "+userName+", "+newAnalyst}));                              
                                    dataSample = DataSampleAnalysis.sampleAnalysisAssignAnalyst(testId, newAnalyst);
                                    break;   
                                case GETSAMPLEINFO:                            
                                    String schemaDataName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName());                     
                                    sampleId = 0;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleId=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    String[] fieldsToGet=null;
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        fieldsToGet=LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+5]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(new Object[]{Arrays.toString(fieldsToGet), sampleId}));
                                    dataSample2D = Rdbms.getRecordFieldsByFilter(procInstanceName, schemaDataName, TblsData.TablesData.SAMPLE.getTableName(), 
                                            new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{sampleId}, fieldsToGet);
                                    break;
                                case ENTERRESULT_LOD:
                                     Integer firstParameter=null;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        firstParameter=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    Integer secondParameter = null;
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        secondParameter=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+5]);                            
                                    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
                                        engine.eval(new FileReader(LOD_JAVASCRIPT_FORMULA));
                                        Invocable invocable = (Invocable) engine;
                                        Object result;
                                        result = invocable.invokeFunction("lossOnDrying", firstParameter, secondParameter);
                                        dataSample=LPArray.addValueToArray1D(dataSample, result);

                                    break;
                                case COC_STARTCHANGE:
                                    String custodianCandidate=null;
                                    sampleId = 0;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleId=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    custodianCandidate = null;
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        custodianCandidate=LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+5]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"sampleId, custodianCandidate", sampleId.toString()+", "+custodianCandidate}));                              
                                    ChangeOfCustody coc =  new ChangeOfCustody();
                                    dataSample = coc.cocStartChange(TblsData.TablesData.SAMPLE_COC, TblsData.SampleCoc.SAMPLE_ID, sampleId, 
                                            custodianCandidate);
                                    break;
                                case COC_CONFIRMCHANGE:
                                    sampleId = 0;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleId=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    comment = null;
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        comment=LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+5]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"sampleId, comment", sampleId.toString()+", "+comment}));                                                          
                                    coc =  new ChangeOfCustody();
                                    dataSample = coc.cocConfirmedChange(TblsData.TablesData.SAMPLE_COC, TblsData.SampleCoc.SAMPLE_ID, sampleId, comment);
                                    break;
                                case COC_ABORTCHANGE:
                                    sampleId = 0;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleId=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    comment = null;
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        comment=LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+5]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"sampleId, comment", sampleId.toString()+", "+comment}));                                                          
                                    coc =  new ChangeOfCustody();
                                    dataSample = coc.cocAbortedChange(TblsData.TablesData.SAMPLE_COC, TblsData.SampleCoc.SAMPLE_ID, sampleId, comment);
                                    break;
                                case RESULT_CHANGE_UOM:
                                    resultId = 0;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        resultId=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    String newUOM = null;
                                    if (lineNumCols>=numEvaluationArguments+5)                
                                        newUOM=LPTestingOutFormat.csvExtractFieldValueString(csvFileContent[iLines][numEvaluationArguments+5]);
                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"resultId, newUOM", resultId.toString()+", "+newUOM}));                                  
                                    dataSample = smpAnaRes.sarChangeUom(resultId, newUOM, smp);
                                    break;
                                case LOGALIQUOT:
                                    sampleId = 0;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        sampleId=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    fieldName=null;
                                    if (lineNumCols>=numEvaluationArguments+6)                
                                        fieldName = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+6]);
                                    fieldValueStrArr=null;
                                    if (lineNumCols>=numEvaluationArguments+7)
                                         fieldValueStrArr = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+7]);
                                    fieldValueObjArr=LPArray.convertStringWithDataTypeToObjectArray(fieldValueStrArr);

                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"sample_id, fieldNames, fieldValues", sampleId.toString()+", "+Arrays.toString(fieldName)+", "+Arrays.toString(fieldValueStrArr)}));                                                                                      
                                    dataSample = smp.logSampleAliquot(sampleId, 
                                            // sampleTemplate, sampleTemplateVersion, 
                                            fieldName, fieldValueObjArr);
                                    break;                     
                                case LOGSUBALIQUOT:
                                    Integer aliquotId = 0;
                                    if (lineNumCols>=numEvaluationArguments+4)                
                                        aliquotId=LPTestingOutFormat.csvExtractFieldValueInteger(csvFileContent[iLines][numEvaluationArguments+4]);
                                    fieldName=null;
                                    if (lineNumCols>=numEvaluationArguments+6)                
                                        fieldName = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+6]);
                                    fieldValueStrArr=null;
                                    if (lineNumCols>=numEvaluationArguments+7)
                                         fieldValueStrArr = LPTestingOutFormat.csvExtractFieldValueStringArr(csvFileContent[iLines][numEvaluationArguments+7]);
                                    fieldValueObjArr=LPArray.convertStringWithDataTypeToObjectArray(fieldValueStrArr);

                                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(
                                        new Object[]{"aliquot_id, fieldNames, fieldValues", aliquotId.toString()+", "+Arrays.toString(fieldName)+", "+Arrays.toString(fieldValueStrArr)}));                                                                                                                  
                                    dataSample = smp.logSampleSubAliquot(aliquotId, 
                                            // sampleTemplate, sampleTemplateVersion, 
                                            fieldName, fieldValueObjArr);
                                    break;                     
                                default:                       
                                    dataSample[0] = "function "+actionName+" not recognized";
                                    dataSample[1] = ""; dataSample[2] = ""; dataSample[3] = ""; dataSample[4] = ""; dataSample[5] = ""; 

                                    break;
                            }
                        }
                    }
                }                
                if (SampleAPIactionsEndpoints.GETSAMPLEINFO.getName().equalsIgnoreCase(actionName))  dataSample = LPArray.array2dTo1d(dataSample2D);

                if (numEvaluationArguments==0){                    
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddField(Arrays.toString(dataSample)));                     
                }
                if (numEvaluationArguments>0){                    
                    Object[] evaluate = tstAssert.evaluate(numEvaluationArguments, tstAssertSummary, dataSample, 4);
                    fileContentTable1Builder.append(LPTestingOutFormat.rowAddFields(evaluate));                    
                }
                fileContentTable1Builder.append(LPTestingOutFormat.ROW_END);                    
            }                          
            tstAssertSummary.notifyResults();
            fileContentTable1Builder.append(LPTestingOutFormat.TABLE_END);
            
            String summaryPhrase ="";
            String scriptIdStr=request.getParameter("scriptId");                       
            Integer scriptId=Integer.valueOf(LPNulls.replaceNull(scriptIdStr));             
            if (numEvaluationArguments==0) summaryPhrase="COMPLETED ALL STEPS";
            else{
                if (tstAssertSummary.getTotalSyntaxisMatch()==csvFileContent.length){
                    summaryPhrase="COMPLETED SUCCESSFULLY";
                    String savePoint= LPNulls.replaceNull(request.getAttribute(LPTestingParams.SCRIPT_EXECUTION_EVIDENCE_SAVE)).toString();
                    if (savePoint==null || savePoint.length()==0)
                        savePoint= LPNulls.replaceNull(request.getParameter(LPTestingParams.SCRIPT_EXECUTION_EVIDENCE_SAVE));
                    if (Boolean.TRUE.equals(Boolean.valueOf(savePoint)))
                        scriptExecutionEvidenceSave(scriptId, summaryPhrase);
                }else{
                    summaryPhrase="COMPLETED WITH UNEXPECTED RESULTS. ";
                    if (tstAssertSummary.getTotalSyntaxisUnMatch()>0) summaryPhrase=summaryPhrase+"Unmatched="+tstAssertSummary.getTotalSyntaxisUnMatch()+". ";
                    if (tstAssertSummary.getTotalSyntaxisUndefined()>0) summaryPhrase=summaryPhrase+"Undefined="+tstAssertSummary.getTotalSyntaxisUndefined()+". ";
                }
            }
            
            String fileContentSummary = LPTestingOutFormat.createSummaryTable(tstAssertSummary, numEvaluationArguments, summaryPhrase, null);
            fileContentBuilder.append(fileContentSummary).append(fileContentTable1Builder.toString());
            out.println(fileContentBuilder.toString());            
            LPTestingOutFormat.createLogFile(csvPathName, fileContentBuilder.toString());     
        } catch(IOException | NoSuchMethodException | ScriptException ex){
            Rdbms.closeRdbms();
            tstAssertSummary=null; 
            String exceptionMessage = ex.getMessage();     
            LPFrontEnd.servletReturnResponseError(request, response, exceptionMessage, null, null, null);                    
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
